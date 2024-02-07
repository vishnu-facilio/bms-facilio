<template>
  <div class="p30" ref="history-container">
    <activities
      v-if="details.createdTime < 1553875578690"
      :record="details"
      :module="moduleName"
      ref="activitiesDiv"
    ></activities>
    <AssetActivaties
      v-else
      :record="details"
      module="asset"
      ref="activitiesDiv"
      @resizeWidget="autoResize"
    ></AssetActivaties>
  </div>
</template>
<script>
import Activities from '@/relatedlist/Activities2'
import AssetActivaties from '@/relatedlist/AssetActivaties'
export default {
  props: [
    'details',
    'layoutParams',
    'hideTitleSection',
    'groupKey',
    'activeTab',
    'moduleName',
    'resizeWidget',
  ],
  components: {
    Activities,
    AssetActivaties,
  },
  mounted() {
    this.autoResize()
  },
  computed: {},
  methods: {
    autoResize() {
      let height = this.$refs['history-container'].scrollHeight + 150
      let width = this.$refs['history-container'].scrollWidth
      if (this.resizeWidget) {
        this.resizeWidget({ height, width })
      }
    },
  },
}
</script>
