package com.amdocs.connector.plugins.documentum.exception;

import com.documentum.fc.common.DfException;

public class DocumentumConnectionException extends RuntimeException {
    public DocumentumConnectionException(String message, DfException e) {
        super(message, e);
    }
}
