<template>
  <!-- OverWiriting For Header -->
  <div
    class="custom-modules-overview overflow-y-scroll budget-summary"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <div
      v-if="!isLoading && customModuleData"
      class="header pT10 pB15 pL20 pR20"
    >
      <div v-if="!$validation.isEmpty(customModuleData)" class="asset-details">
        <div class="d-flex flex-middle">
          <div class="mL5">
            <div class="asset-id mT10">
              <i
                class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                content="back"
                arrow
                v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                @click="routeToList"
              ></i>
              #{{ customModuleData.id }}
            </div>
            <div class="budget-name mb5 mT10">
              {{ customModuleData[mainFieldKey] }}
              <div
                v-if="currentModuleState"
                class="fc-badge text-uppercase inline vertical-middle mL15"
              >
                {{ currentModuleState }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="d-flex flex-direction-row" style="margin-left: auto;">
        <CustomButton
          class="p10"
          :record="customModuleData"
          :key="customModuleData.id + '-custom-button'"
          :moduleName="moduleName"
          :position="POSITION.SUMMARY"
        />
        <iframe
          v-if="downloadUrl"
          :src="downloadUrl"
          style="display: none;"
        ></iframe>
        <template v-if="isStateFlowEnabled">
          <TransitionButtons
            class="mR10"
            :key="customModuleData.id"
            :moduleName="moduleName"
            :record="customModuleData"
            :disabled="isApprovalEnabled"
            buttonClass="asset-el-btn"
            @currentState="() => {}"
            @transitionSuccess="refreshObj(true)"
            @transitionFailure="() => {}"
          ></TransitionButtons>
        </template>

        <portal to="pagebuilder-sticky-top" v-if="isApprovalEnabled">
          <ApprovalBar
            :moduleName="moduleName"
            :key="customModuleData.id + 'approval-bar'"
            :record="customModuleData"
            :hideApprovers="shouldHideApprovers"
            @onSuccess="refreshObj()"
            @onFailure="() => {}"
            class="approval-bar-shadow"
          ></ApprovalBar>
        </portal>
        <el-dropdown
          class="mL10 self-center fc-btn-ico-lg pT3 pB3 pL8 pR8 pointer"
          trigger="click"
          v-if="$hasPermission(`${moduleName}:UPDATE`)"
          @command="action => summaryDropDownAction(action)"
        >
          <span class="el-dropdown-link">
            <inline-svg
              src="svgs/menu"
              class="vertical-middle"
              iconClass="icon icon-md"
            >
            </inline-svg>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item :key="1" v-if="!isRecordLocked" :command="'edit'"
              >Edit</el-dropdown-item
            >
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
      :isV3Api="true"
      :metaFieldTypeMap="metaFieldTypeMap"
    ></page>
    <el-dialog
      :visible.sync="showDeleteDialog"
      class="dialog-d"
      custom-class="setup-dialog45"
      :show-close="false"
    >
      <div class="text-center fc-black-20">
        Do you want to delete or dissociate from
        {{ moduleName ? moduleName : '' }} ?
      </div>
      <span
        slot="footer"
        class="fc-dialog-center-container delete-dialog-footer padding-px18"
      >
        <el-button @click="showDeleteDialog = false">CANCEL</el-button>
        <el-button class="delete-dissociate-buttons" @click="dissociate()"
          >DISSOCIATE</el-button
        >
        <el-button class="delete-dissociate-buttons" @click="deleteRecord()"
          >MOVE TO RECYCLE BIN</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>
<script>
import CustomModuleOverview from 'pages/base-module-v2/Overview'
import { mapState, mapGetters } from 'vuex'
import { API } from '@facilio/api'
import { eventBus } from '@/page/widget/utils/eventBus'
import { isEmpty } from '@facilio/utils/validation'
import CustomButton from '@/custombutton/CustomButton'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { getMetaFieldMapForModules } from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'

export default {
  extends: CustomModuleOverview,
  components: {
    CustomButton,
  },
  data() {
    return {
      notesModuleName: 'budgetnotes',
      attachmentsModuleName: 'budgetattachments',
      primaryFields: ['name'],
      budgetDetails: null,
      associateTermsDialogVisibility: false,
      downloadUrl: null,
      POSITION: POSITION_TYPE,
      metaFieldTypeMap: {},
    }
  },
  computed: {
    moduleName() {
      return 'budget'
    },
    ...mapGetters(['getTicketStatus']),
    mainFieldKey() {
      return 'name'
    },
    customModuleData() {
      return this.budgetDetails
    },
    ...mapState({
      ticketStatus: state => state.ticketStatus.quote,
    }),
    moduleStateId() {
      return this.$getProperty(this, 'customModuleData.moduleState.id')
    },
    canShowRevise() {
      let status = this.getTicketStatus(this.moduleStateId, 'quote')
      if (status && status.status === 'Sent') {
        return true
      }
      return false
    },
  },
  title() {
    'Quote'
  },
  async created() {
    let { moduleName } = this
    this.metaFieldTypeMap = await getMetaFieldMapForModules(moduleName)
  },
  mounted() {
    eventBus.$on('refesh-parent', this.refreshObj)
  },
  beforeDestroy() {
    eventBus.$off('refesh-parent', this.refreshObj)
  },
  methods: {
    refreshObj(refreshList) {
      this.loadCustomModuleData()
      if (refreshList) {
        this.$emit('refreshSummaryList', true)
      }
    },
    async loadCustomModuleData() {
      this.isLoading = true
      let { budget, error } = await API.fetchRecord('budget', {
        id: this.id,
      })
      if (!isEmpty(error)) {
        let { message = 'Error Occured while fetching Budget' } = error
        this.$message.error(message)
      } else {
        this.budgetDetails = budget
      }
      this.isLoading = false
    },
    editCustomModuleData() {
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
        this.$router.push({
          name: 'edit-budget',
          params: { id },
        })
      }
    },
    transformFormData(returnObj, data) {
      returnObj['budget'] = { ...returnObj['budget'], ...data }
      return returnObj
    },
    summaryDropDownAction(action) {
      if (action === 'edit') {
        this.editCustomModuleData(this.customModuleData)
      }
    },
    routeToList() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
          })
      } else {
        this.$router.push({
          path: `/app/ac/budget/all`,
        })
      }
    },
  },
}
</script>
