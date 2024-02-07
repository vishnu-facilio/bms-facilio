<template>
  <div>
    <gmap-autocomplete
      placeholder="Search Google Maps"
      :fields="['address_components', 'geometry']"
      @place_changed="setPlace"
      class="f-location-autofill-input"
    >
    </gmap-autocomplete>
    <Gmap-Map
      ref="mapView"
      :class="customClass ? customClass : 'width100 height400'"
      :zoom="11"
      :center="center"
      :options="
        options
          ? options
          : {
              zoomControl: true,
              mapTypeControl: false,
              scaleControl: false,
              streetViewControl: false,
              rotateControl: false,
              fullscreenControl: false,
              disableDefaultUI: true,
            }
      "
      @click="setLocation"
    >
      <Gmap-Marker
        v-if="place"
        draggable
        @dragend="setLocation"
        :position="{
          lat: place.lat,
          lng: place.lng,
        }"
      ></Gmap-Marker>
    </Gmap-Map>
  </div>
</template>

<script>
import { isEmpty, areValuesEmpty, isArray } from '@facilio/utils/validation'
const gMapsVsFacilioAddressFieldMap = {
  route: 'street',
  locality: 'city',
  administrative_area_level_1: 'state',
  postal_code: 'zip',
  country: 'country',
}
export default {
  props: ['model', 'customClass', 'fillAddress', 'options'],
  data() {
    return {
      place: null,
      center: { lat: 40.712776, lng: -74.005974 },
    }
  },
  mounted() {
    if (
      !isEmpty(this.model) &&
      (!isEmpty(this.model.lat) || !isEmpty(this.model.lng))
    ) {
      this.place = this.$helpers.cloneObject(this.model)
      this.$refs.mapView.$mapPromise.then(() => {
        this.panTo()
      })
    }
  },
  methods: {
    getChoosenLocation() {
      if (!isEmpty((this.model || {}).id)) {
        this.$setProperty(this.place, `id`, this.model.id)
      }
      return this.place
    },
    setPlace(place) {
      if (!isEmpty(place)) {
        this.place = {
          lat: place.geometry.location.lat(),
          lng: place.geometry.location.lng(),
        }
        if (
          this.fillAddress &&
          !isEmpty((place || {}).address_components) &&
          isArray(place.address_components)
        ) {
          this.fillAddressInModel(place.address_components)
        }
        this.panTo()
      }
    },
    panTo() {
      if (!isEmpty(this.$refs.mapView) && !areValuesEmpty(this.place)) {
        this.$refs.mapView.panTo(this.place)
      }
    },
    setLocation(value) {
      if (!isEmpty(value)) {
        this.place = { lat: value.latLng.lat(), lng: value.latLng.lng() }
      }
    },
    fillAddressInModel(addressComponents) {
      addressComponents.forEach(level => {
        let addressType = this.$getProperty(level, ['types', 0])
        if (gMapsVsFacilioAddressFieldMap[addressType]) {
          this.$setProperty(
            this.place,
            `${gMapsVsFacilioAddressFieldMap[addressType]}`,
            level.long_name || level.short_name
          )
        }
      })
    },
  },
}
</script>
