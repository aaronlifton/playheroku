# --- First database schema

# --- !Ups

create sequence s_thread_id;

create table thread (
  id    bigint DEFAULT nextval('s_thread_id'),
  name  varchar(128)
);


# --- !Downs

drop table thread;
drop sequence s_thread_id;