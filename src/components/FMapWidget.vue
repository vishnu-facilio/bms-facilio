<template>
  <div>
    <gmap-map
      :center="center"
      :zoom="10"
      :options="mapOptions"
      :style="mapStyle"
      ref="mapView"
    >
      <gmap-info-window
        v-if="isClicked"
        :options="{ pixelOffset: { width: -0, height: -48 } }"
        :position="currentMarker.location"
      >
        <slot :currentMarker="currentMarker"></slot>
      </gmap-info-window>
      <template v-if="markerType && markerType === 'profile'">
        <custom-marker
          v-for="(d, index) in markersList"
          :key="'profile' + index"
          :marker.sync="d.location"
          @click.native="loadInfo(d)"
        >
          <map-profile-marker :image="d.avatarUrl"> </map-profile-marker>
        </custom-marker>
      </template>
      <template v-else>
        <gmap-marker
          :key="index"
          class="gmarker"
          v-for="(d, index) in markersList"
          :position.sync="d.location"
          :clickable="true"
          :draggable="false"
          :icon="d.icon ? d.icon : defaultIcon"
          @click="loadInfo(d)"
        >
        </gmap-marker>
      </template>
    </gmap-map>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import customMarker from '@/map/CustomMarker.vue'
import MapProfileMarker from './map/MapProfileMarker.vue'
export default {
  props: ['data', 'height', 'markerType'],
  components: { customMarker, MapProfileMarker },
  data() {
    return {
      mapOptions: {
        maxZoom: 16,
        gestureHandling: 'cooperative',
      },
      mapStyle: {
        width: '100%',
        height: this.height ? this.height : '300px',
      },
      defaultIcon: '',
      isClicked: false,
      currentMarker: {},
      center: { lat: 40.712776, lng: -74.005974 },
    }
  },
  watch: {
    data: {
      handler(newVal) {
        if (!isEmpty(newVal)) {
          this.fitBounds()
        }
      },
    },
  },
  computed: {
    markersList() {
      if (!isEmpty(this.data)) {
        return this.data.filter(data => data.location)
      }
      return []
    },
  },
  mounted() {
    if (this.data.location) {
      this.getLocation()
    }
    let theme = window.localStorage.getItem('theme')
    if (theme && theme === 'black') {
      this.mapOptions.styles = [
        { elementType: 'geometry', stylers: [{ color: '#242f3e' }] },
        { elementType: 'labels.text.stroke', stylers: [{ color: '#242f3e' }] },
        { elementType: 'labels.text.fill', stylers: [{ color: '#746855' }] },
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
      ]
    }
    this.$refs.mapView.$mapPromise.then(() => {
      // Waits for Map to be loaded initially
      this.fitBounds()
    })
  },
  methods: {
    loadInfo(data) {
      this.isClicked = false
      this.$nextTick(() => {
        this.currentMarker = data
        this.isClicked = true
      })
    },
    mapclick() {
      this.isClicked = false
    },
    fitBounds() {
      if (window.google && window.google.maps) {
        let b = new window.google.maps.LatLngBounds()
        let self = this
        if (this.data) {
          for (let i = 0; i < this.data.length; i++) {
            if (self.data[i].location) {
              b.extend({
                lat: self.data[i].location.lat,
                lng: self.data[i].location.lng,
              })
            }
          }
        }
        this.$refs.mapView.fitBounds(b)
      }
    },
    getLocation() {
      let self = this
      let geocoder = new window.google.maps.Geocoder()
      geocoder.geocode({ address: this.$account.org.timezone }, function(
        results,
        status
      ) {
        if (status === window.google.maps.GeocoderStatus.OK) {
          let latitude = results[0].geometry.location.lat()
          let longitude = results[0].geometry.location.lng()
          self.center = { lat: latitude, lng: longitude }
        }
      })
    },
  },
}
</script>
<style>
.icon-maintenance {
  background-color: green !important;
}
.icon-critical {
  background-color: red !important;
}
.icon-lifesafety {
  background-color: orange !important;
}
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
  color: #25243e;
}
.gm-style .gm-style-iw {
  font-weight: 400;
  font-size: 11px;
  overflow: hidden;
  padding: 20px !important;
}
.gm-ui-hover-effect {
  top: 0 !important;
  right: 10px !important;
}
.gm-ui-hover-effect img {
  pointer-events: none;
  display: block;
  width: 19px !important;
  height: 19px !important;
  margin: 8px;
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
</style>
