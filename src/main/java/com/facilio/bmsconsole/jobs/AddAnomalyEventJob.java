package com.facilio.bmsconsole.jobs;

import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

public class AddAnomalyEventJob extends FacilioJob {

    private static final Logger LOGGER = Logger.getLogger(AddAnomalyEventJob.class.getName());

    private static DecimalFormat df = new DecimalFormat("#.##");
    @Override
    public void execute(JobContext jc) throws Exception
    {
    	try{
            LOGGER.info("generating Alarm ML started ");
            org.json.simple.JSONObject props=BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
            generateAnomalyEvents(Long.parseLong(props.get("startTime").toString()),Long.parseLong(props.get("endTime").toString()));
            LOGGER.info("generating Alarm ML finished ");
    	}catch(Exception e){
    		LOGGER.fatal("ERROR in AddAnomalyEventJob"+e);
    		throw e;
    	}
    }
    
    private void generateAnomalyEvents(long startTime,long endTime) throws Exception
    {	
//      select PARENT_METER_ID,MIN(TTIME),MAX(TTIME) from Energy_Data where ORGID=78 and PARENT_METER_ID in (1273322,1270982,1270992,1270961) group by PARENT_METER_ID
//      start time is min time
//      end time = max time+ interval
//        long startTime = 1564646400000L;
//        long endTime = 1581667200000L;
        long interval = 3600000;

        String checkGamParent="1273322,1270982,1270992,1270961";
        String[] checkGamParentList = checkGamParent.split(",");
        List<MLAnomalyEvent> eventList = new LinkedList<MLAnomalyEvent>();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        while(startTime<endTime)
        {
//          SELECT DISTINCT PREDICTION_LOG_MODULEID from ML WHERE MODEL_PATH='checkGam1' and ORGID=?
            long checkGamModuleid = 55443;
//          SELECT DISTINCT PREDICTION_LOG_MODULEID from ML WHERE MODEL_PATH='ratioCheck' and ORGID=?   
            long checkratioModuleid1 = 55445;
//          long checkratioModuleid2 = 77887; // IF there is more than one set in ratio hierarchy [1,2,3,4] , [4,5,6,7]

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

            List<Map<String, Object>> props = getData(checkratio1Fields,checkRatio1Module,startTime, startTime+360000,"1273322");
            for(Map<String,Object> prop : props)
            {
                ratio1data.put("1270982_ratio",prop.get("1270982_ratio"));
                ratio1data.put("1270992_ratio",prop.get("1270992_ratio"));
                ratio1data.put("1270961_ratio",prop.get("1270961_ratio"));
            }
            LOGGER.info("ratio1data length : "+ratio1data.length());
            if(ratio1data.length()>0)
            {
                JSONObject data = checkGamData.get("1273322");
                Double actualValue = (Double)data.get("actualValue");
                Double adjustedUpperBound = (Double)data.get("adjustedUpperBound");
                if(actualValue>adjustedUpperBound)
                {   // ML ID
                	// select ML_ID from ML_Variables M inner JOIN Modules ms ON M.MODULEID=ms.MODULEID WHERE ms.NAME='energydata' AND IS_SOURCE=1 AND M.ORGID=? and M.PARENT_ID=? order by ID desc LIMIT 1
                    MLAnomalyEvent parentEvent = generateMLAnomalyEvent(Long.parseLong("1273322"),actualValue,adjustedUpperBound,startTime,134);

                    eventList.add(parentEvent);

                    if(ratio1data.has("1270982_ratio"))
                    {
                        data = checkGamData.get("1270982");
                        MLAnomalyEvent event = generateRCAEvent(Long.parseLong("1270982"),(Double)data.get("actualValue"),
                                        (Double)data.get("adjustedUpperBound"),startTime,135,(Double)ratio1data.get("1270982_ratio"),parentEvent);
                        eventList.add(event);
                    }
                    if(ratio1data.has("1270992_ratio"))
                    {
                        data = checkGamData.get("1270992");
                        MLAnomalyEvent event = generateRCAEvent(Long.parseLong("1270992"),(Double)data.get("actualValue"),
                                        (Double)data.get("adjustedUpperBound"),startTime,136,(Double)ratio1data.get("1270992_ratio"),parentEvent);
                        eventList.add(event);
                    }
                    if(ratio1data.has("1270961_ratio"))
                    {
                        data = checkGamData.get("1270961");
                        MLAnomalyEvent event = generateRCAEvent(Long.parseLong("1270961"),(Double)data.get("actualValue"),
                                        (Double)data.get("adjustedUpperBound"),startTime,137,(Double)ratio1data.get("1270961_ratio"),parentEvent);
                        eventList.add(event);
                    }
                }
            }
            else
            { //ML ID
                MLAnomalyEvent event =checkAndGenerateMLAnomalyEvent(Long.parseLong("1273322"),checkGamData.get("1273322"),startTime,134);
                if(event!=null)
                {
                    eventList.add(event);
                }

                event =checkAndGenerateMLAnomalyEvent(Long.parseLong("1270982"),checkGamData.get("1270982"),startTime,135);
                if(event!=null)
                {
                    eventList.add(event);
                }

                event =checkAndGenerateMLAnomalyEvent(Long.parseLong("1270992"),checkGamData.get("1270992"),startTime,136);
                if(event!=null)
                {
                    eventList.add(event);
                }
            	event =checkAndGenerateMLAnomalyEvent(Long.parseLong("1270961"),checkGamData.get("1270961"),startTime,137);
                if(event!=null)
                {
                    eventList.add(event);
                }
            }
                   
            startTime = startTime+interval;
            LOGGER.info("eventList size : "+eventList.size());
        }
		if(!eventList.isEmpty()){
			addEventChain(eventList);
		}
    
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
        ResourceContext resource = ResourceAPI.getResource(resourceID);
        event.setEventMessage("Anomaly Detected");
        event.setDescription(message);
        event.setResource(resource);
        event.setActualValue(actualValue);
        event.setAdjustedUpperBoundValue(adjustedUpperBound);
        event.setSeverityString("Minor");
        event.setReadingTime(ttime);
        event.setCreatedTime(ttime);
      //select * from Fields WHERE ORGID=? and NAME='totalEnergyConsumptionDelta'
        event.setEnergyDataFieldid(541054);
      //select FIELDID from Fields F INNER JOIN ML ml ON ml.PREDICTION_LOG_MODULEID = F.MODULEID WHERE ml.ID in (select ML_ID from ML_Variables M inner JOIN Modules ms ON M.MODULEID=ms.MODULEID WHERE ms.NAME='energydata' AND IS_SOURCE=1 AND M.ORGID=? and M.PARENT_ID=?)  and NAME='adjustedUpperBound'
        event.setUpperAnomalyFieldid(764441);
        event.setmlid(mlid);
        event.setType(MLAlarmOccurenceContext.MLAnomalyType.Anomaly);
        event.setSiteId(resource.getSiteId());
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
        ResourceContext resource = ResourceAPI.getResource(assetID);
        event.setEventMessage(message);
        event.setDescription("Anomaly cleared");
        event.setResource(resource);
        event.setSeverityString(FacilioConstants.Alarm.CLEAR_SEVERITY);
        event.setReadingTime(ttime);
        event.setCreatedTime(ttime);
        //select FIELDID from Fields WHERE ORGID=? and NAME='totalEnergyConsumptionDelta'
        event.setEnergyDataFieldid(541054);
        //select FIELDID from Fields F INNER JOIN ML ml ON ml.PREDICTION_LOG_MODULEID = F.MODULEID WHERE ml.ID in (select ML_ID from ML_Variables M inner JOIN Modules ms ON M.MODULEID=ms.MODULEID WHERE ms.NAME='energydata' AND IS_SOURCE=1 AND M.ORGID=? and M.PARENT_ID=?)  and NAME='adjustedUpperBound'
        event.setUpperAnomalyFieldid(764441);
        event.setmlid(mlid);
        event.setSiteId(resource.getSiteId());
        return event;
    }
    
    private void addEventChain(List<MLAnomalyEvent> eventList) throws Exception
    {
    	FacilioContext context = new FacilioContext();
        context.put(EventConstants.EventContextNames.EVENT_LIST,eventList);
        Chain chain = TransactionChainFactory.getV2AddEventChain();
        chain.execute(context);
    }
}

                                                                                                                                
                                                                                                                                