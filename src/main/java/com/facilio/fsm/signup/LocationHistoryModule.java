package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocationHistoryModule extends BaseModuleConfig {
    public LocationHistoryModule(){setModuleName(FacilioConstants.LocationHistory.LOCATION_HISTORY);}

    @Override
    public void addData() throws Exception {
        try {
            FacilioModule locationHistory = addLocationHistoryModule();

            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(locationHistory));
            addModuleChain.execute();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private FacilioModule addLocationHistoryModule() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule(FacilioConstants.LocationHistory.LOCATION_HISTORY,
                "Location History",
                "Location_History",
                FacilioModule.ModuleType.BASE_ENTITY,
                false
        );

        List<FacilioField> fields = new ArrayList<>();

        LookupField peopleId = FieldFactory.getDefaultField("people", "People", "PEOPLE_ID", FieldType.LOOKUP,true);
        peopleId.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(peopleId);

        DateField time = FieldFactory.getDefaultField("time", "Time", "TIME", FieldType.DATE_TIME);
        fields.add(time);

        FacilioField location = FieldFactory.getDefaultField("location", "Location", "LOCATION_JSON", FieldType.STRING);
        fields.add(location);

        fields.add(FieldFactory.getDefaultField("batteryInfo","Battery Info","BATTERY_INFO",FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("signalInfo","Signal Info","SIGNAL_INFO",FieldType.DECIMAL));


        module.setFields(fields);
        return module;

    }

}
