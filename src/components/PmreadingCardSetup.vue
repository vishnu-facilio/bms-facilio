<template>
  <el-dialog
    :title="'PM Readings'"
    :append-to-body="true"
    :custom-class="{
      'building-dialog': true,
      'select-building-dialog': true,
      'fc-dialog-center-container': true,
      'gauge-setip-dialog': true,
    }"
    :visible.sync="visibility"
    width="30%"
    :before-close="cancel"
  >
    <div>
      <div>
        <div class="reading-card-header fc-input-label-txt pT20">
          Preventive Maintenance
        </div>
        <el-select
          v-model="data.pmId"
          placeholder="Please select a Field"
          class="fc-input-full-border2"
          filterable
          no-data-text="Loading..."
          @change="loadResources"
        >
          <el-option
            :label="pm.name"
            :value="pm.id"
            v-for="(pm, index) in pmlist"
            :key="index"
          ></el-option>
        </el-select>
      </div>
      <template v-if="pmCreationType === 2">
        <div
          v-if="assignmentType === 4"
          class="reading-card-header fc-input-label-txt pT20"
        >
          Asset
        </div>
        <div v-else class="reading-card-header fc-input-label-txt pT20">
          Space
        </div>
        <el-select
          v-model="data.config.resourceId"
          class="fc-input-full-border2"
        >
          <el-option
            :label="resource.name"
            :value="resource.id"
            v-for="(resource, key) in pmResourceList"
            :key="key"
          ></el-option>
        </el-select>
      </template>
      <div class="pT10">
        <p class="grey-text2 kpi-text-3 ">Default period</p>
        <el-select
          v-model="data.config.operatorId"
          placeholder="Please select a building"
          class="el-input-textbox-full-border"
        >
          <el-option
            :label="dateRange.label"
            :value="dateRange.value"
            v-for="(dateRange, index) in getdateOperators()"
            :key="index"
            v-if="dateRange.label !== 'Range'"
          ></el-option>
        </el-select>
      </div>
    </div>
    <span slot="footer" class="dialog-footer row">
      <el-button @click="cancel()" class="col-6">Cancel</el-button>
      <el-button type="primary" @click="update()" class="col-6" v-if="data.edit"
        >Update</el-button
      >
      <el-button type="primary" @click="save()" class="col-6" v-else
        >Confirm</el-button
      >
    </span>
  </el-dialog>
</template>

<script>
import DateHelper from '@/mixins/DateHelper'
export default {
  props: ['visibility', 'cardData', 'type'],
  mixins: [DateHelper],
  data() {
    return {
      pickerVisibility: false,
      quickSearchQuery: '',
      selectedResourceLabel: null,
      data: {
        pmId: null,
        config: {
          operatorId: 22,
          resourceId: null,
        },
      },
      pmlist: [],
      pmResourceList: [],
      pmMap: {},
      pmCreationType: null,
      assignmentType: null,
    }
  },
  mounted() {
    this.loadPmList()
  },
  methods: {
    loadResources() {
      this.pmResourceList = []
      this.data.config.resourceId = null
      let pm = this.pmMap[this.data.pmId]
      this.pmCreationType = pm.pmCreationType
      this.assignmentType = pm.assignmentType
      this.$http
        .get(`/workorder/getMultiplePMResources?pmId=` + this.data.pmId)
        .then(response => {
          this.pmResourceList = response.data.multiPmResources
        })
    },
    loadPmList() {
      let self = this
      this.$http
        .get('/planned/all')
        .then(function(response) {
          self.pmlist = response.data
          if (self.pmlist) {
            self.pmlist.forEach(i => (self.pmMap[i.id] = i))
          }
        })
        .catch(function() {})
    },
    cancel() {
      this.$emit('update:visibility', false)
      this.$emit('close', false)
    },
    save() {
      this.setValues()
      this.$emit('save', this.data)
      this.cancel()
    },
    update() {
      this.setValues()
      this.$emit('update', this.data)
    },
    setValues() {},
  },
}
</script>

<style>
.dashboard-card-dialog .el-dialog__header {
  padding-top: 10px !important;
}

.gauge-setip-dialog {
  height: 85vh !important;
}
</style>
