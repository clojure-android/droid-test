package coa.droid_test.internal;

import clojure.lang.IFn;
import clojure.lang.Symbol;
import clojure.java.api.Clojure;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;
import org.robolectric.RuntimeEnvironment;

public class Util {

    private static IFn require = null;
    private static IFn getImports = null;

    private static void processNs(String ns, ClassLoader loader,
                                  List<Symbol> namespaceAccumulator) throws ClassNotFoundException {
        Symbol nsSym = Symbol.intern(null, ns);
        List<String> imports = (List<String>)getImports.invoke(loader, ns);
        for (String classname : imports) {
            Class.forName(classname, true, loader);
        }
        require.invoke(nsSym);
        namespaceAccumulator.add(nsSym);
    }

    public static ArrayList<Symbol> importNsDeclarations(List<String> namespaces)
        throws ClassNotFoundException{
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (require == null)
            require = Clojure.var("clojure.core", "require");

        require.invoke(Clojure.read("coa.droid-test.internal.util"));
        if (getImports == null)
            getImports = Clojure.var("coa.droid-test.internal.util",
                                     "extract-imports");

        ArrayList<Symbol> namespaceSymbols = new ArrayList<Symbol>();
        for (String ns : TestRunner.getTestNamespaces()) {
            processNs(ns, loader, namespaceSymbols);
        }
        return namespaceSymbols;
    }

    public static void tryInitNeko() {
        try {
            Class c = Class.forName("neko.App");
            Field instance = c.getField("instance");
            instance.set(null, RuntimeEnvironment.application);
        } catch (ClassNotFoundException e) {
            System.out.println("[WARN] Neko not found");
        } catch (Exception e) {
            System.out.println("[WARN] neko.App/instance field not found");
        }
    }
}
