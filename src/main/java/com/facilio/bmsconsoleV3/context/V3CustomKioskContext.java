package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.bmsconsole.context.ConnectedAppWidgetContext;
import com.facilio.bmsconsoleV3.context.asset.V3DeviceContext;
import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingExternalAttendeeContext;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter

public class V3CustomKioskContext extends V3DeviceContext {
    private static final long serialVersionUID = 1L;

    private ConnectedAppWidgetContext connectedAppWidget;
    private List<V3CustomDeviceButtonMappingContext> customDeviceButton;
    private List<V3CustomKioskButtonContext> connectedAppWidgetList;



}
