
-- Creación de la base de datos
create database erp_recursos_DB;

--creación de tablas
create table Users(
	id 				BIGSERIAL PRIMARY KEY,
	username 		VARCHAR(50) NOT NULL UNIQUE,
 	password_hash 	VARCHAR(255) NOT NULL,
 	role 			VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
 	created_at 		TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE employees (
  id          BIGSERIAL PRIMARY KEY,
  name        VARCHAR(80)  NOT NULL,
  surname     VARCHAR(120) NOT NULL,
  email       VARCHAR(150) NOT NULL UNIQUE,
  department  VARCHAR(80),
  active      BOOLEAN NOT NULL DEFAULT TRUE, --no se tiene que borrar, solo se desactiva
  created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMP NOT NULL DEFAULT NOW(), --para actualizaciones
  CONSTRAINT ck_employees_email CHECK (email LIKE '%@atelcosoluciones.es')
);

CREATE TABLE assets (
  id          BIGSERIAL PRIMARY KEY,
  type        VARCHAR(30)  NOT NULL,  
  brand       VARCHAR(60),
  model       VARCHAR(60),
  serial      VARCHAR(80)  NOT NULL UNIQUE,
  status      VARCHAR(20)  NOT NULL DEFAULT 'EN_STOCK', -- EN_STOCK, ASIGNADO, BAJA
  created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
  CONSTRAINT ck_asset_status CHECK (status IN ('EN_STOCK','ASIGNADO','BAJA'))
);

CREATE TABLE assignments (
  id           BIGSERIAL PRIMARY KEY,
  employee_id  BIGINT NOT NULL REFERENCES employees(id) ON DELETE RESTRICT,
  asset_id     BIGINT NOT NULL REFERENCES assets(id) ON DELETE RESTRICT,
  start_date   TIMESTAMP NOT NULL DEFAULT NOW(),
  end_date     TIMESTAMP NULL,
  created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
  CONSTRAINT ck_assignment_dates CHECK (end_date IS NULL OR end_date >= start_date)
);

-- Índices para acelerar consultas típicas
CREATE INDEX idx_assignments_employee ON assignments(employee_id); --ver las asignaciones de un empleado
CREATE INDEX idx_assignments_asset ON assignments(asset_id); 
CREATE INDEX idx_assets_status ON assets(status);
 
-- Regla CLAVE: un activo solo puede tener una asignación ACTIVA (end_date IS NULL)
CREATE UNIQUE INDEX ux_active_assignment_per_asset
ON assignments(asset_id)
WHERE end_date IS NULL;