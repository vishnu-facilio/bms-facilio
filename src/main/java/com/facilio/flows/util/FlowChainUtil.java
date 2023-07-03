package com.facilio.flows.util;

import com.facilio.blockfactory.enums.BlockType;
import com.facilio.chain.FacilioChain;
import com.facilio.flows.command.AddFlowTransitionCommand;
import com.facilio.flows.command.UpdateFlowTransitionCommand;
import com.facilio.flows.config.*;
import com.facilio.flows.config.annotations.Block;
import com.facilio.flows.context.FlowTransitionContext;
import org.apache.commons.chain.Command;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class FlowChainUtil {
    private static final Map<BlockType, Supplier<FlowConfig>> FLOW_HANDLER_MAP = new HashMap<>();

    private static void initFlowHandlerMap() {
        Reflections reflections = new Reflections(ClasspathHelper.forPackage("com.facilio.flows"), new MethodAnnotationsScanner());
        Set<Method> methodsAnnotatedWithModule = reflections.getMethodsAnnotatedWith(Block.class);

        for (Method method: methodsAnnotatedWithModule) {
            if (method.getParameterCount() != 0) {
                // skip if method has parameters
                continue;
            }

            if (!method.getReturnType().equals(Supplier.class)) {
                // skip if method return type is other than Supplier
                continue;
            }

            Block annotation = method.getAnnotation(Block.class);
            BlockType blockType = annotation.value();
            if (blockType==null) {
                // Skip if blockType is empty
                continue;
            }

            try {
                Supplier<FlowConfig> config = (Supplier<FlowConfig>) method.invoke(null, null);
                FLOW_HANDLER_MAP.put(blockType, config);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static FlowConfig getFlowConfig(BlockType blockType) {
        if (FLOW_HANDLER_MAP.isEmpty()) {
            initFlowHandlerMap();
        }

        Supplier<FlowConfig> flowConfigSupplier = FLOW_HANDLER_MAP.get(blockType);
        if (flowConfigSupplier != null) {
            return flowConfigSupplier.get();
        }
        return null;
    }

    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain getFlowTransitionCreateChain(BlockType blockType) {
        FlowConfig flowConfig = getFlowConfig(blockType);

        Command beforeSaveCommand = null;
        Command afterSaveCommand = null;

        if (flowConfig != null) {
            FlowTransitionSaveHandler createHandler = flowConfig.getCreateHandler();
            if (createHandler != null) {
                beforeSaveCommand = createHandler.getBeforeSaveCommand();
                afterSaveCommand = createHandler.getAfterSaveCommand();
            }
        }

        FacilioChain chain = getDefaultChain();
        addIfNotNull(chain, beforeSaveCommand);
        chain.addCommand(new AddFlowTransitionCommand());
        addIfNotNull(chain, afterSaveCommand);
        return chain;
    }
    public static FacilioChain getFlowTransitionUpdateChain(BlockType blockType) {
        FlowConfig flowConfig = getFlowConfig(blockType);

        Command beforeUpdateCommand = null;
        Command afterUpdateCommand = null;

        if (flowConfig != null) {
            FlowTransitionUpdateHandler updateHandler = flowConfig.getUpdateHandler();
            if (updateHandler != null) {
                beforeUpdateCommand = updateHandler.getBeforeUpdateCommand();
                afterUpdateCommand = updateHandler.getAfterUpdateCommand();
            }
        }

        FacilioChain chain = getDefaultChain();
        addIfNotNull(chain, beforeUpdateCommand);
        chain.addCommand(new UpdateFlowTransitionCommand());
        addIfNotNull(chain, afterUpdateCommand);
        return chain;
    }
    public static FacilioChain getFlowTransitionSummaryChain(BlockType blockType) {
        FlowConfig flowConfig = getFlowConfig(blockType);

        if(flowConfig == null){
            return null;
        }

        Command afterFetchCommand = null;

        FlowTransitionSummaryHandler summaryHandler = flowConfig.getSummaryHandler();
        if(summaryHandler == null){
            return null;
        }

        afterFetchCommand = summaryHandler.getAfterFetchCommand();

        if(afterFetchCommand ==null){
           return null;
        }

        FacilioChain chain = getDefaultChain();
        chain.addCommand(afterFetchCommand);
        return chain;
    }
    public static FacilioChain getFlowTransitionListChain(BlockType blockType) {
        FlowConfig flowConfig = getFlowConfig(blockType);

        if(flowConfig == null){
            return null;
        }

        Command afterFetchCommand = null;

        FlowTransitionListHandler listHandler = flowConfig.getListHandler();
        if(listHandler == null){
            return null;
        }

        afterFetchCommand = listHandler.getAfterFetchCommand();

        if(afterFetchCommand ==null){
            return null;
        }

        FacilioChain chain = getDefaultChain();
        chain.addCommand(afterFetchCommand);
        return chain;
    }
    private static void addIfNotNull(FacilioChain chain, Command command) {
        if (command != null) {
            chain.addCommand(command);
        }
    }
    public static Class<? extends FlowTransitionContext> getBeanClassByBlockType (BlockType blockType){
        FlowConfig flowConfig = FlowChainUtil.getFlowConfig(blockType);
        Class beanClass = null;

        if (flowConfig != null) {
            beanClass = flowConfig.getBeanClass();
        } else {
            beanClass = FlowTransitionContext.class;
        }
        return beanClass;
    }

}
