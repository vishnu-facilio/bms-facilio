<template>
  <el-dialog
    :visible="true"
    :before-close="closeDialog"
    :append-to-body="true"
    width="30%"
    :title="'Change Status'"
    custom-class="fc-dialog-center-container state-transition-list-dialog"
  >
    <div>
      <div class="state-transition-list-container">
        <el-row class="mB20">
          <el-col :span="24">
            <el-form
              :model="formModel"
              :rules="rules"
              ref="stateChooser"
              :label-position="'top'"
            >
              <el-form-item
                label="Choose Transition"
                prop="selectedTransitionId"
              >
                <el-select
                  v-model="formModel.selectedTransitionId"
                  @change="transitionChangeAction"
                  class="fc-input-full-border2 width100"
                >
                  <el-option
                    v-for="transition in stateTransitions || []"
                    :label="transition.name"
                    :value="transition.id"
                    :key="transition.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-form>
          </el-col>
        </el-row>
        <div v-if="selectedTransition && selectedTransition.formId > 0">
          <TransitionForm
            ref="state-transition-form"
            :moduleName="selectedTransition.moduleName"
            :recordId="record.recordId"
            :formId="selectedTransition.formId"
            :record="record"
            :transition="selectedTransition"
            :isExternalModule="shouldCreateModuleRecord"
            :saveAction="transitionToState"
            :closeAction="closeStateFlowForm"
          ></TransitionForm>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog" class="modal-btn-cancel">
          {{ $t('common._common.cancel') }}
        </el-button>
        <el-button
          type="primary"
          @click="changeState"
          :loading="isSaving"
          class="modal-btn-save"
        >
          {{ $t('common._common._save') }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import TransitionButtons from './TransitionButtons'
import TransitionForm from './ExecuteTransitionForm'
import { isEmpty, isFunction } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  extends: TransitionButtons,
  components: { TransitionForm },
  data() {
    return {
      selectedTransition: null,
      formModel: { selectedTransitionId: null },
      isSaving: false,
      rules: {
        selectedTransitionId: [
          {
            required: true,
            message: 'Please Choose a Transition',
            trigger: 'change',
          },
        ],
      },
    }
  },
  methods: {
    closeDialog() {
      this.$emit('onClose')
    },
    transitionChangeAction() {
      let { formModel } = this
      this.selectedTransition = this.stateTransitions.find(
        transition => transition.id == formModel.selectedTransitionId
      )
    },
    changeState() {
      let { selectedTransition } = this
      this.$refs.stateChooser.validate(valid => {
        if (valid && !isEmpty(selectedTransition)) {
          if ((selectedTransition || {}).formId > 0) {
            this.isSaving = true
            this.saveFormRecord()
          } else {
            this.isSaving = true
            this.startTransition(selectedTransition)
          }
        }
      })
    },
    saveFormRecord() {
      let { $refs } = this
      if (!isEmpty($refs)) {
        let formComponent = $refs['state-transition-form']
        if (!isEmpty(formComponent)) {
          formComponent.saveRecord()
        }
      }
    },
    defaultResponseHandler({ error }) {
      this.isSaving = false
      if (!error) {
        this.$message.success(
          this.$t('maintenance._workorder.state_update_success')
        )
        this.$emit('transitionSuccess')
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
<style lang="scss" scoped>
.state-transition-list-dialog {
  .state-transition-list-container {
    min-height: 200px;
    max-height: 600px;
    overflow-x: hidden;
    overflow-y: scroll;
    padding: 0px 1px 60px 1px;
  }
}
</style>
