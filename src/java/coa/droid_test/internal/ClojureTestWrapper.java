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
public class ClojureTestWrapper {

    @Test
    public void clojureTestsRunner() {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            IFn require = Clojure.var("clojure.core", "require");

            require.invoke(Clojure.read("coa.droid-test.internal.util"));
            IFn getImports = Clojure.var("coa.droid-test.internal.util",
                                         "extract-imports");

            require.invoke(Clojure.read("clojure.test"));

            ArrayList<Symbol> namespaces = new ArrayList<Symbol>();
            for (String ns : TestRunner.getTestNamespaces()) {
                Symbol nsSym = Symbol.intern(null, ns);
                List<String> imports = (List<String>)getImports.invoke(loader, ns);
                for (String classname : imports) {
                    Class.forName(classname, true, loader);
                }
                require.invoke(nsSym);
                namespaces.add(nsSym);
            }

            try { // Load humane-test-output if present.
                require.invoke(Clojure.read("pjstadig.humane-test-output"));
                IFn activateHumane = Clojure.var("pjstadig.humane-test-output", "activate!");
                activateHumane.invoke();
            } catch (Exception ex) {}

            try { // Load ultra.test prettifier if present.
                require.invoke(Clojure.read("ultra.test"));
                IFn activateUltra = Clojure.var("ultra.test", "activate!");
                activateUltra.invoke();
            } catch (Exception ex) {}

            IFn runTests = Clojure.var("clojure.test", "run-tests");
            IFn apply = Clojure.var("clojure.core", "apply");
            apply.invoke(runTests, namespaces);

            IFn shutdownAgents = Clojure.var("clojure.core", "shutdown-agents");
            shutdownAgents.invoke();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
