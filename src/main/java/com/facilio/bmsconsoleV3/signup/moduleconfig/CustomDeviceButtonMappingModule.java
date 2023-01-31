package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.NumberField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomDeviceButtonMappingModule extends SignUpData {
    @Override
    public void addData() throws Exception {

        FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CUSTOM_KIOSK);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, addCustomKioskField());
        chain.execute();
    }
    private FacilioModule addCustomDeviceButtonMappingModule() throws Exception {


        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        // TODO Auto-generated method stub
        FacilioModule module = new FacilioModule("customdevicebuttonmapping",
                "Custom Device Button Mapping'",
                "Custom_Device_Button_Mapping",
                FacilioModule.ModuleType.SUB_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();



        LookupField kioskId = (LookupField) FieldFactory.getDefaultField("left", "CustomKiosk", "KIOSK_ID", FieldType.LOOKUP);
        kioskId.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.CUSTOM_KIOSK));
        fields.add(kioskId);

        LookupField buttonId = (LookupField) FieldFactory.getDefaultField("right", "CustomKioskButton", "BUTTON_ID", FieldType.LOOKUP);
        buttonId.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.CUSTOM_KIOSK_BUTTON));
        fields.add(buttonId);

        module.setFields(fields);
        return module;
    }


    private List<FacilioField> addCustomKioskField() throws Exception {


        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioField> kioskfields = new ArrayList<>();


        MultiLookupField customkiosk = (MultiLookupField) FieldFactory.getDefaultField("customDeviceButton", "CustomDeviceButton", null, FieldType.MULTI_LOOKUP);
        customkiosk.setDisplayType(10);
        customkiosk.setParentFieldPosition(1);
        customkiosk.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.CUSTOM_KIOSK_BUTTON));
        customkiosk.setRelModule(addCustomDeviceButtonMappingModule());
        kioskfields.add(customkiosk);

        return kioskfields;
    }
}