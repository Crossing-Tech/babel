package io.xtech.babel.camel.javaprocessors;

import io.xtech.babel.fish.JVMFunction;
import io.xtech.babel.fish.JVMPredicate;

/**
 * Created by babel on 8/24/15.
 */
public class JavaProcessors {
    public static final JVMFunction<String, String> append = new JVMFunction<String, String>() {
        public String apply(String input) {
            return input + "-";
        }
    };

    public static final JVMPredicate<String> containsTrue = new JVMPredicate<String>() {
        public boolean apply(String input) {
            return input.contains("true");
        }
    };
}
