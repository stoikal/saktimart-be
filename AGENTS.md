# Summary

- **Language**: Java 21
- **Framework**: Spring Boot 4.0.6

DO NOT USE Lombok (at least for now)

## Conventions

- Code comments have to answer the question "why"


<!-- gitnexus:start -->
# GitNexus — Code Intelligence

This project is indexed by GitNexus as **saktimart-be** (233 symbols, 569 relationships, 20 execution flows). Use the GitNexus MCP tools to understand code, assess impact, and navigate safely.

> If any GitNexus tool warns the index is stale, run `npx gitnexus analyze` in terminal first.

## Always Do

- **MUST run impact analysis before editing any symbol.** Before modifying a function, class, or method, run `gitnexus_impact({target: "symbolName", direction: "upstream"})` and report the blast radius (direct callers, affected processes, risk level) to the user.
- **MUST run `gitnexus_detect_changes()` before committing** to verify your changes only affect expected symbols and execution flows.
- **MUST warn the user** if impact analysis returns HIGH or CRITICAL risk before proceeding with edits.
- When exploring unfamiliar code, use `gitnexus_query({query: "concept"})` to find execution flows instead of grepping. It returns process-grouped results ranked by relevance.
- When you need full context on a specific symbol — callers, callees, which execution flows it participates in — use `gitnexus_context({name: "symbolName"})`.

## When Debugging

1. `gitnexus_query({query: "<error or symptom>"})` — find execution flows related to the issue
2. `gitnexus_context({name: "<suspect function>"})` — see all callers, callees, and process participation
3. `READ gitnexus://repo/saktimart-be/process/{processName}` — trace the full execution flow step by step
4. For regressions: `gitnexus_detect_changes({scope: "compare", base_ref: "main"})` — see what your branch changed

## When Refactoring

- **Renaming**: MUST use `gitnexus_rename({symbol_name: "old", new_name: "new", dry_run: true})` first. Review the preview — graph edits are safe, text_search edits need manual review. Then run with `dry_run: false`.
- **Extracting/Splitting**: MUST run `gitnexus_context({name: "target"})` to see all incoming/outgoing refs, then `gitnexus_impact({target: "target", direction: "upstream"})` to find all external callers before moving code.
- After any refactor: run `gitnexus_detect_changes({scope: "all"})` to verify only expected files changed.

## Never Do

- NEVER edit a function, class, or method without first running `gitnexus_impact` on it.
- NEVER ignore HIGH or CRITICAL risk warnings from impact analysis.
- NEVER rename symbols with find-and-replace — use `gitnexus_rename` which understands the call graph.
- NEVER commit changes without running `gitnexus_detect_changes()` to check affected scope.

## Tools Quick Reference

| Tool | When to use | Command |
|------|-------------|---------|
| `query` | Find code by concept | `gitnexus_query({query: "auth validation"})` |
| `context` | 360-degree view of one symbol | `gitnexus_context({name: "validateUser"})` |
| `impact` | Blast radius before editing | `gitnexus_impact({target: "X", direction: "upstream"})` |
| `detect_changes` | Pre-commit scope check | `gitnexus_detect_changes({scope: "staged"})` |
| `rename` | Safe multi-file rename | `gitnexus_rename({symbol_name: "old", new_name: "new", dry_run: true})` |
| `cypher` | Custom graph queries | `gitnexus_cypher({query: "MATCH ..."})` |

## Impact Risk Levels

| Depth | Meaning | Action |
|-------|---------|--------|
| d=1 | WILL BREAK — direct callers/importers | MUST update these |
| d=2 | LIKELY AFFECTED — indirect deps | Should test |
| d=3 | MAY NEED TESTING — transitive | Test if critical path |

## Resources

| Resource | Use for |
|----------|---------|
| `gitnexus://repo/saktimart-be/context` | Codebase overview, check index freshness |
| `gitnexus://repo/saktimart-be/clusters` | All functional areas |
| `gitnexus://repo/saktimart-be/processes` | All execution flows |
| `gitnexus://repo/saktimart-be/process/{name}` | Step-by-step execution trace |

## Self-Check Before Finishing

Before completing any code modification task, verify:
1. `gitnexus_impact` was run for all modified symbols
2. No HIGH/CRITICAL risk warnings were ignored
3. `gitnexus_detect_changes()` confirms changes match expected scope
4. All d=1 (WILL BREAK) dependents were updated

## Keeping the Index Fresh

After committing code changes, the GitNexus index becomes stale. Re-run analyze to update it:

```bash
npx gitnexus analyze
```

If the index previously included embeddings, preserve them by adding `--embeddings`:

```bash
npx gitnexus analyze --embeddings
```

To check whether embeddings exist, inspect `.gitnexus/meta.json` — the `stats.embeddings` field shows the count (0 means no embeddings). **Running analyze without `--embeddings` will delete any previously generated embeddings.**

> Claude Code users: A PostToolUse hook handles this automatically after `git commit` and `git merge`.

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
  sku text [unique]
  name text
  description text
  barcode text
  is_enabled boolean [default: true]
  deleted_at datetime
  created_at datetime [default: 'now()']
  updated_at datetime
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
  deleted_at datetime
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
  price bigint
  valid_from datetime
  valid_to datetime
  created_at datetime [default: 'now()']
  updated_at datetime
}

Table pricing.price_tier {
  id_price_tier uuid [pk]
  name text
  description text
  is_enabled boolean [default: true]
  deleted_at datetime
  created_at datetime [default: 'now()']
  updated_at datetime
}
// NOTE: a DEFAULT tier with id `00000000-0000-0000-0000-000000000001` is seeded by Flyway V2.
//       It cannot be deleted due to the `prevent_default_price_tier_delete` trigger.

Table master.customer {
  id_customer uuid [pk]
  name text
  id_price_tier uuid [ref: > pricing.price_tier.id_price_tier]
  is_enabled boolean [default: true]
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
  id_original_sale_item_id uuid [ref: > transaction.sale_item.id_sale_item]
  created_at datetime [default: 'now()']
  updated_at datetime
}

Table inventory.stock_movement {
  id uuid [pk]
  id_product uuid [ref: > master.product.id_product]
  qty_change int
  movement_type text // 'SALE', 'PURCHASE', 'ADJUSTMENT', 'RETURN'
  reference_id uuid // Links to sale_id or purchase_id
  created_at datetime [default: 'now()']
  updated_at datetime
}


```
<!-- db_design:end -->
