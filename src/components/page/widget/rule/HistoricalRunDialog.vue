<template>
  <el-dialog
    :visible.sync="visibility"
    :append-to-body="true"
    custom-class="setup-dialog40 setup-dialog breakdown-popup"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <error-banner
      :error.sync="error"
      :errorMessage.sync="errorText"
    ></error-banner>
    <div class="content-section">
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">{{ 'Historical Run' }}</div>
        </div>
      </div>
      <div class="new-body-modal">
        <el-row :gutter="20">
          <el-col :span="24">
            <p class="fc-input-label-txt pB10">{{ $t('alarm.alarm.asset') }}</p>
            <el-input
              v-model="resourceLabel"
              disabled
              class="fc-border-select fc-input-full-border-select2 width100"
            >
              <i
                @click="chooserVisibility = true"
                slot="suffix"
                style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                class="el-input__icon el-icon-search"
              ></i>
            </el-input>
          </el-col>
        </el-row>
        <el-row align="middle" class="mT20">
          <el-col :span="24" class="pR10">
            <f-date-picker
              :start-placeholder="'Start Date'"
              :end-placeholder="'End Date'"
              :picker-options="dateOptions"
              v-model="selectedDate"
              type="datetimerange"
            ></f-date-picker>
          </el-col>
        </el-row>
        <div class="fc-grey2 f11 line-height20 fw4 pT10">
          {{ $t('rule.create.execute_for_30days') }}
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">{{
          $t('agent.agent.cancel')
        }}</el-button>
        <el-button
          type="primary"
          @click="runThroughRule"
          :loading="isSaving"
          class="modal-btn-save"
          >{{ isSaving ? 'Saving...' : 'SAVE' }}</el-button
        >
      </div>
    </div>
    <space-asset-multi-chooser
      class="fc-input-full-border-select2"
      @associate="associateResource"
      :visibility.sync="chooserVisibility"
      :initialValues="resourceData"
      :query="resourceQuery"
      :showAsset="true"
      :disable="true"
    ></space-asset-multi-chooser>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import ErrorBanner from '@/ErrorBanner'
import moment from 'moment-timezone'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import FDatePicker from 'pages/assets/overview/FDatePicker'
import { mapState } from 'vuex'
import { API } from '@facilio/api'

export default {
  props: ['visibility', 'details'],
  components: {
    ErrorBanner,
    FDatePicker,
    SpaceAssetMultiChooser,
  },
  data() {
    return {
      dateOptions: {
        disabledDate(time) {
          let today = new Date()
          return time.getTime() > today.getTime()
        },
      },
      value1: null,
      isIncludeResource: false,
      resourceQuery: null,
      selectedResourceList: [],
      chooserVisibility: false,
      startTime: null,
      endTime: null,
      error: false,
      errorText: '',
      selectedDate: null,
      isSaving: false,
    }
  },
  mounted() {
    this.$store.dispatch('loadAssetCategory')
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
    assetCategoryId() {
      let { details } = this
      let { moduleName, alarmRule } = details || {}
      let assetCategoryId = ''

      if (moduleName === 'newreadingrules') {
        assetCategoryId = this.$getProperty(details, 'assetCategory.id', '')
      } else {
        assetCategoryId = this.$getProperty(
          alarmRule,
          'preRequsite.assetCategoryId',
          '-1'
        )
      }
      return assetCategoryId
    },
    ruleId() {
      let { details } = this
      let { moduleName, alarmRule } = details || {}
      let ruleId = null
      if (moduleName === 'newreadingrules') {
        ruleId = this.$getProperty(details, 'id', null)
      } else {
        ruleId = this.$getProperty(alarmRule, 'preRequsite.id', null)
      }
      return ruleId
    },
    resourceData() {
      return {
        assetCategory: this.assetCategoryId,
        isIncludeResource: this.isIncludeResource,
      }
    },
    resourceLabel() {
      if (this.assetCategoryId > 0) {
        let category = this.assetCategory.filter(
          d => d.id === this.assetCategoryId
        )
        let message
        let selectedCount = this.selectedResourceList.length
        if (selectedCount) {
          let includeMsg = this.isIncludeResource ? 'included' : 'excluded'
          if (selectedCount === 1) {
            return this.selectedResourceList[0].name
          }
          message =
            selectedCount +
            ' ' +
            category[0].name +
            (selectedCount > 1 ? 's' : '') +
            ' ' +
            includeMsg
        } else {
          message = 'All ' + category[0].name + 's selected'
        }
        return message
      } else if (this.selectedResourceList.id > 0) {
        return this.selectedResourceList.name
      }
      return null
    },
  },
  methods: {
    validateEndDate() {
      let duration = moment.duration(
        moment(this.selectedDate[1]).diff(moment(this.selectedDate[0]))
      )
      if (duration.get('months') < 1 && duration.get('days') <= 30) {
        return true
      } else {
        return false
      }
    },
    associateResource(selectedObj) {
      if (selectedObj.resourceList && selectedObj.resourceList.length) {
        this.selectedResourceList = selectedObj.resourceList
        this.isIncludeResource = selectedObj.isInclude
      }
      this.chooserVisibility = false
      this.resourceQuery = null
    },
    validation() {
      if (this.fromTime === null || this.fromTime > 1) {
        this.errorText = 'Please choose start time'
        this.error = true
      } else if (this.toTime === null || this.toTime > 1) {
        this.errorText = 'Please choose end time'
        this.error = true
      }
    },
    async runThroughRule() {
      this.error = false
      if (this.selectedDate === null) {
        this.error = true
        this.errorText = 'Please select start and end date '
        return
      }
      let fromTime = !isEmpty(this.selectedDate[0]) ? this.selectedDate[0] : -1
      let toTime = !isEmpty(this.selectedDate[1]) ? this.selectedDate[1] : -1
      if (this.error) return
      this.isSaving = true
      let historicalLoggerAssetIds = []
      if (this.selectedResourceList.length > 0) {
        this.selectedResourceList.forEach(element => {
          historicalLoggerAssetIds.push(element.id)
        })
      }
      let isInclude = this.isIncludeResource
      let url = `/v2/reading/rule/historicalrun/${this.ruleId}?startTime=${fromTime}&endTime=${toTime}`
      url += '&isInclude=' + isInclude
      url += '&isScaledFlow=true'
      let { error, data } = API.post(url, {
        historicalLoggerAssetIds: historicalLoggerAssetIds,
      })
      if (error && !data) {
        this.$message.error('Error Occurred')
        this.closeDialog()
        return
      }
      this.closeDialog()
      this.isSaving = false
    },

    closeDialog() {
      this.isSaving = false
      this.$emit('update:visibility', false)
    },
  },
}
</script>

<style scoped>
.new-body-modal {
  height: auto;
}
</style>
<style lang="scss">
.breakdown-popup {
  .el-dialog__body {
    padding: 0 0 54px;
  }
  .el-textarea .el-textarea__inner {
    min-height: 50px !important;
    width: 350px;
    resize: none;
  }
  .search-select-comp2 .el-input__inner,
  .search-date-picker .el-date-editor--datetimerange.el-input,
  .el-date-editor--datetimerange.el-input__inner {
    width: 500px !important;
  }
}
</style>
