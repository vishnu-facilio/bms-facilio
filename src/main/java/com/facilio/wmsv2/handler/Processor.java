package com.facilio.wmsv2.handler;

import com.facilio.wmsv2.filters.BaseFilter;
import com.facilio.wmsv2.filters.WMSFilter;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;
import com.facilio.wmsv2.util.TopicUtil;
import org.apache.commons.collections.CollectionUtils;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class Processor {

    public static final Processor instance = new Processor();
    private final List<BaseHandler> handlers = new ArrayList<>();
    private final List<BaseFilter> filters = new ArrayList<>();

    private Processor() {
        initialize();
    }

    private void initialize() {
        Reflections reflections = new Reflections(ClasspathHelper.forPackage("com.facilio.wmsv2.handler"));
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(TopicHandler.class);
        if (CollectionUtils.isNotEmpty(typesAnnotatedWith)) {
            for (Class clazz : typesAnnotatedWith) {
                try {
                    if (BaseHandler.class.isAssignableFrom(clazz)) {
                        TopicHandler annotation = (TopicHandler) clazz.getAnnotation(TopicHandler.class);
                        String[] topics = annotation.topic();
                        BaseHandler handler = (BaseHandler) clazz.newInstance();
                        handler.setPriority(annotation.priority());
                        handler.setTopics(topics);
                        handler.setDeliverTo(annotation.deliverTo());

                        handlers.add(handler);
                    }
                    else {
                        // log
                    }
                } catch (InstantiationException | IllegalAccessException ex) {
                    // log

                }
            }
        }
        handlers.sort(Comparator.comparingInt(BaseHandler::getPriority));


        reflections = new Reflections(ClasspathHelper.forPackage("com.facilio.wmsv2.filters"));
        typesAnnotatedWith = reflections.getTypesAnnotatedWith(WMSFilter.class);
        if (CollectionUtils.isNotEmpty(typesAnnotatedWith)) {
            for (Class clazz : typesAnnotatedWith) {
                try {
                    if (BaseFilter.class.isAssignableFrom(clazz)) {
                        WMSFilter annotation = (WMSFilter) clazz.getAnnotation(WMSFilter.class);
                        BaseFilter filter = (BaseFilter) clazz.newInstance();
                        filter.setPriority(annotation.priority());

                        filters.add(filter);
                    }
                    else {
                        // log
                    }
                } catch (InstantiationException | IllegalAccessException ex) {
                    // log
                }
            }
        }
        filters.sort(Comparator.comparingInt(BaseFilter::getPriority));
    }

    public static Processor getInstance() {
        return instance;
    }

//    public void processIncomingMessage(Message message) {
//        if (message == null) {
//            return;
//        }
//
//        String topic = message.getTopic();
//        for (BaseHandler baseHandler : handlers) {
//            if (TopicUtil.matchTopic(baseHandler.getTopic(), topic)) {
//                message = baseHandler.processIncomingMessage(message);
//
//                if (message == null) {
//                    // don't process further
//                    break;
//                }
//            }
//        }
//    }

    public BaseHandler getHandler(String topic) {
        for (BaseHandler baseHandler : handlers) {
            if (TopicUtil.matchTopic(topic, baseHandler.getTopics())) {
                return baseHandler;
            }
        }
        return null;
    }

    public Message filterIncomingMessage(Message message) {
        for (BaseFilter filter : filters) {
            message = filter.incoming(message);
            if (message == null) {
                break;
            }
        }
        return message;
    }

    public Message filterOutgoingMessage(Message message) {
        for (BaseFilter filter : filters) {
            message = filter.outgoing(message);
            if (message == null) {
                break;
            }
        }
        return message;
    }
}
