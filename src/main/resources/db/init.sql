
create table if not exists tb_file_info(
    id varchar(36) not null default gen_random_uuid(),
    file_code varchar(36),
    file_name varchar(36),
    file_path varchar(255),
    create_time timestamp not null default now(),
    update_time timestamp not null default now(),
    constraint tb_file_info_pk PRIMARY KEY (id)
    );
comment on table tb_file_info is '文件表';
comment on COLUMN tb_file_info.file_code is '文件编号';
comment on COLUMN tb_file_info.file_name is '文件名称';
comment on COLUMN tb_file_info.file_path is '文件路径';
comment on COLUMN tb_file_info.create_time is '创建时间';
comment on COLUMN tb_file_info.update_time is '更新时间';

create table if not exists tb_datasource (
    datasource_name varchar(255) primary key,
    url varchar(255) not null,
    username varchar(255) not null,
    password varchar(255) not null,
    driver_class_name varchar(255) not null,
    operation_person varchar(255) not null,
    create_time timestamp default current_timestamp,
    update_time timestamp default current_timestamp
);

comment on table tb_datasource is '数据源表';
comment on column tb_datasource.datasource_name is '连接源名称';
comment on column tb_datasource.url is '数据库url';
comment on column tb_datasource.username is '用户名';
comment on column tb_datasource.password is '密码';
comment on column tb_datasource.driver_class_name is '驱动';
comment on column tb_datasource.operation_person is '操作人';