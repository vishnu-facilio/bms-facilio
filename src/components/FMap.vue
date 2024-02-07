<template>
  <div class="smartmap">
    <div class="mapLoading" v-if="mapLoading">
      <div class="map-loading-content">
        Loading...
      </div>
    </div>
    <gmap-map
      :center="center"
      :zoom="8"
      :options="mapOptions"
      @click="getMapClickEvent()"
      ref="mapnew"
    >
      <gmap-info-window
        v-if="isClicked"
        :options="{ pixelOffset: { width: -0, height: -40 } }"
        :position="currentMarker.location"
      >
        <template>
          <div class="smartmap-info-window">
            <div class="am-header row p15 border-bottom2 pT10 pB10 f12 pB0">
              <div class="col-6 text-left bold">ASSET DETAIL</div>
              <div class="col-6 text-right">
                <i
                  class="el-icon-close pointer f13 bold"
                  @click="isClicked = false"
                ></i>
              </div>
            </div>
            <div class="am-info-row row p15">
              <div class="am-info-label  col-4">
                <div class="height100">
                  <img
                    class="default-img"
                    :src="currentMarker.avatarUrl"
                    v-if="currentMarker.avatarUrl"
                  />
                  <div class="default-img" v-else></div>
                </div>
              </div>
              <div class="am-info-label  col-8 pL10">
                <div class="am-id text-left">#{{ currentMarker.id }}</div>
                <div class="am-label am-subject text-left">
                  {{ currentMarker.name }}
                </div>
                <div class="label-txt-black text-left">
                  {{
                    currentMarker.category
                      ? getAssetCategory(currentMarker.category.id)
                        ? getAssetCategory(currentMarker.category.id)
                            .displayName
                        : ''
                      : ''
                  }}
                </div>
              </div>
            </div>
            <template v-if="currentMarker.hasOwnProperty('alarmCount')">
              <div
                class="am-info-row row p15 pB0"
                v-if="
                  currentMarker.alarmCount.data &&
                    currentMarker.alarmCount.data.length
                "
              >
                <div class="col-12 text-left pB15">ALARM DETAIL</div>
                <template
                  v-for="(alarm, index) in currentMarker.alarmCount.data"
                >
                  <div
                    class="col-6 am-label am-subject text-left"
                    :key="index + '1'"
                  >
                    {{
                      getAlarmSeverity(alarm.severityId)
                        ? getAlarmSeverity(alarm.severityId).severity
                        : ''
                    }}
                  </div>
                  <div
                    class="col-6 am-label am-subject text-left"
                    :key="index + '2'"
                  >
                    {{ alarm.count }}
                  </div>
                </template>
              </div>
            </template>

            <div class="am-info-row row p15 pT10 pB10">
              <div class="col-12">
                <div class="mT10">
                  <el-button
                    size="mini"
                    class="plain"
                    @click="openAsset(currentMarker)"
                    >Open Asset</el-button
                  >
                </div>
              </div>
            </div>
          </div>
        </template>
      </gmap-info-window>
      <template>
        <gmap-marker
          :key="index"
          class="gmarker"
          v-for="(d, index) in markers"
          :position.sync="d.location"
          :clickable="true"
          :draggable="false"
          @click="openSiteWindow(d)"
          :options="getOptions(d)"
          :icon="getParentIcon(d)"
        >
        </gmap-marker>
      </template>
    </gmap-map>
  </div>
</template>

<script>
import FMAPMixin from '@/mixins/FMAPMixin'
import { mapGetters } from 'vuex'
import { findRouteForTab, isWebTabsEnabled, tabTypes } from '@facilio/router'

export default {
  props: ['mapheight', 'markers', 'categoryId', 'category'],
  mixins: [FMAPMixin],
  data() {
    return {
      parentClicked: false,
      mapLoading: false,
      assetZoom: 19,
      enableCluster: true,
      currentMarker: {},
      currentParentMarker: {},
      assetAlarmCountData: [],
      isClicked: false,
      ishoverd: false,
      center: {
        lat: 25.2048,
        lng: 55.2708,
      },
      assetsMarkers: [],
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  watch: {
    markers(data) {
      if (data.length > 1) {
        const bounds = new window.google.maps.LatLngBounds()
        for (let m of data) {
          bounds.extend(m.location)
        }
        this.$refs.mapnew.fitBounds(bounds)
      }
    },
  },
  mounted() {
    if (this.markers.length > 1) {
      let self = this
      setTimeout(function() {
        const bounds = new window.google.maps.LatLngBounds()
        for (let m of self.markers) {
          bounds.extend(m.location)
        }
        self.$refs.mapnew.fitBounds(bounds)
      }, 1000)
    }
  },
  computed: {
    ...mapGetters(['getAssetCategory', 'getAlarmSeverity']),
    mapOptions() {
      let theme = window.localStorage.getItem('theme')
      if (theme && theme === 'black') {
        return {
          gestureHandling: 'cooperative',
          maxZoom: 19,
          styles: [
            { elementType: 'geometry', stylers: [{ color: '#242f3e' }] },
            {
              elementType: 'labels.text.stroke',
              stylers: [{ color: '#242f3e' }],
            },
            {
              elementType: 'labels.text.fill',
              stylers: [{ color: '#746855' }],
            },
            {
              featureType: 'administrative.locality',
              elementType: 'labels.text.fill',
              stylers: [{ color: '#d59563' }],
            },
            {
              featureType: 'poi',
              elementType: 'labels.text.fill',
              stylers: [{ color: '#d59563' }],
            },
            {
              featureType: 'poi.park',
              elementType: 'geometry',
              stylers: [{ color: '#263c3f' }],
            },
            {
              featureType: 'poi.park',
              elementType: 'labels.text.fill',
              stylers: [{ color: '#6b9a76' }],
            },
            {
              featureType: 'road',
              elementType: 'geometry',
              stylers: [{ color: '#38414e' }],
            },
            {
              featureType: 'road',
              elementType: 'geometry.stroke',
              stylers: [{ color: '#212a37' }],
            },
            {
              featureType: 'road',
              elementType: 'labels.text.fill',
              stylers: [{ color: '#9ca5b3' }],
            },
            {
              featureType: 'road.highway',
              elementType: 'geometry',
              stylers: [{ color: '#746855' }],
            },
            {
              featureType: 'road.highway',
              elementType: 'geometry.stroke',
              stylers: [{ color: '#1f2835' }],
            },
            {
              featureType: 'road.highway',
              elementType: 'labels.text.fill',
              stylers: [{ color: '#f3d19c' }],
            },
            {
              featureType: 'transit',
              elementType: 'geometry',
              stylers: [{ color: '#2f3948' }],
            },
            {
              featureType: 'transit.station',
              elementType: 'labels.text.fill',
              stylers: [{ color: '#d59563' }],
            },
            {
              featureType: 'water',
              elementType: 'geometry',
              stylers: [{ color: '#17263c' }],
            },
            {
              featureType: 'water',
              elementType: 'labels.text.fill',
              stylers: [{ color: '#515c6d' }],
            },
            {
              featureType: 'water',
              elementType: 'labels.text.stroke',
              stylers: [{ color: '#17263c' }],
            },
          ],
        }
      }
      return {
        gestureHandling: 'cooperative',
        maxZoom: 19,
      }
    },
  },
  methods: {
    openSiteWindow(data) {
      this.isClicked = true
      this.currentMarker = data
    },
    fitBounds(markers) {
      let self = this
      setTimeout(function() {
        const bounds = new window.google.maps.LatLngBounds()
        for (let m of markers) {
          bounds.extend(m.location)
        }
        self.$refs.mapnew.fitBounds(bounds)
        self.mapLoading = false
        self.asset = true
      }, 100)
    },
    closeAllInfoWindow() {
      this.isClicked = false
      this.parentClicked = false
    },
    findRoute() {
      if (isWebTabsEnabled()) {
        let tabType = tabTypes.CUSTOM
        let config = { type: 'portfolio' }
        let { name } = findRouteForTab(tabType, { config }) || {}

        return name ? this.$router.resolve({ name }).href : null
      } else {
        return '/app/home/portfolio'
      }
    },
    openAsset({ site, building }) {
      let parentPath = this.findRoute()

      if (parentPath) {
        this.$router.push({
          path: `${parentPath}/site/${site}/building/${building}`,
        })
      }
      this.selectedAsset.asset = null
    },
    getParentIcon(data) {
      if (data) {
        return {
          url: data.markerUrl,
          scaledSize: data.marker
            ? {
                width: 50,
                height: 50,
                f: '20px',
                b: 'px',
              }
            : {
                width: 80,
                height: 80,
                f: 'px',
                b: 'px',
              },
          origin: data.marker
            ? {
                x: 0,
                y: 0,
              }
            : {
                x: 0,
                y: 0,
              },
          content: '',
        }
      }
    },
    getOptions(data) {
      if (data) {
        return {
          pixelOffset: data.marker
            ? {
                top: -0,
                bottom: -38,
              }
            : {
                width: 0,
                height: 0,
              },
        }
      }
    },
  },
}
</script>

<style>
.ginfo {
  background-color: green !important;
  padding: 50px;
  position: absolute;
  bottom: 0px;
  left: 0px;
  display: block !important;
  z-index: 10000009;
}

.g-marker-info {
  width: 280px;
  min-width: 280px;
}

.g-marker-title {
  font-weight: 600;
  padding: 4px 0;
  cursor: pointer;
  font-size: 12px;
  padding-bottom: 7px;
}

.gm-style-iw + div {
  display: none;
}

.gm-style-iw {
  display: block !important;
}

.gm-style .gm-style-iw {
  font-weight: 400;
  font-size: 11px;
  overflow: hidden;
}

.m-mar {
  margin: 3px;
  margin-right: 5px;
  font-size: 14px;
  margin-left: 0px !important;
}

.g-marker-sub {
  cursor: pointer;
}

.img-class {
  text-align: center;
}

.info-left-window {
  margin-left: 20px;
}

.smartmap {
  width: 100%;
  height: 100%;
}

.smartmap .vue-map-container {
  position: relative;
  height: 100%;
  width: 100%;
}

.smartmap button.gm-ui-hover-effect {
  display: none !important;
}

.smartmap .gm-style-iw.gm-style-iw-c {
  padding: 0px;
}

.smartmap .gm-style-iw.gm-style-iw-c {
  border-radius: 0px;
}

.smartmap-info-window {
  width: 260px;
}

.default-img {
  width: 70px;
  height: 70px;
  background: #e6eaef;
  border-radius: 3px;
}

.am-id {
  color: #39b2c2;
  padding-bottom: 7px;
  font-size: 11px;
}

.am-label {
  font-size: 12px;
  line-height: 1.29;
  letter-spacing: 0.3px;
  color: #666666;
  padding-bottom: 5px;
  letter-spacing: 0.4px;
}

.am-subject {
  font-size: 14px;
  color: #000;
}

.am-header {
  padding-bottom: 0px;
}

.mapLoading {
  height: 100%;
  width: 100%;
  background: #000;
  opacity: 0.5;
  position: absolute;
  z-index: 10;
}

.map-loading-content {
  width: 200px;
  margin: auto;
  height: 100%;
  text-align: center;
  color: #fff;
  position: absolute;
  top: 50%;
  left: 42%;
  font-size: 30px;
}
</style>
