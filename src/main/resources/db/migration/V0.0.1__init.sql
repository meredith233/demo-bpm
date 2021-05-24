create table bpm_template
(
    id           bigint                              not null comment 'id',
    condition_id bigint                              null comment '用于获取查询条件的id',
    process_name varchar(255)                        not null comment '流程名称',
    process_type int(2)                              not null comment '流程类型',
    remark       varchar(255)                        null comment '备注',
    delete_flag  int(2)    default 0                 not null comment '删除状态（0-未删除、1-已删除）',
    created_by   bigint                              null comment '创建人',
    created_at   datetime                            null comment '创建时间',
    updated_by   bigint                              null comment '最后更新人',
    updated_at   timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '最后更新时间',
    version      bigint    default 0                 null comment '版本',
    constraint bpm_template_pk
        primary key (id)
);

create table bpm_template_node
(
    id              bigint                              not null comment 'id',
    process_id      bigint                              not null comment '流程id -> bpm_template -> id',
    parent_node_id  bigint                              null comment '父节点id -> bpm_template_node -> id',
    node_name       varchar(255)                        not null comment '流程节点名称',
    node_type       int(2)                              not null comment '流程节点类型',
    condition_str   varchar(255)                        null comment '条件节点判断条件',
    all_need_finish int(4)                              null comment '需要完成审批的子节点数',
    location        varchar(255)                        not null comment '节点定位值',
    sort            int(4)                              not null comment '排序',
    remark          varchar(255)                        null comment '备注',
    delete_flag     int(2)    default 0                 not null comment '删除状态（0-未删除、1-已删除）',
    created_by      bigint                              null comment '创建人',
    created_at      datetime                            null comment '创建时间',
    updated_by      bigint                              null comment '最后更新人',
    updated_at      timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '最后更新时间',
    version         bigint    default 0                 null comment '版本',
    constraint bpm_template_node_pk
        primary key (id)
);

