import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['rule', 'mode', 'configName', 'module'],
  data() {
    return {
      notifConfig: {
        'Approve Workrequest_11': {
          options: [Fields.REQUESTER],
          module: Modules.WORKREQUEST,
          isUsersEditable: true,
        },
        'Reject Workrequest_11': {
          options: [Fields.REQUESTER],
          module: Modules.WORKREQUEST,
          isUsersEditable: true,
        },
        'Notify Requester on Closing Workorder_11': {
          options: [Fields.REQUESTER],
          module: Modules.WORKORDER,
          isUsersEditable: true,
        },
        'Technician Closes Workorder_11': {
          options: [Fields.ASSIGNED_TO, Fields.ASSIGNED_BY],
          module: Modules.WORKORDER,
        },
        'Assign Tech_11': {
          options: [Fields.ASSIGNED_TO],
          module: Modules.WORKORDER,
          isUsersEditable: true,
        },
        'Assign Team_11': {
          options: [Fields.ASSIGNED_GROUP],
          module: Modules.WORKORDER,
          isUsersEditable: true,
        },
        'Add Comment_11': {
          options: [Fields.ASSIGNED_TO, Fields.ASSIGNED_BY],
          module: Modules.WORKORDER,
        },
        'Technician Resolves Workorder_11': {
          options: [Fields.ASSIGNED_TO, Fields.ASSIGNED_BY],
          module: Modules.WORKORDER,
        },
        'Workorder on hold_11': {
          options: [Fields.ASSIGNED_BY],
          module: Modules.WORKORDER,
        },
        Approval: {
          options: [Fields.REQUESTED_BY, Fields.REQUESTER],
          module: Modules.WORKORDER,
        },
      },
      sysCreatedByUserOption: {
        label: 'Created By',
        field: Fields.SYS_CREATED_BY,
      },
      field: {
        mail: 'email',
        primaryEmail: 'primaryContactEmail',
        primaryPhone: 'primaryContactPhone',
        sms: 'phone',
        web: 'id',
        mobile: 'id',
      },
      tenantFields: [],
      vendorsFields: [],
      userFields: [],
      clientFields: [],
      audienceFields: [],
    }
  },
  async created() {
    let { module } = this
    let { data } = await API.get(`/module/metafields?moduleName=${module}`)
    let fields = this.$getProperty(data, 'meta.fields', [])
    fields = fields.filter(field => {
      let displayTypeEnum = this.$getProperty(field, 'displayType._name')
      return displayTypeEnum !== 'MULTI_LOOKUP_SIMPLE'
    })

    let userFields = fields.filter(field => {
      let { lookupModule: { name: lookupModulename } = {} } = field

      return ['users', 'groups'].includes(lookupModulename)
    })

    if (module === 'workorder') {
      let requesterField = fields.find(field => {
        let { name } = field || {}
        return name === 'requester'
      })
      userFields.push(requesterField)
    }
    let peopleFields = fields.filter(field => {
      let { lookupModule: { name: lookupModulename } = {} } = field
      return lookupModulename === 'people'
    })
    if (!isEmpty(peopleFields)) {
      userFields = [...peopleFields, ...userFields]
    }
    this.userFields = userFields

    let tenantFields = fields.filter(field => {
      let { lookupModule: { name: lookupModulename } = {} } = field

      return lookupModulename === 'tenant'
    })
    this.tenantFields = tenantFields

    let clientFields = fields.filter(field => {
      let { lookupModule: { name: lookupModulename } = {} } = field

      return lookupModulename === 'client'
    })
    this.clientFields = clientFields

    let vendorsFields = fields.filter(field => {
      let { lookupModule: { name: lookupModulename } = {} } = field

      return lookupModulename === 'vendors'
    })
    this.vendorsFields = vendorsFields

    this.audienceFields = fields.filter(field => {
      let { lookupModule: { name: lookupModulename } = {} } = field

      return lookupModulename === 'audience'
    })
  },
  computed: {
    users() {
      return this.$store.state.users
    },
    userList() {
      if (this.users) {
        let users = this.$helpers
          .cloneObject(this.users)
          .filter(user => user.inviteAcceptStatus)
          .map(user => ({
            label: user.name,
            value: user.id,
            displayLabel: user.name + ' (' + user.email + ')',
          }))
        let usersGroups = {
          label: '',
          options: users,
        }
        return [usersGroups, ...this.userTemplateOptions]
      }
      return []
    },
    configRuleName() {
      return (
        this.configName ||
        (this.rule ? this.rule.name + '_' + this.rule.ruleType : '')
      )
    },
    userTemplateOptions() {
      let options = []
      if (!this.rule || !this.notifConfig.hasOwnProperty(this.configRuleName)) {
        let usersGroup = this.constructOptionGroup(
          this.userFields,
          this.getValue,
          'Fields'
        )
        options.push(usersGroup)

        if (this.mode !== 'mobile') {
          if (this.$helpers.isLicenseEnabled('VENDOR')) {
            let vendorGroup = this.constructOptionGroup(
              this.vendorsFields,
              this.getPrimaryEmailValue,
              'Vendors'
            )
            options.push(vendorGroup)
          }
          if (this.$helpers.isLicenseEnabled('TENANTS')) {
            let tenantGroup = this.constructOptionGroup(
              this.tenantFields,
              this.getPrimaryEmailValue,
              'Tenants'
            )
            options.push(tenantGroup)
          }
          if (this.$helpers.isLicenseEnabled('CLIENT')) {
            let clientGroup = this.constructOptionGroup(
              this.clientFields,
              this.getPrimaryEmailValue,
              'Client'
            )
            options.push(clientGroup)
          }
          let audienceGroup = this.constructOptionGroup(
            this.audienceFields,
            this.getFieldNameValue,
            'Audience'
          )
          options.push(audienceGroup)
        }

        if (this.module === 'quote') {
          let fieldsGroupIndex = options.findIndex(
            groups => groups.label === 'Fields'
          )
          let createdByUser = {
            label: 'Fields',
            options: options[fieldsGroupIndex].options
              ? [
                  ...options[fieldsGroupIndex].options,
                  {
                    label: this.sysCreatedByUserOption.label,
                    value: this.getValue(
                      this.sysCreatedByUserOption,
                      this.module
                    ),
                  },
                ]
              : [
                  {
                    label: this.sysCreatedByUserOption.label,
                    value: this.getValue(
                      this.sysCreatedByUserOption,
                      this.module
                    ),
                  },
                ],
            isGroup: true,
          }
          options.splice(fieldsGroupIndex, 1, createdByUser)
        }
        return options
      }
      let conf = this.notifConfig[this.configRuleName]
      options = this.userFields
        .filter(user => conf.options.includes(user.field))
        .map(user => ({
          label: user.label,
          value: this.getValue(user, conf.module),
        }))

      return options
    },
    isUserNonEditable() {
      return (
        this.rule &&
        this.notifConfig[this.configRuleName] &&
        this.notifConfig[this.configRuleName].isUsersEditable === false
      )
    },
    isDefaultRule() {
      return this.rule && this.notifConfig.hasOwnProperty(this.configRuleName)
    },
  },
  methods: {
    constructOptionGroup(fields, getValue, groupLabel) {
      let fieldsList = []
      fields.forEach(field => {
        let options = {
          label: field.displayName,
          value: getValue(field, this.module),
        }
        fieldsList.push(options)
      })

      return {
        label: groupLabel,
        options: fieldsList,
        isGroup: true,
      }
    },
    getValue(user, module) {
      return (
        '${' +
        (user.value ? user.value : module + '.' + (user.field || user.name)) +
        '.' +
        this.field[this.mode] +
        ':-}'
      )
    },
    getPrimaryEmailValue(user, module) {
      return (
        '${' +
        (user.value ? user.value : module + '.' + (user.field || user.name)) +
        '.' +
        this.field[this.mode === 'mail' ? 'primaryEmail' : 'primaryPhone'] +
        ':-}'
      )
    },
    getFieldNameValue(field, module) {
      let { name, lookupModule } = field || {}
      let { name: lookupModuleName } = lookupModule || {}
      if (!isEmpty(name)) {
        return `${name}.notif_mod_${lookupModuleName}`
      } else {
        return module
      }
    },
  },
}

const Modules = {
  WORKORDER: 'workorder',
  WORKREQUEST: 'workorderrequest',
  ALARM: 'alarm',
}

const Fields = {
  REQUESTER: 'requester',
  REQUESTED_BY: 'requestedBy',
  ASSIGNED_BY: 'assignedBy',
  ASSIGNED_TO: 'assignedTo',
  ASSIGNED_GROUP: 'assignmentGroup',
  VENDOR: 'vendor',
  TENANT: 'tenant',
  SYS_CREATED_BY: 'sysCreatedBy',
}
