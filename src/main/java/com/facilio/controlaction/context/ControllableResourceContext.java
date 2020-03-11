package com.facilio.controlaction.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.controlaction.context.ControllablePointContext.ControllablePoints;

public class ControllableResourceContext {
	
	
	public ControllableResourceContext(ResourceContext resource) {
		this.resource = resource;
	}

	Map<Integer,ControlPointContext> controllablePointMap;
	
	List<ControllablePoints> controllablePoints;
	
	ResourceContext resource;
	
	public ResourceContext getResource() {
		return resource;
	}

	public void setResource(ResourceContext resource) {
		this.resource = resource;
	}
	
	public Map<Integer, ControlPointContext> getControllablePointMap() {
		return controllablePointMap;
	}

	public void setControllablePointMap(Map<Integer, ControlPointContext> controllablePointMap) {
		this.controllablePointMap = controllablePointMap;
	}
	
	public void addControllablePointMap(Integer controllablePointId, ControlPointContext readingDataMeta) {
		this.controllablePointMap = controllablePointMap == null ? new HashMap<Integer, ControlPointContext>() : controllablePointMap;
		this.controllablePointMap.put(controllablePointId, readingDataMeta);
	}

	public List<ControllablePoints> getControllablePoints() {
		return controllablePoints;
	}

	public void setControllablePoints(List<ControllablePoints> controllablePoints) {
		this.controllablePoints = controllablePoints;
	}
	public void addControllablePoints(ControllablePoints controllablePoint) {
		controllablePoints = controllablePoints == null ? new ArrayList<>() : controllablePoints;
		if(!controllablePoints.contains(controllablePoint)) {
			this.controllablePoints.add(controllablePoint);
		}
	}
	
}
