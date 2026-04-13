CREATE TABLE medications (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_medications_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_medications_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id)
);

CREATE INDEX idx_medications_patient_active ON medications (patient_id, is_active);
CREATE INDEX idx_medications_doctor_active ON medications (doctor_id, is_active);
