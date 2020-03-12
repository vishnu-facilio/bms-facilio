package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.google.api.client.util.ArrayMap;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FieldUsageCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long fieldId = (long) context.getOrDefault(FacilioConstants.ContextNames.FIELD_ID, -1l);
        long categoryId = (long) context.getOrDefault(FacilioConstants.ContextNames.CATEGORY_ID, -1l);

        if (fieldId > 0 && categoryId > 0 ) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_CATEGORY_ID", "assetCategoryId", categoryId+"", NumberOperators.EQUALS));
            List<ReadingRuleContext> rules =  ReadingRuleAPI.getReadingRules(criteria);
            System.out.println("Rules" + rules.size());
            Map<Long, String> ruleMap = new ArrayMap<>();
            if (!rules.isEmpty()) {
                for (ReadingRuleContext rule : rules) {
                    if (rule.getReadingFieldId() == fieldId) {
                        ruleMap.put(rule.getParentRuleId(), rule.getName());

                    }
                }
            }
            FacilioField fieldObj = modBean.getField(fieldId);
            FacilioModule sourceModule = modBean.getModule(fieldObj.getModuleId());
            List<FacilioField> sourcefields = modBean.getAllFields(sourceModule.getName());
            SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
                    .module(sourceModule)
                    .beanClass(ReadingContext.class)
                    .select(sourcefields)
                    .andCondition(CriteriaAPI.getCondition(fieldObj, CommonOperators.IS_NOT_EMPTY));
                    // .groupBy(sourceModule.getTableName() + ".PARENT_ID");

            List<ReadingContext> readingsList = builder.get();
            System.out.println("readingsList" + readingsList.size());
        }

        return false;
    }
}
