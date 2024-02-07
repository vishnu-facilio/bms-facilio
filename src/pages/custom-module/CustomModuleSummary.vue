<template>
  <div class="custom-module-overview" :class="customClass">
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else-if="!$validation.isEmpty(record)">
      <div class="header p20">
        <div class="custom-module-details">
          <div class="d-flex flex-middle align-center">
            <div v-if="showPhotoField" class="mR5">
              <div v-if="record[photoFieldName]">
                <img
                  v-if="$helpers.isLicenseEnabled('THROW_403_WEBTAB')"
                  :src="record.getImageUrl('photoUrl')"
                  class="img-container"
                />
                <img
                  v-else
                  :src="record.getImage(photoFieldName)"
                  class="img-container"
                />
              </div>
              <div v-else-if="showAvatar">
                <avatar size="lg" :user="{ name: record.name }"></avatar>
              </div>
            </div>
            <div class="mL5">
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
              <div class="custom-module-name ">
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
          <el-button
            v-if="showEdit"
            type="button"
            class="fc-wo-border-btn pL15 pR15 self-center"
            @click="editRecord"
          >
            <i class="el-icon-edit"></i>
          </el-button>
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
import Page from '@/page/PageBuilder'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import CustomButton from '@/custombutton/CustomButton'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import TransitionButtons from '@/stateflow/TransitionButtons'
import ApprovalBar from '@/approval/ApprovalBar'
import Constants from 'util/constant'
import { CustomModuleData } from './CustomModuleData'
import { eventBus } from '@/page/widget/utils/eventBus'

export default {
  name: 'CustomModuleSummary',
  components: { Page, CustomButton, TransitionButtons, ApprovalBar },
  data() {
    return {
      isLoading: false,
      record: null,
      primaryFields: [],
      notesModuleName: 'cmdnotes',
      attachmentsModuleName: 'cmdattachments',
      formFields: null,
      POSITION: POSITION_TYPE,
      customClass: '',
    }
  },
  created() {
    this.$store.dispatch('loadApprovalStatus')
  },
  mounted() {
    eventBus.$on('refresh-overview', this.refreshData)
  },
  beforeDestroy() {
    eventBus.$off('refresh-overview', this.refreshData)
  },
  computed: {
    moduleName() {
      return this.$route.params.moduleName
        ? this.$route.params.moduleName
        : this.$attrs.moduleName || ''
    },
    id() {
      let paramId = this.$attrs.id || this.$route.params.id
      return paramId && paramId !== 'null'
        ? parseInt(this.$route.params.id)
        : ''
    },
    showEdit() {
      let { $org, $hasPermission, record } = this
      let { isCustomModulePermissionsEnabled: orgIdsToCheck } = Constants
      let canShowEdit =
        orgIdsToCheck($org.id) || isWebTabsEnabled()
          ? $hasPermission(`${this.moduleName}:UPDATE`)
          : true

      return canShowEdit && record.canEdit()
    },
    mainFieldKey() {
      return 'name'
    },
    photoFieldName() {
      return 'photoId'
    },
    showPhotoField() {
      let { formFields } = this
      if (formFields) {
        let photoField = formFields.find(field => field.name === 'photo')
        if (photoField) {
          return !photoField.hideField
        } else {
          return false
        }
      }
      return false
    },
    showAvatar() {
      return false
    },
    shouldHideApprovers() {
      return false
    },
    modelDataClass() {
      return CustomModuleData
    },
  },
  watch: {
    id: {
      handler() {
        this.loadRecord(true)
      },
      immediate: true,
    },
    moduleName() {
      this.loadRecord(true)
    },
  },
  methods: {
    refreshData() {
      this.loadRecord(true)
    },
    async loadRecord(force = false) {
      try {
        let { moduleName, id } = this
        this.isLoading = true
        this.record = await this.modelDataClass.fetch({ moduleName, id, force })
        await this.getFormMeta()
      } catch (error) {
        this.$message.error(
          error?.message ||
            this.$t('custommodules.summary.record_summary_error')
        )
      }

      this.isLoading = false
    },
    editRecord() {
      let { moduleName, id } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        name && this.$router.push({ name, params: { id } })
      } else {
        let creationName = 'custommodules-edit'
        this.$router.push({ name: creationName, params: { moduleName, id } })
      }
    },
    async getFormMeta() {
      let { record: { formId } = {}, moduleName } = this
      let url = `/v2/forms/${moduleName}`

      let { data, error } = await API.get(url, {
        formId: formId || -1,
      })
      if (!error) {
        let formFields = (data.form || {}).fields
        this.formFields = formFields
      }
    },
    back() {
      this.$router.go(-1)
    },
  },
}
</script>

<style lang="scss" scoped>
.custom-module-overview {
  .header {
    background: #fff;
    display: flex;
  }
  .custom-module-details {
    flex-grow: 1;
    text-align: left;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
  .custom-module-details .custom-module-id {
    font-size: 12px;
    color: #39b2c2;
  }
  .custom-module-details .custom-module-name {
    font-size: 16px;
    color: #324056;
    font-weight: 500;
    display: flex;
    align-items: center;
    .custom-header {
      display: -webkit-box;
      max-width: 300px;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }
  .fc-badge {
    color: #fff;
    background-color: #23b096;
    padding: 3px 12px 4px;
    font-weight: bold;
  }
  .img-container {
    width: 40px;
    height: 40px;
    border: 1px solid #f9f9f9;
    border-radius: 50%;
  }
}
</style>
