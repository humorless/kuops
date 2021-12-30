(ns kuops.domain.handler
  (:require [kuops.domain.id :as id]
            [kuops.db.core :as db]
            [clojure.tools.logging :as log]))

(defn find-student
  [input]
  (let [res (db/find-student input)]
    (when (some? res)
      (:concat res))))

(defn register
  "register a new student
  
   return failed if the student already exists
   return success"
  [{:keys [name birthday telephone classroom-id] :as input}]
  (let [student (db/find-student input)]
    (if (some? student)
      [:failed "already exist"]
      (try
        [:success (db/create-student! input)]
        (catch Exception ex
          (log/error (.getMessage ex))
          [:failed "input data error"])))))

