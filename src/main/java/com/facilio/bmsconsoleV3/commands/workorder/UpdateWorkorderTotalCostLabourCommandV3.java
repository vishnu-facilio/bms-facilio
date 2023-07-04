package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderCostContext;
import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderLabourContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class UpdateWorkorderTotalCostLabourCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<V3WorkOrderLabourContext> workOrderLabour = Constants.getRecordList((FacilioContext) context);
        long parentIds = workOrderLabour.get(0).getParentId();
        if (  parentIds != -1) {

                FacilioModule workorderCostsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_COST);
                List<FacilioField> workorderCostsFields = modBean
                        .getAllFields(FacilioConstants.ContextNames.WORKORDER_COST);
                Map<String, FacilioField> workorderCostsFieldMap = FieldFactory.getAsMap(workorderCostsFields);

                SelectRecordsBuilder<V3WorkorderCostContext> workorderCostsSetlectBuilder = new SelectRecordsBuilder<V3WorkorderCostContext>()
                        .select(workorderCostsFields).table(workorderCostsModule.getTableName())
                        .moduleName(workorderCostsModule.getName()).beanClass(V3WorkorderCostContext.class)
                        .andCondition(CriteriaAPI.getCondition(workorderCostsFieldMap.get("parentId"),
                                String.valueOf(parentIds), PickListOperators.IS));

                List<V3WorkorderCostContext> workorderCostsList = workorderCostsSetlectBuilder.get();
                double totalcost = 0;
                for (V3WorkorderCostContext wo : workorderCostsList) {
                    totalcost += wo.getCost();
                }

                V3WorkOrderContext workorder = (V3WorkOrderContext) V3Util.getRecord("workorder", parentIds,null);
                workorder.setTotalCost(totalcost);
                V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.WORK_ORDER, parentIds, FieldUtil.getAsJSON(workorder), null, null, null, null, null,null,null, null,null);

            }


        return false;
    }

}

