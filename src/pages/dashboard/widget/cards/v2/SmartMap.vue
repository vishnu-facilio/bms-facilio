<template>
  <div class="dragabale-card height100">
    <shimmer-loading v-if="loading" class="map-shimmer"> </shimmer-loading>
    <div v-else class="height100">
      <div class="map-select-box">
        <el-select
          v-model="selectedSite"
          filterable
          clearable
          collapse-tags
          @change="applyFilter"
          @clear="clearFilters"
          placeholder="Sites"
          class="db-filter fc-tag"
          v-tippy
        >
          <el-option
            v-for="(site, index) in sites"
            :key="index"
            :label="site.name"
            :value="site.id"
          >
          </el-option>
        </el-select>
      </div>
      <f-map
        v-if="assets && assets.length"
        :markers="assets"
        :categoryId="assetCategoryID"
        :category="assetCategory"
      ></f-map>
    </div>
  </div>
</template>
<script>
import DashboardFilterMixin from 'pages/dashboard/mixins/DashboardFilters'
import DateHelper from '@/mixins/DateHelper'
import shimmerLoading from '@/ShimmerLoading'
import FMAPMixin from '@/mixins/FMAPMixin'
import FMap from '@/FMap'
import * as d3 from 'd3'
import { mapGetters } from 'vuex'
export default {
  props: ['widget', 'config'],
  mixins: [DateHelper, DashboardFilterMixin, FMAPMixin],
  data() {
    return {
      loading: false,
      assets: [],
      sites: [],
      fullAssets: [],
      selectedSite: null,
      data: {
        typeName: 'swimmingpool',
      },
      siteAlarmData: [],
    }
  },
  mounted() {
    this.getParams()
    this.loadAssets()
    this.loadSites()
  },
  computed: {
    ...mapGetters(['getAssetCategoryByName', 'getAlarmSeverity']),

    typeName() {
      if (this.data && Object.keys(this.data).length && this.data.typeName) {
        return this.data.typeName
      }
      return 1
    },
    assetCategory() {
      if (this.typeName) {
        return this.getAssetCategoryByName(this.typeName)
      }
      return this.getAssetCategoryByName('swimmingpool')
    },
    assetCategoryID() {
      if (this.assetCategory && this.assetCategory.id) {
        return this.assetCategory.id
      }
      return null
    },
  },
  components: {
    shimmerLoading,
    FMap,
  },
  methods: {
    applyFilter() {
      this.assets = this.$helpers.cloneObject(
        this.fullAssets.filter(rt => rt.siteId === this.selectedSite)
      )
    },
    clearFilters() {
      this.assets = this.$helpers.cloneObject(this.fullAssets)
    },
    loadAssets() {
      let self = this
      this.loading = true
      let url = `/asset/all?filters=%7B%22category%22%3A%7B%22operatorId%22%3A36%2C%22value%22%3A%5B%22${this.assetCategoryID}%22%5D%7D%7D&moduleName=asset&includeParentFilter=true`
      self.$http.get(url).then(response => {
        self.assets = response.data.assets.filter(rt => rt.geoLocation !== null)
        if (self.data.assetId) {
          self.assets = self.assets.filter(rt => rt.id === self.data.assetId)
        }
        self.loadAssetData()
      })
    },
    loadAssetData() {
      let self = this
      let url = '/v2/maps/assetAlarm'
      let params = {
        ids: self.assets.map(rt => rt.id),
        readingModuleName:
          self.assets && self.assets.length
            ? self.assets[0].moduleName || 'energydata'
            : 'energydata',
        readingFieldNames: ['activePowerB'],
      }
      self.$http.post(url, params).then(response => {
        if (response.data.result) {
          self.prepareAssetMarkers(response.data.result)
        }
        self.loading = false
      })
    },
    loadSites: function() {
      let self = this
      this.loading = true
      let url = '/campus'
      self.$http.get(url).then(response => {
        self.sites = response.data.records.filter(
          rt =>
            rt.location &&
            rt.location.lat &&
            rt.location.lat !== null &&
            rt.location.lng &&
            rt.location.lng !== null
        )
      })
    },
    prepareAssetMarkers(data) {
      if (data) {
        let self = this
        if (data.assetAlarmCount) {
          let assetCountMap = []
          let assetAlarmCount = data.assetAlarmCount
          this.assets.forEach(rt => {
            if (assetAlarmCount[rt.id]) {
              rt['alarmCount'] = self.formatAlarmdata(assetAlarmCount[rt.id])
              rt.location = {
                lat: Number(rt.geoLocation.split(',')[0]),
                lng: Number(rt.geoLocation.split(',')[1]),
              }
              rt.count = rt['alarmCount'] ? rt['alarmCount'].total : 0
              rt.color = rt['alarmCount'] ? rt['alarmCount'].color : '#000'
            } else {
              rt.location = {
                lat: Number(rt.geoLocation.split(',')[0]),
                lng: Number(rt.geoLocation.split(',')[1]),
              }
              rt.count = 0
              rt.color = '#000'
            }
            assetCountMap.push(rt.count)
          })
          let min = Math.min(...assetCountMap)
          let max = Math.max(...assetCountMap)
          let opacityScale = d3
            .scaleLinear()
            .range([0, 0.5])
            .domain([min, max])
          this.assets.forEach(rt => {
            rt.opacity = opacityScale(rt.count) + 0.5
            rt.markerUrl = self.getMapMarkers(rt, 1)
          })
          this.fullAssets = this.$helpers.cloneObject(this.assets)
        }
      }
    },
    formatAlarmdata(data) {
      if (data.length) {
        let count = 0
        let maxSeverityId = 0
        data.forEach(rt => {
          count += rt.count
          if (rt.severityId > maxSeverityId) {
            maxSeverityId = rt.severityId
          }
        })

        return {
          total: count,
          color: maxSeverityId
            ? this.getAlarmSeverity(maxSeverityId).color
            : '#000',
          data: data,
        }
      }
      return {
        total: 0,
        color: '#000',
        data: [],
      }
    },
    pereparemapData() {
      let self = this
      this.assets.forEach(rt => {
        rt.count = this.siteAlarmData.find(rl => rl.siteId === rt.siteId)
          ? this.siteAlarmData.find(rl => rl.siteId === rt.siteId).count
          : 0
        rt.color = this.siteAlarmData.find(rl => rl.siteId === rt.siteId)
          ? this.siteAlarmData.find(rl => rl.siteId === rt.siteId).colorCode
          : '#000'
        rt.assetCount = this.siteAlarmData.find(rl => rl.siteId === rt.siteId)
          ? this.siteAlarmData.find(rl => rl.siteId === rt.siteId).assetCount
          : 0
      })
      let i = 1
      let opacity = 0.5 / this.assets.length
      this.assets = this.assets.sort(function(a, b) {
        return a.count < b.count ? -1 : 1
      })
      this.assets.forEach(rt => {
        rt.opacity = opacity * i + 0.5
        rt.markerUrl = self.getMapMarkers(rt, 1)
        i++
      })
    },
    getParams() {
      if (
        this.widget.dataOptions.metaJson &&
        this.widget.dataOptions.metaJson.length
      ) {
        this.data = JSON.parse(this.widget.dataOptions.metaJson)
      } else {
        this.widget.dataOptions.metaJson = JSON.stringify(this.data)
      }
    },
  },
}
</script>
<style>
.kpi-sections {
  font-size: 14px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: 1.36;
  letter-spacing: 1.5px;
  text-align: center;
}

.kpi-period {
  font-size: 12px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.3px;
  text-align: center;
  text-transform: uppercase;
  opacity: 0.6;
}
.kpi-container:hover .color-choose-icon {
  display: block !important;
}
.map-select-box {
  position: absolute;
  z-index: 10;
  top: 10px;
  left: 10px;
  border-color: #ddd;
}
.map-select-box .db-filter .el-input .el-input__inner {
  height: 40px !important;
  border-radius: 0;
  border: 0;
  box-shadow: rgba(0, 0, 0, 0.3) 0px 1px 4px -1px;
  font-family: Roboto, Arial, sans-serif;
  font-size: 18px;
}
.map-select-box .gmnoprint {
  display: none;
}
</style>
