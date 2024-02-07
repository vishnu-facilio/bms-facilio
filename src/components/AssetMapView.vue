<template>
  <div class="assetmap">
    <gmap-map
      :center="center"
      :zoom="8"
      :options="mapOptions"
      @click="isClicked = true"
      ref="mapView"
    >
      <gmap-info-window
        v-if="isClicked"
        :options="{ pixelOffset: { width: -0, height: -10 } }"
        :position="currentMarker.location"
      >
        <template>
          <div class="assetmap-info-window">
            <div class="am-header row p15 f12 pB0">
              <div class="col-6">{{ $t('common._common.asset_detail') }}</div>
              <div class="col-6 text-right">
                <i
                  class="el-icon-close pointer f13"
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
                <div class="am-id ">#{{ currentMarker.id }}</div>
                <div class="am-label am-subject">{{ currentMarker.name }}</div>
                <div class="label-txt-black">
                  {{
                    currentMarker.category
                      ? getAssetCategory(currentMarker.category.id).displayName
                      : ''
                  }}
                </div>
                <div class="mT10">
                  <el-button
                    size="mini"
                    class="plain"
                    @click="openAsset(currentMarker)"
                    >{{ $t('common._common.open_asset') }}</el-button
                  >
                </div>
              </div>
            </div>
          </div>
        </template>
      </gmap-info-window>

      <gmap-marker
        :key="index"
        class="gmarker"
        v-for="(d, index) in markers"
        :position.sync="d.location"
        :clickable="true"
        @mouseover="getHoverdData(d)"
        @mouseleave="leaveHoverdData(d)"
        :draggable="false"
        @click="loadInfo(d)"
        :options="getOptions(d)"
        :icon="getIcon(d)"
      >
      </gmap-marker>
    </gmap-map>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
export default {
  props: ['markers', 'mapheight'],
  data() {
    return {
      mapOptions: {
        gestureHandling: 'cooperative',
        maxZoom: 19,
      },
      currentMarker: {},
      isClicked: false,
      ishoverd: false,
      center: { lat: 12.9712077, lng: 77.9095786 },
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  computed: {
    ...mapGetters(['getAssetCategory']),
  },
  watch: {
    markers(data) {
      if (data.length > 1) {
        const bounds = new window.google.maps.LatLngBounds()
        for (let m of data) {
          bounds.extend(m.location)
        }
        this.$refs.mapView.fitBounds(bounds)
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
        self.$refs.mapView.fitBounds(bounds)
      }, 1000)
    }
  },
  methods: {
    loadInfo(data) {
      this.currentMarker = data
      this.isClicked = true
    },
    openAsset(asset) {
      this.$router.push({
        path: '/app/at/assets/all/' + asset.id + '/overview',
      })
      this.selectedAsset.asset = null
    },
    getHoverdData(data) {
      this.currentMarker = data
      this.ishoverd = true
    },
    leaveHoverdData(data) {
      this.currentMarker = data
      this.ishoverd = false
    },
    getIcon(data) {
      if (data) {
        return {
          url: data.markerUrl,
          scaledSize: data.marker
            ? { width: 20, height: 20, f: '10px', b: 'px' }
            : { width: 15, height: 15, f: 'px', b: 'px' },
          origin: data.marker ? { x: 0, y: 0 } : { x: 0, y: 0 },
          content: '',
        }
      }
    },
    getOptions(data) {
      if (data) {
        return {
          pixelOffset: data.marker
            ? { top: -0, bottom: -38 }
            : { width: 0, height: 0 },
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
.assetmap {
  width: 100%;
  height: 80vh;
  padding: 20px;
}
.assetmap .vue-map-container {
  position: relative;
  height: 100%;
  width: 100%;
}
.assetmap button.gm-ui-hover-effect {
  display: none !important;
}
.assetmap .gm-style-iw.gm-style-iw-c {
  padding: 0px;
}
.assetmap .gm-style-iw.gm-style-iw-c {
  border-radius: 0px;
}
.assetmap-info-window {
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
</style>
