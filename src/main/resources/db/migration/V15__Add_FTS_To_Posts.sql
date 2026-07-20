ALTER TABLE posts ADD COLUMN search_vector tsvector GENERATED ALWAYS AS (
    setweight(to_tsvector('portuguese', coalesce(title, '')), 'A') ||
    setweight(to_tsvector('portuguese', coalesce(tldr, '')), 'B') ||
    setweight(to_tsvector('portuguese', coalesce(content, '')), 'C')
) STORED;

CREATE INDEX idx_posts_search_vector ON posts USING GIN (search_vector);
