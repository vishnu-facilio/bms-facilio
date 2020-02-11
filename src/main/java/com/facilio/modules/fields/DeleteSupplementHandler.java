package com.facilio.modules.fields;

import java.util.Collection;

public interface DeleteSupplementHandler {
    public void deleteSupplements(Collection<Long> ids) throws IllegalAccessException, InstantiationException, Exception;
}
