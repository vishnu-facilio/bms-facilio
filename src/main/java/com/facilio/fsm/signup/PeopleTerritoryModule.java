package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SystemEnumField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PeopleTerritoryModule extends BaseModuleConfig {
    public PeopleTerritoryModule(){setModuleName(FacilioConstants.Territory.PEOPLE_TERRITORY);}
    @Override
    public void addData() throws Exception {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule PeopleTerritoryModule = new FacilioModule(FacilioConstants.Territory.PEOPLE_TERRITORY,
                    "People Territory",
                    "PEOPLE_TERRITORY",
                    FacilioModule.ModuleType.SUB_ENTITY,
                    true
            );

            List<FacilioField> fields = new ArrayList<>();

            LookupField people = FieldFactory.getDefaultField("people", "People", "PEOPLE_ID", FieldType.LOOKUP);
            people.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.PEOPLE));
            fields.add(people);

            LookupField territory = FieldFactory.getDefaultField("territory","Territory","TERRITORY_ID",FieldType.LOOKUP);
            territory.setLookupModule(modBean.getModule(FacilioConstants.Territory.TERRITORY));
            fields.add(territory);

            FacilioField startTime = FieldFactory.getDefaultField("startTime","Start Time","START_TIME", FieldType.DATE_TIME);
            fields.add(startTime);

            FacilioField endTime = FieldFactory.getDefaultField("endTime","End Time","END_TIME", FieldType.DATE_TIME);
            fields.add(endTime);

            PeopleTerritoryModule.setFields(fields);

            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(PeopleTerritoryModule));
            addModuleChain.execute();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
