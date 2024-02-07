<template>
  <el-dialog
    :append-to-body="true"
    :visible="true"
    class="fc-dialog-header-hide wo-state-transition-dialog-center approval-transition-dialog"
    custom-class="wo-web-form"
    :width="hasMultiColumnElements ? '70%' : '35%'"
    style="z-index: 999999"
  >
    <Spinner
      v-if="isLoading || isFormDataLoading"
      size="80"
      :show="true"
    ></Spinner>
    <template v-else>
      <div class="header pL30 pB20 pT10 d-flex bold">
        <div class="title mT10">{{ policyName }}</div>
        <div @click="closeAction" class="mL-auto mR15 mT5 pointer">
          <inline-svg src="svgs/close" iconClass="icon icon-xs"></inline-svg>
        </div>
      </div>
      <f-webform
        :class="transitionClass"
        :form.sync="formObj"
        :module="moduleName"
        :isSaving="isSaving"
        :isV3Api="isV3Api"
        :canShowPrimaryBtn="true"
        :canShowSecondaryBtn="false"
        :isEdit="false"
        :isEditForm="true"
        formLabelPosition="top"
        @save="submitForm"
        @cancel="closeAction"
      ></f-webform>
    </template>
  </el-dialog>
</template>
<script>
import TransitionForm from '@/stateflow/TransitionForm'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  extends: TransitionForm,
  props: ['approvalRule'],
  data() {
    return {
      approvalDetails: {},
    }
  },
  computed: {
    policyName() {
      let { approvalRule, approvalDetails } = this
      return !isEmpty(approvalRule)
        ? this.$getProperty(approvalRule, 'name')
        : !isEmpty(approvalDetails)
        ? this.$getProperty(approvalDetails, 'approvalRule.name')
        : 'Approval'
    },
    transitionClass() {
      let { transition } = this
      if (transition && transition.name === 'Reject') {
        return 'reject-form'
      }
      return 'approval-form'
    },
  },
  watch: {
    transition() {
      this.setFormLabels()
    },
    approvalRule: {
      async handler() {
        if (isEmpty(this.approvalRule)) {
          await this.fetchApprovalDetails()
        }
      },
      immediate: true,
    },
  },
  methods: {
    setFormLabels() {
      let { transition } = this
      if (transition) {
        this.$set(this.formObj, 'primaryBtnLabel', transition.name)
      }
    },
    fetchApprovalDetails() {
      let {
        record: { id },
        moduleName,
      } = this

      if (id) {
        return API.post('v2/approval/approvalDetails', {
          moduleName,
          id,
        }).then(({ error, data }) => {
          if (!error) {
            this.approvalDetails = data || {}
          }
        })
      } else {
        return Promise.resolve()
      }
    },
  },
}
</script>
<style lang="scss">
.approval-transition-dialog {
  .reject-form .fc-web-form-action-btn .primary {
    border: solid 1px #dc7171;
    background-color: #dc7171;
  }
  .f-webform-container {
    overflow: auto;
  }
}
</style>
