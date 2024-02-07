<template>
  <div>
    <div :class="['sidebar-loader', (loading || isSaving) && 'on']"></div>

    <div v-if="loading" class="d-flex approval-bar">
      {{ $t('panel.loading_load') }}
    </div>

    <div v-else class="d-flex approval-bar">
      <div class="approval-desc mR30 f14">
        <template v-if="isRejected">
          <span class="bold">{{ policyName }}</span> {{ $t('panel.was') }}
          <span class="bold" style="color: #dd7171;">{{
            $t('panel.rejected')
          }}</span>
          for this {{ moduleDisplayName || moduleName }}.
          <template v-if="resendApproval">
            <span>You can</span>
            <template v-if="canShowEdit">
              <span class="mL5">either</span>
              <span :class="[isSaving && 'cursor-not-allowed']">
                <a
                  @click="$emit('onEdit')"
                  :class="['mL5 bold', isSaving && 'pointer-events-none']"
                >
                  edit
                </a>
              </span>
              <span class="mL5">or</span>
            </template>
            <span :class="[isSaving && 'cursor-not-allowed']">
              <a
                @click="requestToSave(resendApproval)"
                :class="['mL5 bold', isSaving && 'pointer-events-none']"
              >
                resend
              </a>
            </span>
            <span class="mL5">for approval.</span>
          </template>
        </template>

        <template v-else-if="hasApprovalList && !hideApprovers">
          <span class="bold">{{ policyName }}</span> is pending from
          <span class="bold">{{ approvers[0].name }}</span>
          <el-popover
            v-if="approvers.length > 1"
            placement="bottom"
            width="200"
            trigger="hover"
          >
            <slot :slot="'reference'">
              <div class="mL5 approvers-more-icon">
                +{{ approvers.length - 1 }}
              </div>
            </slot>
            <div>
              <div
                v-for="approver in approvers.slice(1)"
                :key="approver.name"
                class="mB10"
              >
                <ApproverTypeAvatar
                  size="md"
                  :user="approver"
                  :userType="getApproverType(approver)"
                  :moduleName="moduleName"
                  :approvalMeta="approvalMeta"
                ></ApproverTypeAvatar>
              </div>
            </div>
          </el-popover>
        </template>

        <template v-else-if="!$validation.isEmpty(transitions)">
          <span class="bold">{{ policyName }}</span> is pending
        </template>

        <template v-else>
          <span class="bold">{{ policyName }}</span> is pending
        </template>
      </div>

      <div
        class="approval-links text-uppercase f12 bold"
        v-if="!$validation.isEmpty(transitions)"
      >
        <div :class="[isSaving && 'cursor-not-allowed']">
          <a
            v-if="approveTransition"
            @click="requestToSave(approveTransition)"
            :class="['link-approve mR15', isSaving && 'pointer-events-none']"
          >
            <inline-svg src="tick-sign" iconClass="icon icon-xs"></inline-svg>
            {{ approveTransition.name }}
          </a>

          <a
            v-if="rejectTransition"
            @click="requestToSave(rejectTransition)"
            :class="['link-reject', isSaving && 'pointer-events-none']"
          >
            <inline-svg src="svgs/close" iconClass="icon icon-xxs"></inline-svg>
            {{ rejectTransition.name }}
          </a>
        </div>
      </div>
    </div>

    <template v-if="canShowForm">
      <TransitionForm
        :moduleName="selectedTransition.moduleName"
        :recordId="record.recordId"
        :formId="selectedTransition.formId"
        :record="record"
        :transition="selectedTransition"
        :approvalRule="approvalRule"
        :saveAction="saveAction"
        :closeAction="closeStateFlowForm"
        :isV3="isV3"
      ></TransitionForm>
    </template>
  </div>
</template>
<script>
import ApprovalButtons from './ApprovalButtons'
import TransitionForm from './ApprovalForm'
import ApproverTypeAvatar from 'pages/approvals/components/ApprovalUserAvatar'
import { isEmpty, isArray } from '@facilio/utils/validation'
import { mapState } from 'vuex'

export default {
  extends: ApprovalButtons,
  props: ['hideApprovers', 'canShowEdit', 'canHideMsg'],
  components: { TransitionForm, ApproverTypeAvatar },
  data() {
    return {
      isSaving: false,
    }
  },
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
    }),
    transitions() {
      if (!isEmpty(this.stateTransitions)) {
        return [...this.stateTransitions].slice(0, 2)
      } else {
        return []
      }
    },
    policyName() {
      return isEmpty(this.approvalRule)
        ? 'Approval'
        : this.$getProperty(this.approvalRule, 'name')
    },
    shouldCurrentUserApprove() {
      return !isEmpty(this.transitions)
    },
    hasApprovalList() {
      return !isEmpty(this.approvers)
    },
    approvalMeta() {
      return { approvalList: this.pendingApprovalList }
    },
    approvers() {
      let approvers = []

      this.pendingApprovalList.forEach(approver => {
        let { value, type } = approver

        if (!isEmpty(value)) {
          if (isArray(value)) {
            approvers.push(...value)
          } else {
            approvers.push(value)
          }
        } else if (type === 'TENANT') {
          approvers.push({ name: 'Tenant', type: 'TENANT' })
        } else if (type === 'VENDOR') {
          approvers.push({ name: 'Vendor', type: 'VENDOR' })
        }
      })

      return approvers
    },
    isRejected() {
      return this.$getProperty(this.currentState, 'status') === 'Rejected'
    },
    shouldCreateModuleRecord() {
      return false
    },
    shouldCreateNotes() {
      return false
    },
    approveTransition() {
      return this.transitions.find(t => t.name === 'Approve')
    },
    rejectTransition() {
      return this.transitions.find(t => t.name === 'Reject')
    },
    resendApproval() {
      return this.transitions.find(t => !['Approve', 'Reject'].includes(t.name))
    },
    moduleDisplayName() {
      return this.metaInfo?.displayName
    },
  },
  methods: {
    showMessage(transition) {
      /* override */
      let { name } = transition
      let action = ''

      if (!['Approve', 'Reject'].includes(name)) {
        action = 'resent for approval'
      } else if (name === 'Approve') {
        action = 'approved'
      } else if (name === 'Reject') {
        action = 'rejected'
      }

      this.$message.success(
        `${this.moduleDisplayName || this.moduleName} was ${action}`
      )
    },

    requestToSave(transition) {
      this.isSaving = true
      this.startTransition(transition).finally(() => (this.isSaving = false))
    },

    saveAction(formData) {
      return this.transitionToState(formData)
    },

    getApproverType(approver) {
      return approver.id ? { type: 'USER', value: approver } : approver
    },

    defaultResponseHandler({ error }, transition) {
      let { canShowForm } = this

      if (error) {
        !this.canHideMsg && this.$message.error(error.message)
        this.$emit('onFailure', { error, transition })
      } else {
        if (canShowForm) {
          this.closeStateFlowForm()
        }
        this.fetchAvailableStates()
        this.$emit('onSuccess', { error, transition })

        !this.canHideMsg && this.showMessage(transition)
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.approval-bar {
  background-color: #f5fdff;
  padding: 20px;
  justify-content: center;
  align-items: center;
  border-bottom: 1px solid #c8dfe4;

  .approval-links {
    .link-approve {
      color: #3ab2c2;
      svg {
        fill: #3ab2c2;
      }
    }
    .link-reject {
      color: #dd7171;
      svg {
        fill: #dd7171;
      }
    }
  }

  .approvers-more-icon {
    background-color: #dcebef;
    border-radius: 50%;
    padding: 6px 8px 6px 4px;
    font-size: 12px;
    font-weight: bold;
    cursor: pointer;
    display: inline;
  }
}

.sidebar-loader.on {
  background: repeating-linear-gradient(
    to right,
    #f6f7f8 0%,
    #39b2c2 25%,
    #39b2c2 50%,
    #39b2c2 75%,
    #39b2c2 100%
  );
  background-size: 200% auto;
  background-position: 0 100%;
  animation-timing-function: cubic-bezier(0.4, 0, 1, 1);
}
</style>
