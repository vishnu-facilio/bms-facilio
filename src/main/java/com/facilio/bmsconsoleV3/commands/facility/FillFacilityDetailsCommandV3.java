package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FillFacilityDetailsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds  = Constants.getRecordIds(context);
        String moduleName = Constants.getModuleName(context);
        if(CollectionUtils.isNotEmpty(recordIds)) {
            for(Long recId : recordIds) {
                 FacilityContext record = (FacilityContext) CommandUtil.getModuleData(context, moduleName,recId);
                if (record != null) {
                    FacilityAPI.setFacilityAmenities(record);
                }
            }
        }
        return false;
    }
}
