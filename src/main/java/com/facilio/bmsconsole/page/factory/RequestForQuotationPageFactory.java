package com.facilio.bmsconsole.page.factory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class RequestForQuotationPageFactory extends PageFactory{
    private static final Logger LOGGER = LogManager.getLogger(RequestForQuotationPageFactory.class.getName());
    public static Page getRequestForQuotationPage(V3RequestForQuotationContext requestForQuotation, FacilioModule module) throws Exception {
        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);
        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);

        PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.RFQ_PREVIEW);
        previewWidget.addToLayoutParams(tab1Sec1, 24, 24);
        tab1Sec1.addWidget(previewWidget);

        Page.Tab tab2 = page.new Tab("Vendors");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);

        PageWidget vendorWidget = new PageWidget(PageWidget.WidgetType.RFQ_VENDOR);
        vendorWidget.addToLayoutParams(tab2Sec1, 24, 16);
        tab2Sec1.addWidget(vendorWidget);

        Page.Tab tab3 = page.new Tab("Notes & Information");
        page.addTab(tab3);
        Page.Section tab3Sec1 = page.new Section();
        tab3.addSection(tab3Sec1);

        addSecondaryDetailsWidget(tab3Sec1);
        addNotesAttachmentsModule(tab3Sec1);

        Page.Tab tab4 = page.new Tab("Related");
        page.addTab(tab4);
        addRelationshipSection(page, tab4, module.getModuleId());
        Page.Section tab4Sec1 = getRelatedListSectionObj(page);
        tab4.addSection(tab4Sec1);

        addRelatedListWidget(tab4Sec1,"vendorQuotes",module.getModuleId());
        addRelatedListWidget(tab4Sec1,"purchaseorder", module.getModuleId());
        addRelatedListWidget(tab4Sec1,"purchaserequest", module.getModuleId());
        addRelatedList(tab4Sec1,module.getModuleId());


        Page.Tab tab5 = page.new Tab("History");
        page.addTab(tab5);
        Page.Section tab5Sec1 = page.new Section();

        tab5.addSection(tab5Sec1);

        PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);

        activityWidget.addToLayoutParams(tab5Sec1, 24, 3);

        activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_ACTIVITY);

        tab5Sec1.addWidget(activityWidget);

        return page;
    }
    private static void addRelatedList(Page.Section section, long moduleId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> subModules =
                modBean.getSubModules(moduleId, FacilioModule.ModuleType.BASE_ENTITY);


        if (CollectionUtils.isNotEmpty(subModules)) {
            for (FacilioModule subModule : subModules) {

                if(subModule.getName().equals(FacilioConstants.ContextNames.PURCHASE_REQUEST) || subModule.getName().equals(FacilioConstants.ContextNames.PURCHASE_ORDER) || subModule.getName().equals(FacilioConstants.ContextNames.VENDOR_QUOTES)) {
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
    private static void addRelatedListWidget(Page.Section section, String moduleName, long parenModuleId) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> allFields = modBean.getAllFields(module.getName());
        List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == parenModuleId)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(fields)) {
            for (FacilioField field : fields) {
                PageWidget relatedListWidget = new PageWidget(PageWidget.WidgetType.RELATED_LIST);
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
