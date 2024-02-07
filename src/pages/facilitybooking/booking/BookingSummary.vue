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
            <div class="mb5 mT10">
              <i
                class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                content="back"
                arrow
                v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                @click="back"
                v-if="$account.portalInfo"
              ></i>
              <span class="id-txt">{{ `#${customModuleData.localId}` }}</span>
              <div class="mb5 mT5 d-flex flex-row align-center">
                <div class="mainfield-name">
                  {{ $getProperty(customModuleData, 'facility.name', '---') }}
                </div>
                <div class="mL5" @click="openFacilitySummary()">
                  <InlineSvg
                    iconClass="icon icon-xs pointer"
                    class="vertical-middle"
                    src="svgs/new-tab"
                  ></InlineSvg>
                </div>
                <div
                  v-if="currentModuleState"
                  class="fc-badge text-uppercase mL10"
                >
                  {{ currentModuleState }}
                </div>
              </div>
            </div>
            <div class="mT10 sub-text">
              Booked by
              <span class="txt-blue">{{
                $getProperty(customModuleData, 'reservedFor.name', '---')
              }}</span
              ><span class="separator self-center mL10 mR10">|</span>Booked On
              {{
                $options.filters.formatDate(
                  customModuleData.sysCreatedTime,
                  true
                )
              }}
            </div>
          </div>
        </div>
      </div>
      <div class="d-flex flex-direction-row" style="margin-left: auto;">
        <CustomButton
          class="p10 d-flex"
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
          v-if="customModuleData.canShowCancel"
          class="mL10 self-center fc-btn-ico-lg pT3 pB3 pL8 pR8 pointer"
          trigger="click"
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
            <el-dropdown-item
              :key="1"
              v-if="customModuleData.canShowCancel"
              :command="'cancel'"
              >Cancel Booking</el-dropdown-item
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
      :skipMargins="false"
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
      notesModuleName: 'facilitybookingnotes',
      attachmentsModuleName: 'facilitybookingattachments',
      primaryFields: ['name'],
      facilityBookingDetails: null,
      POSITION: POSITION_TYPE,
      customClass: 'booking-summary',
    }
  },
  computed: {
    moduleName() {
      return 'facilitybooking'
    },
    ...mapGetters(['getTicketStatus']),
    customModuleData() {
      return this.facilityBookingDetails
    },
    ...mapState({
      ticketStatus: state => state.ticketStatus.facilitybooking,
    }),
    moduleStateId() {
      return this.$getProperty(this, 'customModuleData.moduleState.id')
    },
  },
  title() {
    'Booking'
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
      let { facilitybooking, error } = await API.fetchRecord(
        this.moduleName,
        {
          id: this.id,
        },
        { force: true } // temp call only for update
      )
      if (!isEmpty(error)) {
        let { message = 'Error Occured while fetching Booking' } = error
        this.$message.error(message)
      } else {
        this.facilityBookingDetails = facilitybooking
      }
      this.isLoading = false
    },
    editCustomModuleData() {
      let { id } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.EDIT) || {}

        if (id && name) {
          this.$router.push({
            name,
          })
        }
      } else if (id) {
        this.$router.push({ name: 'edit-booking', params: { id } })
      }
    },
    transformFormData(returnObj, data) {
      returnObj['facilitybooking'] = {
        ...returnObj['facilitybooking'],
        ...data,
      }
      return returnObj
    },
    summaryDropDownAction(action) {
      if (action === 'cancel') {
        this.cancelBooking()
      }
    },
    openFacilitySummary() {
      let facilityId = this.$getProperty(this.customModuleData, 'facility.id')
      let params = { viewName: this.currentView, id: facilityId }
      let name
      if (isWebTabsEnabled()) {
        name = findRouteForModule('facility', pageTypes.OVERVIEW) || {}
      }
      let { href } =
        this.$router.resolve({ name: name || 'facilitySummary', params }) || {}
      href && window.open(href, '_blank')
    },
    async cancelBooking() {
      let {
        moduleName,
        customModuleData: { id },
      } = this
      let { error } = await API.updateRecord(moduleName, {
        id,
        data: { id },
        params: { cancelBooking: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occurred while cancelling')
      } else {
        this.refreshObj()
      }
    },
  },
}
</script>
