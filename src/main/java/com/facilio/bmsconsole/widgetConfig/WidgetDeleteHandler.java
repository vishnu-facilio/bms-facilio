package com.facilio.bmsconsole.widgetConfig;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Command;

@Getter
@Setter
public class WidgetDeleteHandler implements WidgetDeleteBuilder{
    private Command deleteCommand;
    private WidgetConfig parent;
    public WidgetDeleteHandler(WidgetConfig parent){
        this.parent = parent;
    }

    @Override
    public WidgetCreateBuilder create() {
        return this.parent.create();
    }

    @Override
    public WidgetUpdateBuilder update() {
        return this.parent.update();
    }

    @Override
    public WidgetDeleteBuilder deleteCommand(Command... deleteCommand) {
        if(this.deleteCommand == null){
            this.deleteCommand = WidgetConfig.buildReadChain(deleteCommand);
        }
        return this;
    }

    @Override
    public WidgetSummaryBuilder summary() {
        return this.parent.summary();
    }

    @Override
    public WidgetConfig build() {
        return this.parent.build();
    }
}
