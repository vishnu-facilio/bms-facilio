<template>
  <!-- OverWiriting For Header -->
  <div
    class="custom-modules-overview overflow-y-scroll"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <div
      v-if="!isLoading && customModuleData"
      class="header pT10 pB15 pL20 pR20"
    >
      <div v-if="!$validation.isEmpty(customModuleData)" class="asset-details">
        <div class="d-flex flex-middle">
          <div v-if="showPhotoField">
            <div v-if="customModuleData[photoFieldName]">
              <img
                :src="getImage(customModuleData[photoFieldName])"
                class="img-container"
              />
            </div>
            <div v-else-if="showAvatar">
              <avatar
                size="lg"
                :user="{ name: customModuleData.name }"
              ></avatar>
            </div>
          </div>
          <div class="mL5">
            <div class="asset-id mT10">
              <i
                class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                content="back"
                arrow
                v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                @click="back"
                v-if="$account.portalInfo"
              ></i>
              #{{ customModuleData.localId }}
            </div>
            <div class="asset-name mb5 mT10">
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
          :moduleName="moduleName"
          :position="POSITION.SUMMARY"
          @refresh="refreshObj(true)"
          @onError="() => {}"
        />
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
            <el-dropdown-item
              :key="2"
              v-if="$hasPermission('facilitybooking:CREATE')"
              :command="'book'"
              >{{ $t('tenant.booking.book') }}</el-dropdown-item
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
export default {
  extends: CustomModuleOverview,
  components: {
    CustomButton,
  },
  data() {
    return {
      notesModuleName: 'facilitynotes',
      attachmentsModuleName: 'facilityattachments',
      primaryFields: ['name'],
      facilityDetails: null,
      POSITION: POSITION_TYPE,
      customClass: 'facility-summary',
    }
  },
  computed: {
    moduleName() {
      return 'facility'
    },
    ...mapGetters(['getTicketStatus']),
    customModuleData() {
      return this.facilityDetails
    },
    ...mapState({
      ticketStatus: state => state.ticketStatus.facility,
    }),
    moduleStateId() {
      return this.$getProperty(this, 'customModuleData.moduleState.id')
    },
  },
  title() {
    'Facility'
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
      let { facility, error } = await API.fetchRecord(
        this.moduleName,
        {
          id: this.id,
        },
        { force: true } // temp call only for update
      )
      if (!isEmpty(error)) {
        let { message = 'Error Occured while fetching Facility' } = error
        this.$message.error(message)
      } else {
        this.facilityDetails = facility
      }
      this.isLoading = false
    },
    editCustomModuleData() {
      let { id } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.EDIT) || {}

        if (name) {
          id &&
            this.$router.push({
              name,
              param: { id },
            })
        }
      } else {
        id && this.$router.push({ name: 'edit-facility', params: { id } })
      }
    },
    transformFormData(returnObj, data) {
      returnObj['facility'] = { ...returnObj['facility'], ...data }
      return returnObj
    },
    summaryDropDownAction(action) {
      if (action === 'edit') {
        this.editCustomModuleData()
      } else if (action === 'book') {
        this.openBookingForm()
      }
    },
    openBookingForm() {
      let { id } = this.customModuleData
      let routeName
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule('facilitybooking', pageTypes.CREATE) || {}
        routeName = name
      } else {
        routeName = 'new-booking'
      }
      routeName &&
        this.$router.push({
          name: routeName,
          query: { facility: id },
        })
    },
  },
}
</script>
