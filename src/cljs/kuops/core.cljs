(ns kuops.core
  (:require
   [kitchen-async.promise :as p]
   [lambdaisland.fetch :as fetch]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   [markdown.core :refer [md->html]]
   [kuops.ajax :as ajax]
   [ajax.core :refer [GET POST]]
   [reitit.core :as reitit]
   [clojure.string :as string])
  (:import goog.History))

;;;; Application
(defonce state (r/atom {:name nil
                        :birthday nil
                        :telephone nil
                        :classroom-id nil
                        :result nil}))
(comment
  (defn handle-form-submit [e]
    (.. e preventDefault)
    (let [chk (-> js/document (.getElementById "form-id") (.checkValidity))]
      (println "form validation result: " chk)
      (-> js/document (.getElementById "form-id") (.reportValidity))
      (when chk))))

(defn handle-form-clear [e]
  (swap! state assoc :result nil))

(defn handle-form-register [e]
  (let [{:keys [name birthday telephone classroom-id]} @state
        body {:name name
              :birthday birthday
              :telephone telephone
              :classroom-id classroom-id}
        opts {:body body
              :method :post
              :content-type :json
              :accept :json}]
    (tap> [:handle-form-register opts])
    (p/try
      (p/let [resp (fetch/request "/api/register" opts)
              body (js->clj (:body resp) :keywordize-keys true)
              result (:result body)]
        (tap> [:handle-form-submit body])
        (if (= "success" (first result))
          (swap! state assoc :result "成功")
          (swap! state assoc :result "失敗")))
      (p/catch :default e
        (prn {:url "/api/query-id" :opts opts :e e})))))

(defn handle-form-submit [e]
  (let [{:keys [name birthday telephone]} @state
        body {:name name
              :birthday birthday
              :telephone telephone}
        opts {:body body
              :method :post
              :content-type :json
              :accept :json}]
    (tap> [:handle-form-submit opts])
    (p/try
      (p/let [resp (fetch/request "/api/query-id" opts)
              body (js->clj (:body resp) :keywordize-keys true)
              result (:result body)]
        (tap> [:handle-form-submit body])
        (if (some? result)
          (swap! state assoc :result result)
          (swap! state assoc :result "不存在")))

      (p/catch :default e
        (prn {:url "/api/query-id" :opts opts :e e})))))
;;;; Plumbing code


(defonce session (r/atom {:page :query}))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :class (when (= page (:page @session)) "is-active")}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
    [:nav.navbar.is-info>div.container
     [:div.navbar-brand
      [:a.navbar-item {:href "/" :style {:font-weight :bold}} "Student System"]
      [:span.navbar-burger.burger
       {:data-target :nav-menu
        :on-click #(swap! expanded? not)
        :class (when @expanded? :is-active)}
       [:span] [:span] [:span]]]
     [:div#nav-menu.navbar-menu
      {:class (when @expanded? :is-active)}
      [:div.navbar-start
       [nav-link "?#/query" "Query" :query]
       [nav-link "?#/register" "Register" :register]]]]))

(defn query-page []
  (let [{:keys [name birthday telephone classroom-id result]} @state]
    [:section.section>div.container>div.content
     [:form {:id "form-id"}
      [:div
       [:label "學生姓名"] [:input {:type "text"
                                :placeholder "王大明"
                                :value name
                                :on-change (fn [e]
                                             (let [d (-> e .-target .-value)]
                                               (swap! state assoc :name d)))}]]
      [:div
       [:label "學生生日"] [:input {:type "date"
                                :placeholder "2022-01-01"
                                :value birthday
                                :on-change (fn [e]
                                             (let [d (-> e .-target .-value)]
                                               (swap! state assoc :birthday d)))}]]
      [:div
       [:label "註冊電話"] [:input {:type "tel"
                                :placeholder "0800-092-000"
                                :value telephone
                                :on-change (fn [e]
                                             (let [d (-> e .-target .-value)]
                                               (swap! state assoc :telephone d)))}]]
      [:div
       [:input {:type "submit"
                :value "查詢"
                :on-click handle-form-submit}]]
      [:div
       [:input {:type "submit"
                :value "清除結果"
                :on-click handle-form-clear}]]
      [:div
       [:pre result]]]]))

(defn register-page []
  (let [{:keys [name birthday telephone classroom-id result]} @state]
    [:section.section>div.container>div.content
     [:form {:id "form-id"}
      [:div
       [:label "學生姓名"] [:input {:type "text"
                                :placeholder "王大明"
                                :value name
                                :on-change (fn [e]
                                             (let [d (-> e .-target .-value)]
                                               (swap! state assoc :name d)))}]]
      [:div
       [:label "學生生日"] [:input {:type "date"
                                :placeholder "2022-01-01"
                                :value birthday
                                :on-change (fn [e]
                                             (let [d (-> e .-target .-value)]
                                               (swap! state assoc :birthday d)))}]]
      [:div
       [:label "註冊電話"] [:input {:type "tel"
                                :placeholder "0800-092-000"
                                :value telephone
                                :on-change (fn [e]
                                             (let [d (-> e .-target .-value)]
                                               (swap! state assoc :telephone d)))}]]
      [:div
       [:label "教室ID"] [:input {:type "text"
                                :placeholder "AABBB"
                                :value classroom-id
                                :on-change (fn [e]
                                             (let [d (-> e .-target .-value)]
                                               (swap! state assoc :classroom-id d)))}]]
      [:div
       [:input {:type "submit"
                :value "註冊"
                :on-click handle-form-register}]]
      [:div
       [:input {:type "submit"
                :value "清除結果"
                :on-click handle-form-clear}]]
      [:div
       [:pre result]]]]))

(defn pages [k]
  (case k
    :query #'query-page
    :register #'register-page
    #'query-page))

(defn page []
  [(pages (:page @session))])

;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/query" :query]
    ["/register" :register]]))

(defn match-route [uri]
  (->> (or (not-empty (string/replace uri #"^.*#" "")) "/")
       (reitit/match-by-path router)
       :data
       :name))
;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     HistoryEventType/NAVIGATE
     (fn [^js/Event.token event]
       (swap! session assoc :page (match-route (.-token event)))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(swap! session assoc :docs %)}))

(defn ^:dev/after-load mount-components []
  (rdom/render [#'navbar] (.getElementById js/document "navbar"))
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-components))
