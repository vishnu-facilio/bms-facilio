<template>
  <div class="custom-module-overview" :class="customClass">
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else-if="!$validation.isEmpty(record)">
      <div class="header p20">
        <div class="fL custom-module-details d-flex mL5">
          <div class="custom-module-id">#{{ record && record.id }}</div>
          <div class="custom-module-name d-flex">
            <div class="d-flex max-width300px ">
              <el-tooltip
                placement="bottom"
                effect="dark"
                :content="record[mainFieldKey]"
              >
                <span class="whitespace-pre-wrap custom-header">{{
                  record[mainFieldKey]
                }}</span>
              </el-tooltip>
            </div>
            <div
              v-if="record.isStateFlowEnabled() && record.currentModuleState()"
              class="fc-badge text-uppercase inline vertical-middle mL15"
            >
              {{ record.currentModuleState() }}
            </div>
          </div>
        </div>
        <div class="marginL-auto flex-middle">
          <CustomButton
            class="pR10"
            :record="record"
            :moduleName="moduleName"
            :position="POSITION.SUMMARY"
            @refresh="refreshData()"
            @onError="() => {}"
          />
          <template v-if="record.isStateFlowEnabled()">
            <TransitionButtons
              class="mR10"
              :key="`${record.id}transitions`"
              :moduleName="moduleName"
              :record="record"
              :disabled="record.isApprovalEnabled()"
              buttonClass="asset-el-btn"
              @currentState="() => {}"
              @transitionSuccess="refreshData()"
              @transitionFailure="() => {}"
            ></TransitionButtons>
          </template>
          <portal to="pagebuilder-sticky-top" v-if="record.isApprovalEnabled()">
            <ApprovalBar
              :moduleName="moduleName"
              :key="record.id + 'approval-bar'"
              :record="record"
              :hideApprovers="shouldHideApprovers"
              @onSuccess="refreshData()"
              @onFailure="() => {}"
              class="approval-bar-shadow"
            ></ApprovalBar>
          </portal>
          <el-button
            v-if="showEdit"
            type="button"
            class="fc-wo-border-btn pL15 pR15 self-center"
            @click="editRecord"
          >
            <i class="el-icon-edit"></i>
          </el-button>
        </div>
      </div>

      <Page
        :key="record.id"
        :module="moduleName"
        :id="record.id"
        :details="record"
        :primaryFields="primaryFields"
        :notesModuleName="notesModuleName"
        :isV3Api="true"
        :attachmentsModuleName="attachmentsModuleName"
      ></Page>
    </template>
  </div>
</template>
<script>
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import { mapState } from 'vuex'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: CustomModuleSummary,
  props: ['viewname'],
  data() {
    return {
      notesModuleName: 'insurancenotes',
      attachmentsModuleName: 'insuranceattachments',
    }
  },
  created() {
    this.$store.dispatch('loadTicketStatus', this.moduleName)
  },
  computed: {
    ...mapState({
      ticketStatus: state => state.ticketStatus,
    }),
    states() {
      let { ticketStatus, moduleName, $getProperty } = this
      return ticketStatus ? $getProperty(ticketStatus, moduleName) : []
    },
    moduleName() {
      return 'insurance'
    },
    mainFieldKey() {
      return 'companyName'
    },
    moduleState() {
      let currentStateId = this.$getProperty(this.record, 'moduleState.id')
      let currentState = (this.states || []).find(
        state => state.id === currentStateId
      )

      return currentState ? currentState.status : null
    },
  },
  title() {
    'Insurance'
  },
  methods: {
    editRecord() {
      let { id } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.EDIT) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id,
            },
          })
      } else {
        id &&
          this.$router.push({
            name: 'insuranceEdit',
            params: { id },
          })
      }
    },
  },
}
</script>
