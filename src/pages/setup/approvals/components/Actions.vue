<template>
  <div :style="containerHeight">
    <div id="actions-header" class="section-header">
      {{ $t('common.products.actions') }}
    </div>
    <div class="p50 pT20 pR70 pB30 el-form" style="min-height: 330px;">
      <ActionsConfig
        ref="entry-action-config"
        class="mT15 mB20"
        :configuredActions="entryActions"
        :module="moduleName"
        :moduleFields="moduleFields"
        :actionsHash="actionsHash"
        :supportedActions="supportedActions"
      >
        <template #header>
          <div class="fc-input-label-txt">
            {{
              $t('common._common.specify_actions_execute_when_record_approval')
            }}
          </div>
        </template>
      </ActionsConfig>
      <ActionsConfig
        ref="approve-action-config"
        class="mT15 mB20"
        :configuredActions="approveActionList"
        :module="moduleName"
        :moduleFields="moduleFields"
        :actionsHash="actionsHash"
        :supportedActions="supportedActions"
      >
        <template #header>
          <div class="fc-input-label-txt">
            {{ $t('common._common.specify_actions_execute') }}
          </div>
        </template>
      </ActionsConfig>
      <ActionsConfig
        ref="reject-action-config"
        class="mT15 mB20"
        :configuredActions="rejectActionList"
        :module="moduleName"
        :moduleFields="moduleFields"
        :actionsHash="actionsHash"
        :supportedActions="supportedActions"
      >
        <template #header>
          <div class="fc-input-label-txt">
            {{
              $t(
                'common._common.specify_actions_to_execute_when_record_rejected'
              )
            }}
          </div>
        </template>
      </ActionsConfig>
      <ActionsConfig
        ref="resend-action-config"
        class="mT15 mB20"
        :configuredActions="resendActionList"
        :module="moduleName"
        :moduleFields="moduleFields"
        :actionsHash="actionsHash"
        :supportedActions="supportedActions"
      >
        <template #header>
          <div class="fc-input-label-txt">
            {{
              $t('common._common.specify_actions_execute_when_record_resend')
            }}
          </div>
        </template>
      </ActionsConfig>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import ActionsConfig from './ActionConfig'

const actionsHash = {
  email: {
    label: 'Send Email',
    actionParam: 'mail',
  },
  sms: {
    label: 'Send SMS',
    actionParam: 'sms',
  },
  mobile: {
    label: 'Send Mobile Notification',
    actionParam: 'mobile',
  },
  fieldUpdate: {
    label: 'Field Update',
    actionParam: 'fieldChange',
  },
  script: {
    label: 'Execute Script',
    actionParam: 'script',
  },
  changeStatus: {
    label: 'Change Status',
    actionParam: 'changeStatus',
  },
}
export default {
  name: 'Actions',
  props: {
    policy: {
      type: Object,
    },
    moduleFields: {
      type: Array,
    },
    formFields: {
      type: Array,
    },
  },
  data() {
    return {
      actionsHash: actionsHash,
    }
  },
  components: { ActionsConfig },
  computed: {
    isNew() {
      return isEmpty(this.$route.params.id)
    },
    moduleName() {
      return this.$route.params.moduleName
    },
    entryActions() {
      return this.policy.approvalEntryActions
    },
    approveActionList() {
      return this.policy.approveActions || []
    },
    resendActionList() {
      return this.policy.resendActions || []
    },
    rejectActionList() {
      return this.policy.rejectActions || []
    },
    containerHeight() {
      let root = document.querySelector('.scroll-container')
      let height = root.offsetHeight || 370

      return {
        'min-height': height - 60 + 'px',
      }
    },
    supportedActions() {
      const actions = [
        'email',
        'mobile',
        'fieldUpdate',
        'script',
        'changeStatus',
      ]
      if (this.$helpers.isLicenseEnabled('SMS')) actions.push('sms')
      return actions
    },
  },
  methods: {
    serialize() {
      let approvalEntryActions = this.$refs['entry-action-config'].serialize()
      let approveActions = this.$refs['approve-action-config'].serialize()
      let rejectActions = this.$refs['reject-action-config'].serialize()
      let resendActions = this.$refs['resend-action-config'].serialize()

      return {
        approvalEntryActions,
        approveActions,
        rejectActions,
        resendActions,
      }
    },
  },
}
</script>
