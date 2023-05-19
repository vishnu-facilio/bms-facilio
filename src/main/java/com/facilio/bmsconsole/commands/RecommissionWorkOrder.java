package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.WorkOrderAction;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.PMStatus;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.log4j.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.facilio.agentv2.controller.Controller.getFieldsMap;
import static com.facilio.modules.ModuleFactory.getPMResourcePlannerModule;

@Log4j
public class RecommissionWorkOrder extends FacilioCommand {

    public boolean executeCommand(Context context) throws Exception {
        LOGGER.error("Recommission Work Order chain");
        String resource = String.valueOf(context.get(FacilioConstants.ContextNames.RESOURCE_ID));
        Long resourceId = FacilioUtil.parseLong(resource);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> PMV2ResourceFields = modBean.getAllFields(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER);
        List<FacilioField> PMV1ResourceFields = FieldFactory.getPreventiveMaintenanceFields();
        FacilioModule PMV1ResourcePlannerModule = getPMResourcePlannerModule();
        FacilioModule PMV1Module = modBean.getModule(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
        FacilioModule PMV2ResourcePlannerModule = modBean.getModule(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER);
        FacilioModule PMV2Module = modBean.getModule(FacilioConstants.PM_V2.PM_V2_MODULE_NAME);
        Map<String, FacilioField> pmv1FieldsMap = getFieldsMap(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
        Map<String, FacilioField> pmv2FieldsMap = getFieldsMap(FacilioConstants.PM_V2.PM_V2_MODULE_NAME);

        GenericSelectRecordBuilder PMV2Records = new GenericSelectRecordBuilder()
                .select(PMV2ResourceFields)
                .table(PMV2ResourcePlannerModule.getTableName())
                .innerJoin(PMV2Module.getTableName())
                .on("PM_V2_Resource_Planner.PM_ID = PM_V2.ID")
                //AND PM_V2.PM_STATUS = 2
                .andCondition((CriteriaAPI.getCondition(pmv2FieldsMap.get("pmStatus"), String.valueOf(PlannedMaintenance.PMStatus.ACTIVE.getVal()), NumberOperators.EQUALS)))
                .andCondition(CriteriaAPI.getCondition(FacilioConstants.ContextNames.RESOURCE_ID, "resourceId", "" + resourceId, NumberOperators.EQUALS));

        List<Map<String, Object>> PMV2RecordList = PMV2Records.get();
        List<Long> PMV2List = new ArrayList<>();
        PMV2RecordList.forEach(n -> {
            PMV2List.add((Long) n.get("pmId"));
        });
        if (!PMV2List.isEmpty()) {
            //Unpublish chain - PMV2.bulkUnPublishPM();
            FacilioChain unPublishPMChain = TransactionChainFactoryV3.getDeactivatePM();
            FacilioContext unPublishContext = unPublishPMChain.getContext();
            unPublishContext.put("pmIds", PMV2List);
            unPublishPMChain.execute();
            //Publish chain - PMV2.bulkPublish();
            FacilioChain publishPMChain = TransactionChainFactoryV3.getPublishPM();
            FacilioContext publishContext = publishPMChain.getContext();
            publishContext.put("pmIds", PMV2List);
            publishPMChain.execute();
        }

        GenericSelectRecordBuilder PMV1Records = new GenericSelectRecordBuilder()
                .select(PMV1ResourceFields)
                .table(PMV1Module.getTableName())
                .innerJoin(PMV1ResourcePlannerModule.getTableName())
                .on("PM_Resource_Planner.PM_ID = Preventive_Maintenance.ID")
                //AND Preventive_Maintenance.Status = 1
                .andCondition((CriteriaAPI.getCondition(pmv1FieldsMap.get("status"), String.valueOf(PMStatus.ACTIVE.getValue()), NumberOperators.EQUALS)))
                .andCondition(CriteriaAPI.getCondition(FacilioConstants.ContextNames.RESOURCE_ID, "resourceId", String.valueOf(resourceId), NumberOperators.EQUALS));

        List<Map<String, Object>> PMV1RecordList = PMV1Records.get();

        PMV1RecordList.forEach(n -> {
            WorkOrderAction PMV1Update = new WorkOrderAction();
            PreventiveMaintenance PM = FieldUtil.getAsBeanFromMap(n, PreventiveMaintenance.class);
            PMV1Update.setId(Collections.singletonList(PM.getId()));
            PMV1Update.setPreventivemaintenance(PM);
            try {
                PMV1Update.changePreventiveMaintenanceStatus();
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, "Error while trying to change status & schedule Job for PM(" + PM.getId() + ") during Recommissioning the Resource(" + resourceId + ")");
            }
        });

        return false;
    }
}
