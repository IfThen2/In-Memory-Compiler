package com.github.ifthen2.inmemorycompiler;

import static java.util.Objects.requireNonNull;

import com.github.ifthen2.inmemorycompiler.io.DynamicLocation;
import com.github.ifthen2.inmemorycompiler.model.JavaSourceFromString;
import com.github.ifthen2.inmemorycompiler.util.DynamicFunction;
import com.github.ifthen2.inmemorycompiler.util.ThrowingSupplier;
import com.google.common.base.Preconditions;
import com.google.inject.name.Named;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facilitates the dynamic compilation and loading/reloading of classes. When a class is compiled it
 * is stored along with an ID. Then, an instance of any version of any of the compiled classes can
 * be constructed at any time.
 */
public class ClassReloaderImpl implements ClassReloader {

    public static final String DYNAMIC_CDI_TAG = "DYNAMIC";

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassReloaderImpl.class);

    /**
     * K -> ClassName V -> (K -> ID & V -> Class)
     */
    private final Hashtable<String, Hashtable<Integer, Class<DynamicFunction<?, ?>>>> classMap; // TODO too dirty for the top level. abstract this away to some "ClassStorage"
    private final JavaCompiler compiler;
    private final Supplier<JavaFileManager> fileManagerSupplier;

    @Inject
    public ClassReloaderImpl(@Named(DYNAMIC_CDI_TAG) JavaCompiler compiler,
        Supplier<JavaFileManager> fileManagerSupplier) {

        this.compiler = requireNonNull(compiler, "compiler must not be null");
        this.fileManagerSupplier = requireNonNull(fileManagerSupplier,
            "fileManagerSupplier must not be null");
        this.classMap = new Hashtable<>();
    }

    /**
     * @param className fully qualified class name to be compiled
     * @param sourceCode code to be compiled
     * @param id id to archive the compiled class with
     */
    @Override
    public Supplier<DynamicFunction<?, ?>> compileAndLoadClass(String className,
        String sourceCode, int id) {

        /** Preconditions */

        requireNonNull(className, "className must not be null");
        Preconditions.checkState(!className.isEmpty(), "className must not be empty. provided: {}",
            className);
        requireNonNull(sourceCode, "sourceCode must not be null");
        Preconditions
            .checkState(!sourceCode.isEmpty(), "sourceCode must not be empty. provided: {}",
                className);
        Preconditions.checkArgument(id > 0, "id must be > 0. provided: {}", id);

        /** Body */

        Hashtable<Integer, Class<DynamicFunction<?, ?>>> versionMap = classMap.get(className);

        if (versionMap != null && versionMap.containsKey(id)) {
            throw new RuntimeException("Class+Version already in use");
        }

        JavaFileManager fileManager = fileManagerSupplier.get();

        if (!compileClass(className, sourceCode, fileManager)) {
            throw new RuntimeException("Compilation Failed");
        }

        Class<DynamicFunction<?, ?>> dynamicFunctionClass = loadClass(className, id,
            fileManager); // TODO handle case

        return getInstanceFactory(className, id);
    }

    private boolean compileClass(String className, String sourceCode,
        JavaFileManager fileManager) {

        /** Body */

        List<JavaFileObject> sourceFiles = new ArrayList<>();
        sourceFiles.add(new JavaSourceFromString(className, sourceCode));

        CompilationTask task = compiler.getTask(
            null,
            fileManager,
            null, //TODO CC implement listener
            null,
            null,
            sourceFiles);

        LOGGER.info("Now Compiling Class: {}", className);

        return task.call();
    }

    private Class<DynamicFunction<?, ?>> loadClass(String className, int id,
        JavaFileManager fileManager) {

        /** Body */

        LOGGER.debug("Now Loading Class: {} ID={}", className, id);

        Class<?> clazz = null;

        try {
            clazz = fileManager
                .getClassLoader(DynamicLocation.DYNAMIC_INPUT)
                .loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("error, class not found");
        }

        Class<DynamicFunction<?, ?>> dynamicFunctionClass = null;

        try {
            dynamicFunctionClass = (Class<DynamicFunction<?, ?>>) clazz;
            classMap.putIfAbsent(className, new Hashtable<>());
            classMap.get(className).put(id, dynamicFunctionClass);

        } catch (ClassCastException e) {
            LOGGER.error("Source Code does not implement DynamicFunction<T,R>", e);
        }

        return dynamicFunctionClass;
    }

    /**
     * Retrieve an instance of a {@Code DynamicClass} for a particular id
     *
     * @param className class name to lookup
     * @param id version id to build object from
     * @return instance of {@Link DynamicClass} or null if not found
     */
    @Override
    public DynamicFunction<?, ?> getInstance(String className, int id)
        throws InstantiationException, IllegalAccessException {

        /** Preconditions */

        Preconditions.checkState(!StringUtils.isEmpty(className),
            "className must not be empty. provided: {}",
            className);
        Preconditions.checkArgument(id > 0, "id must be > 0. provided: {}", id);

        /** Body */

        Class<DynamicFunction<?, ?>> clazz = classMap.get(className).get(id);

        return clazz.newInstance();
    }

    /**
     * Retrieve an {@Code Supplier} for instances of a {@Code DynamicClass} for a particular id
     *
     * @param className class name to lookup
     * @param id version id to build object from
     * @return Supplier {@Link DynamicClass} or null if not found
     */
    @Override
    public Supplier<DynamicFunction<?, ?>> getInstanceFactory(String className, int id) {

        /** Preconditions */

        Preconditions.checkState(!StringUtils.isEmpty(className),
            "className must not be empty. provided: {}",
            className);
        Preconditions.checkArgument(id > 0, "id must be > 0. provided: {}", id);

        /** Body */

        Class<DynamicFunction<?, ?>> clazz = classMap.get(className)
            .get(id);

        return clazz == null ? null : ThrowingSupplier.disguiseAsSupplier(clazz::newInstance);
    }
}
