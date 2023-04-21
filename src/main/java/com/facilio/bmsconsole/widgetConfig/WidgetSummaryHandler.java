package com.facilio.bmsconsole.widgetConfig;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Command;

@Getter
@Setter
public class WidgetSummaryHandler implements WidgetSummaryBuilder{
    private Command fetchCommand;
    private WidgetConfig parent;
    public WidgetSummaryHandler(WidgetConfig parent){
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
    public WidgetSummaryBuilder fetchCommand(Command... fetchCommand) {
        if(this.fetchCommand == null){
            this.fetchCommand = WidgetConfig.buildReadChain(fetchCommand);
        }
        return this;
    }

    @Override
    public WidgetDeleteBuilder delete() {
        return this.parent.delete();
    }

    @Override
    public WidgetConfig build() {
        return this.parent.build();
    }
}
