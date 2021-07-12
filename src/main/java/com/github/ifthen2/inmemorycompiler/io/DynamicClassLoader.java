package com.github.ifthen2.inmemorycompiler.io;

import static java.util.Objects.requireNonNull;

import com.github.ifthen2.inmemorycompiler.model.JavaClassAsBytes;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Selectively reloads Dynamic classes while delegating non-dynamic classes to its' parent
 * ClassLoader
 */
public class DynamicClassLoader extends ClassLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicClassLoader.class);

    private final DynamicClassFileManager manager;

    /**
     * Constructs a new DynamicClassLoader.
     *
     * @param parent classloader to delegate non-dynamic classloading to
     * @param manager file manager holding compiled byte-code
     */
    public DynamicClassLoader(ClassLoader parent, DynamicClassFileManager manager) {
        super(parent);
        this.manager = requireNonNull(manager, "manager must not be null");
    }

    /**
     * Attempt to load a class. If the class is a Dynamic class, define it. If not, then delegate
     * the loading to the parent ClassLoader.
     *
     * @param className name of class to load
     * @return compiled {@Link Class} file
     */
    @Override
    public Class loadClass(String className) throws ClassNotFoundException {

        /** Preconditions */
        
        Preconditions.checkState(!StringUtils.isEmpty(className),
            "className must not be empty. provided: {}",
            className);

        /** Body */

        JavaClassAsBytes classAsBytes = manager.getBytesMap().get(className);

        if (classAsBytes == null) {
            return super.loadClass(className);
        } else {
            Class<?> clazz = defineClass(className,
                classAsBytes.getBytes(), 0,
                classAsBytes.getBytes().length);

            return clazz;
        }
    }
}
