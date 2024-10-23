CREATE TABLE IF NOT EXISTS habits
(
    id          uuid        NOT NULL PRIMARY KEY,
    name        varchar(40) NOT NULL,
    type        varchar(40) NOT NULL,
    started_on  timestamptz NOT NULL,
    is_archived boolean     NOT NULL DEFAULT false
)