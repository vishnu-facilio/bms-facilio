package com.facilio.flows.config;

import com.facilio.flows.context.FlowTransitionContext;

public class FlowConfig {
    private Class beanClass;
    public Class getBeanClass() {
        return beanClass;
    }
    private FlowTransitionSaveHandler createHandler;

    public FlowTransitionSaveHandler getCreateHandler() {
        return createHandler;
    }

    public FlowTransitionSummaryHandler getSummaryHandler() {
        return summaryHandler;
    }

    public FlowTransitionUpdateHandler getUpdateHandler() {
        return updateHandler;
    }
    public FlowTransitionListHandler getListHandler() {return listHandler;}

    private FlowTransitionSummaryHandler summaryHandler;
    private FlowTransitionUpdateHandler updateHandler;
    private FlowTransitionListHandler listHandler;
    private FlowConfig() {}
    private FlowConfig(FlowConfigBuilder flowConfigBuilder) {
        this.beanClass = flowConfigBuilder.beanClass;
        this.createHandler = flowConfigBuilder.createHandler;
        this.summaryHandler = flowConfigBuilder.summaryHandler;
        this.updateHandler = flowConfigBuilder.updateHandler;
        this.listHandler = flowConfigBuilder.listHandler;
    }
    public static class FlowConfigBuilder {
        private Class beanClass;
        private FlowConfigBuilder(){}
        public FlowConfigBuilder(Class<? extends FlowTransitionContext> beanClass){
            this.beanClass=beanClass;
        }
        FlowTransitionSaveHandler createHandler;
        FlowTransitionSummaryHandler summaryHandler;
        FlowTransitionUpdateHandler updateHandler;
        FlowTransitionListHandler listHandler;
        public FlowTransitionSaveHandler.FlowTransitionSaveHandlerBuilder create() {
            return new FlowTransitionSaveHandler.FlowTransitionSaveHandlerBuilder(this);
        }
        public FlowTransitionSummaryHandler.FlowTransitionSummaryBuilder summary() {
            return new FlowTransitionSummaryHandler.FlowTransitionSummaryBuilder(this);
        }
        public FlowTransitionUpdateHandler.FlowTransitionUpdateHandlerBuilder update() {
            return new FlowTransitionUpdateHandler.FlowTransitionUpdateHandlerBuilder(this);
        }
        public FlowTransitionListHandler.FlowTransitionListBuilder list() {
            return new FlowTransitionListHandler.FlowTransitionListBuilder(this);
        }
        public FlowConfig  build() {
            return new FlowConfig(this);
        }
    }
}
