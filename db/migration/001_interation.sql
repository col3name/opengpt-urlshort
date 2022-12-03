CREATE TABLE IF NOT EXISTS url_shortener (
   id SERIAL PRIMARY KEY,
   original_url VARCHAR(255) NOT NULL,
   short_url VARCHAR(255) NOT NULL,
   status VARCHAR(255) NOT NULL DEFAULT 1
);
CREATE TABLE IF NOT EXISTS url_statistics (
    id SERIAL PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    count INTEGER NOT NULL,
    ip_address VARCHAR(255) NOT NULL,
    timestamps TIMESTAMP NOT NULL DEFAULT now()
);
ALTER TABLE url_statistics ADD CONSTRAINT myconstraint UNIQUE (ip_address, url);
COMMIT;

