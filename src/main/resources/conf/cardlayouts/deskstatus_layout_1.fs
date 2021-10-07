Map cardLayout(Map params) {
    result = {};
    result["title"] = params.title;


    deskModuleId = Module("desks").getId();

    ticketStatus = Module("ticketstatus").fetch([parentModuleId == deskModuleId]);

    occupiedId = 0;

    vacantId = 0;

    reservableId = 0;

    buildingId = params.buildingId;

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
        criteria: [moduleState == occupiedId],

    };

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


    if (floorId) {
        db1["criteria"] = [moduleState == occupiedId && floor == floorId];
        db2["criteria"] = [moduleState == vacantId && floor == floorId];
        db3["criteria"] = [moduleState == reservableId && floor == floorId];
    }

    occupied = 0;

    vacant = 0;

    reservable = 0;

    if (buildingId != null) {
        occupied = Module("desks").fetch(db1);
        vacant = Module("desks").fetch(db2);
        reservable = Module("desks").fetch(db3);


    }

    value = {};
    value["occupied"] = occupied;
    value["vacant"] = vacant;
    value["reservable"] = reservable;

    result["value"] = null;
    result["valueMap"] = value;

    return result;
}