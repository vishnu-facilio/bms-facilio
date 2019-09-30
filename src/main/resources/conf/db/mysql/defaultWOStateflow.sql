SET @WO_JSON = '{
  "states": [
    {
      "stateId": ${Assigned},
      "x": 360,
      "y": 370,
      "anchors": [null, ${Assign}, null, ${Close-1}, null, ${Start Work}, null, null]
    },
    {
      "stateId": ${Closed},
      "x": 790,
      "y": 630,
      "anchors": [null, ${Re-Open}, null, ${Close-1}, null, ${Close}, null, null]
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
      "anchors": [${Resume}, ${Start Work}, null, ${Re-Open}, null, ${Resolve}, ${Pause}, null]
    }
  ],
  "zoom": 1,
  "version": "0"
}';

UPDATE StateFlow SET DIAGRAM_JSON = @WO_JSON WHERE ORGID=${orgId} AND ID=${stateflowId};
