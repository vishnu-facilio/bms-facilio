package com.facilio.fsm.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.WorkTypeContext;
import com.facilio.fsm.context.WorkTypeLineItemsContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LoadWorkTypeLineItemsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        WorkTypeContext workType = (WorkTypeContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.FieldServiceManagement.WORK_TYPE, id);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String lineItemModuleName = FacilioConstants.ContextNames.FieldServiceManagement.WORK_TYPE_LINE_ITEMS;
        List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<WorkTypeLineItemsContext> builder = new SelectRecordsBuilder<WorkTypeLineItemsContext>()
                .moduleName(lineItemModuleName)
                .select(fields)
                .beanClass(WorkTypeLineItemsContext.class)
                .andCondition(CriteriaAPI.getCondition("WORK_TYPE", "workType", String.valueOf(id), NumberOperators.EQUALS))
                .fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType"), (LookupField) fieldsAsMap.get("toolType"), (LookupField) fieldsAsMap.get("service")));
        List<WorkTypeLineItemsContext> list = builder.get();
        workType.setWorkTypeLineItems(list);

        return false;
    }
}
