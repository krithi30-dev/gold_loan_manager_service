CREATE TABLE branches (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    phone VARCHAR(20),
    created_at DATETIME NOT NULL DEFAULT NOW(),
    updated_at DATETIME NOT NULL DEFAULT NOW()
);

CREATE TABLE users (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    branch_id CHAR(36),
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT NOW(),
    updated_at DATETIME NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_users_branch FOREIGN KEY (branch_id) REFERENCES branches(id)
);

CREATE TABLE customers (
    id CHAR(36) PRIMARY KEY,
    customer_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    dob DATE,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    aadhaar VARCHAR(12),
    pan VARCHAR(10),
    address TEXT,
    photo_url TEXT,
    signature_url TEXT,
    branch_id CHAR(36),
    created_by CHAR(36),
    created_at DATETIME NOT NULL DEFAULT NOW(),
    updated_at DATETIME NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_customers_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    CONSTRAINT fk_customers_user FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE kyc_documents (
    id CHAR(36) PRIMARY KEY,
    customer_id CHAR(36) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    file_url TEXT NOT NULL,
    uploaded_at DATETIME NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_kyc_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE gold_rates (
    id CHAR(36) PRIMARY KEY,
    karat INTEGER NOT NULL,
    rate_per_gram_paise BIGINT NOT NULL,
    effective_date DATE NOT NULL,
    created_by CHAR(36),
    created_at DATETIME NOT NULL DEFAULT NOW(),
    UNIQUE KEY uq_gold_rates_karat_date (karat, effective_date),
    CONSTRAINT fk_gold_rates_user FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE loans (
    id CHAR(36) PRIMARY KEY,
    loan_number VARCHAR(20) NOT NULL UNIQUE,
    customer_id CHAR(36) NOT NULL,
    loan_amount_paise BIGINT NOT NULL,
    outstanding_amount_paise BIGINT NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    tenure_months INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    branch_id CHAR(36),
    sanctioned_by CHAR(36),
    due_date DATE,
    closed_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT NOW(),
    updated_at DATETIME NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_loans_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_loans_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    CONSTRAINT fk_loans_user FOREIGN KEY (sanctioned_by) REFERENCES users(id)
);

CREATE TABLE jewellery_items (
    id CHAR(36) PRIMARY KEY,
    loan_id CHAR(36) NOT NULL,
    item_type VARCHAR(100) NOT NULL,
    weight_grams DECIMAL(10,3) NOT NULL,
    purity_karat INTEGER NOT NULL,
    estimated_value_paise BIGINT NOT NULL,
    image_url TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PLEDGED',
    created_at DATETIME NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_jewellery_loan FOREIGN KEY (loan_id) REFERENCES loans(id)
);

CREATE TABLE repayments (
    id CHAR(36) PRIMARY KEY,
    loan_id CHAR(36) NOT NULL,
    amount_paise BIGINT NOT NULL,
    payment_date DATETIME NOT NULL DEFAULT NOW(),
    payment_mode VARCHAR(20) NOT NULL,
    receipt_number VARCHAR(50) NOT NULL UNIQUE,
    recorded_by CHAR(36),
    notes TEXT,
    created_at DATETIME NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_repayments_loan FOREIGN KEY (loan_id) REFERENCES loans(id),
    CONSTRAINT fk_repayments_user FOREIGN KEY (recorded_by) REFERENCES users(id)
);

CREATE TABLE audit_logs (
    id CHAR(36) PRIMARY KEY,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,
    action VARCHAR(50) NOT NULL,
    performed_by CHAR(36),
    old_value_json TEXT,
    new_value_json TEXT,
    created_at DATETIME NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_audit_user FOREIGN KEY (performed_by) REFERENCES users(id)
);

-- Indexes
CREATE INDEX idx_loans_customer_id ON loans(customer_id);
CREATE INDEX idx_loans_status ON loans(status);
CREATE INDEX idx_loans_branch_id ON loans(branch_id);
CREATE INDEX idx_loans_created_at ON loans(created_at);
CREATE INDEX idx_repayments_loan_id ON repayments(loan_id);
CREATE INDEX idx_customers_phone ON customers(phone);
CREATE INDEX idx_customers_branch_id ON customers(branch_id);
CREATE INDEX idx_gold_rates_effective_date ON gold_rates(effective_date);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);

-- Seed default branch
INSERT INTO branches (id, name, address, phone) VALUES
    ('00000000-0000-0000-0000-000000000001', 'Head Office', '123 Main Street, Chennai, Tamil Nadu', '044-12345678');

-- Seed super admin (password: Admin@123 — BCrypt hash)
INSERT INTO users (id, name, email, password_hash, role, branch_id) VALUES
    ('00000000-0000-0000-0000-000000000002',
     'Super Admin',
     'admin@goldloan.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     'SUPER_ADMIN',
     '00000000-0000-0000-0000-000000000001');

-- Seed today's gold rates (sample)
INSERT INTO gold_rates (id, karat, rate_per_gram_paise, effective_date, created_by) VALUES
    (UUID(), 18, 550000, CURDATE(), '00000000-0000-0000-0000-000000000002'),
    (UUID(), 22, 670000, CURDATE(), '00000000-0000-0000-0000-000000000002'),
    (UUID(), 24, 730000, CURDATE(), '00000000-0000-0000-0000-000000000002');
