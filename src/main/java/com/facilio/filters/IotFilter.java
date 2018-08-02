package com.facilio.filters;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.chain.Chain;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.events.constants.EventConstants;

public class IotFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(IotFilter.class.getName());
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @SuppressWarnings("unchecked")
	@Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        StringBuilder builder = new StringBuilder();
        String line = null;
        try {
        		AccountUtil.cleanCurrentAccount();
            BufferedReader reader = request.getReader();
            String[] list = new String[2];
            int i = 0;
            while ((line = reader.readLine()) != null) {
            	 	list[i++] = line;
                builder.append(line);
                builder.append(System.lineSeparator());
            }
            if(builder.length() > 0) {
            		String data = builder.toString();
	        	    if (!data.contains("-OPERATOR COMMAND-") && !data.contains("$WTPING")) {
	        	    		AccountUtil.setCurrentAccount(78L);
	        	    	
		    		    	String source = list[1];
		    	    		String message = list[0].substring(0, list[0].indexOf("::") - 1);
		    	    		String timeStamp = list[0].substring(list[0].indexOf("::") + 3, list[0].indexOf("P:") - 1);
		    	    		
		    	    		JSONObject payload = new JSONObject();
		    	    		payload.put("source", source.trim());
		    	    		payload.put("entity", source.trim());
		    	    		payload.put("message", message.trim());
		    	    		payload.put("timestamp", DateTimeUtil.getTime(timeStamp.trim(), "HH:mm:ss dd/MM/yyyy"));
		    	    		
		    	    		LOGGER.warn(payload.toString());
		    	    		
		    	    		FacilioContext context = new FacilioContext();
		    	    		context.put(EventConstants.EventContextNames.EVENT_PAYLOAD, payload);
		    	    		Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
		    	    		getAddEventChain.execute(context);
		    	    }
            }
        } catch (Exception e) {
        		LOGGER.error("IOTFilter Exception :", e);
        }
        LOGGER.warn(builder.toString());
    }

    @Override
    public void destroy() {

    }
}
