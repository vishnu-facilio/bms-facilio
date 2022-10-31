package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class GetCustomModulesCountCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Integer moduleType = (Integer) context.get(FacilioConstants.ContextNames.MODULE_TYPE);
        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);

        if (moduleType == null || moduleType <= 0) {
            moduleType = FacilioModule.ModuleType.BASE_ENTITY.getValue();
        }

        FacilioModule moduleModule = ModuleFactory.getModuleModule();
        List<FacilioField> moduleFields = FieldFactory.getModuleFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(moduleFields);

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getCountField())
                .table(moduleModule.getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("type"), String.valueOf(moduleType), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(1), NumberOperators.NOT_EQUALS))
                .andCondition(CriteriaAPI.getCondition("IS_CUSTOM", "custom", String.valueOf(true), BooleanOperators.IS));

        if (StringUtils.isNotEmpty(searchString)) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("name"), searchString, StringOperators.CONTAINS));
        }

        Map<String, Object> modulesMap = selectRecordBuilder.fetchFirst();
        long count = MapUtils.isNotEmpty(modulesMap) ? (long) modulesMap.get("count") : 0;

        context.put(FacilioConstants.ContextNames.COUNT, count);
        return false;
    }
}
