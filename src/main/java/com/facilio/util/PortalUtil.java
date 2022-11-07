package com.facilio.util;

import java.util.List;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.TabIdAppIdMappingContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

import lombok.extern.log4j.Log4j;

@Log4j
public class PortalUtil {
	
	
	public static String getPortalRecordSummaryLink(ApplicationContext appContext, String moduleName, Long recordId) throws Exception {
		
			WebTabContext tab = getModuleTab(appContext.getId(), moduleName);
			
			if(tab != null) {
				
				String appDomain = FacilioProperties.getAppProtocol()+ "://" +appContext.getAppDomain().getDomain()+"/"+appContext.getLinkName()+"/";
				
				WebTabGroupContext webTabGroup = getWebTabGroupFromWebTab(tab);
				
				appDomain += webTabGroup.getRoute() + "/";
				appDomain += tab.getRoute() + "/";
				appDomain += moduleName + "/";
				appDomain += recordId + "/overview";
				
				return appDomain;
			}
			
			return null;
	}

	public static WebTabContext getModuleTab(Long appId,String moduleName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		boolean isSpecialType = LookupSpecialTypeUtil.isSpecialType(moduleName);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getTabIdAppIdMappingModule().getTableName())
                .select(FieldFactory.getTabIdAppIdMappingFields())
                .andCondition(CriteriaAPI.getCondition("TABID_MODULEID_APPID_MAPPING.APP_ID", "appId",String.valueOf(appId), NumberOperators.EQUALS));
        if (!isSpecialType) {
            builder.andCondition(CriteriaAPI.getCondition("TABID_MODULEID_APPID_MAPPING.MODULE_ID", "moduleId",String.valueOf(modBean.getModule(moduleName).getModuleId()), NumberOperators.EQUALS));
        } else {
            builder.andCondition(CriteriaAPI.getCondition("TABID_MODULEID_APPID_MAPPING.SPECIAL_TYPE", "specialType",moduleName, StringOperators.IS));
        }
		
        List<TabIdAppIdMappingContext> webTabGroups = FieldUtil.getAsBeanListFromMapList(builder.get(),TabIdAppIdMappingContext.class);
        
        if(webTabGroups != null && !webTabGroups.isEmpty()) {
        	Long tabId =  webTabGroups.get(0).getTabId();
        	WebTabContext webTab = ApplicationApi.getWebTab(tabId);
        	return webTab;
        }
        
        return null;
	}
	
	public static WebTabGroupContext getWebTabGroupFromWebTab(WebTabContext tab) throws Exception {
		
		 GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
	                .table(ModuleFactory.getWebTabGroupModule().getTableName()).select(FieldFactory.getWebTabGroupFields())
	                .innerJoin(ModuleFactory.getWebTabWebGroupModule().getTableName())
	                .on("WebTab_Group.ID = WebTab_WebGroup.WEBTAB_GROUP_ID")
	                .andCondition(CriteriaAPI.getCondition("WebTab_WebGroup.WEBTAB_ID", "tabId", String.valueOf(tab.getId()), NumberOperators.EQUALS));
	        
		 List<WebTabGroupContext> webTabGroups = FieldUtil.getAsBeanListFromMapList(builder.get(), WebTabGroupContext.class);

		 if(webTabGroups != null && !webTabGroups.isEmpty()) {
			 return webTabGroups.get(0);
		 }
        return null;
	}
}
