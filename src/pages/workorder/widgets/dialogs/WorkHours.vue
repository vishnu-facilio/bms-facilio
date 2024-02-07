<template>
  <el-dialog
    class="duration-dialog setup-dialog"
    :visible.sync="showDialog"
    width="40%"
  >
    <div class="new-header-container">
      <div class="new-header-text">
        <div class="fc-setup-modal-title">Set Work Hours</div>
      </div>
    </div>
    <div class="duration-body-scroll">
      <div v-if="id && id != -1">
        <div class="fc-dark-grey-txt14">Estimated Duration</div>
        <el-row class="el-row">
          <div class="el-col el-col-7" v-if="estimatedDuration.days">
            {{ estimatedDuration.days + ' Days' }}
          </div>
          <div class="el-col el-col-11">
            {{
              estimatedDuration.hours +
                ' Hour' +
                (estimatedDuration.hours > 1 ? 's' : '')
            }}
          </div>
        </el-row>
      </div>
      <div>
        <duration
          class="fc-dark-grey-txt14"
          v-model="actualDuration"
          label="Work Hours"
        ></duration>
      </div>
    </div>
    <div class="modal-dialog-footer">
      <span slot="footer">
        <el-button class="modal-btn-cancel" @click="close">CANCEL</el-button>
        <el-button class="modal-btn-save" type="primary" @click="setDuration"
          >SET</el-button
        >
      </span>
    </div>
  </el-dialog>
</template>
<script>
import Duration from '@/FWorkHourDuration'
export default {
  props: ['visible', 'id', 'callback'],
  components: {
    Duration,
  },
  data() {
    return {
      showDialog: this.visible,
      estimatedDuration: {
        hours: 0,
        days: 0,
      },
      actualDuration: null,
    }
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.getEstimatedDuration()
        }
      },
      immediate: true,
    },
    showDialog(val) {
      this.$emit('update:visible', val)
    },
  },
  methods: {
    getEstimatedDuration() {
      if (!this.id || this.id === -1) {
        return
      }
      this.$http
        .get('/workorder/getEstimatedWorkDuration?workOrderId=' + this.id)
        .then(response => {
          if (response && response.data) {
            let duration = response.data.estimatedDuration
            if (duration && duration !== -1) {
              let days = Math.floor(duration / 86400)
              duration -= days * 86400
              let hours = (duration / 3600).toFixed(2)
              this.estimatedDuration.days = days
              this.estimatedDuration.hours = hours
            }
          }
        })
    },
    setDuration() {
      if (this.callback) {
        this.callback(this.actualDuration)
      }
    },
    close() {
      this.showDialog = false
    },
  },
}
</script>
<style>
.duration-dialog .el-row {
  margin-bottom: 20px;
  padding-top: 10px;
}
.duration-dialog .el-dialog__body {
  padding: 0;
}
.duration-body-scroll {
  height: 350px;
  padding-bottom: 60px;
  padding-left: 40px;
  padding-top: 20px;
  overflow-y: scroll;
  overflow-x: hidden;
}
</style>
