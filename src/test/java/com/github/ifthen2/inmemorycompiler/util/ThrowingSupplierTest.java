package com.github.ifthen2.inmemorycompiler.util;

import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for ThrowingSupplier
 */
class ThrowingSupplierTest {

    ThrowingSupplier<Object, Exception> throwingSupplier;

    @BeforeEach
    void setUp() {
        throwingSupplier = () -> Integer.valueOf(null);
    }

    @Test
    @DisplayName("Testing Ability To Throw Exception")
    void testThrowingException() {
        Assertions.assertThrows(NumberFormatException.class, throwingSupplier::get);

    }

    @Test
    @DisplayName("Test Ability To Disguise As Supplier")
    void testWrappingWithSupplier() {
        Supplier<Object> supplier = ThrowingSupplier.disguiseAsSupplier(throwingSupplier);
        Assertions.assertThrows(RuntimeException.class, supplier::get);
    }
}
