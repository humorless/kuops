#!/usr/bin/env bash
psql -d kuops_dev -c "\copy students FROM 'resources/data/students.csv' DELIMITER ',' CSV HEADER"
