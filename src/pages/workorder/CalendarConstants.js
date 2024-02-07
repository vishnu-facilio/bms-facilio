const calendarSettings = {
  viewSettings: {
    MONTH: {
      displayType: 'dateTime',
      gridLines: 'DAY',
      grouping: 'WEEK',
      gridLinesFormat: 'DD',
    },
    YEAR: {
      displayType: 'dateTime',
      gridLines: 'WEEK',
      grouping: 'MONTH',
    },
    WEEK: { displayType: 'dateTime', gridLines: '1H', grouping: 'DAY' },
    DAY: { displayType: 'dateTime', gridLines: '1H', grouping: 'NONE' },
  },
  moveType: 'single', //just pass everything for now
}
const timeMetricConstants = ['planned', 'actual', 'due', 'resolved']

const PlannerTypes = {
  ASSET_PLANNER: 1,
  STAFF_PLANNER: 2,
  SPACE_PLANNER: 3,
}

export { calendarSettings, timeMetricConstants, PlannerTypes }
