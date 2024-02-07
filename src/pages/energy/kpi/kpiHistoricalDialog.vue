<template>
  <el-dialog
    :visible="true"
    :append-to-body="true"
    custom-class="setup-dialog40 setup-dialog breakdown-popup"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div>
      <el-form
        :model="selectedDate"
        :rules="rules"
        :label-position="'top'"
        ref="categoryForm"
      >
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{ $t('kpi.kpi.historical_header') }}
            </div>
          </div>
        </div>
        <div class="new-body-modal">
          <el-row :gutter="20" v-if="resourceType === 'asset'">
            <el-col :span="24">
              <p class="fc-input-label-txt pB10">
                {{ $t('common.products.assets') }}
              </p>
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
          <el-row :gutter="20" v-if="resourceType === 'space'">
            <el-col :span="24">
              <p class="fc-input-label-txt pB10">
                {{ $t('common.products.space_management') }}
              </p>
              <el-input
                @change="resourceQuery = selectedResourceName"
                v-model="selectedResourceName"
                disabled
                class="mT10 fc-input-full-border2"
                type="text"
                :placeholder="$t('common._common.to_search_type')"
              >
                <i
                  @click="chooserVisibility = true"
                  slot="suffix"
                  style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                  class="el-input__icon el-icon-search enpi-icon-search"
                ></i>
              </el-input>
            </el-col>
          </el-row>
          <el-row align="middle" class="mT20">
            <el-col :span="24">
              <el-form-item prop="date" class="kpi-date-picker-width">
                <f-date-picker
                  start-placeholder="Start Date"
                  end-placeholder="End Date"
                  :picker-options="dateOptions"
                  v-model="selectedDate.date"
                  type="datetimerange"
                ></f-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel">{{
            $t('common._common.cancel')
          }}</el-button>
          <async-button buttonClass="modal-btn-save" :clickAction="save">{{
            $t('common._common._save')
          }}</async-button>
        </div>
      </el-form>
    </div>

    <space-asset-multi-chooser
      v-if="resourceType === 'asset'"
      class="fc-input-full-border-select2"
      @associate="associateResource"
      :visibility.sync="chooserVisibility"
      :initialValues="resourceData"
      :query="resourceQuery"
      :showAsset="true"
      :disable="true"
      :filter="spaceAssetFilter"
    ></space-asset-multi-chooser>

    <space-asset-chooser
      v-else
      @associate="associateResource"
      :visibility.sync="chooserVisibility"
      :initialValues="{}"
      :query="resourceQuery"
      :showAsset="resourceType === 'asset'"
      :picktype="resourceType"
      :filter="spaceAssetFilter"
    ></space-asset-chooser>
  </el-dialog>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import moment from 'moment-timezone'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import FDatePicker from 'pages/assets/overview/FDatePicker'
import { mapState } from 'vuex'
import AsyncButton from '@/AsyncButton'

export default {
  components: {
    FDatePicker,
    SpaceAssetChooser,
    SpaceAssetMultiChooser,
    AsyncButton,
  },

  props: ['details', 'resourceType'],

  data() {
    return {
      dateOptions: {
        disabledDate(time) {
          let today = new Date()
          return time.getTime() > today.getTime()
        },
      },
      isIncludeResource: false,
      resourceQuery: null,
      selectedResourceList: [],
      chooserVisibility: false,
      selectedDate: {
        date: null,
      },
      selectedResourceName: null,
      rules: {
        date: {
          validator: function(rule, value, callback) {
            let { date } = this.selectedDate
            if (date === null || date.length !== 2)
              callback(new Error('Please select start and end date'))
            else {
              let { frequency } = this.details
              let duration = moment.duration(
                moment(date[1]).diff(moment(date[0]))
              )
              if (frequency !== 6) {
                if (
                  duration.get('years') > 2 ||
                  (duration.get('years') === 2 &&
                    duration.get('months') > 0 &&
                    duration.get('days') > 0)
                )
                  callback(
                    new Error('Duration should not excced more than 2 years')
                  )
                callback()
              } else {
                if (
                  duration.get('years') > 10 ||
                  (duration.get('years') === 10 &&
                    duration.get('months') > 0 &&
                    duration.get('days') > 0)
                )
                  callback(
                    new Error('Duration should not excced more than 10 years')
                  )
                callback()
              }
            }
          }.bind(this),
          trigger: 'change',
        },
      },
    }
  },

  created() {
    this.$store.dispatch('loadAssetCategory')
    let { matchedResources } = this.details
    this.selectedResourceName = matchedResources[0].name || null
  },

  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),

    spaceAssetFilter() {
      let filters = {}
      let { siteId, assetCategoryId } = this.details

      if (!isEmpty(siteId)) filters.site = siteId

      if (!isEmpty(assetCategoryId)) filters.assetCategory = assetCategoryId

      if (isEmpty(filters)) return {}
      else return filters
    },

    resourceData() {
      let resourceData = {}
      let { assetCategoryId, includedResources } = this.details

      if (assetCategoryId > 0) {
        resourceData.assetCategory = assetCategoryId
      }
      if (includedResources && includedResources.length) {
        resourceData.isIncludeResource = true
        resourceData.selectedResources = includedResources.map(resource => ({
          id: resource,
        }))
      }
      return resourceData
    },

    resourceLabel() {
      if (this.details.assetCategoryId > 0) {
        let category = this.assetCategory.filter(
          d => d.id === this.details.assetCategoryId
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
    associateResource(selectedObj) {
      this.chooserVisibility = false
      this.resourceQuery = null

      if (this.$getProperty(selectedObj, 'resourceList.length', 0)) {
        this.selectedResourceList = selectedObj.resourceList
        this.isIncludeResource = selectedObj.isInclude
      } else {
        this.selectedResourceName = selectedObj.name
        this.selectedResourceList.push(selectedObj)
      }
    },

    save() {
      this.$refs['categoryForm'].validate(valid => {
        if (!valid) return false

        let dataObj = {
          startTime: null,
          endTime: null,
          historicalLoggerAssetIds: [],
          isInclude: false,
        }
        dataObj.startTime = !isEmpty(this.selectedDate.date[0])
          ? this.selectedDate.date[0]
          : -1
        dataObj.endTime = !isEmpty(this.selectedDate.date[1])
          ? this.selectedDate.date[1]
          : -1
        dataObj.historicalLoggerAssetIds = []
        if (this.selectedResourceList.length > 0) {
          this.selectedResourceList.forEach(element => {
            dataObj.historicalLoggerAssetIds.push(element.id)
          })
        }
        dataObj.isInclude =
          this.resourceType === 'asset' ? this.isIncludeResource : true
        this.$emit('callBack', dataObj)
      })
    },

    closeDialog() {
      this.$emit('close')
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
.kpi-date-picker-width {
  .el-date-editor.el-range-editor.el-input__inner.el-date-editor--datetimerange {
    width: 100% !important;
  }
}
</style>
