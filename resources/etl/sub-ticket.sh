#!/usr/bin/env bash

function changeHeader() {
  FILENAME=$(mktemp)
  echo 'id,name,grade,status,change,subject,stage,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19' > $FILENAME
  cat $1 | sed -e '1 d' >> $FILENAME
  export FILENAME
}

function trans() {
  ## debug
  ## cat $1 | cq -i csv
  cat $1 | \
    cq -i csv '(mapcat
          (fn [{:keys [id name grade status change subject stage] :as student-row}]
            (let [fact-keys (mapv (comp keyword str) (range 20))
                  fact (select-keys student-row fact-keys)
                  fact* (into {} (remove (fn [[k v]] (= v "")) fact))]
              (map 
                (fn [[k v]]
                (let [index-of (fn index-of [x coll]
                                 (let [idx? (fn [i a] (when (= x a) i))]
                                 (first (keep-indexed idx? coll))))
                      p (index-of k fact-keys)]
                    ;; 在運算的過程之中, :191 在內部會轉換成 19 來處理
                    ;; 在運算的過程之中, :181 在內部會轉換成 18 來處理
                    {:id id
                     :name name
                     :grade grade
                     :status status
                     :change change
                     :subject subject
                     :stage stage
                     :min_page (+ 1 (* 10 p))
                     :times v}
                   )
                ) fact*))))' -o csv
}

changeHeader $1
trans $FILENAME
