package com.facilio.bmsconsole.commands;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.facilio.bmsconsole.util.OperationAlarmApi;
import com.google.api.client.util.ArrayMap;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.alarms.AgentEventContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.OperationAlarmEventContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import java.util.*;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.context.*;
import com.facilio.db.criteria.operators.*;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.mssql.SelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.google.api.client.util.Data;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class OperationAlarm extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(OperationAlarm.class.getName());

	public boolean executeCommand(Context context) throws Exception {
    	long hrsToCheckinMillis=3600000 * 12;
    	long endTime = System.currentTimeMillis();
    	long startTime = System.currentTimeMillis() - hrsToCheckinMillis ;
		OperationAlarmApi.processOutOfCoverage(startTime, endTime );
        
	return false;
		}

	}

