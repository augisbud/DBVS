START TRANSACTION;

DROP TRIGGER IF EXISTS trg_refresh_materialized_product_sales ON "InvoiceProducts";
DROP TRIGGER IF EXISTS trg_update_product_quantity ON "InvoiceProducts";
DROP TRIGGER IF EXISTS trg_prevent_overpayment ON "Payments";
DROP TRIGGER IF EXISTS trg_prevent_invoice_update_if_paid ON "Invoices";
DROP TRIGGER IF EXISTS trg_prevent_invoice_products_insert_if_paid ON "InvoiceProducts";
DROP TRIGGER IF EXISTS trg_prevent_invoice_products_update_if_paid ON "InvoiceProducts";
DROP TRIGGER IF EXISTS trg_prevent_invoice_products_delete_if_paid ON "InvoiceProducts";

DROP FUNCTION IF EXISTS refresh_materialized_product_sales();
DROP FUNCTION IF EXISTS trigger_refresh_materialized_product_sales();
DROP FUNCTION IF EXISTS update_product_quantity();
DROP FUNCTION IF EXISTS prevent_overpayment();
DROP FUNCTION IF EXISTS prevent_modification_if_paid();

DROP MATERIALIZED VIEW IF EXISTS "MaterializedProductSales";

DROP VIEW IF EXISTS "ClientAddresses";
DROP VIEW IF EXISTS "InvoiceTotalAmounts";
DROP VIEW IF EXISTS "ProductSummary";

DROP INDEX IF EXISTS "IX_Clients_Email";
DROP INDEX IF EXISTS "IX_Clients_RegistrationCode";
DROP INDEX IF EXISTS "IX_InvoiceProducts_InvoiceId";
DROP INDEX IF EXISTS "IX_InvoiceProducts_ProductId";
DROP INDEX IF EXISTS "IX_Invoices_ClientId";
DROP INDEX IF EXISTS "IX_Payments_InvoiceId";

DROP TABLE IF EXISTS "Payments";
DROP TABLE IF EXISTS "InvoiceProducts";
DROP TABLE IF EXISTS "Invoices";
DROP TABLE IF EXISTS "Products";
DROP TABLE IF EXISTS "Clients";

COMMIT;