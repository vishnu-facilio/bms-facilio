package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;

public class AddCategoryReadingStatusField extends SignUpData {
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        BooleanField hasReadingAsset = new BooleanField();
        hasReadingAsset.setModule(modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
        hasReadingAsset.setName("hasReading");
        hasReadingAsset.setDisplayName("Has Reading");
        hasReadingAsset.setDataType(FieldType.BOOLEAN);
        hasReadingAsset.setDisplayType(FacilioField.FieldDisplayType.DECISION_BOX);
        hasReadingAsset.setDefault(true);
        hasReadingAsset.setColumnName("HAS_READING");
        modBean.addField(hasReadingAsset);

        BooleanField hasReadingMeter = new BooleanField();
        hasReadingMeter.setModule(modBean.getModule(FacilioConstants.Meter.UTILITY_TYPE));
        hasReadingMeter.setName("hasReading");
        hasReadingMeter.setDisplayName("Has Reading");
        hasReadingMeter.setDataType(FieldType.BOOLEAN);
        hasReadingMeter.setDisplayType(FacilioField.FieldDisplayType.DECISION_BOX);
        hasReadingMeter.setDefault(true);
        hasReadingMeter.setColumnName("HAS_READING");
        modBean.addField(hasReadingMeter);
    }
}
