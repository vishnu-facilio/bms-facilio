SET @WO_JSON = '{
  "states": [
    {
      "stateId": ${Assigned},
      "x": 360,
      "y": 370,
      "anchors": [null, ${Assign}, null, ${Close-1}, ${Re-Open}, ${Start Work}, null, null]
    },
    {
      "stateId": ${Closed},
      "x": 900,
      "y": 600,
      "anchors": [null, ${Close-1}, null, null, ${Close}, null, null, ${Re-Open}]
    },
    {
      "stateId": ${On Hold},
      "x": 80,
      "y": 510,
      "anchors": [null, ${Resume}, null, null, null, ${Pause}, null, null]
    },
    {
      "stateId": ${Processing},
      "x": 360,
      "y": 80,
      "anchors": [null, null, null, ${Request}, null, ${Submit}, null, null]
    },
    {
      "stateId": ${Rejected},
      "x": 950,
      "y": 150,
      "anchors": [null, null, null, null, null, null, null, ${Reject}]
    },
    {
      "stateId": ${Requested},
      "x": 640,
      "y": 150,
      "anchors": [null, ${Request}, null, ${Reject}, null, ${Approve}, null, null]
    },
    {
      "stateId": ${Resolved},
      "x": 360,
      "y": 700,
      "anchors": [null, ${Resolve}, null, null, null, ${Close}, null, null]
    },
    {
      "stateId": ${Submitted},
      "x": 360,
      "y": 220,
      "anchors": [null, ${Submit}, null, ${Approve}, null, ${Assign}, null, null]
    },
    {
      "stateId": ${Work in Progress},
      "x": 360,
      "y": 530,
      "anchors": [${Resume}, ${Start Work}, null, null, null, ${Resolve}, ${Pause}, null]
    }
  ],
  "zoom": 1,
  "version": "0"
}';

UPDATE StateFlow SET DIAGRAM_JSON = @WO_JSON WHERE ORGID=${orgId} AND ID=${stateflowId};
