create table users (
    id          integer not null primary key,
    username    varchar(255) not null unique,
    password    varchar(255) not null
);
