package com.facilio.bmsconsoleV3.signup.moduleconfig;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomKioskButtonModule extends SignUpData {
    @Override
    public void addData() throws Exception {
        FacilioModule customKioskButton = addCustomKioskButtonModule();
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(customKioskButton));
        addModuleChain.execute();
    }
    private FacilioModule addCustomKioskButtonModule() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        // TODO Auto-generated method stub
        FacilioModule module = new FacilioModule("customkioskbutton",
                "CustomKioskButton",
                "Custom_Kiosk_Button",
                FacilioModule.ModuleType.BASE_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        FacilioField labelField = (FacilioField) FieldFactory.getDefaultField("label", "Label", "LABEL", FieldType.STRING, true);
        fields.add(labelField);

        NumberField connectedAppWidgetId = (NumberField) FieldFactory.getDefaultField("connectedAppWidgetId", "ConnectedAppWidgetId", "CONNECTEDAPPWIDGET_ID", FieldType.NUMBER);
        fields.add(connectedAppWidgetId);

        NumberField icon = (NumberField) FieldFactory.getDefaultField("icon", "Icon", "ICON", FieldType.NUMBER);
        fields.add(icon);

        SystemEnumField buttonType = FieldFactory.getDefaultField("buttonType", "Button Type'", "BUTTON_TYPE", FieldType.SYSTEM_ENUM);
        buttonType.setEnumName("ButtonType");
        fields.add(buttonType);

        module.setFields(fields);
        return module;
    }
}


