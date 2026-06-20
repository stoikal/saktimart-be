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
spring.flyway.baseline-on-migrate=true
```

**Why `validate`?** Flyway owns the schema now. Hibernate should only validate that entities match the DB, not modify it.

**Why `baseline-on-migrate`?** Not strictly needed since DB is clean, but good safety net if someone runs the app against a DB that already has tables.

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

### Tables (all use `uuid` PKs with gen_random_uuid())

**master schema:**
- `master.product` — sku, name, description, barcode, is_enabled, deleted_at, created_at, updated_at
- `master.product_code` — id_product (FK), code (unique)
- `master.product_category` — name, description, id_parent (self-ref FK), is_enabled, deleted_at, created_at, updated_at
- `master.product_category_mapping` — id_product + id_product_category composite, created_at, updated_at
- `master.customer` — name, id_price_tier (FK to pricing.price_tier), deleted_at, created_at, updated_at
- `master.user` — name, role, is_enabled, deleted_at, created_at, updated_at
- `master.supplier` — name, description, is_enabled, deleted_at, created_at, updated_at

**pricing schema:**
- `pricing.price_tier` — name, description, is_enabled, sort_order, deleted_at, created_at, updated_at
- `pricing.product_price` — id_product (FK), id_price_tier (FK), price (bigint), valid_from, valid_to, created_at, updated_at

**transaction schema:**
- `transaction.purchase` — id_supplier (FK), invoice_number, total, created_at, updated_at
- `transaction.purchase_item` — id_purchase (FK), id_product (FK), cost_price, qty, subtotal, recorded_name, recorded_sku, created_at, updated_at
- `transaction.sale` — id_customer (FK), total_amount, invoice_number (unique), grand_total, paid_amount, change_amount, discount_amount, transaction_date, id_user (FK), created_at, updated_at
- `transaction.sale_item` — id_sale (FK), id_product (FK), id_price_tier (FK), unit_price, cost_price, qty, subtotal, recorded_name, recorded_sku, type, id_original_sale_item_id (self-ref FK), created_at, updated_at

**inventory schema:**
- `inventory.product_valuation` — id_product (FK, unique), avg_cost (bigint), last_purchase_price (bigint), created_at, updated_at
- `inventory.product_inventory` — id_product (FK, unique), stock_qty (int), created_at, updated_at
- `inventory.stock_movement` — id_product (FK), qty_change (int), movement_type, reference_id, created_at, updated_at

### Key constraints

- All PKs: `uuid PRIMARY KEY DEFAULT gen_random_uuid()`
- All FKs: `REFERENCES schema.table(column) ON DELETE RESTRICT` (or SET NULL where nullable makes sense)
- `product_category_mapping` PK: composite `(id_product, id_product_category)`
- Indexes on all FK columns for join/filter performance
- Unique constraints: `product.sku`, `product_code.code`, `sale.invoice_number`

## Step 4: Align Hibernate Entities

After V1 is applied, update entity classes to match the SQL exactly:

- Ensure `@Table(schema = "...")` matches on all entities
- Ensure column names match (`snake_case` in DB vs camelCase in Java)
- Add missing `@SQLDelete` / `@SQLRestriction` to `CategoryEntity`, `PriceTierEntity`, `CustomerEntity` (they have `deletedAt` but no soft-delete annotations)
- Verify the `product_category_mapping` join table definition matches the SQL
- Verify `@Column(unique = true)` etc. match DB constraints

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

## Migration File Checklist

| # | File | What it does |
|---|------|--------------|
| 1 | Add Flyway deps to pom.xml | flyway-core + flyway-database-postgresql |
| 2 | Update application.properties | ddl-auto=validate, flyway config |
| 3 | Write V1__init_schema.sql | Create all schemas + all 16 tables |
| 4 | Align JPA entities | Match column names, types, annotations |
| 5 | Verify | `./mvnw flyway:migrate` then `./mvnw spring-boot:run` |
