package com.facilio.bmsconsoleV3.commands.meter;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.MetersAPI;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.*;

public class UtilityTypeAdditionInExtendModuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<ModuleBaseWithCustomFields> meterList = (List<ModuleBaseWithCustomFields>) (recordMap.get("meter"));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Set<String> extendedModules = new HashSet<>();
        for (ModuleBaseWithCustomFields meter : meterList) {
            V3UtilityTypeContext meterUtilityType = MetersAPI.getUtilityTypeForMeter(((V3MeterContext) (meter)).getUtilityType().getId());
            long meterModuleID = meterUtilityType.getMeterModuleID();
            FacilioModule module = modBean.getModule(meterModuleID);
            if(extendedModules.contains(module.getName())){
                List<ModuleBaseWithCustomFields> list = recordMap.get(module.getName());
                list.add(meter);
                recordMap.put(module.getName(),list);
            }else{
                extendedModules.add(module.getName());
                recordMap.put(module.getName(), new ArrayList(Arrays.asList(meter)));
            }
            Constants.setRecordMap(context, recordMap);
            long utilityTypeId = -1;
            if (meterUtilityType != null && meterUtilityType.getId() != 0) {
                utilityTypeId = meterUtilityType.getId();
            }
            context.put(FacilioConstants.Meter.PARENT_UTILITY_TYPE_ID, utilityTypeId);
            context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
        }
        Constants.setExtendedModules(context, extendedModules);
        List<V3MeterContext> v3meterList = (List<V3MeterContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get(FacilioConstants.Meter.METER));
        for(V3MeterContext meter : v3meterList){
            if (meter.getSpace() == null || meter.getSpace().getId() < 0) {
                V3BaseSpaceContext meterLocation = new V3BaseSpaceContext();
                meterLocation.setId(meter.getSiteId());
                meter.setSpace(meterLocation);
            }
        }
        return false;
    }
}
