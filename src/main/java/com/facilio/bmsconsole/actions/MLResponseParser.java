package com.facilio.bmsconsole.actions;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.struts2.ServletActionContext;

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
			HttpServletRequest request = ServletActionContext.getRequest();
			log.info("Request details are "+request.getParameterMap().toString());
			Map<String, String[]> data = request.getParameterMap();
			for(String key:data.keySet())
			{
				log.info("Parameter name is "+key+"::"+data.get(key));
			}
			log.info("Request body "+request.getContentType()+"::"+request.getContentLength());
			StringWriter writer = new StringWriter();
			IOUtils.copy(request.getInputStream(), writer, StandardCharsets.UTF_8);
			String theString = writer.toString();
			log.info("Input Stream data is "+theString);
			
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
