<template>
  <div>
    <div class="height100 flex-center-vH flex-wrap">
      <el-row class="p30">
        <el-col :span="6">
          <div class="fc-in-custom-icon" style="background-color: #ffffff;">
            <InlineSvg
              src="svgs/storeroom/owner"
              iconClass="icon icon-xlg vertical-middle fc-white-color"
            ></InlineSvg>
          </div>
        </el-col>
        <el-col :span="18">
          <div class="fc-black-13 fwBold text-uppercase text-left">
            {{ $t('common._common.owner') }}
          </div>
          <div class="fc-black-13 text-left pT8">
            {{ getOwner }}
          </div>
        </el-col>
      </el-row>
      <el-row class="p30">
        <el-col :span="6">
          <div class="fc-in-custom-icon" style="background-color: #ffffff;">
            <InlineSvg
              src="svgs/storeroom/calender"
              iconClass="icon icon-xlg vertical-middle fc-white-color"
            ></InlineSvg>
          </div>
        </el-col>
        <el-col :span="18">
          <div class="fc-black-13 fwBold text-uppercase text-left">
            {{ $t('common._common.last_purchased_date') }}
          </div>
          <div class="fc-black-13 text-left pT8">
            {{ getLastPurchasedDate }}
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
    getOwner() {
      let { details } = this
      let { owner } = details || {}
      let { name } = owner || {}
      return name || '---'
    },
    getLastPurchasedDate() {
      let { details } = this
      let { lastPurchasedDate } = details || {}
      if (lastPurchasedDate && lastPurchasedDate != -1) {
        return this.$options.filters.formatDate(lastPurchasedDate, true)
      }
      return '---'
    },
  },
}
</script>
