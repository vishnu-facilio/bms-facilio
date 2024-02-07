import moment from 'moment-timezone'
const dateOperators = [
  {
    index: 0,
    label: 'Today',
    queryStr: 'Today',
    option: 'D',
    value: 22,
    uptoNowValue: 43,
    time: [
      moment()
        .startOf('day')
        .valueOf(),
      moment()
        .endOf('day')
        .valueOf(),
    ],
    timestamp: [
      moment()
        .startOf('day')
        .valueOf(),
      moment()
        .endOf('day')
        .valueOf(),
    ],
  },
  {
    index: 12,
    label: 'Yesterday',
    queryStr: 'Yesterday',
    option: 'D',
    value: 25,
    uptoNowValue: 43,
    time: [
      moment()
        .subtract(1, 'day')
        .startOf('day')
        .valueOf(),
      moment()
        .subtract(1, 'day')
        .endOf('day')
        .valueOf(),
    ],
    timestamp: [
      moment()
        .subtract(1, 'day')
        .startOf('day')
        .valueOf(),
      moment()
        .subtract(1, 'day')
        .endOf('day')
        .valueOf(),
    ],
  },
  {
    index: 6,
    label: 'Last 7 Days',
    option: 'C',
    value: 49,
    uptoNowValue: 49,
    days: '7',
    time: [
      moment()
        .subtract(6, 'day')
        .startOf('day')
        .valueOf(),
      moment()
        .subtract(0, 'day')
        .endOf('day')
        .valueOf(),
    ],
    timestamp: [
      moment()
        .subtract(6, 'day')
        .startOf('day')
        .valueOf(),
      moment()
        .subtract(0, 'day')
        .endOf('day')
        .valueOf(),
    ],
  },
  {
    index: 7,
    label: 'Last 10 Days',
    option: 'C',
    value: 49,
    uptoNowValue: 49,
    days: '10',
    time: [
      moment()
        .subtract(9, 'day')
        .startOf('day')
        .valueOf(),
      moment()
        .subtract(0, 'day')
        .endOf('day')
        .valueOf(),
    ],
    timestamp: [
      moment()
        .subtract(9, 'day')
        .startOf('day')
        .valueOf(),
      moment()
        .subtract(0, 'day')
        .endOf('day')
        .valueOf(),
    ],
  },
  {
    index: 8,
    label: 'Last 15 Days',
    option: 'C',
    value: 49,
    uptoNowValue: 49,
    days: '15',
    time: [
      moment()
        .subtract(14, 'day')
        .startOf('day')
        .valueOf(),
      moment()
        .subtract(0, 'day')
        .endOf('day')
        .valueOf(),
    ],
    timestamp: [
      moment()
        .subtract(14, 'day')
        .startOf('day')
        .valueOf(),
      moment()
        .subtract(0, 'day')
        .endOf('day')
        .valueOf(),
    ],
  },
  {
    index: 9,
    label: 'Last 30 Days',
    option: 'C',
    value: 49,
    days: '30',
    uptoNowValue: 49,
    time: [
      moment()
        .subtract(29, 'day')
        .startOf('day')
        .valueOf(),
      moment()
        .subtract(0, 'day')
        .endOf('day')
        .valueOf(),
    ],
    timestamp: [
      moment()
        .subtract(29, 'day')
        .startOf('day')
        .valueOf(),
      moment()
        .subtract(0, 'day')
        .endOf('day')
        .valueOf(),
    ],
  },
  {
    index: 1,
    label: 'This Week',
    queryStr: 'Current Week',
    option: 'W',
    value: 31,
    uptoNowValue: 47,
    time: [
      moment()
        .startOf('week')
        .valueOf(),
      moment()
        .endOf('week')
        .valueOf(),
    ],
    timestamp: [
      moment()
        .startOf('week')
        .valueOf(),
      moment()
        .endOf('week')
        .valueOf(),
    ],
  },
  {
    index: 11,
    label: 'Last Week',
    queryStr: 'Last Week',
    trimLabel: 'W',
    option: 'W',
    value: 30,
    uptoNowValue: 47,
    time: [
      moment()
        .subtract(1, 'week')
        .startOf('eek')
        .valueOf(),
      moment()
        .subtract(1, 'week')
        .endOf('week')
        .valueOf(),
    ],
    timestamp: [
      moment()
        .subtract(1, 'week')
        .startOf('eek')
        .valueOf(),
      moment()
        .subtract(1, 'week')
        .endOf('week')
        .valueOf(),
    ],
  },
  {
    index: 2,
    label: 'This Month',
    queryStr: 'Current Month',
    option: 'M',
    value: 28,
    uptoNowValue: 48,
    time: [
      moment()
        .startOf('month')
        .valueOf(),
      moment()
        .endOf('month')
        .valueOf(),
    ],
    timestamp: [
      moment()
        .startOf('month')
        .valueOf(),
      moment()
        .endOf('month')
        .valueOf(),
    ],
  },
  {
    index: 3,
    label: 'Last Month',
    queryStr: 'Last Month',
    trimLabel: 'M',
    value: 27,
    option: 'M',
    uptoNowValue: 48,
    time: [
      moment()
        .subtract(1, 'month')
        .startOf('month')
        .valueOf(),
      moment()
        .subtract(1, 'month')
        .endOf('month')
        .valueOf(),
    ],
    timestamp: [
      moment()
        .subtract(1, 'month')
        .startOf('month')
        .valueOf(),
      moment()
        .subtract(1, 'month')
        .endOf('month')
        .valueOf(),
    ],
  },
  {
    index: 4,
    label: 'Range',
    option: 'R',
    value: 100,
    uptoNowValue: 100,
    time: [
      moment()
        .startOf('year')
        .valueOf(),
      moment()
        .endOf('year')
        .valueOf(),
    ],
    timestamp: [
      moment()
        .startOf('year')
        .valueOf(),
      moment()
        .endOf('year')
        .valueOf(),
    ],
  },
  {
    index: 3,
    label: 'This Year',
    queryStr: 'Current Year',
    option: 'Y',
    value: 44,
    uptoNowValue: 46,
    time: [
      moment()
        .startOf('year')
        .valueOf(),
      moment()
        .endOf('year')
        .valueOf(),
    ],
    timestamp: [
      moment()
        .startOf('year')
        .valueOf(),
      moment()
        .endOf('year')
        .valueOf(),
    ],
  },
  {
    index: 3,
    label: 'Last Year',
    queryStr: 'Last Year',
    option: 'Y',
    value: 45,
    uptoNowValue: 45,
    time: [
      moment()
        .subtract(1, 'year')
        .startOf('year')
        .valueOf(),
      moment()
        .subtract(1, 'year')
        .endOf('year')
        .valueOf(),
    ],
    timestamp: [
      moment()
        .subtract(1, 'year')
        .startOf('year')
        .valueOf(),
      moment()
        .subtract(1, 'year')
        .subtract(1, 'year')
        .endOf('year')
        .valueOf(),
    ],
  },
]
export default {
  methods: {
    getDateOperatorFromId(id, value) {
      return dateOperators.find(
        operator => operator.value === id && (!value || operator.days === value)
      )
    },
    getdateOperators() {
      return dateOperators.filter(rt => rt.option !== ('C' || 'R'))
    },
  },
  dateOperators,
}
