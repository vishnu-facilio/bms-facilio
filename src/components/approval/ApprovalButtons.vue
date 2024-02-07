<script>
import { API } from '@facilio/api'
import TransitionButtons from '@/stateflow/TransitionButtons'
import {
  isEmpty,
  isFunction,
  isNullOrUndefined,
} from '@facilio/utils/validation'
import AsyncButton from '@/AsyncButton'
import TransitionForm from './ApprovalForm'

export default {
  extends: TransitionButtons,
  components: {
    AsyncButton,
    TransitionForm,
  },
  props: ['availableTransitions', 'size'],
  data() {
    return {
      pendingApprovalList: [],
      approvalRule: {},
      currentState: {},
    }
  },
  computed: {
    shouldCreateModuleRecord() {
      return false
    },
    shouldCreateNotes() {
      return false
    },
    rootClass() {
      let sizeClass = this.size
      return sizeClass
        ? `approval-transitions-container ${sizeClass}`
        : 'approval-transitions-container'
    },
    transitionFormProps() {
      let {
        selectedTransition,
        record,
        shouldCreateModuleRecord,
        saveDataForTransition,
        cancelTransition,
        isV3,
        approvalRule,
      } = this
      let { moduleName, formId } = selectedTransition || {}
      let { id: recordId } = record || {}

      return {
        moduleName,
        recordId,
        formId,
        record,
        isV3,
        approvalRule,
        transition: selectedTransition,
        isExternalModule: shouldCreateModuleRecord,
        saveAction: saveDataForTransition,
        closeAction: cancelTransition,
      }
    },
  },
  methods: {
    async fetchAvailableStates({ force = false } = {}) {
      if (!isNullOrUndefined(this.availableTransitions)) {
        this.$set(this, 'stateTransitions', this.availableTransitions || [])
      } else {
        this.loading = true

        let { data, error } = await API.post(
          '/v2/approval/availableTransitions',
          {
            moduleName: this.moduleName,
            id: this.record.id,
          },
          { force }
        )

        if (error) {
          this.stateTransitions = null
        } else {
          let transitions = this.$getProperty(data, 'states')
          let approversList = this.$getProperty(data, 'pendingApprovalList')
          let approvalRule = this.$getProperty(data, 'approvalRule')
          let currentState = this.$getProperty(data, 'currentState')

          this.$set(this, 'pendingApprovalList', approversList || [])
          this.$set(this, 'stateTransitions', transitions || [])
          this.$set(this, 'approvalRule', approvalRule || {})
          this.$set(this, 'currentState', currentState || {})
        }

        this.loading = false
      }
    },

    transitionToState(formData, transition) {
      if (isEmpty(transition)) transition = this.selectedTransition

      if (this.isV3) {
        let { moduleName, updateFn, defaultResponseHandler, record } = this
        let params = {
          id: record.id,
          approvalTransitionId: transition.id,
          data: formData || {},
        }

        if (!isEmpty(updateFn) && isFunction(updateFn)) {
          return updateFn(moduleName, params).then(defaultResponseHandler)
        } else {
          let url = `v3/action/${moduleName}/${record.id}/approval`

          return API.patch(url, params).then(response =>
            this.defaultResponseHandler(response, transition)
          )
        }
      } else {
        let url = isFunction(this.updateUrl)
          ? this.updateUrl(transition)
          : this.updateUrl

        let data = {
          moduleName: this.moduleName,
          id: this.record.id,
          approvalTransitionId: transition.id,
        }
        let params = this.transformFn(data, formData)

        return API.post(url, params).then(response =>
          this.defaultResponseHandler(response, transition)
        )
      }
    },

    defaultResponseHandler({ error }, transition) {
      let { canShowForm } = this

      if (error) {
        this.$emit('onFailure')
      } else {
        if (canShowForm) {
          this.closeStateFlowForm()
        }
        this.fetchAvailableStates()
        this.$emit('onSuccess')
        this.showMessage(transition)
      }
    },

    showMessage(transition = {}) {
      if (transition) {
        if (transition.name === 'Approve') {
          this.$message.success('Approved')
        } else if (transition.name === 'Reject') {
          this.$message.success('Rejected')
        } else if (transition.name === 'Re-send') {
          this.$message.success('Re-sent for approval')
        }
      }
    },
  },
}
</script>
<style lang="scss">
.approval-transitions-container {
  .stateflow-btn-wrapper {
    .el-button {
      padding: 10px 25px;
      cursor: pointer;
      text-transform: uppercase;
      font-size: 13px;
      font-weight: 500;
      text-align: center;
      display: inline-block;
      letter-spacing: 0.7px;
      border-radius: 3px;
    }

    .el-button--primary {
      box-shadow: 0 2px 4px 0 rgba(230, 230, 230, 0.5) !important;
      border: solid 1px #39b2c2;
      background-color: #39b2c2;
      color: #fff;
    }
    .el-button--primary:hover {
      background: #3cbfd0 !important;
      color: #fff;
    }

    .el-button:not(.el-button--primary) {
      border: solid 1px #dc7171;
      background-color: #dc7171;
      color: #ffffff;
    }
    .el-button:not(.el-button--primary):hover {
      color: #fff;
      background-color: #ef6d6d !important;
      border-color: #ef6d6d !important;
    }
  }

  &.small {
    .stateflow-btn-wrapper {
      .el-button {
        padding: 6px 16px;
        font-size: 11px;
      }
    }
  }
}
</style>
