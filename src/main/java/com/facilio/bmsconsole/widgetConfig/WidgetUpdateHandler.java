package com.facilio.bmsconsole.widgetConfig;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Command;

@Getter
@Setter
public class WidgetUpdateHandler implements WidgetUpdateBuilder{

    private Command updateCommand;
    WidgetConfig parent;
    public WidgetUpdateHandler(WidgetConfig parent){
        this.parent = parent;
    }

    @Override
    public WidgetCreateBuilder create() {
        return this.parent.create();
    }

    @Override
    public WidgetUpdateBuilder updateCommand(Command... updateCommand) {
        if(this.updateCommand == null){
            this.updateCommand = WidgetConfig.buildTransactionChain(updateCommand);
        }
        return this;
    }

    @Override
    public WidgetDeleteBuilder delete(Command... deleteCommand) {
        return this.parent.delete();
    }

    @Override
    public WidgetSummaryBuilder summary(Command... summaryCommand) {
        return this.parent.summary();
    }

    @Override
    public WidgetConfig build() {
        return this.parent.build();
    }
}
