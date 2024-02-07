<template>
  <div class="height100">
    <div class="reports-summary new-reports-summary height100">
      <div class="reports-header" v-if="graphicsObj">
        <div class="report-title pull-left" style="width: 50%;">
          <div class="title row">
            <div style="overflow:hidden;">
              <div class="f18 fc-widget-label ellipsis max-width350px">
                {{ graphicsObj.name }}
              </div>
            </div>
          </div>
          <div class="description">
            {{ graphicsObj.description ? graphicsObj.description : '---' }}
          </div>
        </div>
        <div
          class="pull-right"
          style="padding-right: 15px; display: inline-flex; padding-top: 18px;"
        >
          <el-button
            class="fc__add__btn mL15"
            @click="editGraphics(graphicsObj)"
            >{{ $t('common._common.edit') }}</el-button
          >
          <el-button
            class="fc__border__btn mL15"
            @click="applyToVisibility = true"
            >{{ $t('common.dashboard.apply_to') }}</el-button
          >
          <el-dropdown
            class="mL15 fc-btn-ico-lg pT5 pB5 pL8 pR8 pointer"
            @command="handleMoreCommand"
          >
            <span class="el-dropdown-link">
              <img src="~assets/menu.svg" height="18" width="18" />
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="edit_details">{{
                $t('common.header.edit_details')
              }}</el-dropdown-item>
              <el-dropdown-item command="duplicate">{{
                $t('common.products.duplicate')
              }}</el-dropdown-item>
              <el-dropdown-item command="download">{{
                $t('common._common.download')
              }}</el-dropdown-item>
              <el-dropdown-item command="delete">{{
                $t('common._common.delete')
              }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
        <div style="clear:both"></div>
      </div>
      <div class="reports-header" style="height: 92px;" v-else></div>
      <!-- <spinner v-else :show="!graphicsObj"></spinner> -->
      <div class="height100 scrollable chart-table-layout p20">
        <f-graphics-builder
          v-if="graphicsId"
          ref="graphicsViewer"
          :key="graphicsViewerKey"
          :showFilters="true"
          :graphicsContext="{ id: graphicsId }"
          @loaded="graphicsLoaded"
          @assetLoaded="assetLoaded"
          readonly="true"
        ></f-graphics-builder>
      </div>
    </div>
    <f-graphics-builder
      v-if="editGraphicsObj"
      :graphicsContext="editGraphicsObj"
      @close="editorClose"
    ></f-graphics-builder>
    <new-graphics-form
      :graphicsContext="editGraphicsDetails"
      @saved="data => graphicsLoaded(data, true)"
      :formVisibility.sync="editGraphicsVisibility"
      :duplicateGraphics="duplicateGraphicsBool"
      v-if="editGraphicsVisibility"
    ></new-graphics-form>
    <div v-if="applyToVisibility">
      <el-dialog
        :title="$t('common.dashboard.applyto')"
        :before-close="cancelApplyToForm"
        :visible.sync="applyToVisibility"
        custom-class="fc-dialog-center-container"
      >
        <div class="graphics-applyto-dialog">
          <el-radio-group
            v-model="applyToData.applyToType"
            @change="verifyForDefaultAsset"
          >
            <el-radio :label="1" class="fc-radio-btn">{{
              $t('common._common.all_category_assets')
            }}</el-radio>
            <el-radio :label="2" class="fc-radio-btn">{{
              $t('common.wo_report.specific_assets')
            }}</el-radio>
            <el-radio :label="3" class="fc-radio-btn">{{
              $t('common._common.filter_based_assets')
            }}</el-radio>
          </el-radio-group>
          <el-row v-if="applyToData.applyToType === 2">
            <el-col :span="12" class="mT30">
              <p class="fc-input-label-txt">
                {{ $t('common._common.applicable_assets') }}
              </p>
              <div
                class="el-select-block form-input fc-input-full-border-select2"
              >
                <el-select
                  multiple
                  collapse-tags
                  v-model="applyToData.applyToAssetIds"
                  filterable
                  @change="verifyForDefaultAsset"
                  :placeholder="$t('common._common.applicable_assets')"
                  class="width100"
                >
                  <el-option
                    v-for="asset in categoryBasedAssetsList"
                    :label="asset.name"
                    :value="asset.id"
                    :key="asset.id"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
          </el-row>
          <el-row v-if="applyToData.applyToType === 3">
            <el-col>
              <p class="fc-input-label-txt pT20 pB0">
                {{ $t('common._common.filters') }}
              </p>
              <new-criteria-builder
                ref="criteriaBuilder"
                v-model="applyToData.criteria"
                :exrule="applyToData.criteria"
                :showSiteField="true"
                @condition="newValue => (applyToData.criteria = newValue)"
                module="asset"
                :hideTitleSection="true"
              ></new-criteria-builder>
            </el-col>
          </el-row>
          <el-row v-if="applyToData.applyToType === 3">
            <el-col class="pointer">
              <el-button
                type="button"
                class="small-border-btn"
                :loading="loading.criteriaList"
                @click="loadCriteriaBasedAssetList(true)"
                >{{ $t('common.dashboard.apply_filters') }}</el-button
              >
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12" class="mT30">
              <p class="fc-input-label-txt">
                {{ $t('common._common.default_asset') }}
              </p>
              <div
                class="el-select-block form-input fc-input-full-border-select2"
              >
                <el-select
                  v-model="defaultAssetId"
                  filterable
                  :placeholder="$t('common._common.default_asset')"
                  class="width100"
                >
                  <el-option
                    v-for="asset in assetListForApplyToType"
                    :label="asset.name"
                    :value="asset.id"
                    :key="asset.id"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
          </el-row>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancelApplyToForm()">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="saveApplyTo"
            :loading="saveLoading.applyTo"
            >{{
              saveLoading.applyTo
                ? $t('common._common._saving')
                : $t('common._common._save')
            }}</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import NewGraphicsForm from './NewGraphicsForm'
import util from 'util/util'
import NewCriteriaBuilder from '@/NewCriteriaBuilder'
import {
  isWebTabsEnabled,
  findRouteForTab,
  tabTypes,
  getApp,
} from '@facilio/router'

export default {
  components: {
    FGraphicsBuilder: () => import('pages/assets/graphics/FGraphicsBuilder'),
    NewGraphicsForm,
    NewCriteriaBuilder,
  },
  data() {
    return {
      graphicsObj: null,
      assetObj: null,
      editGraphicsVisibility: false,
      duplicateGraphicsBool: false,
      editGraphicsObj: null,
      editGraphicsDetails: null,
      applyToVisibility: false,
      applyToData: {
        applyToType: 1,
        applyToAssetIds: [],
        criteria: null,
      },
      defaultAssetId: null,
      saveLoading: {
        applyTo: false,
      },
      loading: {
        criteriaList: false,
      },
      categoryBasedAssetsList: [],
      criteriaBasedAssetsList: [],
      graphicsViewerKey: 0,
      staticCriteria: null,
    }
  },
  computed: {
    assetListForApplyToType() {
      if (this.applyToData.applyToType === 2) {
        return this.selectedAssetsList
      } else if (this.applyToData.applyToType === 1) {
        return this.categoryBasedAssetsList
      } else if (this.applyToData.applyToType === 3) {
        return this.criteriaBasedAssetsList
      }
      return null
    },
    selectedAssetsList() {
      if (this.applyToData.applyToType === 2) {
        return this.categoryBasedAssetsList.filter(i => {
          return this.applyToData.applyToAssetIds.includes(i.id)
        })
      }
      return null
    },
    graphicsId() {
      if (this.$route.params.graphicsid) {
        return parseInt(this.$route.params.graphicsid)
      }
      return null
    },
  },
  methods: {
    graphicsLoaded(obj, emit) {
      if (obj) {
        this.graphicsObj = obj
        if (obj.assetId) {
          this.defaultAssetId = obj.assetId
        }
        if (emit) {
          this.$emit('refresh')
        }
        if (obj.applyTo) {
          this.applyToData = JSON.parse(obj.applyTo)
        }
      }
    },
    assetLoaded(obj) {
      this.assetObj = obj
      if (this.graphicsObj && !this.graphicsObj.assetId) {
        this.defaultAssetId = obj.id
      }
      this.loadCategoryBasedAssetList()
      this.loadCriteriaBasedAssetList(false)
    },
    loadCategoryBasedAssetList() {
      if (
        this.assetObj &&
        this.assetObj.category &&
        this.assetObj.category.id
      ) {
        util
          .loadAsset({ categoryId: this.assetObj.category.id })
          .then(response => {
            this.categoryBasedAssetsList = response.assets
          })
      }
    },
    loadCriteriaBasedAssetList(errorMessage) {
      if (this.applyToData && this.applyToData.criteria) {
        this.loading.criteriaList = true
        util
          .loadAsset({ filterCriterias: this.applyToData.criteria })
          .then(response => {
            this.criteriaBasedAssetsList = response.assets
            this.verifyForDefaultAsset()
            this.staticCriteria = this.applyToData.criteria
            this.loading.criteriaList = false
            if (response.assets && response.assets.length === 0) {
              if (errorMessage) {
                this.$message.error(
                  this.$t('common._common.no_assets_found_applied_criteria')
                )
              }
            }
          })
          .catch(error => {
            this.$message.error(error)
            this.loading.criteriaList = false
          })
      }
    },
    editGraphics(editGraphicsObj) {
      this.editGraphicsObj = editGraphicsObj
    },
    deleteGraphics(index, graphics) {
      let confirmed = confirm(
        this.$t('common._common.are_you_want_delete_graphics')
      )
      if (confirmed) {
        this.$http
          .post('/v2/graphics/delete', { recordId: graphics.id })
          .then(response => {
            if (response) {
              this.refreshGraphics()
              this.$emit('deleted')
            }
          })
      }
    },
    editorClose() {
      this.editGraphicsObj = null
      if (this.$refs['graphicsViewer']) {
        this.graphicsViewerKey += 1
      }
    },
    handleMoreCommand(cmd) {
      if (cmd === 'edit_details') {
        this.editGraphicsDetails = this.graphicsObj
        this.editGraphicsVisibility = true
        this.duplicateGraphicsBool = false
      }
      if (cmd === 'duplicate') {
        this.editGraphicsDetails = this.graphicsObj
        this.editGraphicsVisibility = true
        this.duplicateGraphicsBool = true
      } else if (cmd === 'download') {
        if (this.$refs['graphicsViewer']) {
          this.$refs['graphicsViewer'].exportAsImage()
        }
      } else if (cmd === 'delete') {
        this.deleteGraphics(0, this.graphicsObj)
      }
    },
    saveApplyTo() {
      let applyToTempData = this.$helpers.cloneObject(this.applyToData)
      if (this.applyToData.applyToType > 0) {
        if (
          isNaN(this.defaultAssetId) ||
          this.defaultAssetId === null ||
          this.defaultAssetId < 0
        ) {
          this.$message.error(
            this.$t('common.header.kindly_enter_default_asset')
          )
          return
        }
        if (this.applyToData.applyToType === 2) {
          if (this.applyToData.applyToAssetIds.length === 0) {
            this.$message.error(
              this.$t('common.products.select_atleast_one_asset')
            )
            return
          }
        }
        if (this.applyToData.applyToType === 3) {
          if (this.staticCriteria !== this.applyToData.criteria) {
            this.$message.error(
              this.$t('common.header.kindly_load_asset_for_criteria')
            )
            return
          }
        }
      } else {
        this.$message.error(this.$t('common.header.kindly_input_a_value'))
        return
      }
      if (this.applyToData.applyToType === 1) {
        applyToTempData.applyToAssetIds = []
      }
      let param = {
        id: this.graphicsObj.id,
        assetId: this.defaultAssetId,
        applyTo: JSON.stringify(applyToTempData),
      }
      this.saveLoading.applyTo = true
      this.$http
        .post('/v2/graphics/update', { graphics: param })
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success(
              this.$t('common.header.condition_saved_successfully')
            )
            this.applyToVisibility = false
            this.graphicsObj['applyTo'] = response.data.result.graphics.applyTo
          } else {
            this.$message.error(response.data.message)
          }
          this.saveLoading.applyTo = false
        })
        .catch(error => {
          this.$message.error(error)
          this.saveLoading.applyTo = false
        })
    },
    cancelApplyToForm() {
      if (this.graphicsObj.applyTo) {
        this.applyToData = JSON.parse(this.graphicsObj.applyTo)
      } else {
        this.$set(this.applyToData, 'applyToType', 1)
        this.$set(this.applyToData, 'applyToAssetIds', [])
        this.$set(this.applyToData, 'criteria', null)
      }
      this.applyToVisibility = false
    },
    verifyForDefaultAsset() {
      if (this.applyToData.applyToType === 2) {
        if (this.defaultAssetId && this.defaultAssetId > 0) {
          if (this.applyToData.applyToAssetIds.includes(this.defaultAssetId)) {
            return
          } else {
            this.defaultAssetId = null
          }
        }
      } else if (this.applyToData.applyToType === 3) {
        if (this.defaultAssetId && this.defaultAssetId > 0) {
          if (
            this.criteriaBasedAssetsList.find(
              item => item.id === this.defaultAssetId
            )
          ) {
            return
          } else {
            this.defaultAssetId = null
          }
        }
      } else if (
        this.applyToData.applyToType === 1 &&
        this.graphicsObj.assetId
      ) {
        this.defaultAssetId = this.graphicsObj.assetId
      } else if (
        this.graphicsObj &&
        !this.graphicsObj.assetId &&
        this.assetObj &&
        this.assetObj.id
      ) {
        this.defaultAssetId = this.assetObj.id
      }
    },
    refreshGraphics() {
      let actualPath = `/app/co/graphics/`
      if (isWebTabsEnabled()) {
        let { path } = findRouteForTab(tabTypes.CUSTOM, {
          config: { type: 'graphics' },
        })
        actualPath = `/${getApp()?.linkName}/${path}/`
      }

      this.$router.push({
        path: actualPath,
      })
    },
  },
}
</script>

<style scoped>
.auto-created-report {
  padding-top: 10px;
  padding-bottom: 10px;
  letter-spacing: 0.3px;
  color: #333333;
  padding-left: 10px;
  padding-right: 10px;
}
.auto-created-report:hover {
  background: #f1f8fa;
  color: #39b2c2;
}
.duplicate-report-option {
  padding-top: 10px;
  padding-left: 5px;
  padding-right: 5px;
}
.duplicate-report-option:hover {
  background: #f1f8fa;
}
.reports-summary {
  background: white;
  height: 100%;
}

.reports-header .title {
  font-size: 18px;
  letter-spacing: 0.3px;
  color: #333333;
}

.reports-header .description {
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: left;
  color: #898989;
}

.reports-header {
  background: white;
  padding: 5px;
  border-bottom: solid 1px #eae9e9;
  box-shadow: 0 3px 4px 0 rgba(218, 218, 218, 0.32);
}

.report-title {
  padding: 5px 18px;
}

.report-title div {
  padding: 5px 0;
}

.report-title .title-actions {
  font-size: 16px;
}
.report-title .title-actions-fixed {
  font-size: 16px;
}
.report-title .title-actions i:not(.default) {
  display: none;
}
.report-title .title-actions img {
  display: none;
}
.report-title:hover .title-actions img {
  display: inline-block;
}

.report-title:hover .title-actions i:not(.default) {
  display: inline-block;
}

.title-actions-fixed i:not(.default) {
  display: inline-block;
}

/* .duplicate-points-popover{
  margin:50px;
} */

.report-title .title-actions i {
  padding: 0 5px;
  cursor: pointer;
}

.report-title .title-actions img {
  padding: 0 5px;
  cursor: pointer;
}

.report-title .title-actions img:hover {
  opacity: 1;
}

.report-title .title-actions i:hover {
  opacity: 1;
}

.report-options {
  margin: 10px;
  background-color: #ffffff;
  box-shadow: 0 2px 4px 0 rgba(230, 230, 230, 0.5);
  border: solid 1px #d9e0e7;
  border-radius: 5px;
}
/*
.report-options .el-button {
    margin: 2px;
} */

.reports-chart {
  text-align: center;
  min-height: 300px;
}

.reports-underlyingdata {
  padding: 24px;
  padding-top: 10px;
}
.fc-chart-btn {
  color: #333333;
  font-size: 17px;
  padding: 7px;
  padding-left: 15px;
  padding-right: 10px;
}
.title-actions .el-icon-delete {
  opacity: 0 !important;
}
.reports-header:hover .title-actions .el-icon-delete {
  opacity: 1 !important;
}
.title-actions .el-icon-edit {
  opacity: 0 !important;
}
.reports-header:hover .title-actions .el-icon-edit {
  opacity: 1 !important;
}
.chart-table-layout {
  padding-bottom: 150px;
}
.reports-summary .fc-report {
  background: #f7f8f9 !important;
}
.reports-summary .fc-report-section {
  background: #fff !important;
  box-shadow: 0 4px 10px 0 rgba(178, 178, 178, 0.18);
  min-height: 500px;
  border: solid 1px #e6ebf0;
}
.reports-summary .reports-chart {
  padding-left: 25px;
  padding-right: 25px;
  padding-top: 15px;
  padding-bottom: 40px;
}
.chart-table-layout .fc-report-section {
  position: relative;
}
.reports-summary {
  background: transparent !important;
}
.new-reports-summary {
  background: white !important;
}
.reports-summary .fc-list-view-table {
  background: white !important;
}
.reports-summary .fc-underlyingdata {
  box-shadow: 0 4px 10px 0 rgba(178, 178, 178, 0.18);
}
.reports-summary .reports-underlyingdata .table-header {
  font-size: 16px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.6px;
  text-align: left;
  color: #000000;
  padding-bottom: 35px;
}
.reports-summary .fc-list-view-table thead th {
  background: #fff;
  padding: 25px 20px;
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1px;
  text-align: left;
  color: #879eb5;
}
.reports-summary .fc-list-view-table td {
  padding-left: 20px;
}
.chart-table-layout .fc-report-section .header .chart-select {
  position: absolute;
  right: 15px;
  margin-top: -14px;
  display: inline-flex;
}

/* .chart-table-layout .fc-report-section .header .chart-select.noreportdata {
  top: 10px;
} */
.chart-table-layout .fc-report-section .header {
  padding-top: 25px;
  padding-bottom: 25px;
  font-size: 18px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: center;
  color: #000000;
}
.reports-summary .chart-table-layout .fc-report-section .header {
  height: 70px;
}
.fc-report-section .header .header-content {
  margin: auto;
  width: 50%;
  overflow: hidden;
  white-space: nowrap;
}
.fc-report-filter .filter-field {
  margin-right: 5px;
}
.report-title .title {
  font-size: 18px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.6px;
  text-align: left;
  padding-bottom: 0px;
  color: #000000;
}
.report-title .title .pin {
  font-size: 24px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.5px;
  text-align: left;
  color: #e65b5b;
}
.report-options .el-button + .el-button {
  margin: 0px;
}
.report-options button:first-child {
  border: none !important;
}
.report-options button {
  border-left: 1px solid rgb(217, 224, 231);
}
.report-options button:hover {
  border-left: 1px solid rgb(217, 224, 231);
}
.report-options .fc-cmp-btn {
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.2px;
  text-align: left;
  color: #615f89;
  padding: 10px;
}
.report-options .i.el-icon-date.el-icon--right {
  font-size: 15px;
}
.report-icon {
  width: 17px;
  height: 17px;
}
.nounderlinedata {
  height: 500px;
  background: #fff;
  text-align: center;
}
.nounderlinedata .content {
  margin: auto;
  padding-top: 200px;
}
.reports-summary .fc-list-view-table td {
  font-size: 14px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: 1.57;
  letter-spacing: 0.3px;
  text-align: left;
  color: #333333;
}
.reports-summary table.fc-list-view-table.fc-chart-table {
  border: 0 !important;
}
/* .reports-header:hover .title-actions  {
  padding-top: 12px;
  padding-left: 10px;
} */
.title-actions {
  padding-left: 10px;
}
.cursor-load {
  cursor: progress;
}
.report-diplicate-dialog .el-dialog__header {
  border-bottom: 1px solid #e4eaf0;
}
.report-diplicate-dialog .el-dialog {
  width: 20%;
}
.report-header-title {
  width: 100px;
  margin-left: 20px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}
.report-created-time {
  display: none;
}
@media print {
  .report-created-time {
    display: block;
  }
  .report-tab .el-tabs__header .is-top {
    display: none;
  }
  .reports-header .analytics-page-options-building-analysis {
    display: none;
  }
}
.graphics-applyto-dialog {
  height: 400px;
  padding-bottom: 100px;
  overflow-y: scroll;
}
</style>
