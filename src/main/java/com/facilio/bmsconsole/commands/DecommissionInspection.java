package com.facilio.bmsconsole.commands;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Level;
import org.apache.commons.chain.Context;
@Log4j
public class DecommissionInspection extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.error("Decommission Inspection chain" );
        String resource = String.valueOf(context.get(FacilioConstants.ContextNames.RESOURCE_ID));
        Long resourceId = FacilioUtil.parseLong(resource);
        Boolean decommission = (Boolean) context.get(FacilioConstants.ContextNames.DECOMMISSION);
        if(decommission) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule inspectionResponseModule = modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE);

            DeleteRecordBuilder<InspectionResponseContext> delete = new DeleteRecordBuilder<InspectionResponseContext>()
                    .module(inspectionResponseModule)
                    .andCustomWhere("Inspection_Responses.STATUS = 1")
                    .andCondition(CriteriaAPI.getCondition(FacilioConstants.ContextNames.RESOURCE, "resource", String.valueOf(resourceId), NumberOperators.EQUALS))
                    .skipModuleCriteria();
            delete.markAsDelete();

            LOGGER.log(Level.ERROR, "All Preopen Inspections against the resource(" + resourceId +") has been deleted as the resource is decommissioned");
        }
        return false;
    }
}
