<template>
  <div
    class="custom-modules-overview overflow-y-scroll"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <div v-if="isLoading" class="text-center width100 pT50 mT50">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else>
      <div class="header pT10 pB15 pL20 pR20">
        <div
          v-if="!$validation.isEmpty(customModuleData)"
          class="asset-details"
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
              <div class="asset-id mT10">
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
              <div class="asset-name mb5 d-flex">
                {{
                  customModuleData[mainFieldKey].name ||
                    $getProperty(customModuleData, mainFieldKey)
                }}
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
            @refresh="refreshObj()"
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
              @transitionSuccess="refreshObj()"
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

          <el-button
            type="button"
            v-if="showEdit"
            class="fc-wo-border-btn pL15 pR15 self-center"
            @click="editCustomModuleData(customModuleData)"
          >
            <i class="el-icon-edit" title="Edit" v-tippy></i>
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
import CustomModuleOverview from 'pages/base-module-v2/Overview'
import { API } from '@facilio/api'
import { mapState } from 'vuex'

export default {
  extends: CustomModuleOverview,
  name: 'Overview',
  created() {
    this.$store.dispatch('loadTicketStatus', this.moduleName)
    this.loadMeta()
  },
  data() {
    return {
      metaInfo: null,
      record: null,
      primaryFields: [
        'approvalFlowId',
        'approvalStatus',
        'stateFlowId',
        'scheduledTime',
      ],
    }
  },
  computed: {
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
    }),
    idFieldKey() {
      return 'id'
    },
    showEdit() {
      return false
    },
    shouldHideApprovers() {
      return true
    },
    customModuleData() {
      return this.record
    },
    isV3Api() {
      return true
    },
    mainFieldKey() {
      return 'employee'
    },
    moduleName() {
      return 'moves'
    },
  },
  methods: {
    async loadCustomModuleData() {
      this.isLoading = true
      let { id, moduleName } = this
      let { error, ...rest } = await API.fetchRecord(moduleName, {
        id: id,
      })

      if (error) {
        this.$message.error('Could not load details')
      } else {
        this.record = rest[moduleName]
        this.getFormMeta()
      }
      this.isLoading = false
    },
    loadMeta() {
      API.get('/module/meta', {
        moduleName: this.moduleName,
      }).then(({ data, error }) => {
        if (!error && data) {
          this.metaInfo = data.meta
        }
      })
    },
    transitionFilter(state) {
      return this.canShowState(state)
    },
    canShowState(state) {
      let { user } = this.$account
      let { appType } = user || {}
      if (appType) {
        if (appType === 2) {
          return Boolean(state.showInTenantPortal)
        } else if (appType === 3) {
          return Boolean(state.showInVendorPortal)
        }
        return true
      }
      return true
    },
    editCustomModuleData() {
      // TODO
    },
  },
}
</script>
