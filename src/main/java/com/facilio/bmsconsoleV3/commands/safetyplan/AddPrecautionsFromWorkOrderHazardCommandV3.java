package com.facilio.bmsconsoleV3.commands.safetyplan;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.HazardsAPI;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderHazardContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderHazardPrecautionContext;
import com.facilio.bmsconsoleV3.context.safetyplans.V3HazardPrecautionContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddPrecautionsFromWorkOrderHazardCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        Map<String, Object> bodyParams =  Constants.getBodyParams(context);
        if(bodyParams != null) {
            List<V3WorkorderHazardContext> wohazards = recordMap.get(moduleName);
            Long workorderId = (Long) bodyParams.get("workorder");
            if (CollectionUtils.isNotEmpty(wohazards) && workorderId != null) {
                V3WorkOrderContext workOrder = new V3WorkOrderContext();
                workOrder.setId(workorderId);
                V3WorkorderHazardContext woHazard = wohazards.get(0);
                if (woHazard != null) {
                    List<V3WorkorderHazardPrecautionContext> woHazardPrecautions = new ArrayList<V3WorkorderHazardPrecautionContext>();
                    List<V3HazardPrecautionContext> hazardPrecautions = HazardsAPI.fetchAssociatedPrecautions(woHazard.getHazard().getId());
                    for (V3HazardPrecautionContext hazardPrecaution : hazardPrecautions) {
                        Map<Long, V3WorkorderHazardPrecautionContext> props = HazardsAPI.checkIfPrecautionAvailable(workOrder,woHazard,hazardPrecaution);
                        if(props != null && props.size() == 0) {
                            V3WorkorderHazardPrecautionContext temp = new V3WorkorderHazardPrecautionContext();
                            temp.setWorkorder(workOrder);
                            temp.setWorkorderHazard(woHazard);
                            temp.setPrecaution(hazardPrecaution.getPrecaution());
                            woHazardPrecautions.add(temp);
                        }
                    }
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_HAZARD_PRECAUTION);
                    List<FacilioField> fields = modBean.getAllFields(module.getName());
                    V3RecordAPI.addRecord(false, woHazardPrecautions, module, fields);
                }
            }
        }
        return false;
    }
}

