package com.facilio.bmsconsole.workflow.rule;

public abstract class AbstractStateFlowRuleContext extends WorkflowRuleContext {

    private long defaultStateId;

    public long getDefaultStateId() {
        return defaultStateId;
    }

    public void setDefaultStateId(long defaultStateId) {
        this.defaultStateId = defaultStateId;
    }

    private Boolean defaltStateFlow;

    public Boolean getDefaltStateFlow() {
        return defaltStateFlow;
    }

    public void setDefaltStateFlow(Boolean defaltStateFlow) {
        this.defaltStateFlow = defaltStateFlow;
    }

    public Boolean isDefaltStateFlow() {
        if (defaltStateFlow == null) {
            return false;
        }
        return defaltStateFlow;
    }

    private String diagramJson;

    public String getDiagramJson() {
        return diagramJson;
    }

    public void setDiagramJson(String diagramJson) {
        this.diagramJson = diagramJson;
    }

    private String configJson;
    public String getConfigJson() {
        return configJson;
    }
    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }
}
