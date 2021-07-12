package com.github.ifthen2.inmemorycompiler.io;

import static com.github.ifthen2.inmemorycompiler.ClassReloaderImpl.DYNAMIC_CDI_TAG;

import com.github.ifthen2.inmemorycompiler.model.JavaClassAsBytes;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.Hashtable;
import java.util.Map;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File Manager for use with DynamicClassLoader.
 */
public class DynamicClassFileManager extends ForwardingJavaFileManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicClassFileManager.class);

    private final Map<String, JavaClassAsBytes> bytesMap;

    /**
     * Will initialize the manager with the specified standard java file manager
     */
    @Inject
    public DynamicClassFileManager(
        @Named(DYNAMIC_CDI_TAG) StandardJavaFileManager standardManager) {
        super(standardManager);
        this.bytesMap = new Hashtable<>();
    }

    /**
     * Will be used by us to get the class loader for our compiled class. It creates an anonymous
     * class extending the SecureClassLoader which uses the byte code created by the compiler and
     * stored in the JavaClassObject, and returns the Class for it
     *
     * @param location where to place or search for file objects.
     */
    @Override
    public ClassLoader getClassLoader(Location location) {
        return new DynamicClassLoader(this.getClass().getClassLoader(), this);
    }

    /**
     * Gives the compiler an instance of the JavaClassObject so that the compiler can write the byte
     * code into it.
     *
     * @param location where to place or search for file objects.
     * @param className name of output class file
     * @param kind the kind of file, must be one of SOURCE or CLASS
     * @param sibling a file object to be used as hint for placement; might be null
     */
    @Override
    public JavaFileObject getJavaFileForOutput(Location location,
        String className, Kind kind, FileObject sibling) {

        JavaClassAsBytes classAsBytes = new JavaClassAsBytes(className, kind);
        bytesMap.put(className, classAsBytes);

        return classAsBytes;
    }

    /**
     * Retrives map of compiled byte code
     */
    public Map<String, JavaClassAsBytes> getBytesMap() {
        return bytesMap;
    }
}
