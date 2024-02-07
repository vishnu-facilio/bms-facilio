<template>
  <div class="smartmap" v-bind:class="{ 'mobile-smartmap': $mobile }">
    <div class="mapLoading" v-if="mapLoading">
      <div class="map-loading-content">
        Loading...
      </div>
    </div>
    <gmap-map :center="center" :zoom="zoom" :options="mapOptions" ref="mapnew">
      <gmap-info-window
        v-if="isClicked"
        :options="{ pixelOffset: { width: -0, height: -40 } }"
        :position="currentMarker.location"
      >
        <template>
          <div class="smartmap-info-window visibility-visible-actions">
            <div class="p15">
              <div class="am-info-row row">
                <div class="col-12 text-right">
                  <i
                    class="el-icon-close pointer f14 bold"
                    @click="isClicked = false"
                  ></i>
                </div>
                <div class="am-info-label  col-12">
                  <div class="avatar-cotainer">
                    <img
                      class="fc-avatar fc-avatar-xxlg"
                      :src="currentMarker.image"
                      v-if="currentMarker.image"
                    />
                    <div v-else class="fc-avatar fc-avatar-xxlg">
                      <inline-svg
                        :src="moduleIcons[currentMarker.moduleName]"
                        iconClass="default-image-section "
                      ></inline-svg>
                    </div>
                  </div>
                </div>
                <div class="am-info-label  col-12 pL10 text-center pT10">
                  <div class="am-id ">#{{ currentMarker.id }}</div>
                  <div class="am-label am-subject">
                    {{ currentMarker.name }}
                  </div>
                </div>
              </div>
            </div>

            <div
              class="col-12 border-bottom2 "
              v-if="
                currentMarker &&
                  currentMarker.value &&
                  currentMarker.value.hasOwnProperty('value')
              "
            ></div>
            <div class="p15 pB0 ">
              <div
                class="am-info-label  col-12  text-center "
                v-if="
                  currentMarker &&
                    currentMarker.value &&
                    currentMarker.value.hasOwnProperty('value')
                "
              >
                <div class="row">
                  <div class="col-8 label-txt-black f16 self-center">
                    {{
                      currentMarker &&
                      currentMarker.value &&
                      currentMarker.value.label
                        ? currentMarker.value.label
                        : ''
                    }}
                  </div>
                  <div class="col-4 label-txt-black f24">
                    {{
                      currentMarker &&
                      currentMarker.value &&
                      currentMarker.value.hasOwnProperty('value')
                        ? currentMarker.value.value +
                          ' ' +
                          (currentMarker.value.hasOwnProperty('unit')
                            ? currentMarker.value.unit
                            : '')
                        : ''
                    }}
                  </div>
                </div>
              </div>
            </div>
            <div class="col-12 " v-if="!$mobile && !isOperationsApp">
              <div class="text-right p15 pT10 pB10">
                <i
                  class="el-icon-right text-fc-blue f20 pointer"
                  @click="jumpToModule(currentMarker.moduleName, currentMarker)"
                ></i>
              </div>
            </div>
          </div>
        </template>
      </gmap-info-window>
      <template>
        <gmap-marker
          v-if="isMarkerEnabled"
          :key="index"
          class="gmarker"
          v-for="(d, index) in markers"
          :position.sync="d.location"
          :clickable="true"
          @click="openInfoWindow(d)"
          :draggable="false"
          :icon="getParentIcon(d)"
          :options="getOptions(d)"
        >
        </gmap-marker>

        <gmap-heatmap-layer
          v-if="isHeatmapEnabled"
          :data="markers"
          :options="{
            maxIntensity: maxInten,
            dissipating: true,
            gradient: heatMapStyle.gradient || null,
            radius: 30 || null,
            opacity: 1 || null,
          }"
        />
      </template>
    </gmap-map>
  </div>
</template>

<script>
import FMAPMixin from '@/mixins/FMAPMixin'
import JumpToHelper from '@/mixins/JumpToHelper'
import { moduleIcons } from 'pages/card-builder/card-constants'
import { mapTheams } from 'src/util/map-constant'
import { isEmpty } from '@facilio/utils/validation'
import * as d3 from 'd3'
export default {
  props: ['markers', 'theme', 'layers', 'maxIntensity', 'styles', 'mapZoom'],
  mixins: [FMAPMixin, JumpToHelper],
  data() {
    return {
      currentMarker: null,
      mapLoading: false,
      isClicked: false,
      ishoverd: false,
      center: { lat: 0, lng: 0 },
      // center: {
      //   lat: 25.2048,
      //   lng: 55.2708,
      // },
      zoom: 2,
      assetsMarkers: [],
      moduleIcons,
      mapTheams,
      maxInten: 0,
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  watch: {
    markers(data) {
      this.$refs['mapnew'].$mapPromise.then(() => {
        this.setGoogleGeolocationObj()
        if (data.length > 1) {
          this.fitBoundMultiMarkers(data)
        } else if (data.length === 1) {
          this.fitBoundSingleMarker(data)
        }
      })
    },
  },
  mounted() {
    this.$refs['mapnew'].$mapPromise.then(() => {
      this.setGoogleGeolocationObj()
      if (this.markers.length > 1) {
        this.fitBoundMultiMarkers(this.markers)
      } else if (this.markers.length === 1) {
        this.fitBoundSingleMarker(this.markers)
      }
    })
  },
  computed: {
    heatMapStyle() {
      if (this.isHeatmapEnabled) {
        return this.styles && !isEmpty(this.styles['heatmap'])
          ? this.styles['heatmap']
          : null
      }
      return null
    },
    isHeatmapEnabled() {
      let { layers } = this
      if (!isEmpty(layers)) {
        let heatmapIndex = layers.findIndex(layer => layer === 'heatmap')
        if (heatmapIndex > -1) {
          return true
        }
      }
      return false
    },
    isMarkerEnabled() {
      let { layers } = this
      if (!isEmpty(layers)) {
        let heatmapIndex = layers.findIndex(layer => layer === 'marker')
        if (heatmapIndex > -1) {
          return true
        } else if (heatmapIndex === -1) {
          return false
        }
      }
      return true
    },
    mapOptions() {
      if (this.mapTheams && this.mapTheams.length && this.theme) {
        return this.mapTheams.find(rt => rt.value === this.theme).theme
      }
      return {
        gestureHandling: 'cooperative',
        styles: [],
      }
    },
    isOperationsApp() {
      let appNameFromUrl = window.location.pathname.slice(1).split('/')[0]
      if (appNameFromUrl === 'operations') {
        return true
      }
      return false
    },
  },
  methods: {
    setfitBounds(bounds) {
      if (this.$el) {
        let mapDom = {
          height: this.$el.offsetHeight,
          width: this.$el.offsetWidth,
        }
        let zoom = this.getBoundsZoomLevel(bounds, mapDom)
        if (zoom > 2) {
          // this.zoom = Math.floor(zoom / 2)
        }
      }
      if (this.mapZoom) {
        // this.$refs.mapnew.fitBounds(bounds)

        setTimeout(() => {
          this.center = bounds.getCenter()
          this.$nextTick(() => {
            this.zoom = this.mapZoom
          })
        }, 1000)
      } else {
        setTimeout(() => {
          this.$refs.mapnew.fitBounds(bounds)
        })
      }
    },
    getBoundsZoomLevel(bounds, mapDim) {
      let WORLD_DIM = { height: 256, width: 256 }
      let ZOOM_MAX = 21

      function latRad(lat) {
        let sin = Math.sin((lat * Math.PI) / 180)
        let radX2 = Math.log((1 + sin) / (1 - sin)) / 2
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2
      }

      function zoom(mapPx, worldPx, fraction) {
        return Math.floor(Math.log(mapPx / worldPx / fraction) / Math.LN2)
      }

      let ne = bounds.getNorthEast()
      let sw = bounds.getSouthWest()

      let latFraction = (latRad(ne.lat()) - latRad(sw.lat())) / Math.PI

      let lngDiff = ne.lng() - sw.lng()
      let lngFraction = (lngDiff < 0 ? lngDiff + 360 : lngDiff) / 360

      let latZoom = zoom(mapDim.height, WORLD_DIM.height, latFraction)
      let lngZoom = zoom(mapDim.width, WORLD_DIM.width, lngFraction)

      return Math.min(latZoom, lngZoom, ZOOM_MAX)
    },
    setGoogleGeolocationObj() {
      this.markers.forEach(marker => {
        if (marker.location) {
          let loc = this.$helpers.cloneObject(marker.location)
          marker.location = new window.google.maps.LatLng({
            lat: Number(loc.lat),
            lng: Number(loc.lng),
          })
          marker.raWlocation = {
            lat: Number(loc.lat),
            lng: Number(loc.lng),
          }
        }
      })
    },
    fitBoundMultiMarkers() {
      const bounds = new window.google.maps.LatLngBounds()
      for (let m of this.markers) {
        bounds.extend(m.location)
      }
      let weights = this.markers.map(rt => rt.weight)
      this.maxInten = d3.max(weights)
      if (this.maxInten < 10) {
        this.maxInten = 10
      }
      this.setfitBounds(bounds)
    },
    fitBoundSingleMarker(data) {
      this.maxInten = data[0].weight || 0
      const bounds = new window.google.maps.LatLngBounds()
      for (let m of this.markers) {
        let location = m.raWlocation

        let lat = Number(location.lat)
        let lng = Number(location.lng)

        bounds.extend({
          lat: lat - 0.2,
          lng: lng - 0.2,
        })
        bounds.extend({
          lat: lat + 0.2,
          lng: lng + 0.2,
        })
      }
      this.setfitBounds(bounds)
    },
    getOptions(data) {
      if (data) {
        return {
          pixelOffset: data.markerSvg
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
    getParentIcon(data) {
      if (data) {
        return {
          url: data.markerSvg,
          scaledSize: data.scaledSize
            ? data.scaledSize
            : {
                width: 80,
                height: 80,
                f: '20px',
                b: '20px',
              },
        }
      }
    },
    openInfoWindow(data) {
      this.isClicked = true
      this.currentMarker = data
    },
    getDefaulModuleImage() {
      return require('statics/space/engine.svg')
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

.avatar-cotainer {
  margin: auto;
  width: 30%;
  text-align: center;
}

.default-image-section {
  width: 50px;
  height: 32px;
  top: 5px;
  position: relative;
  text-align: center;
}
</style>
<style scoped>
.default-img {
  background: #e6eaef;
  border-radius: 3px;
  margin: auto;
}

.am-subject {
  font-size: 18px;
  color: #000;
}
.mobile-smartmap {
  height: 400px;
}
</style>
