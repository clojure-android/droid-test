package coa.droid_test.internal;

import clojure.lang.IFn;
import clojure.lang.Symbol;
import clojure.java.api.Clojure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import java.util.List;

@RunWith(TestRunner.class)
public class ExpectationsWrapper {

    @Test
    public void expectationsRunner() {
        try {
            IFn require = Clojure.var("clojure.core", "require");
            require.invoke(Clojure.read("expectations"));
            IFn disableRun = Clojure.var("expectations", "disable-run-on-shutdown");
            disableRun.invoke();

            List<Symbol> namespaces = Util.importNsDeclarations(TestRunner.getTestNamespaces());
            Util.tryInitNeko();

            IFn runTests = Clojure.var("expectations", "run-all-tests");
            Object result = runTests.invoke();

            IFn shutdownAgents = Clojure.var("clojure.core", "shutdown-agents");
            shutdownAgents.invoke();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
