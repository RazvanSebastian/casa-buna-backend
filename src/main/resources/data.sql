-- Insert dummy teams
INSERT INTO teams (name, created_at) VALUES ('Team Alpha', CURRENT_TIMESTAMP);
INSERT INTO teams (name, created_at) VALUES ('Team Beta', CURRENT_TIMESTAMP);
INSERT INTO teams (name, created_at) VALUES ('Team Gamma', CURRENT_TIMESTAMP);

-- Insert dummy managers
INSERT INTO users (email, first_name, last_name) VALUES ('manager1@example.com', 'John', 'Smith');
INSERT INTO users (email, first_name, last_name) VALUES ('manager2@example.com', 'Jane', 'Doe');
INSERT INTO users (email, first_name, last_name) VALUES ('manager3@example.com', 'Bob', 'Johnson');

-- Insert dummy volunteers
INSERT INTO users (email, first_name, last_name) VALUES ('volunteer1@example.com', 'Alice', 'Williams');
INSERT INTO users (email, first_name, last_name) VALUES ('volunteer2@example.com', 'Charlie', 'Brown');
INSERT INTO users (email, first_name, last_name) VALUES ('volunteer3@example.com', 'Diana', 'Prince');
INSERT INTO users (email, first_name, last_name) VALUES ('volunteer4@example.com', 'Eve', 'Adams');
INSERT INTO users (email, first_name, last_name) VALUES ('volunteer5@example.com', 'Leo', 'Martinez');
INSERT INTO users (email, first_name, last_name) VALUES ('volunteer6@example.com', 'Mia', 'Garcia');

-- Insert dummy children
INSERT INTO users (email, first_name, last_name) VALUES ('child1@example.com', 'Frank', 'Miller');
INSERT INTO users (email, first_name, last_name) VALUES ('child2@example.com', 'Grace', 'Lee');
INSERT INTO users (email, first_name, last_name) VALUES ('child3@example.com', 'Henry', 'Davis');
INSERT INTO users (email, first_name, last_name) VALUES ('child4@example.com', 'Ivy', 'Chen');
INSERT INTO users (email, first_name, last_name) VALUES ('child5@example.com', 'Jack', 'Wilson');
INSERT INTO users (email, first_name, last_name) VALUES ('child6@example.com', 'Karen', 'Taylor');

-- Assign roles to managers
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'manager1@example.com' AND r.name = 'ROLE_MANAGER'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'manager2@example.com' AND r.name = 'ROLE_MANAGER'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'manager3@example.com' AND r.name = 'ROLE_MANAGER'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Assign roles to volunteers
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'volunteer1@example.com' AND r.name = 'ROLE_VOLUNTEER'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'volunteer2@example.com' AND r.name = 'ROLE_VOLUNTEER'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'volunteer3@example.com' AND r.name = 'ROLE_VOLUNTEER'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'volunteer4@example.com' AND r.name = 'ROLE_VOLUNTEER'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'volunteer5@example.com' AND r.name = 'ROLE_VOLUNTEER'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'volunteer6@example.com' AND r.name = 'ROLE_VOLUNTEER'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Assign roles to children
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'child1@example.com' AND r.name = 'ROLE_CHILD'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'child2@example.com' AND r.name = 'ROLE_CHILD'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'child3@example.com' AND r.name = 'ROLE_CHILD'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'child4@example.com' AND r.name = 'ROLE_CHILD'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'child5@example.com' AND r.name = 'ROLE_CHILD'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'child6@example.com' AND r.name = 'ROLE_CHILD'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Assign managers to Team Alpha
INSERT INTO team_members (user_id, team_id)
SELECT u.id, t.id
FROM users u, teams t
WHERE u.email = 'manager1@example.com' AND t.name = 'Team Alpha'
ON CONFLICT (user_id, team_id) DO NOTHING;

INSERT INTO team_members (user_id, team_id)
SELECT u.id, t.id
FROM users u, teams t
WHERE u.email = 'manager2@example.com' AND t.name = 'Team Alpha'
ON CONFLICT (user_id, team_id) DO NOTHING;

-- Assign volunteers to Team Alpha
INSERT INTO team_members (user_id, team_id)
SELECT u.id, t.id
FROM users u, teams t
WHERE u.email = 'volunteer1@example.com' AND t.name = 'Team Alpha'
ON CONFLICT (user_id, team_id) DO NOTHING;

INSERT INTO team_members (user_id, team_id)
SELECT u.id, t.id
FROM users u, teams t
WHERE u.email = 'volunteer2@example.com' AND t.name = 'Team Alpha'
ON CONFLICT (user_id, team_id) DO NOTHING;

-- Assign children to Team Alpha
INSERT INTO team_members (user_id, team_id)
SELECT u.id, t.id
FROM users u, teams t
WHERE u.email = 'child1@example.com' AND t.name = 'Team Alpha'
ON CONFLICT (user_id, team_id) DO NOTHING;

INSERT INTO team_members (user_id, team_id)
SELECT u.id, t.id
FROM users u, teams t
WHERE u.email = 'child2@example.com' AND t.name = 'Team Alpha'
ON CONFLICT (user_id, team_id) DO NOTHING;

INSERT INTO team_members (user_id, team_id)
SELECT u.id, t.id
FROM users u, teams t
WHERE u.email = 'child3@example.com' AND t.name = 'Team Alpha'
ON CONFLICT (user_id, team_id) DO NOTHING;

INSERT INTO team_members (user_id, team_id)
SELECT u.id, t.id
FROM users u, teams t
WHERE u.email = 'child4@example.com' AND t.name = 'Team Alpha'
ON CONFLICT (user_id, team_id) DO NOTHING;

-- Assign manager to Team Beta
INSERT INTO team_members (user_id, team_id)
SELECT u.id, t.id
FROM users u, teams t
WHERE u.email = 'manager3@example.com' AND t.name = 'Team Beta'
ON CONFLICT (user_id, team_id) DO NOTHING;

-- Assign volunteers to Team Beta
INSERT INTO team_members (user_id, team_id)
SELECT u.id, t.id
FROM users u, teams t
WHERE u.email = 'volunteer3@example.com' AND t.name = 'Team Beta'
ON CONFLICT (user_id, team_id) DO NOTHING;

INSERT INTO team_members (user_id, team_id)
SELECT u.id, t.id
FROM users u, teams t
WHERE u.email = 'volunteer4@example.com' AND t.name = 'Team Beta'
ON CONFLICT (user_id, team_id) DO NOTHING;

-- Assign children to volunteers in Team Alpha (volunteer1 has child1 and child2)
INSERT INTO volunteer_child_assignments (volunteer_id, child_id, team_id, created_at)
SELECT v.id, c.id, t.id, CURRENT_TIMESTAMP
FROM users v, users c, teams t
WHERE v.email = 'volunteer1@example.com' AND c.email = 'child1@example.com' AND t.name = 'Team Alpha'
ON CONFLICT (volunteer_id, child_id, team_id) DO NOTHING;

INSERT INTO volunteer_child_assignments (volunteer_id, child_id, team_id, created_at)
SELECT v.id, c.id, t.id, CURRENT_TIMESTAMP
FROM users v, users c, teams t
WHERE v.email = 'volunteer1@example.com' AND c.email = 'child2@example.com' AND t.name = 'Team Alpha'
ON CONFLICT (volunteer_id, child_id, team_id) DO NOTHING;

-- Assign children to volunteers in Team Alpha (volunteer2 has child3 and child4)
INSERT INTO volunteer_child_assignments (volunteer_id, child_id, team_id, created_at)
SELECT v.id, c.id, t.id, CURRENT_TIMESTAMP
FROM users v, users c, teams t
WHERE v.email = 'volunteer2@example.com' AND c.email = 'child3@example.com' AND t.name = 'Team Alpha'
ON CONFLICT (volunteer_id, child_id, team_id) DO NOTHING;

INSERT INTO volunteer_child_assignments (volunteer_id, child_id, team_id, created_at)
SELECT v.id, c.id, t.id, CURRENT_TIMESTAMP
FROM users v, users c, teams t
WHERE v.email = 'volunteer2@example.com' AND c.email = 'child4@example.com' AND t.name = 'Team Alpha'
ON CONFLICT (volunteer_id, child_id, team_id) DO NOTHING;
