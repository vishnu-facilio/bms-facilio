package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.time.DateTimeUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class ReadingContext extends ModuleBaseWithCustomFields {

    private static final long serialVersionUID = 1L;

    private long actualTtime = -1;

    private long ttime = -1;
    private ZonedDateTime zdt;

    public void setTtime(long ttime) {
        this.ttime = ttime;
        this.zdt = DateTimeUtil.getDateTime(ttime);
    }

    String date;
    public String getDate() {
        if (zdt != null) {
            return zdt.toLocalDate().toString();
        }
        return null;
    }

    int year;
    public int getYear() {
        if (zdt != null) {
            return zdt.getYear();
        }
        return -1;
    }

    int month;
    public int getMonth() {
        if (zdt != null) {
            return zdt.getMonthValue();
        }
        return -1;
    }

    int week;
    public int getWeek() {
        if (zdt != null) {
            return zdt.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        }
        return -1;
    }

    int day;
    public int getDay() {
        if (zdt != null) {
            return zdt.getDayOfWeek().getValue();
        }
        return -1;
    }

    int hour;
    public int getHour() {
        if (zdt != null) {
            return zdt.getHour();
        }
        return -1;
    }

    private long parentId = -1;

    private Boolean marked;

    public boolean isMarked() {
        return (marked != null) ? marked : false;
    }

    public Map<String, Object> getReadings() {
        return super.getData();
    }

    public void setReadings(Map<String, Object> readings) {
        super.setData(readings);
    }

    public void addReading(String key, Object value) {
        super.setDatum(key, value);
    }

    public Object getReading(String key) {
        return super.getDatum(key);
    }

    private Object parent;

    private boolean newReading = false;

    private SourceType sourceType;

    public SourceType getSourceTypeEnum() {
        return sourceType;
    }

    public int getSourceType() {
        if (sourceType != null) {
            return sourceType.getIndex();
        }
        return -1;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = SourceType.valueOf(sourceType);
    }
    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    private long sourceId = -1;

    @Override
    public String toString() {

        if (getReadings() != null) {

            Map<String, Object> localReadings = new HashMap<>(getReadings());
            localReadings.put("parentId", parentId);
            localReadings.put("ttime", ttime);
            localReadings.put("id", getId());
            return localReadings.toString();
        }
        return null;
    }

    public ReadingContext clone() {
        ReadingContext r = new ReadingContext();
        r.setId(getId());
        r.setReadings(new HashMap<>(getReadings()));
        r.setActualTtime(actualTtime);
        r.setTtime(ttime);
        r.setZdt(zdt);
        r.setParentId(parentId);
        r.setMarked(marked);
        r.setParent(parent);
        r.setNewReading(newReading);
        r.setSourceType(sourceType);
        r.setSourceId(sourceId);
        r.setDataInterval(dataInterval);
        return r;
    }

    private int dataInterval = -1; // in mins

	@Getter @Setter
	private Map<String, Integer> units = new HashMap<>();

}
