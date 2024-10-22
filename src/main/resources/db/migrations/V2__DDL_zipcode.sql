CREATE TABLE IF NOT EXISTS zipcode_service_logs (
id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
zip_code VARCHAR(20) NOT NULL,
service_name VARCHAR(50) NOT NULL,
request TEXT NOT NULL,
external_response TEXT NOT NULL,
response TEXT NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_zipcode_service_logs_zip_code ON zipcode_service_logs (zip_code);
CREATE INDEX IF NOT EXISTS idx_zipcode_service_logs_service_name ON zipcode_service_logs (service_name);