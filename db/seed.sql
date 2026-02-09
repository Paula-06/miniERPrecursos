
--1) Usuario (solo login)
INSERT INTO users (username, password_hash, role)
VALUES ('admin', 'admin123', 'ADMIN');



--2) Empleados (10)
INSERT INTO employees (name, surname, email, department)
VALUES 
  ('Laura',     'García Pérez',     'laura.garcia@atelcosoluciones.es',     'IT'),
  ('Carlos',    'López Martín',     'carlos.lopez@atelcosoluciones.es',     'Finanzas'),
  ('María',     'Santos Díaz',      'maria.santos@atelcosoluciones.es',     'Ventas'),
  ('Javier',    'Ruiz Romero',      'javier.ruiz@atelcosoluciones.es',      'IT'),
  ('Ana',       'Fernández Soto',   'ana.fernandez@atelcosoluciones.es',    'RRHH'),
  ('Nuria',     'Iglesias Núñez',   'nuria.iglesias@atelcosoluciones.es',   'Operaciones'),
  ('Sergio',    'Vega Prieto',      'sergio.vega@atelcosoluciones.es',      'Marketing'),
  ('Paula',     'Castro Alonso',    'paula.castro@atelcosoluciones.es',     'IT'),
  ('David',     'Méndez Rubio',     'david.mendez@atelcosoluciones.es',     'Compras'),
  ('Elena',     'Ortega Pascual',   'elena.ortega@atelcosoluciones.es',     'Calidad');



--3) Activos (20)
INSERT INTO assets (type, brand, model, serial)
VALUES
  ('LAPTOP',  'Dell',      'Latitude 5440',  'LPT-0001'),
  ('LAPTOP',  'HP',        'ProBook 450',    'LPT-0002'),
  ('LAPTOP',  'Lenovo',    'ThinkPad T14',   'LPT-0003'),
  ('LAPTOP',  'Apple',     'MacBook Air M2', 'LPT-0004'),
  ('PHONE',   'Samsung',   'Galaxy S23',     'PHN-0005'),
  ('PHONE',   'Apple',     'iPhone 14',      'PHN-0006'),
  ('PHONE',   'Xiaomi',    'Redmi Note 12',  'PHN-0007'),
  ('MONITOR', 'LG',        'Ultrafine 27"',  'MON-0008'),
  ('MONITOR', 'Dell',      'U2723QE',        'MON-0009'),
  ('MONITOR', 'Samsung',   'Odyssey G5',     'MON-0010'),
  ('TABLET',  'Apple',     'iPad 10ª Gen',   'TAB-0011'),
  ('TABLET',  'Lenovo',    'Tab P11',        'TAB-0012'),
  ('HEADSET', 'Logitech',  'Zone Wired',     'HDT-0013'),
  ('HEADSET', 'Jabra',     'Evolve2 65',     'HDT-0014'),
  ('LAPTOP',  'Dell',      'Latitude 7440',  'LPT-0015'),
  ('LAPTOP',  'HP',        'EliteBook 840',  'LPT-0016'),
  ('PHONE',   'Google',    'Pixel 7a',       'PHN-0017'),
  ('MONITOR', 'AOC',       '24G2',           'MON-0018'),
  ('TABLET',  'Samsung',   'Galaxy Tab S9',  'TAB-0019'),
  ('HEADSET', 'Sony',      'WH-CH720N',      'HDT-0020');



-- 4) Asignaciones ACTIVAS (3)

--3 asignaciones activas (end_date NULL) -> assets 1,2,3 deben estar ASIGNADO
INSERT INTO assignments (id, employee_id, asset_id, start_date, end_date)
VALUES
  (1,  1,  1,  '2026-01-05 09:00:00', NULL),
  (2,  3,  2,  '2026-01-07 10:30:00', NULL),
  (3,  5,  3,  '2026-01-10 12:15:00', NULL);
 
-- 2 asignaciones históricas (ya devueltas) -> el activo queda EN_STOCK (y status ya está EN_STOCK en assets)
INSERT INTO assignments (id, employee_id, asset_id, start_date, end_date)
VALUES
  (4,  2,  4,  '2025-11-12 09:00:00', '2025-12-20 18:00:00'),
  (5,  6,  9,  '2025-10-01 09:00:00', '2025-10-30 17:00:00');



  -- Ajustar secuencias a los máximos (para evitar conflictos al insertar nuevos registros)
SELECT setval(pg_get_serial_sequence('users','id'),       (SELECT COALESCE(MAX(id),1) FROM users));
SELECT setval(pg_get_serial_sequence('employees','id'),   (SELECT COALESCE(MAX(id),1) FROM employees));
SELECT setval(pg_get_serial_sequence('assets','id'),      (SELECT COALESCE(MAX(id),1) FROM assets));
SELECT setval(pg_get_serial_sequence('assignments','id'), (SELECT COALESCE(MAX(id),1) FROM assignments));