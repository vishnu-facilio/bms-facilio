package com.facilio.bmsconsoleV3;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsoleV3.context.Attendance;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.location.LocationContextV3;
import com.facilio.v3.context.V3Context;

@Deprecated
public class AttendanceTransaction extends V3Context {

    private Attendance attendance;

    public Attendance getAttendance() {
        return attendance;
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }

    private TransactionType transactionType;

    public TransactionType getTransactionTypeEnum() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getTransactionType() {
        if (transactionType != null) {
            return transactionType.getValue();
        }
        return null;
    }

    public void setTransactionType(Integer transactionType) {
        this.transactionType = TransactionType.valueOf(transactionType);
    }


    public enum TransactionType {
        CHECKIN,
        CHECKOUT;

        public Integer getValue() {
            return ordinal() + 1;
        }

        public static TransactionType valueOf(Integer value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private SourceType sourceType;

    public SourceType getSourceTypeEnum(){
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getSourceType() {
        if (sourceType != null) {
            return sourceType.getValue();
        }
        return null;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = SourceType.valueOf(sourceType);
    }


    public enum SourceType {
        WEB,
        MOBILE;

        public Integer getValue() {
            return ordinal() + 1;
        }

        public static SourceType valueOf(Integer value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private LocationContextV3 location;
    public LocationContextV3 getLocation() {
        return location;
    }
    public void setLocation(LocationContextV3 location) {
        this.location = location;
    }

    private String ipAddress;
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    private String terminal;
    public String getTerminal() {
        return terminal;
    }
    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    private Long transactionTime;
    public Long getTransactionTime() {
        return transactionTime;
    }
    public void setTransactionTime(Long transactionTime) {
        this.transactionTime = transactionTime;
    }

    public V3PeopleContext getPeople() {
        return people;
    }

    public void setPeople(V3PeopleContext people) {
        this.people = people;
    }

    private V3PeopleContext people;
    private String remarks;
    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

}
