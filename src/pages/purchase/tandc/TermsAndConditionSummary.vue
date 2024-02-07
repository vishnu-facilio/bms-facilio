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
              #{{ customModuleData.id }}
            </div>
            <div class="asset-name mb5 mT10">
              <!-- lock icon -->
              <i
                v-if="isRecordLocked"
                class="fa fa-lock locked-wo mT0"
                data-arrow="true"
                :title="$t('common._common.locked_state')"
                v-tippy
              ></i>
              {{ customModuleData[mainFieldKey] }}
              <div class="fc-badge text-uppercase inline vertical-middle mL10">
                {{ getPublishedStatus() }}
              </div>
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
        <div class="mL10 self-center fc-btn-ico-lg pT3 pB3 pL8 pR8 pointer">
          <i
            class="el-icon-s-order pointer edit-icon-color"
            data-arrow="true"
            :title="$t('common.products.long_description')"
            v-tippy
            @click="longDescVisibility = true"
          ></i>
        </div>
        <el-dropdown
          class="mL10 self-center fc-btn-ico-lg pT3 pB3 pL8 pR8 pointer"
          trigger="click"
          v-if="canShowOptions"
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
            <template v-if="!isRecordLocked && canShowPublish">
              <el-dropdown-item command="edit">{{
                $t('common._common.edit')
              }}</el-dropdown-item>
              <el-dropdown-item command="delete">{{
                $t('common._common.delete')
              }}</el-dropdown-item>
              <el-dropdown-item command="publish">{{
                $t('common.wo_report.publish')
              }}</el-dropdown-item>
            </template>
            <el-dropdown-item v-if="canShowRevise" command="revise">{{
              $t('common.products.revise')
            }}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <LongDescriptionEditor
      v-if="longDescVisibility"
      :content="termsandconditionsDetails.longDesc"
      :disabled="!canShowEdit"
      @onSave="updateLongDescriptionData"
      @onClose="longDescVisibility = false"
    ></LongDescriptionEditor>
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
    <terms-and-condition-form
      :visibility.sync="termsFormVisibility"
      :tAndCData="termsandconditionsDetails"
      :editId="id"
      :isRevised="isRevised"
      v-if="termsFormVisibility"
      @saved="refreshObj(true)"
    ></terms-and-condition-form>
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
import TermsAndConditionForm from './NewTermsAndCondition'
import LongDescriptionEditor from 'src/pages/purchase/tandc/RichTextAreaEditor.vue'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: CustomModuleOverview,
  components: {
    CustomButton,
    TermsAndConditionForm,
    LongDescriptionEditor,
  },
  props: ['viewname', 'field', 'moduleData'],
  data() {
    return {
      notesModuleName: 'termsnotes',
      attachmentsModuleName: 'termsattachments',
      primaryFields: ['localId', 'longDesc'],
      termsandconditionsDetails: null,
      POSITION: POSITION_TYPE,
      termsFormVisibility: false,
      isRevised: false,
      longDescVisibility: false,
    }
  },
  computed: {
    ...mapGetters(['getTicketStatus', 'isStatusLocked']),
    customModuleData() {
      return this.termsandconditionsDetails
    },
    ...mapState({
      ticketStatus: state => state.ticketStatus.termsandconditions,
      metaInfo: state => state.view.metaInfo,
    }),
    moduleStateId() {
      return this.$getProperty(this, 'customModuleData.moduleState.id')
    },
    canShowPublish() {
      let { isPublished } = this.termsandconditionsDetails || {}
      return !isPublished
    },
    canShowRevise() {
      let { moduleName, canShowPublish } = this
      let { isRevised } = this.termsandconditionsDetails || {}
      return (
        this.$hasPermission(`${moduleName}:CREATE`) &&
        !canShowPublish &&
        !isRevised
      )
    },
    canShowOptions() {
      let { moduleName, canShowPublish, canShowRevise, isRecordLocked } = this
      let canShowOptions = (!isRecordLocked && canShowPublish) || canShowRevise
      return this.$hasPermission(`${moduleName}:UPDATE`) && canShowOptions
    },
    isRecordLocked() {
      let { moduleName } = this
      let { moduleState } = this.customModuleData || {}

      if (this.isStateFlowEnabled) {
        let hasState = this.$getProperty(moduleState, 'id')
        return hasState && this.isStatusLocked(hasState, moduleName)
      }
      return false
    },
    isStateFlowEnabled() {
      return this.$getProperty(this, 'customModuleData.moduleState.id')
    },
    canShowEdit() {
      let { isRecordLocked, canShowPublish, $hasPermission, moduleName } = this

      return (
        $hasPermission(`${moduleName}:UPDATE`) &&
        !isRecordLocked &&
        canShowPublish
      )
    },
  },
  title() {
    'Terms And Conditions'
  },
  mounted() {
    eventBus.$on('refesh-parent', this.refreshObj)
  },
  beforeDestroy() {
    eventBus.$off('refesh-parent', this.refreshObj)
  },

  methods: {
    async updateLongDescriptionData(longDesc) {
      let { moduleName, customModuleData } = this
      let { id } = customModuleData
      let params = {
        id,
        data: { longDesc },
      }
      let { error } = await API.updateRecord(moduleName, params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success(
          this.$t('common.products.long_description_edited_successfully')
        )
        this.longDescVisibility = false
        this.loadCustomModuleData()
      }
    },
    getPublishedStatus() {
      let { published } = this.termsandconditionsDetails
      return published ? 'published' : 'not published'
    },
    refreshObj(refreshList) {
      this.loadCustomModuleData()
      if (refreshList || this.canShowRevise) {
        this.$emit('refreshSummaryList', true)
      }
    },
    async loadCustomModuleData(force = false) {
      let config = force ? { force } : {}
      this.isLoading = true
      let { termsandconditions, error } = await API.fetchRecord(
        this.moduleName,
        {
          id: this.id,
        },
        config
      )
      if (!isEmpty(error)) {
        let {
          message = 'Error Occured while fetching Terms and Condition',
        } = error
        this.$message.error(message)
      } else {
        this.termsandconditionsDetails = termsandconditions
      }
      this.isLoading = false
    },
    async delete() {
      let value = await this.$dialog.confirm({
        title: this.$t('common.header.delete_terms_and_conditions'),
        message: this.$t(
          'common._common.are_you_want_delete_terms_and_conditions'
        ),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })
      if (value) {
        let { id } = this
        let { error } = await API.deleteRecord('termsandconditions', id)
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$message.success(this.$t('common._common.delete_success'))
          if (isWebTabsEnabled()) {
            let { name } =
              findRouteForModule(this.getCurrentModule, pageTypes.LIST) || {}
            name &&
              this.$router.push({
                name,
                params: {
                  viewname: this.viewname,
                },
                query: this.$route.query,
              })
          } else {
            this.$router.push({
              path: `/app/purchase/tandc/${this.viewname}`,
            })
          }
        }
      }
    },
    summaryDropDownAction(action) {
      if (action === 'edit') {
        this.termsFormVisibility = true
      } else if (action === 'delete') {
        this.delete()
      } else if (action === 'revise') {
        this.revise()
      } else if (action === 'publish') {
        this.publish()
      }
    },
    async publish() {
      let { id, moduleName } = this
      let { error } = await API.updateRecord(moduleName, {
        id,
        data: {},
        params: { publish: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.$message.success('Published Successfully')
        await this.loadCustomModuleData(true)
      }
    },
    revise() {
      this.isRevised = true
      this.termsFormVisibility = true
    },
  },
}
</script>
