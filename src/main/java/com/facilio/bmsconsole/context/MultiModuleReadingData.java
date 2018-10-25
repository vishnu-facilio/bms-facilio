package com.facilio.bmsconsole.context;

import java.util.List;
import java.util.Map;

public class MultiModuleReadingData {
	
	Map<String, List<ReadingContext>> readingMap;
	public Map<String, List<ReadingContext>> getReadingMap() {
		return readingMap;
	}
	public void setReadingMap(Map<String, List<ReadingContext>> readingMap) {
		this.readingMap = readingMap;
	}
	
	Map<String, ReadingDataMeta> readingDataMeta;
	public Map<String, ReadingDataMeta> getReadingDataMeta() {
		return readingDataMeta;
	}
	public void setReadingDataMeta(Map<String, ReadingDataMeta> readingDataMeta) {
		this.readingDataMeta = readingDataMeta;
	}

}
