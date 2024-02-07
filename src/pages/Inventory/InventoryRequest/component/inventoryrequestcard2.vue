<template>
  <div>
    <div class="height100 flex-center-vH flex-wrap">
      <el-row class="p30">
        <el-col :span="6">
          <div class="fc-in-custom-icon" style="background-color: #efa25e;">
            <InlineSvg
              src="svgs/requested-date"
              iconClass="icon icon-md vertical-middle fc-white-color"
            ></InlineSvg>
          </div>
        </el-col>
        <el-col :span="18">
          <div class="fc-black-13 fwBold text-uppercase text-left">
            {{ $t('common.products.requested_date') }}
          </div>
          <div class="fc-black-13 text-left pT8">
            <span v-if="requestedTime">{{
              requestedTime | formatDate(true)
            }}</span>
            <span v-else>---</span>
          </div>
        </el-col>
      </el-row>
      <el-row class="p30">
        <el-col :span="6">
          <div class="fc-in-custom-icon" style="background-color: #46ca87;">
            <InlineSvg
              src="svgs/requested-date"
              iconClass="icon icon-md vertical-middle fc-white-color"
            ></InlineSvg>
          </div>
        </el-col>
        <el-col :span="18">
          <div class="fc-black-13 fwBold text-uppercase text-left">
            {{ $t('common.products.required_date') }}
          </div>
          <div class="fc-black-13 pT8 text-left">
            <span v-if="requiredTime">{{
              requiredTime | formatDate(true)
            }}</span>
            <span v-else>---</span>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['details'],
  computed: {
    requestedTime() {
      let { requestedTime } = this.details || {}
      return !isEmpty(requestedTime) ? requestedTime : null
    },

    requiredTime() {
      let { requiredTime } = this.details || {}
      return !isEmpty(requiredTime) ? requiredTime : null
    },
  },
}
</script>
