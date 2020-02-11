package com.facilio.modules.fields;

import java.util.Collection;
import java.util.Map;

public interface UpdateSupplementHandler {
    public void updateSupplements(Map<String,Object> record, Collection<Long> ids) throws Exception;
}
