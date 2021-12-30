#!/usr/bin/env bash
psql -d kuops_dev -c "\copy students FROM 'resources/data/students.csv' DELIMITER ',' CSV HEADER"

psql -d kuops_dev -c "SELECT setval(pg_get_serial_sequence('students', 'id'), 2);"
