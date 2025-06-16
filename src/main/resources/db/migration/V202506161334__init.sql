CREATE TABLE file_renamed (
    file_name TEXT PRIMARY KEY,
    renamed_path TEXT NOT NULL,
    renamed_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
