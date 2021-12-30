package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorSettingsContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.context.BaseVisitContextV3;
import com.facilio.bmsconsoleV3.context.V3ContactsContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.VisitResponseContextV3;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.bmsconsoleV3.util.V3ContactsAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class VisitResponseCommandV3 extends FacilioCommand {
	private static Logger log = LogManager.getLogger(VisitResponseCommandV3.class.getName());
	 @Override
	 public boolean executeCommand(Context context) throws Exception {
		    try
			{
	        String moduleName = Constants.getModuleName(context);
	        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
	        List<VisitorLogContextV3> visitorLogs = recordMap.get(moduleName);
	        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOG);
	        List<FacilioField> fields = modBean.getAllFields(module.getName());
	        if(CollectionUtils.isNotEmpty(visitorLogs)) {
	            Map<Long, VisitorSettingsContext> settingsMap = VisitorManagementAPI.getVisitorSettingsForType();
	            for(VisitorLogContextV3 vL : visitorLogs) {
	                CommonCommandUtil.handleLookupFormData(modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOG), vL.getData());
	                VisitorSettingsContext setting = settingsMap.get(vL.getVisitorType().getId());
	                VisitorLogContextV3 vlog= vL;
	                VisitResponseContextV3 visitresponse = new VisitResponseContextV3();
	                if(vlog.getVisitCustomResponse() != null )
                	{
                	VisitResponseContextV3 vresponse= getVisitResponse(vlog.getVisitCustomResponse().getId());
                	 vlog.setVisitCustomResponse(vresponse);
                	}
	                else
	                {
	                if(setting.getResponseEnabled()!= null)
	                {
	                if(setting.getResponseEnabled() == true) {
	                    if(vlog.getVisitCustomResponse() == null )
	                    {
	                    	if(vlog.isDenied() && vlog.isDenied() == true)
	                    	{
	                    		if(setting.getFailureId() != null &&  setting.getFailureId() != -99)
	                    		{
	                    			visitresponse.setId(setting.getFailureId());
	                    		 vlog.setVisitCustomResponse(visitresponse);
	                    		}
	                    	}
	                    	else
	                    	{
	                    		if(setting.getSuccessId() != null && setting.getSuccessId() != -99)
	                    		{
	                    		 visitresponse.setId(setting.getSuccessId());
	                    		 vlog.setVisitCustomResponse(visitresponse);
	                    		}
	                    	}
	                    }
	                    if(vlog.getVisitCustomResponse()!= null)
	                    {
	                     VisitResponseContextV3 vresponse= getVisitResponse(vlog.getVisitCustomResponse().getId());
	                     if(!vresponse.isDeleted())
	                     {
	                    	V3RecordAPI.updateRecord(vlog, module, fields);     
		                    vlog.setVisitCustomResponse(vresponse);
	                     }
	                     else
	                     {
							 log.info("Response Deleted");
	                     }
	                    }
	                }
	               }
	              }
				}
			}
		}
            catch(Exception exp)
            {
            	log.info("Exception occurred ", exp);
            }
	        return false;
	    }
	 public static VisitResponseContextV3 getVisitResponse(long visitresponseId) throws Exception {

	        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISIT_CUSTOM_RESPONSE);
	        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISIT_CUSTOM_RESPONSE);
	        SelectRecordsBuilder<VisitResponseContextV3> builder = new SelectRecordsBuilder<VisitResponseContextV3>()
	                .module(module)
	                .beanClass(VisitResponseContextV3.class)
	                .select(fields)
	                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(visitresponseId), NumberOperators.EQUALS))
	                ;
	        
	        VisitResponseContextV3 records = builder.fetchFirst();
	        return records;
	    }
}
