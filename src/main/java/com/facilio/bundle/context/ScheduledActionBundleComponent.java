package com.facilio.bundle.context;

import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.jobs.ScheduledWorkflowJob;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import com.facilio.xml.builder.XMLBuilder;

public class ScheduledActionBundleComponent extends CommonBundleComponent {
	
	public static final String START_TIME = "startTime";
	public static final String SCHEDULE = "schedule";
	public static final String TIMES = "times";
	public static final String FREQUENCY = "facilioFrequency";

	@Override
	public String getBundleXMLComponentFileName(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		Long scheduledActionRuleId = (Long)context.get(BundleConstants.COMPONENT_ID);
		
		ScheduledWorkflowContext scheduledAction = ScheduledWorkflowJob.getScheduledWorkflowContext(scheduledActionRuleId, null);		
		return scheduledAction.getLinkName();
	}
	
	@Override
	public void getFormatedObject(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		Long scheduledActionRuleId = (Long)context.get(BundleConstants.COMPONENT_ID);
		
		XMLBuilder xmlBuilder = (XMLBuilder) context.get(BundleConstants.COMPONENT_XML_BUILDER);
		
		ScheduledWorkflowContext scheduledAction = ScheduledWorkflowJob.getScheduledWorkflowContext(scheduledActionRuleId, null);
		
		xmlBuilder
				  .element(BundleConstants.Components.NAME).text(scheduledAction.getLinkName()).p()
				  .element(BundleConstants.Components.DISPLAY_NAME).text(scheduledAction.getName()).p()
				  .element(START_TIME).text(scheduledAction.getStartTime()+"").p()
				  .element(SCHEDULE)
				  	.element(FREQUENCY).text(scheduledAction.getSchedule().getFrequencyType()+"").p()
				  	.element(TIMES).text(StringUtils.join(scheduledAction.getSchedule().getTimes(), ",")).p()
				  .p()
				  ;	
	}

	@Override
	public BundleModeEnum getInstallMode(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		XMLBuilder xmlContent = (XMLBuilder) context.get(BundleConstants.COMPONENT_XML_BUILDER);
		
		String name = xmlContent.getElement(BundleConstants.Components.NAME).getText();
		
		BundleModeEnum installMode = null;
		
		if(ScheduledWorkflowJob.getScheduledWorkflowContext(name) != null) {
			installMode = BundleModeEnum.UPDATE;
		}
		else {
			installMode = BundleModeEnum.ADD;
		}
		
		return installMode;
	}

	@Override
	public void install(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleModeEnum modeEnum = (BundleModeEnum) context.get(BundleConstants.INSTALL_MODE);
		
		InstalledBundleContext installedBundle = (InstalledBundleContext) context.get(BundleConstants.INSTALLED_BUNDLE);
		
		XMLBuilder xmlContent = (XMLBuilder) context.get(BundleConstants.COMPONENT_XML_BUILDER);
		
	}

	@Override
	public void postInstall(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
