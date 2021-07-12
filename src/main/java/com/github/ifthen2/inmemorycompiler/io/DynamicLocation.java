package com.github.ifthen2.inmemorycompiler.io;

import javax.tools.JavaFileManager;

public enum DynamicLocation implements JavaFileManager.Location {
    /**
     * Only Location is in memory
     */
    DYNAMIC_INPUT,
    ;

    @Override
    public String getName() {
        return name();
    }

    @Override
    public boolean isOutputLocation() {
        return false;
    }
}
