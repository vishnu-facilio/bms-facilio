import { SetupData, prop } from '@facilio/data'
import $getProperty from 'dlv'
import {
  notifConfig,
  sysCreatedByUserOption,
  handleAddrChange,
  addModuleParam,
  fetchUsers,
} from '../utils'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import helpers from 'src/util/helpers.js'
import $common from 'src/util/common.js'

export class EmailModel extends SetupData {
  cachedFields

  @prop()
  actionType = 3

  @prop({
    deserialize: actionObj => {
      let { templateJson, template } = actionObj || {}
      let { emailStructureId } = template || templateJson || {}
      let isNew = isEmpty(templateJson?.to) && isEmpty(template)
      let isNewTemplate = isNew || emailStructureId > 0
      return {
        isNewTemplate,
      }
    },
    serialize: null,
  })
  isNewTemplate
  @prop({
    deserialize: actionObj => {
      let { templateJson, template } = actionObj || {}
      let isNew = isEmpty(templateJson?.to) && isEmpty(template)
      return { isNew }
    },
    serialize: null,
  })
  isNew
  @prop({ serialize: null })
  templateId = -1

  @prop({
    deserialize: actionObj => {
      let { template, templateJson, moduleName } = actionObj || {}
      let extraProperties = {}
      let { emailStructureId } = templateJson || template
      let isNew = isEmpty(templateJson?.to) && isEmpty(template)
      let isNewTemplate = emailStructureId > 0
      if (isNew) {
        return {
          templateJson: {
            to: [],
            cc: [],
            bcc: [],
            subject: '',
            message: '',
            fromAddr: null,
            emailStructureId: null,
            sendAsSeparateMail: true,
            isAttachmentAdded: false,
            templateFileFileIds: [],
            templateFileIds: [],
            templateUrlStrings: [],
            attachmentList: [],
            workflow: {},
            userWorkflow: { isV2Script: true, workflowV2String: '' },
            ftl: false,
          },
        }
      } else {
        let {
          message,
          subject,
          fromAddr = null,
          emailStructureId,
          fromID = null,
          sendAsSeparateMail,
          userWorkflow,
          isAttachmentAdded,
          ftl = false,
          workflow,
          to: toField,
          cc: ccField,
          bcc: bccField,
        } = template || templateJson || {}
        //a special occation where message,subject surprisingly included only in originalTemplate
        if (
          !Object.hasOwn(template || {}, 'subject') &&
          !Object.hasOwn(templateJson || {}, 'subject')
        ) {
          let { originalTemplate } = template || {}
          subject = originalTemplate?.subject || ''
          message = originalTemplate?.message || ''
        }

        isEmpty(userWorkflow) &&
          (userWorkflow = {
            isV2Script: true,
            workflowV2String: '',
          })
        let { workflowV2String = '' } = userWorkflow || {}
        let userTemplate = template
        if (!isEmpty(templateJson)) {
          let { to, cc, bcc } = templateJson
          userTemplate = {
            originalTemplate: { to, cc, bcc },
            ...templateJson,
          }
        }
        let to = [],
          cc = [],
          bcc = []
        if (!isEmpty(workflow)) {
          to = $common.getUsersFromTemplate(userTemplate)
          cc = $common.getUsersFromTemplate(userTemplate, 'cc')
          bcc = $common.getUsersFromTemplate(userTemplate, 'bcc')
        } else {
          !isEmpty(toField) && (to = (toField || '').split(','))
          !isEmpty(ccField) && (cc = (ccField || '').split(','))
          !isEmpty(bccField) && (bcc = (bccField || '').split(','))
        }
        if (!isEmpty(userWorkflow.workflowV2String)) {
          userWorkflow.workflowV2String = !isEmpty(moduleName)
            ? workflowV2String
                .replace('Map scriptFunc(Map ' + moduleName + ') {\n', '')
                .replace(new RegExp('\n}' + '$'), '')
            : workflowV2String.replace('Map test()' + ' { ', '').slice(0, -2)
        }
        if (!isNewTemplate) {
          let templateObj = templateJson
          emailStructureId = null
          !isEmpty(template) && (templateObj = template.originalTemplate)
          let { html = false } = templateObj
          extraProperties = { html }
        }
        return {
          templateJson: {
            ...extraProperties,
            subject,
            message,
            isAttachmentAdded,
            templateFileFileIds: [],
            templateFileIds: [],
            templateUrlStrings: [],
            attachmentList: [],
            to,
            cc,
            bcc,
            fromAddr: fromAddr || fromID,
            emailStructureId: !isNewTemplate ? null : emailStructureId,
            sendAsSeparateMail,
            workflow: {},
            userWorkflow,
            ftl,
          },
        }
      }
    },
    serialize: (templateJson, instance) => {
      let { moduleName } = instance
      let { to, cc, bcc, userWorkflow, workflow } = templateJson
      let { workflowV2String } = userWorkflow
      let ccKey = !isEmpty(cc)
        ? {
            ids: cc,
            key: 'cc',
          }
        : null
      let bccKey = !isEmpty(bcc)
        ? {
            ids: bcc,
            key: 'bcc',
          }
        : null
      $common.setUserMailWorkflow(
        to,
        templateJson,
        'mail',
        moduleName,
        false,
        ccKey,
        bccKey
      )
      if (!instance.isNewTemplate) {
        let stringsMayContainPlaceholders = `${templateJson.message} ${
          templateJson.subject
        } ${(templateJson?.templateUrlStrings || []).join()}`
        $common.setExpressionFromPlaceHolder(
          workflow,
          stringsMayContainPlaceholders,
          moduleName
        )
      }
      instance.addModuleParam()
      let type = 1,
        name = 'New WorkOrder Email Template'
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
      if (!isEmpty(templateJson.attachmentList)) {
        templateJson.attachmentList = instance.constructAttachmentList()
      } else {
        delete templateJson.attachmentList
        delete templateJson.templateFileIds
        delete templateJson.templateUrlStrings
        delete templateJson.templateFileFileIds
      }
      delete templateJson.isAttachmentAdded
      isEmpty(cc) && delete templateJson.cc
      isEmpty(bcc) && delete templateJson.bcc
      isEmpty(templateJson.emailStructureId) &&
        delete templateJson.emailStructureId
      if ((templateJson.to || []).length < 2)
        delete templateJson.sendAsSeparateMail
      return {
        templateJson: {
          ...templateJson,
          name,
          type,
          userWorkflow,
        },
      }
    },
  })
  templateJson = {}

  @prop({ serialize: null })
  moduleName

  @prop({ serialize: null })
  rule
  @prop({ serialize: null })
  option

  @prop({
    deserialize: actionObj => {
      let { rule } = actionObj
      return {
        configRuleName: rule ? rule.name + '_' + rule.ruleType : '',
      }
    },
    serialize: null,
  })
  configRuleName

  @prop({
    deserialize: actionObj => {
      let { rule } = actionObj
      let configRuleName = rule ? rule.name + '_' + rule.ruleType : ''

      return {
        isUserNonEditable:
          rule && notifConfig[configRuleName]?.isUsersEditable === false,
      }
    },
    serialize: null,
  })
  isUserNonEditable

  async getEmailTemplateList() {
    let { error, data } = await API.get('v2/template/email/list', {
      moduleName: this.moduleName,
    })

    return { data: data?.emailStructures, error }
  }
  static async getSenderList() {
    let { data, error } = await API.get('/v3/modules/data/list', {
      moduleName: 'emailFromAddress',
    })
    if (!error) {
      /* sourceType=1 filters all addresses added for notifications */
      data.emailFromAddress = data.emailFromAddress.filter(
        em => em.sourceType === 1
      )

      return data.emailFromAddress
    }
  }
  async getReceivingUsers(search = '', appId, receiverType = ['to']) {
    let responseObj = await fetchUsers.bind(this, search, appId, receiverType)()
    return responseObj
  }
  async getReceivingFields(search = '') {
    let { moduleName, cachedFields } = this
    let fields = []

    if (!isEmpty(cachedFields)) {
      fields = cachedFields
    } else {
      let { data } = await API.get('/module/metafields', { moduleName })
      fields = $getProperty(data, 'meta.fields', [])
      this.cachedFields = fields
    }
    fields = this.filteredFieldsList(fields, search)

    let userFields = fields.filter(field => {
      let lookupModulename = $getProperty(field, 'lookupModule.name')
      let userField = ['users', 'groups'].includes(lookupModulename)
      let requesterField =
        moduleName === 'workorder' &&
        $getProperty(field, 'name', '') === 'requester'

      return userField || requesterField
    })
    let tenantFields = this.getSpecificlookupList(fields, 'tenant')

    let clientFields = this.getSpecificlookupList(fields, 'client')

    let vendorsFields = this.getSpecificlookupList(fields, 'vendors')

    let audienceFields = this.getSpecificlookupList(fields, 'audience')
    let senderFieldOptions = this.userTemplateOptions({
      userFields,
      audienceFields,
      vendorsFields,
      clientFields,
      tenantFields,
    })
    return senderFieldOptions || {}
  }
  getSpecificlookupList(fields, lookupModule) {
    let filteredFields = (fields || []).filter(field => {
      let { lookupModule: { name: lookupModulename } = {} } = field
      return lookupModulename === lookupModule
    })
    return filteredFields
  }
  constructOptionGroup(fields, getValue) {
    let fieldsList = []
    fields.forEach(field => {
      let options = {
        label: field.displayName,
        value: getValue(field, this.moduleName),
      }
      fieldsList.push(options)
    })
    return fieldsList
  }
  filteredFieldsList(fields, search = '') {
    let filteredFields = fields.filter(field => {
      let { displayName } = field
      let lcName = displayName.toLowerCase()
      let lcSearch = search.toLowerCase()
      let displayTypeEnum = $getProperty(field, 'displayType._name')
      let multiLookupField = displayTypeEnum === 'MULTI_LOOKUP_SIMPLE'

      return !multiLookupField && lcName.match(lcSearch)
    })

    return filteredFields
  }
  userTemplateOptions(otherFields) {
    let {
      userFields,
      audienceFields,
      vendorsFields,
      clientFields,
      tenantFields,
    } = otherFields
    let options = []

    if (!this.rule || !Object.hasOwn(notifConfig || {}, this.configRuleName)) {
      let usersGroup = this.constructOptionGroup(userFields, this.getValue)
      options.Fields = []
      !isEmpty(usersGroup) && (options.Fields = usersGroup)
      if (helpers.isLicenseEnabled('VENDOR')) {
        let vendorGroup = this.constructOptionGroup(
          vendorsFields,
          this.getPrimaryEmailValue
        )
        !isEmpty(vendorGroup) && (options.Vendors = vendorGroup)
      }
      if (helpers.isLicenseEnabled('TENANTS')) {
        let tenantGroup = this.constructOptionGroup(
          tenantFields,
          this.getPrimaryEmailValue
        )
        !isEmpty(tenantGroup) && (options.Tenants = tenantGroup)
      }
      if (helpers.isLicenseEnabled('CLIENT')) {
        let clientGroup = this.constructOptionGroup(
          clientFields,
          this.getPrimaryEmailValue
        )
        !isEmpty(clientGroup) && (options.Clients = clientGroup)
      }
      let audienceGroup = this.constructOptionGroup(
        audienceFields,
        this.getFieldNameValue
      )
      !isEmpty(audienceGroup) && (options.Audiences = audienceGroup)

      if (this.moduleName === 'quote') {
        let isSysCreatedFound = options.Fields.find(
          item => item.label === sysCreatedByUserOption.label
        )
        if (!isSysCreatedFound) {
          options.Fields.push({
            label: sysCreatedByUserOption.label,
            value: this.getValue(sysCreatedByUserOption, this.moduleName),
          })
        }
      }
      return options
    }
    let conf = notifConfig[this.configRuleName] || {}

    options = userFields
      .filter(user => (conf.options || []).includes(user.field))
      .map(user => ({
        label: user.label,
        value: this.getValue(user, conf.module),
      }))
    return options
  }
  async fetchEmailTemplateList() {
    let { error, data } = await API.get(
      `v2/template/email/list?moduleName=${this.moduleName}`
    )
    return { data: data.emailStructures, error }
  }
  async addAttachmentList() {
    let { templateJson, templateId } = this
    if (templateJson.isAttachmentAdded) {
      let { data, error } = await API.get('/v2/template/attachment/getList', {
        templateId,
      })
      if (!error) {
        if (!isEmpty(data)) {
          let attachmentObj = this.deserializeAttachments(data)
          this.templateJson = { ...this.templateJson, ...attachmentObj }
        }
      }
    }
  }

  deserializeAttachments(data) {
    let {
      attachmentUrlList,
      attachments: attachmentList,
      attachmentFileFieldslList,
    } = data || {}
    let templateUrlStrings = (attachmentUrlList || []).map(url => url.urlString)
    let templateFileFileIds = (attachmentFileFieldslList || []).map(
      fieldObj => fieldObj.fieldId
    )
    let templateFileIds = (attachmentList || []).map(
      attachment => attachment.fileId
    )
    return {
      templateFileFileIds,
      templateFileIds,
      templateUrlStrings,
      attachmentList,
    }
  }
  constructAttachmentList() {
    let { templateJson } = this || {}
    this.templateJson.attachmentList = []
    let attachmentList = []
    let {
      templateFileIds,
      templateUrlStrings,
      templateFileFileIds,
    } = templateJson

    let mappedFileIds = (templateFileIds || []).map(fileId => {
      return {
        fileId,
        type: 1,
      }
    })
    let mappedFileFileIds = (templateFileFileIds || []).map(fieldId => {
      return {
        fieldId,
        type: 2,
      }
    })
    let mappedUrlStrings = (templateUrlStrings || [])
      .filter(url => !isEmpty(url))
      .map(urlString => {
        return {
          urlString,
          type: 3,
        }
      })
    attachmentList = [
      ...mappedFileIds,
      ...mappedUrlStrings,
      ...mappedFileFileIds,
    ]
    return attachmentList
  }
  prependDivInMessage() {
    if (this.templateJson.message) {
      this.templateJson.message =
        "<div style='white-space:pre'>" + this.templateJson.message + '</div>'
    }
  }
  onSaveAttachments(urlArray, fileIds, attachments, fileFieldIds) {
    this.templateJson.templateUrlStrings = urlArray
    this.templateJson.templateFileIds = fileIds

    this.templateJson.templateFileFileIds = fileFieldIds
    this.templateJson.attachmentList = attachments
  }
  handleAddrChange(receiverType, userOptionList) {
    return handleAddrChange.bind(
      this,
      receiverType,
      userOptionList,
      true,
      true
    )()
  }
  addModuleParam() {
    let { templateJson, moduleName } = this
    return addModuleParam.bind(this, templateJson, moduleName)()
  }
  getValue(user, module) {
    return (
      '${' +
      (user.value ? user.value : module + '.' + (user.field || user.name)) +
      '.email:-}'
    )
  }
  getPrimaryEmailValue(user, module) {
    return (
      '${' +
      (user.value ? user.value : module + '.' + (user.field || user.name)) +
      '.primaryContactEmail:-}'
    )
  }
  getFieldNameValue(field, module) {
    let { name, lookupModule } = field || {}
    let { name: lookupModuleName } = lookupModule || {}
    if (!isEmpty(name)) {
      return `${name}.notif_mod_${lookupModuleName}`
    } else {
      return module
    }
  }
}
