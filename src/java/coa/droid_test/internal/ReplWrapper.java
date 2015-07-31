package coa.droid_test.internal;

import clojure.lang.IFn;
import clojure.java.api.Clojure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

@RunWith(TestRunner.class)
public class ReplWrapper {

    @Test
    public void nreplStarter() {
        try {
            Util.importNsDeclarations(TestRunner.getTestNamespaces());
            Util.tryInitNeko();

            IFn require = Clojure.var("clojure.core", "require");
            require.invoke(Clojure.read("clojure.tools.nrepl.server"));
            IFn startServer = Clojure.var("clojure.tools.nrepl.server",
                                          "start-server");
            startServer.invoke(Clojure.read(":port"), 8888);
            System.out.println("Started nREPL server on port " + 8888);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
