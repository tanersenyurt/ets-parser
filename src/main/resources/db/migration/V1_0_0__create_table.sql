create table hotel
(
    id             int(16) auto_increment
        primary key,
    hotelId        varchar(255)      null,
    name           varchar(255)      not null,
    discount       int(16) default 0 not null,
    score          double(10, 3)     not null,
    comments_count int(16)           not null,
    worth          double(10, 3)     not null,
    price          double(10, 3)     not null,
    campaignName   varchar(255)      null,
    boardType      varchar(255)      null,
    hotelType      varchar(255)      null,
    country        varchar(255)      null,
    city           varchar(255)      null,
    state          varchar(255)      null,
    theme          varchar(255)      null,
    facilities     text              null,
    lat            varchar(20)       null,
    lon            varchar(20)       null,
    hotelUrl       varchar(255)      null,
    imageUrl       varchar(255)      null,
    source         varchar(10)       not null,
    fromDate       varchar(10)       not null,
    toDate         varchar(10)       not null,
    md5            varchar(32)       not null,
    dateCreated    datetime(6)       not null
);