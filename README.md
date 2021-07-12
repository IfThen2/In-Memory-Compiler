# In-Memory-Compiler

Simple Java In-Memory Compiler and ClassLoader

Allows reloading of the same class name multiple times.

## 1.0 Getting an Instance of `ClassReloader`

### 1.1 With Guice

```java
Injector injector = Guice.createInjector(new ClassReloaderModule());
ClassReloader classReloader = injector.getInstance(ClassReloader.class);
```

### 1.2 Without Guice

```java
JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    ClassReloader classReloader=new ClassReloaderImpl(
        compiler,
        ()->new DynamicClassFileManager(
            javaCompiler.getStandardFileManager(
                null,
                null,
                null)));
```

## 2.0 Compile and Load a Class

### 2.1 The Source Code

Your source code can come from anywhere. Currently, String is the accepted input type
for `ClassReloader`.

```java
    String getTestSourceCode() {
        return new StringBuilder()
            .append("package com.github.ifthen2.inmemorycompiler;\n")
            .append("\n")
            .append("import com.github.ifthen2.inmemorycompiler.util.*;\n")
            .append("\n")
            .append("public class TestClass implements DynamicClass {\n")
            .append("\n")
            .append("    public void execute() {\n")
            .append("        System.out.println(\"Hello World!\");\n")
            .append("    }\n")
            .append("}\n").toString();
    }
```

Please note that your classes must implement `DynamicClass` or they will fail to compile. The single
abstract method signature for `DynamicClass` is

```java
public void execute()
```

### 2.1 Compiling and Loading

When compiling a class, an ID must be provided. although a `Supplier<DynamicClass>` is returned by
the `compileAndLoadSingleClass` method, this ID can later be used to retrieve instances of any
version of any previously loaded `DynamicClass`, or in case the Supplier reference is lost or
discarded.

```java
Supplier<DynamicClass> dynamicClassSupplier = classReloader
    .compileAndLoadSingleClass(TEST_CLASS_1_FQN, getTestSourceCode(), 1);
```

## 3.0 Getting an Instance

Retrieving an instance simply requires the class name a valid associated ID# for that class.

### 3.1 From `Supplier`

```java
Supplier<DynamicClass> instanceFactory = classReloader.getInstanceFactory("my.class.name", 1);
DynamicClass instance = instanceFactory.get();
```

### 3.2 Single Instance

```java
DynamicClass instance = classReloader.getInstance("my.class.name", 1)
```

## 4.0 Executing the Code

```java
instance.execute();
```
