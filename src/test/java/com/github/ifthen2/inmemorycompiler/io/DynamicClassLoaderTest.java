package com.github.ifthen2.inmemorycompiler.io;

import com.github.ifthen2.inmemorycompiler.model.JavaClassAsBytes;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DynamicClassLoaderTest {

    @Mock
    DynamicClassFileManager fileManager;

    @Mock
    JavaClassAsBytes classAsBytes;

    @Mock
    Map<String, JavaClassAsBytes> bytesMap;

    @InjectMocks
    DynamicClassLoader subject;

    @BeforeEach
    void setUp() {
        // TODO implement
    }

    @Test
    @DisplayName("N/A")
    @Disabled("Not Yet Implemented")
    void test() {
        // TODO implement
    }


}
