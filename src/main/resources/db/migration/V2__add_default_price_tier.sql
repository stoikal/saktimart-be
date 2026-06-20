-- V2: Seed default price tier + prevent deletion

INSERT INTO pricing.price_tier (id_price_tier, name, description, is_default, is_enabled, sort_order)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Ritel',
    'Tingkat harga dasar',
    true,
    true,
    0
);

-- Prevent soft-deleting the default price tier
CREATE OR REPLACE FUNCTION prevent_default_price_tier_delete()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.is_default = true AND NEW.deleted_at IS NOT NULL THEN
        RAISE EXCEPTION 'Cannot delete the default price tier';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_prevent_default_price_tier_delete
    BEFORE UPDATE ON pricing.price_tier
    FOR EACH ROW
    EXECUTE FUNCTION prevent_default_price_tier_delete();

-- Prevent physically deleting the default price tier
CREATE OR REPLACE FUNCTION prevent_default_price_tier_physical_delete()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.is_default = true THEN
        RAISE EXCEPTION 'Cannot physically delete the default price tier';
    END IF;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_prevent_default_price_tier_physical_delete
    BEFORE DELETE ON pricing.price_tier
    FOR EACH ROW
    EXECUTE FUNCTION prevent_default_price_tier_physical_delete();
