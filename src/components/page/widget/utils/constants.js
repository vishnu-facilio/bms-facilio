import { isEmpty } from '@facilio/utils/validation'

export const getDefaultFields = dataObj => {
  let formulaField = [
    {
      displayName: 'Description',
      displayTypeEnum: 'TEXTAREA',
      name: 'description',
    },
    {
      displayName: 'Site',
      displayTypeEnum: 'LOOKUP_SIMPLE',
      name: 'siteId',
      lookupModuleName: 'site',
    },
    {
      displayName: 'KPI Category',
      displayTypeEnum: 'LOOKUP_SIMPLE',
      name: 'kpiCategory',
      lookupModuleName: 'kpiCategory',
    },
    {
      displayName: 'Frequency',
      displayTypeEnum: 'NUMBER',
      name: 'frequencyEnum',
    },
  ]

  if (!isEmpty(dataObj.spaceCategoryId)) {
    formulaField.push({
      displayName: 'Space Category',
      displayTypeEnum: 'LOOKUP_SIMPLE',
      name: 'spaceCategoryId',
      lookupModuleName: 'spaceCategory',
    })
  }

  if (!isEmpty(dataObj.assetCategoryId)) {
    formulaField.push({
      displayName: 'Asset Category',
      displayTypeEnum: 'LOOKUP_SIMPLE',
      name: 'assetCategoryId',
      lookupModuleName: 'assetCategory',
    })
  }

  if (
    !isEmpty(dataObj.includedResources) &&
    dataObj.includedResources.length === 1
  ) {
    formulaField.push({
      displayName: 'Asset',
      displayTypeEnum: 'LOOKUP_SIMPLE',
      name: 'matchedResources',
      lookupModuleName: 'asset',
    })
  }

  if (
    isEmpty(dataObj.assetCategoryId) &&
    !isEmpty(dataObj.matchedResourcesIds)
  ) {
    formulaField.push({
      displayName: 'Space',
      displayTypeEnum: 'LOOKUP_SIMPLE',
      name: 'matchedResources',
      lookupModuleName: 'space',
    })
  }

  return { formulaField }
}
