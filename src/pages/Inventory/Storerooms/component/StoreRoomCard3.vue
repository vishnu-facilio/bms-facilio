<template>
  <div>
    <div class="height100 flex-top-vH flex-wrap " style="margin-top:20px">
      <el-row class="p30">
        <el-col :span="6">
          <div class="fc-in-custom-icon" style="background-color: #ffffff;">
            <InlineSvg
              src="svgs/storeroom/location"
              iconClass="icon icon-xlg vertical-middle fc-white-color"
            ></InlineSvg>
          </div>
        </el-col>
        <el-col :span="18">
          <div class="fc-black-13 fwBold text-uppercase text-left">
            {{ $t('common._common.location') }}
          </div>
          <div class="fc-black-13 text-left pT8">
            {{ getLocationName }}
          </div>
          <div v-if="details.location">
            <div
              @click="openMap"
              class="pT10 fc-black-14 pointer text-left fc-italic"
            >
              <inline-svg
                src="svgs/maps"
                class="vertical-middle"
                iconClass="icon icon-sm"
              ></inline-svg>
              {{ 'Open in map' }}
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
export default {
  props: ['details'],
  components: {},
  computed: {
    getLocationName() {
      let { details } = this
      let { location } = details || {}
      let { name } = location || {}
      return name || '---'
    },
  },
  methods: {
    openMap() {
      this.$helpers.openInMap(
        this.details.location.lat,
        this.details.location.lng
      )
    },
  },
}
</script>
