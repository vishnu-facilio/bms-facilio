<template>
  <div style="min-height: 200px;" class="survey-respondent-style">
    <div
      id="surveyRespondent-header"
      class="section-header survey-header-color"
    >
      {{ $t('survey.survey_respondent') }}
    </div>

    <div
      class="pL50 pR70 fc-input-label-txt survey-required-label d-flex"
      style="margin-top: 10px;"
    >
      <span> {{ $t('survey.define_who_can_take_this_survey') }}</span>
      <div class="survey-info-icon">
        <el-tooltip
          :popper-class="'tooltip-content'"
          effect="dark"
          :content="$t('survey.survey_info_for_tenant_primary_contact')"
          placement="top-start"
        >
          <fc-icon
            group="action"
            name="info"
            class="pointer mL10"
            size="18"
            color="#E6333D"
          ></fc-icon>
        </el-tooltip>
      </div>
    </div>
    <div class="pB30 pL40 mT15">
      <div style="min-height: 150px;">
        <el-row
          v-for="(approver, key) in activeApprovers"
          :key="key"
          class="pL20"
        >
          <el-col :span="6">
            <el-select
              v-model="approver.type"
              :placeholder="$t('common.products.select_type')"
              class="fc-input-full-border-select2 width100"
              @change="resetApproverId(key)"
              filterable
            >
              <el-option
                v-for="type in approverTypes"
                :key="`type-${type.id}`"
                :label="type.name"
                :value="type.id"
              ></el-option>
            </el-select>
          </el-col>
          <el-col :span="15" class="pL10">
            <el-select
              v-model="approver.approverId"
              :placeholder="$t('common._common.select')"
              class="fc-input-full-border-select2 width75"
              filterable
            >
              <div v-if="approver.type === 1">
                <el-option
                  v-for="(option, index) in typeOptionMap[approver.type]"
                  :key="`approveroption-${index}`"
                  :label="option.name || option.email || '---'"
                  :value="option.id"
                ></el-option>
              </div>

              <el-option
                v-else
                v-for="(option, index) in typeOptionMap[approver.type]"
                :key="`approveroption-${index}`"
                :label="option.displayName || option.name"
                :value="option.id"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import Approvers from 'pages/setup/approvals/components/Approvers.vue'
import { API } from '@facilio/api'

const approverType = {
  USER: 1,
  ROLE: 2,
  TEAM: 3,
  FIELD: 4,
  TENANT: 6,
  VENDOR: 7,
}
const dataTypes = {
  LOOKUP: 7,
}

export default {
  name: 'SurveyRespondent',
  props: ['moduleFields', 'selectedSurvey'],
  extends: Approvers,
  data() {
    return {
      assignedTo: null,
      approverTypes: [
        { id: 1, name: 'User' },
        { id: 4, name: 'Field' },
      ],
      respondentHash: {
        1: 'userId',
        2: 'roleId',
        4: 'fieldId',
        6: 'fieldId',
        7: 'fieldId',
      },
      userList: [],
    }
  },
  watch: {
    selectedSurvey: {
      handler(newVal) {
        if (!isEmpty(newVal)) {
          this.prefillRespondent()
        }
      },
      immediate: true,
    },
    typeOptions: {
      handler: async function(typeOptions) {
        this.typeOptionMap = await this.getApproverOptions(typeOptions)
        await this.loadUserList()
      },
      immediate: true,
    },
  },
  computed: {
    typeOptions() {
      return {
        users: [
          ...this.users,
          {
            name: 'Requester',
            peopleId: 'requester',
          },
        ],
        roles: this.roles,
        teams: this.teams,
        fields: this.moduleFields,
      }
    },
  },
  methods: {
    prefillRespondent() {
      let { selectedSurvey } = this
      if (!isEmpty(selectedSurvey)) {
        let { workflowRule } = selectedSurvey || {}
        let { actions } = workflowRule || {}
        let type = this.$getProperty(actions, '0.template.sharingType', null)
        let fieldId = this.$getProperty(actions, '0.template.fieldId', null)

        if (type === approverType.USER && !isEmpty(fieldId)) {
          this.$set(this, 'activeApprovers', [
            { type: type, approverId: 'requester' },
          ])
        } else {
          if (type === approverType.USER) {
            let userId = this.$getProperty(actions, '0.template.userId', null)
            this.$set(this, 'activeApprovers', [
              { type: type, approverId: userId },
            ])
          }
          if (
            [
              approverType.FIELD,
              approverType.TENANT,
              approverType.VENDOR,
            ].includes(type)
          ) {
            this.$set(this, 'activeApprovers', [
              {
                type: type,
                approverId: fieldId,
              },
            ])
          }
        }
      }
    },
    serialize() {
      let { activeApprovers, respondentHash, moduleFields } = this
      let { type, approverId } = activeApprovers[0] || {}
      if (approverId === 'requester') {
        approverId = (moduleFields.find(f => f.name === 'requester') || {}).id

        return {
          sharingType: type,
          fieldId: approverId,
        }
      } else {
        if (type === approverType.USER) {
          return {
            sharingType: type,
            [respondentHash[type]]: approverId,
          }
        } else {
          return { sharingType: type, [respondentHash[type]]: approverId }
        }
      }
    },
    async loadUserList() {
      let filters = {
        peopleType: {
          operatorId: 54,
          value: ['1', '3', '5'],
        },
      }
      let { data, error } = await API.get(`/v2/people/list`, { filters })
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        let { people = [] } = data || {}
        this.typeOptionMap[1] = [
          ...people,
          {
            name: 'Requester',
            id: 'requester',
          },
        ]
      }
    },
    async getApproverOptions(options, supportedTypes = null) {
      let types = !isEmpty(supportedTypes)
        ? supportedTypes
        : Object.values(approverType)

      let supportedFields = await this.getSupportedFields(options.fields)

      let { users, roles, teams, fields } = options

      return types.reduce((res, type) => {
        if (type === approverType.USER) {
          res[type] = users
        } else if (type === approverType.ROLE) {
          res[type] = roles
        } else if (type === approverType.TEAM) {
          res[type] = teams
        } else if (type === approverType.VENDOR) {
          res[type] = this.getVendorFields(fields)
        } else if (type === approverType.TENANT) {
          res[type] = this.getTenantFields(fields)
        } else if (type === approverType.FIELD) {
          res[type] = supportedFields
        }
        return res
      }, {})
    },
    getTenantFields(fields) {
      return fields.filter(field => {
        let { dataType } = field
        return (
          dataType === dataTypes.LOOKUP &&
          this.$getProperty(field, 'lookupModule.name', '') === 'tenant'
        )
      })
    },
    getVendorFields(fields) {
      return fields.filter(field => {
        let { dataType } = field
        return (
          dataType === dataTypes.LOOKUP &&
          this.$getProperty(field, 'lookupModule.name', '') === 'vendors'
        )
      })
    },
    async getSupportedFields(fields = []) {
      let peopleModuleId = null
      let { data, error } = await API.get('/module/meta', {
        moduleName: 'people',
      })
      if (!error) {
        let { meta: { module = {} } = {} } = data
        if (module) {
          peopleModuleId = module['moduleId']
        }
      }
      let fieldsList = fields.filter(field => {
        let isPeopleField = false
        if (peopleModuleId) {
          let extendedModuleIds = this.$getProperty(
            field,
            'lookupModule.extendedModuleIds',
            []
          )
          let moduleId = this.$getProperty(field, 'lookupModule.moduleId', null)
          isPeopleField =
            peopleModuleId === moduleId ||
            extendedModuleIds.some(id => id === peopleModuleId)
        }
        let lookupModuleName = this.$getProperty(field, 'lookupModule.name', '')
        return (
          field.dataType === dataTypes.LOOKUP &&
          (['users', 'tenant'].includes(lookupModuleName) || isPeopleField)
        )
      })
      return fieldsList
    },
  },
}
</script>
<style scoped>
.survey-required-label {
  font-size: 14px;
  font-weight: normal;
  letter-spacing: 0.5px;
  color: #e6333d !important;
}
</style>
<style>
.tooltip-content {
  width: 350px !important;
  word-break: break-word;
  text-align: justify;
}
</style>
