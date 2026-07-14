CREATE TABLE authors (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    bio TEXT,
    main_title VARCHAR(255),
    preferred_social_network VARCHAR(255),
    preferred_social_network_username VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP,
    CONSTRAINT fk_author_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE bibliographic_references (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP,
    CONSTRAINT fk_biblio_post FOREIGN KEY (post_id) REFERENCES posts(id)
);

CREATE TABLE footnotes (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP,
    CONSTRAINT fk_footnote_post FOREIGN KEY (post_id) REFERENCES posts(id)
);

ALTER TABLE posts ADD COLUMN tldr TEXT;
ALTER TABLE posts ADD COLUMN published_at TIMESTAMP;

-- Drop foreign key to users
ALTER TABLE posts DROP CONSTRAINT fk_post_author;

-- Add foreign key to authors
ALTER TABLE posts ADD CONSTRAINT fk_post_author FOREIGN KEY (author_id) REFERENCES authors(id);
