const defaultFormats = {
  number: {
    type: 'number',
    unit: {
      label: 'auto',
      labelId: 1,
      value: null,
    },
    decimal: 0,
    negation: '-',
    separator: {
      thousand: ',',
    },
  },
  decimal: {
    type: 'decimal',
    unit: {
      label: 'auto',
      labelId: 1,
      value: null,
    },
    decimal: 2,
    negation: '-',
    separator: {
      thousand: ',',
      decimal: '.',
    },
  },
  currency: {
    type: 'currency',
    currency: {
      value: '$',
      position: 0, // array position
    },
    unit: {
      label: 'auto',
      labelId: 1,
      value: null,
    },
    decimal: 0,
    negation: '-',
    separator: {
      thousand: ',',
      decimal: '.',
    },
  },
}
export default {
  getDefaultFormat() {
    return defaultFormats
  },
}
