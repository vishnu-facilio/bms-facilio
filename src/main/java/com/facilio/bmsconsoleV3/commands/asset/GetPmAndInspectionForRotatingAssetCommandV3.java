package com.facilio.bmsconsoleV3.commands.asset;

import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.facilio.bmsconsoleV3.util.V3AssetAPI.getInspectionTemplatesList;
import static com.facilio.bmsconsoleV3.util.V3AssetAPI.plannedMaintenanceList;

public class GetPmAndInspectionForRotatingAssetCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long assetId = (Long) context.get(FacilioConstants.ContextNames.ASSET);
        if(assetId!=null){
            if(CollectionUtils.isNotEmpty(plannedMaintenanceList(assetId))) {
                List<PlannedMaintenance> plannedMaintenanceList = plannedMaintenanceList(assetId).stream().map(plannedMaintenance -> {
                    PlannedMaintenance pm = new PlannedMaintenance();
                    pm.setId(plannedMaintenance.getId());
                    pm.setName(plannedMaintenance.getName());
                    return pm;
                }).collect(Collectors.toList());
                context.put(FacilioConstants.ContextNames.PLANNED_MAINTENANCE_LIST,plannedMaintenanceList);
            }
            if(CollectionUtils.isNotEmpty(getInspectionTemplatesList(assetId))) {
                List<InspectionTemplateContext> inspectionTemplateList = getInspectionTemplatesList(assetId).stream().map(inspectionTemplate -> {
                    InspectionTemplateContext inspectionTemplateContext = new InspectionTemplateContext();
                    inspectionTemplateContext.setId(inspectionTemplate.getId());
                    inspectionTemplateContext.setName(inspectionTemplate.getName());
                    return inspectionTemplateContext;
                }).collect(Collectors.toList());
                context.put(FacilioConstants.ContextNames.INSPECTION_TEMPLATE_LIST, inspectionTemplateList);
            }
        }
        return false;
    }
}
