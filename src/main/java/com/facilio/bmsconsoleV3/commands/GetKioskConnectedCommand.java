package com.facilio.bmsconsoleV3.commands;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.bmsconsole.context.ConnectedAppWidgetContext;
import com.facilio.bmsconsoleV3.context.V3CustomKioskButtonContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import nl.basjes.shaded.org.springframework.util.CollectionUtils;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetKioskConnectedCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3CustomKioskButtonContext> V3CustomKioskButtonContexts = recordMap.get(moduleName);
        if(CollectionUtils.isEmpty(V3CustomKioskButtonContexts)) {
            return true;
        }
        List<ConnectedAppWidgetContext>connectedAppWidget;
        for(V3CustomKioskButtonContext customKioskbutton:V3CustomKioskButtonContexts){

            long id= customKioskbutton.getConnectedAppWidgetId();
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(moduleName);

            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(FieldFactory.getConnectedAppWidgetsFields())
                    .table(ModuleFactory.getConnectedAppWidgetsModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("ID","id", String.valueOf(id), NumberOperators.EQUALS));

            List<Map<String, Object>> props = selectBuilder.get();
            connectedAppWidget= FieldUtil.getAsBeanListFromMapList(props,ConnectedAppWidgetContext.class);

            for(ConnectedAppWidgetContext widgetDetails:connectedAppWidget){
                long connectedAppId=widgetDetails.getConnectedAppId();
                GenericSelectRecordBuilder Builder = new GenericSelectRecordBuilder().select(FieldFactory.getConnectedAppFields())
                        .table(ModuleFactory.getConnectedAppsModule().getTableName())
                        .andCondition(CriteriaAPI.getCondition("ID","id", String.valueOf(connectedAppId), NumberOperators.EQUALS));
                List<Map<String, Object>> details = Builder.get();
                List<ConnectedAppContext>connectedApp= FieldUtil.getAsBeanListFromMapList(details,ConnectedAppContext.class);


                for(ConnectedAppContext app:connectedApp){
                    widgetDetails.setConnectedAppLinkName(app.getLinkName());
                    widgetDetails.setProductionBaseUrl(app.getProductionBaseUrl());
                    widgetDetails.setSandBoxBaseUrl(app.getSandBoxBaseUrl());
                }

            }

            customKioskbutton.setConnectedAppWidgetContext(connectedAppWidget);
        }


        return false;
    }
}
