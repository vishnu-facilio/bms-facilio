package com.facilio.bmsconsole.page.factory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class BillAlertPageFactory extends PageFactory {

	public static Page getBillAlertPage(ModuleBaseWithCustomFields record, FacilioModule module) throws Exception {
		Page page = new Page();
		
		
		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);
		
		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		
		if (record == null) {
			return page;
        }
		
        
        
        PageWidget billDetails= new PageWidget(PageWidget.WidgetType.BILL_ALERT_DETAILS);
        billDetails.addToLayoutParams(tab1Sec1, 24, 5);
        billDetails.addToWidgetParams("hideBg", true);
        tab1Sec1.addWidget(billDetails);
        
        PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.BILL_LIST);
        previewWidget.addToLayoutParams(tab1Sec1, 24, 5);
        tab1Sec1.addWidget(previewWidget);

            
         PageWidget commentWidget = new PageWidget(PageWidget.WidgetType.COMMENT);
         commentWidget.addToLayoutParams(tab1Sec1, 24, 8);
         commentWidget.setTitle("Comments");
         tab1Sec1.addWidget(commentWidget);
         
         PageWidget attachmentWidget = new PageWidget(PageWidget.WidgetType.ATTACHMENT);
         attachmentWidget.addToLayoutParams(tab1Sec1, 24, 6);
         attachmentWidget.setTitle("Attachments");
         tab1Sec1.addWidget(attachmentWidget);
		return page;
	}

}
