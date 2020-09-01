package com.facilio.bmsconsole.jobs;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.chain.Chain;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.MLAlarmOccurenceContext;
import com.facilio.bmsconsole.context.MLAnomalyEvent;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class AddSimpleAnomalyEventJob extends FacilioJob {

	private static final Logger LOGGER = Logger.getLogger(AddSimpleAnomalyEventJob.class.getName());
	Long energyFieldId;
	Long upperAnomalyFieldId;
	private static DecimalFormat df = new DecimalFormat("#.##");

	@Override
	public void execute(JobContext jc) throws Exception {
		try {
			LOGGER.info("generating Alarm ML started ");
			org.json.simple.JSONObject props = BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
			energyFieldId = Long.parseLong(props.get("energyFieldId").toString());
			upperAnomalyFieldId = Long.parseLong(props.get("upperAnomalyFieldId").toString());
			generateAnomalyEvents(props, jc.getTimezone());
			LOGGER.info("generating Alarm ML finished ");
		} catch (Exception e) {
			LOGGER.fatal("ERROR in AddSimpleAnomalyEventJob" + e);
			throw e;
		}
	}

	public static void computeStartTimeAndEndTime(org.json.simple.JSONObject props, String timeZone, long times[]) {
		Object startTimeString = props.get("startTime");
		Object endTimeString = props.get("endTime");

		if ((startTimeString != null) && (endTimeString != null)) {
			times[0] = Long.parseLong(startTimeString.toString());
			times[1] = Long.parseLong(endTimeString.toString());
			return;
		}

		final Calendar nowObject = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		int year = nowObject.get(Calendar.YEAR);
		int month = nowObject.get(Calendar.MONTH);
		int day = nowObject.get(Calendar.DATE);
		int hour = nowObject.get(Calendar.HOUR);
		nowObject.set(year, month, day, hour, 0, 0);

		int interval = Integer.parseInt(props.get("interval").toString());
		times[1] = nowObject.getTimeInMillis();
		times[0] = times[1] - interval;
		return;
	}

	private void generateAnomalyEvents(org.json.simple.JSONObject props, String timeZone) throws Exception {
		long times[] = new long[2];
		computeStartTimeAndEndTime(props, timeZone, times);

		long startTime = times[0];
		long endTime = times[1];

		LOGGER.info("startTime : " + startTime + " endTime : " + endTime);
		long interval = 3600000;
		String checkGamParent = props.get("checkGamParent").toString();
		String ml = props.get("ml").toString();
		String[] checkGamParentList = checkGamParent.split(",");
		String[] mlListStr = ml.split(",");
		Long[] mlList = new Long[mlListStr.length];
		for (int i = 0; i < mlListStr.length; i++) {
			mlList[i] = Long.parseLong(mlListStr[i]);
		}
		List<MLAnomalyEvent> eventList = new LinkedList<MLAnomalyEvent>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		while (startTime < endTime) {
			long checkGamModuleid = Long.parseLong(props.get("checkGamModuleid").toString());

			FacilioModule checkGamModule = modBean.getModule(checkGamModuleid);
			List<FacilioField> gamFields = modBean.getAllFields(checkGamModule.getName());

			Hashtable<String, JSONObject> checkGamData = new Hashtable<String, JSONObject>();
			for (String parentID : checkGamParentList) {
				List<Map<String, Object>> props1 = getData(gamFields, checkGamModule, startTime, startTime + 10000,
						parentID);
				for (Map<String, Object> prop : props1) {
					JSONObject data = new JSONObject();
					data.put("actualValue", prop.get("actualValue"));
					data.put("adjustedUpperBound", prop.get("adjustedUpperBound"));
					checkGamData.put(parentID, data);
				}
			}

			MLAnomalyEvent event = null;
			for (int i = 1; i < mlList.length; i++) {
				event = checkAndGenerateMLAnomalyEvent(Long.parseLong(checkGamParentList[i]),
							checkGamData.get(checkGamParentList[i]), startTime, mlList[i]);
				if (event != null) {
					eventList.add(event);
				}
			}
			startTime = startTime + interval;
		}
		if (!eventList.isEmpty()) {
			addEventChain(eventList);
		}
	}

	private List<Map<String, Object>> getData(List<FacilioField> fieldList, FacilioModule module, long startTime,
			long endTime, String parentID) throws Exception {
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
				.select(fieldList).module(module).beanClass(ReadingContext.class).orderBy("TTIME ASC")
				.andCustomWhere("TTIME >= ? AND TTIME < ? AND PARENT_ID=? ", startTime, endTime, parentID);

		return selectBuilder.getAsProps();
	}

	private MLAnomalyEvent generateMLAnomalyEvent(long resourceID, double actualValue, double adjustedUpperBound,
			long ttime, long mlid) throws Exception {
		String message = "Anomaly Detected. Actual Consumption :" + df.format(actualValue)
				+ "KWH , Expected Max Consumption :" + df.format(adjustedUpperBound) + "KWH";
		MLAnomalyEvent event = new MLAnomalyEvent();
		ResourceContext resource = ResourceAPI.getResource(resourceID);
		event.setEventMessage("Anomaly Detected");
		event.setDescription(message);
		event.setResource(resource);
		event.setActualValue(actualValue);
		event.setAdjustedUpperBoundValue(adjustedUpperBound);
		event.setSeverityString("Minor");
		event.setReadingTime(ttime);
		event.setCreatedTime(ttime);
		// select * from Fields WHERE ORGID=? and NAME='totalEnergyConsumptionDelta'
//        event.setEnergyDataFieldid(541054);
		event.setEnergyDataFieldid(energyFieldId);
		// select FIELDID from Fields F INNER JOIN ML ml ON ml.PREDICTION_LOG_MODULEID =
		// F.MODULEID WHERE ml.ID in (select ML_ID from ML_Variables M inner JOIN
		// Modules ms ON M.MODULEID=ms.MODULEID WHERE ms.NAME='energydata' AND
		// IS_SOURCE=1 AND M.ORGID=? and M.PARENT_ID=?) and NAME='adjustedUpperBound'
//        event.setUpperAnomalyFieldid(764441);
		event.setUpperAnomalyFieldid(upperAnomalyFieldId);
		event.setmlid(mlid);
		event.setType(MLAlarmOccurenceContext.MLAnomalyType.Anomaly);
		event.setSiteId(resource.getSiteId());
		return event;
	}

	private MLAnomalyEvent generateRCAEvent(long resourceID, double actualValue, double adjustedUpperBound, long ttime,
			long mlid, double ratio, MLAnomalyEvent parentEvent) throws Exception {
		MLAnomalyEvent event = generateMLAnomalyEvent(resourceID, actualValue, adjustedUpperBound, ttime, mlid);
		event.setType(MLAlarmOccurenceContext.MLAnomalyType.RCA);
		event.setRatio(ratio);
		event.setParentEvent(parentEvent);
		return event;
	}

	private MLAnomalyEvent checkAndGenerateMLAnomalyEvent(long resourceID, JSONObject checkGamData, long ttime,
			long mlid) throws Exception {
		// LOGGER.info("data "+checkGamData + " resorceid : "+resourceID + " ttime :
		// "+ttime);
		if (checkGamData != null && checkGamData.has("actualValue")) {
			Double actualValue = (Double) checkGamData.get("actualValue");
			Double adjustedUpperBound = (Double) checkGamData.get("adjustedUpperBound");
			if (actualValue > adjustedUpperBound) {
				return generateMLAnomalyEvent(resourceID, actualValue, adjustedUpperBound, ttime, mlid);
			} else {
				return clearAnomalyEvent(resourceID, ttime, mlid);
			}
		}
		return null;
	}

	private MLAnomalyEvent clearAnomalyEvent(long assetID, long ttime, long mlid) throws Exception {
		String message = "Anomaly Cleared";
		MLAnomalyEvent event = new MLAnomalyEvent();
		ResourceContext resource = ResourceAPI.getResource(assetID);
		event.setEventMessage(message);
		event.setDescription("Anomaly cleared");
		event.setResource(resource);
		event.setSeverityString(FacilioConstants.Alarm.CLEAR_SEVERITY);
		event.setReadingTime(ttime);
		event.setCreatedTime(ttime);
		// select FIELDID from Fields WHERE ORGID=? and
		// NAME='totalEnergyConsumptionDelta'
//        event.setEnergyDataFieldid(541054);
		event.setEnergyDataFieldid(energyFieldId);
		// select FIELDID from Fields F INNER JOIN ML ml ON ml.PREDICTION_LOG_MODULEID =
		// F.MODULEID WHERE ml.ID in (select ML_ID from ML_Variables M inner JOIN
		// Modules ms ON M.MODULEID=ms.MODULEID WHERE ms.NAME='energydata' AND
		// IS_SOURCE=1 AND M.ORGID=? and M.PARENT_ID=?) and NAME='adjustedUpperBound'
//        event.setUpperAnomalyFieldid(764441);
		event.setUpperAnomalyFieldid(upperAnomalyFieldId);
		event.setmlid(mlid);
		event.setSiteId(resource.getSiteId());
		return event;
	}

	private void addEventChain(List<MLAnomalyEvent> eventList) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(EventConstants.EventContextNames.EVENT_LIST, eventList);
		Chain chain = TransactionChainFactory.getV2AddEventChain(true);
		chain.execute(context);
	}
}
