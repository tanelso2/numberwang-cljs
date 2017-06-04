(ns numberwang.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as string]))

(enable-console-print!)

(println "This text is printed from src/numberwang/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:view :entry}))

(defmulti result-view (fn [data owner] (:result data)))

(defmethod result-view :none
  [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil ""))))

(defmethod result-view :win
  [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil
        (dom/h1 nil "That's numberwang!")))))

(defmethod result-view :lose
  [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil
        (dom/h1 nil "No")))))

(defn handle-change [e owner {:keys [text]}]
  (let [value (.. e -target -value)]
    (om/set-state! owner :text value)))

(defn check-winner [data owner]
  (if (<= 90 (rand-int 100))
    (om/set-state! owner :result :win)
    (om/set-state! owner :result :lose))
  (om/set-state! owner :text ""))

(defn entry-view [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:text ""
       :result :none})
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
                      "Submit")
          (om/build result-view state))))))

(defmulti top-view (fn [data _] (:view data)))

(defmethod top-view :entry
  [data owner] (entry-view data owner))

(om/root
  entry-view
  app-state
  {:target (. js/document (getElementById "app"))})

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
