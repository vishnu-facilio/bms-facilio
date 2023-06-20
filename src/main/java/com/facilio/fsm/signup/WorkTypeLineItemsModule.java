package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;

import java.util.*;
import java.util.stream.Collectors;

public class WorkTypeLineItemsModule extends BaseModuleConfig {

    public WorkTypeLineItemsModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.WORK_TYPE_LINE_ITEMS);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean bean = Constants.getModBean();

        FacilioModule workTypeModule = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.WORK_TYPE);
        FacilioModule itemTypeModule = bean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
        FacilioModule toolTypeModule = bean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
        FacilioModule serviceModule = bean.getModule(FacilioConstants.ContextNames.SERVICE);

        if(workTypeModule!=null && workTypeModule.getModuleId()>0 && itemTypeModule!=null && itemTypeModule.getModuleId()>0 && toolTypeModule!=null && toolTypeModule.getModuleId()>0 && serviceModule!=null && serviceModule.getModuleId()>0){
            FacilioModule workTypeLineItemsModule = constructWorkTypeLineItemsModule(workTypeModule,itemTypeModule,toolTypeModule,serviceModule);

            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(workTypeLineItemsModule));
            addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE, workTypeModule.getName());
            addModuleChain.execute();
            bean.addSubModule(workTypeModule.getModuleId(), workTypeLineItemsModule.getModuleId());
        }
    }

    private FacilioModule constructWorkTypeLineItemsModule(FacilioModule workTypeModule,FacilioModule itemTypeModule,FacilioModule toolTypeModule, FacilioModule serviceModule){
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.WORK_TYPE_LINE_ITEMS, "Work Type Line Items", "Work_Type_Line_Items", FacilioModule.ModuleType.SUB_ENTITY);

        List<FacilioField> fields = new ArrayList<>();

        LookupField parent = FieldFactory.getDefaultField("workType","Work Type","WORK_TYPE",FieldType.LOOKUP,true);
        parent.setLookupModule(workTypeModule);
        fields.add(parent);

        SystemEnumField inventoryType = FieldFactory.getDefaultField("inventoryType","Inventory Type","INVENTORY_TYPE",FieldType.SYSTEM_ENUM);
        inventoryType.setEnumName("WorkTypeInventoryType");
        fields.add(inventoryType);

        LookupField itemType = FieldFactory.getDefaultField("itemType","Item Type","ITEM_TYPE",FieldType.LOOKUP);
        itemType.setLookupModule(itemTypeModule);
        fields.add(itemType);

        LookupField toolType = FieldFactory.getDefaultField("toolType","Tool Type","TOOL_TYPE",FieldType.LOOKUP);
        toolType.setLookupModule(toolTypeModule);
        fields.add(toolType);

        LookupField service = FieldFactory.getDefaultField("service","Service","SERVICE",FieldType.LOOKUP);
        service.setLookupModule(serviceModule);
        fields.add(service);

        fields.add(FieldFactory.getDefaultField("quantity","Quantity","QUANTITY",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL));


        EnumField unitOfMeasure = FieldFactory.getDefaultField("unitOfMeasure","Unit of Measure","UNIT_OF_MEASURE", FieldType.ENUM);
        List<String> unitOfMeasures = Arrays.asList("Each", "kg", "Hour");

        List<EnumFieldValue<Integer>> unitOfMeasureValues = unitOfMeasures.stream().map(val -> {
            int index = unitOfMeasures.indexOf(val)+1;
            return new EnumFieldValue<>(index, val, index, true);
        }).collect(Collectors.toList());
        unitOfMeasure.setValues(unitOfMeasureValues);
        fields.add(unitOfMeasure);

        module.setFields(fields);

        return module;
    }
}
