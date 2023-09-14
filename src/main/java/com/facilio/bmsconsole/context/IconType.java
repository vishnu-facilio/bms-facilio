package com.facilio.bmsconsole.context;

import com.facilio.modules.FacilioStringEnum;
import lombok.Getter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum IconType implements FacilioStringEnum {
    analytics("Analytics"),
    asset("Asset"),
    automation("Automation"),
    automation_plus("Automation_plus"),
    budget("Budget"),
    building_portfolio("Building_portfolio"),
    client("Client"),
    community("Community"),
    connected_apps("Connected_apps"),
    contract("Contract"),
    customisation("Customisation"),
    dashboard("Dashboard"),
    developer_setting("Developer_setting"),
    energy("Energy"),
    energy_analytics("Energy_analytics"),
    home("Home"),
    induction("Induction"),
    inspection("Inspection"),
    inventory("Inventory"),
    logs("Logs"),
    maintenance("Maintenance"),
    people("People"),
    portfolio("Portfolio"),
    process("Process"),
    procurement("Procurement"),
    resources("Resources"),
    safety_plan("Safety_plan"),
    security_check("Security_check"),
    service_request("Service_request"),
    space("Space"),
    tenant("Tenant"),
    user_access("User_access"),
    vendor("Vendor"),
    visitor("Visitor"),
    visitor_setting("Visitor_setting"),
    workorder("Workorder"),
    workplace("Workplace"),
    wrench("Wrench"),
    meter("Meter"),
    controls("Controls"),
    energy_benchmark("Energy_Benchmark"),
    diagnostics("Diagnostics"),
    utility("Utility"),
    raw_alarm("Raw Alarm"),
    filtered_alarms("Filtered Alarms"),
    flagged_events("Flagged Events"),
    time_sheet("Time_sheet"),
    time_off("Time_off"),
    trip("Trip"),
    workforce("Workforce"),
    service_order("Service_order"),
    service_appointment("Service_appointment"),
    service_pm("Service_pm"),
    invoice("Invoice"),
    attendance("Attendance");

    private static final Map<String, IconType> ICON_NAME_VS_TYPE = initIconNamevsType();

    private final String iconName;

    IconType(String iconName) {
        this.iconName = iconName;
    }

    public static IconType valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }
    public static IconType getIconTypeByIconName(String iconName){
        return ICON_NAME_VS_TYPE.get(iconName);
    }
    private static Map<String, IconType> initIconNamevsType() {
        Map<String, IconType> iconNameVsIconType = new HashMap<>(values().length);
        for (IconType type : values()) {
            iconNameVsIconType .put(type.getIconName(), type);
        }
        return Collections.unmodifiableMap(iconNameVsIconType);
    }

}
