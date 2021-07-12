package com.github.ifthen2.inmemorycompiler.util;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A binding annotation for internal class reloader dynnamic properties.
 */
@Retention(RetentionPolicy.RUNTIME)
@BindingAnnotation
@interface Dynamic {

}
