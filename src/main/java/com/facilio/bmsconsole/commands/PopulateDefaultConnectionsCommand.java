package com.facilio.bmsconsole.commands;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FieldUtil;

public class PopulateDefaultConnectionsCommand extends FacilioCommand{
	
	private static final Logger LOGGER = LogManager.getLogger(PopulateDefaultConnectionsCommand.class.getName());

	private static final String DEFAULT_CONNECTIONS = "conf/defaultConnections.yml";
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Yaml yaml = new Yaml();
        List<Object> connections = null;
        try(InputStream inputStream = PopulateDefaultConnectionsCommand.class.getClassLoader().getResourceAsStream(DEFAULT_CONNECTIONS);) {
        	
        	
        	connections = yaml.load(inputStream);
        	addConnections(connections);
        }
        catch (Exception e) {
            LOGGER.error("Error occurred while reading default connection conf file. "+e.getMessage(), e);
        }
		
		return false;
	}
	
	
	private void addConnections(List<Object> connections) throws Exception {
		
		if(connections != null) {
			
			for (Object connection :connections) {
				Map connectionMap = (Map) connection;
				ConnectionContext connectionContext = FieldUtil.getAsBeanFromMap(connectionMap, ConnectionContext.class);
				
				FacilioChain chain = TransactionChainFactory.getAddConnectionChain();
				
				FacilioContext context = chain.getContext();
				
				context.put(FacilioConstants.ContextNames.CONNECTION, connectionContext);
				
				chain.execute();
			}
		}
	}
	
}
