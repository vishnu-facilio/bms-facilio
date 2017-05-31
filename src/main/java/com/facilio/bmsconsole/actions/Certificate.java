package com.facilio.bmsconsole.actions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.facilio.aws.util.AwsUtil;
import com.opensymphony.xwork2.ActionSupport;

public class Certificate extends ActionSupport
{
	private static Logger logger = Logger.getLogger(Certificate.class.getName());
	
	public String execute()
	{
		HttpServletResponse response = ServletActionContext.getResponse();
		String resource = ServletActionContext.getRequest().getParameter("resource");
		System.out.println(resource);
		response.setContentType("application/octet-stream");
		
		ServletOutputStream out = null;
		try 
		{
			out = response.getOutputStream();
			if(resource != null && "privatekey".equals(resource))
			{
				response.setHeader("Content-Disposition","attachment;filename=device.private.key");
				out.write(AwsUtil.getCertificateResult().getKeyPair().getPrivateKey().getBytes());	//TODO
			}
			else
			{
				response.setHeader("Content-Disposition","attachment;filename=device.crt.pem");
				out.write(AwsUtil.getCertificateResult().getCertificatePem().getBytes());	//TODO
			}
		} 
		catch (IOException e) 
		{
			logger.log(Level.SEVERE, "Exception while creating certificate" + e.getMessage(), e);
		}
		finally
		{
	        try 
	        {
	        	out.flush();
				out.close();
			} 
	        catch (IOException e) 
	        {
	        	logger.log(Level.SEVERE, "Exception while closing stream" + e.getMessage(), e);
			}
		}
		return null;
	}
}
