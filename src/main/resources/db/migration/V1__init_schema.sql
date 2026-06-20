-- V1: Initial schema — all 4 schemas, all 16 tables
-- PostgreSQL 13+ required (gen_random_uuid() built-in)

-- =============================================================================
-- SCHEMAS
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS master;
CREATE SCHEMA IF NOT EXISTS pricing;
CREATE SCHEMA IF NOT EXISTS "transaction";
CREATE SCHEMA IF NOT EXISTS inventory;

-- =============================================================================
-- PRICING (no FKs — created first)
-- =============================================================================

CREATE TABLE pricing.price_tier (
    id_price_tier   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name            text NOT NULL,
    description     text,
    is_default      boolean NOT NULL DEFAULT false,
    is_enabled      boolean NOT NULL DEFAULT true,
    sort_order      smallint,
    deleted_at      timestamp,
    created_at      timestamp NOT NULL DEFAULT now(),
    updated_at      timestamp
);

-- =============================================================================
-- MASTER
-- =============================================================================

CREATE TABLE master.product (
    id_product  uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    sku         text NOT NULL,
    name        text NOT NULL,
    description text,
    barcode     text,
    is_enabled  boolean NOT NULL DEFAULT true,
    deleted_at  timestamp,
    created_at  timestamp NOT NULL DEFAULT now(),
    updated_at  timestamp
);

CREATE TABLE master.product_code (
    id_product_code uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    id_product      uuid NOT NULL,
    code            text NOT NULL,
    created_at      timestamp NOT NULL DEFAULT now(),
    updated_at      timestamp,

    CONSTRAINT fk_product_code_product FOREIGN KEY (id_product)
        REFERENCES master.product (id_product) ON DELETE CASCADE
);

CREATE TABLE master.product_category (
    id_product_category uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name                text NOT NULL,
    description         text,
    id_parent           uuid,
    is_enabled          boolean NOT NULL DEFAULT true,
    created_at          timestamp NOT NULL DEFAULT now(),
    updated_at          timestamp,

    CONSTRAINT fk_product_category_parent FOREIGN KEY (id_parent)
        REFERENCES master.product_category (id_product_category) ON DELETE SET NULL
);

CREATE TABLE master.product_category_mapping (
    id_product          uuid NOT NULL,
    id_product_category uuid NOT NULL,
    created_at          timestamp NOT NULL DEFAULT now(),
    updated_at          timestamp,

    CONSTRAINT pk_product_category_mapping PRIMARY KEY (id_product, id_product_category),
    CONSTRAINT fk_product_category_mapping_product FOREIGN KEY (id_product)
        REFERENCES master.product (id_product) ON DELETE CASCADE,
    CONSTRAINT fk_product_category_mapping_category FOREIGN KEY (id_product_category)
        REFERENCES master.product_category (id_product_category) ON DELETE CASCADE
);

CREATE TABLE master.customer (
    id_customer    uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name           text NOT NULL,
    id_price_tier  uuid,
    deleted_at     timestamp,
    created_at     timestamp NOT NULL DEFAULT now(),
    updated_at     timestamp,

    CONSTRAINT fk_customer_price_tier FOREIGN KEY (id_price_tier)
        REFERENCES pricing.price_tier (id_price_tier) ON DELETE RESTRICT
);

CREATE TABLE master.user (
    id_user     uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name        text NOT NULL,
    role        text NOT NULL,
    is_enabled  boolean NOT NULL DEFAULT true,
    deleted_at  timestamp,
    created_at  timestamp NOT NULL DEFAULT now(),
    updated_at  timestamp
);

CREATE TABLE master.supplier (
    id_supplier  uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name         text NOT NULL,
    description  text,
    is_enabled   boolean NOT NULL DEFAULT true,
    deleted_at   timestamp,
    created_at   timestamp NOT NULL DEFAULT now(),
    updated_at   timestamp
);

-- =============================================================================
-- PRICING (product_price depends on master.product)
-- =============================================================================

CREATE TABLE pricing.product_price (
    id_product_price uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    id_product       uuid NOT NULL,
    id_price_tier    uuid NOT NULL,
    price            numeric(15,2) NOT NULL,
    valid_from       timestamp,
    valid_to         timestamp,
    created_at       timestamp NOT NULL DEFAULT now(),
    updated_at       timestamp,

    CONSTRAINT fk_product_price_product FOREIGN KEY (id_product)
        REFERENCES master.product (id_product) ON DELETE CASCADE,
    CONSTRAINT fk_product_price_price_tier FOREIGN KEY (id_price_tier)
        REFERENCES pricing.price_tier (id_price_tier) ON DELETE CASCADE
);

-- =============================================================================
-- TRANSACTION (schema name is reserved — all references must be quoted)
-- =============================================================================

CREATE TABLE "transaction".purchase (
    id_purchase    uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    id_supplier    uuid,
    invoice_number text NOT NULL,
    total          bigint NOT NULL DEFAULT 0,
    created_at     timestamp NOT NULL DEFAULT now(),
    updated_at     timestamp,

    CONSTRAINT fk_purchase_supplier FOREIGN KEY (id_supplier)
        REFERENCES master.supplier (id_supplier) ON DELETE SET NULL
);

CREATE TABLE "transaction".purchase_item (
    id_purchase_item uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    id_purchase      uuid NOT NULL,
    id_product       uuid,
    cost_price       bigint NOT NULL DEFAULT 0,
    qty              int NOT NULL DEFAULT 0,
    subtotal         bigint NOT NULL DEFAULT 0,
    recorded_name    text,
    recorded_sku     text,
    created_at       timestamp NOT NULL DEFAULT now(),
    updated_at       timestamp,

    CONSTRAINT fk_purchase_item_purchase FOREIGN KEY (id_purchase)
        REFERENCES "transaction".purchase (id_purchase) ON DELETE CASCADE,
    CONSTRAINT fk_purchase_item_product FOREIGN KEY (id_product)
        REFERENCES master.product (id_product) ON DELETE SET NULL
);

CREATE TABLE "transaction".sale (
    id_sale           uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    id_customer       uuid,
    total_amount      bigint NOT NULL DEFAULT 0,
    invoice_number    text NOT NULL,
    grand_total       bigint NOT NULL DEFAULT 0,
    paid_amount       bigint NOT NULL DEFAULT 0,
    change_amount     bigint NOT NULL DEFAULT 0,
    discount_amount   bigint NOT NULL DEFAULT 0,
    transaction_date  timestamp NOT NULL,
    id_user           uuid,
    created_at        timestamp NOT NULL DEFAULT now(),
    updated_at        timestamp,

    CONSTRAINT fk_sale_customer FOREIGN KEY (id_customer)
        REFERENCES master.customer (id_customer) ON DELETE SET NULL,
    CONSTRAINT fk_sale_user FOREIGN KEY (id_user)
        REFERENCES master.user (id_user) ON DELETE SET NULL,
    CONSTRAINT uk_sale_invoice_number UNIQUE (invoice_number)
);

CREATE TABLE "transaction".sale_item (
    id_sale_item            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    id_sale                 uuid NOT NULL,
    id_product              uuid,
    id_price_tier           uuid,
    unit_price              bigint NOT NULL DEFAULT 0,
    cost_price              bigint NOT NULL DEFAULT 0,
    qty                     int NOT NULL DEFAULT 0,
    subtotal                bigint NOT NULL DEFAULT 0,
    recorded_name           text,
    recorded_sku            text,
    type                    text NOT NULL DEFAULT 'SALE',
    id_original_sale_item   uuid,
    created_at              timestamp NOT NULL DEFAULT now(),
    updated_at              timestamp,

    CONSTRAINT fk_sale_item_sale FOREIGN KEY (id_sale)
        REFERENCES "transaction".sale (id_sale) ON DELETE RESTRICT,
    CONSTRAINT fk_sale_item_product FOREIGN KEY (id_product)
        REFERENCES master.product (id_product) ON DELETE RESTRICT,
    CONSTRAINT fk_sale_item_price_tier FOREIGN KEY (id_price_tier)
        REFERENCES pricing.price_tier (id_price_tier) ON DELETE SET NULL,
    CONSTRAINT fk_sale_item_original_sale_item FOREIGN KEY (id_original_sale_item)
        REFERENCES "transaction".sale_item (id_sale_item) ON DELETE SET NULL
);

-- =============================================================================
-- INVENTORY
-- =============================================================================

CREATE TABLE inventory.product_valuation (
    id_product_valuation uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    id_product           uuid NOT NULL,
    avg_cost             bigint NOT NULL DEFAULT 0,
    last_purchase_price  bigint NOT NULL DEFAULT 0,
    created_at           timestamp NOT NULL DEFAULT now(),
    updated_at           timestamp,

    CONSTRAINT fk_product_valuation_product FOREIGN KEY (id_product)
        REFERENCES master.product (id_product) ON DELETE CASCADE,
    CONSTRAINT uk_product_valuation_product UNIQUE (id_product)
);

CREATE TABLE inventory.product_inventory (
    id_product_inventory uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    id_product           uuid NOT NULL,
    stock_qty            int NOT NULL DEFAULT 0,
    created_at           timestamp NOT NULL DEFAULT now(),
    updated_at           timestamp,

    CONSTRAINT fk_product_inventory_product FOREIGN KEY (id_product)
        REFERENCES master.product (id_product) ON DELETE CASCADE,
    CONSTRAINT uk_product_inventory_product UNIQUE (id_product)
);

CREATE TABLE inventory.stock_movement (
    id_stock_movement uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    id_product        uuid NOT NULL,
    qty_change        int NOT NULL DEFAULT 0,
    movement_type     text NOT NULL,
    reference_id      uuid,
    created_at        timestamp NOT NULL DEFAULT now(),
    updated_at        timestamp,

    CONSTRAINT fk_stock_movement_product FOREIGN KEY (id_product)
        REFERENCES master.product (id_product) ON DELETE CASCADE
);

-- =============================================================================
-- INDEXES (FK columns for join/filter performance)
-- =============================================================================

CREATE INDEX idx_product_code_id_product ON master.product_code (id_product);
CREATE INDEX idx_product_category_mapping_id_product ON master.product_category_mapping (id_product);
CREATE INDEX idx_product_category_mapping_id_product_category ON master.product_category_mapping (id_product_category);
CREATE INDEX idx_product_category_id_parent ON master.product_category (id_parent);
CREATE INDEX idx_customer_id_price_tier ON master.customer (id_price_tier);
CREATE INDEX idx_product_price_id_product ON pricing.product_price (id_product);
CREATE INDEX idx_product_price_id_price_tier ON pricing.product_price (id_price_tier);
CREATE INDEX idx_purchase_id_supplier ON "transaction".purchase (id_supplier);
CREATE INDEX idx_purchase_item_id_purchase ON "transaction".purchase_item (id_purchase);
CREATE INDEX idx_purchase_item_id_product ON "transaction".purchase_item (id_product);
CREATE INDEX idx_sale_id_customer ON "transaction".sale (id_customer);
CREATE INDEX idx_sale_id_user ON "transaction".sale (id_user);
CREATE INDEX idx_sale_item_id_sale ON "transaction".sale_item (id_sale);
CREATE INDEX idx_sale_item_id_product ON "transaction".sale_item (id_product);
CREATE INDEX idx_sale_item_id_price_tier ON "transaction".sale_item (id_price_tier);
CREATE INDEX idx_sale_item_id_original_sale_item ON "transaction".sale_item (id_original_sale_item);
CREATE INDEX idx_product_valuation_id_product ON inventory.product_valuation (id_product);
CREATE INDEX idx_product_inventory_id_product ON inventory.product_inventory (id_product);
CREATE INDEX idx_stock_movement_id_product ON inventory.stock_movement (id_product);

-- =============================================================================
-- CASE-INSENSITIVE UNIQUE INDEXES
-- =============================================================================

CREATE UNIQUE INDEX idx_product_sku_lower ON master.product (LOWER(sku));
CREATE UNIQUE INDEX idx_product_code_code_lower ON master.product_code (LOWER(code));
