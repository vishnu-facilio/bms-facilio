<template>
  <div class="setup-readings height100vh">
    <div class="fc-setup-header" style="height: 182px;">
      <div class="setting-title-block fL">
        <div class="setting-form-title">{{ $t('common.tabs.readings') }}</div>
        <div class="heading-description">
          {{ $t('common._common.list_of_all_asset_readings') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn fR">
        <el-button
          type="primary"
          class="setup-el-btn transition3s"
          @click="showFormula"
          v-if="this.selectedtab === 'formula'"
        >
          {{ $t('common._common.add_formula') }}
          <new-asset-reading-formula
            v-if="formulaShowDialog"
            :visibility.sync="formulaShowDialog"
            :model="model"
            :isNew="!isEdit"
            @saved="aftersave"
          >
          </new-asset-reading-formula>
        </el-button>
        <el-button
          type="primary"
          @click="newAssetReadingDialog"
          class="setup-el-btn transition3s"
          v-if="
            this.selectedtab === 'connected' || this.selectedtab === 'available'
          "
          >{{ $t('common._common.add_reading') }}</el-button
        >
        <new-asset-reading-dialog
          v-if="showDialog"
          @saved="aftersave"
          :visibility.sync="showDialog"
          :model="model"
        >
        </new-asset-reading-dialog>
        <asset-value-add
          v-if="editvalueAdd"
          :field="field"
          :visibility.sync="editvalueAdd"
        ></asset-value-add>
      </div>
      <div class="clearboth flex-middle pT20 justify-content-space width100">
        <div>
          <el-select
            v-model="readinglist"
            placeholder="Select Readings"
            class="fc-input-full-border2 space-reading-link"
            @change="readingSpaceAssetChoosing"
          >
            <el-option
              :label="$t('common._common.asset_readings')"
              value="assetreadings"
            >
              {{ $t('common._common.asset_readings') }}
            </el-option>
            <el-option
              :label="$t('common._common.space_readings')"
              value="spacereadings"
            >
              {{ $t('common._common.space_readings') }}
            </el-option>
            <el-option
              :label="$t('common._common.weather_readings')"
              value="weatherreadings"
            >
              {{ $t('common._common.weather_readings') }}
            </el-option>
          </el-select>
          <el-select
            v-model="categoryId"
            filterable
            :placeholder="$t('common.products.select_category')"
            class="fc-input-full-border-select2 mL20"
            :default-first-option="true"
            @change="getAllAssetReadings"
          >
            <el-option
              v-for="category in assetCategories"
              :key="category.id"
              :label="category.displayName"
              :value="category.id"
            ></el-option>
          </el-select>
        </div>
        <div>
          <f-search v-model="filterAsset" class="reading-search"></f-search>
        </div>
      </div>
      <!-- tab section -->
      <div class="flex-middle clearboth mT15">
        <el-tabs
          v-model="selectedtab"
          @tab-click="readingsTabChange"
          class="width100 setup-reading-tab"
        >
          <el-tab-pane
            v-for="tab in tabs"
            :key="tab.key"
            :label="tab.label"
            :name="tab.key"
          >
            <div class="container-scroll">
              <div class="row pL0 pR0">
                <div class="col-lg-12 col-md-12">
                  <table class="setting-list-view-table width100">
                    <thead>
                      <tr>
                        <th
                          class="setting-table-th setting-th-text"
                          style="width: 30%;"
                        >
                          {{ $t('common.header.reading_name') }}
                        </th>
                        <th
                          class="setting-table-th setting-th-text"
                          style="width: 30%;"
                        >
                          {{ $t('setup.setupLabel.link_name') }}
                        </th>
                        <th
                          class="setting-table-th setting-th-text"
                          style="width: 10%;"
                          v-if="showType"
                        >
                          {{ $t('common._common.reading_type') }}
                        </th>
                        <th
                          class="setting-table-th setting-th-text"
                          style="width: 10%;"
                          v-if="showType"
                        >
                          {{ $t('common._common.type') }}
                        </th>
                        <th
                          class="setting-table-th setting-th-text"
                          style="width: 50%;"
                        ></th>
                      </tr>
                    </thead>
                    <tbody v-if="loading">
                      <tr>
                        <td colspan="100%" class="text-center">
                          <spinner :show="loading" size="80"></spinner>
                        </td>
                      </tr>
                    </tbody>
                    <tbody v-else-if="filterAsset.length === 0">
                      <tr>
                        <td colspan="100%" style="text-align:center;">
                          {{ $t('common._common.nodata') }}
                        </td>
                      </tr>
                    </tbody>
                    <tbody v-else>
                      <tr
                        class="tablerow"
                        v-for="(field, index) in filterAsset"
                        :key="index"
                      >
                        <td :style="{ width: assetCount ? '30%' : '40%' }">
                          {{ field.displayName }}
                        </td>
                        <td>
                          {{ field.name }}
                        </td>
                        <td style="width: 20%;" v-if="showType">
                          {{
                            field.counterField
                              ? 'Counter'
                              : $constants.dataType[field.dataType]
                          }}
                        </td>
                        <td v-if="showType">
                          {{ field.default ? 'Default' : 'Custom' }}
                        </td>
                        <td style="width: 50%;">
                          <div
                            class="text-left actions"
                            style="margin-top:-3px;margin-right: 15px;text-align:center;"
                          >
                            <i
                              class="el-icon-edit pointer"
                              :title="$t('common.wo_report.edit_reading')"
                              v-tippy
                              @click="editReadingField(field)"
                              v-if="editReadingHide"
                            ></i>
                            <i
                              class="el-icon-edit pointer"
                              :title="$t('common._common.formula_reading')"
                              v-tippy
                              @click="editFormulaField(field)"
                              v-if="editFormulaReadingshow"
                            ></i>
                            <!-- <span
                              class="pL15"
                              @click="editvalueAddDialog(field)"
                              style="color: rgb(57, 178, 194);font-size: 14px; font-weight: 500;"
                              v-if="showType"
                              >Populate Data</span
                            > -->
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                  <edit-asset-reading
                    v-if="editshowDialog"
                    :model="model"
                    :visibility.sync="editshowDialog"
                    @saved="aftersave"
                    :unitDetails="metricsUnits"
                    resourceType="asset"
                    :categoryId="categoryId"
                  ></edit-asset-reading>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
    <!-- container scroll -->
  </div>
</template>
<script>
import NewAssetReadingDialog from 'pages/setup/new/v1/NewAssetReadingDialog'
import NewAssetReadingFormula from 'pages/setup/new/v1/NewAssetReadingFormula'
import EditAssetReading from 'pages/setup/new/v1/EditAssetReadingDialog'
import AssetValueAdd from 'pages/setup/AssetValueAdd'
import FSearch from '@/FSearch'
import { API } from '@facilio/api'
import ReadingsHelper from './ReadingsHelper'

export default {
  title() {
    return 'Asset Reading'
  },
  mixins: [ReadingsHelper],
  components: {
    NewAssetReadingDialog,
    EditAssetReading,
    AssetValueAdd,
    NewAssetReadingFormula,
    FSearch,
  },
  data() {
    return {
      showType: true,
      error: false,
      editReadingHide: true,
      editFormulaReadingshow: false,
      errorMessage: null,
      formula: {
        name: '',
        workflow: null,
        includedResources: [],
      },
      categoryId: null,
      metricsUnits: null,
      dialogVisible: false,
      readingDialogVisible: false,
      loading: true,
      filterAsset: [],
      quickSearchQuery: null,
      assetCategories: [],
      assetReadingFields: [],
      assetCount: null,
      loadForm: false,
      closepopover: false,
      readings: [],
      model: {},
      showDialog: false,
      editshowDialog: false,
      editFormulashowDialog: false,
      formulaShowDialog: false,
      editvalueAdd: false,
      showQuickSearch: false,
      currentIndex: 0,
      selectedtab: 'connected',
      tabs: [
        {
          key: 'connected',
          label: this.$t('common.header.connected'),
        },
        {
          key: 'available',
          label: this.$t('common._common.available'),
        },
      ],
      perPage: 50,
      readinglist: 'assetreadings',
      formulaBtnEnable: false,
      isEdit: false,
    }
  },
  created() {
    this.init()
  },
  computed: {
    resourceData() {
      return {
        assetCategory: this.categoryId,
        isIncludeResource: true,
        selectedResources: this.formula.includedResources.map(resource => ({
          id: resource && typeof resource === 'object' ? resource.id : resource,
        })),
      }
    },
    categoryName() {
      if (this.categoryId) {
        let category = this.assetCategories.find(
          category => category.id === this.categoryId
        )
        if (category) {
          return category.name
        }
      }
      return ''
    },
  },
  watch: {
    editshowDialog() {
      if (!this.editshowDialog) {
        this.model = {}
      }
    },
    formulaShowDialog() {
      if (!this.formulaShowDialog) {
        this.model = {}
        this.isEdit = false
      }
    },
    editFormulashowDialog() {
      if (!this.editFormulashowDialog) {
        this.model = {}
      }
    },
  },
  mounted: function() {
    this.loadDefaultMetricUnits()
  },
  methods: {
    init() {
      if (!this.isKpiEnabled()) {
        this.tabs.push({
          key: 'formula',
          label: this.$t('common._common.formula'),
        })
      }
      this.loadAssetCategories()
    },
    async loadAssetCategories() {
      let params = {
        page: 1,
        perPage: 5000,
        withCount: true,
      }
      let moduleName = 'assetcategory'
      let { list, error } = await API.fetchAll(moduleName, params, {
        force: true,
      })
      if (!this.categoryId) {
        this.assetCategories = list
        this.categoryId = this.$getProperty(this, 'assetCategories.0.id')
        this.getAllAssetReadings()
      }
      // this.assetCategories=
    },
    isKpiEnabled() {
      return this.$helpers.isLicenseEnabled('NEW_KPI')
    },
    loadData(url) {
      if (this.selectedtab === 'connected') {
        url += '?type=connected'
      } else if (this.selectedtab === 'formula') {
        url += '?type=formula'
      } else if (this.selectedtab === 'available') {
        url += '?type=available'
      }
      return this.$http.get(url)
    },
    handleClick() {},
    toggleQuickSearch() {
      this.showQuickSearch = !this.showQuickSearch
      if (this.showQuickSearch) {
        this.$nextTick(() => {
          this.$refs.quickSearchQuery.focus()
        })
      }
    },
    editvalueAddDialog(id) {
      this.editvalueAdd = true
      this.field = id
    },
    aftersave: function() {
      this.selectedtab = 'available'
      this.getAllAssetReadingsSave(true)
    },
    closeSearch() {
      this.toggleQuickSearch()
      this.quickSearchQuery = null
      this.quickSearch()
    },
    getAllAssetReadings() {
      this.getAllAssetReadingsSave(false)
    },
    getAllAssetReadingsSave(bool) {
      let self = this
      this.loading = true
      self.filterAsset = []
      this.$util
        .loadAssetReadingFields(
          null,
          this.categoryId,
          false,
          this.selectedtab,
          true
        )
        .then((response, bool) => {
          self.filterAsset = response
          this.loading = false
          if (bool) {
            this.showType = true
            this.editReadingHide = true
            this.editFormulaReadingshow = false
          }
        })
    },
    newAssetReadingDialog() {
      this.showDialog = true
    },
    readingsTabChange() {
      this.getAllAssetReadings()
      if (this.selectedtab === 'formula') {
        this.showType = false
        this.editReadingHide = false
        this.editFormulaReadingshow = true
      } else if (this.selectedtab === 'connected') {
        this.showType = true
        this.editReadingHide = true
        this.editFormulaReadingshow = false
      } else if (this.selectedtab === 'available') {
        this.showType = true
        this.editReadingHide = true
        this.editFormulaReadingshow = false
      }
    },
    editReadingField(field) {
      this.editshowDialog = true
      this.model.fields = [field]
      this.model.categoryId = Number(field.categoryId)
    },
    editFormulaField(field) {
      this.isEdit = true
      this.formulaShowDialog = true
      this.model.fields = [field]
      this.model.categoryId = Number(field.categoryId)
    },
    loadDefaultMetricUnits() {
      let self = this
      self.$http.get('/units/getDefaultMetricUnits').then(response => {
        self.metricsUnits = response.data
      })
    },
    showFormula() {
      this.formulaShowDialog = true
    },
  },
}
</script>
