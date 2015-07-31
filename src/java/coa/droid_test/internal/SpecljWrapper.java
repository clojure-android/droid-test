package coa.droid_test.internal;

import clojure.lang.IFn;
import clojure.lang.Symbol;
import clojure.java.api.Clojure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import java.util.List;

@RunWith(TestRunner.class)
public class SpecljWrapper {

    @Test
    public void specljRunner() {
        try {
            List<Symbol> namespaces = Util.importNsDeclarations(TestRunner.getTestNamespaces());
            Util.tryInitNeko();

            IFn require = Clojure.var("clojure.core", "require");

            require.invoke(Clojure.read("speclj.cli"));
            require.invoke(Clojure.read("speclj.run.standard"));

            IFn runSpecs = Clojure.var("speclj.run.standard", "run-specs");
            runSpecs.invoke();

            IFn shutdownAgents = Clojure.var("clojure.core", "shutdown-agents");
            shutdownAgents.invoke();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
