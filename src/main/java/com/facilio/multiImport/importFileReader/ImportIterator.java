package com.facilio.multiImport.importFileReader;

import com.facilio.multiImport.multiImportExceptions.ImportParseException;

public interface ImportIterator<E> {
    boolean hasNext();

    E next() throws ImportParseException;

    default void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
