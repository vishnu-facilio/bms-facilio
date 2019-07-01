package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.MLUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class MLResponseParser extends ActionSupport 
{
	private static org.apache.log4j.Logger log = LogManager.getLogger(MLResponseParser.class.getName());
	
	public String parseResponse()
	{
		try
		{
			log.info("ML ID and Result are "+mlID+":::"+result);
			List<MLContext> mlContextList = MLUtil.getMLContext(mlID);
			if(mlContextList.isEmpty())
			{
				log.error("No ML Context present for "+mlID);
				return ERROR;
			}
			MLContext mlContext = mlContextList.get(0);
			mlContext.setResult(result);
			
			Context context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ML, mlContext);
			Chain c = FacilioChainFactory.addMLReadingChain();
			c.execute(context);
			
		}
		catch(Exception e)
		{
			log.fatal("Exception while parsing mlContext"+mlID, e);
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	String result;
	long mlID;
	public void setResult(String result)
	{
		this.result = result;
	}
	
	public String getResult()
	{
		return result;
	}
	
	public void setMlid(long mlID)
	{
		this.mlID = mlID;
	}
	public long getMlid()
	{
		return mlID;
	}

}
