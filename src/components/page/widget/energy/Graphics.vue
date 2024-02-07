<template>
  <div class="p10" ref="graphics-container">
    <f-graphics-builder
      v-if="details.id"
      :graphicsContext="{
        assetId: config.assetId,
        assetCategoryId: config.assetCategoryId,
      }"
      readonly="true"
      :drilldown="!$mobile"
    ></f-graphics-builder>
  </div>
</template>
<script>
export default {
  props: ['tab', 'moduleName', 'details', 'layoutParams', 'fields'],
  components: {
    FGraphicsBuilder: () => import('pages/assets/graphics/FGraphicsBuilder'),
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  computed: {
    config() {
      return {
        width: 1000,
        height: 10000,
        assetId: this.details.id,
        assetCategoryId: this.details.category.id,
      }
    },
  },
}
</script>
