<template>
  <div>
    <div
      v-if="record.canCurrentUserApprove"
      class="approval-actions"
      :class="{ hide: onHover }"
    >
      <slot name="action">
        <template>
          <div class="flLeft">
            <async-button
              v-if="!$validation.isEmpty(transitions[0])"
              :buttonClass="approveClass || 'approve-btn'"
              :disable="transitions[0].scheduled"
              :clickAction="() => updateTransition(true, transitions[0])"
              >{{ transitions[0].name }}</async-button
            >
          </div>
          <div class="flLeft">
            <async-button
              v-if="!$validation.isEmpty(transitions[1])"
              :buttonClass="rejectClass || 'fc-reject-btn'"
              :disable="transitions[1].scheduled"
              :clickAction="() => updateTransition(false, transitions[1])"
              >{{ transitions[1].name }}</async-button
            >
          </div>
        </template>
      </slot>
    </div>
    <div v-else>
      <slot name="waiting"></slot>
    </div>

    <template v-if="canShowForm">
      <TransitionForm
        :moduleName="selectedTransition.moduleName"
        :recordId="record.recordId"
        :formId="selectedTransition.formId"
        :record="record"
        :transition="selectedTransition"
        :saveAction="transitionToState"
        :closeAction="closeStateFlowForm"
      ></TransitionForm>
    </template>
  </div>
</template>

<script>
import TransitionButtons from '@/stateflow/TransitionButtons'
import TransitionForm from '@/stateflow/TransitionForm'
import AsyncButton from '@/AsyncButton'
import { isEmpty, isFunction } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  name: 'ApproveReject',
  extends: TransitionButtons,
  props: [
    'rejectClass',
    'approveClass',
    'record',
    'onHover',
    'moduleName',
    'moduleDisplayName',
    'updateUrl',
    'transformFn',
    'stateFlowList',
  ],
  components: { TransitionForm, AsyncButton },

  data() {
    return {
      isApproved: false,
    }
  },

  computed: {
    stateFlows() {
      return this.stateFlowList || this.$store.state[this.moduleName].stateFlows
    },

    isStateFlowEnabled() {
      return Boolean(this.record.moduleState && this.record.moduleState.id)
    },

    transitions() {
      if (this.isStateFlowEnabled) {
        let states = this.stateFlows[
          this.record.stateFlowId + '_' + this.record.moduleState.id
        ]
        return [...states].sort((a, b) => a.buttonType - b.buttonType)
      } else {
        return null
      }
    },
  },

  methods: {
    fetchAvailableStates() {},
    updateTransition(isApproved, transition) {
      this.isApproved = isApproved
      return this.startTransition(transition)
    },
    defaultResponseHandler({ error }) {
      let minimumTimeout = this.$helpers.delay(1000)

      if (!error) {
        if (this.canShowForm) this.closeStateFlowForm()

        this.$helpers
          .delay(500)
          .then(() =>
            this.$message.success(
              `${this.moduleDisplayName || 'Work Order'} is ${
                this.isApproved ? 'Approved' : 'Rejected'
              }`
            )
          )
        minimumTimeout.then(() => {
          this.$emit('transitionSuccess')
        })
      } else {
        this.$message({
          showClose: true,
          message:
            error.message ||
            this.$t('maintenance._workorder.state_update_failed'),
          type: 'error',
        })
        this.$emit('transitionFailure')
      }
    },
    transitionToState(formData, transition) {
      if (isEmpty(transition)) transition = this.selectedTransition

      let url = this.getUrl(transition)
      let params = this.getParams({ formData, transition })
      let { defaultResponseHandler, updateFn, moduleName } = this

      if (this.isV3) {
        if (!isEmpty(updateFn) && isFunction(updateFn)) {
          return updateFn(moduleName, params).then(defaultResponseHandler)
        } else {
          return API.updateRecord(moduleName, params).then(
            defaultResponseHandler
          )
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
  },
}
</script>

<style>
.approve-btn {
  padding: 10px 20px;
  font-weight: 500;
  border-radius: 3px;
  background-color: #39b2c2;
  color: #fff;
  cursor: pointer;
  font-size: 12px;
  letter-spacing: 0.3px;
  margin-right: 15px;
  text-transform: uppercase;
}
.approve-btn:hover {
  color: #fff;
  background: #33a6b5;
  transition: all 0.5s ease-in-out;
  -webkit-transition: all 0.5s ease-in-out;
}
.approval-actions .fc-reject-btn:hover {
  color: #fff;
  background-color: #dc7171;
  border-color: none;
}
</style>
