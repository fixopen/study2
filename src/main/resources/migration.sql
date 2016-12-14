alter table additionals rename column table_name to object_type;
alter table answer_records rename column commit_time to object_type;
alter table answer_records alter column answer type integer;
alter table answer_records rename column answer to index;
alter table cards rename column subject to subject_id;
alter table comments drop column client_id;
alter table consumptions alter column object_type type name;
-- split image_texts to image_id and content
alter table knowledge_point_content_maps drop column subject_id;
alter table knowledge_point_content_maps drop column grade;
alter table knowledge_point_content_maps drop column volume_id;
alter table knowledge_point_content_maps rename column type to object_type;
alter table knowledge_points drop column subject_id;
alter table knowledge_points drop column grade;
alter table knowledge_points drop column store_path;
alter table knowledge_points drop column video_url;
alter table knowledge_points rename column title to name;
alter table knowledge_points add column price bigint;
alter table knowledge_points add column discount double precision;
alter table problem_options alter column "order" type integer;
alter table problem_options add column index integer;
alter table problem_standard_answers rename column name to index;
alter table problem_standard_answers alter column index type integer;
alter table problems drop column subject_id;
alter table problems drop column volume_id;
alter table problems drop column knowledge_point_id;
alter table problems rename column title to name;
alter table problems add column image_id bigint;
-- split problems to image_id from store_path
alter table problems drop column store_path;
alter table problems add column video_id bigint;
-- split problems to video_id from video_url & video_image
alter table problems drop column video_url;
alter table problems drop column video_image;
alter table schedulers rename column title to name;
alter table schedulers rename column cdn_link to content_link;
alter table schedulers rename column generalization to abstraction;
alter table schedulers add column teacher_id bigint;
-- split schedulers to users from teacher & teacher_description
alter table schedulers drop column teacher;
alter table schedulers drop column teacher_description;
alter table schedulers add column price bigint;
alter table schedulers add column discount double precision;
alter table subjects add column no name;
alter table users rename column location to address;
alter table videos rename column cover to cover_id;
alter table volumes rename column title to name;
alter table volumes add column cover_id bigint;
-- split volumes to images from bookcover
alter table volumes drop column bookcover;
alter table wechat_users drop column ref_id;
alter table wechat_users alter column user_id drop not null;