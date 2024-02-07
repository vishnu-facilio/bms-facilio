<template>
  <div class="custom-module-overview" :class="customClass">
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else-if="!$validation.isEmpty(record)">
      <div class="header pT20 pL20 pR20">
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
            <div>
              <div class="custom-module-id">
                <i
                  v-if="$account.portalInfo"
                  class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                  content="back"
                  arrow
                  v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                  @click="back"
                ></i>
                #{{ record && record.id }}
              </div>
              <div class="mT5 custom-module-name">
                <div class="d-flex max-width300px">
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
                  v-if="
                    record.isStateFlowEnabled() && record.currentModuleState()
                  "
                  class="fc-badge text-uppercase inline vertical-middle mL15"
                >
                  {{ record.currentModuleState() }}
                </div>
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

          <el-dropdown
            v-if="showDropdown"
            trigger="click"
            class="fc-btn-ico-lg mL15 fc-btn-ico-lg pT5 pB5 pL8 pR8 pointer height40 mT4"
          >
            <div class="flex-middle">
              <i class="el-icon-more pointer overview-icon-more-btn"></i>
            </div>

            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>
                <div v-if="hasUpdatePermission" @click="toggleShowNewShift">
                  {{ $t('common._common.edit') }}
                </div>
              </el-dropdown-item>
              <el-dropdown-item>
                <div
                  v-if="deleteableShift(record)"
                  @click="deleteShift(record.id)"
                >
                  {{ $t('common._common.delete') }}
                </div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>

      <NewShift
        v-if="showNewShift"
        :visibility.sync="showNewShift"
        :id="record.id"
        :viewName="viewname"
        @saved="toggleNewShiftWindowAndReload"
      ></NewShift>

      <Page
        :key="record.id"
        :module="moduleName"
        :id="record.id"
        :details="record"
        :primaryFields="primaryFields"
        :isV3Api="true"
      ></Page>
    </template>
  </div>
</template>
<script>
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import NewShift from 'src/pages/peopleV2/shift/NewShift.vue'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { mapGetters, mapState } from 'vuex'
export default {
  name: 'ShiftSummary',
  extends: CustomModuleSummary,
  components: {
    NewShift,
  },
  props: ['viewname'],
  data() {
    return {
      showNewShift: false,
    }
  },
  computed: {
    ...mapState({
      currentTab: state => state.webtabs.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),
    showDropdown() {
      return this.hasUpdatePermission || this.hasDeletePermission
    },
    hasUpdatePermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('UPDATE', currentTab)
    },
    hasDeletePermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('DELETE', currentTab)
    },
    title() {
      return 'Shift'
    },
    moduleName() {
      return 'shift'
    },
  },
  methods: {
    deleteableShift(shift) {
      if (shift.defaultShift) {
        return false
      }
      return this.hasDeletePermission
    },
    toggleNewShiftWindowAndReload() {
      this.showNewShift = false
      this.$root.$emit('reload-shift-list')
    },
    toggleShowNewShift() {
      this.showNewShift = !this.showNewShift
    },
    async deleteShift(shiftID) {
      if (isEmpty(shiftID)) {
        return
      }
      let { error } = await API.deleteRecord(this.moduleName, [shiftID])
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success(
          `${'Shift'} ${this.$t('custommodules.list.delete_success')}`
        )
      }
    },
  },
}
</script>
