<template>
  <div
    class="custom-module-overview overflow-y-scroll"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <div v-if="isLoading" class="text-center width100 pT50 mT50">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else>
      <div class="header pT10 pB15 pL20 pR20">
        <div
          v-if="!$validation.isEmpty(customModuleData)"
          class="custom-module-details"
        >
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
              <div class="custom-module-id mT10">
                <i
                  v-if="$account.portalInfo"
                  class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                  content="back"
                  arrow
                  v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                  @click="back"
                ></i>
                #{{ customModuleData[idFieldKey] }}
              </div>
              <div class="custom-module-name mb5 d-flex">
                {{ $getProperty(customModuleData, mainFieldKey) }}
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

          <el-button
            v-if="hasBookingCreatePermission"
            type="button"
            class="fc-wo-border-btn pL15 pR15 self-center normal-case"
            @click="openBookingForm()"
          >
            {{ $t('tenant.booking.book') }}
          </el-button>
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
import CustomButton from '@/custombutton/CustomButton'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import { findTab, isWebTabsEnabled, tabTypes } from '@facilio/router'

export default {
  extends: Overview,
  components: {
    CustomButton,
  },
  data() {
    return {
      notesModuleName: 'facilitynotes',
      attachmentsModuleName: 'facilityattachments',
      customClass: 'facility-summary',
      POSITION: POSITION_TYPE,
    }
  },
  computed: {
    hasBookingCreatePermission() {
      if (isWebTabsEnabled()) {
        let { tabId } = findTab(tabTypes.MODULE, {
          moduleName: 'facilitybooking',
        })
        return this.$hasPermission(`facilitybooking:CREATE`, tabId) || false
      }
      return false
    },
    idFieldKey() {
      return 'localId'
    },
  },
  methods: {
    openBookingForm() {
      let { id } = this.customModuleData
      let { name } =
        findRouteForModule('facilitybooking', pageTypes.CREATE) || {}
      name &&
        this.$router.push({
          name,
          query: { facility: id },
        })
    },
  },
}
</script>
