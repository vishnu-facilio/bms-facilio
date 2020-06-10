package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchOldWorkordersCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(Constants.MODULE_NAME);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(wos)) {
            V3WorkOrderContext workOrder = wos.get(0);
            context.put(FacilioConstants.ContextNames.REQUESTER, wos.get(0).getRequester());

            List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
            new ArrayList<>();
            if (workOrder != null && recordIds != null && !recordIds.isEmpty()) {

                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleName);

                List<FacilioField> fields = modBean.getAllFields(moduleName);
                SelectRecordsBuilder<V3WorkOrderContext> builder = new SelectRecordsBuilder<V3WorkOrderContext>()
                        .moduleName(FacilioConstants.ContextNames.WORK_ORDER)
                        .beanClass(V3WorkOrderContext.class)
                        .select(fields)
                        .andCondition(CriteriaAPI.getIdCondition(recordIds, module))
                        .orderBy("ID");

                List<V3WorkOrderContext> workOrderContexts = builder.get();
                if(CollectionUtils.isNotEmpty(workOrderContexts)) {
                    List<Map<String, Object>> mapList = FieldUtil.getAsMapList(workOrderContexts, V3WorkOrderContext.class);
                    context.put(FacilioConstants.TicketActivity.OLD_TICKETS, FieldUtil.getAsBeanListFromMapList(mapList, V3WorkOrderContext.class));
                }
                context.put(FacilioConstants.ContextNames.RECORD_LIST, workOrderContexts);
            }
            context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);

        }
        return false;
    }
}
