package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddRelatedListTitleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioField> lookupFieldFields = FieldFactory.getLookupFieldFields();
        Map<String, FacilioField> lookupFieldFieldsAsMap = FieldFactory.getAsMap(lookupFieldFields);

        List<FacilioField> fieldFields = FieldFactory.getFieldFields();
        Map<String, FacilioField> fieldFieldsAsMap = FieldFactory.getAsMap(fieldFields);

        FacilioField relatedListDisplayNameField = lookupFieldFieldsAsMap.get("relatedListDisplayName");

        List<FacilioField> selectFields = new ArrayList<>();
        selectFields.add(relatedListDisplayNameField);
        selectFields.add(fieldFieldsAsMap.get("fieldId"));
        selectFields.add(fieldFieldsAsMap.get("moduleId"));

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(selectFields)
                .table(ModuleFactory.getLookupFieldsModule().getTableName())
                .innerJoin(ModuleFactory.getFieldsModule().getTableName())
                .on("LookupFields.FIELDID = Fields.FIELDID")
                .andCondition(CriteriaAPI.getCondition(relatedListDisplayNameField, CommonOperators.IS_EMPTY));

        List<Map<String, Object>> records = selectBuilder.get();

        if(CollectionUtils.isNotEmpty(records)) {
            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .fields(Collections.singletonList(relatedListDisplayNameField))
                    .table(ModuleFactory.getLookupFieldsModule().getTableName());

            List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();

            for (Map<String, Object> record : records) {
                if (record.containsKey("moduleId") && (Long) record.get("moduleId") > 0 && record.containsKey("fieldId") && (Long) record.get("fieldId") > 0) {
                    FacilioModule module = modBean.getModule((Long) record.get("moduleId"));
                    if(!FacilioUtil.isEmptyOrNull(module)) {
                        String relatedListDisplayName = module.getDisplayName();
                        if(StringUtils.isEmpty(relatedListDisplayName)){
                            relatedListDisplayName = module.getName();
                        }
                        GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
                        updateVal.addWhereValue("fieldId", record.get("fieldId"));
                        updateVal.addUpdateValue("relatedListDisplayName", relatedListDisplayName);
                        batchUpdateList.add(updateVal);
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(batchUpdateList)) {
                updateRecordBuilder.batchUpdate(Collections.singletonList(lookupFieldFieldsAsMap.get("fieldId")), batchUpdateList);
            }
        }
        return false;
    }
}
