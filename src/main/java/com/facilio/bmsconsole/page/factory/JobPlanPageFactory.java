package com.facilio.bmsconsole.page.factory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JobPlanPageFactory extends PageFactory {
    private static final Logger LOGGER = LogManager.getLogger(com.facilio.bmsconsole.page.factory.JobPlanPageFactory.class.getName());

    public static Page getJobPlanPage(JobPlanContext jobPlanContext, FacilioModule module) throws Exception {

        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
        previewWidget.addToLayoutParams(tab1Sec1, 24, 4);
        // label position for field details widget can be added via Widget Params
        previewWidget.addToWidgetParams("labelPosition", "top");
        tab1Sec1.addWidget(previewWidget);


        Page.Section tab1Sec2 = page.new Section();
        tab1Sec2.setName("Sections");
        tab1.addSection(tab1Sec2);
        PageWidget taskWidget = new PageWidget(PageWidget.WidgetType.JOBPLAN_TASKS_WIDGET);
        taskWidget.addToLayoutParams(tab1Sec2, 24, 10);
        tab1Sec2.addWidget(taskWidget);

        //Inventory tab
//        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INVENTORY) && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PLANNED_INVENTORY) ) {
//            addInventoryTab(page);
//        }

        Page.Tab tab2 = page.new Tab("Notes & Information");
        //Plans Tab
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INVENTORY)
                && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PLANNED_INVENTORY)) {
            addPlansTab(page);
        }
        
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);
        
        JSONArray customFieldList = getJobPlanCustomFieldList(module);
        
        if(!customFieldList.isEmpty()) {
        	
        	JSONObject widgetParam = new JSONObject();
            
            widgetParam.put("fields", customFieldList);
            
            PageWidget secondaryDetailsWidget = new PageWidget(PageWidget.WidgetType.Q_AND_A_SECONDARY_DETAILS_WIDGET);
            secondaryDetailsWidget.addToLayoutParams(tab2Sec1, 24, 8);
            secondaryDetailsWidget.setWidgetParams(widgetParam);
            tab2Sec1.addWidget(secondaryDetailsWidget);
        }
        
        PageWidget notesWidget = new PageWidget(PageWidget.WidgetType.COMMENT);
        notesWidget.addToLayoutParams(tab2Sec1, 24, 8);
        notesWidget.setTitle("Notes");
        tab2Sec1.addWidget(notesWidget);

        PageWidget attachmentWidget = new PageWidget(PageWidget.WidgetType.ATTACHMENT);
        attachmentWidget.addToLayoutParams(tab2Sec1, 24, 6);
        attachmentWidget.setTitle("Attachments");
        tab2Sec1.addWidget(attachmentWidget);

        Page.Tab tab3 = page.new Tab("History");
        page.addTab(tab3);
        Page.Section tab4Sec1 = page.new Section();
        tab3.addSection(tab4Sec1);
        PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
        activityWidget.addToLayoutParams(tab4Sec1, 24, 3);
        activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.JOB_PLAN_ACTIVITY);
        tab4Sec1.addWidget(activityWidget);

        return  page;
    }
    
    private static JSONArray getJobPlanCustomFieldList(FacilioModule module) throws Exception {
		// TODO Auto-generated method stub
    	
		JSONArray fieldList = new JSONArray();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> customFields = modBean.getAllCustomFields(module.getName());
		if(customFields != null) {
			fieldList.addAll(customFields.stream().filter(f -> !f.getName().equals("formId")).map(FacilioField::getName).collect(Collectors.toList()));
		}
		
		return fieldList;
	}
    
	private static void addInventoryTab(Page page) throws Exception {
        Page.Tab itemsAndLaborTab = page.new Tab("Items & Tools");
        page.addTab(itemsAndLaborTab);

        Page.Section itemsAndLaborSection = page.new Section();
        itemsAndLaborTab.addSection(itemsAndLaborSection);

        int yOffset = 0;

        // overall cost
        PageWidget overallCost = new PageWidget(PageWidget.WidgetType.PLANNED_INVENTORY_OVERALL_COST);
        overallCost.addToLayoutParams(0, 0, 5, 8);
        itemsAndLaborSection.addWidget(overallCost);

        // items
        PageWidget items = new PageWidget(PageWidget.WidgetType.PLANNED_INVENTORY_ITEMS);
        items.addToLayoutParams(5, yOffset, 19, 7);
        itemsAndLaborSection.addWidget(items);

        // tools
        PageWidget tools = new PageWidget(PageWidget.WidgetType.PLANNED_INVENTORY_TOOLS);
        tools.addToLayoutParams(5, yOffset, 19, 7);
        itemsAndLaborSection.addWidget(tools);

        // services
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CONTRACT)) {
            PageWidget services = new PageWidget(PageWidget.WidgetType.PLANNED_INVENTORY_SERVICES);
            services.addToLayoutParams(5, yOffset, 19, 7);
            itemsAndLaborSection.addWidget(services);
        }
    }
    private static void addPlansTab(Page page) throws Exception{
        Page.Tab plansTab = page.new Tab("Plans");
        page.addTab(plansTab);

        Page.Section plansSection = page.new Section();
        plansTab.addSection(plansSection);


        PageWidget plansPageWidgetGroup = new PageWidget(PageWidget.WidgetType.GROUP);
        plansPageWidgetGroup.addToLayoutParams(plansSection, 24, 10);
        plansPageWidgetGroup.addToWidgetParams("type", WidgetGroup.WidgetGroupType.TAB);
        plansSection.addWidget(plansPageWidgetGroup);

        PageWidget labourWidget = new PageWidget();
        labourWidget.setWidgetType(PageWidget.WidgetType.JOB_PLAN_LABOURS);
        plansPageWidgetGroup.addToWidget(labourWidget);
        labourWidget.setName("Labors");
        labourWidget.setTitle("Labor");
        JSONObject labourRelatedList = new JSONObject();
        labourRelatedList.put("summaryWidgetName","jobPlanLaboursWidget");
        labourWidget.setRelatedList(labourRelatedList);


        PageWidget itemsWidget = new PageWidget();
        itemsWidget.setWidgetType(PageWidget.WidgetType.JOB_PLAN_ITEMS);
        plansPageWidgetGroup.addToWidget(itemsWidget);
        itemsWidget.setName("Items");
        itemsWidget.setTitle("Item");
        JSONObject itemsRelatedList = new JSONObject();
        itemsRelatedList.put("summaryWidgetName","jobPlanItemsWidget");
        itemsWidget.setRelatedList(itemsRelatedList);

        PageWidget toolsWidget = new PageWidget();
        toolsWidget.setWidgetType(PageWidget.WidgetType.JOB_PLAN_TOOLS);
        plansPageWidgetGroup.addToWidget(toolsWidget);
        toolsWidget.setName("Tools");
        toolsWidget.setTitle("Tool");
        JSONObject toolsRelatedList = new JSONObject();
        toolsRelatedList.put("summaryWidgetName","jobPlanToolsWidget");
        toolsWidget.setRelatedList(toolsRelatedList);

        PageWidget serviceWidget = new PageWidget();
        serviceWidget.setWidgetType(PageWidget.WidgetType.JOB_PLAN_SERVICE);
        plansPageWidgetGroup.addToWidget(serviceWidget);
        serviceWidget.setName("Service");
        serviceWidget.setTitle("Service");
        JSONObject servicesRelatedList = new JSONObject();
        servicesRelatedList.put("summaryWidgetName","jobPlanServicesWidget");
        serviceWidget.setRelatedList(servicesRelatedList);
    }
}

