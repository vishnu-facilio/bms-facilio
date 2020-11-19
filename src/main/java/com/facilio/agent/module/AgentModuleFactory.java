package com.facilio.agent.module;

import com.facilio.modules.FacilioModule;

public class AgentModuleFactory {
	
	public static FacilioModule getMessageToipcModule() {
		FacilioModule module = new FacilioModule();
		module.setName("messagetopic");
		module.setDisplayName("Message Toipc");
		module.setTableName("MessageTopic");
		return module;
	}
	
	public static FacilioModule getAgentDisableModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName("agentDisable");
		module.setDisplayName("Agent Disable");
		module.setTableName("Agent_Disable");
		return module;
	}
	
	public static FacilioModule getLonWorksPointModule() {
		FacilioModule bacnetIpPointModule = new FacilioModule();
		bacnetIpPointModule.setName("lonworksPoint");
		bacnetIpPointModule.setDisplayName("LonWorks Point");
		bacnetIpPointModule.setTableName("LonWorks_Point");
		return bacnetIpPointModule;
	}
}
