package com.github.ifthen2.inmemorycompiler.guice;

import com.github.ifthen2.inmemorycompiler.ClassReloader;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for ClassReloaderModule
 */
class ClassReloaderModuleTest {

    @Test
    @DisplayName("Testing Guice Bindings")
    void testGuiceBindings() {
        Injector injector = Guice.createInjector(new ClassReloaderModule());
        Assertions.assertDoesNotThrow(() -> injector.getInstance(ClassReloader.class));
    }
}
