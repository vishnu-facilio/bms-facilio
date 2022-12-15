package com.facilio.bmsconsoleV3.context.floorplan;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class FloorPlanToolTipContext {
    private static final Logger LOGGER = LogManager.getLogger(V3FloorplanCustomizationContext.class.getName());
    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String moduleName;

    public String getModuleDisplayName() {
        return moduleDisplayName;
    }

    public void setModuleDisplayName(String moduleDisplayName) {
        this.moduleDisplayName = moduleDisplayName;
    }

    private String moduleDisplayName;

    public ToolTip getToolTip() {
        if(toolTip == null)
        {
            this.toolTip = new ToolTip();
        }
        return toolTip;
    }

    public void setToolTip(ToolTip toolTip) {
        this.toolTip = toolTip;
    }

    public Marker getMarker() {
        if(marker == null)
        {
            this.marker= new Marker();
        }
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    private ToolTip toolTip;
    private Marker marker;


    public static class ToolTip {
        private Label primaryLabel;
        private List<Label> secondaryLabel = new ArrayList<>();

        public Label getPrimaryLabel() {
            if (primaryLabel == null) {
                this.primaryLabel = new Label().setLabelType(LabelType.CUSTOM);
            }
            return primaryLabel;
        }

        public void setPrimaryLabel(Label primaryLabel) {
            this.primaryLabel = primaryLabel;
        }

        public List<Label> getSecondaryLabel() {
            if (secondaryLabel.isEmpty()) {
                secondaryLabel.add(new Label().setLabelType(LabelType.CUSTOM));
            }
            return secondaryLabel;
        }

        public void setSecondaryLabel(List<Label> secondaryLabel) {
            this.secondaryLabel = secondaryLabel;
        }
    }

    public static class Marker{
        private Label primaryLabel;

        public Label getPrimaryLabel() {
            if(primaryLabel == null)
            {
                this.primaryLabel = new Label().setLabelType(LabelType.CUSTOM);
            }
            return primaryLabel;
        }

        public void setPrimaryLabel(Label primaryLabel) {
            this.primaryLabel = primaryLabel;
        }

        public Label getSecondaryLabel() {
            if(secondaryLabel == null)
            {
                this.secondaryLabel = new Label().setLabelType(LabelType.CUSTOM);
            }
            return secondaryLabel;
        }

        public void setSecondaryLabel(Label secondaryLabel) {
            this.secondaryLabel = secondaryLabel;
        }

        private Label secondaryLabel;
    }


    public static class Label {

        private LabelType labelType;
        private String customText=" ";
        private String color="rgba(50, 64, 86, 1)";
        private int fontSize=11;

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        private String fieldName="";

        public long getIconId() {
            return iconId;
        }

        public void setIconId(long iconId) {
            this.iconId = iconId;
        }

        public String getIconType() {
            return iconType;
        }

        public void setIconType(String iconType) {
            this.iconType = iconType;
        }

        private long iconId=-1;

        private String iconType="";


        public LabelType getLabelType() {
            return labelType;
        }

        public Label setLabelType(LabelType labelType) {
            this.labelType= labelType;
            return this;
        }

        public String getCustomText() throws Exception {
            return customText;
        }

        public Label setCustomText(String customText) {
            this.customText = customText;
            return this;
        }

        public String getColor() {
            return color;
        }

        public Label setColor(String color) {
            this.color = color;
            return this;
        }

        public int getFontSize() {
            return fontSize;
        }

        public Label setFontSize(int fontSize) {
            this.fontSize = fontSize;
            return this;
        }
    }

    public static enum LabelType {
        CUSTOM() {
            @Override
            public String format(ModuleBaseWithCustomFields context) throws Exception {
                if (this.getModuleName() != null) {
                    return WorkflowRuleAPI.replacePlaceholders(this.getModuleName(), context, this.getCustomText(), "--");
                }
                return " ";
            }
        },
            DEFAULT() {
                @Override
                public String format (ModuleBaseWithCustomFields context) throws Exception {
                    if (this.getModuleName() != null) {
                        return WorkflowRuleAPI.replacePlaceholders(this.getModuleName(), context, this.getCustomText(), "--");
                    }
                    return " ";
                }
        };

        private String customText;

        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }

        private String moduleName;

        public void setCustomText(String customText) {
            this.customText = customText;
        }

        public String getCustomText() {
            return this.customText;
        }

        public abstract String format(ModuleBaseWithCustomFields context) throws Exception;
    }
}
