import router from 'src/router'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { Message } from 'element-ui'

export async function redirectUrlAction(params) {
  let { url, record, type, moduleName } = params
  let finalUrl = url
  if (!isEmpty(record)) {
    const URL = 'v2/modules/rules/replacePlaceHolders'
    let { data } = await API.post(URL, {
      moduleName: moduleName,
      recordId: record.id,
      formattedString: url,
    })
    finalUrl = data.encodedReplacedString
  }

  if (!isEmpty(finalUrl)) {
    urlAction(finalUrl, type, moduleName, record)
  }
}
export async function internalUrlAction(args, canCreate) {
  if (isWebTabsEnabled()) {
    let { config, record, type, moduleName: currentModuleName } = args
    let { navigateTo } = config
    let formattedString = JSON.stringify(config)
    let { moduleName } = config
    let URL = 'v2/modules/rules/replacePlaceHolders'
    let { data } = await API.post(URL, {
      moduleName: currentModuleName,
      recordId: record.id,
      formattedString: formattedString,
    })
    let { replacedString, replacedJson } = data || {}
    let replacedObj
    if (!isEmpty(replacedJson)) {
      replacedObj = replacedJson
    } else {
      replacedObj = JSON.parse(replacedString)
    }

    let finalUrl = ''

    if (navigateTo === 'Form') {
      if (!canCreate) {
        Message.error(`You don't have permission to create a ${moduleName}`)
        return
      } else {
        let { formDataJson } = replacedObj
        let queryString = ''
        if (!isEmpty(formDataJson)) {
          if (isEmpty(replacedJson)) {
            formDataJson = JSON.parse(formDataJson)
          }
        }
        let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}
        if (!isEmpty(name)) {
          let { href } = router.resolve({ name }) || {}
          finalUrl = `${href}`
          if (!isEmpty(formDataJson)) {
            queryString = encodeURIComponent(JSON.stringify(formDataJson))
            finalUrl = `${finalUrl}?formDetails=${queryString}`
          }
        } else {
          Message.error(`${moduleName} is not available in this app`)
        }
      }
    } else {
      let { recordId } = replacedObj
      if (isEmpty(recordId)) {
        Message.error(`There is no record id/${moduleName} found`)
        return
      } else {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (isEmpty(name)) {
          Message.error(`${moduleName} is not available in this app`)
          return
        } else {
          let params = { viewname: 'all', id: recordId }
          let { href } = router.resolve({ name, params }) || {}

          finalUrl = encodeURI(href)
        }
      }
    }

    if (!isEmpty(finalUrl)) {
      urlAction(finalUrl, type, moduleName, record)
    }
  } else {
    Message.error('This custom button is configured for webTabEnabled Apps')
  }
}

function urlAction(url, type, moduleName, record) {
  if (url) {
    if (type === redirectType.DIFFERENT_TAB) {
      window.open(url, '_blank', 'noopener,noreferrer')
    } else if (type === redirectType.CURRENT_TAB) {
      window.location.replace(url)
    }
  } else if (isEmpty(url) && moduleName) {
    let routerData = getRoute(moduleName, record)

    if (type === redirectType.CURRENT_TAB) {
      router.push({ path: routerData.href })
    } else if (type === redirectType.DIFFERENT_TAB) {
      window.open(routerData.href, '_blank', 'noopener,noreferrer')
    }
  }
}
export const redirectType = {
  CURRENT_TAB: 'CURRENT_TAB',
  DIFFERENT_TAB: 'DIFFERENT_TAB',
  POPUP: 'POPUP',
}

function getRoute(moduleName, record) {
  let recordId = record.id
  let routes = {
    asset: {
      name: 'assetsummary',
      params: { viewname: 'all', assetid: recordId },
    },
    workorder: {
      name: 'wosummarynew',
      params: { id: recordId },
    },
    alarms: {
      path: `/app/fa/faults/all/newsummary/${recordId}`,
    },
    tenant: {
      name: 'tenantSummary',
      params: { viewName: 'all', id: recordId },
    },
  }
  return router.resolve(routes[moduleName])
}
