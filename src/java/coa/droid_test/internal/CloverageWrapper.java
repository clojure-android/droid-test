package coa.droid_test.internal;

import clojure.lang.IFn;
import clojure.lang.Symbol;
import clojure.java.api.Clojure;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import java.util.List;

@RunWith(TestRunner.class)
public class CloverageWrapper {

    @Test
    public void cloverageRunner() {
        try {
            List<Symbol> namespaces = Util.importNsDeclarations(TestRunner.getTestNamespaces());
            Util.tryInitNeko();
            ArrayList<String> cloverageArgs = new ArrayList<String>();

            for (Symbol ns : namespaces) {
                cloverageArgs.add("-x");
                cloverageArgs.add(ns.getName());
            }
            cloverageArgs.addAll(TestRunner.getSourceNamespaces());

            IFn require = Clojure.var("clojure.core", "require");
            require.invoke(Clojure.read("cloverage.coverage"));

            IFn coverage = Clojure.var("cloverage.coverage", "-main");
            IFn apply = Clojure.var("clojure.core", "apply");
            IFn concat = Clojure.var("clojure.core", "concat");
            apply.invoke(coverage, "--coveralls", cloverageArgs);

            IFn shutdownAgents = Clojure.var("clojure.core", "shutdown-agents");
            shutdownAgents.invoke();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
