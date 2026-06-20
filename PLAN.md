# Plan: Migrate from Hibernate auto-DDL to Flyway

## Current State

- Schema managed by `spring.jpa.hibernate.ddl-auto=update`
- Database is **clean** (all tables/schemas dropped)
- `db/migration/` directory exists but is empty
- 4 schemas: `master`, `pricing`, `transaction`, `inventory`
- 16 tables defined in DB design, only 5 have JPA entities so far

## Step 1: Add Flyway dependency

Add to `pom.xml`:

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

Spring Boot 4.0.6 will auto-configure Flyway. No version needed (managed by BOM).

## Step 2: Update `application.properties`

```properties
# Change ddl-auto from "update" to "validate"
spring.jpa.hibernate.ddl-auto=validate

# Flyway config (defaults are fine, but explicit is better)
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=false
```

**Why `validate`?** Flyway owns the schema now. Hibernate should only validate that entities match the DB, not modify it.

**Why `baseline-on-migrate=false`?** The DB is clean. Setting this to `true` would cause Flyway to baseline on any existing Hibernate-generated tables (from the old `ddl-auto=update` era), marking them as "already applied" and skipping creation. Since we want Flyway to own everything from scratch, this must be `false`.

> **Note:** If a `flyway_schema_history` table exists from a prior failed attempt, drop it before first run: `DROP TABLE IF EXISTS flyway_schema_history;`

## Step 3: Create Initial Migration

File: `src/main/resources/db/migration/V1__init_schema.sql`

This single migration creates all schemas and all 16 tables from the DB design.

### Schemas to create

```sql
CREATE SCHEMA IF NOT EXISTS master;
CREATE SCHEMA IF NOT EXISTS pricing;
CREATE SCHEMA IF NOT EXISTS "transaction";
CREATE SCHEMA IF NOT EXISTS inventory;
```

Note: `transaction` is a PostgreSQL reserved word — must be quoted.

### Tables (all use `uuid` PKs with `gen_random_uuid()`)

> **PostgreSQL requirement:** `gen_random_uuid()` is built-in from PostgreSQL 13+. No `pgcrypto` extension needed.

**master schema:**
- `master.product` — sku (case-insensitive unique via `LOWER(sku)` index), name, description, barcode, is_enabled, deleted_at, created_at, updated_at
- `master.product_code` — code (case-insensitive unique via `LOWER(code)` index)
- `master.product_category` — name, description, id_parent (self-ref FK), is_enabled, created_at, updated_at (hard-deleted, no soft delete)
- `master.product_category_mapping` — composite PK `(id_product, id_product_category)`, created_at, updated_at. No separate `id` column.
- `master.customer` — name, id_price_tier (FK to pricing.price_tier), deleted_at, created_at, updated_at
- `master.user` — name, role, is_enabled, deleted_at, created_at, updated_at
- `master.supplier` — name, description, is_enabled, deleted_at, created_at, updated_at

**pricing schema:**
- `pricing.price_tier` — name, description, is_default, is_enabled, sort_order, deleted_at, created_at, updated_at
- `pricing.product_price` — id_product (FK), id_price_tier (FK), price (NUMERIC(15,2)), valid_from, valid_to, created_at, updated_at

**transaction schema** (all table references must be quoted: `"transaction".purchase`, `"transaction".sale_item`, etc.):
- `transaction.purchase` — id_supplier (FK), invoice_number, total, created_at, updated_at
- `transaction.purchase_item` — id_purchase (FK), id_product (FK), cost_price, qty, subtotal, recorded_name, recorded_sku, created_at, updated_at
- `transaction.sale` — id_customer (FK), total_amount, invoice_number (unique), grand_total, paid_amount, change_amount, discount_amount, transaction_date, id_user (FK), created_at, updated_at
- `transaction.sale_item` — id_sale (FK), id_product (FK), id_price_tier (FK), unit_price, cost_price, qty, subtotal, recorded_name, recorded_sku, type, id_original_sale_item (self-ref FK), created_at, updated_at

**inventory schema:**
- `inventory.product_valuation` — id_product (FK, unique), avg_cost (bigint), last_purchase_price (bigint), created_at, updated_at
- `inventory.product_inventory` — id_product (FK, unique), stock_qty (int), created_at, updated_at
- `inventory.stock_movement` — id_product (FK), qty_change (int), movement_type, reference_id (no FK — polymorphic reference to sale_id or purchase_id), created_at, updated_at

### Table creation order (FK dependencies)

Tables must be created in this order to satisfy foreign key constraints:

1. `pricing.price_tier` (no FKs)
2. `master.product` (no FKs)
3. `master.product_code` (FK → product)
4. `master.product_category` (self-ref FK — deferred or created after self)
5. `master.product_category_mapping` (FKs → product, product_category)
6. `master.customer` (FK → price_tier)
7. `master.user` (no FKs)
8. `master.supplier` (no FKs)
9. `pricing.product_price` (FKs → product, price_tier)
10. `transaction.purchase` (FK → supplier)
11. `transaction.purchase_item` (FKs → purchase, product)
12. `transaction.sale` (FKs → customer, user)
13. `transaction.sale_item` (FKs → sale, product, price_tier, self-ref)
14. `inventory.product_valuation` (FK → product)
15. `inventory.product_inventory` (FK → product)
16. `inventory.stock_movement` (FK → product)

### Key constraints

- All PKs: `uuid PRIMARY KEY DEFAULT gen_random_uuid()`
- All PK column names follow convention `id_<entity>` (e.g., `id_product`, `id_sale`).
- `product_category_mapping` PK: composite `(id_product, id_product_category)`
- Indexes on all FK columns for join/filter performance
- Unique constraints: `product.sku`, `product_code.code` (case-insensitive via `LOWER()` functional indexes)
- `product_category` is hard-deleted (no `deleted_at` column)

### FK ON DELETE behavior

| FK | Rule | Rationale |
|----|------|-----------|
| `product_code.id_product → product` | CASCADE | Delete product_code if product is deleted |
| `product_price.id_product → product` | CASCADE | Delete product_price if product is deleted |
| `purchase_item.id_product → product` | SET NULL | Can be deleted |
| `sale_item.id_product → product` | RESTRICT | Don't delete product if sale items exist |
| `product_valuation.id_product → product` | CASCADE | Delete if product is deleted |
| `product_inventory.id_product → product` | CASCADE | Delete if product is deleted |
| `stock_movement.id_product → product` | CASCADE | Delete if product is deleted |
| `product_category_mapping.*` | CASCADE | Delete mapping when either side is deleted |
| `product_category.id_parent → product_category` | SET NULL | Root categories have no parent |
| `customer.id_price_tier → price_tier` | RESTRICT | Don't delete tier if customers use it |
| `product_price.id_price_tier → price_tier` | CASCADE | Can be deleted |
| `sale_item.id_price_tier → price_tier` | SET NULL | Acceptable risk  |
| `purchase.id_supplier → supplier` | SET NULL | Acceptable risk |
| `purchase_item.id_purchase → purchase` | CASCADE | Can be deleted |
| `sale.id_customer → customer` | SET NULL | Customer can be soft-deleted, sale remains |
| `sale.id_user → user` | SET NULL | Acceptable risk |
| `sale_item.id_sale → sale` | RESTRICT | Don't delete sale if items exist |
| `sale_item.id_original_sale_item → sale_item` | SET NULL | Return may reference original; original can be deleted |

### FK constraint naming convention

All FK constraints must be explicitly named to stay consistent with entity `@ForeignKey` annotations:

```
fk_{table}_{referenced_table}
```

For self-referencing FKs, append `_parent` or `_self` to disambiguate:
- `fk_product_category_parent` (self-ref on `product_category.id_parent`)
- `fk_sale_item_original_sale_item` (self-ref on `sale_item.id_original_sale_item`)

Examples: `fk_product_code_product`, `fk_customer_price_tier`, `fk_product_category_parent`.

Entities that already specify `@ForeignKey(name = "...")` must match the migration SQL exactly.

### Case-insensitive unique for product_code.code and product.sku

Instead of plain `UNIQUE` constraints, use functional unique indexes:

```sql
CREATE UNIQUE INDEX idx_product_code_code_lower ON master.product_code (LOWER(code));
CREATE UNIQUE INDEX idx_product_sku_lower ON master.product (LOWER(sku));
```

This ensures `'ABC'` and `'abc'` are treated as duplicates. In the Hibernate entities, use `@Column(columnDefinition = "text")` without `unique = true` — the DB handles uniqueness via the functional indexes.

## Step 4: Align Hibernate Entities

After V1 is applied, update entity classes to match the SQL exactly:

- Ensure `@Table(schema = "...")` matches on all entities
- Ensure column names match (`snake_case` in DB vs camelCase in Java)
- Do NOT add `deletedAt` to `CategoryEntity` — it is hard-deleted
- **BLOCKER: Add `isEnabled` field to `ProductEntity`** — the DB has `is_enabled boolean [default: true]` on `master.product` but the entity is missing it. The app **will not start** with `ddl-auto=validate` until this is added.
- Verify the `product_category_mapping` join table definition matches the SQL (composite PK, column names)
- Verify `@Column(precision = 15, scale = 2)` on `ProductPriceEntity.price` matches DB `NUMERIC(15,2)` — already correct
- Verify `@SQLInsert` on `ProductEntity.categories` still works with the migration's column definitions
- Ensure all `@ForeignKey(name = "...")` annotations match constraint names in the migration SQL

## Step 5: Future Migrations

Naming convention: `V{version}__{description}.sql`

- `V1__init_schema.sql` — this plan
- `V2__add_something.sql` — next change
- `V3__another_change.sql` — and so on

**Rules:**
- Never modify a committed migration. Always create a new one.
- Use `-- Flyway undo` comments if needed (requires Flyway Teams/Enterprise).
- Keep migrations small and focused.
- Test migrations on a clean DB before committing.

## Step 6: Migration Failure Recovery

If V1 fails partway through (e.g., FK constraint error), Flyway blocks re-running the same migration by default.

**Recovery steps:**
1. Fix the SQL in `V1__init_schema.sql`
2. Drop the partial state: `DROP TABLE IF EXISTS flyway_schema_history;`
3. Drop any partially created tables/schemas (since the DB should be clean)
4. Restart the app — Flyway will run V1 from scratch

> **Tip:** For a clean DB, you can also just `DROP SCHEMA master, pricing, "transaction", inventory CASCADE;` before restarting.

## Migration File Checklist

| # | File | What it does |
|---|------|--------------|
| 1 | Add Flyway deps to pom.xml | flyway-core + flyway-database-postgresql |
| 2 | Update application.properties | ddl-auto=validate, flyway config |
| 3 | Write V1__init_schema.sql | Create all schemas + all 16 tables |
| 4 | Align JPA entities | Match column names, types, annotations |
| 5 | Verify | `./mvnw flyway:migrate` then `./mvnw spring-boot:run` |
| 6 | Failure recovery (if needed) | Drop `flyway_schema_history`, restart |
