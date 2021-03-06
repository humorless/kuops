-- :name find-student :? :1
-- :doc retrieves a student kuops_unique_id given the name and birthday
SELECT CONCAT(classroom_id, '_',  id) FROM students
WHERE student_name = :name AND student_birthday = :birthday AND telephone = :telephone

-- :name get-student :? :1
-- :doc retrieves all the student fields given the id
SELECT * FROM students
WHERE id = :id

-- :name create-student! :! :n
-- :doc creates a new student record
INSERT INTO students
(id, student_name, student_birthday, telephone, classroom_id)
VALUES (DEFAULT, :name, :birthday, :telephone, :classroom-id)

-- :name get-student-serial :? :1
-- :doc returns a the student serial id
SELECT currval(pg_get_serial_sequence('students', 'id'));

-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(id, first_name, last_name, email, pass)
VALUES (:id, :first_name, :last_name, :email, :pass)

-- :name update-user! :! :n
-- :doc updates an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieves a user record given the id
SELECT * FROM users
WHERE id = :id

-- :name delete-user! :! :n
-- :doc deletes a user record given the id
DELETE FROM users
WHERE id = :id
