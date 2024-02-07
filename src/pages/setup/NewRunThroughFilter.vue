<template>
  <el-dialog
    :visible.sync="showDialog"
    width="40%"
    :title="$t('setup.setupLabel.run_through_meter')"
    custom-class="new-runthrough-page fc-dialog-center-container"
  >
    <div class="">
      <el-row>
        <el-col :span="24" class="width100">
          <div class="fc-input-label-txt">
            {{ $t('setup.setupLabel.virtual_meter') }}
          </div>
          <el-select
            class="width100 fc-input-full-border2 fc-tag"
            v-model="selectedvmmeterlist"
            multiple
            collapse-tags
          >
            <el-option
              v-for="(meter, index) in virtualMeterslist"
              :key="index"
              :label="meter.name"
              :value="meter.id"
              :disabled="meter.isHistoricalRunning"
            >
            </el-option>
          </el-select>
        </el-col>
      </el-row>
      <el-row class="mT20" :gutter="20">
        <el-col :span="12">
          <div class="fc-input-label-txt pT15">
            {{ $t('setup.setupLabel.start_time') }}
          </div>
          <el-date-picker
            v-model="startTime"
            :type="'datetime'"
            class="fc-input-full-border2 width100"
          ></el-date-picker>
        </el-col>
        <el-col :span="12">
          <div class="fc-input-label-txt pT15">
            {{ $t('setup.setupLabel.end_time') }}
          </div>
          <el-date-picker
            v-model="endTime"
            :type="'datetime'"
            class="fc-input-full-border2 width100"
          ></el-date-picker>
        </el-col>
      </el-row>
    </div>
    <div class="modal-dialog-footer">
      <button
        type="button"
        class="modal-btn-cancel"
        @click="showDialog = false"
      >
        <span>{{ $t('setup.users_management.cancel') }}</span>
      </button>
      <button type="button" class="modal-btn-save" @click="start">
        <span>{{ $t('setup.setupLabel.start') }}</span>
      </button>
    </div>
  </el-dialog>
</template>
<script>
export default {
  props: ['visible', 'meterList'],
  data() {
    return {
      selectedMeters: [],
      selectedvmmeterlist: [],
      virtualMeterslist: [],
      startTime: null,
      endTime: null,
      interval: '',
      showDialog: this.visible,
      runParentMeter: true,
    }
  },
  computed: {
    // virtualList () {
    //   return this.meterList ? this.meterList.filter(meter => meter.virtual).map(meter => ({
    //     key: meter.id, label: meter.name
    //   })) : []
    // }
  },
  mounted: function() {
    this.loadvmmeterlist()
  },
  watch: {
    visible(val, oldVal) {
      this.showDialog = val
    },
    showDialog(val, oldVal) {
      this.$emit('update:visible', val)
    },
  },
  methods: {
    start() {
      let data = {
        startTime: this.$helpers.getTimeInOrg(this.startTime),
        endTime: this.$helpers.getTimeInOrg(this.endTime),
        // interval: this.interval,
        // runParentMeter: this.runParentMeter
      }
      if (this.selectedvmmeterlist.length) {
        data.vmList = this.selectedvmmeterlist
      }
      this.$http.post('/energymeter/insertvmeterreadings', data).then(d => {
        this.selectedvmmeterlist = []
        this.startTime = null
        this.endTime = null
        // this.interval = null
        this.$emit('loadfilterlist')
        this.$message('Run through filter started')
        this.showDialog = false
      })
    },
    loadvmmeterlist() {
      let data = {
        startTime: this.$helpers.getTimeInOrg(this.startTime),
        endTime: this.$helpers.getTimeInOrg(this.endTime),
      }
      this.startTime = null
      this.endTime = null
      let self = this
      self.loading = true
      self.$http
        .get('/v2/virtualMeter/getvmList', data)
        .then(function(response) {
          self.virtualMeterslist = response.data.result.virtualMeters
        })
    },
  },
}
</script>
<style lang="scss">
.new-runthrough-page {
  .el-dialog__body {
    height: 300px;
  }
}
</style>
