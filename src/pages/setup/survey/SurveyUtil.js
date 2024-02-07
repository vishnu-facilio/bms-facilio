import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { Message } from 'element-ui'
import $helpers from 'src/util/helpers.js'
import setProperty from 'dset'

export const responseStatusHash = {
  1: 'DISABLED',
  2: 'NOT_ANSWERED',
  3: 'PARIALLY_ANSWERED',
  4: 'COMPLETED',
}

//returns moduleId Key for using in list API  params
// export function getModuleIdKey(moduleName) {
//   let moduleIdKey = ''
//   if (!isEmpty(moduleName)) {
//     //Add Checks for modules like workorder,vendorcontact etc...
//     if (moduleName === 'workorder') {
//       moduleName = 'workOrder'
//     }

//     return `${moduleName}Id`
//   }
//   return moduleIdKey
// }

//loads all surveys
/*
parentModuleName: parentModuleName(eg: workorder)
recordId: record Id of parent module (eg:workOrderId)
type: define type of survey list needed (viewAllSurveys/viewAllSurveyResults)
*/
export async function loadAllSurveys(
  parentModuleName,
  recordId,
  type,
  page,
  perPage,
  viewName,
  clientCriteria,
  filters
) {
  let moduleName = 'surveyResponse'
  let url = 'v3/modules/data/list'
  let params = {
    page: page,
    perPage: perPage,
    withCount: true,
    viewName: viewName,
    force: false,
    moduleName,
  }

  if (!isEmpty(type)) {
    params = { ...params, ...type }
  }
  if (!isEmpty(parentModuleName)) {
    params = { ...params, currentModuleName: parentModuleName }
  }

  if (!isEmpty(recordId)) {
    params = { ...params, recordId }
  }

  if (!isEmpty(clientCriteria)) {
    params = {
      ...params,
      clientCriteria: JSON.stringify(clientCriteria),
    }
  }
  if (!isEmpty(filters)) {
    params = { ...params, filters: JSON.stringify(filters) }
  }

  let { error, data } = await API.get(url, params)
  if (error) {
    Message.error(error.message || 'Error Occured')
  } else {
    if (!isEmpty(data)) {
      let { [moduleName]: surveys = [] } = data || {}
      return surveys
    }
  }
  return []
}

//loads all surveys
/*
parentModuleName: parentModuleName(eg: workorder)
*/
export async function loadAllTemplates(
  parentModuleName,
  page,
  perPage,
  viewName
) {
  let url = 'v3/modules/data/list'
  let moduleName = 'surveyTemplate'
  let params = {
    page: page,
    perPage: perPage,
    withCount: true,
    viewName: viewName,
    force: false,
    moduleName,
  }

  if (!isEmpty(parentModuleName)) {
    params = { ...params, currentModuleName: parentModuleName }
  }

  let { error, data } = await API.get(url, params)
  if (error) {
    Message.error(error.message || 'Error Occured')
  } else {
    if (!isEmpty(data)) {
      let { [moduleName]: templates = [] } = data || {}
      return templates
    }
  }
  return []
}

export async function getSurveyDetails(selectedSurvey) {
  let { id } = selectedSurvey || {}
  let responseModuleName = 'surveyResponse'
  let params = { id, moduleName: responseModuleName }

  let { error, data } = await API.get('v3/modules/data/summary', params)
  if (error) {
    Message.error('Error Occured')
  } else {
    if (!isEmpty(data)) {
      let { [responseModuleName]: survey } = data || {}
      return survey
    }
  }
  return {}
}

// export async function getTemplateDetails(moduleName, template) {
//   let { id } = template || {}
//   let responseTemplateName = 'surveyTemplate'
//   let params = { id, moduleName: responseTemplateName }

//   let { error, data } = await API.get('v3/modules/data/summary', params)
//   if (error) {
//     Message.error(error.message || 'Error Occured')
//   } else {
//     if (!isEmpty(data)) {
//       let { [responseTemplateName]: template = {} } = data || {}
//       return template
//     }
//   }
//   return {}
// }

export async function loadSurveyForSurveyBar(
  force = false,
  moduleName,
  recordId,
  surveyDetails
) {
  if (checkIfLicenseEnabled() && !isEmpty(recordId)) {
    let { error, data } = await API.get(
      `v2/survey/fetch/${moduleName}/${recordId}`,
      {
        force,
      }
    )
    if (error) {
      this.$message(error.message || 'Error Occured')
    } else {
      if (!isEmpty(data)) {
        let survey = { ...surveyDetails, ...data }
        let { response = [] } = survey || {}
        let answeredSurveys = []
        if (!isEmpty(response)) {
          response.forEach(survey => {
            //filtering partially answered and completed surveys
            if (survey.responseStatus > 2) {
              answeredSurveys.push(survey)
            }
          })
        }
        setProperty(survey, 'answeredSurveys', answeredSurveys)
        return survey
      }
    }
  }
  return {
    answeredSurveys: [],
  }
}

function checkIfLicenseEnabled() {
  let { isLicenseEnabled } = $helpers
  return isLicenseEnabled('SURVEY')
}
