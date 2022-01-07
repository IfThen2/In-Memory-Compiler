package com.github.ifthen2.inmemorycompiler;

import com.github.ifthen2.inmemorycompiler.util.DynamicFunction;
import java.util.function.Supplier;

public interface ClassReloader {

    Supplier<DynamicFunction<?, ?>> compileAndLoadClass(String className,
        String sourceCode,
        int id);

    DynamicFunction<?, ?> getInstance(String className, int id)
        throws InstantiationException, IllegalAccessException;

    Supplier<DynamicFunction<?, ?>> getInstanceFactory(String className, int id);
}
