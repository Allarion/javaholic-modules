ALTER TABLE iam_user RENAME COLUMN username TO identifier;
ALTER TABLE iam_user ADD COLUMN display_name VARCHAR(255);
