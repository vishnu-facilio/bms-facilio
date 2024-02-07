<template>
  <div class="dragabale-card height100">
    <shimmer-loading v-if="loading" class="map-shimmer"> </shimmer-loading>
    <div v-else class="height100">
      <f-map
        v-if="mapdata && mapdata.length"
        :markers="mapdata"
        :categoryId="assetCategoryID"
        :category="assetCategory"
      ></f-map>
    </div>
  </div>
</template>
<script>
import DashboardFilterMixin from 'pages/dashboard/mixins/DashboardFilters'
import DateHelper from '@/mixins/DateHelper'
import colors from 'charts/helpers/colors'
import GaugeChart from '@/GaugeChart'
import shimmerLoading from '@/ShimmerLoading'
import FMap from '@/FNewMap'
export default {
  props: ['widget', 'config'],
  mixins: [DateHelper, DashboardFilterMixin],
  data() {
    return {
      loading: false,
      mapdata: [],
      data: {
        typeName: 'swimmingpool',
      },
      siteAlarmData: [],
      buildingLoaction: {
        1240236: {
          city: null,
          contact: null,
          country: null,
          currentTime: 1570128211345,
          data: null,
          deleted: false,
          empty: true,
          faxPhone: null,
          formId: -1,
          id: 35531,
          lat: 25.2510166,
          lng: 55.2898577,
          localId: 0,
          moduleId: -1,
          moduleState: null,
          name: null,
          offlineModifiedTime: -1,
          orgId: -1,
          phone: null,
          primaryValue: null,
          siteId: -1,
          state: null,
          stateFlowId: 0,
          street: null,
          sysCreatedBy: null,
          sysCreatedTime: -1,
          sysDeletedBy: null,
          sysDeletedTime: -1,
          sysModifiedBy: null,
          sysModifiedTime: -1,
          zip: null,
        },
        1240225: {
          city: null,
          contact: null,
          country: null,
          currentTime: 1570128211294,
          data: null,
          deleted: false,
          empty: true,
          faxPhone: null,
          formId: -1,
          id: 35530,
          lat: 25.2510166,
          lng: 55.2898577,
          localId: 0,
          moduleId: -1,
          moduleState: null,
          name: null,
          offlineModifiedTime: -1,
          orgId: -1,
          phone: null,
          primaryValue: null,
          siteId: -1,
          state: null,
          stateFlowId: 0,
          street: null,
          sysCreatedBy: null,
          sysCreatedTime: -1,
          sysDeletedBy: null,
          sysDeletedTime: -1,
          sysModifiedBy: null,
          sysModifiedTime: -1,
          zip: null,
        },
        1240580: {
          city: null,
          contact: null,
          country: null,
          currentTime: 1570128882023,
          data: null,
          deleted: false,
          empty: true,
          faxPhone: null,
          formId: -1,
          id: 35543,
          lat: 25.2788468,
          lng: 55.3309395,
          localId: 0,
          moduleId: -1,
          moduleState: null,
          name: null,
          offlineModifiedTime: -1,
          orgId: -1,
          phone: null,
          primaryValue: null,
          siteId: -1,
          state: null,
          stateFlowId: 0,
          street: null,
          sysCreatedBy: null,
          sysCreatedTime: -1,
          sysDeletedBy: null,
          sysDeletedTime: -1,
          sysModifiedBy: null,
          sysModifiedTime: -1,
          zip: null,
        },
      },
    }
  },
  created() {},
  mounted() {
    this.getParams()
    this.getMapData()
  },
  components: {
    shimmerLoading,
    FMap,
  },
  methods: {
    getTspanX(count, def) {
      count = count + ''
      if (count.length === 1) {
        return def + 5
      } else {
        return def
      }
    },
    getMapData() {
      this.loading = true
      let self = this
      let params = {}
      self.$http
        .post(
          '/report/energy/portfolio/getAllBuildingsWithRootMeterMeta',
          params
        )
        .then(function(response) {
          let reportData = response.data.reportData
          if (reportData && reportData.buildingsWithRootMeterMeta) {
            self.mapdata = self.pereparemapData(
              reportData.buildingsWithRootMeterMeta
            )
          }

          self.loading = false
        })
        .catch(function(error) {
          console.log(error)
          self.loading = false
        })
    },
    pereparemapData(data) {
      let self = this
      let i = 1
      let opacity = 0.5 / data.length
      data = data.sort(function(a, b) {
        return a.thisMonthKwh < b.thisMonthKwh ? -1 : 1
      })
      data.forEach(rt => {
        let obj = self
          .$convert(rt.thisMonthKwh)
          .from('kWh')
          .toBest()
        rt.count = Math.round(obj.val) + ' ' + obj.unit
        rt.location = self.buildingLoaction[rt.building]
        rt.opacity = opacity * i + 0.5
        rt.markerUrl = self.getMapMarkers(rt, 1)
        i++
      })
      return data
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
    getMapMarkers(rt, type) {
      let x = this.getTspanX(rt.count, 16.5459096)
      let x1 = this.getTspanX(rt.count, 35)
      let svg1 = ``
      if (type && type === 1) {
        svg1 = `<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="85px" height="85px" viewBox="0 0 85 85" version="1.1">
    <!-- Generator: Sketch 55.2 (78181) - https://sketchapp.com -->
    <title>Group 2</title>
    <desc>Created with Sketch.</desc>
    <g id="Page-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
        <g id="Artboard-Copy" transform="translate(-160.000000, -111.000000)" style="opacity:${rt.opacity}">
            <g id="Group-2" transform="translate(160.000000, 111.000000)">
                <circle id="Oval-Copy-4" fill="#FF5E5E" opacity="0.699999988" cx="42.5" cy="42.5" r="22.5"/>
                <circle id="Oval-Copy-5" fill="#FF5E5E" opacity="0.300000012" cx="42.5" cy="42.5" r="32.5"/>
                <circle id="Oval-Copy-6" fill="#FF5E5E" opacity="0.200000003" cx="42.5" cy="42.5" r="42.5"/>
                <circle id="Oval" fill="#EF5151" cx="42.5" cy="42.5" r="14.5"/>
                <text id="24" font-family="ProximaNova-Bold, Proxima Nova" font-size="12" font-weight="bold" fill="#FFFFFF">
                    <tspan x="${x1}" y="46">${rt.count}</tspan>
                </text>
            </g>
        </g>
    </g>
</svg>`
      } else {
        svg1 = `<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="62px" height="80px" viewBox="0 0 62 80" version="1.1">
                  <title>Group 2 Copy</title>
                  <desc>Created with Sketch.</desc>
                  <g id="Page-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                      <g id="Group-2-Copy" transform="translate(0.200000, 0.200000)">
                          <g id="Group">
                              <path d="M30.7526226,55.4684967 C17.1645388,55.4684967 6.14942237,44.4533803 6.14942237,30.8652965 C6.14942237,17.2772127 17.1645388,6.2684967 30.7526226,6.2684967 C44.3407064,6.2684967 55.3558228,17.2772127 55.3558228,30.8652965 C55.3558228,44.4533803 44.3407064,55.4684967 30.7526226,55.4684967" id="Path" fill="#FFFFFF"/>
                              <path d="M30.7526226,53.0684967 C18.4902055,53.0684967 8.54973459,43.1280258 8.54973459,30.8656087 C8.54973459,18.6031916 18.4902055,8.6684967 30.7526226,8.6684967 C43.0150396,8.6684967 52.9555106,18.6031916 52.9555106,30.8656087 C52.9555106,43.1280258 43.0150396,53.0684967 30.7526226,53.0684967 M30.7526226,0 C13.7687878,0 -2.84217094e-14,13.7687878 -2.84217094e-14,30.7526226 C-2.84217094e-14,54.2033366 24.8890244,79.6 30.8519433,79.6 C36.8148621,79.6 61.5052452,54.2033366 61.5052452,30.7526226 C61.5052452,13.7687878 47.7364573,0 30.7526226,0" id="Fill-1" fill="${rt.color}"/>
                              <path d="M30.7526226,53.0684967 C18.4902055,53.0684967 8.54973459,43.1280258 8.54973459,30.8656087 C8.54973459,18.6031916 18.4902055,8.6684967 30.7526226,8.6684967 C43.0150396,8.6684967 52.9555106,18.6031916 52.9555106,30.8656087 C52.9555106,43.1280258 43.0150396,53.0684967 30.7526226,53.0684967" id="Path" fill="#FFFFFF"/>
                              <path d="M29.724641,2.1193783 C38.1787772,2.1193783 45.7455395,5.9161022 50.8109907,11.8980598 C45.9002953,7.41174931 39.3624732,4.67517793 32.1850571,4.67517793 C16.9337201,4.67517793 4.5702952,17.031419 4.5702952,32.282756 C4.5702952,39.0818397 7.0274003,45.3069802 11.1020923,50.1186593 C5.57644711,45.068923 2.10987904,37.802809 2.10987904,29.7269564 C2.10987904,14.4756193 14.4733039,2.1193783 29.724641,2.1193783 Z" id="Combined-Shape" fill="${rt.color}"/>
                              <path d="M35.8098164,15.4051386 C42.2018576,15.4051386 47.922767,18.2766474 51.7515365,22.8005079 C48.0397747,19.4068702 43.0965477,17.3369571 37.6695387,17.3369571 C26.1417121,17.3369571 16.7967335,26.6765057 16.7967335,38.2043323 C16.7967335,43.3410644 18.6522178,48.044383 21.7295199,51.6806217 C17.5551836,47.8645317 14.9370113,42.3743058 14.9370113,36.2725138 C14.9370113,24.7446872 24.2819898,15.4051386 35.8098164,15.4051386 Z" id="Combined-Shape" fill="#F1F3F4" transform="translate(33.344274, 33.542880) rotate(-180.000000) translate(-33.344274, -33.542880) "/>
                          </g>
                          <text id="75" font-family="ProximaNova-Semibold, Proxima Nova" font-size="25" font-weight="500" fill="#000" style="font-family: sans-serif;font-weight: 700;">
                              <tspan x="${x}" y="40">${rt.count}</tspan>
                          </text>
                      </g>
                  </g>
              </svg>`
      }
      let base64 = btoa(unescape(encodeURIComponent(svg1)))
      return `data:image/svg+xml;base64,${base64}`
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
  /* color: #ffffff; */
  text-transform: uppercase;
  opacity: 0.6;
}
.kpi-container:hover .color-choose-icon {
  display: block !important;
}
</style>
