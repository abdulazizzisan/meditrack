CREATE TABLE medical_histories (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    diagnosis VARCHAR(255) NOT NULL,
    notes TEXT,
    visit_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_medical_histories_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_medical_histories_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id)
);

CREATE INDEX idx_medical_histories_patient_visit_date ON medical_histories (patient_id, visit_date DESC);
CREATE INDEX idx_medical_histories_doctor_visit_date ON medical_histories (doctor_id, visit_date DESC);
