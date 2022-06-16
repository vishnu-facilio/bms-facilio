package com.facilio.bmsconsoleV3.commands.labour;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.labour.LabourContextV3;
import com.facilio.bmsconsoleV3.context.location.LocationContextV3;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SetLocationCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<LabourContextV3> labours = recordMap.get("labour");
        if(labours!=null && !labours.isEmpty()) {
            for (LabourContextV3 labour : labours) {
                LocationContextV3 location = labour.getLocation();
                List<Long> recordIds = Collections.singletonList(location.getId());
                ((LocationContextV3) location).setName(labour.getName() + "_location");
                if (location != null) {
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.LOCATION);
                    List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.LOCATION);

                    if(CollectionUtils.isNotEmpty(recordIds) && recordIds.get(0) != -1){
                        V3RecordAPI.updateRecord(location, module, fields,false, true);
                    }
                    else{
                        V3RecordAPI.addRecord(false,Collections.singletonList(location),module,fields);
                    }
                }
            }
        }
        else{
            throw new IllegalArgumentException("Record cannot be null during addition");
        }
        return false;
    }
}