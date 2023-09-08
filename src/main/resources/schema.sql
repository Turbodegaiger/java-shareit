DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
id BIGINT GENERATED BY DEFAULT AS IDENTITY,
name VARCHAR(50) NOT NULL,
email VARCHAR(100) NOT NULL,
CONSTRAINT PK_USER PRIMARY KEY(id),
CONSTRAINT UQ_USER_EMAIL UNIQUE(email)
);

CREATE TABLE IF NOT EXISTS requests (
id BIGINT GENERATED BY DEFAULT AS IDENTITY,
description VARCHAR(512) NOT NULL,
requestor_id BIGINT NOT NULL,
created_datetime TIMESTAMP WITHOUT TIME ZONE NOT NULL CHECK (created_datetime <= CURRENT_TIMESTAMP),
CONSTRAINT PK_REQUEST PRIMARY KEY(id),
CONSTRAINT FK_REQUESTOR FOREIGN KEY(requestor_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS items (
id BIGINT GENERATED BY DEFAULT AS IDENTITY,
name VARCHAR(255) NOT NULL,
description VARCHAR(512) NOT NULL,
available BOOLEAN NOT NULL,
owner_id BIGINT NOT NULL,
request_id BIGINT,
CONSTRAINT PK_ITEM PRIMARY KEY(id),
CONSTRAINT FK_ITEM_OWNER FOREIGN KEY(owner_id) REFERENCES users(id),
CONSTRAINT FK_REQUEST FOREIGN KEY(request_id) REFERENCES requests(id)
);

CREATE TABLE IF NOT EXISTS bookings (
id BIGINT GENERATED BY DEFAULT AS IDENTITY,
start_datetime TIMESTAMP WITHOUT TIME ZONE NOT NULL CHECK (start_datetime < end_datetime AND start_datetime > CURRENT_TIMESTAMP),
end_datetime TIMESTAMP WITHOUT TIME ZONE NOT NULL CHECK (end_datetime > start_datetime AND end_datetime > CURRENT_TIMESTAMP),
item_id BIGINT NOT NULL,
booker_id BIGINT NOT NULL,
status VARCHAR(8) NOT NULL,
CONSTRAINT PK_BOOKING PRIMARY KEY(id),
CONSTRAINT FK_PK_BOOKING_ITEM_ID FOREIGN KEY(item_id) REFERENCES items(id),
CONSTRAINT FK_BOOKER_ID FOREIGN KEY(booker_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS comments (
id BIGINT GENERATED BY DEFAULT AS IDENTITY,
text VARCHAR(512) NOT NULL,
item_id BIGINT NOT NULL,
author_id BIGINT NOT NULL,
created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
CONSTRAINT PK_COMMENTS PRIMARY KEY(id),
CONSTRAINT FK_COMMENTS_ITEM_ID FOREIGN KEY(item_id) REFERENCES items(id),
CONSTRAINT FK_AUTHOR_ID FOREIGN KEY(author_id) REFERENCES users(id)
);