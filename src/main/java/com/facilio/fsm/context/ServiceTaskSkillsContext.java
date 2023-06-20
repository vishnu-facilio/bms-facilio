package com.facilio.fsm.context;

import com.facilio.v3.context.V3Context;

public class ServiceTaskSkillsContext  extends V3Context {
    private static final long serialVersionUID = 1L;

    private ServiceTaskContext left;
    private ServiceSkillsContext right;

    public ServiceTaskContext getLeft() {
        return left;
    }

    public void setLeft(ServiceTaskContext left) {
        this.left = left;
    }

    public ServiceSkillsContext getRight() {
        return right;
    }

    public void setRight(ServiceSkillsContext right) {
        this.right = right;
    }
}
