-- Users Table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('ROLE_ADMIN', 'ROLE_USER'))
);

-- Boards Table
CREATE TABLE boards (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    owner_id INT REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Lists Table
CREATE TABLE lists (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    board_id INT REFERENCES boards(id) ON DELETE CASCADE,
    position INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Cards Table
CREATE TABLE cards (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    list_id INT REFERENCES lists(id) ON DELETE CASCADE,
    position INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert users
INSERT INTO users (username, password, role) VALUES
('nurik', '$2a$12$9pml//HbMdII7IHKA2xhyOeMnrYj7xgzIyrLEYLYFdFMXDubxgeMW', 'ROLE_ADMIN'),
('sabina', '$2a$12$rCNnVWK2lqkl2ID1RjSyxO92WfpU/ft6g94BvqK//K6f4OBG7kNT.', 'ROLE_USER');

-- Insert boards
INSERT INTO boards (name, owner_id) VALUES
('Personal Tasks', (SELECT id FROM users WHERE username = 'nurik')),
('Work Tasks', (SELECT id FROM users WHERE username = 'sabina'));

-- Insert lists for boards
INSERT INTO lists (name, board_id, position) VALUES
('To Do', (SELECT id FROM boards WHERE name = 'Personal Tasks'), 1),
('In Progress', (SELECT id FROM boards WHERE name = 'Personal Tasks'), 2),
('Done', (SELECT id FROM boards WHERE name = 'Personal Tasks'), 3),
('Backlog', (SELECT id FROM boards WHERE name = 'Work Tasks'), 1),
('Ongoing', (SELECT id FROM boards WHERE name = 'Work Tasks'), 2);

-- Insert cards for lists
INSERT INTO cards (title, description, list_id, position) VALUES
('Buy groceries', 'Milk, Bread, Eggs', (SELECT id FROM lists WHERE name = 'To Do' AND board_id = (SELECT id FROM boards WHERE name = 'Personal Tasks')), 1),
('Finish report', 'Complete the monthly sales report', (SELECT id FROM lists WHERE name = 'In Progress' AND board_id = (SELECT id FROM boards WHERE name = 'Personal Tasks')), 1),
('Clean room', 'Organize the workspace and throw away trash', (SELECT id FROM lists WHERE name = 'Done' AND board_id = (SELECT id FROM boards WHERE name = 'Personal Tasks')), 1),
('Prepare for presentation', 'Gather all necessary materials and practice', (SELECT id FROM lists WHERE name = 'Backlog' AND board_id = (SELECT id FROM boards WHERE name = 'Work Tasks')), 1),
('Attend team meeting', 'Discuss project updates and next steps', (SELECT id FROM lists WHERE name = 'Ongoing' AND board_id = (SELECT id FROM boards WHERE name = 'Work Tasks')), 1);

