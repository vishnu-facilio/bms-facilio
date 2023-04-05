package com.facilio.bmsconsole.page.factory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class PurchaseModulesPageFactory extends PageFactory {

    public static Page getPrPage(V3PurchaseRequestContext record, FacilioModule module) throws Exception {

        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.PR_PREVIEW);
        previewWidget.addToLayoutParams(tab1Sec1, 24, 24);
        tab1Sec1.addWidget(previewWidget);


        Page.Tab tab2 = page.new Tab("Notes & Information");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);

        addSecondaryDetailsWidget(tab2Sec1);

        PageWidget notesWidget = new PageWidget(PageWidget.WidgetType.COMMENT);
        notesWidget.addToLayoutParams(tab2Sec1, 24, 8);
        notesWidget.setTitle("Notes");
        tab2Sec1.addWidget(notesWidget);

        PageWidget attachmentWidget = new PageWidget(PageWidget.WidgetType.ATTACHMENT);
        attachmentWidget.addToLayoutParams(tab2Sec1, 24, 6);
        attachmentWidget.setTitle("Attachments");
        tab2Sec1.addWidget(attachmentWidget);

        Page.Tab tab3 = page.new Tab("Related");
        page.addTab(tab3);

        addRelationshipSection(page, tab3, record.getModuleId());

        Page.Section tab3Sec1 = getRelatedListSectionObj(page);
        tab3.addSection(tab3Sec1);
        addAssociatedTermsWidget(tab3Sec1,FacilioConstants.ContextNames.PR_ASSOCIATED_TERMS);
        addRelatedList(tab3Sec1, record.getModuleId());

        Page.Tab tab4 = page.new Tab("History");
        page.addTab(tab4);
        Page.Section tab4Sec1 = page.new Section();
        tab4.addSection(tab4Sec1);
        PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
        activityWidget.addToLayoutParams(tab4Sec1, 24, 3);
        activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.PURCHASE_REQUEST_ACTIVITY);
        tab4Sec1.addWidget(activityWidget);

        return page;
    }


    public static Page getPoPage(V3PurchaseOrderContext record, FacilioModule module) throws Exception {

        Page page = new Page();
        boolean isAtre = AccountUtil.getCurrentOrg().getOrgId() == 418;
        if (!isAtre) {
	        	Page.Tab tab1 = page.new Tab("summary");
	        	page.addTab(tab1);
	        	
	        	Page.Section tab1Sec1 = page.new Section();
	        	tab1.addSection(tab1Sec1);
	        	PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.PO_PREVIEW);
	        	previewWidget.addToLayoutParams(tab1Sec1, 24, 24);
	        	tab1Sec1.addWidget(previewWidget);
        }


        Page.Tab tab2 = page.new Tab("Notes & Information");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);

        addSecondaryDetailsWidget(tab2Sec1);

        PageWidget notesWidget = new PageWidget(PageWidget.WidgetType.COMMENT);
        notesWidget.addToLayoutParams(tab2Sec1, 24, 8);
        notesWidget.setTitle("Notes");
        tab2Sec1.addWidget(notesWidget);

        PageWidget attachmentWidget = new PageWidget(PageWidget.WidgetType.ATTACHMENT);
        attachmentWidget.addToLayoutParams(tab2Sec1, 24, 6);
        attachmentWidget.setTitle("Attachments");
        tab2Sec1.addWidget(attachmentWidget);

        Page.Tab tab3 = page.new Tab("Related");
        page.addTab(tab3);

        addRelationshipSection(page, tab3, module.getModuleId());

        Page.Section tab3Sec1 = getRelatedListSectionObj(page);
        tab3.addSection(tab3Sec1);
        addAssociatedTermsWidget(tab3Sec1,FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS);
        addSubModuleRelatedListWidget(tab3Sec1, FacilioConstants.ContextNames.PURCHASE_REQUEST, module.getModuleId());
        addRelatedList(tab3Sec1, record.getModuleId());

        Page.Tab tab4 = page.new Tab("History");
        page.addTab(tab4);
        Page.Section tab4Sec1 = page.new Section();
        tab4.addSection(tab4Sec1);
        PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
        activityWidget.addToLayoutParams(tab4Sec1, 24, 3);
        activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.PURCHASE_ORDER_ACTIVITY);
        tab4Sec1.addWidget(activityWidget);

        return page;
    }
    public static PageWidget addAssociatedTermsWidget(Page.Section section,String moduleName) {

        PageWidget associatedTermsWidget = new PageWidget();
        associatedTermsWidget.addToLayoutParams(section, 24, 7);
        associatedTermsWidget.setWidgetType(PageWidget.WidgetType.ASSOCIATED_TERMS);
        if(moduleName.equals(FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS)){
            associatedTermsWidget.addToWidgetParams("moduleName",FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS);
        }
        else{
            associatedTermsWidget.addToWidgetParams("moduleName",FacilioConstants.ContextNames.PR_ASSOCIATED_TERMS);
        }
        section.addWidget(associatedTermsWidget);

        return associatedTermsWidget;
    }
    private static void addRelatedList(Page.Section section, long moduleId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> subModules =
                modBean.getSubModules(moduleId, FacilioModule.ModuleType.BASE_ENTITY);


        if (CollectionUtils.isNotEmpty(subModules)) {
            for (FacilioModule subModule : subModules) {

                if(subModule.getName().equals(FacilioConstants.ContextNames.PURCHASE_REQUEST) || subModule.getName().equals(FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS) || subModule.getName().equals(FacilioConstants.ContextNames.PR_ASSOCIATED_TERMS)) {
                    continue;
                }
                List<FacilioField> allFields = modBean.getAllFields(subModule.getName());
                List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == moduleId)).collect(Collectors.toList());
                if ((CollectionUtils.isNotEmpty(fields))) {
                    for (FacilioField field : fields) {
                        PageWidget relatedListWidget = new PageWidget(PageWidget.WidgetType.RELATED_LIST);
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

    private static void addSecondaryDetailsWidget(Page.Section section) {
        PageWidget detailsWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
        detailsWidget.addToLayoutParams(section, 24, 7);
        section.addWidget(detailsWidget);
    }

}
