CREATE TABLE IF NOT EXISTS events
(
    id BINARY (16) PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    start_at    TIMESTAMP(3) NOT NULL,
    finish_at   TIMESTAMP(3) NOT NULL,
    location    TEXT         NULL
);

-- Optional indexes for faster range queries or sorting
CREATE INDEX idx_events_start_at ON events (start_at);
CREATE INDEX idx_events_finish_at ON events (finish_at);
