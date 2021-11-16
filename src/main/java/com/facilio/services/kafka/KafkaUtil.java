package com.facilio.services.kafka;

import java.util.Properties;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.queue.source.KafkaMessageSource;

import lombok.NonNull;
import lombok.extern.log4j.Log4j;

@Log4j
public class KafkaUtil {
	
	public static void setKafkaAuthProps(@NonNull Properties props, @NonNull KafkaMessageSource source) {
		if (source.isAuthEnabled()) {
            String username = source.getUserName();
            String password = source.getPassword();
            if (source.getAuthMode().equals("sasl_ssl")) {
            	String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
            	String jaasCfg = String.format(jaasTemplate, username, password);
            	props.put("security.protocol", "SASL_SSL");
            	props.put("sasl.mechanism", "SCRAM-SHA-512");
            	props.put("sasl.jaas.config", jaasCfg);
            }
		}
	}
}
