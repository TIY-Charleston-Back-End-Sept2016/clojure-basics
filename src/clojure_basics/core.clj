(ns clojure-basics.core
  (:require [clojure.string :as str]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h])
  (:gen-class))

(def file-name "people.csv")

(defn read-people []
  (let [people (str/split-lines (slurp file-name))
        people (map (fn [line]
                      (str/split line #","))
                 people)
        header (first people)
        people (rest people)
        people (map (fn [line]
                      (zipmap header line))
                 people)]
    people))

(defn people-html [country]
  (let [people (read-people)
        people (filter (fn [person]
                         (or (nil? country)
                             (= country (get person "country"))))
                 people)]
    [:table
     [:tr
      [:th "ID"]
      [:th "First Name"]
      [:th "Last Name"]
      [:th "Email"]
      [:th "Country"]
      [:th "IP Address"]]
     (map (fn [person]
            [:tr
             [:td (get person "id")]
             [:td (get person "first_name")]
             [:td (get person "last_name")]
             [:td (get person "email")]
             [:td (get person "country")]
             [:td (get person "ip_address")]])
       people)]))

(defn header []
  [:div
   [:a {:href "/Russia"} "Russia"]
   (repeat 5 "&nbsp;")
   [:a {:href "/Brazil"} "Brazil"]
   (repeat 5 "&nbsp;")
   [:a {:href "/Germany"} "Germany"]])

(c/defroutes app
  (c/GET "/" []
    (h/html [:html [:body (header) (people-html nil)]]))
  (c/GET "/:country" [country]
    (h/html [:html [:body (header) (people-html country)]])))

(defn -main [& args]
  (j/run-jetty app {:port 3000}))

