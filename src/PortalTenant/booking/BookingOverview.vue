<template>
  <div
    class="custom-module-overview overflow-y-scroll"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <div v-if="isLoading" class="text-center width100 pT50 mT50">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else-if="!$validation.isEmpty(customModuleData)">
      <div class="header pT10 pB15 pL20 pR20">
        <div class="asset-details">
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
              <div class="mT10">
                <i
                  v-if="$account.portalInfo"
                  class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                  content="back"
                  arrow
                  v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                  @click="back"
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
            </div>
          </div>
        </div>
        <div class="d-flex flex-direction-row" style="margin-left: auto;">
          <CustomButton
            class="p10 d-flex"
            :record="customModuleData"
            :moduleName="moduleName"
            :position="POSITION.SUMMARY"
            @refresh="refreshData(true)"
            @onError="() => {}"
          />
          <template v-if="isStateFlowEnabled">
            <TransitionButtons
              :key="customModuleData.id"
              :moduleName="moduleName"
              :record="customModuleData"
              :disabled="isApprovalEnabled"
              :transitionFilter="transitionFilter"
              buttonClass="asset-el-btn"
              @currentState="() => {}"
              @transitionSuccess="refreshData()"
              @transitionFailure="() => {}"
              class="mR10"
            ></TransitionButtons>
          </template>

          <portal to="pagebuilder-sticky-top" v-if="isApprovalEnabled">
            <ApprovalBar
              :moduleName="moduleName"
              :key="customModuleData.id + 'approval-bar'"
              :record="customModuleData"
              :hideApprovers="shouldHideApprovers"
              @onSuccess="refreshData()"
              @onFailure="() => {}"
              class="approval-bar-shadow"
            ></ApprovalBar>
          </portal>

          <el-dropdown
            v-if="canShowActions"
            class="mL10 self-center fc-btn-ico-lg pT3 pB3 pL8 pR8 pointer"
            trigger="click"
            @command="summaryDropDownAction"
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
              <el-dropdown-item v-if="canShowEdit" :command="'edit'">{{
                $t('common._common.edit')
              }}</el-dropdown-item>
              <el-dropdown-item v-if="canShowDelete" :command="'delete'">{{
                $t('common._common.delete')
              }}</el-dropdown-item>
              <el-dropdown-item
                v-if="customModuleData.canShowCancel"
                :command="'cancel'"
                >Cancel Booking</el-dropdown-item
              >
            </el-dropdown-menu>
          </el-dropdown>

          <!-- <el-dropdown
            v-if="!customModuleData.isCancelled"
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
                v-if="!customModuleData.isCancelled"
                :command="'cancel'"
                >Cancel Booking</el-dropdown-item
              >
            </el-dropdown-menu>
          </el-dropdown> -->
        </div>
      </div>
      <page
        v-if="customModuleData && customModuleData.id"
        :key="customModuleData.id"
        :module="moduleName"
        :id="customModuleData.id"
        :details="customModuleData"
        :primaryFields="primaryFields"
        :notesModuleName="notesModuleName"
        :attachmentsModuleName="attachmentsModuleName"
        :isV3Api="isV3Api"
      ></page>
    </template>
  </div>
</template>
<script>
import Overview from '../components/ModuleSummary'
import { findRouteForModule, pageTypes } from '@facilio/router'
import { API } from '@facilio/api'
import CustomButton from '@/custombutton/CustomButton'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'

export default {
  extends: Overview,
  components: {
    CustomButton,
  },
  data() {
    return {
      notesModuleName: 'facilitybookingnotes',
      attachmentsModuleName: 'facilitybookingattachments',
      customClass: 'booking-summary',
      POSITION: POSITION_TYPE,
    }
  },
  computed: {
    moduleName() {
      return this.$attrs.moduleName
    },
    mainFieldKey() {
      return 'facility.name'
    },
    idFieldKey() {
      return 'localId'
    },
  },
  methods: {
    openFacilitySummary() {
      let facilityId = this.$getProperty(this.customModuleData, 'facility.id')
      let { name } = findRouteForModule('facility', pageTypes.OVERVIEW) || {}
      let { href } =
        this.$router.resolve({
          name,
          params: {
            id: facilityId,
          },
        }) || {}
      href && window.open(href, '_blank')
    },
    summaryDropDownAction(action) {
      if (action === 'cancel') {
        this.cancelBooking()
      } else if (action === 'edit') {
        this.editCustomModuleData(this.record)
      } else if (action === 'delete') {
        this.deleteRecord(this.record)
      }
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
        this.refreshData()
      }
    },
  },
}
</script>
