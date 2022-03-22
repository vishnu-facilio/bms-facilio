package com.facilio.queueingservice;

import java.util.List;


import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.impl.OrgBeanCacheImpl;
import com.facilio.accounts.impl.UserBeanImpl;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.util.V3Util;

import lombok.extern.log4j.Log4j;

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
