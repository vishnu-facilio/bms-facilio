<template>
  <!-- OverWiriting For Header -->
  <div
    class="custom-modules-overview overflow-y-scroll"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <div v-if="!isLoading" class="header pT10 pB15 pL20 pR20 border-bottom18">
      <div v-if="!$validation.isEmpty(customModuleData)" class="asset-details">
        <div class="d-flex flex-middle">
          <div class="mL5">
            <div class="asset-id mb5 mT10">#{{ customModuleData.id }}</div>
            <div class="asset-name">
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
            <div class="d-flex pT10">
              <div class="fc-black-12">
                <span>{{
                  customModuleData.data.lookup_1
                    ? customModuleData.data.lookup_1.name
                    : ''
                }}</span>
              </div>
            </div>
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
        <el-dropdown
          class="dashboard-dropdown-right p10 pR10 pL10 self-center bl-more-btn"
          @command="dashboardCommand"
          v-if="bill"
          trigger="click"
        >
          <span class="el-dropdown-link" v-if="bill">
            <i class="el-icon-more rotate-90 pointer f14 fc-grey6"></i>
          </span>
          <el-dropdown-menu slot="dropdown" class="dashboard-subheader-dp">
            <el-dropdown-item command="edit">
              <div>{{ 'Show parsed bill' }}</div>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
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
    <BillSummaryDialog
      v-if="showBillSummary && bill"
      :visibility.sync="showBillSummary"
      :summary="bill"
    ></BillSummaryDialog>
  </div>
</template>
<script>
import CustomModuleOverview from 'pages/base-module-v2/Overview'
import BillSummaryDialog from 'src/pages/etisalat/UtilityBills/UtilityBillsSummaryDialog'
import { isEmpty } from '@facilio/utils/validation'
export default {
  extends: CustomModuleOverview,
  props: ['details'],
  data() {
    return {
      showBillSummary: false,
      updateUrl: `/v2/module/data/update`,
    }
  },
  computed: {
    moduleName() {
      return 'custom_alert'
    },
    bill() {
      if (
        !isEmpty(this.customModuleData) &&
        this.customModuleData.data &&
        this.customModuleData.data.utilitybill_1
      ) {
        return this.customModuleData.data.utilitybill_1
      }
      return null
    },
    currentModuleState() {
      let { moduleName, customModuleData } = this
      let currentStateId = this.$getProperty(customModuleData, 'moduleState.id')
      let currentState = this.$store.getters.getTicketStatus(
        currentStateId,
        moduleName
      )
      let { displayName, status } = currentState || {}

      if (!isEmpty(displayName)) {
        return displayName
      }
      return status || null
    },
  },
  components: {
    BillSummaryDialog,
  },
  methods: {
    dashboardCommand(modeule) {
      if (modeule === 'edit') {
        this.showBillSummary = true
      }
    },
  },
}
</script>
<style>
.alert-critical {
  color: #fe6c6c;
}
.alert-major {
  color: #ed9b56;
}
.alert-minor {
  color: #d5bd21;
}
.bl-more-btn {
  background: #fff;
  border-radius: 3px;
  /* box-shadow: 0 2px 4px 0 rgba(230, 230, 230, 0.5); */
  border: 1px solid #d9e0e7;
  color: #605e88;
  font-size: 14px;
}
</style>
