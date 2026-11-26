-- Create sequence for roles
CREATE SEQUENCE IF NOT EXISTS roles_seq START WITH 1 INCREMENT BY 1;

-- Create roles table
CREATE TABLE IF NOT EXISTS roles
(
    id   BIGINT PRIMARY KEY DEFAULT nextval('roles_seq'),
    name VARCHAR(255) UNIQUE NOT NULL
);

-- Create sequence for users
CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 1;

-- Create users table
CREATE TABLE IF NOT EXISTS users
(
    id         BIGINT PRIMARY KEY DEFAULT nextval('users_seq'),
    email      VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255),
    last_name  VARCHAR(255)
);

-- Create user_roles join table
CREATE TABLE IF NOT EXISTS user_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- Create sequence for teams
CREATE SEQUENCE IF NOT EXISTS teams_seq START WITH 1 INCREMENT BY 1;

-- Create teams table
CREATE TABLE IF NOT EXISTS teams
(
    id                       BIGINT PRIMARY KEY DEFAULT nextval('teams_seq'),
    name                     VARCHAR(255),
    created_at               TIMESTAMP NOT NULL,
    updated_at               TIMESTAMP,
    created_by_user_id       BIGINT,
    last_modified_by_user_id BIGINT
);

-- Create team_members join table
CREATE TABLE IF NOT EXISTS team_members
(
    user_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, team_id),
    CONSTRAINT fk_team_members_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_team_members_team FOREIGN KEY (team_id) REFERENCES teams (id) ON DELETE CASCADE
);

-- Create sequence for volunteer_child_assignments
CREATE SEQUENCE IF NOT EXISTS volunteer_child_assignments_seq START WITH 1 INCREMENT BY 1;

-- Create volunteer_child_assignments table
CREATE TABLE IF NOT EXISTS volunteer_child_assignments
(
    id                       BIGINT PRIMARY KEY DEFAULT nextval('volunteer_child_assignments_seq'),
    volunteer_id             BIGINT NOT NULL,
    child_id                 BIGINT NOT NULL,
    team_id                  BIGINT NOT NULL,
    created_at               TIMESTAMP NOT NULL,
    updated_at               TIMESTAMP,
    created_by_user_id       BIGINT,
    last_modified_by_user_id BIGINT,
    CONSTRAINT uq_volunteer_child_team UNIQUE (volunteer_id, child_id, team_id),
    CONSTRAINT fk_volunteer_child_assignments_volunteer FOREIGN KEY (volunteer_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_volunteer_child_assignments_child FOREIGN KEY (child_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_volunteer_child_assignments_team FOREIGN KEY (team_id) REFERENCES teams (id) ON DELETE CASCADE
);

-- Insert roles
INSERT INTO roles (name)
VALUES ('ROLE_USER')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name)
VALUES ('ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name)
VALUES ('ROLE_VOLUNTEER')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name)
VALUES ('ROLE_CHILD')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name)
VALUES ('ROLE_MANAGER')
ON CONFLICT (name) DO NOTHING;

-- Create user_profiles table
CREATE TABLE IF NOT EXISTS user_profiles
(
    user_id          BIGINT PRIMARY KEY,
    phone            VARCHAR(255),
    self_description VARCHAR(255),
    age              INTEGER,
    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Create sequence for volunteer_requests
CREATE SEQUENCE IF NOT EXISTS volunteer_requests_seq START WITH 1 INCREMENT BY 1;

-- Create volunteer_requests table
CREATE TABLE IF NOT EXISTS volunteer_requests
(
    id                       BIGINT PRIMARY KEY DEFAULT nextval('volunteer_requests_seq'),
    self_description         VARCHAR(255),
    first_name               VARCHAR(255),
    last_name                VARCHAR(255),
    age                      INTEGER,
    occupation               VARCHAR(255),
    phone                    VARCHAR(255),
    education_activities     TEXT[],
    education_level          TEXT[],
    health_activities        TEXT[],
    it_support_activities    TEXT[],
    logistic_activities      TEXT[],
    other_activities         VARCHAR(255),
    status                   VARCHAR(20) NOT NULL,
    requestor_user_id        BIGINT,
    approver_user_id         BIGINT,
    created_at               TIMESTAMP   NOT NULL,
    updated_at               TIMESTAMP,
    created_by_user_id       BIGINT,
    last_modified_by_user_id BIGINT,
    CONSTRAINT fk_volunteer_requests_requestor FOREIGN KEY (requestor_user_id) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_volunteer_requests_approver FOREIGN KEY (approver_user_id) REFERENCES users (id) ON DELETE SET NULL
);

-- Create sequence for notifications
CREATE SEQUENCE IF NOT EXISTS notifications_seq START WITH 1 INCREMENT BY 1;

-- Create notifications table
CREATE TABLE IF NOT EXISTS notifications
(
    id         BIGINT PRIMARY KEY DEFAULT nextval('notifications_seq'),
    title      VARCHAR(255) NOT NULL,
    message    VARCHAR(255) NOT NULL,
    is_read    BOOLEAN            DEFAULT FALSE,
    created_at TIMESTAMP          DEFAULT CURRENT_TIMESTAMP,
    user_id    BIGINT,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL
);

-- Insert default admin
INSERT INTO users (email, first_name, last_name)
VALUES ('razvan.parautiu@gmail.com', 'Razvan', 'Parautiu')
ON CONFLICT (email) DO NOTHING;
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u,
     roles r
WHERE u.email = 'razvan.parautiu@gmail.com'
  AND r.name = 'ROLE_ADMIN'
  AND NOT EXISTS (SELECT 1
                  FROM user_roles ur
                  WHERE ur.user_id = u.id
                    AND ur.role_id = r.id);
