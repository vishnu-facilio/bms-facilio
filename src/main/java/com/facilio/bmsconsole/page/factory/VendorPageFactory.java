package com.facilio.bmsconsole.page.factory;

import java.util.List;
import java.util.stream.Collectors;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountConstants.UserType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.context.WorkPermitContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.page.WidgetGroup.WidgetGroupType;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class VendorPageFactory extends PageFactory{
	private static final Logger LOGGER = LogManager.getLogger(VendorPageFactory.class.getName());
	public static Page getVendorPage(VendorContext vendor) throws Exception {
		Page page = new Page();

		if (AccountUtil.isFeatureEnabled(FeatureLicense.ETISALAT)) {
	
			
			Tab tab1 = page.new Tab("summary");
			page.addTab(tab1);
			
			Section tab1Sec1 = page.new Section();

			tab1.addSection(tab1Sec1);
			Section tab1Sec2 = page.new Section("TARIFFS");

			tab1.addSection(tab1Sec2);

		
			PageWidget cards= new PageWidget(PageWidget.WidgetType.SUPPLIER_CARDS);
			cards.addToLayoutParams(tab1Sec1, 10, 4);
			tab1Sec1.addWidget(cards);
			
			PageWidget cards2= new PageWidget(PageWidget.WidgetType.SUPPLIER_CARDS2);
			cards2.addToLayoutParams(tab1Sec1, 14, 4);
			tab1Sec1.addWidget(cards2);
			
			PageWidget electriycity= new PageWidget(PageWidget.WidgetType.SUPPLIER_ELECTRYCITY);
			electriycity.addToLayoutParams(tab1Sec2, 24, 4);
			tab1Sec2.addWidget(electriycity);
			
			PageWidget water= new PageWidget(PageWidget.WidgetType.SUPPLIER_WATER);
			water.addToLayoutParams(tab1Sec2, 24, 4);
			tab1Sec2.addWidget(water);
			
			PageWidget seawage= new PageWidget(PageWidget.WidgetType.SUPPLIER_SEAWAGE);
			seawage.addToLayoutParams(tab1Sec2, 24, 4);
			tab1Sec2.addWidget(seawage);
			
			PageWidget details= new PageWidget(PageWidget.WidgetType.SUPPLIER_DETAILS);
			details.addToLayoutParams(tab1Sec2, 24, 4);
			tab1Sec2.addWidget(details);
			
			Section tab1Sec3 = page.new Section();
			tab1.addSection(tab1Sec3);
			
			PageWidget subModuleGroup = new PageWidget(WidgetType.GROUP);
			subModuleGroup.addToLayoutParams(tab1Sec3, 24, 8);
			subModuleGroup.addToWidgetParams("type", WidgetGroupType.TAB);
			tab1Sec3.addWidget(subModuleGroup);
			
			PageWidget notesWidget = new PageWidget();
			notesWidget.setWidgetType(WidgetType.COMMENT);
			subModuleGroup.addToWidget(notesWidget);
			
			PageWidget attachmentWidget = new PageWidget();
			attachmentWidget.setWidgetType(WidgetType.ATTACHMENT);
			subModuleGroup.addToWidget(attachmentWidget);
	
			
			
			Page.Tab tab2 = page.new Tab("ACTIVE PAYMENTS");
			page.addTab(tab2);
			Page.Section tab2Sec1 = page.new Section();
			tab2.addSection(tab2Sec1);
			
			PageWidget payments= new PageWidget(PageWidget.WidgetType.SUPPLIER_ACTIVE_PAYMENTS);
			payments.addToLayoutParams(tab2Sec1, 24, 24);
			tab2Sec1.addWidget(payments);

			
			Page.Tab tab4 = page.new Tab("CANCELLED PAYMENTS");
			page.addTab(tab4);
			Page.Section tab4Sec1 = page.new Section();
			tab4.addSection(tab4Sec1);
			
			
			PageWidget cancelPayments= new PageWidget(PageWidget.WidgetType.SUPPLIER_CANCELLED_PAYMENTS);
			cancelPayments.addToLayoutParams(tab4Sec1, 24, 24);
			tab4Sec1.addWidget(cancelPayments);
			
			
	
			
			
			Page.Tab tab5 = page.new Tab("ARREARS");
			page.addTab(tab5);
			Page.Section tab5Sec1 = page.new Section();
			tab5.addSection(tab5Sec1);
			
			PageWidget outstanding = new PageWidget(PageWidget.WidgetType.ARREARS);
			outstanding.addToLayoutParams(tab5Sec1, 10, 4);
			tab5Sec1.addWidget(outstanding);
			
			PageWidget underreview = new PageWidget(PageWidget.WidgetType.UNDER_REVIEW);
			underreview.addToLayoutParams(tab5Sec1, 14, 4);
			tab5Sec1.addWidget(underreview);
			
			Page.Section tab5Sec2 = page.new Section();
			tab5.addSection(tab5Sec2);
			
			PageWidget detailTable = new PageWidget(PageWidget.WidgetType.DETAILS_TABLE);
			detailTable.addToLayoutParams(tab5Sec2, 24, 8);
			tab5Sec2.addWidget(detailTable);
			
			PageWidget detailtabs = new PageWidget(PageWidget.WidgetType.DETAILS_TAB);
			detailtabs.addToLayoutParams(tab5Sec2, 24, 8);
			tab5Sec2.addWidget(detailtabs);
			
			
			Page.Tab tab3 = page.new Tab("TRANSACTIONS");
			page.addTab(tab3);
			Page.Section tab3Sec1 = page.new Section();
			tab3.addSection(tab3Sec1);
			
			PageWidget transaction= new PageWidget(PageWidget.WidgetType.SUPPLIER_TRANSACTIONS);
			transaction.addToLayoutParams(tab3Sec1, 24, 24);
			tab3Sec1.addWidget(transaction);
			
			
			

			
		}
		else {

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(vendor.getModuleId());

			Tab tab1 = page.new Tab("summary");
			page.addTab(tab1);

			Section tab1Sec1 = page.new Section();
			tab1.addSection(tab1Sec1);
//			addPrimaryDetailsWidget(tab1Sec1);
			addSecondaryDetailsWidget(tab1Sec1);
			if(vendor.getAddress()!=null) {
				Section tab1Sec2 = page.new Section();
				tab1.addSection(tab1Sec2);
				addAddressInfoWidget(tab1Sec2);
			}
			
			Section tab1Sec3 = page.new Section();
			tab1.addSection(tab1Sec3);
			
			PageWidget subModuleGroup = new PageWidget(WidgetType.GROUP);
			subModuleGroup.addToLayoutParams(tab1Sec3, 24, 8);
			subModuleGroup.addToWidgetParams("type", WidgetGroupType.TAB);
			tab1Sec3.addWidget(subModuleGroup);
			
			PageWidget notesWidget = new PageWidget();
			notesWidget.setWidgetType(WidgetType.COMMENT);
			subModuleGroup.addToWidget(notesWidget);
			
			
			Tab tab2 = page.new Tab("related list");
			page.addTab(tab2);


			Section tab2Sec1 ;
//			addRelatedListWidget(tab2Sec1, "contact", vendor.getModuleId());
			if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_CONTACTS)) {
				tab2Sec1 = page.new Section();
				tab2.addSection(tab2Sec1);
				addRelatedListWidget(tab2Sec1, "vendorcontact", vendor.getModuleId());
			} else {
				tab2Sec1 = page.new Section();
				tab2.addSection(tab2Sec1);
				addRelatedListWidget(tab2Sec1, "contact", vendor.getModuleId());
			}

			Section tab2Sec2 = page.new Section();
			tab2.addSection(tab2Sec2);
			addRelatedListWidget(tab2Sec2, "vendorDocuments", vendor.getModuleId());
			
			Section tab2Sec3 = page.new Section();
			tab2.addSection(tab2Sec3);
			addRelatedListWidget(tab2Sec3, "insurance", vendor.getModuleId());
			
			if(AccountUtil.getCurrentUser().getAppDomain() != null && AccountUtil.getCurrentUser().getAppDomain().getAppDomainTypeEnum() == AppDomainType.FACILIO) {

				Section tab2Sec4 = page.new Section();
				tab2.addSection(tab2Sec4);
				addRelatedListWidget(tab2Sec4, "workorder", vendor.getModuleId());
				
				Section tab2Sec5 = page.new Section();
				tab2.addSection(tab2Sec5);
				addRelatedListWidget(tab2Sec5, "purchaseorder", vendor.getModuleId());
				
				Section tab2Sec6 = page.new Section();
				tab2.addSection(tab2Sec6);
				addRelatedListWidget(tab2Sec6, "contracts", vendor.getModuleId());
				
			}
		}
		return page;

		
	}

	private static void addPrimaryDetailsWidget(Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.PRIMARY_DETAILS_WIDGET);
		pageWidget.addToLayoutParams(section, 24, 6);
		section.addWidget(pageWidget);
	}

	private static void addSecondaryDetailsWidget(Section section) {
		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(section, 24, 5);
		section.addWidget(detailsWidget);
	}

	private static void addInsuranceWidget(Section section) {
		PageWidget plannedWidget = new PageWidget(WidgetType.INSURANCE, "insurance");
		plannedWidget.addToLayoutParams(section, 24, 10);
		section.addWidget(plannedWidget);
	}

	private static void addAddressInfoWidget(Section section) {
		PageWidget recurringInfoWidget = new PageWidget(WidgetType.ADDRESS, "addressLocation");
		recurringInfoWidget.addToLayoutParams(section, 24, 6);
		section.addWidget(recurringInfoWidget);
	}
	
	private static void addRelatedListWidget(Section section, String moduleName, long parenModuleId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> allFields = modBean.getAllFields(module.getName());
		List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == parenModuleId)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(fields)) {
			for (FacilioField field : fields) {
				PageWidget relatedListWidget = new PageWidget(WidgetType.RELATED_LIST);
				JSONObject relatedList = new JSONObject();
				relatedList.put("module", module);
				relatedList.put("field", field);
				relatedListWidget.setRelatedList(relatedList);
				relatedListWidget.addToLayoutParams(section, 24, 10);
				section.addWidget(relatedListWidget);
			}
		}
	}
	
}
