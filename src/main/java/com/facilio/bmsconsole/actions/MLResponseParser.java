package com.facilio.bmsconsole.actions;

import java.time.ZoneId;
import java.util.List;

import com.facilio.services.factory.FacilioFactory;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.MLUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;
import com.opensymphony.xwork2.ActionSupport;

public class MLResponseParser extends ActionSupport 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static org.apache.log4j.Logger log = LogManager.getLogger(MLResponseParser.class.getName());
	
	
	
	public String parseResponse()
	{
		try
		{
			log.info("ML ID and Result are "+ml_id+":::"+result+"::"+orgid+"::"+error);
//			if(error==null || error.isEmpty())
//			{
				AccountUtil.setCurrentAccount(orgid);
				List<MLContext> mlContextList = MLUtil.getMLContext(ml_id);
				if(mlContextList.isEmpty())
				{
					log.error("No ML Context present for "+ml_id);
					return ERROR;
				}
				MLContext mlContext = mlContextList.get(0);
				mlContext.setResult(result);
				mlContext.setPredictionTime(predictedtime);
				
				Context context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.ML, mlContext);
				FacilioChain c = FacilioChainFactory.addMLReadingChain();
				c.execute(context);
//			}
//			else
//			{
//				sendErrorMail();
//			}
			
		}
		catch(Exception e)
		{
			log.fatal("Exception while parsing mlContext"+ml_id, e);
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	private void sendErrorMail()
	{
		try
		{
			JSONObject json = new JSONObject();
			json.put("sender", "mlerror@facilio.com");
			json.put("to", "ai@facilio.com");
			json.put("subject", orgid+" - "+ml_id);
			
			StringBuilder body = new StringBuilder()
									.append(error)
									.append("\n\nInfo : \n--------\n")
									.append("\n Org Time : ").append(DateTimeUtil.getDateTime())
									.append("\n Indian Time : ").append(DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata")))
									.append("\n\nMsg : ")
									.append(error)
									.append("\n\nOrg Info : \n--------\n")
									.append(orgid)
									;
			json.put("message", body.toString());
			
			FacilioFactory.getEmailClient().sendEmail(json);
		}
		catch(Exception e)
		{
			log.fatal("Error while sending mail ",e);
		}
	}
	
	String result;
	long ml_id;
	long orgid;
	long predictedtime;
	String error;
	public void setResult(String result)
	{
		this.result = result;
	}
	
	public String getResult()
	{
		return result;
	}
	
	public void setMl_id(long ml_id)
	{
		this.ml_id = ml_id;
	}
	public long getMl_id()
	{
		return ml_id;
	}
	public long getOrgid()
	{
		return orgid;
	}
	public void setOrgid(long orgid)
	{
		this.orgid = orgid;
	}
	public long getPredictedtime()
	{
		return predictedtime;
	}
	public void setPredictedtime(long predictedtime)
	{
		this.predictedtime = predictedtime;
	}
	
	public void setError(String error)
	{
		this.error = error;
	}
	public String getError()
	{
		return error;
	}

}
