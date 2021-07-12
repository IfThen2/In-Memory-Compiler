package com.github.ifthen2.inmemorycompiler.util;

import com.github.ifthen2.inmemorycompiler.ClassReloader;
import com.github.ifthen2.inmemorycompiler.guice.ClassReloaderModule;
import com.github.ifthen2.inmemorycompiler.io.DynamicClassLoader;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
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

        DynamicClass dynamic = classReloader
            .compileAndLoadSingleClass(TEST_CLASS_1_FQN, getTestSourceCode(), 1).get();

        Assertions.assertEquals(TEST_CLASS_1_FQN, dynamic.getClass().getName());
        Assertions.assertTrue(dynamic.getClass().getClassLoader() instanceof DynamicClassLoader);
        Assertions.assertEquals(
            classReloader.getInstance(TEST_CLASS_1_FQN, 1).getClass().getClassLoader(),
            dynamic.getClass().getClassLoader());
        Assertions.assertDoesNotThrow(
            () -> classReloader.getInstance(TEST_CLASS_1_FQN, 1).getClass().cast(dynamic));
    }

    @Test
    @DisplayName("Test Casting Classes Loaded By Different ClassLoaders")
    void testCastingClassesFromDifferentClassLoaders() {

        Supplier<DynamicClass> dynamicClassSupplier = classReloader
            .compileAndLoadSingleClass(TEST_CLASS_1_FQN, getTestSourceCode(), 1);

        DynamicClass dynamicFirst = dynamicClassSupplier.get();

        dynamicClassSupplier = classReloader
            .compileAndLoadSingleClass(TEST_CLASS_1_FQN, getTestSourceCode(), 2);

        DynamicClass dynamicSecond = dynamicClassSupplier.get();

        Assertions.assertThrows(ClassCastException.class,
            () -> dynamicFirst.getClass().cast(dynamicSecond));
    }

    @Test
    @DisplayName("Test Adding Classes Loaded By Different ClassLoaders To Generic Collections")
    void testAddingClassesFromDifferentClassLoadersToCollection() {

        Supplier<DynamicClass> dynamicClassSupplier = classReloader
            .compileAndLoadSingleClass(TEST_CLASS_1_FQN, getTestSourceCode(), 1);

        DynamicClass dynamicClassFirst = dynamicClassSupplier.get();

        dynamicClassSupplier = classReloader
            .compileAndLoadSingleClass(TEST_CLASS_1_FQN, getTestSourceCode(), 2);

        DynamicClass dynamicClassSecond = dynamicClassSupplier.get();

        List<DynamicClass> dynamicClassList = new ArrayList<>();

        Assertions.assertDoesNotThrow(() -> dynamicClassList.add(dynamicClassFirst));
        Assertions.assertDoesNotThrow(() -> dynamicClassList.add(dynamicClassSecond));
    }

    //TODO CC More Integration Tests

    String getTestSourceCode() {
        return new StringBuilder()
            .append("package com.github.ifthen2.inmemorycompiler;\n")
            .append("\n")
            .append("import com.github.ifthen2.inmemorycompiler.util.*;\n")
            .append("\n")
            .append("public class TestClass implements DynamicClass {\n")
            .append("\n")
            .append("    public String toString() {\n")
            .append("        return this.getClass().getSimpleName();\n")
            .append("    }\n")
            .append("    public void execute() {\n")
            .append("        System.out.println(\"ClassLoader: \"")
            .append("           + this.getClass().getClassLoader());\n")
            .append("    }\n")
            .append("}\n").toString();
    }
}
