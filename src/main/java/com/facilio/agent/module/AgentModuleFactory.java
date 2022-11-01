package com.facilio.agent.module;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;

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
        FacilioModule lonworksModule = new FacilioModule();
        lonworksModule.setName("lonworksPoint");
        lonworksModule.setDisplayName("LonWorks Point");
        lonworksModule.setTableName("LonWorks_Point");
		lonworksModule.setExtendModule(ModuleFactory.getPointModule());
        return lonworksModule;
    }

    public static FacilioModule getRdmPointModule() {
        FacilioModule lonworksModule = new FacilioModule();
        lonworksModule.setName("rdmPoint");
        lonworksModule.setDisplayName("RDM Point");
        lonworksModule.setTableName("RDM_Point");
		lonworksModule.setExtendModule(ModuleFactory.getPointModule());
        return lonworksModule;
	}

	public static FacilioModule getE2PointModule() {
		FacilioModule e2Module = new FacilioModule();
		e2Module.setName("e2Point");
		e2Module.setDisplayName("E2 Point");
		e2Module.setTableName("E2_Point");
		e2Module.setExtendModule(ModuleFactory.getPointModule());
		return e2Module;
	}
}
