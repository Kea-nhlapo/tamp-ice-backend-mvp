UPDATE users
SET password_hash = '$2a$10$NoRL.FpriyhXMmuIWQM73Oi2o/RZUe5n/XPpxEhsU/sBJlsdRLR2i'
WHERE email IN (
    'admin@tamp.test',
    'owner@tamp.test',
    'transporter@tamp.test'
);
