package com.facilio.v3.commands;

import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.operators.NumberOperators;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.modules.fields.FacilioField;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.V3Builder.V3Config;
import org.apache.commons.chain.Context;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import com.facilio.beans.ModuleBean;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j;
import com.facilio.modules.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Log4j
public class HandleCurrencyFields extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        String moduleName = Constants.getModuleName(context);
        FacilioModule module = moduleBean.getModule(moduleName);

        try {
            Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
            if (MapUtils.isEmpty(recordMap)) {
                return false;
            }

            List<ModuleBaseWithCustomFields> summaryRecords = recordMap.get(moduleName);
            if (CollectionUtils.isEmpty(summaryRecords)) {
                return false;
            }

            List<Long> fieldIds = getCurrencyFieldsIds(module);
            if (CollectionUtils.isEmpty(fieldIds)) {
                return false;
            }


            String currencyRecordsModuleName = FacilioConstants.SystemLookup.CURRENCY_RECORD;
            List<FacilioField> selectFields = moduleBean.getAllFields(currencyRecordsModuleName);
            FacilioModule currencyRecordsModule = moduleBean.getModule(currencyRecordsModuleName);


            V3Config config = ChainUtil.getV3Config(moduleName);
            Class beanClass = ChainUtil.getBeanClass(config, currencyRecordsModule);

            for (ModuleBaseWithCustomFields summaryRecord : summaryRecords) {
                long recordId = summaryRecord.getId();

                SelectRecordsBuilder relBuilder = new SelectRecordsBuilder<>()
                        .beanClass(beanClass)
                        .select(selectFields)
                        .module(currencyRecordsModule)
                        .andCondition(CriteriaAPI.getCondition("FIELD_ID", "fieldId", StringUtils.join(fieldIds, ","), NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(recordId), NumberOperators.EQUALS));

                ModuleBaseWithCustomFields moduleBaseWithCustomFields = relBuilder.fetchFirst();

                if (moduleBaseWithCustomFields != null) {
                    summaryRecord.getData().put(FacilioConstants.ContextNames.HAS_CURRENCY_FIELD_VALUE, true);
                }
            }
        } catch (Exception e) {
            LOGGER.info("##Currency Field - Exception occurred ", e);
        }

        return false;
    }

    private static List<Long> getCurrencyFieldsIds(FacilioModule module) throws Exception {
        List<Long> extendedModuleIds = module.getExtendedModuleIds();
        FacilioField fieldIdField = FieldFactory.getNumberField("fieldId", "FIELDID", ModuleFactory.getFieldsModule());

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table("Fields")
                .select(Collections.singletonList(fieldIdField))
                .andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", StringUtils.join(extendedModuleIds, ","), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("DATA_TYPE", "dataType", String.valueOf(FieldType.CURRENCY_FIELD.getTypeAsInt()), NumberOperators.EQUALS));

        List<Map<String, Object>> propsList = selectBuilder.get();

        if (CollectionUtils.isNotEmpty(propsList)) {
            List<Long> fieldIds = propsList.stream().map(prop -> (Long) prop.get("fieldId")).collect(Collectors.toList());
            return fieldIds;
        }

        return null;
    }
}
