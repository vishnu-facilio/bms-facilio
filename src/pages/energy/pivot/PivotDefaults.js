export const defaultTheme = {
  density: 'SMALL',
  name: 'Default',
  class: 'default',
  grid: 'both',
  stripe: false,
  fontSize: 'medium',
  number: false,
}
export const pivotThemeOptions = [
  {
    name: 'Default',
    class: 'default',
  },
  {
    class: 'blue',
    name: 'Blue',
  },
  {
    class: 'teal',
    name: 'Teal',
  },
  {
    class: 'purple',
    name: 'Purple',
  },
  {
    class: 'black-orange',
    name: 'Black orange',
  },
  {
    class: 'bright-orange',
    name: 'Bright orange',
  },
  {
    class: 'yellow',
    name: 'Yellow',
  },
  {
    class: 'green',
    name: 'Green',
  },
  {
    class: 'mid-night',
    name: 'Mid night',
  },
  {
    class: 'mac-os',
    name: 'Mac OS',
  },
]

export const datePeriodOptions = [
  {
    dateOperator: -1,
    label: 'None',
  },
  {
    dateOperator: 22,
    label: 'Today',
  },
  {
    dateOperator: 25,
    label: 'Yesterday',
  },
  {
    dateOperator: 31,
    label: 'This week',
  },
  {
    dateOperator: 30,
    label: 'Last week',
  },
  {
    dateOperator: 28,
    label: 'This month',
  },
  { dateOperator: 66, label: 'This Month Till Yesterday' },
  {
    dateOperator: 27,
    label: 'Last month',
  },
  {
    dateOperator: 68,
    label: 'This Quarter',
  },
  {
    dateOperator: 69,
    label: 'Last Quarter',
  },
  {
    dateOperator: 44,
    label: 'Current Year',
  },
  {
    dateOperator: 45,
    label: 'Last Year',
  },
  {
    dateOperator: 20,
    label: 'Custom',
  },
]

export const defaultColFormat = {
  dataColumn: {
    width: 150,
    autoWidth: true,
    label: null,
    isFixed: false,
    textWarp: false,
    hideColumn: false,
  },
  rowColumn: {
    width: 200,
    autoWidth: true,
    label: null,
    isFixed: false,
    textWarp: false,
    hideColumn: false,
  },
  formulaColumn: {
    width: 200,
    autoWidth: true,
    label: null,
    isFixed: false,
    textWarp: false,
    hideColumn: false,
  },
}

export const timeAggregation = [
  { label: 'Full Date', name: 'Actual', value: 0, enumValue: null },
  { label: 'Hourly', name: 'Hourly', value: 20, enumValue: 'hourly' },
  { label: 'Daily', name: 'Daily', value: 12, enumValue: 'daily' },
  { label: 'Weekly', name: 'Weekly', value: 11, enumValue: 'weekly' },
  { label: 'Monthly', name: 'Monthly', value: 10, enumValue: 'monthly' },
  { label: 'Quarterly', name: 'Quarterly', value: 25, enumValue: 'quarterly' },
  { label: 'Yearly', name: 'Yearly', value: 8, enumValue: 'yearly' },
  {
    label: 'Hour of day',
    name: 'Hour of day',
    value: 19,
    enumValue: 'hourofday',
  },
  {
    label: 'Day of week',
    name: 'Day of week',
    value: 17,
    enumValue: 'dayofweek',
  },
  {
    label: 'Day of month',
    name: 'Day of month',
    value: 18,
    enumValue: 'dayofmonth',
  },
]

export const aggrOptions = [
  {
    label: 'Count',
    value: 1,
  },
  {
    label: 'Avg',
    value: 2,
  },
  {
    label: 'Sum',
    value: 3,
  },
  {
    label: 'Min',
    value: 4,
  },
  {
    label: 'Max',
    value: 5,
  },
]
