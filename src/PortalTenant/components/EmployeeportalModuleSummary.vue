<template>
  <div class="custom-module-overview overflow-y-scroll" :class="customClass">
    <div v-if="isLoading" class="text-center width100 pT50 mT50">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else>
      <div class="summary-header pT10 pB15 pL20 pR20">
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
            <div>
              <!-- <div class="custom-module-id mT10">
                <i
                  v-if="$account.portalInfo"
                  class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                  content="back"
                  arrow
                  v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                  @click="back"
                ></i>
                #{{ customModuleData[idFieldKey] }}
              </div> -->
              <div class="emp-header-bredcom-header" @click="back">
                <span v-if="currentGroup">{{ currentGroup.displayName }} </span>

                <span v-if="currentView"> / {{ currentView.displayName }}</span>
              </div>
              <div class="custom-module-name mb5 d-flex">
                {{ $getProperty(customModuleData, mainFieldKey) }}
                <SummaryHeaderTag
                  v-if="isStateFlowEnabled && currentModuleState"
                  :label="currentModuleState"
                  :size="'small'"
                >
                </SummaryHeaderTag>
              </div>
            </div>
          </div>
        </div>
        <div class="flex-middle flex-direction-row" style="margin-left: auto;">
          <CustomButton
            class="emp-custom-btn"
            :record="customModuleData"
            :moduleName="moduleName"
            :position="POSITION.SUMMARY"
            @refresh="refreshData()"
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
            class="pointer fc-summary-more-icon"
            trigger="click"
            @command="dropDownActions"
          >
            <span class="el-dropdown-link">
              <i class="el-icon-more rotate-90 pointer"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item v-if="canShowEdit" :command="'edit'">{{
                $t('common._common.edit')
              }}</el-dropdown-item>
              <el-dropdown-item v-if="canShowDelete" :command="'delete'">{{
                $t('common._common.delete')
              }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
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
import Page from 'src/pages/spacebooking/SpaceBookingPageBuilder.vue'
import ModuleSummary from 'src/PortalTenant/components/ModuleSummary.vue'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import SummaryHeaderTag from 'src/PortalTenant/components/SummaryHeaderTag.vue'

export default {
  extends: ModuleSummary,
  components: { Page, SummaryHeaderTag },
  mounted() {
    this.init()
  },
  computed: {
    ...mapState({
      groupViews: state => {
        return !isEmpty(state.view.groupViews) ? state.view.groupViews : []
      },
    }),
    currentViewRouter() {
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      } else {
        return null
      }
    },
    currentView() {
      let { groupViews, currentViewRouter } = this
      let selectedViews = null

      groupViews.forEach(group => {
        let { views } = group
        let selectedView = views.find(v => v.name === currentViewRouter)
        if (selectedView) {
          selectedViews = selectedView
        }
      })

      return selectedViews || null
    },
    currentViews() {
      let { groupViews, currentViewRouter } = this
      let selectedViews = []

      groupViews.forEach(group => {
        let { views } = group
        let selectedView = views.find(v => v.name === currentViewRouter)
        if (selectedView) {
          selectedViews = views
        }
      })

      return selectedViews.map(view => ({
        label: view.displayName,
        id: view.id,
        name: view.name,
        isCustom: !view.isDefault,
        primary: view.primary,
      }))
    },
    currentGroup() {
      let { groupViews } = this

      let selectedGroup = groupViews.find(group => {
        let { views } = group
        let selectedView = views.find(v => v.name === this.currentViewRouter)

        return !isEmpty(selectedView)
      })

      return selectedGroup
    },
  },
  methods: {
    async init() {
      await this.$store.dispatch('view/clearViews')
      await this.loadViews()
      await this.loadViewDetails()
    },
    loadViews() {
      return this.$store
        .dispatch('view/loadGroupViews', {
          moduleName: this.moduleName,
        })
        .then(() => {
          this.initializeViews()
        })
    },

    loadViewDetails() {
      let { currentView, moduleName, currentGroup } = this
      let viewObj =
        this.$getProperty(currentGroup, 'views', []).find(
          v => v.name === currentView
        ) || {}
      let viewModuleName = this.$getProperty(viewObj, 'moduleName', null)

      if (isEmpty(currentView)) return
      if (isEmpty(moduleName) && isEmpty(viewModuleName)) return

      let param = {
        viewName: currentView,
        moduleName: isEmpty(viewModuleName) ? moduleName : viewModuleName,
      }
      this.$store.dispatch('view/loadViewDetail', param).catch(() => {})
    },

    actions(name) {
      if (name === 'edit') {
        this.editRecord()
      }
    },
  },
}
</script>
<style scoped>
.custom-module-overview {
  background-color: #fff;
  height: 100%;
}
</style>
<style>
.emp-custom-btn .el-button {
  height: 31px;
  padding: 2px 8px 0;
}
.employee-portal-homepage .summary-header {
  background: #fff;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  border-bottom: none;
  padding-top: 7px;
  padding-bottom: 0;
}
.fc-summary-more-icon {
  color: #000;
  font-size: 14px;
  margin-left: 10px;
  padding: 6px 6px 6px;
}
.fc-summary-more-icon.el-dropdown:hover {
  background: rgba(202, 212, 216, 0.3);
  -webkit-transition: 0.2s all;
  border-radius: 5px;
  transition: 0.2s all;
}
.emp-header-bredcom-header {
  font-size: 12px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.26px;
  color: #a0acc0;
  padding: 5px;
  cursor: pointer;
  padding-left: 0;
}
.emp-header-bredcom-header:hover {
  border-radius: 5px;
  transition: 0.2s all;
  color: gray;
}
</style>
