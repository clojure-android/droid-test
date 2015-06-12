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
    static String[] namespacesToTest;

    public synchronized static String[] getTestNamespaces() {
        ClassLoader myClassLoader = TestRunner.class.getClassLoader();
        if (namespacesToTest == null) {
            if (! myClassLoader.toString().startsWith("sun.")) {
                try {
                    ClassLoader parentClassLoader = TestRunner.class.getClassLoader().getParent();
                    Class otherClassInstance = parentClassLoader.loadClass(TestRunner.class.getName());
                    Method getInstanceMethod = otherClassInstance.getDeclaredMethod("getTestNamespaces", new Class[] { });
                    Object otherAbsoluteSingleton = getInstanceMethod.invoke(null, new Object[] { } );
                    namespacesToTest = (String[]) otherAbsoluteSingleton;// Proxy.newProxyInstance(myClassLoader,
                    // new Class[] { },
                    // new PassThroughProxyHandler(otherAbsoluteSingleton));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                throw new RuntimeException("What the hell?");
            }
        }
        return namespacesToTest;
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

    public static void main (String[] args) {
        Option notAcquirePkgs = OptionBuilder.hasArg().create("ignore");
        Option modeOption = OptionBuilder.hasArg().create("mode");
        Options options = new Options();
        options.addOption(notAcquirePkgs);
        options.addOption(modeOption);
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, args);
            namespacesToTest = line.getArgs();
            if (namespacesToTest == null) {
                System.err.println("No test namespaces were provided.");
                System.exit(-1);
            }
            String ignore = line.getOptionValue("ignore");
            if (ignore != null) {
                notAcquiredPackages = ignore.split(";");
            }

            String mode = line.getOptionValue("mode");;
            if (mode == null) {
                mode = "clojuretest";
            }

            if ("clojuretest".equals(mode)) {
                JUnitCore.runClasses(new Class[]{ ClojureTestWrapper.class });
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
            System.err.println("Something went wrong: " + e.getMessage());
        }
        finally {
            // Forcequit to avoid hanging on threads
            System.exit(0);
        }

    }

}
