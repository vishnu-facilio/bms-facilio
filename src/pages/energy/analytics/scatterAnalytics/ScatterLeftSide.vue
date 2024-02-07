<template>
  <div>
    <div class="overflow-scroll-flex min-width0 min-height0 fc-scatter-left">
      <div class="width100 fc-analytics-left-sidebar fc-overflow-y pB200">
        <slot name="sideBarHide"></slot>
        <div class="mT10">
          <!-- <div>
            <div class="fc-grey7-12 f14 text-left line-height25">
              {{ $t('analytics.analytics.mode') }}
            </div>
            <div>
              <el-select
                v-model="scatterType"
                @change="onTypeChange()"
                placeholder="Select"
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="item in mode"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </div>
          </div> -->
          <div class="fc-text-pink fw6">
            {{ $t('home.dashboard.generate_chart') }}
          </div>
          <div v-show="scatterType === 1">
            <div class="pT10">
              <div class="fc-grey7-12 f14 text-left line-height25">
                {{ $t('analytics.analytics.Category') }}
              </div>
              <div>
                <el-select
                  v-model="selectedAssetCategory"
                  filterable
                  @change="OnCategoryChange()"
                  placeholder="Select"
                  class="fc-input-full-border-select2 width100"
                  :loading="categoriesLoading"
                >
                  <el-option
                    v-for="(category, idx) in assetCategoryList"
                    :key="idx"
                    :label="category.displayName"
                    :value="category.id"
                  ></el-option>
                </el-select>
              </div>
            </div>
            <div class="pT20">
              <div class="fc-grey7-12 f14 text-left line-height25">
                {{ $t('analytics.analytics.xaxis') }}
              </div>
              <div>
                <el-select
                  v-model="selectedxField"
                  filterable
                  placeholder="Select"
                  class="fc-input-full-border-select2 width100"
                  :loading="readingsLoading"
                  @change="populateDataPoints()"
                >
                  <el-option
                    v-for="(reading, idx) in readings"
                    :key="idx"
                    :label="reading.displayName"
                    :value="reading.fieldId"
                  ></el-option>
                </el-select>
              </div>
            </div>
            <div class="pT20">
              <div class="fc-grey7-12 f14 text-left line-height25">
                {{ $t('analytics.analytics.yaxis') }}
              </div>
              <div>
                <el-select
                  v-model="selectedyField"
                  filterable
                  placeholder="Select"
                  class="fc-input-full-border-select2 width100"
                  :loading="readingsLoading"
                  @change="populateDataPoints()"
                >
                  <el-option
                    v-for="(reading, idx) in readings"
                    :key="idx"
                    :label="reading.displayName"
                    :value="reading.fieldId"
                  ></el-option>
                </el-select>
              </div>
            </div>
            <div class="pT20">
              <div class="flex-middle justify-content-space">
                <div class="fc-grey7-12 f14 text-left line-height25">
                  {{ $t('analytics.analytics.assets') }}
                </div>
                <!-- <div class="pointer">
                  <i
                    class="el-icon-search fc-grey7-12 f14 text-left fwBold"
                  ></i>
                </div> -->
              </div>
              <div>
                <el-select
                  :disabled="!(selectedxField && selectedyField)"
                  v-model="selectedAsset"
                  @change="populateDataPoints()"
                  placeholder="Select Assets"
                  class="fc-select-multiple-tag width100 fc-tag"
                  multiple
                  filterable
                  :loading="assetsLoading"
                  remote
                  :remote-method="remoteMethod"
                >
                  <el-option
                    v-for="(asset, idx) in allAssets"
                    :key="idx"
                    :label="asset.name"
                    :value="asset.id"
                  ></el-option>
                </el-select>
              </div>
            </div>
          </div>
          <div v-show="scatterType === 2"></div>
          <div v-show="scatterType === 3">
            <f-scatter-point-selector
              v-if="scatterType === 3"
              :visibility="true"
              :enableFloatimngIcon="true"
              @updateDataPoints="updateMultiModeDataPoints"
              :analyticsConfig.sync="config"
            ></f-scatter-point-selector>
          </div>
          <div v-show="scatterType === 1" class="mT20">
            <f-scatter-graph-points
              @baseLineDataChanged="baseLineDataChanged"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import FScatterPointSelector from 'pages/report/components/FScatterPointSelector'
import FScatterGraphPoints from 'pages/report/components/FScatterGraphPoints.vue'

export default {
  props: ['config'],
  components: {
    FScatterPointSelector,
    FScatterGraphPoints,
  },
  data() {
    return {
      mode: [
        {
          label: 'Category',
          value: 1,
        },
        {
          label: 'Single',
          value: 2,
        },
        {
          label: 'Multiple',
          value: 3,
        },
      ],
      scatterType: 1,
      selectedAssetCategory: null,
      selectedxField: null,
      selectedyField: null,
      allAssets: [],
      selectedAsset: [],
      readings: null,
      dataPoints: [],
      assetCategoryList: [],
      categoriesLoading: false,
      assetsLoading: false,
      readingsLoading: false,
      baselineData: null,
    }
  },
  created() {
    this.$store
      .dispatch('loadAssetCategory')
      .then(() => this.loadAssetCategories())
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
  },
  methods: {
    loadFromReport() {
      if (this.config.scatter) {
        this.selectedAssetCategory = this.config.scatter.selectedAssetCategory
          ? this.config.scatter.selectedAssetCategory
          : null
        this.selectedAsset = this.config.scatter.selectedAsset
          ? this.config.scatter.selectedAsset
          : []
        this.selectedxField = this.config.scatter.selectedxField
          ? this.config.scatter.selectedxField
          : null
        this.selectedyField = this.config.scatter.selectedyField
          ? this.config.scatter.selectedyField
          : null
        this.loadReadings()
      }
    },
    baseLineDataChanged(data) {
      this.baselineData = data
      this.populateDataPoints()
    },
    loadAssetCategories() {
      this.categoriesLoading = true
      API.get('/asset/getAssetCategoryWithReadings').then(({ error, data }) => {
        if (!error) {
          let categoryIds = data ? Object.keys(data) : [-1]
          this.assetCategoryList = this.assetCategory.filter(ele => {
            for (let index = 0; index < categoryIds.length; index++) {
              const element = categoryIds[index]
              if (parseInt(element) === ele.id) {
                ele.toggle = false
                return ele
              }
            }
          })
          this.categoriesLoading = false
        } else {
          let { message } = error
          this.$message.error(message)
          this.categoriesLoading = false
        }
      })
    },
    loadReadings() {
      this.readingsLoading = true
      this.config.selectedAssetCategory = this.selectedAssetCategory
      this.$util
        .loadAssetReadingFields(null, this.selectedAssetCategory)
        .then(response => {
          this.readings = response
          this.readingsLoading = false
        })
      this.loadAssets()
    },
    loadAssets() {
      this.assetsLoading = true
      let url = 'asset/getAssetsWithReadings'
      let params = {
        fetchOnlyAssets: true,
        page: 1,
        perPage: 50,
      }
      if (this.selectedAssetCategory)
        params.categoryIds = [this.selectedAssetCategory]
      API.post(url, params).then(({ data, error }) => {
        if (error) {
          this.$message.error('Error in loading Assets')
        } else {
          this.allAssets = Object.keys(data.assets || {}).map(assetId => ({
            name: data.assets[assetId],
            id: assetId,
          }))
          this.assetsLoading = false
        }
      })
    },
    remoteMethod(query) {
      if (query != '') {
        this.assetsLoading = true
        let url = 'asset/getAssetsWithReadings'
        let params = {
          fetchOnlyAssets: true,
          page: 1,
          perPage: 50,
        }
        if (!isEmpty(query)) params.search = query
        if (this.selectedAssetCategory)
          params.categoryIds = [this.selectedAssetCategory]
        API.post(url, params).then(({ data, error }) => {
          if (error) {
            this.$message.error('Error in loading Assets')
          } else {
            this.allAssets = Object.keys(data.assets || {}).map(assetId => ({
              name: data.assets[assetId],
              id: assetId,
            }))
            this.assetsLoading = false
          }
        })
      }
    },
    OnCategoryChange() {
      this.selectedxField = null
      this.selectedyField = null
      this.selectedAsset = []
      this.readings = null
      this.assetsLoading = false
      this.readingsLoading = false
      this.loadReadings()
      this.$emit('updatecategoryDataPoints', [], [], {
        type: 1,
        selectedAssetCategory: this.selectedAssetCategory,
        selectedAsset: this.selectedAsset,
        selectedxField: this.selectedxField,
        selectedyField: this.selectedyField,
        baselineData: this.baselineData,
      })
    },
    onTypeChange() {
      this.selectedxField = null
      this.selectedyField = null
      this.selectedAssetCategory = null
      this.selectedAsset = []
      this.readings = null
      this.assetsLoading = false
      this.readingsLoading = false
      this.$emit('flushChartState', true)
    },
    populateDataPoints() {
      switch (this.scatterType) {
        case 1:
          if (
            this.selectedAsset.length &&
            this.selectedxField &&
            this.selectedyField
          ) {
            let changedDataPoints = []
            let tempConfig = []
            for (let assetId of this.selectedAsset) {
              let assetDetails = this.allAssets.find(
                asset => assetId === asset.id
              )
              let yPoint = {
                parentId: assetId,
                assetCategoryId: this.selectedAssetCategory,
                prediction: false,
                aliases: {},
                yAxis: {
                  fieldId: this.selectedyField,
                  aggr: 3,
                },
              }
              changedDataPoints.push(yPoint)
              let xPoint = {
                parentId: assetId,
                assetCategoryId: this.selectedAssetCategory,
                prediction: false,
                aliases: {},
                yAxis: {
                  fieldId: this.selectedxField,
                  aggr: 3,
                },
                xDataPoint: true,
              }
              changedDataPoints.push(xPoint)
              tempConfig.push({
                xAxis: [yPoint],
                yAxis: xPoint,
              })
            }
            this.$emit(
              'updatecategoryDataPoints',
              changedDataPoints,
              tempConfig,
              {
                type: 1,
                selectedAssetCategory: this.selectedAssetCategory,
                selectedAsset: this.selectedAsset,
                selectedxField: this.selectedxField,
                selectedyField: this.selectedyField,
                baselineData: this.baselineData,
              }
            )
          }
          break
      }
    },
    updateMultiModeDataPoints(scatterconfig) {
      this.$emit('updateMultiModeDataPoints', scatterconfig)
    },
  },
}
</script>
