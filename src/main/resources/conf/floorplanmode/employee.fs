Map floorPlanMode(Map params) {
    floorId = params.floorId;
    spaceIds = params.spaceId;
     em = new NameSpace("employee");
  	areas = em.employeeAreas(params);
    result = {};
    result.areas = areas;
    result.layer = "employee";
    return result;
}