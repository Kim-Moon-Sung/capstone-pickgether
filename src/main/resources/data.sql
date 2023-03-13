insert into user(user_Id, user_password, email, nickname, memo, birthday, created_at)
values ('user', '{noop}password', 'email@email.com', 'nick', 'memo', now(), now());

-- 게시글 수정
insert into vote(category, content, create_at, display_range, expired_at, is_multi_pick, modified_at, title, user_id)
values('WORRY', 'This is #content', now(), 'FRIEND', now(), false, now(), 'title', 'user');

insert into vote_option(content, image_link, vote_id)
values('new option1', '/link/image1.png', 1);

insert into vote_option(content, image_link, vote_id)
values('new option2', '/link/image2.png', 1);

insert into hashtag(content)
values('content');

insert into vote_hashtag(hashtag_id, vote_id)
values(1, 1);

-- 게시글 정렬
insert into user(user_Id, user_password, email, nickname, memo, birthday, created_at)
values ('user2', '{noop}password', 'email@email.com', 'nick', 'memo', now(), now());

insert into vote(category, content, create_at, display_range, expired_at, is_multi_pick, modified_at, title, user_id)
values('FREE', 'This is FREE', now(), 'FRIEND', now(), false, now(), 'FREE', 'user2');

insert into hashtag(content)
values('content2');

insert into vote_hashtag(hashtag_id, vote_id)
values(1, 2);

insert into vote_hashtag(hashtag_id, vote_id)
values(2, 2);

insert into vote(category, content, create_at, display_range, expired_at, is_multi_pick, modified_at, title, user_id)
values('SURVEY', 'This is SURVEY', now(), 'FRIEND', now(), false, now(), 'SURVEY', 'user2');

insert into vote_hashtag(hashtag_id, vote_id)
values(1, 3);

insert into vote(category, content, create_at, display_range, expired_at, is_multi_pick, modified_at, title, user_id)
values('ENTERPRISE', 'This is ENTERPRISE', now(), 'FRIEND', now(), false, now(), 'ENTERPRISE', 'user2');

insert into vote_hashtag(hashtag_id, vote_id)
values(1, 4);

insert into vote(category, content, create_at, display_range, expired_at, is_multi_pick, modified_at, title, user_id)
values('WORRY', 'This is WORRY', now(), 'FRIEND', now(), false, now(), 'WORRY', 'user2');

insert into vote_hashtag(hashtag_id, vote_id)
values(1, 5);