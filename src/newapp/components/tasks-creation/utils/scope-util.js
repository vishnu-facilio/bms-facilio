import { isEmpty } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'

export const JOB_PLAN_SCOPE = {
  1: 'ASSETS',
  2: 'SPACES',
  3: 'ASSETCATEGORY',
  4: 'SPACECATEGORY',
  5: 'BUILDINGS',
  6: 'SITES',
  7: 'FLOORS',
}

export const JOB_PLAN_SECTION_SCOPE = {
  1: {
    'Every Asset': 5,
  },
  2: {
    'Asset Category': 4,
    'Every Space': 5,
  },
  3: {
    'Every Asset': 5,
  },
  4: {
    'Asset Category': 4,
    'Every Space': 5,
  },
  5: {
    'Space Category': 3,
    'Asset Category': 4,
    'Every Building': 5,
  },
  6: {
    'Space Category': 3,
    'Asset Category': 4,
    'All Buildings': 7,
    'Every Site': 5,
  },
  7: {
    'Space Category': 3,
    'Asset Category': 4,
    'Every Floor': 5,
  },
}

export const JOB_PLAN_TASK_SCOPE = {
  'Space Category': {
    'Asset Category': 4,
    'Every Space': 5,
  },
  'Asset Category': {
    'Every Asset': 5,
  },
  'All Buildings': {
    'Space Category': 3,
    'Asset Category': 4,
    'Every Building': 5,
  },
  'Every Site': {
    'Space Category': 3,
    'Asset Category': 4,
    'All Buildings': 7,
    'Every Site': 5,
  },
  'Every Space': { 'Asset Category': 4, 'Every Space': 5 },
  'Every Asset': { 'Every Asset': 5 },
  'Every Building': {
    'Space Category': 3,
    'Asset Category': 4,
    'Every Building': 5,
  },
  'Every Floor': {
    'Space Category': 3,
    'Asset Category': 4,
    'Every Floor': 5,
  },
}

export const SCOPE_LOOKUP_FIELDS = {
  3: {
    isDataLoading: false,
    options: [],
    config: {},
    lookupModuleName: 'spacecategory',
    placeHolderText: 'Select Space Category',
    field: {
      lookupModule: {
        name: 'spacecategory',
        displayName: 'Space Category',
      },
    },
    multiple: false,
  },
  4: {
    isDataLoading: false,
    options: [],
    config: {},
    lookupModuleName: 'assetcategory',
    placeHolderText: 'Select Asset Category',
    field: {
      lookupModule: {
        name: 'assetcategory',
        displayName: 'Asset Category',
      },
    },
    multiple: false,
  },
}
// Adding resource contexts for loading readings for tasks:
export function addResourceParams(formModel, section) {
  let { jobPlanCategory, assetCategory, spaceCategory } = formModel || {}
  let { id: category } = jobPlanCategory || {}
  let jobPlanResource

  if (!isEmpty(category)) {
    if (JOB_PLAN_SCOPE[category] === 'ASSETCATEGORY') {
      jobPlanResource = assetCategory
    }
    if (JOB_PLAN_SCOPE[category] === 'SPACECATEGORY') {
      jobPlanResource = spaceCategory
    }
  }

  let serializedSection = cloneDeep(section)
  let { tasks, jobPlanSectionCategory } = serializedSection || {}
  let resource

  if (JOB_PLAN_SCOPE[jobPlanSectionCategory] === 'ASSETCATEGORY') {
    let { spaceCategory } = section || {}
    let { id = null } = spaceCategory || {}
    resource = id
  } else {
    let { assetCategory } = section || {}
    let { id = null } = assetCategory || {}
    resource = id
  }
  let sectionResource = resource

  let serializedTasks = tasks.map(task => {
    let { jobPlanTaskCategory } = task || {}
    let resource

    if (jobPlanTaskCategory === 3) {
      let { spaceCategory } = task || {}
      let { id = null } = spaceCategory || {}
      resource = id
    } else {
      let { assetCategory } = task || {}
      let { id = null } = assetCategory || {}
      resource = id
    }
    return {
      ...task,
      jobPlanResource,
      sectionResource,
      resource,
      jobPlanCategory: category,
    }
  })
  serializedSection = {
    ...serializedSection,
    tasks: serializedTasks,
    resource,
  }

  return serializedSection
}
