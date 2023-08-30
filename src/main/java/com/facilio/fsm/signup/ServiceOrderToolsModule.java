package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceOrderToolsModule  extends BaseModuleConfig {
    public ServiceOrderToolsModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TOOLS);
    }
    @Override
    public void addData() throws Exception {
        ModuleBean bean = Constants.getModBean();

        FacilioModule serviceOrder = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        FacilioModule serviceTask = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        FacilioModule toolType = bean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
        FacilioModule tool = bean.getModule(FacilioConstants.ContextNames.TOOL);
        FacilioModule storeRoom = bean.getModule(FacilioConstants.ContextNames.STORE_ROOM);

        if(serviceOrder!=null && serviceOrder.getModuleId()>0 && serviceTask!=null && serviceTask.getModuleId()>0 && toolType!=null && toolType.getModuleId()>0 && storeRoom!=null && storeRoom.getModuleId()>0 && tool!=null && tool.getModuleId()>0){

            FacilioModule serviceOrderToolsModule = constructServiceOrderToolsModule(toolType,storeRoom,tool);

            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceOrderToolsModule));
            addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
            addModuleChain.execute();
            bean.addSubModule(serviceOrder.getModuleId(), serviceOrderToolsModule.getModuleId(),0);
            bean.addSubModule(serviceTask.getModuleId(), serviceOrderToolsModule.getModuleId(),0);
            createParentFields(serviceOrder,serviceTask,serviceOrderToolsModule);
        }
    }
    private FacilioModule constructServiceOrderToolsModule(FacilioModule toolTypeMod, FacilioModule storeRoomMod,FacilioModule toolMod){
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TOOLS, "Service Order Tools", "Service_Order_Tools", FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField toolType = FieldFactory.getDefaultField("toolType","Tool Type","TOOL_TYPE",FieldType.LOOKUP);
        toolType.setLookupModule(toolTypeMod);
        fields.add(toolType);

        LookupField tool = FieldFactory.getDefaultField("tool","Tool","TOOL",FieldType.LOOKUP,true);
        tool.setRequired(true);
        tool.setLookupModule(toolMod);
        fields.add(tool);

        LookupField storeRoom = FieldFactory.getDefaultField("storeRoom","Storeroom","STOREROOM",FieldType.LOOKUP);
        storeRoom.setLookupModule(storeRoomMod);
        fields.add(storeRoom);

        fields.add(FieldFactory.getDefaultField("duration","Duration","DURATION",FieldType.NUMBER, FacilioField.FieldDisplayType.DURATION));
        fields.add(FieldFactory.getDefaultField("quantity","Quantity","QUANTITY",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("rate","Rate","RATE",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("totalCost","Total Cost","TOTAL_COST",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("issueTime","Issue Time","ISSUE_TIME",FieldType.DATE_TIME, FacilioField.FieldDisplayType.DATETIME));
        fields.add(FieldFactory.getDefaultField("returnTime","Return Time","RETURN_TIME",FieldType.DATE_TIME, FacilioField.FieldDisplayType.DATETIME));

        module.setFields(fields);

        return module;
    }
    private void createParentFields(FacilioModule serviceOrderMod,FacilioModule serviceTaskMod,FacilioModule serviceOrderToolsModule)throws Exception{
        ModuleBean bean = Constants.getModBean();

        LookupField parent = FieldFactory.getDefaultField("serviceOrder","Service Order","SERVICE_ORDER", FieldType.LOOKUP);
        parent.setRequired(true);
        parent.setLookupModule(serviceOrderMod);
        parent.setModule(serviceOrderToolsModule);
        bean.addField(parent);

        LookupField serviceTask = FieldFactory.getDefaultField("serviceTask","Service Task","SERVICE_TASK",FieldType.LOOKUP);
        serviceTask.setLookupModule(serviceTaskMod);
        serviceTask.setModule(serviceOrderToolsModule);
        bean.addField(serviceTask);
    }
}
