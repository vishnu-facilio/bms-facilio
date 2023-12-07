package com.facilio.trigger.config;

import com.facilio.trigger.context.BaseTriggerContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TriggerConfig {

    private Class beanClass;
    public Class getBeanClass() {
        return beanClass;
    }
    private TriggerCreateHandler createHandler;

    private TriggerUpdateHandler updateHandler;
    private TriggerSummaryHandler summaryHandler;
    private TriggerDeleteHandler deleteHandler;

    private TriggerListHandler listHandler;
    public TriggerCreateHandler getCreateHandler() {
        return createHandler;
    }

    public TriggerUpdateHandler getUpdateHandler(){
        return updateHandler;
    }

    public TriggerSummaryHandler getSummaryHandler() {
        return summaryHandler;
    }

    public TriggerDeleteHandler getDeleteHandler(){return deleteHandler;}

    public TriggerListHandler getTriggerListHandler(){
        return listHandler;
    }

    private TriggerConfig() {}
    private TriggerConfig(TriggerConfigBuilder triggerConfigBuilder) {
        this.beanClass = triggerConfigBuilder.beanClass;
        this.createHandler = triggerConfigBuilder.createHandler;
        this.updateHandler = triggerConfigBuilder.updateHandler;
        this.summaryHandler = triggerConfigBuilder.summaryHandler;
        this.deleteHandler = triggerConfigBuilder.deleteHandler;
        this.listHandler = triggerConfigBuilder.listHandler;
    }

    public static class TriggerConfigBuilder{

        private Class beanClass;
        private TriggerConfigBuilder(){}
        public TriggerConfigBuilder(Class<? extends BaseTriggerContext> beanClass){
            this.beanClass=beanClass;
        }
        TriggerCreateHandler createHandler;

        TriggerUpdateHandler updateHandler;

        TriggerSummaryHandler summaryHandler;

        TriggerDeleteHandler deleteHandler;

        TriggerListHandler listHandler;
        public TriggerCreateHandler.TriggerCreateHandlerBuilder create() {
            return new TriggerCreateHandler.TriggerCreateHandlerBuilder(this);
        }

        public  TriggerUpdateHandler.TriggerUpdateHandlerBuilder update(){
            return new TriggerUpdateHandler.TriggerUpdateHandlerBuilder(this);
        }

        public  TriggerSummaryHandler.TriggerSummaryBuilder summary(){
            return new TriggerSummaryHandler.TriggerSummaryBuilder(this);
        }

        public  TriggerDeleteHandler.TriggerDeleteBuilder delete(){
            return new TriggerDeleteHandler.TriggerDeleteBuilder(this);
        }

        public TriggerListHandler.TriggerListBuilder list(){
            return new TriggerListHandler.TriggerListBuilder(this);
        }
        public TriggerConfig  build() {
            return new TriggerConfig(this);
        }

    }

}
