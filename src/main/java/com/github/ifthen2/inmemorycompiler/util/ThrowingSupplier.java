package com.github.ifthen2.inmemorycompiler.util;

import java.util.function.Supplier;

/**
 * Modification of Supplier that can throw an Exception
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Exception> {

    T get() throws E;

    /**
     * Wraps a ThrowingSupplier into a Supplier
     */
    static <T> Supplier<T> disguiseAsSupplier(
        ThrowingSupplier<T, Exception> throwingSupplier) {
        return () -> {
            try {
                return throwingSupplier.get();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }
}
