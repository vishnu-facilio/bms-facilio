package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class FieldUsageCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long fieldId = (long) context.getOrDefault(FacilioConstants.ContextNames.FIELD_ID, -1l);
        long categoryId = (long) context.getOrDefault(FacilioConstants.ContextNames.CATEGORY_ID, -1l);

        if (fieldId > 0 && categoryId > 0 ) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//            Criteria criteria = new Criteria();
//            criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_CATEGORY_ID", "assetCategoryId", categoryId+"", NumberOperators.EQUALS));
//            List<ReadingRuleContext> rules =  ReadingRuleAPI.getReadingRules(criteria);
//            Map<Long, String> ruleMap = new ArrayMap<>();
//            if (!rules.isEmpty()) {
//                for (ReadingRuleContext rule : rules) {
//                    if (rule.getReadingFieldId() == fieldId) {
//                        ruleMap.put(rule.getParentRuleId(), rule.getName());
//                    }
//                }
//                context.put(FacilioConstants.ContextNames.RULES, ruleMap);
//            }
            List<FacilioField> sourcefields = FieldFactory.getReadingDataMetaFields();
            Map<String, FacilioField> sourcefieldMap = FieldFactory.getAsMap(FieldFactory.getReadingDataMetaFields());


            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .select(sourcefields)
                    .table(ModuleFactory.getReadingDataMetaModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition(sourcefieldMap.get("value"), "-1", StringOperators.ISN_T))
                    .andCondition(CriteriaAPI.getCondition(sourcefieldMap.get("fieldId"), fieldId+"", NumberOperators.EQUALS))
                    .limit(100);

            List<Map<String, Object>> readingsList = builder.get();
            if ( readingsList != null  && readingsList.size() > 0) {
//                System.out.println("readingsList" + readingsList.size());
                context.put(FacilioConstants.ContextNames.RESOURCE_LIST, readingsList);
                context.put(FacilioConstants.ContextNames.READINGS, "Has Readings");

            }
        }

        return false;
    }
}
