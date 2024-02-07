<template>
  <div>
    <div class="height100 flex-center-vH flex-wrap">
      <el-row class="p30">
        <el-col :span="6">
          <div class="fc-in-custom-icon" style="background-color: #ffffff;">
            <InlineSvg
              src="svgs/storeroom/balance"
              iconClass="icon icon-xlg vertical-middle fc-white-color"
            ></InlineSvg>
          </div>
        </el-col>
        <el-col :span="18">
          <div class="fc-black-13 fwBold text-uppercase text-left">
            {{ $t('common._common.no_item_types') }}
          </div>
          <div class="fc-black-13 text-left pT8">
            {{ getNoItemTypes }}
          </div>
        </el-col>
      </el-row>
      <el-row class="p30">
        <el-col :span="6">
          <div class="fc-in-custom-icon" style="background-color: #ffffff;">
            <InlineSvg
              src="svgs/storeroom/balance"
              iconClass="icon icon-xlg vertical-middle fc-white-color"
            ></InlineSvg>
          </div>
        </el-col>
        <el-col :span="18">
          <div class="fc-black-13 fwBold text-uppercase text-left">
            {{ $t('common._common.no_tool_types') }}
          </div>
          <div class="fc-black-13 text-left pT8">
            {{ getNoToolTypes }}
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
    getNoItemTypes() {
      let { details } = this
      let { noOfItemTypes } = details || {}
      return noOfItemTypes || '---'
    },
    getNoToolTypes() {
      let { details } = this
      let { noOfToolTypes } = details || {}
      return noOfToolTypes || '---'
    },
  },
}
</script>
