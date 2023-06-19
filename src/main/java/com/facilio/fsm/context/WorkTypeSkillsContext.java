package com.facilio.fsm.context;

import com.facilio.v3.context.V3Context;

public class WorkTypeSkillsContext extends V3Context {
    private WorkTypeContext left;
    private ServiceSkillsContext right;

    public WorkTypeContext getLeft() {
        return left;
    }

    public void setLeft(WorkTypeContext left) {
        this.left = left;
    }

    public ServiceSkillsContext getRight() {
        return right;
    }

    public void setRight(ServiceSkillsContext right) {
        this.right = right;
    }
}
