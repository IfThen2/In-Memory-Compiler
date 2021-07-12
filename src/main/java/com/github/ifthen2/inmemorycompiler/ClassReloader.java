package com.github.ifthen2.inmemorycompiler;

import com.github.ifthen2.inmemorycompiler.util.DynamicClass;
import java.util.function.Supplier;

public interface ClassReloader {

    Supplier<DynamicClass> compileAndLoadSingleClass(String className, String sourceCode, int id);

    DynamicClass getInstance(String className, int id)
        throws InstantiationException, IllegalAccessException;

    Supplier<DynamicClass> getInstanceFactory(String className, int id);
}
