# --- First database schema

# --- !Ups

create sequence s_message_id;

create table message (
  id      bigint DEFAULT nextval('s_message_id'),
  thread  bigint references thread(id),
  body    text
);


# --- !Downs

drop table message;
drop sequence s_message_id;