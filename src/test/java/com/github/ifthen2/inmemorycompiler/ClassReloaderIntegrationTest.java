package com.github.ifthen2.inmemorycompiler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.ifthen2.inmemorycompiler.guice.ClassReloaderModule;
import com.github.ifthen2.inmemorycompiler.io.DynamicClassLoader;
import com.github.ifthen2.inmemorycompiler.util.DynamicFunction;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Integration Tests for ClassReloader
 */
class ClassReloaderIntegrationTest {

    static final String TEST_CLASS_1_FQN = "com.github.ifthen2.inmemorycompiler.TestClass";

    ClassReloader classReloader;

    @BeforeEach
    void setUp() {
        Injector injector = Guice.createInjector(new ClassReloaderModule());
        classReloader = injector.getInstance(ClassReloader.class);
    }

    @Test
    @DisplayName("Test Simple Compile & Load Single Class")
    void testSimpleCompileAndLoad() throws InstantiationException, IllegalAccessException {

        DynamicFunction<String, String> dynamic = (DynamicFunction<String, String>) classReloader
            .compileAndLoadClass(TEST_CLASS_1_FQN, getTestSourceCode(), 1).get();

        assertEquals(TEST_CLASS_1_FQN, dynamic.getClass().getName());
        assertTrue(dynamic.getClass().getClassLoader() instanceof DynamicClassLoader);
        assertEquals(
            classReloader.getInstance(TEST_CLASS_1_FQN, 1).getClass().getClassLoader(),
            dynamic.getClass().getClassLoader());
        assertDoesNotThrow(
            () -> classReloader.getInstance(TEST_CLASS_1_FQN, 1).getClass().cast(dynamic));
    }

    @Test
    @DisplayName("Test Casting Classes Loaded By Different ClassLoaders")
    void testCastingClassesFromDifferentClassLoaders() {

        Supplier<DynamicFunction<?, ?>> dynamicClassSupplier = classReloader
            .compileAndLoadClass(TEST_CLASS_1_FQN, getTestSourceCode(), 1);

        DynamicFunction<String, String> dynamicFirst = (DynamicFunction<String, String>) dynamicClassSupplier
            .get();

        dynamicClassSupplier = classReloader
            .compileAndLoadClass(TEST_CLASS_1_FQN, getTestSourceCode(), 2);

        DynamicFunction<String, String> dynamicSecond = (DynamicFunction<String, String>) dynamicClassSupplier
            .get();

        assertThrows(ClassCastException.class,
            () -> dynamicFirst.getClass().cast(dynamicSecond));
    }

    @Test
    @DisplayName("Test Adding Classes Loaded By Different ClassLoaders To Generic Collections")
    void testAddingClassesFromDifferentClassLoadersToCollection() {

        Supplier<DynamicFunction<?, ?>> dynamicClassSupplier = classReloader
            .compileAndLoadClass(TEST_CLASS_1_FQN, getTestSourceCode(), 1);

        DynamicFunction<String, String> dynamicFunctionFirst = (DynamicFunction<String, String>) dynamicClassSupplier
            .get();

        dynamicClassSupplier = classReloader
            .compileAndLoadClass(TEST_CLASS_1_FQN, getTestSourceCode(), 2);

        DynamicFunction<String, String> dynamicFunctionSecond = (DynamicFunction<String, String>) dynamicClassSupplier
            .get();

        List<DynamicFunction<String, String>> dynamicFunctionList = new ArrayList<>();

        assertDoesNotThrow(() -> dynamicFunctionList.add(dynamicFunctionFirst));
        assertDoesNotThrow(() -> dynamicFunctionList.add(dynamicFunctionSecond));
    }

    //TODO CC More Integration Tests

    String getTestSourceCode() {
        return new StringBuilder()
            .append("package com.github.ifthen2.inmemorycompiler;\n")
            .append("\n")
            .append("import com.github.ifthen2.inmemorycompiler.util.*;\n")
            .append("\n")
            .append("public class TestClass implements DynamicFunction<String, String> {\n")
            .append("\n")
            .append("    public String apply(String s) {\n")
            .append("        System.out.println(s + \"more\");\n")
            .append("        return s + \"more\";\n")
            .append("    }\n")
            .append("}\n").toString();
    }
}
