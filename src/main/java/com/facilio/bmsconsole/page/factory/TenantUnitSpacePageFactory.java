package com.facilio.bmsconsole.page.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.context.SummaryWidget;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.util.SummaryWidgetUtil;
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

    public static Page getTenantUnitSpacePage(TenantUnitSpaceContext record, FacilioModule module) throws Exception {
        Page page = new Page();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule baseSpaceModule = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
        FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();

        tab1.addSection(tab1Sec1);


        PageWidget detailsOverviewWidget= new PageWidget(PageWidget.WidgetType.TENANT_UNIT_OVERVIEW);
        detailsOverviewWidget.addToLayoutParams(tab1Sec1, 24, 4);
        detailsOverviewWidget.addToWidgetParams("card","tenantunitoverview");
        tab1Sec1.addWidget(detailsOverviewWidget);

        if (record.getDescription() != null && !record.getDescription().isEmpty()) {
            PageWidget descWidget = new PageWidget(PageWidget.WidgetType.TENANT_DESCRIPTION);
            descWidget.addToLayoutParams(tab1Sec1,24, 2);
            tab1Sec1.addWidget(descWidget);
        }

        PageWidget card2= new PageWidget(PageWidget.WidgetType.TENANT_UNIT_TENANT);
        card2.addToLayoutParams(tab1Sec1, 8, 8);
        card2.addToWidgetParams("card","tenantunittenant");
        tab1Sec1.addWidget(card2);

        PageWidget card3= new PageWidget(PageWidget.WidgetType.TENANT_UNIT_WORKORDER);
        card3.addToLayoutParams(tab1Sec1, 8, 8);
        card3.addToWidgetParams("card","tenantunitworkorder");
        tab1Sec1.addWidget(card3);

        PageWidget card4= new PageWidget(PageWidget.WidgetType.TENANT_UNIT_LOCATION);
        card4.addToLayoutParams(tab1Sec1, 8, 8);
        card4.addToWidgetParams("card","tenantunitlocation");
        tab1Sec1.addWidget(card4);

        PageWidget photoWidget= new PageWidget(PageWidget.WidgetType.TENANT_UNIT_PHOTO);
        photoWidget.addToLayoutParams(tab1Sec1, 24, 4);
        photoWidget.addToWidgetParams("card","tenantunitphoto");
        tab1Sec1.addWidget(photoWidget);
        if(!(AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().isPortalUser())){
            addTenantUnitSpecialWidget(tab1Sec1);
        }
        if (record == null) {
            return page;
        }

        Page.Tab tab2 = page.new Tab("Notes and Information");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);

        boolean isNewSummaryWidgetAdded = false;
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SUMMARY_WIDGET)) {
            SummaryWidget pageWidget = SummaryWidgetUtil.getMainSummaryWidget(module.getModuleId());
            if(pageWidget != null){
                isNewSummaryWidgetAdded = true;
                PageWidget newSummaryFieldsWidget = new PageWidget(PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, FacilioConstants.WidgetNames.MAIN_SUMMARY_WIDGET);
                newSummaryFieldsWidget.addToLayoutParams(tab2Sec1, 24, 7);
                tab2Sec1.addWidget(newSummaryFieldsWidget);
            }
        }
        if(!isNewSummaryWidgetAdded) {
            addSecondaryDetailsWidget(tab2Sec1);
        }

        if(!(AccountUtil.getCurrentOrg().getId() == 418l && AccountUtil.getCurrentApp() != null && AccountUtil.getCurrentApp().getLinkName().equals(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP))) {
            addNotesAttachmentsModule(tab2Sec1);
        }


        Page.Tab tab3 = page.new Tab("Related");
        page.addTab(tab3);

        addRelationshipSection(page, tab3, module.getModuleId());
        Page.Section tab3Sec1 = getRelatedListSectionObj(page);
        tab3.addSection(tab3Sec1);

        List<String> excludedModules = new ArrayList<>();
        excludedModules.add(FacilioConstants.ContextNames.WORK_ORDER);
        excludedModules.add(FacilioConstants.ContextNames.TENANT_SPACES);
        excludedModules.add(FacilioConstants.ContextNames.ASSET);

        //for atre -- to be removed
        if (AccountUtil.getCurrentOrg().getOrgId() == 418l && AccountUtil.getCurrentApp() != null && !AccountUtil.getCurrentApp().getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) {
            excludedModules.add("custom_vendormapping");
            excludedModules.add("custom_retailoperationassignment");
            excludedModules.add("custom_incidentmanagement_1");
        }

        if (AccountUtil.getCurrentOrg().getOrgId() == 418l) {
            excludedModules.add(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
            if (AccountUtil.getCurrentApp() != null && AccountUtil.getCurrentApp().getLinkName().equals(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP)) {
                excludedModules.add(FacilioConstants.ContextNames.ASSET);
            } else {
                addRelatedListWidget(tab3Sec1, FacilioConstants.ContextNames.ASSET, baseSpaceModule.getModuleId(), "Assets");
            }
        } else {
            addRelatedListWidget(tab3Sec1, FacilioConstants.ContextNames.ASSET, baseSpaceModule.getModuleId(), "Assets");
        }
        addRelatedListWidget(tab3Sec1, FacilioConstants.ContextNames.WORK_ORDER, resourceModule.getModuleId(), "Work Orders");
        addRelatedListWidgets(tab3Sec1, module.getModuleId(), excludedModules, false);
        return page;
    }
    private static PageWidget addNotesAttachmentsModule(Page.Section section) {

        PageWidget subModuleGroup = new PageWidget(PageWidget.WidgetType.GROUP);
        subModuleGroup.addToLayoutParams(section, 24, 8);
        subModuleGroup.addToWidgetParams("type", WidgetGroup.WidgetGroupType.TAB);
        section.addWidget(subModuleGroup);

        PageWidget notesWidget = new PageWidget();
        notesWidget.setWidgetType(PageWidget.WidgetType.COMMENT);
        subModuleGroup.addToWidget(notesWidget);

        PageWidget attachmentWidget = new PageWidget();
        attachmentWidget.setWidgetType(PageWidget.WidgetType.ATTACHMENT);
        subModuleGroup.addToWidget(attachmentWidget);

        return subModuleGroup;
    }
    private static void addSecondaryDetailsWidget(Page.Section section) {
        PageWidget detailsWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
        detailsWidget.addToLayoutParams(section, 24, 7);
        section.addWidget(detailsWidget);
    }

    private static void addTenantUnitSpecialWidget(Page.Section section) {
        PageWidget specialWidget = new PageWidget(PageWidget.WidgetType.TENANT_UNIT_SPECIAL_WIDGET);
        specialWidget.addToLayoutParams(section, 24, 9);
        section.addWidget(specialWidget);
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
