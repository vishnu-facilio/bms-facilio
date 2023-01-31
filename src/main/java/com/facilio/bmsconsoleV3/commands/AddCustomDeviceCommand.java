package com.facilio.bmsconsoleV3.commands;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3DeviceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.context.Constants;
import nl.basjes.shaded.org.springframework.util.CollectionUtils;
import org.apache.commons.chain.Context;
import java.util.*;

public class AddCustomDeviceCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3CustomKioskContext> customKioskmoduleContexts = recordMap.get(moduleName);
        if(CollectionUtils.isEmpty(customKioskmoduleContexts)) {
            return true;
        }
        for (V3CustomKioskContext customKiosk : customKioskmoduleContexts) {
            V3DeviceContext device = new V3DeviceContext();
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            V3AssetCategoryContext category = AssetsAPI.getCategoryByAssetModuleV3(modBean.getModule(FacilioConstants.ModuleNames.DEVICES).getModuleId());
            customKiosk.setCategory(category);
            customKiosk.setDeviceTypeEnum(V3DeviceContext.DeviceType.CUSTOM_KIOSK);
            customKiosk.setDeviceType(customKiosk.getDeviceTypeEnum().getIndex());

        }

        return false;
    }

}
