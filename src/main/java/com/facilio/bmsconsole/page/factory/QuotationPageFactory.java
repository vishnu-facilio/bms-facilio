package com.facilio.bmsconsole.page.factory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class QuotationPageFactory extends PageFactory {

    public static Page getQuotationPage(QuotationContext record) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule quotationModule = modBean.getModule(FacilioConstants.ContextNames.QUOTATION);

        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

            Page.Section tab1Sec1 = page.new Section();
            tab1.addSection(tab1Sec1);
                PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.QUOTATION_PREVIEW);
                previewWidget.addToLayoutParams(tab1Sec1, 24, 24);
                tab1Sec1.addWidget(previewWidget);


        Page.Tab tab2 = page.new Tab("Notes & Attachments");
        page.addTab(tab2);
            Page.Section tab2Sec1 = page.new Section();
            tab2.addSection(tab2Sec1);
            addCommonSubModuleGroup(tab2Sec1);


        Page.Tab tab3 = page.new Tab("Related Records");
        page.addTab(tab3);
            Page.Section tab3Sec1 = page.new Section();
            tab3.addSection(tab3Sec1);
            addRelatedListWidget(tab3Sec1, "quotationterms", quotationModule.getModuleId());


        return page;
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
