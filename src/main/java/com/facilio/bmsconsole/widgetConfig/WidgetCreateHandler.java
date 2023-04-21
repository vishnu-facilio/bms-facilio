package com.facilio.bmsconsole.widgetConfig;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Command;

@Getter
@Setter
public class WidgetCreateHandler implements WidgetCreateBuilder{
    private Command saveCommand;
    WidgetConfig parent;
    public WidgetCreateHandler(WidgetConfig parent) {
        this.parent = parent;
    }

    @Override
    public WidgetCreateBuilder saveCommand(Command... saveCommand) {
        if(this.saveCommand == null){
            this.saveCommand = WidgetConfig.buildTransactionChain(saveCommand);
        }
        return this;
    }
    @Override
    public WidgetUpdateBuilder update(Command... updateCommand) {
        return this.parent.update();
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
