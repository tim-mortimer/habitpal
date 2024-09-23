CREATE TABLE habits
(
    id          uuid        NOT NULL,
    name        varchar(40) NOT NULL,
    type        varchar(40) NOT NULL,
    started_on  timestamptz NOT NULL,
    is_archived boolean     NOT NULL DEFAULT false
)