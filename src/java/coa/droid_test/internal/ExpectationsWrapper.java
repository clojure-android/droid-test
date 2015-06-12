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
public class ExpectationsWrapper {

    @Test
    public void expectationsRunner() {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            IFn require = Clojure.var("clojure.core", "require");

            require.invoke(Clojure.read("coa.droid-test.internal.util"));
            IFn getImports = Clojure.var("coa.droid-test.internal.util",
                                         "extract-imports");

            require.invoke(Clojure.read("expectations"));
            IFn disableRun = Clojure.var("expectations", "disable-run-on-shutdown");
            disableRun.invoke();

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



            IFn runTests = Clojure.var("expectations", "run-all-tests");
            // IFn apply = Clojure.var("clojure.core", "apply");
            Object result = runTests.invoke();

            IFn shutdownAgents = Clojure.var("clojure.core", "shutdown-agents");
            shutdownAgents.invoke();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
