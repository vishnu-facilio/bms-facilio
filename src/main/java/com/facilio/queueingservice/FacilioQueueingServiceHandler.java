package com.facilio.queueingservice;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.queue.source.KafkaMessageSource;
import com.facilio.queue.source.MessageSource;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.queueingservice.services.QueueingService;
import com.facilio.services.procon.processor.FacilioProcessor;

import lombok.extern.log4j.Log4j;

@Log4j
public class FacilioQueueingServiceHandler {
	
	public static void init() throws Exception {
		
		MessageSource source = MessageSourceUtil.getDefaultSource();
		try {
			Set<Class<?>> typesAnnotatedWith = new Reflections(ClasspathHelper.forPackage("com.facilio.queueingservice.services")).getTypesAnnotatedWith(FacilioQueueingServiceAnnotation.class);

			if (CollectionUtils.isNotEmpty(typesAnnotatedWith)) {
	            for (Class queueingServiceClass : typesAnnotatedWith) {
	                if (QueueingService.class.isAssignableFrom(queueingServiceClass)) {
	                	FacilioQueueingServiceAnnotation annotation = (FacilioQueueingServiceAnnotation) queueingServiceClass.getAnnotation(FacilioQueueingServiceAnnotation.class);
	                    
	                	String topic = annotation.topic();
	                    String consumerGroup = annotation.consumerGroup();
	                    
                    	QueueingService queueingService = (QueueingService) queueingServiceClass.getDeclaredConstructor().newInstance();
                        
                    	FacilioProcessor processor = getProcessor(topic,consumerGroup,source,queueingService);
                    	
                        new Thread(processor).start();
	                }
	            }
	        }
		}
		catch(Exception e) {
			LOGGER.error("cannot initialize queueing services ", e);
		}
	}

	private static FacilioProcessor getProcessor(String topic, String consumerGroup, MessageSource source, QueueingService queueingService) {

		switch (source.getType()) {
		case KAFKA:
			FacilioProcessor processor = new FacilioQueueingServiceKafkaProcessor(topic, consumerGroup, (KafkaMessageSource) source, queueingService);
			return processor;
		case MQTT:
			
			break;
		default:
			break;
		}
		return null;
	}
	
}
