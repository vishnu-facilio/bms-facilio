package com.facilio.multiresource.util;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MultiResourceContext;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.context.Constants;

public class MultiResourceUtil{
    
	public static List<MultiResourceContext> getMultiResource(long parentModuleId, long parentRecordId) throws Exception{
		ModuleBean moduleBean = Constants.getModBean();
		SelectRecordsBuilder<MultiResourceContext> selectBuilder = new SelectRecordsBuilder<MultiResourceContext>()
				.moduleName(FacilioConstants.MultiResource.NAME)
				.beanClass(MultiResourceContext.class)
				.select(moduleBean.getAllFields(FacilioConstants.MultiResource.NAME))
				.andCondition(CriteriaAPI.getCondition("PARENT_MODULE_ID", "parentModuleId",String.valueOf(parentModuleId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("PARENT_RECORD_ID", "parentRecordId",String.valueOf(parentRecordId), NumberOperators.EQUALS));
		
		List<MultiResourceContext> multiResourceList = selectBuilder.get(); 
		
		return multiResourceList;
	}
	
	public static void insertMultiResource(List<MultiResourceContext> multiResourceList) throws Exception{
		ModuleBean moduleBean = Constants.getModBean();
		InsertRecordBuilder<MultiResourceContext> insertBuilder = new InsertRecordBuilder<MultiResourceContext>()
				.moduleName(FacilioConstants.MultiResource.NAME)
				.fields(moduleBean.getAllFields(FacilioConstants.MultiResource.NAME))
				.addRecords(multiResourceList);
		insertBuilder.save();
		
	}
}
