package com.facilio.bmsconsole.widgetConfig;

import com.facilio.chain.FacilioChain;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Command;

@Getter
@Setter
public class WidgetConfig implements WidgetBuilder{

    private WidgetCreateHandler widgetCreateHandler;
    private WidgetUpdateHandler widgetUpdateHandler;
    private WidgetDeleteHandler widgetDeleteHandler;
    private WidgetSummaryHandler widgetSummaryHandler;

    @Override
    public WidgetCreateBuilder create() {
        if(this.widgetCreateHandler == null){
            this.widgetCreateHandler = new WidgetCreateHandler(this);
        }
        return this.widgetCreateHandler;
    }
    @Override
    public WidgetUpdateBuilder update() {
        if(this.widgetUpdateHandler == null){
            this.widgetUpdateHandler = new WidgetUpdateHandler(this);
        }
        return this.widgetUpdateHandler;
    }
    @Override
    public WidgetSummaryBuilder summary() {
        if(this.widgetSummaryHandler == null){
            this.widgetSummaryHandler = new WidgetSummaryHandler(this);
        }
        return this.widgetSummaryHandler;
    }

    @Override
    public WidgetDeleteBuilder delete() {
        if(this.widgetDeleteHandler == null){
            this.widgetDeleteHandler = new WidgetDeleteHandler(this);
        }
        return this.widgetDeleteHandler;
    }

    @Override
    public WidgetConfig build() {
        return this;
    }

    public static FacilioChain buildTransactionChain(Command[] facilioCommands) {
        FacilioChain c = FacilioChain.getTransactionChain();
        for (Command facilioCommand: facilioCommands) {
            c.addCommand(facilioCommand);
        }
        return c;
    }

    public static FacilioChain buildReadChain(Command[] facilioCommands) {
        FacilioChain c = FacilioChain.getNonTransactionChain();
        for (Command facilioCommand: facilioCommands) {
            c.addCommand(facilioCommand);
        }
        return c;
    }
}
