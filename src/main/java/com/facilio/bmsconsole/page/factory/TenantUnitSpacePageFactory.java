package com.facilio.bmsconsole.page.factory;

import java.util.List;
import java.util.stream.Collectors;

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
        addSpaceDetailWidget(tab1Sec1);
        addRelatedListWidget(tab1Sec1, FacilioConstants.ContextNames.TENANT_SPACES, baseSpaceModule.getModuleId(), "Tenant Spaces");
        addRelatedListWidget(tab1Sec1, FacilioConstants.ContextNames.ASSET, baseSpaceModule.getModuleId(), "Assets");
        if (AccountUtil.getCurrentOrg().getOrgId() != 320l) {
	        	addRelatedListWidget(tab1Sec1, FacilioConstants.ContextNames.WORK_ORDER, resourceModule.getModuleId(), "Work Orders");
        }
        addCommonSubModuleWidget(tab1Sec1, module, tenantUnitSpaceContext);
        return page;
    }

    private static void addSecondaryDetailsWidget(Page.Section section) {
        PageWidget detailsWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
        detailsWidget.addToLayoutParams(section, 24, 4);
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
