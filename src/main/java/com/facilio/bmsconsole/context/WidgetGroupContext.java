package com.facilio.bmsconsole.context;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class WidgetGroupContext extends PageSectionWidgetContext {
    private WidgetGroupConfigContext config;
    private List<WidgetGroupSectionContext> sections;

    public WidgetGroupContext addConfig(WidgetGroupConfigContext.ConfigType configType) {
        this.setConfig(new WidgetGroupConfigContext(configType));
        return this;
    }

    public WidgetGroupSectionContext addSection(String name, String displayName, String description) {
        double sequenceNumber = CollectionUtils.isNotEmpty(this.getSections()) ? ((this.getSections().size()+1) * 10D ) : 10; //(number of sections in widgetGroup incremented by one * 10) to get sequence number
        WidgetGroupSectionContext section = new WidgetGroupSectionContext(name, displayName, sequenceNumber, description);
        if(this.getSections() == null) {
            this.setSections(new ArrayList<>(Arrays.asList(section)));
        }
        else {
            this.getSections().add(section);
        }
        section.setParentContext(this);
        return section;
    }
}
