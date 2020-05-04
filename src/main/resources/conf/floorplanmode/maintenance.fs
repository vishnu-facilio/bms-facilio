Map floorPlanMode(Map params) {
floorId = params.floorId;
spaceIds = params.spaceId;

 areas = [];
   for each index, spaceId in spaceIds {
    area = {};
      resource = BaseSpace().getSubordinates(spaceId);
    log "" + resource;
  db = {
    criteria: [resource == resource],
  };
  workorder = Module("workorder").fetch(db);
    if (workorder != null) {
      icon = {};
      icon["type"] = "MAINTENANCE";
      icon["position"] = "top-left";
      icons = [];
      icons.add(icon);
      label = "" + workorder.size();
      area["spaceId"] = spaceId;
      area["label"] = label;
      area["values"] = workorder;
      area["icons"] = icons;
      areas.add(area);
     }
    else {
      empty = [];
      area["spaceId"] = spaceId;
      area["label"] = "0";
      area["values"] = empty;
      areas.add(area);
    }
      }
result = {};
  result.areas = areas;
  result.layer = "maintenance";
  return result;
}