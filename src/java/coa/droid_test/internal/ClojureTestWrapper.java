package coa.droid_test.internal;

import clojure.lang.IFn;
import clojure.lang.Symbol;
import clojure.java.api.Clojure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import java.util.List;

@RunWith(TestRunner.class)
public class ClojureTestWrapper {

    @Test
    public void clojureTestsRunner() {
        try {
            List<Symbol> namespaces = Util.importNsDeclarations(TestRunner.getTestNamespaces());
            Util.tryInitNeko();

            IFn require = Clojure.var("clojure.core", "require");
            require.invoke(Clojure.read("clojure.test"));

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
