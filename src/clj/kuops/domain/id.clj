(ns kuops.domain.id)

(defn generate 
  [{:keys [classroom serial]}]
  (str classroom "_" serial))

(comment
  (generate
   {:classroom "AABBCC"
    :serial 12344}))
