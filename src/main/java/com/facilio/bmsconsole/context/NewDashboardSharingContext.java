package com.facilio.bmsconsole.context;

public class NewDashboardSharingContext extends SingleSharingContext{
    private boolean locked;

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
