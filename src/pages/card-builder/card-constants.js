import Vue from 'vue'
export const dateOperators = [
  {
    label: 'Yesterday',
    value: 'Yesterday',
    enumValue: 25,
  },
  {
    label: 'Today',
    value: 'Today',
    enumValue: 22,
  },
  {
    label: 'Today Up to Now',
    value: 'today upto now',
    enumValue: 43,
  },
  {
    label: 'Till Now',
    value: 'Till Now',
    enumValue: 72,
  },
  {
    label: 'Last Week',
    value: 'Last Week',
    enumValue: 30,
  },
  {
    label: 'This Week',
    value: 'Current Week',
    enumValue: 31,
  },
  {
    label: 'Last Month',
    value: 'Last Month',
    enumValue: 27,
  },
  {
    label: 'This Month',
    value: 'Current Month',
    enumValue: 28,
  },
  {
    label: 'Last Year',
    value: 'Last Year',
    enumValue: 45,
  },
  {
    label: 'This Year',
    value: 'Current Year',
    enumValue: 44,
  },
  {
    label: 'This Quarter',
    value: 'This Quarter',
    enumValue: 68,
  },
  {
    label: 'Last Quarter',
    value: 'Last Quarter',
    enumValue: 69,
  },
  {
    label: 'This Week Until Now',
    value: 'Current Week upto now',
    enumValue: 47,
  },
  {
    label: 'This Month Until Now',
    value: 'Current Month upto now',
    enumValue: 48,
  },
  {
    label: 'This Year Until Now',
    value: 'Current Year upto now',
    enumValue: 46,
  },
  {
    label: 'This Year Until Last Month',
    value: 'Current year upto last month',
    enumValue: 80,
  },
]
export const aggregateFunctions = [
  {
    label: 'Sum',
    value: 'sum',
    enumValue: 3,
  },
  {
    label: 'Avg',
    value: 'avg',
    enumValue: 2,
  },
  {
    label: 'Min',
    value: 'min',
    enumValue: 4,
  },
  {
    label: 'Max',
    value: 'max',
    enumValue: 5,
  },
  {
    label: 'Last Value',
    value: 'lastValue',
  },
]
export const decimalPlaces = {
  Auto: -1,
  '0': 0,
  '1': 1,
  '2': 2,
  '3': 3,
  '4': 4,
}
export const kpiTypes = {
  Reading: 'reading',
  Module: 'module',
}
export const unitPosition = {
  Prefix: 1,
  Suffix: 2,
}
export const periodAggregateFunctions = {
  Hourly: 20,
  Daily: 12,
  Monthly: 10,
  'High-res': 0,
}
export const locationModules = {
  Site: 'site',
  Building: 'building',
  Asset: 'asset',
}
export const locationFields = {
  'Current Location': 'currentLoction',
  'Geo Location': 'geoLoaction',
  Location: 'location',
}
export const markerValues = {
  'No of Workoders': 'noOfWorkorders',
  'No of Alarms': 'noOfAlarms',
  // 'No of Buildings': 'noOfBuildings',
  // 'No of Floors': 'noOfFloors',
  // 'Total Energy Consumption': 'totalEnergy',
}
export const iconTypes = {
  Default: 1,
  Simple: 2,
  stick: 3,
  Radious: 4,
}
export const predefinedColors = [
  '#ffffff',
  '#663383',
  '#933f95',
  '#612b9b',
  '#492fa9',
  '#1d2da0',
  '#2e52ad',
  '#3964c5',
  '#1f95da',
  '#218fb8',
  '#15a7a6',
  '#f7768c',
  '#ec598c',
  '#c95db4',
  '#a450c1',
  '#9339b3',
  '#7e45bc',
  '#8260bd',
  '#5f7dc5',
  '#5fa0c5',
  '#6bc7c2',
  '#5ab6b3',
  '#69d0aa',
  '#86de9b',
  '#a3de86',
  '#d0de86',
  '#efd880',
  '#efc580',
  '#ee9671',
  '#ee8171',
  '#ee7171',
  '#000000',
]

export const graphicIcons = [
  {
    name: 'auto',
    path: 'svgs/cardbuilder/icons/auto',
  },
  {
    name: 'manual',
    path: 'svgs/cardbuilder/icons/manual',
  },
  {
    name: 'bell',
    path: 'svgs/cardbuilder/icons/bell',
    activePath: 'svgs/cardbuilder/icons/bell_active',
    activeClass: '',
  },
  {
    name: 'bulb',
    path: 'svgs/cardbuilder/icons/bulb',
    activePath: 'svgs/cardbuilder/icons/bulb_on',
    activeClass: '',
  },
  {
    name: 'fan',
    path: 'svgs/cardbuilder/icons/fan',
    activePath: 'svgs/cardbuilder/icons/fan_active',
    activeClass: 'icon-animation spin',
  },
  {
    name: 'supplyfan',
    path: 'svgs/cardbuilder/icons/supplyfan',
    activePath: 'svgs/cardbuilder/icons/supplyfan_active',
    activeClass: 'icon-animation spin',
  },
  {
    name: 'motor',
    path: 'svgs/cardbuilder/icons/motor',
    className: 'icon-xl',
    activePath: 'svgs/cardbuilder/icons/motor_active',
    activeClass: '',
  },
  {
    name: 'power',
    path: 'svgs/cardbuilder/icons/energy',
    activePath: 'svgs/cardbuilder/icons/energy_active',
    activeClass: '',
  },
  {
    name: 'energy',
    path: 'svgs/cardbuilder/icons/energy_alt',
    activePath: 'svgs/cardbuilder/icons/energy_alt_active',
    activeClass: '',
  },
  {
    name: 'thermometer',
    path: 'svgs/cardbuilder/icons/thermometer',
  },
  {
    name: 'pressure',
    path: 'svgs/cardbuilder/icons/pressure',
    className: 'icon-xl',
    activePath: 'svgs/cardbuilder/icons/pressure_active',
    activeClass: '',
  },
  {
    name: 'multigauge',
    path: 'svgs/cardbuilder/icons/pressure',
    className: 'icon-xl',
    activeClass: '',
  },
  {
    name: 'battery',
    path: 'svgs/cardbuilder/icons/battery',
    activePath: 'svgs/cardbuilder/icons/battery_active',
    activeClass: '',
  },
  {
    name: 'carbon',
    path: 'svgs/cardbuilder/icons/carbon',
    activePath: 'svgs/cardbuilder/icons/carbon_active',
    activeClass: '',
  },
  {
    name: 'carbon_alt',
    path: 'svgs/cardbuilder/icons/carbon_alt',
    activePath: 'svgs/cardbuilder/icons/carbon_alt_active',
    activeClass: '',
  },
  {
    name: 'humidity',
    path: 'svgs/cardbuilder/icons/humidity',
    activePath: 'svgs/cardbuilder/icons/humidity_active',
    activeClass: '',
  },
  {
    name: 'humidity_alt',
    path: 'svgs/cardbuilder/icons/humidity_alt',
    activePath: 'svgs/cardbuilder/icons/humidity_alt_active',
    activeClass: '',
  },
  {
    name: 'nodes',
    path: 'svgs/cardbuilder/icons/nodes',
    activeClass: '',
  },
  {
    name: 'clock',
    path: 'svgs/cardbuilder/icons/clock',
    activePath: 'svgs/cardbuilder/icons/clock_active',
    activeClass: '',
  },
  {
    name: 'wave',
    path: 'svgs/cardbuilder/icons/wave',
    activePath: 'svgs/cardbuilder/icons/wave_active',
    activeClass: '',
  },
  {
    name: 'wave_alt1',
    path: 'svgs/cardbuilder/icons/wave_alt1',
  },
  {
    name: 'wave_alt2',
    path: 'svgs/cardbuilder/icons/wave_alt2',
  },
  {
    name: 'box',
    path: 'svgs/cardbuilder/icons/box',
    activePath: 'svgs/cardbuilder/icons/box_active',
    activeClass: '',
  },
  {
    name: 'drop',
    path: 'svgs/cardbuilder/icons/drop',
    activePath: 'svgs/cardbuilder/icons/drop_active',
    activeClass: '',
  },
  {
    name: 'heatwheel',
    path: 'svgs/cardbuilder/icons/heatwheel',
    activePath: 'svgs/cardbuilder/icons/heatwheel_active',
    activeClass: '',
  },
  {
    name: 'volve',
    path: 'svgs/cardbuilder/icons/volve',
  },
]

export const mapIcons = [
  {
    name: 'Default',
    path: 'svgs/cardbuilder/icons/map_0',
    value: 1,
  },
  {
    name: 'Marker',
    path: 'svgs/cardbuilder/icons/map_1',
    value: 2,
  },
  {
    name: 'Radious',
    path: 'svgs/cardbuilder/icons/map_2',
    value: 3,
  },
  {
    name: 'Pin',
    path: 'svgs/cardbuilder/icons/map_3',
    value: 4,
  },
]

export const moduleIcons = {
  asset: 'svgs/cardbuilder/icons/asset',
  site: 'svgs/cardbuilder/icons/building',
  building: 'svgs/cardbuilder/icons/building',
}
export const modules = [
  {
    label: 'Work Order',
    moduleName: 'workorder',
    license: 'MAINTENANCE',
  },
  {
    label: 'FDD',
    moduleName: 'newreadingalarm',
    license: 'ALARMS',
  },
  {
    label: 'Building performance',
    moduleName: 'energydata',
    license: 'ENERGY',
  },
  {
    label: 'Asset',
    moduleName: 'asset',
    license: 'SPACE_ASSET',
  },
  {
    label: 'Inventory Request',
    moduleName: 'inventoryrequest',
    list: [],
    expand: false,
    license: 'INVENTORY',
  },
  {
    label: 'Item',
    moduleName: 'item',
    list: [],
    expand: false,
    license: 'INVENTORY',
  },
  {
    label: 'Contracts',
    moduleName: 'contracts',
    list: [],
    expand: false,
    license: 'CONTRACT',
  },
  {
    label: 'Purchaseorder',
    moduleName: 'purchaseorder',
    list: [],
    expand: false,
    license: 'PURCHASE',
  },
  {
    label: 'Purchaserequest',
    moduleName: 'purchaserequest',
    list: [],
    expand: false,
    license: 'PURCHASE',
  },
  {
    label: 'Visit',
    moduleName: 'visitorlog',
    list: [],
    expand: false,
    license: 'VISITOR',
  },
  {
    label: 'contact',
    moduleName: 'contact',
    list: [],
    expand: false,
    license: 'VISITOR',
  },
  {
    label: 'Watchlist',
    moduleName: 'watchlist',
    list: [],
    expand: false,
    license: 'VISITOR',
  },
  {
    label: 'Visitor',
    moduleName: 'visitor',
    list: [],
    expand: false,
    license: 'VISITOR',
  },
]
export const getUniqueId = () => {
  return (
    Date.now().toString(36) +
    Math.random()
      .toString(36)
      .substr(2, 5)
  ).toLowerCase()
}
export const tableThemes = [
  {
    label: 'Plain',
    value: 'default',
  },
  {
    label: 'Classic',
    value: 'classic',
  },
  {
    label: 'Classic gray',
    value: 'classicgray',
  },
  {
    label: 'Border',
    value: 'border',
  },
]
export const sortingList = [
  {
    label: 'Ascending',
    value: 'asc',
  },
  {
    label: 'Descending',
    value: 'dsc',
  },
]
