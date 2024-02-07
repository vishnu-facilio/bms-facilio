import router from 'router'
import http from 'util/http'
import Vue from 'vue'
import NewDateHelper from '@/mixins/NewDateHelper'
import { v4 as uuid } from 'uuid'
import { Buffer } from 'buffer'
import {
  isWebTabsEnabled,
  findRouteForTab,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { API } from '@facilio/api'

const FCA_EVENT_PREFIX = key => {
  return 'fca.' + key
}

const MODULE_VIEW = {
  workorder: '/app/wo/orders/${view}',
  alarm: '/app/fa/faults/${view}',
  asset: '/app/at/assets/${view}',
  purchaseorder: '/app/purchase/po/${view}',
  purchaserequest: '/app/purchase/pr/${view}',
  contracts: '/app/ct/purchasecontract/${view}',
  visitorlog: '/app/vi/visits/${view}',
  visitor: '/app/vi/visitor/${view}',
  watchlist: '/app/vi/watchlist/${view}',
}

const MODULE_SUMAARY = {
  workorder: '/app/wo/orders/summary/${id}',
  alarm: '/app/fa/faults/all/newsummary/${id}',
  asset: '/app/at/assets/all/${id}/overview',
}

export default class AppEventChannel {
  constructor(options) {
    this._origin = (options && options.origin) || window.location.host
    this._currentUser = options.user
    this._currentOrg = options.org
    this._activeWidgets = {}
    this._currentApp = options.app

    this._handlers = {
      request: requestHandler,
      api: apiHandler,
      common: commonHandler,
      variables: variablesHandler,
      interface: interfaceHandler,
      liveevent: liveEventHandler,
    }

    window.addEventListener('message', this.handleEvent.bind(this))
  }

  getOrigin() {
    return window.location.protocol + '//' + window.location.host
  }

  attachWidget(widget, context, options, actions, exportMode) {
    let id = uuid()
    let subscribedTopics = {}

    this._activeWidgets[id] = {
      id,
      widget,
      context,
      options,
      actions,
      exportMode,
      subscribedTopics,
    }
    return id
  }

  deattachWidget(uuid) {
    if (this._activeWidgets[uuid]) {
      this.sendEventToInstance(uuid, 'app.destroyed')

      try {
        let subscribedTopics = this._activeWidgets[uuid].subscribedTopics
        if (
          Vue.prototype.$wms &&
          subscribedTopics &&
          Object.keys(subscribedTopics).length
        ) {
          for (let subscribedToken in subscribedTopics) {
            let subscribedTopic = subscribedTopics[subscribedToken]
            Vue.prototype.$wms.unsubscribe(
              subscribedTopic.topic,
              subscribedTopic.handler
            )
          }
        }
        // eslint-disable-next-line no-empty
      } catch {}
      delete this._activeWidgets[uuid]
    }
  }

  sendEventToInstance(instanceUid, key, msg) {
    let data = {
      key: FCA_EVENT_PREFIX(key),
      message: msg,
      instanceUid: instanceUid,
    }
    this._rawPostMessage(instanceUid, data)
  }

  sendEvent(widgetTypes, key, msg) {
    let widgetTypeList = Array.isArray(widgetTypes)
      ? widgetTypeList
      : [widgetTypes]
    for (let widgetType of widgetTypeList) {
      let matchedWidgetKeys = Object.keys(this._activeWidgets).filter(
        key => this._activeWidgets[key].widget.entityTypeEnum === widgetType
      )
      if (matchedWidgetKeys && matchedWidgetKeys.length) {
        for (let matchedKey of matchedWidgetKeys) {
          this.sendEventToInstance(matchedKey, key, msg)
        }
      }
    }
  }

  sendResponse(instanceUid, replyId, key, result, error) {
    let data = {
      id: replyId,
      key: key,
      result: result,
      error: error,
      instanceUid: instanceUid,
    }
    this._rawPostMessage(instanceUid, data)
  }

  _rawPostMessage(instanceUid, data) {
    if (!this._activeWidgets[instanceUid]) return

    let instance = this._activeWidgets[instanceUid]
    instance.options.frame.postMessage(
      JSON.stringify(data),
      instance.options.origin
    )
  }

  _isValidEvent(event) {
    let data = event.data
    if (!data) {
      return false
    }

    if (typeof data === 'string') {
      try {
        data = JSON.parse(event.data)
      } catch (e) {
        return false
      }
    }

    if (!this._activeWidgets[data.instanceUid]) {
      // not an valid connected app widget event
      return false
    }

    let widget = this._activeWidgets[data.instanceUid]
    return (
      widget &&
      widget.options.origin === event.origin &&
      widget.options.frame === event.source
    )
  }

  handleEvent(event) {
    if (!this._isValidEvent(event)) return

    let data = event.data
    if (typeof data === 'string') {
      try {
        data = JSON.parse(event.data)
      } catch (e) {
        return false
      }
    }

    const instanceUid = data.instanceUid
    const id = data.id
    const key = data.key
    const params = data.params
    const instance = this._activeWidgets[instanceUid]

    let handler = key.split('.')[0]
    let eventName = key.split('.')[1]

    if (handler === 'app' && eventName === 'handshake') {
      this.sendEventToInstance(instanceUid, 'app.loaded', {
        widget: instance.widget,
        context: instance.context,
        exportMode: instance.exportMode,
        currentUser: this._currentUser,
        currentOrg: this._currentOrg,
        currentApp: this._currentApp,
      })
    } else if (this._handlers[handler]) {
      this._handlers[handler](instance, eventName, params, this)
        .then(response => {
          this.sendResponse(instanceUid, id, key, response, null)
        })
        .catch(error => {
          this.sendResponse(instanceUid, id, key, null, error)
        })
    }
  }
}

export const requestHandler = (instance, eventName, params) => {
  return new Promise((resolve, reject) => {
    if (eventName === 'invokeFacilioAPI') {
      if (!params.url) {
        reject('Cannot make request to empty url.')
        return
      } else {
        try {
          new URL(params.url)
          reject('Url should not include domain.')
          return
        } catch (err) {
          if (err) {
            // valid facilio url
          }
        }
      }
      params.options = params.options || {}

      let requestOptions = {}
      requestOptions.url = params.url

      requestOptions.method = params.options.method || 'GET'
      if (params.options.params) {
        requestOptions.params = params.options.params
      }
      if (params.options.headers) {
        requestOptions.headers = params.options.headers
      }
      if (params.options.data) {
        requestOptions.data = params.options.data
      }
      if (params.options.contentType) {
        requestOptions.headers = requestOptions.headers || {}
        requestOptions.headers['Content-Type'] = params.options.contentType
      }
      http
        .request(requestOptions)
        .then(response => {
          if (response.data) {
            resolve(response.data)
          } else {
            resolve(null)
          }
        })
        .catch(error => {
          reject(error)
        })
    } else if (eventName === 'invokeConnectorAPI') {
      if (!params.connector) {
        reject('Connector namespace is empty.')
        return
      }
      if (!params.url) {
        reject('Cannot make request to empty url.')
        return
      }

      params.options = params.options || {}

      let requestOptions = {}
      requestOptions.connectedAppId = instance.widget.connectedAppId
      requestOptions.connector = params.connector
      requestOptions.url = params.url

      requestOptions.method = params.options.method || 'GET'
      if (params.options.params) {
        requestOptions.params = params.options.params
      }
      if (params.options.headers) {
        requestOptions.headers = params.options.headers
      }
      if (params.options.data) {
        requestOptions.data = params.options.data
      }
      if (params.options.contentType) {
        requestOptions.contentType = params.options.contentType
      }
      http
        .post('/v2/connectedApps/executeAPI', { apiRequest: requestOptions })
        .then(response => {
          if (response.data.result.apiResponse) {
            resolve(response.data.result.apiResponse)
          } else {
            resolve(null)
          }
        })
        .catch(error => {
          reject(error)
        })
    } else if (eventName === 'invokeExternalAPI') {
      if (!params.url) {
        reject('Cannot make request to empty url.')
        return
      } else {
        try {
          new URL(params.url)
        } catch (err) {
          reject(err)
          return
        }
      }

      params.options = params.options || {}

      let requestOptions = {}
      requestOptions.connectedAppId = instance.widget.connectedAppId
      requestOptions.url = params.url

      requestOptions.method = params.options.method || 'GET'
      if (params.options.params) {
        requestOptions.params = params.options.params
      }
      if (params.options.headers) {
        requestOptions.headers = params.options.headers
      }
      if (params.options.data) {
        requestOptions.data = params.options.data
      }
      if (params.options.contentType) {
        requestOptions.contentType = params.options.contentType
      }
      if (params.options.auth && window.btoa) {
        requestOptions.headers = requestOptions.headers || {}
        requestOptions.headers['Authorization'] =
          'Basic ' +
          window.btoa(
            params.options.auth.username + ':' + params.options.auth.password
          )
      }
      http
        .post('/v2/connectedApps/executeAPI', { apiRequest: requestOptions })
        .then(response => {
          if (response.data.result.apiResponse) {
            resolve(response.data.result.apiResponse)
          } else {
            resolve(null)
          }
        })
        .catch(error => {
          reject(error)
        })
    }
  })
}

export const apiHandler = (instance, eventName, params) => {
  return new Promise((resolve, reject) => {
    if (eventName === 'createRecord') {
      if (!params.moduleName) {
        reject('Module should not be empty.')
        return
      }
      params.options = params.options || {}

      API.createRecord(params.moduleName, params.options).then(response => {
        if (response.error) {
          reject(response)
        } else {
          resolve(response)
        }
      })
    } else if (eventName === 'fetchRecord') {
      if (!params.moduleName) {
        reject('Module should not be empty.')
        return
      }
      params.options = params.options || {}

      API.fetchRecord(params.moduleName, params.options).then(response => {
        if (response.error) {
          reject(response)
        } else {
          resolve(response)
        }
      })
    } else if (eventName === 'fetchAll') {
      if (!params.moduleName) {
        reject('Module should not be empty.')
        return
      }
      params.options = params.options || {}

      API.fetchAll(params.moduleName, params.options).then(response => {
        if (response.error) {
          reject(response)
        } else {
          resolve(response)
        }
      })
    } else if (eventName === 'updateRecord') {
      if (!params.moduleName) {
        reject('Module should not be empty.')
        return
      }
      params.options = params.options || {}

      API.updateRecord(params.moduleName, params.options).then(response => {
        if (response.error) {
          reject(response)
        } else {
          resolve(response)
        }
      })
    } else if (eventName === 'deleteRecord') {
      if (!params.moduleName) {
        reject('Module should not be empty.')
        return
      }
      params.options = params.options || {}
      let idOrArray = params.options.id || null
      if (!idOrArray || (Array.isArray(idOrArray) && !idOrArray.length)) {
        reject('ID should not be empty.')
        return
      }

      API.deleteRecord(params.moduleName, idOrArray).then(response => {
        if (response.error) {
          reject(response)
        } else {
          resolve(response)
        }
      })
    }
  })
}

export const commonHandler = (instance, eventName, params) => {
  return new Promise((resolve, reject) => {
    if (eventName === 'exportAsPDF') {
      if (!params.linkName) {
        reject('Invalid widget linkName.')
        return
      }

      let exportOptions = params.options || {}
      let widgetContext = params.context || {}

      let data = {
        connectedAppId: instance.widget.connectedAppId,
        linkName: params.linkName,
        exportOptions: JSON.stringify(exportOptions),
        widgetContext: JSON.stringify(widgetContext),
      }
      http
        .post('/v2/connectedApps/exportWidgetAsPdf', data)
        .then(response => {
          if (
            response.data &&
            response.data.result &&
            response.data.result.url
          ) {
            resolve(response.data.result)
          } else {
            resolve(null)
          }
        })
        .catch(error => {
          reject(error)
        })
    } else if (eventName === 'exportAsImage') {
      if (!params.linkName) {
        reject('Invalid widget linkName.')
        return
      }

      let exportOptions = params.options || {}
      let widgetContext = params.context || {}

      let data = {
        connectedAppId: instance.widget.connectedAppId,
        linkName: params.linkName,
        exportOptions: JSON.stringify(exportOptions),
        widgetContext: JSON.stringify(widgetContext),
      }
      http
        .post('/v2/connectedApps/exportWidgetAsImage', data)
        .then(response => {
          if (
            response.data &&
            response.data.result &&
            response.data.result.url
          ) {
            resolve(response.data.result)
          } else {
            resolve(null)
          }
        })
        .catch(error => {
          reject(error)
        })
    } else if (eventName === 'toBase64') {
      if (!params.url && !params.fileId) {
        reject('Image fileId or url is missing.')
        return
      }

      if (params.fileId) {
        params.url = `/v2/files/download/${params.fileId}`
      }
      http
        .get(params.url, {
          responseType: 'arraybuffer',
        })
        .then(response => {
          resolve(Buffer.from(response.data, 'binary').toString('base64'))
        })
        .catch(error => {
          reject(error)
        })
    }
  })
}

export const variablesHandler = (instance, eventName, params) => {
  return new Promise((resolve, reject) => {
    if (eventName === 'get') {
      if (!params.name) {
        reject('Invalid variable name.')
        return
      }

      let queryParams = {
        connectedAppId: instance.widget.connectedAppId,
        name: params.name,
      }
      http
        .get('/v2/connectedApps/getVariable', { params: queryParams })
        .then(response => {
          if (
            response.data &&
            response.data.result &&
            response.data.result.variable
          ) {
            resolve(response.data.result.variable.value)
          } else {
            resolve(null)
          }
        })
        .catch(error => {
          reject(error)
        })
    } else if (eventName === 'set') {
      if (!params.name) {
        reject('Invalid variable name.')
        return
      }
      let variableObj = {
        name: params.name,
        value: params.value,
        connectedAppId: instance.widget.connectedAppId,
      }
      if (params.visibility === true || params.visibility === false) {
        variableObj.visibility = params.visibility
      }

      http
        .post('/v2/connectedApps/addOrUpdateVariable', {
          variable: variableObj,
          upsert: true,
        })
        .then(response => {
          if (
            response.data &&
            response.data.result &&
            response.data.result.variable
          ) {
            resolve(true)
          } else {
            resolve(false)
          }
        })
        .catch(error => {
          reject(error)
        })
    } else if (eventName === 'delete') {
      if (!params.name) {
        reject('Invalid variable name.')
        return
      }
      let variableObj = {
        name: params.name,
        connectedAppId: instance.widget.connectedAppId,
      }

      http
        .post('/v2/connectedApps/deleteVariable', variableObj)
        .then(response => {
          if (response) {
            resolve(true)
          }
        })
        .catch(error => {
          reject(error)
        })
    }
  })
}

export const interfaceHandler = (instance, eventName, params) => {
  return new Promise((resolve, reject) => {
    if (eventName === 'navigateTo') {
      if (!params.path) {
        reject('Invalid path.')
        return
      }
      params.query = params.query || {}

      router
        .push({
          path: params.path,
          query: params.query,
        })
        .then(() => {
          resolve(true)
        })
        .catch(err => {
          reject(err)
        })
    } else if (eventName === 'openSummary') {
      if (!params.module || !params.id) {
        reject('Invalid module or id.')
        return
      }

      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(params.module, pageTypes.OVERVIEW) || {}

        if (name) {
          let routePath = router.resolve({
            name,
            params: {
              viewname: 'all',
              id: params.id,
            },
          }).href

          if (params.newtab) {
            window.open(routePath, '_blank')
            resolve(true)
          } else {
            router
              .push({ path: routePath })
              .then(() => {
                resolve(true)
              })
              .catch(err => {
                reject(err)
              })
          }
        } else {
          reject('Unsupported module.')
          return
        }
      } else {
        if (!MODULE_SUMAARY[params.module]) {
          reject('Unsupported module.')
          return
        }

        let url = MODULE_SUMAARY[params.module].replace('${id}', params.id)
        if (params.newtab) {
          let routeData = router.resolve({ path: url })
          window.open(routeData.href, '_blank')
          resolve(true)
        } else {
          router
            .push({
              path: url,
            })
            .then(() => {
              resolve(true)
            })
            .catch(err => {
              reject(err)
            })
        }
      }
    } else if (eventName === 'openListView') {
      if (!params.module) {
        reject('Invalid module.')
        return
      }
      if (params.filters && typeof params.filters !== 'object') {
        reject('Filters should be json object.')
        return
      }

      params.view = params.view || 'all'
      params.filters = params.filters || {}

      let query = {}
      if (Object.keys(params.filters).length) {
        query.search = JSON.stringify(params.filters)
        query.includeParentFilter = true
      }

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(params.module, pageTypes.LIST)

        if (name) {
          let routhPath = router.resolve({
            name,
            params: { viewname: params.view },
            query,
          }).href

          if (params.newtab) {
            window.open(routhPath, '_blank')
            resolve(true)
          } else {
            router
              .push({ path: routhPath })
              .then(() => {
                resolve(true)
              })
              .catch(err => {
                reject(err)
              })
          }
        } else {
          reject('Unsupported module.')
        }
      } else {
        if (!MODULE_VIEW[params.module]) {
          reject('Unsupported module.')
          return
        }

        let url = MODULE_VIEW[params.module].replace('${view}', params.view)

        if (params.newtab) {
          let routeData = router.resolve({ path: url, query: query })
          window.open(routeData.href, '_blank')
          resolve(true)
        } else {
          router
            .push({
              path: url,
              query: query,
            })
            .then(() => {
              resolve(true)
            })
            .catch(err => {
              reject(err)
            })
        }
      }
    } else if (eventName === 'openAnalytics') {
      if (!params.dataPoints || !params.dataPoints.length) {
        reject('Datapoints missing..')
        return
      }

      params.xAggr = params.xAggr || 0
      params.dateRange = params.dateRange || { operatorId: 22, value: null }

      let dataPoints = []
      for (let dp of params.dataPoints) {
        dataPoints.push({
          parentId: dp.parentId,
          yAxis: {
            fieldId: dp.readingId,
            aggr: dp.aggr || 3,
          },
        })
      }

      let analyticsConfig = {
        analyticsType: 2,
        mode: 1,
        period: params.xAggr,
        dateFilter: NewDateHelper.getDatePickerObject(
          params.dateRange.operatorId,
          params.dateRange.value
        ),
        dataPoints: dataPoints,
      }

      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.ANALYTIC_BUILDING) || {}

        if (name) {
          let routePath = router.resolve({
            name,
            query: { filters: JSON.stringify(analyticsConfig) },
          }).href

          if (params.newtab) {
            window.open(routePath, '_blank')
            resolve(true)
          } else {
            router
              .push({ path: routePath })
              .then(() => {
                resolve(true)
              })
              .catch(err => {
                reject(err)
              })
          }
        }
      } else {
        if (params.newtab) {
          let routeData = router.resolve({
            path: '/app/em/analytics/building',
            query: { filters: JSON.stringify(analyticsConfig) },
          })
          window.open(routeData.href, '_blank')
          resolve(true)
        } else {
          router
            .push({
              path: '/app/em/analytics/building',
              query: {
                filters: JSON.stringify(analyticsConfig),
              },
            })
            .then(() => {
              resolve(true)
            })
            .catch(err => {
              reject(err)
            })
        }
      }
    } else if (eventName === 'showTrend') {
      if (!params.dataPoints || !params.dataPoints.length) {
        reject('Datapoints missing..')
        return
      }

      params.xAggr = params.xAggr || 0
      params.dateRange = params.dateRange || { operatorId: 22, value: null }

      let dataPoints = []
      for (let dp of params.dataPoints) {
        dataPoints.push({
          parentId: dp.parentId,
          yAxis: {
            fieldId: dp.readingId,
            aggr: dp.aggr || 3,
          },
        })
      }

      let analyticsConfig = {
        period: params.xAggr,
        dateFilter: NewDateHelper.getDatePickerObject(
          params.dateRange.operatorId,
          params.dateRange.value
        ),
        dataPoints: dataPoints,
        hidechartoptions: true,
        hidecharttypechanger: true,
      }

      Vue.prototype.$popupView.openPopup({
        title: 'Trend',
        type: 'trend',
        analyticsConfig: analyticsConfig,
      })
      resolve(true)
    } else if (eventName === 'showControl') {
      if (!params.resourceId || !params.readingId) {
        reject('Resource or reading Id missing..')
        return
      }

      Vue.prototype.$popupView.openPopup({
        type: 'control',
        resourceId: params.resourceId,
        fieldId: params.readingId,
        groupId: params.groupId || null,
        onSetControlValue: () => {
          resolve(true)
        },
        resetControlValue: () => {
          resolve(false)
        },
      })
    } else if (eventName === 'fullscreen') {
      if (instance.actions.fullscreen()) {
        resolve(true)
      } else {
        reject('Full screen not supported.')
      }
    } else if (eventName === 'trigger') {
      if (!params.namespace) {
        reject('Trigger namespace missing..')
        return
      }
      params.options = params.options || {}

      try {
        let result = instance.actions.trigger(params.namespace, params.options)
        resolve(result)
      } catch (err) {
        reject('Trigger invoke failed.')
      }
    } else if (eventName === 'notify') {
      if (!params.options) {
        reject('Notify options missing..')
        return
      }
      let notifyOptions = params.options
      notifyOptions.dangerouslyUseHTMLString = false // HTML not allowed
      if (notifyOptions?.link?.url) {
        notifyOptions.onClick = () => {
          window.open(
            notifyOptions.link.url,
            notifyOptions.link.target || '_blank'
          )
        }
      }

      if (Vue.prototype.$notify) {
        Vue.prototype.$notify(notifyOptions)
        resolve(true)
      } else {
        resolve(false)
      }
    }
  })
}

export const liveEventHandler = (instance, eventName, params, self) => {
  return new Promise((resolve, reject) => {
    if (
      [
        'subscribeUserEvent',
        'subscribeOrgEvent',
        'subscribeAppEvent',
        'subscribeLiveReading',
      ].includes(eventName)
    ) {
      if (!params.topic) {
        reject('Topic missing..')
        return
      }
      let topic = `__custom__/user/${params.topic}`
      if (eventName == 'subscribeOrgEvent') {
        topic = `__custom__/org/${params.topic}`
      } else if (eventName == 'subscribeAppEvent') {
        topic = `__custom__/app/${params.topic}`
      } else if (eventName == 'subscribeLiveReading') {
        topic = `__livereading__/${params.topic}`
      }

      let subscribedToken = uuid()

      let subscribedTopic = {
        topic: topic,
        handler: data => {
          self.sendEventToInstance(instance.id, subscribedToken, data)
        },
      }
      if (Vue.prototype.$wms) {
        Vue.prototype.$wms.subscribe(
          subscribedTopic.topic,
          subscribedTopic.handler
        )
        instance.subscribedTopics[subscribedToken] = subscribedTopic
      }
      resolve(subscribedToken)
    } else if (eventName === 'unsubscribe') {
      if (!params.token) {
        reject('Subscribed token missing..')
        return
      }
      let subscribedToken = params.token
      let subscribedTopic = instance.subscribedTopics[subscribedToken]

      if (!subscribedTopic) {
        // no subscription exists with the given token
        resolve(false)
        return
      }
      if (Vue.prototype.$wms) {
        Vue.prototype.$wms.unsubscribe(
          subscribedTopic.topic,
          subscribedTopic.handler
        )
      }
      delete instance.subscribedTopics[subscribedToken]
      resolve(true)
    } else {
      reject('Invalid function : ' + eventName)
    }
  })
}
