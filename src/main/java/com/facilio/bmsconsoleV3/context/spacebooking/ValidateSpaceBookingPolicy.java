package com.facilio.bmsconsoleV3.context.spacebooking;

import java.util.concurrent.TimeUnit;

public class ValidateSpaceBookingPolicy {

    public static enum policyENUM {

        IS_MULTIDAY_BOOKING() {
            @Override
            public boolean validate(V3SpaceBookingPolicyJSONContext policy,V3SpaceBookingContext booking) {

                boolean flag = true;

                long startTime = booking.getBookingStartTime();
                long endTime = booking.getBookingEndTime();
                long diff = startTime-endTime;
                long dayDiff = TimeUnit.MILLISECONDS.toDays(diff);
                if(dayDiff>0)
                {
                    if(policy.getIsMultipleDayBooking().isEnable()){
                        flag = true;
                    }
                    else{
                        flag = false;
                    }
                }


                return flag;
            }
        },


        BOOKING_ADVANCE_DAYS() {
            @Override
            public boolean validate(V3SpaceBookingPolicyJSONContext policy,V3SpaceBookingContext booking) {
                boolean flag = true;
                if(policy.getBookingAdvanceDays().isEnable()) {
                    flag = false;


                    int advanceDays = (int) policy.getBookingAdvanceDays().getValue();


                    long startTime = booking.getBookingStartTime();
                    long currentTime = System.currentTimeMillis();
                    long diff = startTime-currentTime;
                    long dayDiff = TimeUnit.MILLISECONDS.toDays(diff);

                    try {

                        if (dayDiff <= advanceDays) {
                            flag = true;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return flag;
            }
        },
        BOOKING_ADVANCE_HOURS() {
            @Override
            public boolean validate(V3SpaceBookingPolicyJSONContext policy,V3SpaceBookingContext booking) {
                boolean flag = true;
                if(policy.getBookingAdvanceHours().isEnable()) {
                    flag = false;

                    long startTime = booking.getBookingStartTime();
                    long currentTime = System.currentTimeMillis();
                    long diff = startTime-currentTime;
                    long hourDif = TimeUnit.MILLISECONDS.toHours(diff);

                    try {
                        if (hourDif<=policy.getBookingAdvanceHours().getValue()) {
                            flag = true;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return flag;
            }

        },
        MAXIMUM_ATTENDEES() {
            @Override
            public boolean validate(V3SpaceBookingPolicyJSONContext policy,V3SpaceBookingContext booking) {
                boolean flag = true;
                if(policy.getMaximumAttendees().isEnable()) {
                    flag = false;
                    long attendeeCount = booking.getNoOfAttendees();
                    long maxCount = policy.getMaximumAttendees().getValue();

                    try {
                        if (attendeeCount<= maxCount) {
                            flag = true;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return flag;
            }

        };
        public abstract boolean validate(V3SpaceBookingPolicyJSONContext policy,V3SpaceBookingContext booking) throws Exception;

    }
}




