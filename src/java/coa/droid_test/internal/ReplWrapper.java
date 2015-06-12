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
public class ReplWrapper {

    @Test
    public void nreplStarter() {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            IFn require = Clojure.var("clojure.core", "require");

            require.invoke(Clojure.read("clojure.tools.nrepl.server"));
            IFn startServer = Clojure.var("clojure.tools.nrepl.server",
                                          "start-server");
            startServer.invoke();
            // IFn getImports = Clojure.var("coa.droid-test.internal.util",
            //                              "extract-imports");

            // require.invoke(Clojure.read("clojure.test"));

            // ArrayList<Symbol> namespaces = new ArrayList<Symbol>();
            // for (String ns : TestRunner.getTestNamespaces()) {
            //     Symbol nsSym = Symbol.intern(null, ns);
            //     List<String> imports = (List<String>)getImports.invoke(loader, ns);
            //     for (String classname : imports) {
            //         Class.forName(classname, true, loader);
            //     }
            //     require.invoke(nsSym);
            //     namespaces.add(nsSym);
            // }

            // IFn runTests = Clojure.var("clojure.test", "run-tests");
            // IFn apply = Clojure.var("clojure.core", "apply");
            // apply.invoke(runTests, namespaces);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
