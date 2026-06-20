# Summary

- **Language**: Java 21
- **Framework**: Spring Boot 4.0.6

DO NOT USE Lombok (at least for now)

## Conventions

- Code comments have to answer the question "why"


<!-- gitnexus:start -->
# GitNexus — Code Intelligence

This project is indexed by GitNexus as **saktimart-be** (347 symbols, 749 relationships, 27 execution flows). Use the GitNexus MCP tools to understand code, assess impact, and navigate safely.

> Index stale? Run `node .gitnexus/run.cjs analyze` from the project root — it auto-selects an available runner. No `.gitnexus/run.cjs` yet? `npx gitnexus analyze` (npm 11 crash → `npm i -g gitnexus`; #1939).

## Always Do

- **MUST run impact analysis before editing any symbol.** Before modifying a function, class, or method, run `impact({target: "symbolName", direction: "upstream"})` and report the blast radius (direct callers, affected processes, risk level) to the user.
- **MUST run `detect_changes()` before committing** to verify your changes only affect expected symbols and execution flows. For regression review, compare against the default branch: `detect_changes({scope: "compare", base_ref: "main"})`.
- **MUST warn the user** if impact analysis returns HIGH or CRITICAL risk before proceeding with edits.
- When exploring unfamiliar code, use `query({query: "concept"})` to find execution flows instead of grepping. It returns process-grouped results ranked by relevance.
- When you need full context on a specific symbol — callers, callees, which execution flows it participates in — use `context({name: "symbolName"})`.

## Never Do

- NEVER edit a function, class, or method without first running `impact` on it.
- NEVER ignore HIGH or CRITICAL risk warnings from impact analysis.
- NEVER rename symbols with find-and-replace — use `rename` which understands the call graph.
- NEVER commit changes without running `detect_changes()` to check affected scope.

## Resources

| Resource | Use for |
|----------|---------|
| `gitnexus://repo/saktimart-be/context` | Codebase overview, check index freshness |
| `gitnexus://repo/saktimart-be/clusters` | All functional areas |
| `gitnexus://repo/saktimart-be/processes` | All execution flows |
| `gitnexus://repo/saktimart-be/process/{name}` | Step-by-step execution trace |

## Cross-Repo Groups

This repository is listed under GitNexus **group(s): saktimart** (see `~/.gitnexus/groups/`). For cross-repo analysis, use MCP tools `impact`, `query`, and `context` with `repo` set to `@<groupName>` or `@<groupName>/<memberPath>` (paths match keys in that group’s `group.yaml`). Use `group_list` / `group_sync` for membership and sync. From the project root: `node .gitnexus/run.cjs group list`, `node .gitnexus/run.cjs group sync <name>`, `node .gitnexus/run.cjs group impact <name> --target <symbol> --repo <group-path>` (the `.gitnexus/run.cjs` path is repo-root-relative).

## CLI

| Task | Read this skill file |
|------|---------------------|
| Understand architecture / "How does X work?" | `.claude/skills/gitnexus/gitnexus-exploring/SKILL.md` |
| Blast radius / "What breaks if I change X?" | `.claude/skills/gitnexus/gitnexus-impact-analysis/SKILL.md` |
| Trace bugs / "Why is X failing?" | `.claude/skills/gitnexus/gitnexus-debugging/SKILL.md` |
| Rename / extract / split / refactor | `.claude/skills/gitnexus/gitnexus-refactoring/SKILL.md` |
| Tools, resources, schema reference | `.claude/skills/gitnexus/gitnexus-guide/SKILL.md` |
| Index, status, clean, wiki CLI commands | `.claude/skills/gitnexus/gitnexus-cli/SKILL.md` |

<!-- gitnexus:end -->

<!-- db_design:start -->
# DB Design

```
Table master.product {
  id_product uuid [pk]
  sku text
  name text
  description text
  barcode text
  is_enabled boolean [default: true]
  deleted_at datetime
  created_at datetime [default: 'now()']
  updated_at datetime
  indexes {
    sku unique [note: 'case-insensitive via LOWER(sku)']
  }
}

Table master.product_code {
  id_product_code uuid [pk]
  id_product uuid [ref: - master.product.id_product]
  code text
  indexes {
    code unique [note: 'case-insensitive via LOWER(code)']
  }
}

Table inventory.product_valuation {
  id_product_valuation uuid [pk]
  id_product uuid [unique, ref: - master.product.id_product]
  avg_cost bigint
  last_purchase_price bigint
  created_at datetime [default: 'now()']
  updated_at datetime
}

Table master.product_category {
  id_product_category uuid [pk]
  name text
  description text
  id_parent uuid [ref: > master.product_category.id_product_category]
  is_enabled boolean [default: true]
  created_at datetime [default: 'now()']
  updated_at datetime
}

Table master.product_category_mapping {
  id_product uuid [ref: > master.product.id_product]
  id_product_category uuid [ref: > master.product_category.id_product_category]
  created_at datetime [default: 'now()']
  updated_at datetime
}

Table inventory.product_inventory {
  id_product_inventory uuid [pk]
  id_product uuid [unique, ref: - master.product.id_product]
  stock_qty int
  created_at datetime [default: 'now()']
  updated_at datetime
}

Table pricing.product_price {
  id_product_price uuid [pk]
  id_product uuid [ref: > master.product.id_product]
  id_price_tier uuid [ref: > pricing.price_tier.id_price_tier]
  price numeric(15,2)
  valid_from datetime
  valid_to datetime
  created_at datetime [default: 'now()']
  updated_at datetime
}
// NOTE: valid_to because I want to use append only when editing/deleting price

Table pricing.price_tier {
  id_price_tier uuid [pk]
  name text
  description text
  is_default boolean [default: false]
  is_enabled boolean [default: true]
  sort_order smallint
  deleted_at datetime
  created_at datetime [default: 'now()']
  updated_at datetime
}

Table master.customer {
  id_customer uuid [pk]
  name text
  id_price_tier uuid [ref: > pricing.price_tier.id_price_tier]
  deleted_at datetime
  created_at datetime [default: 'now()']
  updated_at datetime
}

Table master.user {
  id_user uuid [pk]
  name text
  role text
  is_enabled boolean [default: true]
  deleted_at datetime
  created_at datetime [default: 'now()']
  updated_at datetime
}

Table master.supplier {
  id_supplier uuid [pk]
  name text
  description text
  is_enabled boolean [default: true]
  deleted_at datetime
  created_at datetime [default: 'now()']
  updated_at datetime
}

Table transaction.purchase {
  id_purchase uuid [pk]
  id_supplier uuid [ref: > master.supplier.id_supplier]
  invoice_number text
  total bigint
  created_at datetime [default: 'now()']
  updated_at datetime
}

Table transaction.purchase_item {
  id_purchase_item uuid [pk]
  id_purchase uuid [ref: > transaction.purchase.id_purchase]
  id_product uuid [ref: > master.product.id_product]
  cost_price bigint
  qty int
  subtotal bigint
  recorded_name text
  recorded_sku text
  created_at datetime [default: 'now()']
  updated_at datetime
}

Table transaction.sale {
  id_sale uuid [pk]
  id_customer uuid [ref: > master.customer.id_customer]
  total_amount bigint
  invoice_number text [unique]
  grand_total bigint
  paid_amount bigint
  change_amount bigint
  discount_amount bigint
  transaction_date datetime
  id_user uuid [ref: > master.user.id_user]
  created_at datetime [default: 'now()']
  updated_at datetime
}

Table transaction.sale_item {
  id_sale_item uuid [pk]
  id_sale uuid [ref: > transaction.sale.id_sale]
  id_product uuid [ref: > master.product.id_product]
  id_price_tier uuid [ref: > pricing.price_tier.id_price_tier]
  unit_price bigint
  cost_price bigint
  qty int
  subtotal bigint
  recorded_name text
  recorded_sku text
  type text // 'SALE', 'RETURN'
  id_original_sale_item uuid [ref: > transaction.sale_item.id_sale_item]
  created_at datetime [default: 'now()']
  updated_at datetime
}

Table inventory.stock_movement {
  id_stock_movement uuid [pk]
  id_product uuid [ref: > master.product.id_product]
  qty_change int
  movement_type text // 'SALE', 'PURCHASE', 'ADJUSTMENT', 'RETURN'
  reference_id uuid // Links to sale_id or purchase_id
  created_at datetime [default: 'now()']
  updated_at datetime
}

Ref: "transaction"."sale_item"."id_sale_item" < "transaction"."sale_item"."id_product"


```
<!-- db_design:end -->
