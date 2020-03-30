package com.facilio.bmsconsole.page.factory;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.ApprovalRuleMetaContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.workflow.rule.ApproverContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class ApprovalPageFactory extends PageFactory {
	
	public static Page getApprovalPage(FacilioModule module, ModuleBaseWithCustomFields record) throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getApprovalRuleDetailsChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.ID, record.getApprovalFlowId());
		chain.execute();
		
		ApprovalRuleMetaContext approvalMeta = (ApprovalRuleMetaContext) context.get(FacilioConstants.ContextNames.APPROVAL_RULE);
		List<ApproverContext> approvers = approvalMeta.getApprovers();
		
		
		Page page = new Page();

        Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        
        addApprovalFieldsWidget(tab1Sec1);
        if (CollectionUtils.isNotEmpty(approvers) && approvers.size() > 1) {
        		addApproversWidget(tab1Sec1);
        }
        addSecondaryDetailWidget(tab1Sec1);
        addCommonSubModuleGroup(tab1Sec1);

        return page;
	}
	
	private static void addApprovalFieldsWidget(Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.APPROVAL_FIELDS_WIDGET);
		pageWidget.addToLayoutParams(section, 24, 4);
		section.addWidget(pageWidget);
	}

	private static void addApproversWidget(Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.APPROVERS);
		pageWidget.addToLayoutParams(section, 24, 6);
		section.addWidget(pageWidget);
	}

}
