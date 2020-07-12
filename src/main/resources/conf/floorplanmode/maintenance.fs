Map floorPlanMode(Map params) {
floorId = params.floorId;
spaceIds = params.spaceId;

 areas = [];
   for each index, spaceId in spaceIds {
    area = {};
      resource = Module("basespace").getSubordinates(spaceId);
    log "" + resource;
  db = {
    criteria: [resource == resource],
  };
  workorder = Module("workorder").fetch(db);
    if (workorder != null) {
      icon = {};
      label= {};
      label["value"] = "" + workorder.size();
      label["unit"] = "";

      icon["type"] = "MAINTENANCE";
      icon["position"] = "center";
      icon["value"] = label;
      icons = [];
      icons.add(icon);
      area["spaceId"] = spaceId;
      area["values"] = workorder;
      area["icons"] = icons;
      areas.add(area);
     }
    else {
      empty = [];
      area["spaceId"] = spaceId;
      area["values"] = empty;
      areas.add(area);
    }
      }
result = {};
  result.areas = areas;
  result.layer = "maintenance";
  return result;
}