package com.github.ifthen2.inmemorycompiler.model;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import javax.tools.SimpleJavaFileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a Java source code file
 */
public class JavaSourceFromString extends SimpleJavaFileObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaSourceFromString.class);

    private final String sourceCode;

    /**
     * Constructs a new JavaSourceFromString.
     *
     * @param name the name of the compilation unit represented by this file object
     * @param sourceCode the source code for the compilation unit represented by this file object
     */
    public JavaSourceFromString(String name, String sourceCode) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
            Kind.SOURCE);
        this.sourceCode = requireNonNull(sourceCode, "sourceCode must not be null");
    }

    /**
     * Retrieve the source code to be compiled
     */
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return sourceCode;
    }
}
