CREATE TABLE courses (
                         id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         name TEXT UNIQUE NOT NULL,
                         description TEXT NOT NULL,
                         price NUMERIC(6,2),
                         category TEXT NOT NULL,
                         duration BIGINT NOT NULL,
                         difficulty TEXT NOT NULL,
                         image_url TEXT NOT NULL,
                         module_collection JSONB NOT NULL,
                         created_at TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
