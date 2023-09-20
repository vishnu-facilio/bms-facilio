package com.facilio.fsm.context;

import com.facilio.v3.context.V3Context;

public class ServiceTaskTemplateSkillsContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private ServiceTaskTemplateContext left;
    private ServiceSkillsContext right;

    public ServiceTaskTemplateContext getLeft() {
        return left;
    }

    public void setLeft(ServiceTaskTemplateContext left) {
        this.left = left;
    }

    public ServiceSkillsContext getRight() {
        return right;
    }

    public void setRight(ServiceSkillsContext right) {
        this.right = right;
    }
}
