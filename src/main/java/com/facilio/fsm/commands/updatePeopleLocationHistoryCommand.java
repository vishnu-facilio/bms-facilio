package com.facilio.fsm.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.fsm.context.V3LocationHistoryContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class updatePeopleLocationHistoryCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

            String moduleName = Constants.getModuleName(context);

            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<V3LocationHistoryContext> locationHistoryList = recordMap.get(moduleName);


            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule locationHistoryModule = modBean.getModule(FacilioConstants.LocationHistory.LOCATION_HISTORY);
            if (CollectionUtils.isNotEmpty(locationHistoryList)) {
                V3LocationHistoryContext locationHistory = locationHistoryList.get(0);
                long peopleId = locationHistory.getPeopleId();
                String location = locationHistory.getLocation();
                long time = locationHistory.getTime();
                FacilioField lastSyncTime = Constants.getModBean().getField("lastSyncTime",FacilioConstants.ContextNames.PEOPLE);
                FacilioField currentLocation = Constants.getModBean().getField("currentLocation",FacilioConstants.ContextNames.PEOPLE);

                List<FacilioField> fieldList = new ArrayList<>();
                fieldList.add(lastSyncTime);
                fieldList.add(currentLocation);

                V3PeopleContext peopleContext = new V3PeopleContext();
                peopleContext.setId(peopleId);
                peopleContext.setLastSyncTime(time);
                peopleContext.setCurrentLocation(location);
                V3RecordAPI.updateRecord(peopleContext, modBean.getModule(FacilioConstants.ContextNames.PEOPLE), fieldList);

            }

        return false;
    }
}
