package com.facilio.bmsconsoleV3.commands.homepage;

import com.facilio.bmsconsole.homepage.homepagewidgetdata.HomepageWidgetData;
import com.facilio.bmsconsoleV3.util.HomepageWidgteApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class getHomePageWidgetDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName;

        String widgetLinkName = (String) context.get(FacilioConstants.ContextNames.HomePage.WIDGET_LINKNAME);
        if(widgetLinkName.equals(FacilioConstants.ContextNames.HomePage.NUTSHLL_WIDGET)) {
            context.put(FacilioConstants.ContextNames.HomePage.WIDGET_DATA, getNutshellWidget());

        }
        else if (widgetLinkName.equals((FacilioConstants.ContextNames.HomePage.ACTION_WIDGET))) {
            context.put(FacilioConstants.ContextNames.HomePage.WIDGET_DATA, getActionWidget());
        }
        else if (widgetLinkName.equals(FacilioConstants.ContextNames.HomePage.RECENT_RESERVED_SPACE)) {
            context.put(FacilioConstants.ContextNames.HomePage.WIDGET_DATA, getRecentReservedSpace());
        }

        return false;
    }
    private static JSONObject getRecentReservedSpace() throws Exception {
        return HomepageWidgteApi.getRecentlyReservedSpace();
    }
    private static  List<HomepageWidgetData> getActionWidget() throws Exception {

        List<HomepageWidgetData> widgetDataList = new ArrayList<>();
        widgetDataList.addAll(HomepageWidgteApi.getMyDeskBookingActionCards());
        widgetDataList.addAll(HomepageWidgteApi.getMyParkingBookingActionCards());
        widgetDataList.addAll(HomepageWidgteApi.getMySpaceBookingActionCards());
        widgetDataList.addAll(HomepageWidgteApi.getMyDeliveriesActionCards());
        widgetDataList.addAll(HomepageWidgteApi.getMyVisitorActionCards());


        return widgetDataList;
    }
    private static List<HomepageWidgetData> getNutshellWidget() throws Exception {
        HomepageWidgetData homepageWidget = new HomepageWidgetData();

        List<HomepageWidgetData> widgetDataList = new ArrayList<HomepageWidgetData>();
        widgetDataList.add(HomepageWidgteApi.getLatestMyDeskBooking());
        widgetDataList.add(HomepageWidgteApi.getLatestMyParkingBooking());
        widgetDataList.add(HomepageWidgteApi.getLatestMySpaceBooking());
        widgetDataList.addAll(HomepageWidgteApi.getMyVisitorActionCards());


        return widgetDataList.stream().filter(widget -> (widget != null )).collect(Collectors.toList());
    }
    }