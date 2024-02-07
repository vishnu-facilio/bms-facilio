<template>
  <div class="floorplan-builder-toolbar">
    <!-- <el-select
      v-model="leagend.active"
      placeholder="Select"
      class="fc-input-select fc-input-full-border2 fc-input-full-border0"
    >
      <el-option
        v-for="item in options"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      >
      </el-option>
    </el-select> -->
    <div class="fL">
      <div class="floorplan-viewtools-header">Floor Plan</div>
    </div>
    <div
      class="fR"
      v-if="flooplans && flooplans.length && flooplans.length > 1"
    >
      <el-select
        @change="loadFloorPlan()"
        v-model="selectedFloorplanId"
        placeholder="Select"
        class="fc-input-select fc-input-full-border2 fc-input-full-border0"
      >
        <el-option
          v-for="floorplan in flooplans"
          :key="floorplan.id"
          :label="floorplan.name"
          :value="floorplan.id"
        >
        </el-option>
      </el-select>
    </div>
  </div>
</template>

<script>
export default {
  props: ['leagend', 'flooplans', 'floorPlan', 'floorPlanId'],
  data() {
    return {
      selectedFloorplanId: null,
      options: [
        {
          value: 'CONTROL',
          label: 'Control Types',
        },
        {
          value: 'SPACE',
          label: 'Space Types',
        },
        {
          value: 'NONE',
          label: 'None',
        },
      ],
    }
  },
  watch: {
    floorPlanId: {
      handler: function(newVal, oldVal) {
        this.selectedFloorplanId = this.floorPlanId
      },
    },
  },
  mounted() {
    if (this.floorPlanId) {
      this.selectedFloorplanId = this.floorPlanId
    }
  },
  methods: {
    selectLayer(action) {},
    loadFloorPlan() {
      this.$emit('OnchangeFloorPlan', this.selectedFloorplanId)
    },
  },
}
</script>
