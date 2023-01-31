package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.bmsconsole.context.ConnectedAppWidgetContext;
import com.facilio.bmsconsoleV3.context.V3CustomDeviceButtonMappingContext;
import com.facilio.bmsconsoleV3.context.V3CustomKioskButtonContext;
import com.facilio.bmsconsoleV3.context.V3CustomKioskContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import nl.basjes.shaded.org.springframework.util.CollectionUtils;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetCustomkKioskButtonCommand extends FacilioCommand {
    public boolean executeCommand(Context context) throws Exception {
        FacilioContext V3CustomKioskContext = (FacilioContext) context.get(FacilioConstants.ContextNames.SUMMARY_CONTEXT);
        Map<String, List> recordMap = (Map<String, List>) (V3CustomKioskContext != null ? V3CustomKioskContext.get(Constants.RECORD_MAP) :  context.get(Constants.RECORD_MAP));

        String moduleName = Constants.getModuleName(context);
        List<V3CustomKioskContext> customKioskmoduleContexts = recordMap.get(moduleName);
        if(CollectionUtils.isEmpty(customKioskmoduleContexts)) {
            return true;
        }

        List<V3CustomKioskButtonContext> connectedAppWidgetList = new ArrayList<>();
        List<V3CustomKioskButtonContext>connectedWidget;

        List<ConnectedAppWidgetContext> connectedAppWidgetDetails;
        for(V3CustomKioskContext customKiosk : customKioskmoduleContexts) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<V3CustomDeviceButtonMappingContext> button = customKiosk.getCustomDeviceButton();
            for (V3CustomDeviceButtonMappingContext terms : button) {
                long id = terms.getId();
                GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(modBean.getAllFields(FacilioConstants.ContextNames.CUSTOM_KIOSK_BUTTON))
                        .table(modBean.getModule(FacilioConstants.ContextNames.CUSTOM_KIOSK_BUTTON).getTableName())
                        .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(id), NumberOperators.EQUALS));

                List<Map<String, Object>> props = selectBuilder.get();

                Map<String, Object> record = props.get(0);

                long connectedAppWidgetId = (long) record.get("connectedAppWidgetId");
                GenericSelectRecordBuilder Builder = new GenericSelectRecordBuilder().select(FieldFactory.getConnectedAppWidgetsFields())
                        .table(ModuleFactory.getConnectedAppWidgetsModule().getTableName())
                        .andCondition(CriteriaAPI.getCondition("ID","id", String.valueOf(connectedAppWidgetId), NumberOperators.EQUALS));
                List<Map<String, Object>> prop = Builder.get();
                connectedAppWidgetDetails = FieldUtil.getAsBeanListFromMapList(prop, ConnectedAppWidgetContext.class);


                for(ConnectedAppWidgetContext widgetDetails:connectedAppWidgetDetails){
                    long connectedAppId=widgetDetails.getConnectedAppId();
                    GenericSelectRecordBuilder select = new GenericSelectRecordBuilder().select(FieldFactory.getConnectedAppFields())
                            .table(ModuleFactory.getConnectedAppsModule().getTableName())
                            .andCondition(CriteriaAPI.getCondition("ID","id", String.valueOf(connectedAppId), NumberOperators.EQUALS));
                    List<Map<String, Object>> details = select.get();
                    List<ConnectedAppContext>connectedApp= FieldUtil.getAsBeanListFromMapList(details,ConnectedAppContext.class);

                    for(ConnectedAppContext app:connectedApp){
                        widgetDetails.setConnectedAppLinkName(app.getLinkName());
                        widgetDetails.setProductionBaseUrl(app.getProductionBaseUrl());
                        widgetDetails.setSandBoxBaseUrl(app.getSandBoxBaseUrl());
                    }

                }

                int buttonType = (int) record.get("buttonType");
                connectedWidget= FieldUtil.getAsBeanListFromMapList(props, V3CustomKioskButtonContext.class);
                for (V3CustomKioskButtonContext widget : connectedWidget)
                {
                    widget.setId(id);
                    widget.setButtonTypeEnum(V3CustomKioskButtonContext.ButtonType.valueOf(buttonType));
                    widget.setConnectedAppWidgetContext(connectedAppWidgetDetails);
                    connectedAppWidgetList.add(widget);
                }

                customKiosk.setConnectedAppWidgetList(connectedAppWidgetList);


//                if (buttonType == 1) {
//                    CheckedIn = FieldUtil.getAsBeanListFromMapList(props, V3CustomKioskButtonContext.class);
//                    for (V3CustomKioskButtonContext checkIn : CheckedIn) {
//                        checkIn.setConnecXtedAppWidgetContext(connectedAppWidgetDetails);
//                        checkIn.setId(id);
//                        checkIn.setButtonTypeEnum(V3CustomKioskButtonContext.ButtonType.valueOf(buttonType));
//                        CheckInButtons.add(checkIn);
//
//                    }
//                } else {
//                    CheckedOut = FieldUtil.getAsBeanListFromMapList(props, V3CustomKioskButtonContext.class);
//                    for (V3CustomKioskButtonContext checkOut : CheckedOut) {
//                        checkOut.setConnectedAppWidgetContext(connectedAppWidgetDetails);
//                        checkOut.setId(id);
//                        checkOut.setButtonTypeEnum(V3CustomKioskButtonContext.ButtonType.valueOf(buttonType));
//                        CheckOutButtons.add(checkOut);
//                    }
//                }
//                customKiosk.setCheckInButtons(CheckInButtons);
//                customKiosk.setCheckOutButtons(CheckOutButtons);

            }
        }
        return false;
    }
}
