<template>
  <div v-if="record.showCancel">
    <el-button class="booking-cancel" @click="cancelBooking">{{
      'Cancel'
    }}</el-button>
  </div>
</template>

<script>
import { API } from '@facilio/api'

export default {
  props: ['record'],
  methods: {
    async cancelBooking() {
      let url = 'v3/spacebooking/action'
      let spaceBookingIds = []
      spaceBookingIds.push(this.record.id)
      let json = { action: 'cancel', spaceBookingIds }
      let { data, error } = await API.post(url, json)
      this.$emit('refreshData')
    },
  },
}
</script>
<style>
.booking-cancel.el-button--default {
  height: auto !important;
  line-height: 1;
  display: inline-block;
  letter-spacing: 0.7px !important;
  border-radius: 3px;
  padding: 8px 15px !important;
  text-transform: inherit;
  border: solid 1px #0053cc;
  background-color: #0053cc;
  color: #fff;
  font-size: 13px;
}
.booking-cancel:hover {
  background-color: #0544a1 !important;
  color: #fff;
  border: solid 1px #0053cc;
}
</style>
