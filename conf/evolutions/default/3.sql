# --- First database schema

# --- !Ups

ALTER TABLE message ADD COLUMN user varchar(128);



# --- !Downs

ALTER TABLE products DROP COLUMN user;
