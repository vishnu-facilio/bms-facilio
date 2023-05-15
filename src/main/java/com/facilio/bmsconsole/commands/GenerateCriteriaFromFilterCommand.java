package com.facilio.bmsconsole.commands;

import java.util.*;

import com.facilio.db.criteria.operators.*;
import com.facilio.v3.util.FilterUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;

public class GenerateCriteriaFromFilterCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(GenerateCriteriaFromFilterCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		boolean isV4 = Constants.isV4(context);
		if (isV4) {
			return false;
		}
		long startTime = System.currentTimeMillis();
		Criteria criteria = new Criteria();

		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
		JSONObject quickFilter = (JSONObject) context.get(FacilioConstants.ContextNames.QUICK_FILTER);

		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

		Criteria filterCriteria=FilterUtil.getCriteriaFromFilters(filters,moduleName, context);
		Criteria quickFilterCriteria=FilterUtil.getCriteriaFromQuickFilter(quickFilter,moduleName);

		if(!filterCriteria.isEmpty()){
			criteria.andCriteria(filterCriteria);
		}
		if(!quickFilterCriteria.isEmpty()){
			criteria.andCriteria(quickFilterCriteria);
		}

		if (!criteria.isEmpty()) {
			context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, criteria);
		}
		long timeTaken = System.currentTimeMillis() - startTime;
		LOGGER.debug("Time taken to execute GenerateCriteriaFromFilterCommand : "+timeTaken);
		return false;
	}

}
