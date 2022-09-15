package com.facilio.multiImport.util;

import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

import java.util.List;
import java.util.Map;

public class MultiImportApi {

    public static ImportDataDetails getImportData(Long id) throws Exception {
        if (id == null) {
            return null;
        }

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getImportDataDetailsFields())
                .table(ModuleFactory.getImportDataDetailsModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id,ModuleFactory.getImportDataDetailsModule()));

        List<Map<String, Object>> props = selectBuilder.get();
        if (props != null && !props.isEmpty()) {
            ImportDataDetails importDataDetails = FieldUtil.getAsBeanFromMap(props.get(0), ImportDataDetails.class);
            return importDataDetails;
        }
        return null;
    }
}
