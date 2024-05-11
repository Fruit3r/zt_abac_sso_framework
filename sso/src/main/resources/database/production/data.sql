INSERT INTO Users (username, password, email, blocked, attributes)
VALUES ('Admin', '$2a$10$CCsxWxtdSEV/oeXJRrH5RujibFZp.SMrp3AtTT2kpyoIdExIw3n6W', 'admin@email.com', FALSE, '{"role":"root"}');

INSERT INTO Policies (name, rules)
VALUES ('root_policy', '#subject_role=="root"');