<template>
  <div class="width100 height100 dragabale-card">
    <card-loading v-if="isLoading"></card-loading>
    <template v-else>
      <f-map
        :markers="markers"
        :theme="theme"
        :layers="layers"
        :maxIntensity="maxIntensity"
        :styles="mapStyles"
        :mapZoom="zoom"
      >
      </f-map>
    </template>
  </div>
</template>
<script>
import FMap from '@/FNewMap'
import { isEmpty } from '@facilio/utils/validation'
import helper from 'pages/card-builder/card-helpers'
import BaseCard from 'pages/card-builder/cards/common/BaseCard'
import cardLoading from 'pages/card-builder/components/CardLoading'
export default {
  extends: BaseCard,
  components: { FMap, cardLoading },
  mixins: [helper],
  data() {
    return {
      rerender: false,
      markers: [],
      maxIntensity: 0,
    }
  },
  computed: {
    heatmap() {
      if (!isEmpty(this.cardState) && this.cardState.heatmap) {
        return this.cardState.heatmap
      }
      return {}
    },
    mapStyles() {
      let styles = {}
      let { heatmap } = this
      if (!isEmpty(heatmap)) {
        this.$set(styles, 'heatmap', heatmap)
      }
      return styles
    },
    markerstyle() {
      if (!isEmpty(this.cardState) && this.cardState.marker) {
        return this.cardState.marker
      }
      return {}
    },
    theme() {
      if (this.cardStyle.theme) {
        return this.cardStyle.theme
      }
      return null
    },
    layers() {
      if (!isEmpty(this.cardState) && this.cardState.layers) {
        return this.cardState.layers
      }
      return []
    },
    zoom() {
      if (!isEmpty(this.cardState) && this.cardState.zoom) {
        return this.cardState.zoom
      }
      return null
    },
  },
  watch: {
    'cardData.values': {
      handler: function() {
        this.prepareMapdata()
        this.prepareMapConfig()
        this.rerenderCall()
      },
      deep: true,
    },
    'cardData.icon': {
      handler: function() {
        let self = this
        this.rerender = true
        setTimeout(function cb() {
          self.rerender = false
        }, 100)
      },
      deep: true,
    },
  },
  mounted() {
    this.prepareMapdata()
  },
  methods: {
    rerenderCall() {
      this.rerender = true
      setTimeout(() => {
        this.rerender = false
      }, 100)
    },
    prepareMapdata() {
      if (!isEmpty(this.cardData)) {
        this.maxIntensity = 0
        let { styles } = this.markerstyle
        if (this.cardData && this.cardData.values) {
          this.markers = this.$helpers.cloneObject(this.cardData.values)
          this.markers.forEach(marker => {
            if (marker.location) {
              marker.loc = this.$helpers.cloneObject(marker.location)
            }
            if (marker.loc) {
              let loc = marker.loc.split(',')
              marker.location = {
                lat: Number(loc[0]),
                lng: Number(loc[1]),
              }
            }
            if (marker.weight && marker.weight > this.maxIntensity) {
              this.maxIntensity = marker.weight
            }
            marker.markerSvg = this.getMapMarkerIcon(marker, styles)
            marker.scaledSize = this.getScaledSize(marker)
          })
        }
      }
    },
    getScaledSize(data) {
      let { icon } = data
      if (icon === 1) {
        return {
          width: 40,
          height: 40,
          f: '20px',
          b: '10px',
        }
      }
      if (icon === 2) {
        return {
          width: 40,
          height: 40,
          f: '20px',
          b: '10px',
        }
      } else if (icon === 3) {
        return {
          width: 80,
          height: 80,
          f: '20px',
          b: '20px',
        }
      } else if (icon === 4) {
        return {
          width: 40,
          height: 40,
          f: '20px',
          b: '20px',
        }
      } else {
        return {
          width: 40,
          height: 40,
          f: '20px',
          b: '20px',
        }
      }
    },
    prepareMapConfig() {},
  },
}
</script>
