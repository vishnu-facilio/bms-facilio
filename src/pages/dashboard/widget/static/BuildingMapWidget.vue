<template>
  <div class="row fmapwidget" style="width: 100%; height: 100%">
    <div
      class=""
      v-bind:class="{
        'map-drag-area': mode === 'new',
        'map-drag-area': mode === 'edit',
      }"
    ></div>
    <f-map-widget :data="data" height="400px" style="width: 100%; height: 100%">
      <template v-slot="markerScope">
        <div class="row ellipsis" style="min-width: 250px">
          <div class="col">
            <div>
              <div class="row">
                <space-avatar
                  :name="false"
                  size="xxlg"
                  :space="markerScope.currentMarker"
                ></space-avatar>
                <div
                  class="fw5 ellipsis inline pointer self-center"
                  @click="openOverview(markerScope.currentMarker.id)"
                  style="font-size: 16px; top: 10px; margin-left: 10px"
                >
                  {{ markerScope.currentMarker.name }}
                </div>
              </div>
              <div
                class="row g-marker-sub"
                style="margin: 20px"
                v-for="(stats, index) in markerScope.currentMarker.stats"
                :key="index"
              >
                <div class="col-7">
                  <div
                    class="pull-left"
                    @click="
                      filterByBuilding(
                        '/app/fa/faults',
                        markerScope.currentMarker.id,
                        stats
                      )
                    "
                  >
                    {{ stats.severity }}
                  </div>
                </div>
                <div class="col-5">
                  <div
                    class="pull-right"
                    style="font-weight: 500"
                    @click="
                      filterByBuilding(
                        '/app/fa/faults',
                        markerScope.currentMarker.id,
                        stats
                      )
                    "
                  >
                    {{ stats.count }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </f-map-widget>
  </div>
</template>
<script>
import FMapWidget from '@/FMapWidget'
import SpaceAvatar from '@/avatar/Space'
export default {
  data() {
    return {
      mapSummary: {
        loading: true,
        defaultImage: require('statics/space/building.svg'),
        data: [],
        center: { lat: 24.8001981, lng: 55.1142434 },
      },
      data: '',
    }
  },
  mounted() {
    this.initdata()
  },
  created() {
    this.$store.dispatch('loadAlarmSeverity')
  },
  components: {
    FMapWidget,
    SpaceAvatar,
  },
  computed: {
    mode() {
      if (this.$route.query) {
        return this.$route.query.create
      } else {
        return 'normal'
      }
    },
  },
  methods: {
    initdata() {
      let self = this
      setTimeout(function() {
        self.loadMapSummary()
      }, 3000)
    },
    filterByBuilding(url, param, stats) {
      let filterObj = {
        resource: {
          operatorId: 38,
          value: [param + ''],
        },
      }
      if (stats && stats.id !== -1) {
        filterObj.severity = {
          operatorId: 36,
          value: [stats.id + ''],
        }
      }
      console.log('filterByBuilding', url)
      // url = url + '?search=' + encodeURIComponent(JSON.stringify(filterObj))
      this.$router.push({
        path: url,
        query: {
          search: JSON.stringify(filterObj),
          includeParentFilter: 'true',
        },
      })
    },
    loadMapSummary() {
      let self = this
      self.$http
        .get('/report/alarms/map')
        .then(function(response) {
          if (response.data && response.data.length) {
            let reportData = response.data.filter(
              rt =>
                rt.longitude &&
                rt.longitude !== null &&
                rt.latitude &&
                rt.latitude !== null
            )
            let mapDatas = []
            let severityDetails = self.getTopSeverityDetails(3)
            for (let row of reportData) {
              let stats = severityDetails.map(severityLevel => ({
                id: severityLevel.id,
                severity: severityLevel.severity,
                count: row.stats[severityLevel.severity] || 0,
              }))
              stats.push({
                id: -1,
                severity: 'Total',
                count: row.stats.Total || 0,
              })
              let thisData = {
                avatarUrl: row.avatar,
                id: row.id,
                location: { lat: row.latitude, lng: row.longitude },
                name: row.name,
                stats: stats,
              }
              mapDatas.push(thisData)
            }
            self.data = mapDatas
          } else {
            self.mapSummary.loading = false
          }
        })
        .catch(function(error) {
          console.log(error)
          self.data = []
        })
    },
    getTopSeverityDetails(levels) {
      let severityLevel = this.$store.state.alarmSeverity
      let topThreeSeverity = []
      for (
        let n = 0, len = Object.keys(severityLevel).length;
        n < levels && n < len;
        n++
      ) {
        let severityLevelObject = {
          cardinality: severityLevel[n].cardinality,
          severity: severityLevel[n].severity,
          id: severityLevel[n].id,
        }
        topThreeSeverity.push(severityLevelObject)
      }
      return topThreeSeverity
    },
  },
}
</script>
<style>
.fmapwidget .vue-map-container {
  height: 100% !important;
}
.map-drag-area {
  position: absolute;
  width: 100%;
  height: 100%;
  background: #000;
  opacity: 0.3;
  z-index: 1;
}
.map-drag-area:hover {
  cursor: all-scroll;
}
</style>
