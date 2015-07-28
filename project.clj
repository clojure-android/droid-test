(defproject org.clojure-android/droid-test "0.1.1-SNAPSHOT"
  :description "Robolectric wrapper for testing Clojure-Android projects"
  :url "http://github.com/clojure-android/droid-test"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.google.android/android "4.1.1.4"]
                 [org.robolectric/robolectric "3.0"]
                 [junit/junit "4.12"]
                 [commons-cli/commons-cli "1.3"]]
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"])
