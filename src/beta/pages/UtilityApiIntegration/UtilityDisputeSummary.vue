<template>
  <FContainer height="100%">
    <FContainer
      v-if="isLoading"
      height="100%"
      display="flex"
      alignItems="center"
      justifyContent="center"
    >
      <FSpinner :size="30" />
    </FContainer>
    <template v-else>
      <FContainer
        display="flex"
        flexDirection="column"
        height="100%"
        class="page-summary-container-ms"
      >
        <FContainer
          padding="containerNone containerXLarge"
          flex-shrink="0"
          class="page-header-ms"
        >
          <FPageHeader
            :heading="record[mainFieldKey]"
            :breadCrumbProps="{ appearance: 'back' }"
            :tagProps="{ ...currentStatus }"
            :captionText="record ? `#${record.id}` : ''"
            :withTabs="true"
            @path="back"
          >
            <template #buttons>
              <FButtonGroup :moreOptions="false">
                <template #buttons>
                  <CustomButton
                    :record="record"
                    :moduleName="moduleName"
                    :position="POSITION.SUMMARY"
                    @refresh="refreshData()"
                    @onError="() => {}"
                    @hasCustomButtons="val => (hasCustomButtons = val)"
                  />
                  <FDivider
                    v-if="hasCustomButtons"
                    height="14px"
                    margin="containerNone containerLarge"
                  />
                  <StateflowButton
                    v-if="record.isStateFlowEnabled()"
                    :key="`${record.id}transitions`"
                    :moduleName="moduleName"
                    :record="record"
                    :disabled="record.isApprovalEnabled()"
                    buttonClass="asset-el-btn"
                    @currentState="() => {}"
                    @transitionSuccess="refreshData()"
                    @transitionFailure="() => {}"
                    @hasTransitionBtns="val => (hasTransitionBtns = val)"
                  />
                  <FDivider
                    v-if="hasTransitionBtns"
                    height="14px"
                    margin="containerNone containerLarge"
                  />
                  <FButton
                    appearance="primary"
                    v-if="primaryButton.label"
                    @click="primaryButton.clickAction(primaryButton)"
                    :loading="primaryButton.loading"
                  >
                    {{ primaryButton.label }}
                  </FButton>
                  <FButton
                    appearance="primary"
                    v-if="secondaryButton.label"
                    @click="secondaryButton.clickAction(secondaryButton)"
                    :loading="secondaryButton.loading"
                  >
                    {{ secondaryButton.label }}
                  </FButton>
                </template>
              </FButtonGroup>
            </template>
          </FPageHeader>
        </FContainer>
        <FContainer
          padding="containerNone containerMedium"
          flexGrow="1"
          overflow="hidden"
          class="page-builder-ms"
        >
          <Page
            :key="record.id"
            :module="moduleName"
            :id="record.id"
            :details="record"
            :primaryFields="primaryFields"
            :notesModuleName="notesModuleName"
            :isV3Api="true"
            :attachmentsModuleName="attachmentsModuleName"
          >
          </Page>
        </FContainer>
      </FContainer>
    </template>
  </FContainer>
</template>

<script>
import {
  FContainer,
  FButton,
  FButtonGroup,
  FPageHeader,
  FSpinner,
  FDivider,
} from '@facilio/design-system'
import { API } from '@facilio/api'
import CustomModuleSummary from 'src/pages/custom-module/CustomModuleSummary.vue'
import StateflowButton from 'src/beta/summary/buttons/StateflowButton.vue'
import CustomButton from 'src/beta/summary/buttons/CustomButton.vue'
import Page from 'src/beta/summary/PageBuilder.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: CustomModuleSummary,
  name: 'UtilityDisputetSummary',
  components: {
    FContainer,
    FButtonGroup,
    FButton,
    FPageHeader,
    FSpinner,
    StateflowButton,
    FDivider,
    CustomButton,

    Page,
  },
  data() {
    return {
      hasTransitionBtns: false,
      hasCustomButtons: false,
      primaryButton: {},
      secondaryButton: {},
      moreActions: [],
      isLoading: false,
      buttons: {
        resolveDispute: this.resolveDispute,
      },
    }
  },
  computed: {
    mainFieldKey() {
      return 'subject'
    },

    currentStatus() {
      let { record } = this || {}
      let { moduleState } = record || {}
      let displayName = moduleState?.displayName
      let type = ''
      if (displayName === 'Under Dispute') {
        type = 'warning'
      } else {
        type = 'success'
      }
      if (moduleState.status) {
        return {
          appearance: 'status',
          text: displayName || '',
          statusType: type,
        }
      }
      return { appearance: 'status', text: '' }
    },
    recordLocked() {
      let { record } = this || {}
      let { status } = record || {}
      if (status) {
        return !status?.recordLocked
      }
      return false
    },
    isStateFlowEnabled() {
      let hasState = this.$getProperty(this.record, 'moduleState.id')
      let isEnabled = this.$getProperty(
        this.moduleMeta,
        'module.stateFlowEnabled'
      )
      return hasState && isEnabled
    },
    isApprovalEnabled() {
      let { record } = this
      let { approvalFlowId, approvalStatus } = record || {}
      return !isEmpty(approvalFlowId) && !isEmpty(approvalStatus)
    },
    deleteLocked() {
      let { record } = this || {}
      let { status } = record || {}
      if (status) {
        return !status?.deleteLocked
      }
      return false
    },
  },
  watch: {
    record: {
      handler() {
        this.loadSystemButtons()
      },
    },
  },
  methods: {
    async fetchData(button, warning, params = {}) {
      let { record } = this || {}
      let url = `v3/module/utilityDispute/${record.id}/systemButton/${button?.identifier}?moduleName=utilityDispute&recordId=${record.id}&buttonAction=systemButton&identifier=${button?.identifier}`
      button.loading = true
      let { error, data } = await API.post(url, params)
      button.loading = false
      return { error, data }
    },
    async loadSystemButtons() {
      let { record, buttons } = this || {}
      let { id } = record || {}
      if (record) {
        this.isLoading = true
        let url = `v2/systemButton/getAvailableButtons`
        let bodyReg = {
          id,
          moduleName: 'utilityDispute',
          positionType: 1,
        }
        let { error, data } = await API.get(url, bodyReg, { force: true })
        if (error) {
          let { message } = error
          this.$message.error(message)
        } else {
          let { workflowRuleList } = data || {}
          if (workflowRuleList) {
            this.primaryButton = {}
            this.secondaryButton = {}
            this.moreActions = []
            workflowRuleList.map((element, index) => {
              if (element?.buttonTypeEnum === 'CREATE') {
                this.primaryButton = {
                  buttonType: element?.buttonTypeEnum,
                  identifier: element?.identifier,
                  label: element?.name,
                  clickAction: buttons[element?.identifier],
                  loading: false,
                }
              } else if (element?.buttonTypeEnum === 'EDIT') {
                this.secondaryButton = {
                  buttonType: element?.buttonTypeEnum,
                  identifier: element?.identifier,
                  label: element?.name,
                  clickAction: buttons[element?.identifier],
                  loading: false,
                }
              } else if (element?.buttonTypeEnum === 'OTHERS') {
                this.moreActions.push({
                  buttonType: element?.buttonTypeEnum,
                  identifier: element?.identifier,
                  label: element?.name,
                  clickAction: buttons[element?.identifier],
                })
              }
            })
          }
        }
      }
      this.isLoading = false
    },
    async resolveDispute(button, warning = true) {
      let { refreshData } = this || {}
      let { error, data } = await this.fetchData(button, warning)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        if (data?.utilityDisputeStatus) {
          let { message = '' } = data.utilityDisputeStatus
          this.$message.success(message)
          refreshData()
        }
      }
    },

    refreshData() {
      this.loadRecord(true)
    },
    async loadRecord(force = false) {
      try {
        let { moduleName, id } = this
        this.isLoading = true
        this.record = await this.modelDataClass.fetch({ moduleName, id, force })
        await this.getFormMeta()
      } catch (error) {
        this.$message.error(
          error?.message ||
            this.$t('custommodules.summary.record_summary_error')
        )
      }
      this.isLoading = false
    },
  },
}
</script>
