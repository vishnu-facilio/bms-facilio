import Vue from 'vue'

export const viewStateMeta = {
  HOURLY: {
    operatorID: 22,
    pickerTabName: 'H',
    displayName: 'H',
    start: null,
    end: null,
    frequencyId: 8,
  },
  DAILY: {
    operatorID: 31,
    pickerTabName: 'D',
    displayName: 'D',
    start: null,
    end: null,
    frequencyId: 1,
  },
  WEEKLY: {
    operatorID: 28,
    pickerTabName: 'W',
    displayName: 'W',
    start: null,
    end: null,
    frequencyId: 2,
  },
  MONTHLY: {
    operatorID: 44,
    pickerTabName: 'M',
    displayName: 'M',
    start: null,
    end: null,
    frequencyId: 3,
  },
  YEARLY: {
    operatorID: 45,
    pickerTabName: 'Y',
    displayName: 'Y',
    start: null,
    end: null,
    frequencyId: 6,
  },
}

export const getModules = () => {
  return [
    {
      label: 'FDD',
      moduleName: Vue.prototype.$helpers.isLicenseEnabled('NEW_ALARMS')
        ? 'newreadingalarm'
        : 'alarm',
      license: 'ALARMS',
    },
    {
      label: 'Building performance',
      moduleName: 'energydata',
      license: 'ENERGY',
    },
    {
      label: 'Contracts',
      moduleName: 'contracts',
      list: [],
      expand: false,
      license: 'CONTRACT',
    },
    {
      label: 'Contact',
      moduleName: 'contact',
      list: [],
      expand: false,
      license: 'VISITOR',
    },
    {
      label: 'Work Order Cost',
      moduleName: 'workorderCost',
      license: 'MAINTENANCE',
    },
  ]
}
