package com.facilio.services.kafka;

import java.util.Properties;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.queue.source.KafkaMessageSource;
import com.facilio.queue.source.MessageSource;

import lombok.NonNull;
import lombok.extern.log4j.Log4j;

@Log4j
public class KafkaUtil {
	
	public static void setKafkaAuthProps(@NonNull Properties props, @NonNull KafkaMessageSource source) {
		source.setAuthProps(props);
	}
	
	public static boolean parseData(MessageSource source) {
		return source instanceof KafkaMessageSource && ((KafkaMessageSource)source).shouldParseData();
	}
}
