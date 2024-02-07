<template>
  <div ref="amenities-container" class="facility-amenties">
    <div
      v-if="$validation.isEmpty(details.amenities)"
      class="d-flex justify-content-center flex-direction-column align-center height100"
    >
      <div>
        <inline-svg
          src="svgs/emptystate/data-empty"
          class="vertical-middle'"
          iconClass="icon icon-60"
        ></inline-svg>
      </div>
      <div class="fc-black3-16 self-center bold">
        No Amenities available.
      </div>
    </div>
    <el-row v-else>
      <el-col
        v-for="(amenity, index) in details.amenities"
        :key="index"
        class="amenties-col border-bottom"
        :span="6"
      >
        <InlineSvg
          class="mR15 vertical-middle"
          src="taskcompleteicon"
        ></InlineSvg
        >{{ amenity.name }}
      </el-col>
    </el-row>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['details', 'resizeWidget'],
  created() {
    this.autoResize()
  },
  methods: {
    autoResize() {
      this.$nextTick(() => {
        if (!isEmpty(this.$refs['amenities-container'])) {
          let height = this.$refs['amenities-container'].scrollHeight + 30
          let width = this.$refs['amenities-container'].scrollWidth
          if (this.resizeWidget) {
            this.resizeWidget({ height, width })
          }
        }
      })
    },
  },
}
</script>
