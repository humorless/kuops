#!/usr/bin/env bash

## 此函數處理單一 yaml 檔
function trans() {
  cat $1 | \
    cq '(mapcat 
          (fn [e]
            (let [{:keys [progressdetail]} e
                  f (dissoc e :progressdetail)]
              (map 
                (fn [d]
                  (let [[x y z] (clojure.string/split d #" ")
                        [a b c] (clojure.string/split z #"[-*]")]
                    (assoc f :progress_entry_0 x
                             :progress_entry_1 y
                             :progress_entry_2 a
                             :progress_entry_3 b
                             :progress_entry_4 c))) progressdetail))))' -o csv
}

## 考慮在營業所的資料夾下執行
for date_dir in $(ls .)
do
    if [ -d $date_dir ]; then
        ##echo "in the directory" $date_dir
        for file in $(ls $date_dir)
        do
            ##echo "find the file" $file
            ## yaml_path combine the directory name and file name to create to relative path
            yaml_path="./$date_dir/$file"
            trans $yaml_path | \
            awk -v date="$date_dir" -v classroom="$file" \
                'BEGIN {FS=","; OFS=","} {print date, classroom, $0}'
        done
    fi
done
