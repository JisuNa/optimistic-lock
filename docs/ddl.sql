use korail;

create table seat
(
    id      bigint unsigned auto_increment comment '식별값',
    user_id bigint unsigned null comment '유저 식별값',
    seat_number varchar(32) not null comment '좌석번호',
    constraint primary key (id)
);
