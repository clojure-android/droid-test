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
public class SpecljWrapper {

    @Test
    public void specljRunner() {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            IFn require = Clojure.var("clojure.core", "require");

            require.invoke(Clojure.read("coa.droid-test.internal.util"));
            IFn getImports = Clojure.var("coa.droid-test.internal.util",
                                         "extract-imports");

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

            require.invoke(Clojure.read("speclj.cli"));
            require.invoke(Clojure.read("speclj.run.standard"));

            IFn runSpecs = Clojure.var("speclj.run.standard", "run-specs");
            // IFn apply = Clojure.var("clojure.core", "apply");
            runSpecs.invoke();

            IFn shutdownAgents = Clojure.var("clojure.core", "shutdown-agents");
            shutdownAgents.invoke();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
