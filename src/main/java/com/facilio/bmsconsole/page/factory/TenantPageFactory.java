package com.facilio.bmsconsole.page.factory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.modules.FieldFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class TenantPageFactory extends PageFactory{

	public static Page getTenantPage(TenantContext record, FacilioModule module) throws Exception {
		Page page = new Page();


		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);

		Section tab1Sec1 = page.new Section();

		tab1.addSection(tab1Sec1);

		if(AccountUtil.getCurrentApp().getAppCategory() == ApplicationContext.AppCategory.PORTALS.getIndex()) {
			addSecondaryDetailsWidget(tab1Sec1);
			addNotesAttachmentsModule(tab1Sec1);
		}
		else {
			PageWidget detailsWidgetPrimaryContact = new PageWidget(WidgetType.TENANT_DETAIL_CONTACT);
			detailsWidgetPrimaryContact.addToLayoutParams(tab1Sec1, 12, 4);
			detailsWidgetPrimaryContact.addToWidgetParams("card", "tenantdetailcontact");
			tab1Sec1.addWidget(detailsWidgetPrimaryContact);

			PageWidget detailsWidgetOverview = new PageWidget(WidgetType.TENANT_DETAIL_OVERVIEW);
			detailsWidgetOverview.addToLayoutParams(tab1Sec1, 12, 4);
			detailsWidgetOverview.addToWidgetParams("card", "tenantdetailoverview");
			tab1Sec1.addWidget(detailsWidgetOverview);

			if (record.getDescription() != null && !record.getDescription().isEmpty()) {
				PageWidget descWidget = new PageWidget(PageWidget.WidgetType.TENANT_DESCRIPTION);
				descWidget.addToLayoutParams(tab1Sec1, 24, 2);
				tab1Sec1.addWidget(descWidget);
			}

			PageWidget card2 = new PageWidget(PageWidget.WidgetType.TENANT_WORKORDERS);
			card2.addToLayoutParams(tab1Sec1, 12, 7);
			card2.addToWidgetParams("card", "tenantworkorders");
			tab1Sec1.addWidget(card2);

			PageWidget card3 = new PageWidget(PageWidget.WidgetType.TENANT_BOOKINGS);
			card3.addToLayoutParams(tab1Sec1, 12, 7);
			card3.addToWidgetParams("card", "tenantbookings");
			tab1Sec1.addWidget(card3);


			addTenantSpecialWidget(tab1Sec1);


			addSubModuleRelatedListWidget(tab1Sec1, FacilioConstants.ContextNames.TENANT_CONTACT, module.getModuleId());
		}
		if (record == null) {
			return page;
		}

		if(AccountUtil.getCurrentApp().getAppCategory() != ApplicationContext.AppCategory.PORTALS.getIndex()) {
			Page.Tab tab2 = page.new Tab("Notes and Information");
			page.addTab(tab2);
			Page.Section tab2Sec1 = page.new Section();
			tab2.addSection(tab2Sec1);
			addSecondaryDetailsWidget(tab2Sec1);
			if (record.getAddress() != null) {
				Section tab1Sec2 = page.new Section();
				tab1.addSection(tab1Sec2);
				addAddressInfoWidget(tab2Sec1);
			}
			addNotesAttachmentsModule(tab2Sec1);
		}

		Tab tab3 = page.new Tab("Related");
		page.addTab(tab3);

		addRelationshipSection(page, tab3, module.getModuleId());

		Section tab3Sec1 = getRelatedListSectionObj(page);
		tab3.addSection(tab3Sec1);

		if(AccountUtil.getCurrentOrg().getOrgId() == 321l) {
			addSpecialRelatedListWidgetForDemoOrg(tab3Sec1, record.getModuleId());
		}
		addRelatedList(tab3Sec1, record.getModuleId());
		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.QUOTATION)) {
			addSubModuleRelatedListWidget(tab3Sec1, FacilioConstants.ContextNames.QUOTE, module.getModuleId());
		}
		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.FACILITY_BOOKING)) {
			addSubModuleRelatedListWidget(tab3Sec1, FacilioConstants.ContextNames.FACILITY_BOOKING, module.getModuleId());
		}

		Page.Tab tab4 = page.new Tab("History");
		page.addTab(tab4);
		Page.Section tab4Sec1 = page.new Section();
		tab4.addSection(tab4Sec1);
		PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
		activityWidget.addToLayoutParams(tab4Sec1, 24, 3);
		activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.TENANT_ACTIVITY);
		tab4Sec1.addWidget(activityWidget);

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
	private static void addSpecialRelatedListWidgetForDemoOrg(Section section, long moduleId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule custom_module_utility = modBean.getModule("custom_tenantutility");
		FacilioModule custom_module_billing = modBean.getModule("custom_tenantbilling");

		List<FacilioField> allFields = modBean.getAllFields(custom_module_utility.getName());
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(allFields);

		PageWidget relatedListWidget = new PageWidget(WidgetType.RELATED_LIST);
		JSONObject relatedList = new JSONObject();
		relatedList.put("module", custom_module_utility);
		relatedList.put("field", fieldsAsMap.get("tenant"));
		relatedListWidget.setRelatedList(relatedList);
		relatedListWidget.addToLayoutParams(section, 24, 8);
		section.addWidget(relatedListWidget);

		List<FacilioField> billFields = modBean.getAllFields(custom_module_billing.getName());
		Map<String, FacilioField> billFieldsAsMap = FieldFactory.getAsMap(billFields);

		PageWidget billRelatedListWidget = new PageWidget(WidgetType.RELATED_LIST);
		JSONObject billRelatedList = new JSONObject();
		billRelatedList.put("module", custom_module_billing);
		billRelatedList.put("field", billFieldsAsMap.get("tenanttobebilled"));
		billRelatedListWidget.setRelatedList(billRelatedList);
		billRelatedListWidget.addToLayoutParams(section, 24, 8);
		section.addWidget(billRelatedListWidget);
	}


	private static void addRelatedList(Section section, long moduleId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioModule> subModules =
				modBean.getSubModules(moduleId, FacilioModule.ModuleType.BASE_ENTITY);

		if (CollectionUtils.isNotEmpty(subModules)) {
			for (FacilioModule subModule : subModules) {
				if(subModule.getName().equals(FacilioConstants.ContextNames.FACILITY_BOOKING )|| subModule.getName().equals(FacilioConstants.ContextNames.QUOTE)) {
					if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.QUOTATION)) {
						continue;
					}
					if (!AccountUtil.isFeatureEnabled(FeatureLicense.FACILITY_BOOKING)) {
						continue;
					}
				}
				if(subModule.getName().equals(FacilioConstants.ContextNames.CONTACT) ) {
					continue;
				}
				if(AccountUtil.getCurrentOrg().getOrgId() == 321l && (subModule.getName().equals("custom_tenantutility") || subModule.getName().equals("custom_tenantbilling"))) {
					continue;
				}

				if(subModule.getName().equals(FacilioConstants.ContextNames.TENANT_CONTACT)) {
					continue;
				}

				if(altayerDisableRelatedList(subModule)) {
					continue;
				}
				List<FacilioField> allFields = modBean.getAllFields(subModule.getName());
				List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == moduleId)).collect(Collectors.toList());
				if ((CollectionUtils.isNotEmpty(fields)) && (!subModule.getName().equals("tenantunit")) && (!subModule.getName().equals("tenantspaces"))) {
					for (FacilioField field : fields) {
						PageWidget relatedListWidget = new PageWidget(WidgetType.RELATED_LIST);
						JSONObject relatedList = new JSONObject();
						relatedList.put("module", subModule);
						relatedList.put("field", field);
						relatedListWidget.setRelatedList(relatedList);
						relatedListWidget.addToLayoutParams(section, 24, 8);
						section.addWidget(relatedListWidget);
					}
				}
			}
		}
	}

	private static void addTenantSpecialWidget(Section section) {
		PageWidget specialWidget = new PageWidget(WidgetType.TENANT_SPECIAL_WIDGET);
		specialWidget.addToLayoutParams(section, 24, 9);
		section.addWidget(specialWidget);
	}
	private static void addSecondaryDetailsWidget(Section section) {
		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(section, 24, 7);
		section.addWidget(detailsWidget);
	}
	private static void addAddressInfoWidget(Section section) {
		PageWidget recurringInfoWidget = new PageWidget(WidgetType.ADDRESS, "addressLocation");
		recurringInfoWidget.addToLayoutParams(section, 24, 6);
		section.addWidget(recurringInfoWidget);
	}
}
