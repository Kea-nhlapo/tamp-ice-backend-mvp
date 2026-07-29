INSERT INTO users (
    name,
    email,
    password_hash,
    role,
    compliance_status,
    rating
) VALUES
    (
        'Synthetic Admin',
        'admin@tamp.test',
        '$2a$10$dXJ3SW6G7P50lGqbtJ11deXLW1VDbESJOZ0P.7hGz5M9bMqoM5mG',
        'ADMIN',
        'APPROVED',
        0.00
    ),
    (
        'Synthetic Freight Owner',
        'owner@tamp.test',
        '$2a$10$dXJ3SW6G7P50lGqbtJ11deXLW1VDbESJOZ0P.7hGz5M9bMqoM5mG',
        'FREIGHT_OWNER',
        'APPROVED',
        0.00
    ),
    (
        'Synthetic Transporter',
        'transporter@tamp.test',
        '$2a$10$dXJ3SW6G7P50lGqbtJ11deXLW1VDbESJOZ0P.7hGz5M9bMqoM5mG',
        'TRANSPORTER',
        'APPROVED',
        0.00
    );

INSERT INTO compliance_documents (
    user_id, document_type, document_reference, status
)
SELECT id, 'OPERATING_LICENCE', 'MOCK-LIC-001', 'APPROVED'
FROM users
WHERE email = 'transporter@tamp.test';

INSERT INTO cargo_loads (
    owner_id, origin, destination, cargo_type, weight, volume,
    pickup_start, pickup_end, status
)
SELECT id, 'Johannesburg', 'Durban', 'GENERAL', 5000.00, 25.00,
       TIMESTAMP '2026-07-30 08:00:00', TIMESTAMP '2026-07-30 17:00:00', 'OPEN'
FROM users
WHERE email = 'owner@tamp.test';

INSERT INTO trucks (
    transporter_id, type, capacity, current_location,
    availability_start, availability_end, status
)
SELECT id, 'BOX_TRUCK', 8000.00, 'Johannesburg',
       TIMESTAMP '2026-07-30 06:00:00', TIMESTAMP '2026-07-30 20:00:00', 'AVAILABLE'
FROM users
WHERE email = 'transporter@tamp.test';
