<template>
  <div class="map" ref="map"></div>
</template>
<script>
import mapboxgl from 'mapbox-gl'
export default {
  name: 'base-map',
  props: {
    styles: {
      type: String,
      required: false,
      default: () => {
        // return null;
        // return 'mapbox://styles/mapbox/streets-v11'
        return 'mapbox://styles/prasanth1/ckmw2ly7e0jy817r6jzd4em5j'
      },
    },
    center: {
      type: Array,
      required: false,
      default: () => [0, 0],
    },
    zoom: {
      type: Number,
      required: false,
      default: 20,
    },
    minZoom: {
      type: Number,
      required: false,
      default: 17,
    },
    maxZoom: {
      type: Number,
      required: false,
      default: 22,
    },
  },
  data() {
    return {
      loaded: false,
    }
  },

  mounted() {
    //map component on mount emits map object and a promise->which resolves when map is loaded

    mapboxgl.accessToken =
      'pk.eyJ1IjoicHJhc2FudGgxIiwiYSI6ImNrbHhodzhzZDJ0YXYydW53d3hhaTBlNW4ifQ.4F4YF7aUuwmUQV5TfA4lbQ'

    this.map = new mapboxgl.Map({
      container: this.$refs.map, // container id
      style: this.styles,
      center: this.center,
      zoom: this.zoom, // starting zoom
      maxZoom: this.maxZoom,
      minZoom: this.minZoom,
      renderWorldCopies: false,
    })

    let onMapLoad = new Promise(res => {
      this.map.on('load', () => {
        this.loaded = true
        this.map.addControl(new mapboxgl.NavigationControl())
        res()
      })
    })

    this.$emit('mapLoaded', {
      map: this.map,
      mapboxgl,
      onMapLoad,
    })
  },
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
.map {
  height: 100%;
  background: #f7f7f7;
  & canvas {
    position: relative !important;
  }
}
</style>
