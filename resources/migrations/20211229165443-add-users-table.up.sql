CREATE TABLE students
(id serial PRIMARY KEY,
 student_name text NOT NULL,
 student_birthday DATE NOT NULL,
 parent_name text NOT NULL,
 kuops_unique_id text NOT NULL);