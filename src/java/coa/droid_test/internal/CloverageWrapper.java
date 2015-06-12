package coa.droid_test.internal;

import clojure.lang.IFn;
import clojure.lang.Symbol;
import clojure.java.api.Clojure;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import java.util.List;
import java.util.Arrays;

@RunWith(TestRunner.class)
public class CloverageWrapper {

    @Test
    public void cloverageRunner() {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            IFn require = Clojure.var("clojure.core", "require");

            require.invoke(Clojure.read("coa.droid-test.internal.util"));
            IFn getImports = Clojure.var("coa.droid-test.internal.util",
                                         "extract-imports");

            ArrayList<String> namespaces = new ArrayList<String>();
            for (String ns : TestRunner.getTestNamespaces()) {
                Symbol nsSym = Symbol.intern(null, ns);
                List<String> imports = (List<String>)getImports.invoke(loader, ns);
                for (String classname : imports) {
                    Class.forName(classname, true, loader);
                }
                require.invoke(nsSym);
                namespaces.add("-x");
                namespaces.add(ns);
            }

            List<String> sourceNamespaces = TestRunner.getSourceNamespaces();

            require.invoke(Clojure.read("cloverage.coverage"));

            IFn coverage = Clojure.var("cloverage.coverage", "-main");
            IFn apply = Clojure.var("clojure.core", "apply");
            IFn concat = Clojure.var("clojure.core", "concat");
            apply.invoke(coverage, "--coveralls", concat.invoke(namespaces, sourceNamespaces));

            IFn shutdownAgents = Clojure.var("clojure.core", "shutdown-agents");
            shutdownAgents.invoke();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
