-- データベースの初期化スクリプト
-- タスク管理アプリ用のテーブル作成

-- リストテーブル
CREATE TABLE IF NOT EXISTS lists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    position_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- タスクテーブル
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status ENUM('TODO', 'DONE') NOT NULL DEFAULT 'TODO',
    position_order INT NOT NULL DEFAULT 0,
    list_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (list_id) REFERENCES lists(id) ON DELETE CASCADE
);

-- 初期データの挿入
INSERT IGNORE INTO lists (id, title, position_order) VALUES 
(1, 'To Do', 0),
(2, 'In Progress', 1),
(3, 'Done', 2);

INSERT IGNORE INTO tasks (title, description, status, position_order, list_id) VALUES 
('サンプルタスク1', 'これはサンプルのタスクです', 'TODO', 0, 1),
('サンプルタスク2', '進行中のタスクです', 'TODO', 0, 2),
('完了タスク', '完了したタスクです', 'DONE', 0, 3); 