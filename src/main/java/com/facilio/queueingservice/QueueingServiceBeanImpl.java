package com.facilio.queueingservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.impl.OrgBeanCacheImpl;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.relation.util.RelationshipDataUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;

public class QueueingServiceBeanImpl implements QueueingServiceBean {
    private static final Logger LOGGER = LogManager.getLogger(OrgBeanCacheImpl.class.getName());

	@Override
	public void addScriptLogs(List<Map<String,Object>> records){
		
			ModuleBean modBean;
			try {
				modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(FacilioConstants.Workflow.WORKFLOW_LOG);
				V3Util.createRecordList(module,records,null,null);
			} catch (Exception e) {
				LOGGER.info(e);
			}
	}

	@Override
	public void addScriptLog(Map<String, Object> record) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.Workflow.WORKFLOW_LOG);
		V3Util.createRecord(module, record);
	}
}
