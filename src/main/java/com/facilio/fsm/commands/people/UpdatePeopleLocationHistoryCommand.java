package com.facilio.fsm.commands.people;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.V3LocationHistoryContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.integrations.GoogleMapsAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdatePeopleLocationHistoryCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

            String moduleName = Constants.getModuleName(context);
            long peopleId = AccountUtil.getCurrentUser().getPeopleId();
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule locationHistoryModule = modBean.getModule(moduleName);
            List<FacilioField> allFields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
            List<FacilioField> selectFields = new ArrayList<>();
            selectFields.add(fieldMap.get("time"));
            selectFields.add(fieldMap.get("location"));
            selectFields.add(fieldMap.get("batteryInfo"));
            selectFields.add(fieldMap.get("signalInfo"));

            SelectRecordsBuilder<V3LocationHistoryContext> builder = new SelectRecordsBuilder<V3LocationHistoryContext>()
                    .module(locationHistoryModule)
                    .select(selectFields)
                    .beanClass(V3LocationHistoryContext.class)
                    .andCondition(CriteriaAPI.getCondition("PEOPLE_ID", "peopleId", String.valueOf(peopleId), NumberOperators.EQUALS))
                    .orderBy("TIME DESC")
                    .limit(1);
            List<V3LocationHistoryContext> locationHistoryContextList = builder.get();
            if(CollectionUtils.isNotEmpty(locationHistoryContextList)){
                V3LocationHistoryContext locationHistory = locationHistoryContextList.get(0);
                String location = locationHistory.getLocation();
                JSONObject latLng = (JSONObject) new JSONParser().parse(location);
                String formattedAddress = GoogleMapsAPI.reverseGeocode(latLng.get("lat")+","+latLng.get("lng"));
                latLng.put("address",formattedAddress);
                location = latLng.toJSONString();
                long time = locationHistory.getTime();
                Double battery_info = locationHistory.getBatteryInfo();
                Double signal_info = locationHistory.getSignalInfo();
                FacilioField lastSyncTime = Constants.getModBean().getField("lastSyncTime",FacilioConstants.ContextNames.PEOPLE);
                FacilioField currentLocation = Constants.getModBean().getField("currentLocation",FacilioConstants.ContextNames.PEOPLE);
                FacilioField batteryInfo = Constants.getModBean().getField("batteryInfo",FacilioConstants.ContextNames.PEOPLE);
                FacilioField signalInfo = Constants.getModBean().getField("signalInfo",FacilioConstants.ContextNames.PEOPLE);;

                List<FacilioField> fieldList = new ArrayList<>();
                fieldList.add(lastSyncTime);
                fieldList.add(currentLocation);
                fieldList.add(batteryInfo);
                fieldList.add(signalInfo);

                V3PeopleContext pplRecord = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE,peopleId,V3PeopleContext.class);
                Long peopleLastSyncTime = pplRecord.getLastSyncTime();
                if(time > peopleLastSyncTime) {
                    V3PeopleContext peopleContext = new V3PeopleContext();
                    peopleContext.setId(peopleId);
                    peopleContext.setLastSyncTime(time);
                    peopleContext.setCurrentLocation(location);
                    peopleContext.setBatteryInfo(battery_info);
                    peopleContext.setSignalInfo(signal_info);
                    V3RecordAPI.updateRecord(peopleContext, modBean.getModule(FacilioConstants.ContextNames.PEOPLE), fieldList);
                }
            }

        return false;
    }
}
