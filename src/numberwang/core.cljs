(ns numberwang.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as string]))

(enable-console-print!)

(println "This text is printed from src/numberwang/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:view :entry}))

(defn handle-change [e owner {:keys [text]}]
  (let [value (.. e -target -value)]
    (om/set-state! owner :text value)))

(defn set-entry []
  (swap! app-state assoc :view :entry))

(defn set-winner []
  (js/setTimeout set-entry 5000)
  (swap! app-state assoc :view :win))

(defn set-loser []
  (js/setTimeout set-entry 2000)
  (swap! app-state assoc :view :lose))

(defn check-winner [_ owner]
  (if (<= 50 (rand-int 100))
    (set-winner)
    (set-loser))
  (om/set-state! owner :text ""))

(defn entry-view [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:text ""})
    om/IRenderState
    (render-state [this state]
      (dom/div #js {:className "container"}
        (dom/div #js {:className "row"}
          (dom/input
            #js {:type "number" :value (:text state)
                 :onChange #(handle-change % owner state)})
          (dom/button #js {:onClick #(check-winner data owner)
                           :disabled (string/blank? (:text state))
                           :className "btn btn-default"}
                      "Submit"))))))

(defn win-view [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div #js {:className "container"
                    :style {:height "100%"
                            :width "100%"}}
        (dom/h1 #js {:style {:text-align "justify"}}
          "THAT'S NUMBERWANG!!!!!")))))

(defn lose-view [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div #js {:className "container"
                    :style {:text-align "justify"}}
        (dom/h1 nil
          "NO")))))

(defmulti top-view (fn [data _] (:view data)))

(defmethod top-view :entry
  [data owner] (entry-view data owner))

(defmethod top-view :win
  [data owner] (win-view data owner))

(defmethod top-view :lose
  [data owner] (lose-view data owner))

(om/root
  top-view
  app-state
  {:target (. js/document (getElementById "app"))})

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
