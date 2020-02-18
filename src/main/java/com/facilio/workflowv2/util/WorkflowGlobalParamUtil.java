package com.facilio.workflowv2.util;

import java.util.ArrayList;
import java.util.List;

public class WorkflowGlobalParamUtil {
	
	public static final String PREVIOUS_VALUE = "previousValue";
	public static final String PREVIOUS_VALUE_RECIEVED_TIME = "previousValueReceivedTime";
	
	public static final String DATA_CACHE = "dataCache";
	public static final String RDM_CACHE = "rdmCache";

	private static final List<String> APPROVED_GLOBAL_PARAM_VAR_NAMES = new ArrayList<>();
	
	static {
		APPROVED_GLOBAL_PARAM_VAR_NAMES.add(PREVIOUS_VALUE);
		APPROVED_GLOBAL_PARAM_VAR_NAMES.add(PREVIOUS_VALUE_RECIEVED_TIME);
		APPROVED_GLOBAL_PARAM_VAR_NAMES.add(DATA_CACHE);
		APPROVED_GLOBAL_PARAM_VAR_NAMES.add(RDM_CACHE);
	}
	
	
	public static List<String> getApprovedGlobalParamNames() {
		return APPROVED_GLOBAL_PARAM_VAR_NAMES;
	}
}
