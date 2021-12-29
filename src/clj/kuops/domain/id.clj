(ns kuops.domain.id
  (:require [buddy.core.codecs :as codec]
            [clojure.string :as str]))

(defn hexify [s]
  (-> s
      codec/str->bytes
      codec/bytes->hex))

(defn unhexify [hex]
  (-> hex
      codec/hex->bytes
      codec/bytes->str))

(defn generate
  [{:keys [s-name birthday-str]}]
  (let [name-hex (hexify s-name)
        day-hex   (hexify birthday-str)]
    (str name-hex "-" day-hex)))

(defn parse
  [id-str]
  (let [[name-hex day-hex] (str/split id-str #"-")
        name-s (unhexify name-hex)
        day (unhexify day-hex)]
    {:name name-s
     :birthday day}))

(comment
  (hexify "楊")
  (hexify "楊過")
  (unhexify "e6a58a")
  (unhexify "e6a58ae9818e")
  (generate
   {:s-name "楊過"
    :birthday-str "19871212"}))
