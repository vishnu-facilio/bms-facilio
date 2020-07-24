package com.facilio.bmsconsole.jobs;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Chain;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.json.simple.parser.JSONParser;

public class MultivariateAnomalyEventJob extends FacilioJob
{
   private static final Logger LOGGER = Logger.getLogger(MultivariateAnomalyEventJob.class.getName());


	@Override
	public void execute(JobContext jc) throws Exception 
	{
        LOGGER.info("generating Multivariate anomaly Alarm ML started ");
        try
		{
            LOGGER.info("generating Multivariate anomaly Alarm ML started ");
            org.json.simple.JSONObject props=BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());

            generateAnomalyEvents(props);
            LOGGER.info("generating Alarm ML finished ");
    	}
		catch(Exception e)
		{
    		LOGGER.fatal("ERROR in MultivariateAnomalyEventJob"+e);
    		throw e;
    	}
	}
	private void generateAnomalyEvents(org.json.simple.JSONObject props) throws Exception
    {	
        long startTime = Long.parseLong(props.get("startTime").toString());
		long endTime = 	Long.parseLong(props.get("endTime").toString());
        long interval = Long.parseLong(props.get("interval").toString());
        String assetId= props.get("assetId").toString();
        String mlId= props.get("mlId").toString();
        JSONParser parser = new JSONParser();
        String varFieldIdStr = props.get("varFieldId").toString();
        org.json.simple.JSONObject varFieldId = (org.json.simple.JSONObject) parser.parse(varFieldIdStr);

        List<MultiVariateAnomalyEvent> eventList = new LinkedList<MultiVariateAnomalyEvent>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        while(startTime<=endTime)
        {
            JSONObject listOfVarFieldId = new JSONObject();
            JSONObject neighbourCount = new JSONObject();
            JSONObject listOfVarRatioFields = new JSONObject();

        	long outlierModuleId = Long.parseLong(props.get("outlierModuleId").toString());
            FacilioModule outlierModule = modBean.getModule(outlierModuleId);
            List<FacilioField> outlierFields = modBean.getAllFields(outlierModule.getName());
            Hashtable<String,JSONObject> data = new Hashtable<String,JSONObject>();
            List<Map<String,Object>> outlierData = getData(outlierFields,outlierModule,startTime, startTime+interval,assetId);
            MultiVariateAnomalyEvent event = new MultiVariateAnomalyEvent();
            for(Map<String,Object> eachRecord : outlierData)
            {
                LOGGER.info("each record outlier " + eachRecord.get("outlier").toString());
                if(eachRecord.get("outlier").toString().equals("-1") )
                {
                	long ratioModuleId = Long.parseLong(props.get("ratioModuleId").toString());
                	FacilioModule ratioModule = modBean.getModule(ratioModuleId);
                    List<FacilioField> ratioFields = modBean.getAllFields(ratioModule.getName());
                    List<Map<String,Object>> propsRatioData = getData(ratioFields,ratioModule,startTime, startTime+interval,assetId);
                    LOGGER.info("ratioData " + propsRatioData.toString());
                    String causingVarFieldId = new String();
                    Double maxValue = 0.0;
                    for(Object key : varFieldId.keySet())
                    {
                        if(key.toString().contains("ratio"))
                        {
                            LOGGER.info("listOfVarRatioFields "+ key.toString());
                            Double currentValue = (Double) propsRatioData.get(0).get(key);
                            listOfVarRatioFields.put(varFieldId.get(key).toString(),currentValue);
                            if(currentValue >= maxValue)
                            {
                                causingVarFieldId = varFieldId.get(key).toString();
                                maxValue = currentValue;
                            }
                        }
                        else
                        {
                            LOGGER.info("listOfVarFieldId "+ key.toString()) ;
                            listOfVarFieldId.put(varFieldId.get(key).toString(),eachRecord.get(key.toString()));
                        }
                    }
                    neighbourCount.put("pctNeighbour" , eachRecord.get("pctNeighbour"));
                    neighbourCount.put("optPercentage" , eachRecord.get("optPercentage"));
                    LOGGER.info("generateMultivariateAnomalyEvent "+ mlId + "assetId "+ assetId + " neighbourCount "+neighbourCount +" listOfVarFieldId " +listOfVarFieldId + " listOfVarRatioFields " +listOfVarRatioFields+ " causingVarFieldId " +causingVarFieldId + " multivariateAnomalyID "+ props.get("multivariateAnomalyID").toString() + " ttime "+Long.parseLong(eachRecord.get("ttime").toString()));
                    event = generateMultiVariateAnomalyEvent(mlId,assetId,neighbourCount,listOfVarFieldId,listOfVarRatioFields,causingVarFieldId,props.get("multivariateAnomalyID").toString(),Long.parseLong(eachRecord.get("ttime").toString()));


                    LOGGER.info("MultiVariateAnomaly event created");

                }
                else 
                {
                    event = clearMultiVariateAnomalyEvent(mlId,assetId,Long.parseLong(eachRecord.get("ttime").toString()),props.get("multivariateAnomalyID").toString(),neighbourCount,listOfVarFieldId,listOfVarRatioFields);
                    LOGGER.info("MultiVariateAnomaly clear event created");
                }

                LOGGER.info("eventDetails outlier - " + event.getOutlier().toString());
                LOGGER.info("eventDetails getListOfVarFieldsStr - " + event.getListOfVarFieldsStr());
                LOGGER.info("eventDetails getNeighbourCountStr - " + event.getNeighbourCountStr());
                LOGGER.info("eventDetails getRatioStr - " + event.getRatioStr());
                LOGGER.info("eventDetails getMessageKey - " + event.getMessageKey());
                LOGGER.info("eventDetails getEventMessage - " + event.getEventMessage());
                LOGGER.info("eventDetails getCausingVariableId - " + event.getCausingVariableId());
                LOGGER.info("eventDetails getStartDate - " + event.getStartDate());
                LOGGER.info("eventDetails getEndDate - " + event.getEndDate());
                LOGGER.info("eventDetails getMultivariateAnomalyId - " + event.getMultivariateAnomalyId());
                LOGGER.info("eventDetails getResource - " + event.getResource());
                LOGGER.info("eventDetails ttime - " + event.getCreatedTime());
                LOGGER.info("eventDetails siteid - " + event.getSiteId());
                LOGGER.info("eventDetails orgid - " + event.getOrgId());
                LOGGER.info("eventDetails moduleid - " + event.getModuleId());
                eventList.add(event);
            }
        startTime = startTime+interval;
        }
        if(!eventList.isEmpty())
        {
            addEventChain(eventList);
        }
    } 
    private List<Map<String, Object>> getData(List<FacilioField> fieldList, FacilioModule module, long startTime, long endTime, String parentID) throws Exception
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

    private MultiVariateAnomalyEvent generateMultiVariateAnomalyEvent(String mlId,String assetId,JSONObject neighbourCount,JSONObject listOfVarFieldId,JSONObject listOfVarRatioFields,String causingVarFieldId, String multiVariateAnomalyId , long ttime) throws Exception
    {
            String message = "data is anomaly due to "+causingVarFieldId;
            MultiVariateAnomalyEvent event = new MultiVariateAnomalyEvent();
            ResourceContext resource = ResourceAPI.getResource(Long.parseLong(assetId));
            event.setEventMessage("Anomaly Detected");
            event.setDescription(message);
            event.setResource(resource);
            event.setMessageKey("MultiVariateAnomaly_" + assetId +"_" + multiVariateAnomalyId);
            event.setSeverityString("Minor");
            event.setCreatedTime(ttime);
            event.setCausingVariableId( Long.parseLong(causingVarFieldId));
            event.setListOfVarFieldsStr(listOfVarFieldId.toString());
            event.setNeighbourCountStr(neighbourCount.toString());
            event.setMultivariateAnomalyId(Long.parseLong(multiVariateAnomalyId));
            event.setOutlier(true);
            event.setRatioStr(listOfVarRatioFields.toString());
            //event.setType(MLAlarmOccurenceContext.MLAnomalyType.Anomaly);
            event.setEndDate(ttime);
            event.setStartDate(ttime-(90*24*60*60*1000));
            event.setSiteId(resource.getSiteId());
            event.setEventType(BaseAlarmContext.Type.MULTIVARIATE_ANOMALY_ALARM.getIndex());
            return event;
     }
    
     private MultiVariateAnomalyEvent clearMultiVariateAnomalyEvent(String mlId,String assetId,long ttime ,String multivariateAnomalyId,JSONObject neighbourCount,JSONObject listOfVarFieldId,JSONObject listOfVarRatioFields) throws Exception
     {
         String message = "Anomaly Cleared";
         MultiVariateAnomalyEvent event = new MultiVariateAnomalyEvent();
         ResourceContext resource = ResourceAPI.getResource(Long.parseLong(assetId));
         event.setEventMessage(message);
         event.setDescription("Anomaly cleared");
         event.setMessageKey("MultiVariateAnomaly_" + assetId +"_" + multivariateAnomalyId);
         event.setResource(resource);
         event.setSeverityString(FacilioConstants.Alarm.CLEAR_SEVERITY);
         event.setCreatedTime(ttime);
         event.setOutlier(false);
         event.setStartDate(ttime-(90*24*60*60*1000));
         event.setEndDate(ttime);
         event.setMultivariateAnomalyId(Long.parseLong(multivariateAnomalyId));
         event.setListOfVarFieldsStr(listOfVarFieldId.toString());
         event.setRatioStr(listOfVarRatioFields.toString());
         event.setNeighbourCountStr(neighbourCount.toString());
         event.setSiteId(resource.getSiteId());
         event.setEventType(BaseAlarmContext.Type.MULTIVARIATE_ANOMALY_ALARM.getIndex());

         return event;
     }
     private void addEventChain(List<MultiVariateAnomalyEvent> eventList) throws Exception
     {
         FacilioContext context = new FacilioContext();
         context.put(EventConstants.EventContextNames.EVENT_LIST,eventList);
         Chain chain = TransactionChainFactory.getV2AddEventChain(true);
         chain.execute(context);
     }
}

