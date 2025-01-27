CREATE TABLE news_article (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    url TEXT NOT NULL,
    published_at TEXT NOT NULL
);

INSERT INTO news_article (title, url, published_at) VALUES
('Breaking News: Tech Innovation', 'https://example.com/breaking-tech', '2025-01-25'),
('Economy Update', 'https://example.com/economy-update', '2025-01-26'),
('Sports Update: Local Team Wins', 'https://example.com/local-team-wins', '2025-01-26'),
('Weather Alert', 'https://example.com/weather-alert', '2025-01-26'),
('New Movie Release', 'https://example.com/new-movie', '2025-01-27');