package com.bervan.common.controller;

import java.util.List;

/**
 * Result of an import operation: number imported, number skipped and error messages.
 */
public class ImportResult {
    private final int imported;
    private final int skipped;
    private final List<String> errors;

    public ImportResult(int imported, int skipped, List<String> errors) {
        this.imported = imported;
        this.skipped = skipped;
        this.errors = errors;
    }

    public int getImported() {
        return imported;
    }

    public int getSkipped() {
        return skipped;
    }

    public List<String> getErrors() {
        return errors;
    }
}

