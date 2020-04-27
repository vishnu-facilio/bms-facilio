Map floorPlanMode(Map params) {
  floorId = params.floorId;
  db1 = {
    criteria: [id != -1],
  };
  spaceCategory = Module("spaceCategory").fetch(db1);
  
  db2 = {
    criteria: [floor == floorId],
  };
  spacelist = Module("space").fetch(db2);
 areas = [];
  for each index, space in spacelist {
    area = {};
    if(space.spaceCategory != null) {
      area["spaceId"] = space.id;
       for each idx, spaceCat in spaceCategory {
        if (space.spaceCategory.id == spaceCat.id) {
          area["label"] = spaceCat.name;
         }
      }
          areas.add(area);
      }
  }
result = {};
  result.areas = areas;
  result.layer = "heatmap";
  return result;
}