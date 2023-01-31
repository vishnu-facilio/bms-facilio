package com.facilio.bmsconsoleV3.context.shift;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import lombok.Data;

@Data
public class ShiftSlot {

    private  long id;
    private long from;
    private long to;
    private Shift shift;
    private V3PeopleContext people;

    public ShiftSlot() {
    }

    public ShiftSlot(V3PeopleContext emp, Shift shift, long from, long to) {
        this.from = from;
        this.to = to;
        this.shift = shift;
        this.people = emp;
    }

    public ShiftSlot(long id, V3PeopleContext emp, Shift shift, long from, long to) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.shift = shift;
        this.people = emp;
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
