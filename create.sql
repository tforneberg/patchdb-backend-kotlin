create table bands (id  serial not null, name varchar(255), primary key (id))
create table collections (user_id int4 not null, patch_id int4 not null)
create table news (id  serial not null, content varchar(255), date_created date, title varchar(255), created_by int4, primary key (id))
create table patches (id  serial not null, amount_users int4, date_inserted date, description varchar(255), image varchar(255), manufacturer varchar(255), name varchar(255), num_of_copies int4, release_date date, state varchar(255), type varchar(255), band_id int4 not null, user_inserted int4, primary key (id))
create table users (id  serial not null, email varchar(255), image varchar(255), name varchar(255), password varchar(255), status varchar(255), primary key (id))
alter table collections add constraint FKg8n4u5yu0m1ksvx498o0hj541 foreign key (patch_id) references patches
alter table collections add constraint FKn7pdedyqaiddr0uxdj603my7d foreign key (user_id) references users
alter table news add constraint FKnene07l7q6d9a5kogt3be6uei foreign key (created_by) references users
alter table patches add constraint FK7oma7e6xevg32nb7h70w2t1et foreign key (band_id) references bands
alter table patches add constraint FK46t4m6ubef331mlp2hqb9m58a foreign key (user_inserted) references users
create table bands (id  serial not null, name varchar(255), primary key (id))
create table collections (user_id int4 not null, patch_id int4 not null)
create table news (id  serial not null, content varchar(255), date_created date, title varchar(255), created_by int4, primary key (id))
create table patches (id  serial not null, amount_users int4, date_inserted date, description varchar(255), image varchar(255), manufacturer varchar(255), name varchar(255), num_of_copies int4, release_date date, state varchar(255), type varchar(255), band_id int4 not null, user_inserted int4, primary key (id))
create table users (id  serial not null, email varchar(255), image varchar(255), name varchar(255), password varchar(255), status varchar(255), primary key (id))
alter table collections add constraint FKg8n4u5yu0m1ksvx498o0hj541 foreign key (patch_id) references patches
alter table collections add constraint FKn7pdedyqaiddr0uxdj603my7d foreign key (user_id) references users
alter table news add constraint FKnene07l7q6d9a5kogt3be6uei foreign key (created_by) references users
alter table patches add constraint FK7oma7e6xevg32nb7h70w2t1et foreign key (band_id) references bands
alter table patches add constraint FK46t4m6ubef331mlp2hqb9m58a foreign key (user_inserted) references users
create table bands (id  serial not null, name varchar(255), primary key (id))
create table collections (user_id int4 not null, patch_id int4 not null)
create table news (id  serial not null, content varchar(255), date_created date, title varchar(255), created_by int4, primary key (id))
create table patches (id  serial not null, amount_users int4, date_inserted date, description varchar(255), image varchar(255), manufacturer varchar(255), name varchar(255), num_of_copies int4, release_date date, state varchar(255), type varchar(255), band_id int4 not null, user_inserted int4, primary key (id))
create table users (id  serial not null, email varchar(255), image varchar(255), name varchar(255), password varchar(255), status varchar(255), primary key (id))
alter table collections add constraint FKg8n4u5yu0m1ksvx498o0hj541 foreign key (patch_id) references patches
alter table collections add constraint FKn7pdedyqaiddr0uxdj603my7d foreign key (user_id) references users
alter table news add constraint FKnene07l7q6d9a5kogt3be6uei foreign key (created_by) references users
alter table patches add constraint FK7oma7e6xevg32nb7h70w2t1et foreign key (band_id) references bands
alter table patches add constraint FK46t4m6ubef331mlp2hqb9m58a foreign key (user_inserted) references users
create table bands (id  serial not null, name varchar(255), primary key (id))
create table collections (user_id int4 not null, patch_id int4 not null)
create table news (id  serial not null, content varchar(255), date_created date, title varchar(255), created_by int4, primary key (id))
create table patches (id  serial not null, amount_users int4, date_inserted date, description varchar(255), image varchar(255), manufacturer varchar(255), name varchar(255), num_of_copies int4, release_date date, state varchar(255), type varchar(255), band_id int4 not null, user_inserted int4, primary key (id))
create table users (id  serial not null, email varchar(255), image varchar(255), name varchar(255), password varchar(255), status varchar(255), primary key (id))
alter table collections add constraint FKg8n4u5yu0m1ksvx498o0hj541 foreign key (patch_id) references patches
alter table collections add constraint FKn7pdedyqaiddr0uxdj603my7d foreign key (user_id) references users
alter table news add constraint FKnene07l7q6d9a5kogt3be6uei foreign key (created_by) references users
alter table patches add constraint FK7oma7e6xevg32nb7h70w2t1et foreign key (band_id) references bands
alter table patches add constraint FK46t4m6ubef331mlp2hqb9m58a foreign key (user_inserted) references users
create table bands (id  serial not null, name varchar(255), primary key (id))
create table collections (user_id int4 not null, patch_id int4 not null)
create table news (id  serial not null, content varchar(255), date_created date, title varchar(255), created_by int4, primary key (id))
create table patches (id  serial not null, amount_users int4, date_inserted date, description varchar(255), image varchar(255), manufacturer varchar(255), name varchar(255), num_of_copies int4, release_date date, state varchar(255), type varchar(255), band_id int4 not null, user_inserted int4, primary key (id))
create table users (id  serial not null, email varchar(255), image varchar(255), name varchar(255), password varchar(255), status varchar(255), primary key (id))
alter table collections add constraint FKg8n4u5yu0m1ksvx498o0hj541 foreign key (patch_id) references patches
alter table collections add constraint FKn7pdedyqaiddr0uxdj603my7d foreign key (user_id) references users
alter table news add constraint FKnene07l7q6d9a5kogt3be6uei foreign key (created_by) references users
alter table patches add constraint FK7oma7e6xevg32nb7h70w2t1et foreign key (band_id) references bands
alter table patches add constraint FK46t4m6ubef331mlp2hqb9m58a foreign key (user_inserted) references users
create table bands (id  serial not null, name varchar(255), primary key (id))
create table collections (user_id int4 not null, patch_id int4 not null)
create table news (id  serial not null, content varchar(255), date_created date, title varchar(255), created_by int4, primary key (id))
create table patches (id  serial not null, amount_users int4, date_inserted date, description varchar(255), image varchar(255), manufacturer varchar(255), name varchar(255), num_of_copies int4, release_date date, state varchar(255), type varchar(255), band_id int4 not null, user_inserted int4, primary key (id))
create table users (id  serial not null, email varchar(255), image varchar(255), name varchar(255), password varchar(255), status varchar(255), primary key (id))
alter table collections add constraint FKg8n4u5yu0m1ksvx498o0hj541 foreign key (patch_id) references patches
alter table collections add constraint FKn7pdedyqaiddr0uxdj603my7d foreign key (user_id) references users
alter table news add constraint FKnene07l7q6d9a5kogt3be6uei foreign key (created_by) references users
alter table patches add constraint FK7oma7e6xevg32nb7h70w2t1et foreign key (band_id) references bands
alter table patches add constraint FK46t4m6ubef331mlp2hqb9m58a foreign key (user_inserted) references users
create table bands (id  serial not null, name varchar(255), primary key (id))
create table collections (user_id int4 not null, patch_id int4 not null)
create table news (id  serial not null, content varchar(255), date_created date, title varchar(255), created_by int4, primary key (id))
create table patches (id  serial not null, amount_users int4, date_inserted date, description varchar(255), image varchar(255), manufacturer varchar(255), name varchar(255), num_of_copies int4, release_date date, state varchar(255), type varchar(255), band_id int4 not null, user_inserted int4, primary key (id))
create table users (id  serial not null, email varchar(255), image varchar(255), name varchar(255), password varchar(255), status varchar(255), primary key (id))
alter table collections add constraint FKg8n4u5yu0m1ksvx498o0hj541 foreign key (patch_id) references patches
alter table collections add constraint FKn7pdedyqaiddr0uxdj603my7d foreign key (user_id) references users
alter table news add constraint FKnene07l7q6d9a5kogt3be6uei foreign key (created_by) references users
alter table patches add constraint FK7oma7e6xevg32nb7h70w2t1et foreign key (band_id) references bands
alter table patches add constraint FK46t4m6ubef331mlp2hqb9m58a foreign key (user_inserted) references users
create table bands (id  serial not null, name varchar(255), primary key (id))
create table collections (user_id int4 not null, patch_id int4 not null)
create table news (id  serial not null, content varchar(255), date_created date, title varchar(255), created_by int4, primary key (id))
create table patches (id  serial not null, amount_users int4, date_inserted date, description varchar(255), image varchar(255), manufacturer varchar(255), name varchar(255), num_of_copies int4, release_date date, state varchar(255), type varchar(255), band_id int4 not null, user_inserted int4, primary key (id))
create table users (id  serial not null, email varchar(255), image varchar(255), name varchar(255), password varchar(255), status varchar(255), primary key (id))
alter table collections add constraint FKg8n4u5yu0m1ksvx498o0hj541 foreign key (patch_id) references patches
alter table collections add constraint FKn7pdedyqaiddr0uxdj603my7d foreign key (user_id) references users
alter table news add constraint FKnene07l7q6d9a5kogt3be6uei foreign key (created_by) references users
alter table patches add constraint FK7oma7e6xevg32nb7h70w2t1et foreign key (band_id) references bands
alter table patches add constraint FK46t4m6ubef331mlp2hqb9m58a foreign key (user_inserted) references users
create table bands (id  serial not null, name varchar(255), primary key (id))
create table collections (user_id int4 not null, patch_id int4 not null)
create table news (id  serial not null, content varchar(255), date_created date, title varchar(255), created_by int4, primary key (id))
create table patches (id  serial not null, amount_users int4, date_inserted date, description varchar(255), image varchar(255), manufacturer varchar(255), name varchar(255), num_of_copies int4, release_date date, state varchar(255), type varchar(255), band_id int4 not null, user_inserted int4, primary key (id))
create table users (id  serial not null, email varchar(255), image varchar(255), name varchar(255), password varchar(255), status varchar(255), primary key (id))
alter table collections add constraint FKg8n4u5yu0m1ksvx498o0hj541 foreign key (patch_id) references patches
alter table collections add constraint FKn7pdedyqaiddr0uxdj603my7d foreign key (user_id) references users
alter table news add constraint FKnene07l7q6d9a5kogt3be6uei foreign key (created_by) references users
alter table patches add constraint FK7oma7e6xevg32nb7h70w2t1et foreign key (band_id) references bands
alter table patches add constraint FK46t4m6ubef331mlp2hqb9m58a foreign key (user_inserted) references users
create table bands (id  serial not null, name varchar(255), primary key (id))
create table collections (user_id int4 not null, patch_id int4 not null)
create table news (id  serial not null, content varchar(255), date_created date, title varchar(255), created_by int4, primary key (id))
create table patches (id  serial not null, amount_users int4, date_inserted date, description varchar(255), image varchar(255), manufacturer varchar(255), name varchar(255), num_of_copies int4, release_date date, state varchar(255), type varchar(255), band_id int4 not null, user_inserted int4, primary key (id))
create table users (id  serial not null, email varchar(255), image varchar(255), name varchar(255), password varchar(255), status varchar(255), primary key (id))
alter table collections add constraint FKg8n4u5yu0m1ksvx498o0hj541 foreign key (patch_id) references patches
alter table collections add constraint FKn7pdedyqaiddr0uxdj603my7d foreign key (user_id) references users
alter table news add constraint FKnene07l7q6d9a5kogt3be6uei foreign key (created_by) references users
alter table patches add constraint FK7oma7e6xevg32nb7h70w2t1et foreign key (band_id) references bands
alter table patches add constraint FK46t4m6ubef331mlp2hqb9m58a foreign key (user_inserted) references users
