package com.facilio.bmsconsoleV3.context.shift;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import lombok.Data;

import java.util.Date;

@Data
public class ShiftSlot {

    private  long id;
    private long from;
    private Date readableFrom;
    private long to;
    private Date readableTo;
    private Shift shift;
    private V3PeopleContext people;

    public ShiftSlot() {
    }

    public ShiftSlot(V3PeopleContext emp, Shift shift, long from, long to) {
        this.from = from;
        this.to = to;
        this.shift = shift;
        this.people = emp;
        this.readableFrom = new Date(from);
        this.readableTo = new Date(to);
    }

    public ShiftSlot(long id, V3PeopleContext emp, Shift shift, long from, long to) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.shift = shift;
        this.people = emp;
        this.readableFrom = new Date(from);
        this.readableTo = new Date(to);
    }

    @Override
    public String toString() {
        return "ShiftSlot{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", shift=" + shift +
                ", people=" + people +
                '}';
    }
}
