package coa.droid_test.internal;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.runner.JUnitCore;
import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.internal.bytecode.InstrumentationConfiguration;
import org.apache.commons.cli.*;
import org.apache.commons.cli.DefaultParser;
import java.lang.reflect.*;

public class TestRunner extends RobolectricTestRunner {

    static String[] notAcquiredPackages;
    static ArrayList<String> namespacesToTest;
    static ArrayList<String> sourceNamespaces;

    public synchronized static ArrayList<String> getTestNamespaces() {
        ClassLoader myClassLoader = TestRunner.class.getClassLoader();
        if (namespacesToTest == null) {
            if (! myClassLoader.toString().startsWith("sun.")) {
                try {
                    ClassLoader parentClassLoader = TestRunner.class.getClassLoader().getParent();
                    Class otherClassInstance = parentClassLoader.loadClass(TestRunner.class.getName());
                    Method getInstanceMethod = otherClassInstance.getDeclaredMethod("getTestNamespaces", new Class[] { });
                    Object otherAbsoluteSingleton = getInstanceMethod.invoke(null, new Object[] { } );
                    namespacesToTest = (ArrayList<String>) otherAbsoluteSingleton;// Proxy.newProxyInstance(myClassLoader,
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                throw new RuntimeException("Shouldn't be called with default Sun classloader");
            }
        }
        return namespacesToTest;
    }

    public synchronized static ArrayList<String> getSourceNamespaces() {
        ClassLoader myClassLoader = TestRunner.class.getClassLoader();
        if (sourceNamespaces == null) {
            if (! myClassLoader.toString().startsWith("sun.")) {
                try {
                    ClassLoader parentClassLoader = TestRunner.class.getClassLoader().getParent();
                    Class otherClassInstance = parentClassLoader.loadClass(TestRunner.class.getName());
                    Method getInstanceMethod = otherClassInstance.getDeclaredMethod("getSourceNamespaces", new Class[] { });
                    Object otherAbsoluteSingleton = getInstanceMethod.invoke(null, new Object[] { } );
                    sourceNamespaces = (ArrayList<String>) otherAbsoluteSingleton;// Proxy.newProxyInstance(myClassLoader,
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                throw new RuntimeException("Shouldn't be called with default Sun classloader");
            }
        }
        return sourceNamespaces;
    }

    public TestRunner(final Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    public InstrumentationConfiguration createClassLoaderConfig() {
        InstrumentationConfiguration.Builder builder =
            InstrumentationConfiguration.newBuilder();

        if (notAcquiredPackages != null)
            for (String pkg : notAcquiredPackages) {
                try {
                    builder = builder.doNotAquirePackage(pkg);
                } catch (Throwable t) {
                    t.printStackTrace();
                    System.exit(-1);
                }
            }

        return builder.build();
    }

    public static void parseNamespaces(String[] freeArgs) {
        if (freeArgs.length == 0)
            return;
        ArrayList<String> sources = new ArrayList<String>();
        ArrayList<String> tests = new ArrayList<String>();
        if (freeArgs.length > 0) {
            int i = 0;
            if (":src".equals(freeArgs[i])) {
                i++;
                while (i < freeArgs.length) {
                    String ns = freeArgs[i];
                    if (":test".equals(ns))
                        break;
                    sources.add(ns);
                    i++;
                }
            }
            if (":test".equals(freeArgs[i])) {
                i++;
                while (i < freeArgs.length) {
                    tests.add(freeArgs[i]);
                    i++;
                }
            }
        }

        namespacesToTest = tests;
        if (namespacesToTest == null || namespacesToTest.size() == 0) {
            System.err.println("No test namespaces were provided.");
            System.exit(-1);
        }

        sourceNamespaces = sources;
    }

    public static void main (String[] args) {
        Option notAcquirePkgs = OptionBuilder.hasArg().create("ignore");
        Option modeOption = OptionBuilder.hasArg().create("mode");
        Options options = new Options();
        options.addOption(notAcquirePkgs);
        options.addOption(modeOption);
        CommandLineParser parser = new DefaultParser();
        String mode = null;
        try {
            CommandLine line = parser.parse(options, args);

            String ignore = line.getOptionValue("ignore");
            if (ignore != null) {
                notAcquiredPackages = ignore.split(";");
            }

            mode = line.getOptionValue("mode");
            if (mode == null) {
                mode = "clojuretest";
            }

            // if (!mode.equals("repl"))
            parseNamespaces(line.getArgs());

            if ("clojuretest".equals(mode)) {
                JUnitCore.runClasses(new Class[]{ ClojureTestWrapper.class });
            } else if ("cloverage".equals(mode)) {
                JUnitCore.runClasses(new Class[]{ CloverageWrapper.class });
            } else if ("speclj".equals(mode)){
                JUnitCore.runClasses(new Class[]{ SpecljWrapper.class });
            } else if ("expectations".equals(mode)){
                JUnitCore.runClasses(new Class[]{ ExpectationsWrapper.class });
            } else if ("repl".equals(mode)){
                JUnitCore.runClasses(new Class[]{ ReplWrapper.class });
            } else {
                System.err.println("Unrecognized mode: " + mode);
            }
        } catch (ParseException e) {
            System.err.println("Malformed command line: " + e.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        finally {
            // Forcequit to avoid hanging on threads
            if (mode == null || !("repl".equals(mode)))
                System.exit(0);
        }

    }

}
