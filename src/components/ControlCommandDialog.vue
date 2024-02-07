<template>
  <div>
    <div>
      <div class="reading-card-header fc-input-label-txt pT20">
        Controlable Asset
      </div>
      <el-select
        v-model="modelData.selectedAsset"
        placeholder="Please select a Field"
        class=" fc-input-full-border2"
        filterable
        no-data-text="Loading..."
      >
        <el-option
          :label="asset.name"
          :value="asset.id"
          v-for="asset in controlableAssets"
          :key="asset.id"
        ></el-option>
      </el-select>
    </div>
    <div>
      <div class="reading-card-header fc-input-label-txt pT20">
        Reading
      </div>
      <el-select
        v-model="modelData.selectedReading"
        placeholder="Please select a Field"
        class=" fc-input-full-border2"
        filterable
        no-data-text="Loading..."
      >
        <el-option
          :label="reading.displayName"
          :value="reading.name"
          v-for="(reading, index) in assetReadings"
          :key="index"
        ></el-option>
      </el-select>
    </div>
  </div>
</template>
<script>
import ControlMixinHelper from '@/mixins/ControlCommandMixin'

export default {
  props: ['modelData'],
  mixins: [ControlMixinHelper],
  data() {
    return {
      controlCommand: {},
      assetReadings: null,
      controlableAssets: null,
    }
  },
  watch: {
    'modelData.selectedAsset': {
      handler: function(newVal, oldVal) {
        if (oldVal !== newVal) {
          this.assetReadings = this.getControlableReadings(newVal)
          this.modelData.assetdetails = this.controlableAssets.filter(asset => {
            return asset.id === newVal
          })
        }
      },
      immediate: true,
    },
    'modelData.selectedReading': {
      handler: function(newVal, oldVal) {
        if (oldVal !== newVal) {
          this.modelData.readingField = this.assetReadings.filter(field => {
            return field.name === newVal
          })
        }
      },
      immediate: true,
    },
  },
  computed: {},
  methods: {
    getControlableAssets() {
      this.$http
        .get('/v2/controlAction/getControllableAssets')
        .then(response => {
          this.controlableAssets = response.data.result.controllableResources
        })
    },
    getControlableReadings(resourceId) {
      this.$http
        .get('/v2/controlAction/getControllableFields?resourceId=' + resourceId)
        .then(response => {
          this.assetReadings = response.data.result.controllableFields
        })
    },
  },
  mounted() {
    this.getControlableAssets()
  },
}
</script>
