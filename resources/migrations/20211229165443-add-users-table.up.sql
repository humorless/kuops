CREATE TABLE students
(id serial PRIMARY KEY,
 student_name text NOT NULL,
 student_birthday DATE NOT NULL,
 telephone text NOT NULL,
 kuops_id text UNIQUE NOT NULL);
