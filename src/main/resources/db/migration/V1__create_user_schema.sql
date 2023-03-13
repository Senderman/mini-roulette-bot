CREATE TABLE `USER` (
    user_id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    coins INT NOT NULL,
    pending_coins INT NOT NULL,
    last_coin_request_date TIMESTAMP NOT NULL
);