CREATE TABLE client (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    rut VARCHAR(20),
    address VARCHAR(255),
    phone VARCHAR(20)
);

CREATE TABLE app_user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    password VARCHAR(255)
);

CREATE TABLE token (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255) UNIQUE,
    token_type VARCHAR(20) DEFAULT 'BEARER',
    revoked BOOLEAN DEFAULT FALSE,
    expired BOOLEAN DEFAULT FALSE,
    user_id INTEGER,
    FOREIGN KEY (user_id) REFERENCES app_user(id)
);

INSERT INTO client (address, nombre, phone, rut) VALUES
('calle diez', 'Jhon stivens', '971568325', '12345678-9'),
('calle dos', 'lolo allens', '971568453', '76345678-9'),
('calle tres', 'Wheid Whads', '971568435', '78545678-9'),
('calle cuatro', 'Polo Pollens', '971568783', '63345678-9'),
('calle uno', 'Luis lutten', '957648344', '98765432-1');
