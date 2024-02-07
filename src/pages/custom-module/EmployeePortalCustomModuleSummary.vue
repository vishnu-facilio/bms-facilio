<template>
  <div class="custom-module-overview sss" :class="customClass">
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else-if="!$validation.isEmpty(record)">
      <div class="summary-header p20">
        <div class="custom-module-details">
          <div class="d-flex flex-middle align-center">
            <div v-if="showPhotoField" class="mR5">
              <div v-if="record[photoFieldName]">
                <img
                  :src="record.getImage(photoFieldName)"
                  class="img-container"
                />
              </div>
              <div v-else-if="showAvatar">
                <avatar size="lg" :user="{ name: record.name }"></avatar>
              </div>
            </div>
            <div class="">
              <!-- <div class="custom-module-id">
                <i
                  v-if="$account.portalInfo"
                  class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                  content="back"
                  arrow
                  v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                  @click="back"
                ></i>
                #{{ record && record.id }}
              </div> -->
              <div class="emp-header-bredcom-header" @click="back">
                <span v-if="currentGroup">{{ currentGroup.displayName }} </span>

                <span v-if="currentView"> / {{ currentView.displayName }}</span>
              </div>
              <div class="custom-module-name ">
                <div class="d-flex max-width300px">
                  <span class="whitespace-pre-wrap">{{
                    record[mainFieldKey]
                  }}</span>
                  <span
                    v-if="
                      record.isStateFlowEnabled() && record.currentModuleState()
                    "
                    class="fc-badge text-uppercase inline vertical-middle mL15"
                  >
                    {{ record.currentModuleState() }}
                  </span>
                </div>

                <!-- <div
                  v-if="
                    record.isStateFlowEnabled() && record.currentModuleState()
                  "
                  class="fc-badge text-uppercase inline vertical-middle mL15"
                >
                  {{ record.currentModuleState() }}
                </div> -->
              </div>
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
          <!-- <el-button
              v-if="showEdit"
              type="button"
              class="fc-wo-border-btn pL15 pR15 self-center"
              @click="editRecord"
            >
              <i class="el-icon-edit"></i>
            </el-button> -->
          <div>
            <el-dropdown
              v-if="showEdit"
              class="pointer  fc-summary-more-icon"
              @command="actions"
              trigger="click"
            >
              <span class="el-dropdown-link">
                <i class="el-icon-more rotate-90 pointer"></i>
              </span>
              <el-dropdown-menu slot="dropdown" class="dashboard-subheader-dp">
                <el-dropdown-item command="edit" v-if="showEdit">
                  <div>
                    {{ $t('common._common.edit') }}
                  </div>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
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
import Page from 'src/pages/spacebooking/SpaceBookingPageBuilder.vue'
import ModuleSummary from 'src/pages/custom-module/CustomModuleSummary'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
export default {
  extends: ModuleSummary,
  components: { Page },
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
<style>
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
