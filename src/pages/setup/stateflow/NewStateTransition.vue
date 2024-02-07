<template>
  <div>
    <el-form :model="stateTransitionObj" :label-position="'top'">
      <error-banner :error="error" :errorMessage="errorMessage"></error-banner>
      <div class="new-header-container pL20 pR30 state-transition-header">
        <div class="d-flex">
          <span @click="cancel" class="mR10 pointer" title="Go Back" v-tippy>
            <inline-svg
              src="left-arrow"
              iconClass="icon vertical-middle"
            ></inline-svg>
          </span>
          <div class="stateflow-setup-title">
            {{ isNew ? 'New State Transition' : 'Edit State Transition' }}
          </div>
        </div>
        <i
          class="el-icon-delete pointer pT3"
          style="color: red"
          @click="remove()"
          title="Delete transition"
          v-tippy
        ></i>
      </div>

      <div
        v-if="showProgressBar"
        :class="['sidebar-loader', saving && 'on']"
      ></div>

      <div
        v-if="loading"
        class="new-body-modal new-stateflow-modal new-stateflow-transition-modal"
      >
        <div v-for="index in [1, 2, 3]" :key="index">
          <el-row class="mB20">
            <el-col :span="24">
              <span class="lines loading-shimmer width50 mB10"></span>
              <span class="lines loading-shimmer width95 mB10 height40"></span>
            </el-col>
          </el-row>
        </div>

        <el-row class="mB10 mT10">
          <el-col :span="24">
            <span class="lines loading-shimmer width50 mB10"></span>
            <div class="mT10 d-flex">
              <div
                v-for="index in [1, 2, 3]"
                :key="index"
                class="flex-middle flex-grow"
              >
                <span class="circle loading-shimmer"></span>
                <span class="lines loading-shimmer mL10 mR30 flex-grow"></span>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <div
        v-else
        class="new-body-modal new-stateflow-modal new-stateflow-transition-modal"
      >
        <el-row class="mB10">
          <el-col :span="24">
            <el-form-item prop="name" class="mB10">
              <p class="fc-input-label-txt">{{ $t('setup.stateflow.name') }}</p>
              <el-input
                :autofocus="isNew"
                v-model="stateTransitionObj.name"
                @change="autoSave()"
                class="width100 pR20"
                placeholder="Enter transition name"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row class="mB10">
          <el-col :span="24">
            <el-form-item prop="name" class="mB10">
              <p class="fc-input-label-txt">From State</p>
              <el-select
                v-model="stateTransitionObj.fromStateId"
                @change="autoSave()"
                placeholder="Select the from state"
                class="fc-input-full-border-select2 width100 pR20"
                filterable
              >
                <el-option
                  v-for="item in ticketStatus"
                  :key="item.id"
                  :label="item.displayName"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item prop="name" class="mB10">
              <p class="fc-input-label-txt">To State</p>
              <el-select
                v-model="stateTransitionObj.toStateId"
                @change="autoSave()"
                placeholder="Select the to state"
                class="fc-input-full-border-select2 width100 pR20"
                filterable
              >
                <el-option
                  v-for="item in ticketStatus"
                  :key="item.id"
                  :label="item.displayName"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row class="mB10 mT10">
          <el-col :span="24">
            <el-form-item prop="name">
              <p class="fc-input-label-txt">Button Priority</p>
              <el-radio-group
                v-model="stateTransitionObj.buttonType"
                @change="autoSave()"
                class="mT5"
              >
                <el-radio :label="buttonTypeList[1].key" class="fc-radio-btn">
                  {{ buttonTypeList[1].value }}</el-radio
                >
                <el-radio :label="buttonTypeList[2].key" class="fc-radio-btn">
                  {{ buttonTypeList[2].value }}</el-radio
                >
                <el-radio
                  :label="buttonTypeList[0].key"
                  class="fc-radio-btn is-focus is-checked"
                  >{{ buttonTypeList[0].value }}</el-radio
                >
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <div v-if="!isNew">
          <el-tabs v-model="activeTab" class="state-transition-tabs">
            <el-tab-pane label="Before" name="before">
              <before-state-transition
                :transitionObj="stateTransitionObj"
                :module="module"
                :timeObj.sync="timeObj"
                :dateObj.sync="dateObj"
                :moduleFields="moduleFields"
                :save="save"
                :setProps="setProps"
              ></before-state-transition>
            </el-tab-pane>
            <el-tab-pane label="Confirmations" name="confirmation">
              <state-transition-confirmations
                ref="stateFlowConfirmations"
                :module="module"
                :transition="stateTransitionObj"
                :criteriaList="criteriaList"
                :criteriaListLoading="criteriaListLoading"
                @setCondition="setCondition"
                @autoSave="save"
              ></state-transition-confirmations>
            </el-tab-pane>
            <el-tab-pane label="Validations" name="validation">
              <state-transition-validations
                ref="stateFlowValidations"
                :module="module"
                :transition="stateTransitionObj"
                :criteriaList="criteriaList"
                :criteriaListLoading="criteriaListLoading"
                @setCondition="setCondition"
                @autoSave="save"
              ></state-transition-validations>
            </el-tab-pane>

            <el-tab-pane label="After" name="after">
              <state-transition-actions
                ref="stateFlowActions"
                :stateflow="stateTransitionObj"
                :actions="supportedActionList"
                :module="module"
                :moduleFields="moduleFields"
                :moduleId="moduleId"
                @onUpdate="
                  actions => (stateTransitionObj.actions = actions || [])
                "
                @autoSave="save"
              ></state-transition-actions>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>

      <delete-dialog
        v-if="showDeleteDialog"
        @onClose="showDeleteDialog = false"
        @onSave="deleteTransition(stateTransitionObj)"
      ></delete-dialog>

      <div v-if="!isAutoSaveNeeded" class="dialog-footer">
        <el-button @click="cancel" class="modal-btn-cancel">Cancel</el-button>
        <el-button
          type="primary"
          class="modal-btn-save shadow-none"
          @click="save"
          :loading="saving"
        >
          {{
            saving ? $t('common._common._saving') : $t('common._common._save')
          }}
        </el-button>
      </div>
    </el-form>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import debounce from 'lodash/debounce'
import { API } from '@facilio/api'
import ErrorBanner from '@/ErrorBanner'
import BeforeStateTransition from './BeforeStateTransition'
import StateTransitionActions from './StateTransitionActions'
import StateTransitionValidations from './StateTransitionValidations'
import StateTransitionConfirmations from './StateTransitionConfirmations'
import DeleteDialog from './TransitionDeleteDialog'
import {
  serializeApprover,
  deserializeApprover,
} from 'newapp/utils/approverUtils'

const DIALOG_TYPE = {
  FIELD: 1,
  FORM: 2,
}

export default {
  props: [
    'isNew',
    'transitionObj',
    'closeAction',
    'stateFlowId',
    'stateFlowObj',
    'module',
    'isAutoSaveNeeded',
    'showProgressBar',
  ],
  components: {
    ErrorBanner,
    BeforeStateTransition,
    StateTransitionActions,
    StateTransitionValidations,
    StateTransitionConfirmations,
    DeleteDialog,
  },

  data() {
    return {
      loading: true,
      saving: false,
      error: false,
      errorMessage: '',
      activeTab: 'before',
      stateTransitionObj: {
        criteria: null,
        approvers: [],
        showInVendorPortal: false,
        showInTenantPortal: false,
        shouldExecuteFromPermalink: false,
        actions: [],
        allApprovalRequired: false,
        type: 1,
        buttonType: -1,
        dialogType: DIALOG_TYPE.FIELD,
        formModuleName: null,
        validations: [],
      },
      formName: null,
      selectedFormFields: [],
      requesterField: [
        {
          name: 'Requester',
          id: 'requester',
        },
      ],
      timeObj: {
        days: null,
        minute: 0,
        hours: 0,
      },
      dateObj: {
        scheduleType: 3,
        dateFieldId: null,
        time: null,
      },
      supportedActionList: [
        'email',
        'fieldUpdate',
        ...(this.$helpers.isLicenseEnabled('SMS') ? ['sms'] : []),
        'mobile',
        'script',
        'changeStatus',
      ],
      buttonTypeList: [
        { key: 0, value: 'Not Specified' },
        {
          key: 1,
          value: 'Primary',
        },
        {
          key: 2,
          value: 'Secondary',
        },
      ],
      moduleFieldsMeta: [],
      moduleId: null,

      //namedCriteria
      criteriaList: [],
      criteriaListLoading: false,

      // Props for stateflow builder
      isPartialTransition: false,
      tempTransitionId: null,
      showDeleteDialog: false,
    }
  },

  created() {
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')
    this.getModuleFields()

    let promises = [
      this.$store.dispatch('loadTicketStatus', {
        moduleName: this.module,
        forceUpdate: true,
      }),
    ]

    Promise.all(promises).then(() => {
      this.init()
      this.loadCriteriaList()
    })
  },

  computed: {
    ...mapState({
      users: state => state.users,
      teams: state => state.groups,
      roles: state => state.roles,
    }),
    moduleFields() {
      return this.moduleFieldsMeta || null
    },
    ticketStatus() {
      return this.$store.state.ticketStatus[this.module]
    },
  },

  watch: {
    transitionObj(newValue, oldValue) {
      if (newValue?.id !== oldValue?.id) this.init()
    },
  },

  methods: {
    init() {
      if (!isEmpty(this.transitionObj) && this.transitionObj.isPartial) {
        this.isPartialTransition = true
        this.tempTransitionId = this.transitionObj.id

        delete this.transitionObj.isPartial
        delete this.transitionObj.id
      }

      if (this.isPartialTransition) {
        this.stateTransitionObj = {
          ...this.stateTransitionObj,
          ...this.transitionObj,
        }
        this.deserialize()
        this.loading = false
      } else {
        this.loadTransitionData().then(() => {
          this.deserialize()
          this.loading = false
        })
      }
    },

    loadTransitionData() {
      let { id } = this.transitionObj
      if (isEmpty(id)) return Promise.resolve()

      return API.get(`/v2/statetransition/view?stateTransitionId=${id}`).then(
        ({ error, data }) => {
          if (!error) {
            let { transition } = data

            return (this.stateTransitionObj = {
              ...this.transitionObj,
              ...transition,
            })
          }
        }
      )
    },

    setCondition(condition) {
      this.criteriaList.push(condition)
      this.criteriaListLoading = true

      this.$nextTick(() => {
        this.criteriaListLoading = false
      })
    },

    async loadCriteriaList() {
      this.criteriaListLoading = true

      let { module: moduleName } = this
      let { error, data } = await API.post('v2/namedCriteria/list', {
        moduleName,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.criteriaList = data.namedCriteriaList || []
      }
      this.criteriaListLoading = false
    },

    setProps(props) {
      Object.entries(props).forEach(([key, value]) => {
        this.$set(this.stateTransitionObj, key, value)
      })
    },

    deserialize() {
      let {
        approvers,
        form,
        formId,
        actions,
        type,
        dialogType = null,
      } = this.stateTransitionObj
      let { TRANSITION_TYPE } = this.$constants

      if (!isEmpty(approvers)) {
        this.stateTransitionObj.approvers = approvers.map(approver =>
          deserializeApprover(approver)
        )
      }

      if (!actions) {
        this.stateTransitionObj.actions = []
      }

      if (TRANSITION_TYPE.time === Number(type)) {
        this.timeObj = this.$helpers.secTodaysHoursMinu(
          this.stateTransitionObj.scheduleTime
        )
      } else if (TRANSITION_TYPE.datefield === Number(type)) {
        let { dateFieldId, scheduleType, time } = this.stateTransitionObj

        this.dateObj = { dateFieldId, scheduleType, time }
        this.timeObj = this.$helpers.secTodaysHoursMinu(
          this.stateTransitionObj.interval
        )
      }

      this.selectedFormFields = []
      if (form && formId && dialogType === DIALOG_TYPE.FIELD) {
        if (form.sections && form.sections.length > 0) {
          form.sections.forEach(field => {
            if (!isEmpty(field.subFormId)) this.selectedFormFields.push(field)
            else
              (field.fields || []).forEach(field => {
                this.selectedFormFields.push(field)
              })
          })
        }
      }
    },

    serialize(data) {
      let {
        stateTransition: { type, criteria, dialogType },
      } = data
      let { TRANSITION_TYPE } = this.$constants

      if (type === TRANSITION_TYPE.user) {
        let approvers = data.stateTransition.approvers

        if (!isEmpty(approvers)) {
          data.stateTransition.approvers = approvers.map(approver =>
            serializeApprover(approver, { fields: this.moduleFields })
          )
        }

        delete data.stateTransition.dateFieldId
        delete data.stateTransition.scheduleType
        delete data.stateTransition.time
        delete data.stateTransition.timeObj
        delete data.stateTransition.interval
        delete data.stateTransition.scheduleTime
      } else if (
        [TRANSITION_TYPE.time, TRANSITION_TYPE.datefield].includes(type)
      ) {
        delete data.stateTransition.approvers
        delete data.stateTransition.criteria
        let interval = this.$helpers.daysHoursMinuToSec(this.timeObj)

        if (TRANSITION_TYPE.datefield === type) {
          let { dateFieldId, scheduleType, time } = this.dateObj

          data.stateTransition = {
            ...data.stateTransition,
            dateFieldId,
            scheduleType,
            time,
            interval,
          }
          delete data.stateTransition.scheduleTime
        } else {
          delete data.stateTransition.dateFieldId
          delete data.stateTransition.scheduleType
          delete data.stateTransition.time
          delete data.stateTransition.interval
          data.stateTransition.scheduleTime = interval
        }
        delete data.stateTransition.timeObj
      } else if (type === TRANSITION_TYPE.condition) {
        delete data.stateTransition.approvers
        delete data.stateTransition.scheduleTime
        delete data.stateTransition.dateFieldId
        delete data.stateTransition.scheduleType
        delete data.stateTransition.time
        delete data.stateTransition.timeObj
        delete data.stateTransition.interval
      }

      // Criteria Handling
      if (criteria) {
        data.stateTransition.criteria = this.serializeCriteria(criteria)
      }

      // Button type handling
      if (!this.isNew && data.stateTransition.buttonType === -1) {
        data.stateTransition.buttonType = -99
      }

      // Form handling
      delete data.stateTransition.form
      if ([TRANSITION_TYPE.time, TRANSITION_TYPE.condition].includes(type)) {
        data.stateTransition.formModuleName = ''
        data.stateTransition.formId = -99
      } else if (dialogType === DIALOG_TYPE.FIELD) {
        data.stateTransition.formModuleName = ''
        data.stateTransition.formId = data.stateTransition.formId || -99
      } else {
        let { formModuleName, formId } = data.stateTransition

        if (isEmpty(formModuleName) || isEmpty(formId)) {
          data.stateTransition.formModuleName = ''
          data.stateTransition.formId = -99
        }
      }

      // Action Serialization
      let actionsTab = this.$refs['stateFlowActions']
      data.stateTransition.actions = actionsTab
        ? actionsTab.serializeActions() || []
        : []

      // Temp handling for fixing issue with 'criteria' object inside module obj
      // TODO Should fix using models
      if (this.$getProperty(data.stateTransition, 'module')) {
        delete data.stateTransition.module
      }
      if (this.$getProperty(data.stateTransition, 'event')) {
        delete data.stateTransition.event
      }
    },

    save() {
      this.saving = true
      let data = { moduleName: this.module }

      data.stateTransition = {
        moduleId: this.stateFlowObj.moduleId,
        stateFlowId: this.stateFlowId,
        shouldFormInterfaceApply: false,
        ...this.stateTransitionObj,
      }

      this.serialize(data)
      this.$emit('onSaving')

      return API.post('/v2/statetransition/addOrUpdate', data)
        .then(({ error, data }) => {
          if (!error) {
            let eventName = !this.isNew
              ? 'onTransitionUpdate'
              : 'onTransitionCreate'

            this.$emit(
              eventName,
              data.transition,
              this.isPartialTransition ? this.tempTransitionId : null
            )
          } else {
            this.$emit('onError', error)
          }
        })
        .finally(() => (this.saving = false))
    },

    loadFieldId(fieldName) {
      if (this.moduleFieldsMeta) {
        return this.moduleFieldsMeta.filter(field => field.name === fieldName)
      }
    },

    getModuleFields() {
      API.get(`/module/metafields?moduleName=${this.module}`).then(
        ({ error, data }) => {
          if (!error) {
            this.moduleFieldsMeta = this.$getProperty(data, 'meta.fields', [])
            this.moduleId = this.$getProperty(data, 'meta.module.id', null)
          }
        }
      )
    },

    serializeCriteria(criteria) {
      for (let condition of Object.keys(criteria.conditions)) {
        let hasValidFieldName =
          criteria.conditions[condition].hasOwnProperty('fieldName') &&
          !isEmpty(criteria.conditions[condition].fieldName)

        if (!hasValidFieldName) {
          delete criteria.conditions[condition]
        } else {
          let discardKeys = [
            'valueArray',
            'operatorsDataType',
            'operatorLabel',
            'operator',
          ]

          discardKeys.forEach(key => {
            delete criteria.conditions[condition][key]
          })
        }
      }

      if (criteria && criteria.conditions) {
        if (Object.keys(criteria.conditions).length === 0) {
          criteria = null
        }
      }
      return criteria
    },

    cancel() {
      this.$emit('onClose')
    },

    remove() {
      if (!this.isNew) this.showDeleteDialog = true
      else if (this.isPartialTransition)
        this.$emit('onDelete', this.tempTransitionId, { isPartial: true })
    },

    deleteTransition({ id }) {
      return API.post('/v2/statetransition/delete', {
        stateFlowId: this.stateFlowId,
        stateTransitionId: id,
      }).then(({ error }) => {
        if (!error) {
          this.$emit('onDelete', id)
          this.cancel()
        } else {
          this.$message.error(error.message || 'Error Occurred')
        }
      })
    },

    autoSave: debounce(function() {
      let { name, fromStateId, toStateId } = this.stateTransitionObj
      let areEmptyFields =
        isEmpty(name) || isEmpty(fromStateId) || isEmpty(toStateId)

      if (this.isAutoSaveNeeded === true) {
        if (this.isNew && areEmptyFields) {
          return
        }
        this.save()
      }
    }, 2 * 1000),
  },
}
</script>
<style lang="scss" scoped>
.new-stateflow-modal {
  &.new-body-modal {
    padding-bottom: 0;
    margin-top: 0;
    padding-top: 20px;
  }
}
.dialog-footer {
  display: flex;
  > * {
    margin: 0;
  }
}
.field-row {
  padding: 20px 10px;
  border-bottom: 1px solid #f4f5f7;
  display: flex;
}
.field-row:hover {
  background-color: #f1f8fa;
}
.el-dialog-body-size {
  font-size: 20px;
  padding-bottom: 80px;
  padding-left: 30px;
  padding-top: 45px;
  color: #324056;
  letter-spacing: 0.5px;
}
.button-padding {
  padding: 22px 0px 22px 0px;
}
.height40 {
  height: 40px !important;
}
.width95 {
  width: 95%;
}
.circle {
  height: 20px;
  width: 20px;
  border-radius: 50%;
}
.lines {
  height: 15px;
  border-radius: 5px;
}
</style>
<style lang="scss">
.mandatory-checkbox .el-checkbox .el-checkbox__label {
  font-weight: normal;
}
.state-transition-tabs {
  margin: 15px -30px 0;
  &.el-tabs {
    > .el-tabs__content {
      padding-left: 30px;
      padding-top: 20px;
    }
    > .el-tabs__header {
      padding-top: 5px;
      .el-tabs__nav-scroll {
        padding: 0 30px;
      }
    }
  }
  .portal-checkbox.el-checkbox .el-checkbox__label {
    font-size: 13px;
  }
}
.new-statetransition-config {
  font-size: 13px;
  font-weight: 500;
}
.new-stateflow-transition-modal {
  .criteria-alphabet-block {
    width: 6%;
    padding-right: 20px;
  }
  .creteria-dropdown {
    width: 32%;
    padding-right: 20px;
  }
  .creteria-input {
    width: 32%;
    padding-right: 20px;
  }
  .fc-input-label-txt.txt-color,
  .txt-color {
    color: #324056;
  }
  .link-color {
    color: #2e9dfd;
  }
  .configure-blue {
    color: #6171db;
  }
  .configured-green {
    color: #5bc293;
  }
}
</style>
