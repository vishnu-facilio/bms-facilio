package com.facilio.trigger.util;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.trigger.command.*;
import com.facilio.trigger.config.*;
import com.facilio.trigger.config.annotations.Event;
import com.facilio.trigger.config.annotations.Module;
import com.facilio.v3.V3Builder.V3Config;
import lombok.SneakyThrows;
import org.apache.commons.chain.Command;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class TriggerChainUtil {

    private static final Map<EventType, Supplier<TriggerConfig>> TRIGGER_HANDLER_MAP = new HashMap<>();

    private static void initTriggerHandlerMap() throws Exception{
        Reflections reflections = new Reflections(ClasspathHelper.forPackage("com.facilio.trigger"), new MethodAnnotationsScanner());
        fillEventMap(reflections);
    }

    private static void fillEventMap(Reflections reflections) throws Exception{
        Set<Method> methodsAnnotatedWithModule = reflections.getMethodsAnnotatedWith(Event.class);

        for (Method method: methodsAnnotatedWithModule) {
            if (method.getParameterCount() != 0) {
                continue;
            }

            if (!method.getReturnType().equals(Supplier.class)) {
                continue;
            }

            Event annotation = method.getAnnotation(Event.class);
            EventType eventType = annotation.value();
            if (eventType==null) {
                continue;
            }

            try {
                Supplier<TriggerConfig> config = (Supplier<TriggerConfig>) method.invoke(null, null);
                TRIGGER_HANDLER_MAP.put(eventType, config);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    public static TriggerConfig getTriggerConfig(EventType eventType){
        if (TRIGGER_HANDLER_MAP.isEmpty() ) {
            initTriggerHandlerMap();
        }

        Supplier<TriggerConfig> triggerConfigSupplier = TRIGGER_HANDLER_MAP.get(eventType);
        if (triggerConfigSupplier != null) {
            return triggerConfigSupplier.get();
        }
        return null;
    }

    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }


    private static void addIfNotNull(FacilioChain chain, Command command) {
        if (command != null) {
            chain.addCommand(command);
        }
    }

    public static FacilioChain getTriggerCreateChain(EventType eventType) {
        TriggerConfig triggerConfig = getTriggerConfig(eventType);

        Command afterSaveCommand = null;

        if (triggerConfig != null) {
            TriggerCreateHandler createHandler = triggerConfig.getCreateHandler();
            if (createHandler != null) {
                afterSaveCommand = createHandler.getAfterSaveCommand();
            }
        }

        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ValidateTriggerCommand());
        chain.addCommand(new AddOrUpdateTriggerCommand());
        chain.addCommand(new AddOrUpdateTriggerActionAndRelCommand());
        addIfNotNull(chain, afterSaveCommand);
        return chain;
    }

    public static FacilioChain getTriggerUpdateChain(EventType eventType) {
        TriggerConfig triggerConfig = getTriggerConfig(eventType);

        Command afterUpdateCommand = null;

        if (triggerConfig != null) {
            TriggerUpdateHandler updateHandler = triggerConfig.getUpdateHandler();
            if (updateHandler != null) {
                afterUpdateCommand = updateHandler.getAfterUpdateCommand();
            }
        }

        FacilioChain chain = getDefaultChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.EVENT_TYPE,eventType);
        chain.addCommand(new ValidateTriggerCommand());
        chain.addCommand(new ValidateScheduleTriggerCommand());
        chain.addCommand(new AddOrUpdateTriggerCommand());
        chain.addCommand(new AddOrUpdateTriggerActionAndRelCommand());
        addIfNotNull(chain, afterUpdateCommand);
        return chain;
    }

    public static FacilioChain getTriggerSummaryChain(EventType eventType) {
        TriggerConfig triggerConfig = getTriggerConfig(eventType);

        Command afterFetchCommand = null;
        if(triggerConfig != null){
            TriggerSummaryHandler summaryHandler = triggerConfig.getSummaryHandler();
            if(summaryHandler != null){
                afterFetchCommand = summaryHandler.getAfterFetchCommand();
            }
        }

        FacilioChain chain = getDefaultChain();
        addIfNotNull(chain,afterFetchCommand);
        return chain;
    }

    public static FacilioChain getTriggerDeleteChain(EventType eventType) {
        TriggerConfig triggerConfig = getTriggerConfig(eventType);

        Command afterDeleteCommand = null;
        if(triggerConfig != null){
            TriggerDeleteHandler deleteHandler = triggerConfig.getDeleteHandler();
            if(deleteHandler != null){
                afterDeleteCommand = deleteHandler.getAfterDeleteCommand();
            }
        }

        FacilioChain chain = getDefaultChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.EVENT_TYPE,eventType);
        chain.addCommand(new DeleteTriggerCommand());
        addIfNotNull(chain,afterDeleteCommand);
        return chain;
    }

    public static FacilioChain getTriggerListChain(EventType eventType) {
        TriggerConfig triggerConfig = getTriggerConfig(eventType);

        Command afterfetchCommand = null;
        if(triggerConfig != null){
            TriggerListHandler listHandler = triggerConfig.getTriggerListHandler();
            if(listHandler != null){
                afterfetchCommand = listHandler.getAfteFetchCommand();
            }
        }

        FacilioChain chain = getDefaultChain();
        addIfNotNull(chain,afterfetchCommand);
        return chain;
    }
}
