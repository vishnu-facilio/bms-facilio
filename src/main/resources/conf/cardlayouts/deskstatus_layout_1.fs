Map cardLayout(Map params) {
    result = {};
    result["title"] = params.title;

    deskModuleId = Module("desks").getId();
    floorModule = Module("floor");
    buildingModule = Module("building");
    deskModule = Module("desks");

    ticketStatus = Module("ticketstatus").fetch([parentModuleId == deskModuleId]);

    occupiedId = 0;

    vacantId = 0;

    reservableId = 0;

    buildingId = params.buildingId;
    floorId = params.floorId;
    departments = params.departments;
    departmentId = params.departmentId;

    for each index, data in ticketStatus {

        if (data.status == "occupied") {
            occupiedId = data.id;
        }
        if (data.status == "reservable") {
            reservableId = data.id;
        }
        if (data.status == "vacant") {
            vacantId = data.id;
        }

    }

    db = {
        criteria: [moduleState == occupiedId && department != [10] && floorId == 1445418],

    };

    if (departments == null) {
        departments = [];
    }
    if (buildingId != null && departmentId != null) {
        db1 = {
            criteria: [moduleState == occupiedId && building == buildingId && department == departmentId],
            field: "id",
            aggregation: "count"

        };


        db2 = {
            criteria: [moduleState == vacantId && building == buildingId && department == departmentId],
            field: "id",
            aggregation: "count"

        };

        db3 = {
            criteria: [moduleState == reservableId && building == buildingId && department == departmentId],
            field: "id",
            aggregation: "count"

        };
      
        db4 = {
            criteria: [building == buildingId && department == departmentId],
            field: "id",
            aggregation: "count"

        };
    } else if (buildingId != null && departments.size() > 0) {
        db1 = {
            criteria: [moduleState == occupiedId && building == buildingId && department != departments],
            field: "id",
            aggregation: "count"

        };


        db2 = {
            criteria: [moduleState == vacantId && building == buildingId && department != departments],
            field: "id",
            aggregation: "count"

        };

        db3 = {
            criteria: [moduleState == reservableId && building == buildingId && department != departments],
            field: "id",
            aggregation: "count"

        };
       db4 = {
            criteria: [building == buildingId && department != departments],
            field: "id",
            aggregation: "count"

        };
    } else {

        db1 = {
            criteria: [moduleState == occupiedId && building == buildingId],
            field: "id",
            aggregation: "count"

        };


        db2 = {
            criteria: [moduleState == vacantId && building == buildingId],
            field: "id",
            aggregation: "count"

        };

        db3 = {
            criteria: [moduleState == reservableId && building == buildingId],
            field: "id",
            aggregation: "count"

        };
      
       db4 = {
            criteria: [building == buildingId],
            field: "id",
            aggregation: "count"

        };
    }


    if (floorId != null) {

        if (departments != null && departments.size() > 0) {
            db1 = {
                criteria: [moduleState == occupiedId && floor == floorId && department != departments],
                field: "id",
                aggregation: "count"

            };

            db2 = {
                criteria: [moduleState == vacantId && floor == floorId && department != departments],
                field: "id",
                aggregation: "count"

            };

            db3 = {
                criteria: [moduleState == reservableId && floor == floorId && department != departments],
                field: "id",
                aggregation: "count"

            };
           db4 = {
                criteria: [floor == floorId && department != departments],
                field: "id",
                aggregation: "count"

            };

        } else {

            db1 = {
                criteria: [moduleState == occupiedId && floor == floorId],
                field: "id",
                aggregation: "count"

            };

            db2 = {
                criteria: [moduleState == vacantId && floor == floorId],
                field: "id",
                aggregation: "count"

            };

            db3 = {
                criteria: [moduleState == reservableId && floor == floorId],
                field: "id",
                aggregation: "count"

            };
          
          
            db4 = {
                criteria: [floor == floorId],
                field: "id",
                aggregation: "count"

            };

        }
    }



    occupied = 0;

    vacant = 0;

    reservable = 0;
  
  	total = 0;

    if (buildingId != null) {
        occupied = Module("desks").fetch(db1);
        vacant = Module("desks").fetch(db2);
        reservable = Module("desks").fetch(db3);
		total = deskModule.fetch(db4);
    }

    value = {};
    value["occupied"] = occupied;
    value["vacant"] = vacant;
    value["reservable"] = reservable;
  	value["deskCount"] = total;

    result["value"] = null;

    if (buildingId != null) {
        value["building"] = buildingModule.fetch([id == buildingId]);
        buildingDb = {
            criteria: [building == buildingId],
            field: "id",
            aggregation: "count"
        };
        value["floorCount"] = floorModule.fetch(buildingDb);
        deskDb = {
            criteria: [building == buildingId],
            field: "id",
            aggregation: "count"
        };
    }

    if (floorId != null) {
        value["floor"] = floorModule.fetch([id == floorId]);
          deskDb = {
            criteria: [floor == floorId],
            field: "id",
            aggregation: "count"
        };
    }

    result["valueMap"] = value;

    return result;
}