package com.facilio.bmsconsoleV3.context.floorplan;

import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.constants.FacilioConstants;

public class V3FloorplanCustomizationContext {

    private DeskLabel deskPrimaryLabel;
    private DeskLabel deskSecondaryLabel;
    private BookingStateColor deskBookingState;

    private SpaceLabel spacePrimaryLabel;
    private SpaceLabel spaceSecondaryLabel;
    private String defaultSpaceColor;
    private BookingStateColor spaceBookingState;

    private boolean compactView=false;
    private  boolean textHalo = true;

    public boolean isCompactView() {
        return compactView;
    }

    public void setCompactView(boolean compactView) {
        this.compactView = compactView;
    }

    public boolean isTextHalo() {
        return textHalo;
    }

    public void setTextHalo(boolean textHalo) {
        this.textHalo = textHalo;
    }

    public boolean isAllowTextOverlap() {
        return allowTextOverlap;
    }

    public void setAllowTextOverlap(boolean allowTextOverlap) {
        this.allowTextOverlap = allowTextOverlap;
    }

    private  boolean allowTextOverlap = true;


    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    private int fontSize = 11;

    public double getImageOpacity() {
        return imageOpacity;
    }

    public void setImageOpacity(double imageOpacity) {
        this.imageOpacity = imageOpacity;
    }

    private double imageOpacity=0.7;


    public DeskLabel getDeskPrimaryLabel() {
        if (deskPrimaryLabel == null) {
            return new DeskLabel().setLabelType(DeskLabelType.FULL_NAME);
        }
        return deskPrimaryLabel;
    }

    public void setDeskPrimaryLabel(DeskLabel deskPrimaryLabel) {
        if (deskPrimaryLabel == null) {
            this.deskPrimaryLabel= new DeskLabel().setLabelType(DeskLabelType.FULL_NAME);
        }
        this.deskPrimaryLabel = deskPrimaryLabel;
    }

    public DeskLabel getDeskSecondaryLabel() {
        if (deskSecondaryLabel == null) {
            return new DeskLabel().setLabelType(DeskLabelType.DESK_NAME);
        }
        return deskSecondaryLabel;
    }

    public void setDeskSecondaryLabel(DeskLabel deskSecondaryLabel) {
        if (deskSecondaryLabel == null) {
            this.deskSecondaryLabel= new DeskLabel().setLabelType(DeskLabelType.DESK_NAME);
        }
        this.deskSecondaryLabel = deskSecondaryLabel;
    }

    public BookingStateColor getDeskBookingState() {
        if(deskBookingState ==null)
        {
            return new BookingStateColor();
        }

        return deskBookingState;
    }

    public void setDeskBookingState(BookingStateColor deskBookingState) {
        this.deskBookingState = deskBookingState;
    }

    public SpaceLabel getSpacePrimaryLabel() {
        if (spacePrimaryLabel == null) {
            return new SpaceLabel().setLabelType(SpaceLabelType.DEFAULT);
        }
        return spacePrimaryLabel;
    }

    public void setSpacePrimaryLabel(SpaceLabel spacePrimaryLabel) {
        this.spacePrimaryLabel = spacePrimaryLabel;
    }

    public SpaceLabel getSpaceSecondaryLabel() {
        if (spaceSecondaryLabel == null) {
            return new SpaceLabel().setLabelType(SpaceLabelType.CATEGORY);
        }
        return spaceSecondaryLabel;
    }

    public void setSpaceSecondaryLabel(SpaceLabel spaceSecondaryLabel) {
        this.spaceSecondaryLabel = spaceSecondaryLabel;
    }

    public String getDefaultSpaceColor() {
        return defaultSpaceColor;
    }

    public void setDefaultSpaceColor(String defaultSpaceColor) {
        this.defaultSpaceColor = defaultSpaceColor;
    }

    public BookingStateColor getSpaceBookingState() {
        if(spaceBookingState ==null)
        {
            return new BookingStateColor();
        }
        return spaceBookingState;
    }

    public void setSpaceBookingState(BookingStateColor spaceBookingState) {
        this.spaceBookingState = spaceBookingState;
    }

    public class BookingStateColor {
        private String nonReservableColor = "rgba(0, 0, 0,0.3)"; // non reservable
        private String availableColor="rgba(34, 174, 92, 0.3)"; // available to book
        private String partiallyAvailableColor="rgba(13, 91, 225, 0.3)"; // partially available for booking
        private String notAvailableColor="rgba(220, 74, 76, 0.3)"; // not available since its already booked

        public double getOpacity() {
            return opacity;
        }

        public void setOpacity(double opacity) {
            this.opacity = opacity;
        }

        private double opacity=0.3;

        public String getNonReservableColor() {
            return nonReservableColor;
        }

        public void setNonReservableColor(String nonReservableColor) {
            this.nonReservableColor = nonReservableColor;
        }

        public String getAvailableColor() {
            return availableColor;
        }

        public void setAvailableColor(String availableColor) {
            this.availableColor = availableColor;
        }

        public String getPartiallyAvailableColor() {
            return partiallyAvailableColor;
        }

        public void setPartiallyAvailableColor(String partiallyAvailableColor) {
            this.partiallyAvailableColor = partiallyAvailableColor;
        }

        public String getNotAvailableColor() {
            return notAvailableColor;
        }

        public void setNotAvailableColor(String notAvailableColor) {
            this.notAvailableColor = notAvailableColor;
        }

    }

    public class DeskLabel {

        private DeskLabelType labelType;
        private String customText=" ";
        private String color="rgba(50, 64, 86, 1)";
        private int fontSize=11;

        public DeskLabelType getLabelType() {
            return labelType;
        }

        public DeskLabel setLabelType(DeskLabelType labelType) {
            this.labelType = labelType;
            return this;
        }

        public String getCustomText() {
            return customText;
        }

        public DeskLabel setCustomText(String customText) {
            this.customText = customText;
            return this;
        }

        public String getColor() {
            return color;
        }

        public DeskLabel setColor(String color) {
            this.color = color;
            return this;
        }

        public int getFontSize() {
            return fontSize;
        }

        public DeskLabel setFontSize(int fontSize) {
            this.fontSize = fontSize;
            return this;
        }
    }

    public class SpaceLabel {

        public SpaceLabelType getLabelType() {
            return labelType;
        }

        public SpaceLabel setLabelType(SpaceLabelType labelType) {
            this.labelType = labelType;
            return this;
        }

        private SpaceLabelType labelType;
        private String customText=" ";
        private String color="rgba(50, 64, 86, 1)";
        private int fontSize=11;

        public String getCustomText() {
            return customText;
        }

        public SpaceLabel setCustomText(String customText) {
            this.customText = customText;
            return this;
        }

        public String getColor() {
            return color;
        }

        public SpaceLabel setColor(String color) {
            this.color = color;
            return this;
        }

        public int getFontSize() {
            return fontSize;
        }

        public SpaceLabel setFontSize(int fontSize) {
            this.fontSize = fontSize;
            return this;
        }
    }

    public static enum DeskLabelType {
        FULL_NAME() {
            @Override
            public String format(V3DeskContext desk) {
                V3EmployeeContext emp=desk.getEmployee();
                return emp.getName();
            }
        },
        DESK_NAME() {
            @Override
            public String format(V3DeskContext desk) {
                return desk.getName();
            }
        },
        RETURN_NULL() {
            @Override
            public String format(V3DeskContext desk) {
                return "";
            }
        },
        FIRST_NAME() {
            @Override
            public String format(V3DeskContext desk) {
                V3EmployeeContext emp=desk.getEmployee();
                String name = emp.getName();
                String namesplit[] =name.split(" ");
                return namesplit[0];
            }
        },
        LAST_NAME() {
            @Override
            public String format(V3DeskContext desk) {
                V3EmployeeContext emp=desk.getEmployee();
                String name = emp.getName();
                String namesplit[] =name.split(" ");
                String lname="";
                if(namesplit.length==1)
                {
                    return namesplit[0];
                }
                for(int i=1;i<namesplit.length;i++)
                {
                    lname=lname+" "+namesplit[i];
                }
                return lname;
            }
        },
        INITIAL_WITH_FIRST_NAME() {
            @Override
            public String format(V3DeskContext desk) {
                V3EmployeeContext emp=desk.getEmployee();
                String name = emp.getName();
                String namesplit[] =name.split(" ");
                if(namesplit.length==1)
                {
                    return namesplit[0];
                }
                String initialname = namesplit[0];
                for(int i=1;i<namesplit.length;i++)
                {
                    initialname=initialname+" "+namesplit[i].charAt(0);
                }
                return initialname;
            }
        },
        INITIAL_WITH_LAST_NAME() {
            @Override
            public String format(V3DeskContext desk) {
                V3EmployeeContext emp=desk.getEmployee();
                String name = emp.getName();
                String namesplit[] =name.split(" ");
                if(namesplit.length==1)
                {
                    return namesplit[0];
                }
                String initialname = "";
                for(int i=0;i<namesplit.length-1;i++)
                {
                    initialname=initialname+namesplit[i].charAt(0)+" ";
                }
                return initialname+namesplit[namesplit.length-1];
            }
        },
        CUSTOM() {
            @Override
            public String format(V3DeskContext desk) throws Exception {
                return WorkflowRuleAPI.replacePlaceholders(FacilioConstants.ContextNames.Floorplan.DESKS, desk, this.getCustomText(), "--");
            }
        };

        private String customText;

        public void setCustomText(String customText) {
            this.customText = customText;
        }

        public String getCustomText() {
            return this.customText;
        }

        public abstract String format(V3DeskContext desk) throws Exception;
    }

    public static enum SpaceLabelType {
        CUSTOM() {
            @Override
            public String format(SpaceContext space) throws Exception {
                return WorkflowRuleAPI.replacePlaceholders(FacilioConstants.ModuleNames.SPACE, space, this.getCustomText(), "--");
            }
        },
        DEFAULT() {
            @Override
            public String format(SpaceContext space) throws Exception {
                return  space.getName();
            }
        },
        RETURN_NULL() {
            @Override
            public String format(SpaceContext space) throws Exception{
                return "";
            }
        },
        CATEGORY() {
            @Override
            public String format(SpaceContext space) throws Exception {
                if (space.getSpaceCategory() != null) {
                    return space.getSpaceCategory().getName();
                }
                return null;
            }
        };

        private String customText;

        public void setCustomText(String customText) {
            this.customText = customText;
        }

        public String getCustomText() {
            return this.customText;
        }

        public abstract String format(SpaceContext space) throws Exception;
    }
}
