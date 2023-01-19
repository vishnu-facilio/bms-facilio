package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetSystemModulesCountCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);

        List<String> systemModuleNames = new ArrayList<>();
        for(String moduleName : GetModulesListCommand.MODULES) {
            if (AccountUtil.isModuleLicenseEnabled(moduleName)) {
                systemModuleNames.add(moduleName);
            }
        }

        FacilioModule moduleModule = ModuleFactory.getModuleModule();
        List<FacilioField> moduleFields = FieldFactory.getModuleFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(moduleFields);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getCountField())
                .table(moduleModule.getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(1), NumberOperators.NOT_EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("name"), StringUtils.join(systemModuleNames, ","), StringOperators.IS));

        if (StringUtils.isNotEmpty(searchString)) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("displayName"), searchString, StringOperators.CONTAINS));
        }

        Map<String, Object> modulesMap = selectRecordBuilder.fetchFirst();
        long count = MapUtils.isNotEmpty(modulesMap) ? (long) modulesMap.get("count") : 0;

        context.put(FacilioConstants.ContextNames.COUNT, count);
        return false;
    }
}
