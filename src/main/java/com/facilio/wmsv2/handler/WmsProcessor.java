package com.facilio.wmsv2.handler;

import com.facilio.ims.util.TopicUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.filters.BaseFilter;
import com.facilio.wmsv2.filters.WMSFilter;
import com.facilio.wmsv2.message.TopicHandler;
import com.facilio.wmsv2.message.WebMessage;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.*;

@Log4j
public class WmsProcessor {

    private final List<WmsHandler> handlers = new ArrayList<>();
    private final List<BaseFilter> filters = new ArrayList<>();

    private static String WMS_TOPIC_CONF = "conf/fms/wmsTopicInfo.yml";

    private static WmsProcessor instance = new WmsProcessor();
    private WmsProcessor() {
        initialize();
    }

    private void initialize() {
        try  {
            loadSystemHandlers();
            Map<String, Object> json = FacilioUtil.loadYaml(WMS_TOPIC_CONF);
            List<Map<String, Object>> wmsTopics = (List<Map<String, Object>>) json.get("topics");
            for(Map<String, Object> topic : wmsTopics) {
                String handlerStr = (String) topic.getOrDefault("handler", DefaultHandler.class.getName());
                Class<?> clazz = Class.forName(handlerStr);
                if (WmsHandler.class.isAssignableFrom(clazz)) {
                    Object origTopic = topic.get("topic");
                    String[] topics;
                    if(List.class.isInstance(origTopic)) {
                        List<String> multiTopics = (List<String>) origTopic;
                        topics = multiTopics.toArray(new String[multiTopics.size()]);
                    } else {
                        topics = ((String) origTopic).split(",");
                    }

                    WmsHandler handler = (WmsHandler) clazz.newInstance();
                    handler.setTopics(topics);
                    handler.setPriority((Integer) topic.getOrDefault("priority", TopicHandler.DEFAULT.priority));
                    handler.setDeliverTo(TopicHandler.DELIVER_TO.valueOf((String) topic.getOrDefault("deliverTo", TopicHandler.DEFAULT.deliverTo)));

                    handlers.add(handler);

                }
            }
//
            handlers.sort(Comparator.comparingInt(WmsHandler::getPriority));

            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .forPackages("com.facilio.wmsv2.filters")
                    .filterInputsBy(new FilterBuilder().includePackage("com.facilio.wmsv2.filters"))
                    .setScanners(new TypeAnnotationsScanner()));

//            Reflections reflections = new Reflections(ClasspathHelper.forPackage("com.facilio.wmsv2.filters"));
            Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(WMSFilter.class);
            if (CollectionUtils.isNotEmpty(typesAnnotatedWith)) {
                for (Class clazz : typesAnnotatedWith) {
                    try {
                        if (BaseFilter.class.isAssignableFrom(clazz)) {
                            WMSFilter annotation = (WMSFilter) clazz.getAnnotation(WMSFilter.class);
                            BaseFilter filter = (BaseFilter) clazz.newInstance();
                            filter.setPriority(annotation.priority());
                            filters.add(filter);
                        } else {
                            // log
                        }
                    } catch (InstantiationException | IllegalAccessException ex) {
                        // log
                    }
                }
            }
            filters.sort(Comparator.comparingInt(BaseFilter::getPriority));
        } catch (Exception e) {
            LOGGER.error("Error occured while loading wmsTopicInfo conf file at "+ WMS_TOPIC_CONF, e);
        }
    }

    private void loadSystemHandlers() {
        addSystemHandlers(new SubscribeHandler(), Topics.System.subscribe, Topics.System.unsubscribe);
        addSystemHandlers(new PingHandler(), Topics.System.ping);
        addSystemHandlers(new PushHandler(), Topics.System.push);
    }

    private void addSystemHandlers(WmsHandler handler, String... topics) {
        handler.setTopics(topics);
        handler.setPriority(-10);
        handlers.add(handler);
    }


    public static WmsProcessor getInstance() {
        return instance;
    }



    public WmsHandler getHandler(String topic) {
        for (WmsHandler baseHandler : handlers) {
            if (TopicUtil.matchTopic(topic, baseHandler.getTopics())) {
                return baseHandler;
            }
        }
        return null;
    }

    public WebMessage filterIncomingMessage(WebMessage message) {
        for (BaseFilter filter : filters) {
            message = filter.incoming(message);
            if (message == null) {
                break;
            }
        }
        return message;
    }

    public WebMessage filterOutgoingMessage(WebMessage message) {
        for (BaseFilter filter : filters) {
            message = filter.outgoing(message);
            if (message == null) {
                break;
            }
        }
        return message;
    }

}