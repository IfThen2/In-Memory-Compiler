package com.github.ifthen2.inmemorycompiler.io;

import com.github.ifthen2.inmemorycompiler.model.JavaClassAsBytes;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit Tests for DynamicClassFileManager
 */
@ExtendWith(MockitoExtension.class)
class DynamicClassFileManagerTest {

    static final String TEST_CLASS_NAME = "test.class";

    @Mock
    StandardJavaFileManager fileManager;

    @InjectMocks
    DynamicClassFileManager subject;

    @Test
    @DisplayName("Test Java Output File Creation")
    void test(@Mock FileObject sibling) {

        JavaFileObject outputFile = subject
            .getJavaFileForOutput(DynamicLocation.DYNAMIC_INPUT, TEST_CLASS_NAME, Kind.CLASS,
                sibling);

        Assertions.assertTrue(outputFile instanceof JavaClassAsBytes);
        Assertions
            .assertEquals("/".concat(TEST_CLASS_NAME.replace('.', '/').concat(".class")),
                outputFile.getName());
        Assertions.assertEquals(Kind.CLASS, outputFile.getKind());
    }
}
