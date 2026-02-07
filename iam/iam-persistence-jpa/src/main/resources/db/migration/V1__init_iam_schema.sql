create table iam_user (
    id uuid not null,
    username varchar(255) not null,
    status varchar(32) not null,
    primary key (id)
);

create table iam_role (
    id uuid not null,
    name varchar(255) not null,
    primary key (id)
);

create table iam_permission (
    id uuid not null,
    code varchar(255) not null,
    primary key (id)
);

create table iam_user_role (
    user_id uuid not null,
    role_id uuid not null,
    primary key (user_id, role_id),
    constraint fk_user_role_user
        foreign key (user_id) references iam_user (id),
    constraint fk_user_role_role
        foreign key (role_id) references iam_role (id)
);

create table iam_role_permission (
    role_id uuid not null,
    permission_id uuid not null,
    primary key (role_id, permission_id),
    constraint fk_role_perm_role
        foreign key (role_id) references iam_role (id),
    constraint fk_role_perm_perm
        foreign key (permission_id) references iam_permission (id)
);
