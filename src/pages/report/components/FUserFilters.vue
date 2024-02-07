<template>
  <div class="row report-user-filters">
    <div class="col-12">
      <div>
        <el-select
          v-if="liveFilterField === 'site' && liveFilterType === 'multi'"
          v-model="reportFilter.siteFilter"
          clearable
          multiple
          collapse-tags
          @change="applyFilter"
          placeholder="All Sites"
          class="report-userfilter-select"
          size="mini"
          v-tippy
        >
          <el-option
            v-for="(d, index) in filterData"
            :key="index"
            :label="d.label"
            :value="d.value"
          >
          </el-option>
        </el-select>

        <el-select
          v-if="liveFilterField === 'site' && liveFilterType === 'single'"
          v-model="reportFilter.siteId"
          clearable
          collapse-tags
          @change="applyFilter"
          placeholder="All Sites"
          class="report-userfilter-select"
          size="mini"
          v-tippy
        >
          <el-option
            v-for="(d, index) in filterData"
            :key="index"
            :label="d.label"
            :value="d.value"
          >
          </el-option>
        </el-select>

        <el-select
          v-if="liveFilterField === 'building' && liveFilterType === 'multi'"
          v-model="reportFilter.buildingFilter"
          clearable
          multiple
          collapse-tags
          @change="applyFilter"
          placeholder="All Buildings"
          class="report-userfilter-select"
          size="mini"
          v-tippy
        >
          <el-option
            v-for="(d, index) in filterData"
            :key="index"
            :label="d.name"
            :value="d.id"
          >
          </el-option>
        </el-select>

        <el-select
          v-if="liveFilterField === 'building' && liveFilterType === 'single'"
          v-model="reportFilter.buildingId"
          clearable
          collapse-tags
          @change="applyFilter"
          placeholder="All Buildings"
          class="report-userfilter-select"
          size="mini"
          v-tippy
        >
          <el-option
            v-for="(d, index) in filterData"
            :key="index"
            :label="d.name"
            :value="d.id"
          >
          </el-option>
        </el-select>

        <el-select
          v-if="liveFilterField === 'asset' && liveFilterType === 'multi'"
          v-model="reportFilter.assetFilter"
          clearable
          multiple
          collapse-tags
          @change="applyFilter"
          placeholder="All Assets"
          class="report-userfilter-select"
          size="mini"
          v-tippy
        >
          <el-option
            v-for="(d, index) in filterData"
            :key="index"
            :label="d.name"
            :value="d.id"
          >
          </el-option>
        </el-select>

        <el-select
          v-if="liveFilterField === 'asset' && liveFilterType === 'single'"
          v-model="reportFilter.assetId"
          clearable
          collapse-tags
          @change="applyFilter"
          placeholder="All Assets"
          class="report-userfilter-select"
          size="mini"
          v-tippy
        >
          <el-option
            v-for="(d, index) in filterData"
            :key="index"
            :label="d.name"
            :value="d.id"
          >
          </el-option>
        </el-select>
      </div>
    </div>
  </div>
</template>
<script>
import clone from 'lodash/clone'
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import { API } from '@facilio/api'
import cloneDeep from 'lodash/cloneDeep'

export default {
  props: ['report', 'filters'],
  mixins: [NewDataFormatHelper],
  data() {
    return {
      reportFilter: {
        siteFilter: [],
        siteId: null,
        buildingFilter: [],
        buildingId: null,
        categoryFilter: [],
        assetFilter: [],
        assetId: null,
      },
      filterData: [],
      readings: null,
      sites: [],
      buildings: [],
      urlParams: {
        perPage: 50,
        page: 1,
        viewName: 'hidden-all',
      },
    }
  },
  computed: {
    liveFilterField() {
      if (
        this.report &&
        this?.report?.options?.common?.filters?.filterState?.liveFilterField
      ) {
        return this.report.options.common.filters.filterState.liveFilterField
      }
      return null
    },
    liveFilterType() {
      if (
        this.report &&
        this?.report?.options?.common?.filters?.filterState?.liveFilterType
      ) {
        return this.report.options.common.filters.filterState.liveFilterType
      }
      return null
    },
  },
  created() {
    Promise.all([
      this.loadDefaultSites(),
      this.loadDefaultBuilding(),
      // this.$store.dispatch('loadSites'),
      // this.$store.dispatch('loadBuildings'),
    ])
      .then(() => this.loadFilterData())
      .then(() => {
        this.initProps()
      })
  },
  mounted() {},
  methods: {
    async loadDefaultBuilding() {
      let { data, error } = await API.get(
        '/v3/picklist/building',
        this.urlParams
      )
      if (!error && data?.pickList.length > 0) {
        this.buildings = data.pickList
        if (this.buildings?.length == 50) {
          let params = cloneDeep(this.urlParams)
          params.page = 2
          let { data, error } = await API.get('/v3/picklist/building', params)
          if (!error && data?.pickList.length > 0) {
            Array.prototype.push.apply(this.buildings, data.pickList)
          }
        }
      }
    },
    async loadDefaultSites() {
      let { data, error } = await API.get('/v3/picklist/site', this.urlParams)
      if (!error && data?.pickList.length > 0) {
        this.sites = data.pickList
        if (this.sites?.length == 50) {
          let params = cloneDeep(this.urlParams)
          params.page = 2
          let { data, error } = await API.get('/v3/picklist/site', params)
          if (!error && data?.pickList.length > 0) {
            Array.prototype.push.apply(this.sites, data.pickList)
          }
        }
      }
    },
    async loadFilterData() {
      if (this.liveFilterField === 'site') {
        let sites = clone(this.sites) || []
        if (
          this.report.options.common.filters.filterState &&
          this.report.options.common.filters.filterState.siteFilter &&
          this.report.options.common.filters.filterState.siteFilter.length
        ) {
          this.filterData = sites.filter(
            st =>
              this.report.options.common.filters.filterState.siteFilter.indexOf(
                st.value
              ) >= 0
          )
        } else {
          this.filterData = sites
        }
      } else if (this.liveFilterField === 'building') {
        let buildingList = clone(this.buildings) || []
        let buildings = []
        if (
          this.report.options.common.filters.filterState &&
          this.report.options.common.filters.filterState.buildingFilter &&
          this.report.options.common.filters.filterState.buildingFilter.length
        ) {
          this.filterData = buildingList.filter(building => {
            if (
              this.report.options.common.filters.filterState.buildingFilter.indexOf(
                building.value
              )
            ) {
              buildings.push({
                id: building.value,
                name: building.label,
              })
            }
          })
        } else {
          buildingList.filter(building => {
            buildings.push({
              id: building.value,
              name: building.label,
            })
          })
        }

        this.filterData = buildings
      } else if (this.liveFilterField === 'asset') {
        if (!this.readings) {
          let self = this
          let assets = []
          if (
            self.report.options.common.filters.filterState &&
            self.report.options.common.filters.filterState.assetFilter &&
            self.report.options.common.filters.filterState.assetFilter.length
          ) {
            let selected_assets = []
            for (let aId of self.report.options.common.filters.filterState
              .assetFilter) {
              if (aId > 0) {
                selected_assets.push(aId + '')
              }
            }
            let filters = JSON.stringify({
              id: { operatorId: 36, value: selected_assets },
            })
            let params = { filters: filters }
            let { data, error } = await API.get('/v3/picklist/asset', params)
            if (!error && data?.pickList?.length > 0) {
              data.pickList.forEach(element => {
                assets.push({ id: element.value, name: element.label })
              })
              self.filterData = assets
            }
          } else {
            let { data, error } = await API.get('/asset/getreadings')
            if (!error && data) {
              let assetList = data.assets
              if (
                self.report.options.common.filters.filterState &&
                self.report.options.common.filters.filterState.categoryFilter &&
                self.report.options.common.filters.filterState.categoryFilter
                  .length
              ) {
                let assets_with_category = data.categoryWithAssets
                for (let category_id of self.report.options.common.filters
                  .filterState.categoryFilter) {
                  if (
                    assets_with_category &&
                    assets_with_category[category_id]
                  ) {
                    for (let asset_id in assets_with_category[category_id]) {
                      if (assetList && assetList[asset_id + '']) {
                        assets.push({
                          id: Number(asset_id),
                          name: assetList[asset_id + ''],
                        })
                      }
                    }
                  }
                }
              } else {
                for (let aId in assetList) {
                  assets.push({
                    id: Number(aId),
                    name: assetList[aId + ''],
                  })
                }
              }
              self.filterData = assets
            }
          }
        }
      }
      this.$emit('filterData',this.filterData)
    },
    initProps() {
      if (this.filters && Object.keys(this.filters).length) {
        if (this.liveFilterType == 'single') {
          this.reportFilter.assetId = this.filters.assetFilter.length
            ? this.filters.assetFilter[0]
            : null
          this.reportFilter.buildingId = this.filters.buildingFilter.length
            ? this.filters.buildingFilter[0]
            : null
          this.reportFilter.siteId = this.filters.siteFilter.length
            ? this.filters.siteFilter[0]
            : null
        } else {
          this.reportFilter.assetFilter = this.filters.assetFilter.length
            ? this.filters.assetFilter
            : []
          this.reportFilter.buildingFilter = this.filters.buildingFilter.length
            ? this.filters.buildingFilter
            : []
          this.reportFilter.siteFilter = this.filters.siteFilter.length
            ? this.filters.siteFilter
            : []
        }
        if (
          this.filters?.ttimeFilter &&
          Object.keys(this.filters.ttimeFilter).length > 0
        ) {
          this.reportFilter.ttimeFilter = this.filters.ttimeFilter
        }
      }
      this.$emit('userFilterLoaded')
    },
    applyFilter() {
      if (this.liveFilterField === 'site') {
        if (this.liveFilterType === 'single') {
          if (this.reportFilter.siteId) {
            this.reportFilter.siteFilter = [this.reportFilter.siteId]
          } else {
            this.reportFilter.siteFilter = []
          }
        }
      } else if (this.liveFilterField === 'building') {
        if (this.liveFilterType === 'single') {
          if (this.reportFilter.buildingId) {
            this.reportFilter.buildingFilter = [this.reportFilter.buildingId]
          } else {
            this.reportFilter.buildingFilter = []
          }
        }
      } else if (this.liveFilterField === 'asset') {
        if (this.liveFilterType === 'single') {
          if (this.reportFilter.assetId) {
            this.reportFilter.assetFilter = [this.reportFilter.assetId]
          } else {
            this.reportFilter.assetFilter = []
          }
        }
      }
      let filters = this.prepareUserFilterProps(
        this.reportFilter,
        this.report,
        true
      )
      this.$emit('applyFilter', filters)
    },
  },
}
</script>
<style>
.report-userfilter-select .el-input .el-input__inner {
  font-size: 12px;
  padding: 8px;
  height: 30px !important;
  border-radius: 3px;
  background-color: #ffffff;
  border: solid 1px #cff1f6;
}
.report-userfilter-select.el-select {
  margin-right: 10px;
  border: none;
  width: 180px;
  cursor: pointer;
}
.report-userfilter-select.el-select .el-tag {
  max-width: 102px;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  text-align: left;
  color: #31a4b4;
  background: #31a4b412;
  margin-top: 5px;
}
.report-userfilter-select.el-select .el-select__tags {
  max-width: none !important;
}
.report-userfilter-select > .el-select__tags {
  display: inline-flex;
  left: 0;
}
.report-userfilter-select input.el-select__input {
  position: relative;
}
.report-userfilter-select .el-select .el-input .el-select__caret {
  color: #39b2c2;
  font-weight: 600;
  font-size: 16px;
}

.fc-widget .report-user-filters {
  position: absolute;
  top: 9px;
  left: 15px;
  z-index: 1000;
}
</style>
