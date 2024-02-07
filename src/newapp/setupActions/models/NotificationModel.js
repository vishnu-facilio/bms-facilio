import { SetupData, prop } from '@facilio/data'
import $getProperty from 'dlv'
import { isEmpty } from '@facilio/utils/validation'
import $common from 'src/util/common.js'
import { API } from '@facilio/api'
import { loadApps } from 'util/appUtil'
import constants from 'util/constant'
import { handleAddrChange, addModuleParam, fetchUsers } from '../utils'
export class NotificationModel extends SetupData {
  fields = {}

  @prop()
  actionType = 7

  @prop({
    deserialize: actionObj => {
      let { template, templateJson, moduleName } = actionObj || {}
      let isNew = isEmpty(templateJson?.application) && isEmpty(template)
      if (isNew) {
        return {
          templateJson: {
            application: null,
            to: [],
            subject: null,
            message: null,
            isPushNotification: false,
            workflow: {},
            userWorkflow: { isV2Script: true, workflowV2String: '' },
          },
        }
      }
      let message = null,
        subject = null,
        userTemplate = null,
        to = null
      let {
        application = null,
        body = '',
        isSendNotification = false,
        type = 8,
        userWorkflow,
        ftl,
      } = template || templateJson || {}
      isEmpty(userWorkflow) &&
        (userWorkflow = {
          isV2Script: true,
          workflowV2String: '',
        })
      let { workflowV2String = '' } = userWorkflow || {}
      if (!isEmpty(templateJson)) {
        message = templateJson.message
        subject = templateJson.subject
        userTemplate = {
          originalTemplate: { to: templateJson.to },
          ...templateJson,
        }
      } else {
        let templateData = (template?.originalTemplate || {}).data || {}
        message = templateData.text || null
        subject = templateData.title || null
        userTemplate = template
      }
      to = $common.getUsersFromTemplate(userTemplate) || []

      if (!isEmpty(userWorkflow.workflowV2String)) {
        userWorkflow.workflowV2String = !isEmpty(moduleName)
          ? workflowV2String
              .replace('Map scriptFunc(Map ' + moduleName + ') {\n', '')
              .replace(new RegExp('\n}' + '$'), '')
          : workflowV2String.replace('Map test()' + ' { ', '').slice(0, -2)
      }
      return {
        templateJson: {
          application,
          body,
          to,
          isSendNotification,
          isPushNotification: isSendNotification,
          type,
          message,
          subject,
          userWorkflow,
          workflow: {},
          ftl,
        },
      }
    },

    serialize: (templateJson, instance) => {
      let { to, isPushNotification, userWorkflow, workflow = {} } = templateJson
      let { workflowV2String } = userWorkflow
      let { moduleName } = instance
      let type = 8
      let name = 'New WorkOrder Push Notification Template'
      let isSendNotification = isPushNotification
      $common.setUserMailWorkflow(to, templateJson, 'mobile', moduleName)
      $common.setExpressionFromPlaceHolder(
        workflow,
        templateJson.message,
        instance.moduleName
      )
      $common.setExpressionFromPlaceHolder(workflow, templateJson.subject)
      instance.formatMobileData(templateJson, moduleName)
      instance.addModuleParam(templateJson, moduleName)
      if (!isEmpty(userWorkflow.workflowV2String)) {
        userWorkflow.workflowV2String = !isEmpty(moduleName)
          ? 'Map scriptFunc(Map ' +
            moduleName +
            ') {\n' +
            workflowV2String +
            '\n}'
          : 'Map test()' + ' { ' + workflowV2String + ' }'
      } else {
        userWorkflow = null
      }
      return {
        templateJson: {
          ...templateJson,
          type,
          name,
          isSendNotification,
          userWorkflow,
        },
      }
    },
  })
  templateJson = {
    application: null,
    to: [],
    subject: null,
    default: 'true',
    message: null,
    isPushNotification: false,
    workflow: null,
    userWorkflow: { isV2Script: true, workflowV2String: '' },
  }

  @prop()
  moduleName = null

  async getReceivingUsers(search = '') {
    let responseObj = await fetchUsers.bind(this, search, null, ['to'])()
    return responseObj
  }

  async getMetaFields() {
    let { moduleName, fields } = this
    if (isEmpty(fields)) {
      let { data } = await API.get('/module/metafields', { moduleName })

      fields = $getProperty(data, 'meta.fields', [])
      this.fields = fields
    }
    return fields
  }

  async fetchFieldList(search = '') {
    let { moduleName } = this
    let fields = await this.getMetaFields()
    let userFields = (fields || []).filter(field => {
      let { displayName = '' } = field || {}
      let lcName = displayName.toLowerCase()
      let lcSearch = search.toLowerCase()
      let displayTypeEnum = $getProperty(field, 'displayType._name')
      let isMultiLookupField = displayTypeEnum === 'MULTI_LOOKUP_SIMPLE'
      let lookupModulename = $getProperty(field, 'lookupModule.name')
      let isUserField = ['users', 'groups', 'people'].includes(lookupModulename)
      let isRequesterField =
        moduleName === 'workorder' &&
        $getProperty(field, 'name', '') === 'requester'
      return (
        !isMultiLookupField &&
        (isUserField || isRequesterField) &&
        lcName.match(lcSearch)
      )
    })
    let options = userFields.map(fieldObj => {
      let { displayName, value, field, name } = fieldObj || {}
      let fieldValue = value || moduleName + '.' + (field || name)

      return {
        label: displayName,
        value: `\${${fieldValue}.id:-}`,
      }
    })
    if (moduleName === 'quote') {
      let sysCreatedByUserLabel = 'Created By'
      let sysCreatedByUserValue = 'sysCreatedBy'

      options.push({
        label: sysCreatedByUserLabel,
        value: `\${${moduleName}.${sysCreatedByUserValue}.id:-}`,
      })
    }
    return options
  }
  handleAddrChange(receiverType, userOptionList) {
    return handleAddrChange.bind(
      this,
      receiverType,
      userOptionList,
      true,
      false
    )()
  }
  formatMobileData(data, moduleName) {
    let summaryPage = moduleName.toUpperCase() + '_SUMMARY'
    let message = {
      content_available: true,
      summary_id: '${' + moduleName + '.id}',
      sound: 'default',
      module_name: moduleName,
      priority: 'high',
      text: data.message,
      click_action: summaryPage,
      title: data.subject,
    }
    data.body = JSON.stringify({
      name: moduleName.toUpperCase() + '_PUSH_NOTIFICATION',
      notification: message,
      data: message,
      id: data.id,
    })
    data.to = data.id
    $common.setExpressionFromPlaceHolder(
      data.workflow,
      '${' + moduleName + '.id}'
    )
  }
  addModuleParam() {
    let { templateJson, moduleName } = this
    return addModuleParam.bind(this, templateJson, moduleName)()
  }
  async fetchAppList() {
    const {
      appCategory: { FEATURE_GROUPING, WORK_CENTERS, PORTALS },
    } = constants
    let { error, data } = await loadApps()
    if (error) {
      throw error
    } else {
      return (data || []).filter(app =>
        [FEATURE_GROUPING, WORK_CENTERS, PORTALS].includes(app.appCategory)
      )
    }
  }
}
