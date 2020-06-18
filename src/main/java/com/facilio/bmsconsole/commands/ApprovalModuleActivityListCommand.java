package com.facilio.bmsconsole.commands;

import com.facilio.activity.ActivityContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.CommonActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class ApprovalModuleActivityListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (StringUtils.isNotEmpty(moduleName)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioModule> subModules = modBean.getSubModules(moduleName, FacilioModule.ModuleType.ACTIVITY);
            if (CollectionUtils.isEmpty(subModules)) {
                throw new IllegalArgumentException("No Activity module found");
            }
            FacilioModule activityModule = subModules.get(0);

            List<FacilioField> fields = modBean.getAllFields(activityModule.getName());
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            SelectRecordsBuilder<ActivityContext> builder = new SelectRecordsBuilder<ActivityContext>()
                    .module(activityModule)
                    .select(fields)
                    .beanClass(ActivityContext.class)
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("type"), String.valueOf(CommonActivityType.APPROVAL.getValue()), NumberOperators.EQUALS))
                    .orderBy(fieldMap.get("ttime").getCompleteColumnName() + " DESC ");
            List<ActivityContext> activityContexts = builder.get();

            context.put(FacilioConstants.ContextNames.ACTIVITY_LIST, activityContexts);
        }
        return false;
    }
}
