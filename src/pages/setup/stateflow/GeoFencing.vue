<template>
  <div>
    <div>
      <p class="fc-input-label-txt txt-color">
        {{ $t('setup.stateflow.geo_restriction') }}
      </p>
      <div class="d-flex">
        <div
          :class="
            isFormConfigured ? 'configured-green' : 'configure-blue pointer'
          "
          class="new-statetransition-config"
          @click="editGeoField"
        >
          {{ isFormConfigured ? 'Configured' : 'Configure' }}
        </div>
        <div v-if="isFormConfigured">
          <i
            class="el-icon-edit pointer edit-icon pL30 txt-color"
            @click="editGeoField"
          ></i>
          <span class="reset-txt pointer mL20" @click="removeForm()">
            Reset
          </span>
        </div>
      </div>
      <hr class="separator-line mR40 mT20" />
    </div>
    <GeoFencingFormVue
      v-if="canShowGeoFencingFormPicker"
      :selectedGeoFields="selectedGeoFields"
      :module="module"
      @save="save"
      @close="close"
    />
  </div>
</template>
<script>
import GeoFencingFormVue from './GeoFencingForm.vue'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['module', 'transition', 'setProps'],
  data() {
    return {
      canShowGeoFencingFormPicker: false,
      activeGeoField: null,
      selectedGeoFields: {},
    }
  },
  components: {
    GeoFencingFormVue,
  },
  computed: {
    isFormConfigured() {
      return (
        (this.hasLocationFieldId || this.hasLocationLookupFieldId) &&
        this.hasRadius
      )
    },
    hasLocationFieldId() {
      let { locationFieldId } = this.transition || {}

      return !isEmpty(locationFieldId) && locationFieldId !== -99
    },
    hasLocationLookupFieldId() {
      let { locationLookupFieldId } = this.transition || {}

      return !isEmpty(locationLookupFieldId) && locationLookupFieldId !== -99
    },
    hasRadius() {
      let { radius } = this.transition || {}

      return !isEmpty(radius) && radius !== -99
    },
  },
  methods: {
    close() {
      this.canShowGeoFencingFormPicker = false
    },
    removeForm() {
      this.setProps({
        locationFieldId: -99,
        locationLookupFieldId: -99,
        radius: -99,
      })
      this.$emit('onSave')
    },
    editGeoField() {
      let { locationFieldId, locationLookupFieldId, radius } =
        this.transition || {}

      this.activeGeoField = []
      if (this.hasLocationFieldId) {
        this.activeGeoField.push(locationFieldId)
        if (this.hasLocationLookupFieldId) {
          this.activeGeoField.push(locationLookupFieldId)
        }
      }
      this.selectedGeoFields = {
        activeGeoField: this.activeGeoField,
        range: this.hasRadius ? radius : null,
      }
      this.canShowGeoFencingFormPicker = true
    },
    save(GeoFieldObj) {
      this.setProps({ ...(GeoFieldObj || {}) })
      this.$emit('onSave')
    },
  },
}
</script>
