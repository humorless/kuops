-- :name find-student :? :*
-- :doc retrieves a student kuops_unique_id given the name and birthday
SELECT kuops_id FROM students
WHERE student_name = :name AND student_birthday = :birthday


-- :name get-student :? :1
-- :doc retrieves all the student fields given the id
SELECT * FROM students
WHERE id = :id

-- :name create-student! :! :n
-- :doc creates a new student record
INSERT INTO students
(id, student_name, student_birthday, parent_name, kuops_id)
VALUES (DEFAULT, :student-name, :birthday, :parent-name, :kuops-id)

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
