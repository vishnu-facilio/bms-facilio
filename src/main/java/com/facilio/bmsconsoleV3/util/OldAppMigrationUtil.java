package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.context.IconType;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.PortalUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OldAppMigrationUtil {
    public static void addTabsForCustomModule(ApplicationLayoutContext layout) {
        try {
            int maxOrder = 0;

            List<FacilioField> fields = new ArrayList<>();
            fields.add(FieldFactory.getField("order", "MAX(TABGROUP_ORDER)", FieldType.NUMBER));
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getWebTabGroupModule().getTableName())
                    .select(fields)
                    .andCondition(CriteriaAPI.getCondition("LAYOUT_ID", "layoutId", String.valueOf(layout.getId()), NumberOperators.EQUALS));
            List<Map<String, Object>> orderList = builder.get();

            if (CollectionUtils.isNotEmpty(orderList)) {
                maxOrder = (int) orderList.get(0).get("order");
            }

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioModule> customModulesList = modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true);
            List<WebTabContext> webTabs = new ArrayList<>();

            WebTabGroupContext connectedApp = new WebTabGroupContext(webTabs, "Connected App", "connectedapp", 9, ++maxOrder, 18l, layout.getId(), IconType.connected_apps);
            WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
            long webTabGroupId = tabBean.addWebTabGroup(connectedApp);
            connectedApp.setId(webTabGroupId);

            int i = 0;
            for (FacilioModule customModule : customModulesList) {
                long webTabId = -1;
                WebTabContext webTab = PortalUtil.getModuleTab(layout.getApplicationId(), customModule.getName());
                if (webTab != null) {
                    webTabId = webTab.getId();
                }
                if (webTabId <= 0) {
                    webTab = new WebTabContext(customModule.getDisplayName(), customModule.getName(), WebTabContext.Type.MODULE, Arrays.asList(customModule.getModuleId()), null, 18, null, layout.getApplicationId());
                    webTabId = tabBean.addTab(webTab);
                    webTab.setId(webTabId);
                    tabBean.insertIntoTabIdAppIdMappingTable(webTab);
                }
                webTabs.add(webTab);
            }

            FacilioChain chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webTabGroupId);
            context.put(FacilioConstants.ContextNames.WEB_TABS, webTabs);
            chain.execute();

        } catch (Exception e) {

        }

    }

    public static WebTabGroupContext findWebTabGroupForWebTab(long webTabId, Integer deviceType) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName())
                .select(FieldFactory.getWebTabGroupFields())
                .innerJoin(ModuleFactory.getWebTabWebGroupModule().getTableName())
                .on("WebTab_Group.ID = WebTab_WebGroup.WEBTAB_GROUP_ID")
                .innerJoin(ModuleFactory.getApplicationLayoutModule().getTableName())
                .on("WebTab_Group.LAYOUT_ID = Application_Layout.ID")
                .andCondition(CriteriaAPI.getCondition("WebTab_WebGroup.WEBTAB_ID", "webTabId",
                        String.valueOf(webTabId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("Application_Layout.DEVICE_TYPE", "layoutDeviceType",
                        String.valueOf(deviceType), NumberOperators.EQUALS));
        WebTabGroupContext webTabGroup = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), WebTabGroupContext.class);
        return webTabGroup;
    }
}
