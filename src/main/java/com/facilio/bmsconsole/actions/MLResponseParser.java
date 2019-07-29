package com.facilio.bmsconsole.actions;

import java.time.ZoneId;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.MLUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;
import com.opensymphony.xwork2.ActionSupport;

public class MLResponseParser extends ActionSupport 
{
	private static org.apache.log4j.Logger log = LogManager.getLogger(MLResponseParser.class.getName());
	
	
	
	public String parseResponse()
	{
		try
		{
			log.info("ML ID and Result are "+ml_id+":::"+result+"::"+orgid+"::"+error);
			if(error==null || error.isEmpty())
			{
				AccountUtil.setCurrentAccount(orgid);
				List<MLContext> mlContextList = MLUtil.getMLContext(ml_id);
				if(mlContextList.isEmpty())
				{
					log.error("No ML Context present for "+ml_id);
					return ERROR;
				}
				MLContext mlContext = mlContextList.get(0);
				mlContext.setResult(result);
				
				Context context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.ML, mlContext);
				Chain c = FacilioChainFactory.addMLReadingChain();
				c.execute(context);
			}
			else
			{
				AwsUtil.sendErrorMail(orgid,ml_id,error);
			}
			
		}
		catch(Exception e)
		{
			log.fatal("Exception while parsing mlContext"+ml_id, e);
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	String result;
	long ml_id;
	long orgid;
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
	
	public void setError(String error)
	{
		this.error = error;
	}
	public String getError()
	{
		return error;
	}

}
