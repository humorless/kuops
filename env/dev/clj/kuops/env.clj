(ns kuops.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [kuops.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[kuops started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[kuops has shut down successfully]=-"))
   :middleware wrap-dev})
