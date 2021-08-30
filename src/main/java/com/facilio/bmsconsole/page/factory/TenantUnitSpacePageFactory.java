package com.facilio.bmsconsole.page.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.facilio.accounts.dto.Account;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class TenantUnitSpacePageFactory extends PageFactory {
    public static Page getTenantUnitSpacePage(TenantUnitSpaceContext tenantUnitSpaceContext, FacilioModule module) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule baseSpaceModule = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
        FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        addSecondaryDetailsWidget(tab1Sec1);
        addCommonSubModuleWidget(tab1Sec1, module, tenantUnitSpaceContext);

        Page.Tab tab2 = page.new Tab("related records");
        page.addTab(tab2);

        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);

        if(AccountUtil.getCurrentApp() != null && AccountUtil.getCurrentApp().getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) {
            addRelatedListWidget(tab2Sec1, FacilioConstants.ContextNames.TENANT_SPACES, baseSpaceModule.getModuleId(), "Tenant History");
            addRelatedListWidget(tab2Sec1, FacilioConstants.ContextNames.ASSET, baseSpaceModule.getModuleId(), "Assets");
        }else if(AccountUtil.getCurrentApp() != null && AccountUtil.getCurrentApp().getLinkName().equals(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP)) {
            addRelatedListWidget(tab2Sec1, FacilioConstants.ContextNames.ASSET, baseSpaceModule.getModuleId(), "Assets");
        }
        if (AccountUtil.getCurrentOrg().getOrgId() == 320l) {
            addRelatedListWidget(tab2Sec1, FacilioConstants.ContextNames.WORK_ORDER, module.getModuleId(), "Work Orders");
        } else {
            addRelatedListWidget(tab2Sec1, FacilioConstants.ContextNames.WORK_ORDER, resourceModule.getModuleId(), "Work Orders");
        }
        List<String> excludedModules = new ArrayList<>();
        excludedModules.add(FacilioConstants.ContextNames.WORK_ORDER);
        excludedModules.add(FacilioConstants.ContextNames.TENANT_SPACES);

        //for atre -- to be removed
        if(AccountUtil.getCurrentOrg().getOrgId() == 418l && AccountUtil.getCurrentApp() != null && !AccountUtil.getCurrentApp().getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) {
            excludedModules.add("custom_vendormapping");
            excludedModules.add("custom_retailoperationassignment");
            excludedModules.add("custom_incidentmanagement_1");
        }

        addRelatedListWidgets(tab2Sec1, module.getModuleId(), excludedModules, false);
        return page;
    }

    private static void addSecondaryDetailsWidget(Page.Section section) {
        PageWidget detailsWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
        detailsWidget.addToLayoutParams(section, 24, 7);
        section.addWidget(detailsWidget);
    }

    private static void addSpaceDetailWidget(Page.Section section) {
        PageWidget recurringInfoWidget = new PageWidget(PageWidget.WidgetType.SPACE_WIDGET, "spaceWidget");
        recurringInfoWidget.addToLayoutParams(section, 24, 3);
        section.addWidget(recurringInfoWidget);
    }

    public static void addRelatedListWidget(Page.Section section, String moduleName, long parenModuleId, String moduleDisplayName) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> allFields = modBean.getAllFields(module.getName());
        List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == parenModuleId)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(fields)) {
            for (FacilioField field : fields) {
                PageWidget relatedListWidget = new PageWidget(PageWidget.WidgetType.RELATED_LIST);
                JSONObject relatedList = new JSONObject();
                module.setDisplayName(moduleDisplayName);
                relatedList.put("module", module);
                relatedList.put("field", field);
                relatedListWidget.setRelatedList(relatedList);
                relatedListWidget.addToLayoutParams(section, 24, 10);
                section.addWidget(relatedListWidget);
            }
        }
    }
}
