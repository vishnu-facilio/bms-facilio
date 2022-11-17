package com.facilio.bmsconsoleV3.context.inventory;

import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.v3.context.V3Context;

public class V3ServingSitesContext  extends V3Context {
    private V3StoreRoomContext left;
    private V3SiteContext right;

    public V3StoreRoomContext getLeft() {
        return left;
    }

    public void setLeft(V3StoreRoomContext left) {
        this.left = left;
    }

    public V3SiteContext getRight() {
        return right;
    }

    public void setRight(V3SiteContext right) {
        this.right = right;
    }
}
