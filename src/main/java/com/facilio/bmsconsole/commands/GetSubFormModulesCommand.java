package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.facilio.command.FacilioCommand;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiLookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class GetSubFormModulesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (StringUtils.isNotEmpty(moduleName)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Invalid module");
            }

            List<FacilioModule> subModules = new ArrayList<>();
            List<FacilioModule> systemDefinedSubModules = getSystemDefinedSubModules(modBean, module);
            if (CollectionUtils.isNotEmpty(systemDefinedSubModules)) {
                subModules.addAll(systemDefinedSubModules);
            }
            List<FacilioModule> configuredSubModules = modBean.getSubModules(module.getModuleId(), FacilioModule.ModuleType.BASE_ENTITY);
            if (CollectionUtils.isNotEmpty(configuredSubModules)) {
                subModules.addAll(configuredSubModules);
            }
            //adding multi select lookup field related modules
            List<FacilioField> fields = modBean.getAllFields(moduleName);
            if(CollectionUtils.isNotEmpty(fields)) {
                for(FacilioField field : fields) {
                    if(field.getDataTypeEnum() == FieldType.MULTI_LOOKUP){
                        subModules.add(((MultiLookupField)field).getRelModule());
                    }
                }
            }

            context.put(FacilioConstants.ContextNames.MODULE_LIST, subModules);
        }
        return false;
    }

    private List<FacilioModule> getSystemDefinedSubModules(ModuleBean modBean, FacilioModule module) throws Exception{
        switch (module.getName()) {
//            case FacilioConstants.ContextNames.WORK_ORDER:
//                return Arrays.asList(modBean.getModule());
            case FacilioConstants.ContextNames.ASSET:
                return Arrays.asList(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER));
        
        case FacilioConstants.ContextNames.VENDORS:
          return Arrays.asList(modBean.getModule("vendorsNotes"), modBean.getModule(FacilioConstants.ContextNames.INSURANCE),modBean.getModule("vendorDocuments"));
          
        case FacilioConstants.ContextNames.WORK_ORDER:
            return Arrays.asList(modBean.getModule("ticketnotes"), modBean.getModule(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT));
            
        case FacilioConstants.ContextNames.SAFETY_PLAN:
            return Arrays.asList(modBean.getModule(FacilioConstants.ContextNames.SAFETYPLAN_HAZARD));
            
        case FacilioConstants.ContextNames.HAZARD:
            return Arrays.asList(modBean.getModule(FacilioConstants.ContextNames.HAZARD_PRECAUTION));
          
        case FacilioConstants.ContextNames.INVENTORY_REQUEST:
            return Arrays.asList(modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS));

            case FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING:
                return Arrays.asList(modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING_EXTERNAL_ATTENDEE));

            case FacilioConstants.UTILITY_INTEGRATION_TARIFF:
                return Arrays.asList(modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_TARIFF_SLAB));

        }
        return null;
    }
}
