package com.facilio.bmsconsole.jobs;

import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.chargebee.org.json.JSONArray;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.MLAlarmOccurenceContext;
import com.facilio.bmsconsole.context.MLAnomalyEvent;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class AnomalyEventJob extends FacilioJob {

	private static final Logger LOGGER = Logger.getLogger(AnomalyEventJob.class.getName());

	private static DecimalFormat df = new DecimalFormat("#.##");
	@Override
	public void execute(JobContext jc) throws Exception
	{
		try{
			JSONObject props= new JSONObject(BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName()).toString());
	        generateAnomalyEvents(props);
		}catch(Exception e){
			LOGGER.fatal("ERROR in AnomalyEventJob"+e);
			e.printStackTrace();
			throw e;
		}
	}

	private void generateAnomalyEvents(JSONObject jobProps) throws Exception
	{
        long startTime=jobProps.getLong("startTime");
        long endTime=  jobProps.getLong("endTime");
        long interval = jobProps.getLong("interval");

        JSONArray checkGamParent=new JSONArray(jobProps.getString("checkGamParent"));
        String[] checkGamParentList =  new String[checkGamParent.length()];

        for(int i=0;i<checkGamParent.length();i++){
        	JSONObject json =new JSONObject(checkGamParent.getString(i)) ;
        	checkGamParentList[i]  = json.getString("sourceId");
        }
        List<MLAnomalyEvent> eventList = new LinkedList<MLAnomalyEvent>();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        long checkGamModuleid = jobProps.getLong("checkGamModuleid");
        long checkratioModuleid1 = jobProps.getLong("checkratioModuleid1");
      
        while(startTime<endTime)
        {

            FacilioModule checkGamModule = modBean.getModule(checkGamModuleid);
            List<FacilioField> gamFields = modBean.getAllFields(checkGamModule.getName());

            FacilioModule checkRatio1Module = modBean.getModule(checkratioModuleid1);
            List<FacilioField> checkratio1Fields = modBean.getAllFields(checkRatio1Module.getName());

            Hashtable<String,JSONObject> checkGamData = new Hashtable<String,JSONObject>();
            JSONObject ratio1data = new JSONObject();
            for(String parentID:checkGamParentList)
            {
                    List<Map<String,Object>> props = getData(gamFields,checkGamModule,startTime, startTime+10000,parentID);
                    for(Map<String,Object> prop : props)
                    {
                            JSONObject data = new JSONObject();
                            data.put("actualValue", prop.get("actualValue"));
                            data.put("adjustedUpperBound", prop.get("adjustedUpperBound"));
                            checkGamData.put(parentID, data);
                    }
            }

            List<Map<String, Object>> props = getData(checkratio1Fields,checkRatio1Module,startTime, startTime+interval,checkGamParentList[0]);
            for(Map<String,Object> prop : props)
            {
            	for(int i=1;i<checkGamParentList.length;i++){
                	
                	ratio1data.put(checkGamParentList[i]+"_ratio",prop.get(checkGamParentList[i]+"_ratio"));
                }
            }
           
            if(ratio1data.length()>0)
            {
                JSONObject data = checkGamData.get(checkGamParentList[0]);
                Double actualValue = (Double)data.get("actualValue");
                Double adjustedUpperBound = (Double)data.get("adjustedUpperBound");
                if(actualValue>adjustedUpperBound)
                {
                    MLAnomalyEvent parentEvent = generateMLAnomalyEvent(Long.parseLong(checkGamParentList[0]),actualValue,adjustedUpperBound,startTime,new JSONObject(checkGamParent.get(0).toString()).getLong("mlId"));

                    eventList.add(parentEvent);

                    for(int i=1;i<checkGamParent.length();i++){
                    	if(ratio1data.has(checkGamParentList[1]+"_ratio"))
                        {
                                data = checkGamData.get(checkGamParentList[1]);
                                MLAnomalyEvent event = generateRCAEvent(Long.parseLong(checkGamParentList[1]),(Double)data.get("actualValue"),
                                                (Double)data.get("adjustedUpperBound"),startTime,new JSONObject(checkGamParent.get(i).toString()).getLong("mlId"),(Double)ratio1data.get(checkGamParentList[1]+"_ratio"),parentEvent);
                                eventList.add(event);
                        }
                    }
                }
            }
            else
            {
                MLAnomalyEvent event;
                for(int i=0;i<checkGamParent.length();i++){
                	event =checkAndGenerateMLAnomalyEvent(Long.parseLong(checkGamParentList[i]),checkGamData.get(checkGamParentList[i]),startTime,new JSONObject(checkGamParent.get(i).toString()).getLong("mlId"));
                    if(event!=null)
                    {
                            eventList.add(event);
                    }	
                }
            }
            startTime = startTime+interval;
        }

        addEventChain(eventList);

	}

	private List<Map<String, Object>> getData(List<FacilioField> fieldList,FacilioModule module,long startTime, long endTime,String parentID) throws Exception
	{
    	SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
                                                        .select(fieldList)
                                                        .module(module)
                                                        .beanClass(ReadingContext.class)
                                                        .orderBy("TTIME ASC")
                                                        .andCustomWhere("TTIME >= ? AND TTIME < ? AND PARENT_ID=? ",
                                                                startTime, endTime,parentID);

    	return selectBuilder.getAsProps();
	}

	private MLAnomalyEvent generateMLAnomalyEvent(long resourceID,double actualValue,double adjustedUpperBound,long ttime,long mlid) throws Exception
	{
	    String message = "Anomaly Detected. Actual Consumption :"+df.format(actualValue)+"KWH , Expected Max Consumption :"+df.format(adjustedUpperBound)+"KWH";
	    MLAnomalyEvent event = new MLAnomalyEvent();
	    event.setEventMessage("Anomaly Detected");
	    event.setDescription(message);
		event.setResource(ResourceAPI.getResource(resourceID));
		event.setActualValue(actualValue);
		event.setAdjustedUpperBoundValue(adjustedUpperBound);
		event.setSeverityString("Minor");
		event.setReadingTime(ttime);
		event.setCreatedTime(ttime);
		event.setEnergyDataFieldid(733048);
		event.setUpperAnomalyFieldid(759853);
		event.setmlid(mlid);
		event.setType(MLAlarmOccurenceContext.MLAnomalyType.Anomaly);
		return event;
	}

	private MLAnomalyEvent generateRCAEvent(long resourceID,double actualValue,double adjustedUpperBound,long ttime,long mlid,double ratio,MLAnomalyEvent parentEvent) throws Exception
	{
        MLAnomalyEvent event = generateMLAnomalyEvent(resourceID,actualValue,adjustedUpperBound,ttime,mlid);
        event.setType(MLAlarmOccurenceContext.MLAnomalyType.RCA);
        event.setRatio(ratio);
        event.setParentEvent(parentEvent);
        return event;
	}
	private MLAnomalyEvent checkAndGenerateMLAnomalyEvent(long resourceID,JSONObject checkGamData,long ttime,long mlid) throws Exception
	{
        LOGGER.info("data "+checkGamData + " resorceid : "+resourceID + " ttime : "+ttime);
        if(checkGamData!=null && checkGamData.has("actualValue"))
        {
                Double actualValue = (Double)checkGamData.get("actualValue");
                Double adjustedUpperBound = (Double)checkGamData.get("adjustedUpperBound");
                if(actualValue>adjustedUpperBound)
                {
                        return generateMLAnomalyEvent(resourceID,actualValue,adjustedUpperBound,ttime,mlid);
                }
                else
                {
                         return clearAnomalyEvent(resourceID,ttime,mlid);
                }

        }
        return null;
	}

	private MLAnomalyEvent clearAnomalyEvent(long assetID,long ttime,long mlid) throws Exception
	{
        String message = "Anomaly Cleared";
        MLAnomalyEvent event = new MLAnomalyEvent();
        event.setEventMessage(message);
        event.setDescription("Anomaly cleared");
        event.setResource(ResourceAPI.getResource(assetID));
        event.setSeverityString(FacilioConstants.Alarm.CLEAR_SEVERITY);
        event.setReadingTime(ttime);
        event.setCreatedTime(ttime);
        event.setEnergyDataFieldid(733048);
        event.setUpperAnomalyFieldid(759853);
        event.setmlid(mlid);
        return event;
	}
	
    private void addEventChain(List<MLAnomalyEvent> eventList) throws Exception
    {
	    FacilioContext context = new FacilioContext();
	    context.put(EventConstants.EventContextNames.EVENT_LIST,eventList);
	    Chain chain = TransactionChainFactory.getV2AddEventChain(true);
	    chain.execute(context);
    }

}

                                                                                                                                
                                                                                                                                