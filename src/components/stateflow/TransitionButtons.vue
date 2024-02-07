<template>
  <div class="display-flex" :class="rootClass">
    <div v-if="hasTransitionBtns" class="d-flex">
      <div class="stateflow-btn-wrapper mL10">
        <async-button
          buttonType="primary"
          :buttonClass="asyncBtnClassName"
          :disable="transitions.featured[0].scheduled"
          :clickAction="startTransition"
          :actionParams="[transitions.featured[0]]"
          :disabled="isDisabled"
          :data-test-selector="transitions.featured[0].name"
          :title="`${transitions.featured[0].name}`"
          :loading="firstBtnLoading"
          v-tippy
        >
          {{ transitions.featured[0].name }}
        </async-button>
      </div>
      <div v-if="hasDropDownBtns" class="mL10">
        <el-dropdown
          split-button
          class="pointer stateflow-btn-wrapper"
          @click="initiateDropdownTransition(transitions.featured[1])"
          @command="initiateDropdownTransition"
          :data-test-selector="transitions.featured[1].name"
          :disabled="isDisabled"
        >
          <div
            class="transition-btn-name"
            :title="`${transitions.featured[1].name}`"
            v-tippy
          >
            <span v-if="dropdownLoading">
              <i class="el-icon-loading"></i>
              {{ $t('common._common.loading') }}
            </span>
            <span v-else>{{ transitions.featured[1].name }}</span>
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="(transition, index) in transitions.dropdown"
              :key="index"
              :command="transition"
              :disabled="isDisabled"
              :data-test-selector="transition.name"
            >
              {{ transition.name }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
      <div v-else-if="hasOnly2Btns" class="stateflow-btn-wrapper mL10">
        <async-button
          :buttonClass="asyncBtnClassName"
          :disable="transitions.featured[1].scheduled"
          :clickAction="startTransition"
          :actionParams="[transitions.featured[1]]"
          :disabled="isDisabled"
          :data-test-selector="transitions.featured[1].name"
          :title="`${transitions.featured[1].name}`"
          :loading="lastBtnLoading"
          v-tippy
        >
          {{ transitions.featured[1].name }}
        </async-button>
      </div>
    </div>

    <TransitionForm
      v-if="canShowForm"
      v-bind="transitionFormProps"
    ></TransitionForm>

    <ConfirmationDialog
      v-if="showConfirmationDialog"
      :transition="selectedTransition"
      :confirmations="confirmationDialogs"
      :onConfirm="continueTransition"
      :onCancel="cancelTransition"
    ></ConfirmationDialog>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import AsyncButton from '@/AsyncButton'
import TransitionForm from './TransitionForm'
import ConfirmationDialog from './TransitionConfirmations'
import { isEmpty, isFunction, isObject } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'
import {
  actionStatusFlag,
  transitionBtnActionsOrder,
  transitionBtnActions,
} from './TransitionButtonsUtils'

export default {
  props: [
    'moduleName',
    'record',
    'updateFn',
    'updateUrl',
    'transformFn',
    'transitionFilter',
    'buttonClass',
    'disabled',
    'hideNotifications',
  ],
  components: { AsyncButton, TransitionForm, ConfirmationDialog },
  created() {
    this.fetchAvailableStates()
  },
  data() {
    return {
      stateTransitions: null,
      loading: false,
      canShowForm: false,
      selectedTransition: null,
      dataForCurrentTransition: null,
      confirmationDialogs: null,
      showConfirmationDialog: false,
      dropdownLoading: false,
      selectedTransitionActions: null,
      firstBtnLoading: false,
      lastBtnLoading: false,
    }
  },
  computed: {
    isV3() {
      let { moduleName, updateUrl } = this
      return !isEmpty(moduleName) && isEmpty(updateUrl)
    },
    transitions() {
      if (!isEmpty(this.stateTransitions)) {
        let transitions = [...this.stateTransitions].sort((first, second) => {
          // sorts array in ascending order but placing all '-1' values at the end
          if ([-1, 0, 3].includes(first.buttonType)) return 1
          if ([-1, 0, 3].includes(second.buttonType)) return -1
          return first.buttonType - second.buttonType
        })

        return {
          featured: transitions.slice(0, 2) || [],
          dropdown: transitions.slice(2) || [],
        }
      } else {
        return null
      }
    },
    shouldCreateModuleRecord() {
      let { selectedTransition = {} } = this
      let { dialogType = null, formModuleName } = selectedTransition || {}

      return (
        !isEmpty(dialogType) && !isEmpty(formModuleName) && dialogType === 2
      )
    },
    shouldCreateNotes() {
      let { form } = this.selectedTransition || {}
      return this.$getProperty(form, 'module.type', null) === 5
    },
    rootClass() {
      return ''
    },
    isDisabled() {
      let {
        loading,
        disabled,
        dropdownLoading,
        firstBtnLoading,
        lastBtnLoading,
      } = this
      return (
        loading ||
        disabled ||
        dropdownLoading ||
        firstBtnLoading ||
        lastBtnLoading
      )
    },
    hasTransitionBtns() {
      let { featured } = this.transitions || {}
      return !isEmpty(featured)
    },
    hasDropDownBtns() {
      let { featured, dropdown } = this.transitions || {}
      return (
        this.hasTransitionBtns &&
        !isEmpty(dropdown) &&
        featured.length > 1 &&
        dropdown.length > 0
      )
    },
    hasOnly2Btns() {
      let { featured } = this.transitions || {}
      return this.hasTransitionBtns && featured.length > 1
    },
    asyncBtnClassName() {
      let { buttonClass, isDisabled } = this
      return `${buttonClass} ${isDisabled && 'disabled'} transition-btn-name`
    },
    transitionFormProps() {
      let {
        selectedTransition,
        record,
        shouldCreateModuleRecord,
        saveDataForTransition,
        cancelTransition,
        isV3,
      } = this
      let { moduleName, formId } = selectedTransition || {}
      let { id: recordId } = record || {}

      return {
        moduleName,
        recordId,
        formId,
        record,
        isV3,
        transition: selectedTransition,
        isExternalModule: shouldCreateModuleRecord,
        saveAction: saveDataForTransition,
        closeAction: cancelTransition,
      }
    },
  },
  watch: {
    moduleName(newVal, oldVal) {
      if (newVal !== oldVal) this.fetchAvailableStates()
    },
    record(newVal, oldVal) {
      if (newVal !== oldVal) this.fetchAvailableStates({ force: true })
    },
  },
  methods: {
    refresh() {
      this.fetchAvailableStates()
    },
    async fetchAvailableStates({ force = false } = {}) {
      this.loading = true
      let { data, error } = await API.get(
        '/v2/statetransition/getAvailableState',
        {
          moduleName: this.moduleName,
          id: this.record.id,
        },
        { force }
      )

      if (error) {
        this.stateTransitions = null
      } else {
        let { currentState = null, states = [] } = data || {}

        if (currentState) {
          this.$emit('currentState', currentState.id)
        }
        if (!isEmpty(this.transitionFilter) && !isEmpty(this.states)) {
          states = states.filter(this.transitionFilter)
        }
        this.stateTransitions = states || []
      }
      this.loading = false
    },
    startTransition(transition) {
      let { featured } = this.transitions || {}
      if (!(this.dropdownLoading || isEmpty(featured))) {
        let { id: firstTransitionId } = featured[0] || {}
        let { id: lastTransitionId } = featured[1] || {}
        let { id: currentTransitionId } = transition || {}

        this.firstBtnLoading = currentTransitionId === firstTransitionId
        this.lastBtnLoading = currentTransitionId === lastTransitionId
      }

      let { NOT_CONFIGURED, YET_TO_START } = actionStatusFlag

      this.selectedTransitionActions = Object.entries(
        transitionBtnActions || {}
      ).reduce((actionList, [action, transKey]) => {
        actionList[action] = !isEmpty(transition[transKey])
          ? YET_TO_START
          : NOT_CONFIGURED
        return actionList
      }, {})

      this.selectedTransition = cloneDeep(transition)
      return this.startTransitionProcess()
    },
    startTransitionProcess() {
      let { selectedTransitionActions, selectedTransition } = this
      let { YET_TO_START, IN_PROCESS, FAILED } = actionStatusFlag
      let isAnyActionFailed = transitionBtnActionsOrder.some(
        action => selectedTransitionActions[action] === FAILED
      )
      let currentRunningProcess = transitionBtnActionsOrder.some(
        action => selectedTransitionActions[action] === IN_PROCESS
      )

      if (isAnyActionFailed || currentRunningProcess) return

      let nextProcessToStart =
        transitionBtnActionsOrder.find(
          action => selectedTransitionActions[action] === YET_TO_START
        ) || null

      if (!isEmpty(nextProcessToStart)) {
        this.$set(
          this.selectedTransitionActions,
          nextProcessToStart,
          IN_PROCESS
        )

        if (nextProcessToStart === 'FORM') {
          this.showStateFlowForm()
          return Promise.resolve()
        } else if (nextProcessToStart === 'CONFIRMATION') {
          return this.checkConfirmations(selectedTransition)
        }
      } else {
        return this.continueTransition()
      }
    },
    saveDataForTransition(formData) {
      this.dataForCurrentTransition = cloneDeep(formData)
      this.$set(
        this.selectedTransitionActions,
        'FORM',
        actionStatusFlag.SUCCESS
      )
      this.closeStateFlowForm()
      return this.startTransitionProcess()
    },
    async checkConfirmations(transition) {
      let { moduleName, record } = this
      let params = {
        id: record.id,
        moduleName: moduleName,
        transitionId: transition.id,
      }

      let { dataForCurrentTransition } = this
      if (!isEmpty(transition.formId) && !isEmpty(dataForCurrentTransition)) {
        let formData = {
          ...dataForCurrentTransition,
          ...(dataForCurrentTransition.data || {}),
        }
        delete formData.data
        params['data'] = formData
      }

      let { error, data } = await API.post(
        `v2/statetransition/confirmationDialogs`,
        params
      )
      if (error) {
        this.defaultResponseHandler({ error })
        this.cancelTransition()
      } else {
        let { validConfirmationDialogs } = data

        if (!isEmpty(validConfirmationDialogs)) {
          this.confirmationDialogs = validConfirmationDialogs
          this.showConfirmationDialog = true
        } else {
          this.$set(
            this.selectedTransitionActions,
            'CONFIRMATION',
            actionStatusFlag.SUCCESS
          )
          return this.startTransitionProcess()
        }
      }
    },
    proceedAfterConfirmation() {
      this.$set(
        this.selectedTransitionActions,
        'CONFIRMATION',
        actionStatusFlag.SUCCESS
      )
      this.startTransitionProcess()
    },
    initiateDropdownTransition(transition) {
      this.dropdownLoading = true
      this.startTransition(transition)
    },
    continueTransition() {
      let {
        selectedTransition,
        dataForCurrentTransition: formData = null,
      } = this

      return this.transitionToState(formData, selectedTransition)
    },
    cancelTransition() {
      this.selectedTransition = null
      this.dataForCurrentTransition = null
      this.confirmationDialogs = null
      this.dropdownLoading = false
      this.firstBtnLoading = false
      this.lastBtnLoading = false
      this.selectedTransitionActions = null

      this.closeStateFlowForm()
      this.closeConfirmationDialogs()
    },
    getParams({ formData, transition }) {
      let { moduleName, record } = this
      let { id } = record || {}
      let { id: stateTransitionId, formId, formModuleName } = transition || {}
      let serializedFormData = formData

      if (this.shouldCreateModuleRecord) {
        serializedFormData = {
          relations: {
            [formModuleName]: [{ data: [{ ...formData, formId }] }],
          },
        }
      }

      if (this.shouldCreateNotes || !this.isV3) {
        let firstArg = { moduleName, id, stateTransitionId }
        let secondArg = !this.isV3 && !this.shouldCreateNotes ? formData : null

        return this.transformFn(firstArg, secondArg)
      } else {
        return { id, stateTransitionId, data: serializedFormData || {} }
      }
    },
    transitionToState(formData, transition) {
      if (isEmpty(transition)) transition = this.selectedTransition

      let url = this.getUrl(transition)
      let params = this.getParams({ formData, transition })
      let { defaultResponseHandler, updateFn, moduleName, record } = this

      if (this.isV3) {
        if (!isEmpty(updateFn) && isFunction(updateFn)) {
          return updateFn(moduleName, params).then(defaultResponseHandler)
        } else {
          let { id: recordId } = record || {}
          let url = `v3/action/${moduleName}/${recordId}/transition`

          return API.patch(url, params).then(defaultResponseHandler)
        }
      } else {
        return API.post(url, params).then(async ({ error }) => {
          if (error) {
            defaultResponseHandler({ error })
          } else if (this.shouldCreateNotes || this.shouldCreateModuleRecord) {
            this.v2SubModuleHandler(formData, transition)
              .then(() => defaultResponseHandler({}))
              .catch(() => {
                // Edge case where transition is success but record creation fails
                this.fetchAvailableStates({ force: true })
                this.$emit('transitionSuccess')
                defaultResponseHandler({ error: {} })
              })
          } else {
            defaultResponseHandler({})
          }
        })
      }
    },
    async v2SubModuleHandler(formData, transition) {
      let { shouldCreateNotes, shouldCreateModuleRecord } = this
      let { formModuleName } = transition

      if (shouldCreateNotes) {
        return this.createNoteForModule(formModuleName, formData, transition)
      } else if (shouldCreateModuleRecord) {
        return this.createRecordForModule(formModuleName, formData, transition)
      }
    },
    createNoteForModule(moduleName, formData) {
      return new Promise((resolve, reject) => {
        API.post('v2/notes/add', {
          module: moduleName,
          ticketModuleName: this.moduleName,
          note: {
            parentId: this.record.id,
            notifyRequester: false,
            body: formData.body,
          },
        }).then(({ error }) => {
          if (error) {
            reject()
          } else {
            resolve()
          }
        })
      })
    },
    createRecordForModule(moduleName, formData, transition) {
      let { siteId = null } = this.record || {}
      let formId = transition.formId || null
      let { form } = transition
      let isCustomModule = this.$getProperty(form, 'module.custom', false)
      let data

      if (isCustomModule) {
        data = {
          moduleName,
          withLocalId: false,
          ...this.constructCustomModuleData({}, formData, transition),
        }
      } else {
        // TODO MOVE to v3

        if (moduleName === 'workpermit') {
          let datum = {
            ...formData,
            ticket: {
              id: this.record.id,
            },
          }
          if (!isEmpty(siteId)) datum.siteId = siteId
          if (!isEmpty(formId)) datum.formId = formId

          data = {
            workPermitRecords: [datum],
          }
        } else {
          let formObj = this.constructWoModuleData(formData, transition)
          data = {
            workorder: {
              ...formObj,
            },
          }
          moduleName = 'workorders'
        }
      }

      let url = isCustomModule ? 'v2/module/data/add' : `/v2/${moduleName}/add`

      return new Promise((resolve, reject) => {
        API.post(url, data).then(({ error }) => {
          if (error) {
            reject()
          } else {
            resolve()
          }
        })
      })
    },
    constructCustomModuleData(returnObj, data, transition) {
      // Remove once v3 is done for custom modules
      returnObj['moduleData'] = {}

      if (data) {
        if (data.comment) {
          returnObj.comment = data.comment
          delete data.comment
        }

        returnObj['moduleData'] = { ...returnObj['moduleData'], ...data }

        let { siteId = null } = this.record || {}
        let formId = transition.formId || null
        if (!isEmpty(siteId) && isEmpty(returnObj['moduleData'].siteId))
          returnObj['moduleData'].siteId = siteId
        if (!isEmpty(formId)) returnObj['moduleData'].formId = formId
      }

      let formFields = this.$getProperty(transition, 'form.fields') || []
      let currentModuleLookupField = formFields.find(
        field => field.lookupModuleName === this.moduleName
      )

      if (currentModuleLookupField) {
        returnObj['moduleData'].data = returnObj['moduleData'].data || {}
        returnObj['moduleData'].data[
          currentModuleLookupField.name
        ] = this.record.id
      }

      return returnObj
    },
    constructWoModuleData(data, transition) {
      // Remove once v3 is done for workorders, handle it through relations
      let formData = { ...data }

      let { siteId = null } = this.record || {}
      let formId = transition.formId || null
      if (!isEmpty(siteId) && isEmpty(formData.siteId)) formData.siteId = siteId
      if (!isEmpty(formId)) formData.formId = formId

      let formFields = this.$getProperty(transition, 'form.fields') || []
      let currentModuleLookupField = formFields.find(field => {
        let { field: fieldObj } = field
        return field.lookupModuleName === this.moduleName && !fieldObj.default
      })

      if (currentModuleLookupField) {
        formData.data = formData.data || {}
        formData.data[currentModuleLookupField.name] = { id: this.record.id }
      }

      Object.keys(formData).forEach(name => {
        let value = formData[name]
        if (isObject(value) && value.id === -99) {
          formData[name] = null
        }
      })

      return formData
    },
    defaultResponseHandler({ error } = {}) {
      let {
        $helpers: { delay },
        $message,
      } = this

      if (!error) {
        this.fetchAvailableStates({ force: true })
        this.$emit('transitionSuccess')
      } else {
        this.$emit('transitionFailure')
      }
      this.cancelTransition()

      if (!this.hideNotifications) {
        if (error) {
          $message.error(
            error.message ||
              this.$t('maintenance._workorder.state_update_failed')
          )
        } else {
          delay(200).then(() => {
            $message.success(
              this.$t('maintenance._workorder.state_update_success')
            )
          })
        }
      }
    },
    closeStateFlowForm() {
      this.canShowForm = false
    },
    showStateFlowForm() {
      this.canShowForm = true
    },
    closeConfirmationDialogs() {
      this.showConfirmationDialog = false
    },
    getUrl(transition) {
      if (isFunction(this.updateUrl)) {
        return this.updateUrl(transition)
      } else {
        return this.updateUrl
      }
    },
  },
}
</script>

<style scoped>
.transition-btn-name {
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
