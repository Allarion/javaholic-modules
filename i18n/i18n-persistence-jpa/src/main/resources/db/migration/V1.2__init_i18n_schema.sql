create table i18n_entry (
    id uuid not null,
    translation_key  varchar(255) not null,
    locale varchar(32) not null,
    translation_value  varchar(2000) not null,
    version bigint,
    primary key (id),
    constraint uk_i18n_key_locale unique (translation_key, locale)
);
