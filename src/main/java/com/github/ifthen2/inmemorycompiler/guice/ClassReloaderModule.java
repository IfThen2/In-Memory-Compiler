package com.github.ifthen2.inmemorycompiler.guice;

import static com.github.ifthen2.inmemorycompiler.ClassReloaderImpl.DYNAMIC_CDI_TAG;

import com.github.ifthen2.inmemorycompiler.ClassReloader;
import com.github.ifthen2.inmemorycompiler.ClassReloaderImpl;
import com.github.ifthen2.inmemorycompiler.io.DynamicClassFileManager;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.name.Named;
import java.util.function.Supplier;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Guice Setup Required Bindings and Providers
 */
public class ClassReloaderModule extends AbstractModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassReloaderModule.class);

    /**
     * Setup required GUICE bindings
     */
    @Override
    protected void configure() {
        bind(ClassReloader.class).to(ClassReloaderImpl.class).in(Scopes.SINGLETON);
    }

    /**
     * Provides the standard Java file manager for a particular Java compiler
     *
     * @param javaCompiler compiler to obtain manager from
     */
    @Provides
    @Named(DYNAMIC_CDI_TAG)
    public StandardJavaFileManager standardJavaFileManager(
        @Named(DYNAMIC_CDI_TAG) JavaCompiler javaCompiler) {
        return javaCompiler
            .getStandardFileManager(null, null, null);
    }

    /**
     * Provides a file manager Supplier
     *
     * @param javaCompiler compiler to obtain manager from
     */
    @Provides
    public Supplier<JavaFileManager> javaFileManagerSupplier(
        @Named(DYNAMIC_CDI_TAG) JavaCompiler javaCompiler) {
        return () -> new DynamicClassFileManager(
            javaCompiler.getStandardFileManager(null, null, null));
    }

    /**
     * Provides the default system Java compiler
     */
    @Provides
    @Named(DYNAMIC_CDI_TAG)
    public JavaCompiler javaCompiler() {
        return ToolProvider.getSystemJavaCompiler();
    }
}
