package com.facilio.services.kafka;

import java.util.Properties;

import com.facilio.aws.util.FacilioProperties;

public class KafkaUtil {

	public static void setKafkaAuthProps(Properties props) {
		if (FacilioProperties.getKafkaAuthMode().equalsIgnoreCase("sasl_ssl")) {
            String username = FacilioProperties.getKafkaSaslUsername();
            String password = FacilioProperties.getKafkaSaslPassword();
            String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
            String jaasCfg = String.format(jaasTemplate, username, password);
            props.put("security.protocol", "SASL_SSL");
            props.put("sasl.mechanism", "SCRAM-SHA-512");
            props.put("sasl.jaas.config", jaasCfg);
		}
	}
}
