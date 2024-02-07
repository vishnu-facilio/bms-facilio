<template>
  <!-- OverWiriting For Header -->
  <div
    class="custom-modules-overview overflow-y-scroll"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <div v-if="!isLoading" class="header pT10 pB15 pL20 pR20">
      <div v-if="!$validation.isEmpty(customModuleData)" class="asset-details">
        <div class="d-flex flex-middle">
          <div class="mL5">
            <div class="asset-id mT10">#{{ customModuleData.id }}</div>
            <div class="asset-name pT5">
              {{ customModuleData[mainFieldKey] }}
              <span v-if="currentModuleState">
                <div
                  v-if="currentModuleState"
                  class="fc-badge text-uppercase inline vertical-middle mL5"
                >
                  {{ currentModuleState }}
                </div>
              </span>
            </div>
            <!-- <div class="flex-middle">
              <div class="fc-grey12-10 pT5">
                FEWA
              </div>
              <div class="pR10 pL10 fc-grey12-10 pT5">|</div>
              <div class="fc-green-label5 pT5">Active</div>
            </div> -->
          </div>
        </div>
      </div>
      <div class="d-flex flex-direction-row" style="margin-left: auto;">
        <template v-if="isStateFlowEnabled">
          <TransitionButtons
            class="mR10"
            :key="customModuleData.id"
            :moduleName="moduleName"
            :record="customModuleData"
            :updateUrl="updateUrl"
            :transformFn="transformFormData"
            :disabled="isApprovalEnabled"
            buttonClass="asset-el-btn"
            @currentState="() => {}"
            @transitionSuccess="refreshObj()"
            @transitionFailure="() => {}"
          ></TransitionButtons>
        </template>
      </div>
    </div>
    <page
      v-if="!isLoading && customModuleData && customModuleData.id"
      :key="customModuleData.id"
      :module="moduleName"
      :id="customModuleData.id"
      :details="customModuleData"
      :primaryFields="primaryFields"
      :notesModuleName="notesModuleName"
      :attachmentsModuleName="attachmentsModuleName"
    ></page>
  </div>
</template>
<script>
import CustomModuleOverview from 'pages/base-module-v2/Overview'
import { isEmpty } from '@facilio/utils/validation'
import { mapGetters } from 'vuex'
export default {
  extends: CustomModuleOverview,
  props: ['details'],
  data() {
    return {
      updateUrl: `/v2/module/data/update`,
    }
  },
  computed: {
    ...mapGetters(['getTicketStatus']),
    moduleName() {
      return 'custom_utilityaccounts_1'
    },
    currentModuleState() {
      let { moduleName, customModuleData } = this
      let currentStateId = this.$getProperty(customModuleData, 'moduleState.id')
      let currentState = this.getTicketStatus(currentStateId, moduleName)
      let { displayName, status } = currentState || {}

      if (!isEmpty(displayName)) {
        return displayName
      }
      return status || null
    },
  },
}
</script>
