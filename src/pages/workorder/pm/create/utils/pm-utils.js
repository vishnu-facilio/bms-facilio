import { isEmpty } from '@facilio/utils/validation'
import getProperty from 'dlv'

export const JOB_PLAN_SCOPE = {
  ASSETS: 1,
  SPACES: 2,
  ASSETCATEGORY: 3,
  SPACECATEGORY: 4,
  BUILDINGS: 5,
  SITES: 6,
  FLOORS: 7,
}
export const SCOPE_CATEGORY_PLACEHOLDER = {
  ASSETS: 'Assets',
  SPACES: 'Spaces',
  ASSETCATEGORY: 'Assets',
  SPACECATEGORY: 'Spaces',
  BUILDINGS: 'Buildings',
  SITES: 'Sites',
  FLOORS: 'Floors',
}
export const SCOPE_PLACEHOLDER = {
  ASSETS: 'Assets',
  SPACES: 'Spaces',
  ASSETCATEGORY: 'Asset Category',
  SPACECATEGORY: 'Space Category',
  BUILDINGS: 'Buildings',
  SITES: 'Sites',
  FLOORS: 'Floors',
}
export const SCOPE_CATEGORY_SINGULAR_PLACEHOLDER = {
  ASSETS: 'Asset',
  SPACES: 'Space',
  ASSETCATEGORY: 'Asset',
  SPACECATEGORY: 'Space',
  BUILDINGS: 'Building',
  SITES: 'Site',
  FLOORS: 'Floor',
}

export const PUBLISH_STATUS = {
  Unpublish: 1,
  Publish: 2,
  Disable: 3,
  'Pending Revision': 4,
  Revised: 5,
}

export const PUBLISHED_STATUS = {
  1: 'Unpublished',
  2: 'Published',
  3: 'Disabled',
  4: 'Pending Revision',
  5: 'Revised',
}

export const PUBLISHED_STATUS_ICON_HASH = {
  1: 'unpublish',
  2: 'published',
  3: 'disabled',
  4: 'pending-revision',
  5: 'revised',
}

export const PUBLISHED_STATUS_ICON_COLOR_HASH = {
  unpublish: '#ff5533',
  published: '#009945',
  disabled: '#b8c0c6',
  'pending-revision': '#fa942e',
  revised: '#71818e',
}

export const FREQUENCY_HASH = {
  3: 'MONTHLY_DATE',
  4: 'MONTHLY_WEEK',
  5: 'ANNUALLY_DATE',
  6: 'ANNUALLY_WEEK',
  7: 'QUARTERLY_DATE',
  8: 'QUARTERLY_WEEK',
  9: 'HALF_YEARLY_DATE',
  10: 'HALF_YEARLY_WEEK',
}
export const getResourcePlaceholder = (pmRecord, isUpperCase, isSingular) => {
  let placeholder = 'Resource'
  if (!isEmpty(pmRecord)) {
    let { assignmentTypeEnum } = pmRecord || {}
    if (isSingular) {
      placeholder =
        SCOPE_CATEGORY_SINGULAR_PLACEHOLDER[assignmentTypeEnum] || 'Resource'
    } else {
      placeholder = SCOPE_CATEGORY_PLACEHOLDER[assignmentTypeEnum] || 'Resource'
    }
  }
  if (!isUpperCase) {
    placeholder = placeholder.toLowerCase()
  }
  return placeholder
}

export const getPlaceholderText = props => {
  let { pmRecord, text, isUpperCase, modifyText, replaceAll, isSingular } =
    props || {}
  let resourcePlaceholder = getResourcePlaceholder(
    pmRecord,
    isUpperCase,
    isSingular
  )
  let replaceableText = 'Resources'
  if (!isUpperCase) {
    replaceableText = 'resources'
    resourcePlaceholder = resourcePlaceholder.toLowerCase()
  }
  if (!isEmpty(modifyText)) {
    replaceableText = modifyText
  }
  if (replaceAll) return text.replaceAll(replaceableText, resourcePlaceholder)
  else return text.replace(replaceableText, resourcePlaceholder)
}

export function getCategoryFilter(pmRecord, category) {
  let categoryHash = { 3: 'assetCategory', 4: 'spaceCategory' }
  let categoryFilter = {}
  let selectedCategory = categoryHash[category] || {}

  if (!isEmpty(selectedCategory)) {
    let categoryId = getProperty(pmRecord, `${selectedCategory}.id`, null)

    if (!isEmpty(categoryId)) {
      categoryFilter = {
        [selectedCategory]: { operatorId: 54, value: [`${categoryId}`] },
      }
    }
  }
  return categoryFilter
}
