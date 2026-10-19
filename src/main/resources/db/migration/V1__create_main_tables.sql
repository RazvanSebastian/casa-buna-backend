-- Create sequence for roles
CREATE SEQUENCE IF NOT EXISTS roles_seq START WITH 1 INCREMENT BY 1;

-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT PRIMARY KEY DEFAULT nextval('roles_seq'),
    name VARCHAR(255) UNIQUE NOT NULL
);

-- Create sequence for users
CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 1;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY DEFAULT nextval('users_seq'),
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255)
);

-- Create user_roles join table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Insert roles
INSERT INTO roles (name) VALUES ('ROLE_USER') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_VOLUNTEER') ON CONFLICT (name) DO NOTHING;

-- Create user_profiles table
CREATE TABLE IF NOT EXISTS user_profiles (
    user_id BIGINT PRIMARY KEY,
    phone VARCHAR(255),
    job_details VARCHAR(255),
    age INTEGER,
    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create sequence for volunteer_requests
CREATE SEQUENCE IF NOT EXISTS volunteer_requests_seq START WITH 1 INCREMENT BY 1;

-- Create volunteer_requests table
CREATE TABLE IF NOT EXISTS volunteer_requests (
    id BIGINT PRIMARY KEY DEFAULT nextval('volunteer_requests_seq'),
    self_description VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    age INTEGER,
    occupation VARCHAR(255),
    phone VARCHAR(255),
    education_activities TEXT[],
    education_level TEXT[],
    health_activities TEXT[],
    it_support_activities TEXT[],
    logistic_activities TEXT[],
    other_activities VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    requestor_user_id BIGINT,
    approver_user_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by_user_id BIGINT,
    last_modified_by_user_id BIGINT,
    CONSTRAINT fk_volunteer_requests_requestor FOREIGN KEY (requestor_user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_volunteer_requests_approver FOREIGN KEY (approver_user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Insert default admin
INSERT INTO users (email, first_name, last_name) VALUES ('razvan.parautiu@gmail.com', 'Razvan', 'Parautiu') ON CONFLICT (email) DO NOTHING;
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'razvan.parautiu@gmail.com'
  AND r.name = 'ROLE_ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
);
