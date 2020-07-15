package com.facilio.bmsconsole.jobs;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Chain;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.MultiVariateAnomalyEvent;
import com.facilio.bmsconsole.context.ReadingContext;
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
   private static final Logger LOGGER = Logger.getLogger(AddAnomalyEventJob.class.getName());


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
        while(startTime<endTime)
        {
        	long outlierModuleId = Long.parseLong(props.get("outlierModuleId").toString());
            FacilioModule outlierModule = modBean.getModule(outlierModuleId);
            List<FacilioField> outlierFields = modBean.getAllFields(outlierModule.getName());
            Hashtable<String,JSONObject> data = new Hashtable<String,JSONObject>();
            List<Map<String,Object>> outlierData = getData(outlierFields,outlierModule,startTime, startTime+interval,assetId);
            MultiVariateAnomalyEvent event = new MultiVariateAnomalyEvent();
            for(Map<String,Object> eachRecord : outlierData)
            {
                if(eachRecord.get("outlier").toString().equals("-1") )
                {
                	JSONObject listOfVarFieldId = new JSONObject();
                    JSONObject neighbourCount = new JSONObject();
                    JSONObject listOfVarRatioFields = new JSONObject();

                	long ratioModuleId = Long.parseLong(props.get("ratioModuleId").toString());
                	FacilioModule ratioModule = modBean.getModule(ratioModuleId);
                    List<FacilioField> ratioFields = modBean.getAllFields(ratioModule.getName());
                    List<Map<String,Object>> propsRatioData = getData(ratioFields,ratioModule,startTime, startTime+10000,assetId);
                    String causingVarFieldId = new String();
                    float maxValue = (float) 0.0;
                    for(Object key : varFieldId.keySet())
                    {
                        if(key.toString().contains("ratio"))
                        {
                            float currentValue = (float)propsRatioData.get(0).get(key);
                            listOfVarRatioFields.put(varFieldId.get(key).toString(),currentValue);
                            if(currentValue >= maxValue)
                            {
                                causingVarFieldId = varFieldId.get(key).toString();
                                maxValue = currentValue;
                            }
                        }
                        else
                        {
                            listOfVarFieldId.put(varFieldId.get(key).toString(),eachRecord.get(key.toString()));
                        }
                    }
                    neighbourCount.put("pctNeighbour" , eachRecord.get("pctNeighbour"));
                    neighbourCount.put("optPercentage" , eachRecord.get("optPercentage"));

                    event = generateMultiVariateAnomalyEvent(mlId,assetId,neighbourCount,listOfVarFieldId,listOfVarRatioFields,causingVarFieldId,props.get("multivariateAnomalyID").toString(),Long.parseLong(props.get("ttime").toString()));
                }
                else 
                {
                    event = clearMultiVariateAnomalyEvent(mlId,assetId,Long.parseLong(props.get("ttime").toString()));
                }
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
            //ResourceContext resource = ResourceAPI.getResource(resourceID);
            event.setEventMessage("Anomaly Detected");
            event.setDescription(message);
            //event.setResource(resource);

            event.setSeverityString("Minor");
            //event.setReadingTime(ttime);
            event.setCreatedTime(ttime);
            event.setCausingVariableId( Long.parseLong(causingVarFieldId));
            event.setListOfVarFieldsStr(listOfVarFieldId.toString());
            event.setNeighbourCountStr(neighbourCount.toString());
            event.setMultivariateAnomalyId(Long.parseLong(causingVarFieldId));
            event.setOutlier(true);
            event.setRatioStr(listOfVarRatioFields.toString());
            //event.setType(MLAlarmOccurenceContext.MLAnomalyType.Anomaly);
            event.setEndDate(ttime);
            event.setStartDate(ttime-(90*24*60*60*1000));
            return event;
     }
    
     private MultiVariateAnomalyEvent clearMultiVariateAnomalyEvent(String mlId,String assetId,long ttime) throws Exception
     {
         String message = "Anomaly Cleared";
         MultiVariateAnomalyEvent event = new MultiVariateAnomalyEvent();
         //ResourceContext resource = ResourceAPI.getResource(assetID);
         event.setEventMessage(message);
         event.setDescription("Anomaly cleared");
         //event.setResource(resource);
         event.setSeverityString(FacilioConstants.Alarm.CLEAR_SEVERITY);
         event.setCreatedTime(ttime);
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

