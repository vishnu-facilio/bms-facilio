<template>
  <el-dialog
    :title="'Gauge Setup'"
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
        <div class="reading-card-header fc-input-label-txt pT20">Name</div>
        <el-input
          placeholder="Name"
          v-model="data.title"
          class="fc-input-full-border2"
        ></el-input>
      </div>
      <div>
        <div class="reading-card-header fc-input-label-txt pT10">Asset</div>
        <el-input
          v-model="selectedResourceLabel"
          :disabled="true"
          type="text"
          :placeholder="$t('common._common.to_search_type')"
          class="fc-input-full-border-select2"
        >
          <i
            @click="pickerVisibility = true"
            slot="suffix"
            style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
            class="el-input__icon el-icon-search"
          ></i>
        </el-input>
      </div>
      <div>
        <div class="reading-card-header fc-input-label-txt pT20">Reading</div>
        <el-select
          v-model="data.buildings.selectedReading"
          placeholder="Please select a Field"
          class="fc-input-full-border2"
          filterable
          no-data-text="Loading..."
        >
          <el-option
            :label="reading.displayName"
            :value="reading.name"
            v-for="(reading, index) in readings"
            :key="index"
          ></el-option>
        </el-select>
      </div>
      <div>
        <div class="reading-card-header fc-input-label-txt pT20">Period</div>
        <el-select
          v-model="data.operatorId"
          placeholder="Please select a period"
          class="fc-input-full-border2"
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
      <div>
        <div class="reading-card-header fc-input-label-txt pT20">Min value</div>
        <el-input
          placeholder="Min value"
          v-model="data.min"
          class="fc-input-full-border2"
        ></el-input>
      </div>
      <div>
        <div class="reading-card-header fc-input-label-txt pT20">
          Max valuer
        </div>
        <el-input
          placeholder="Max Value"
          v-model="data.max"
          class="fc-input-full-border2"
        ></el-input>
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
    <space-asset-chooser
      @associate="associate($event)"
      :resourceType="[2]"
      :visibility.sync="pickerVisibility"
      :query="quickSearchQuery"
      picktype="'asset'"
      :showAsset="true"
      :appendToBody="true"
      :closeOnClickModel="true"
      :filter="filter"
    ></space-asset-chooser>
  </el-dialog>
</template>
<script>
import DateHelper from '@/mixins/DateHelper'
import NewDateHelper from '@/mixins/NewDateHelper'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import ControlMixinHelper from '@/mixins/ControlCommandMixin'

import { mapState } from 'vuex'
export default {
  props: ['visibility', 'cardData', 'type'],
  mixins: [DateHelper, ControlMixinHelper],
  data() {
    return {
      pickerVisibility: false,
      quickSearchQuery: '',
      selectedResourceLabel: null,
      data: {
        key: '',
        title: '',
        edit: false,
        operatorId: 28,
        aggregation: 'sum',
        gaugeMeta: {
          minorTicks: 2,
          majorTicks: [0, 200, 400, 600, 800, 1000],
          highlights: [
            {
              from: 0,
              to: 350,
              color: '#ffb2a6',
            },
            {
              from: 350,
              to: 800,
              color: '#c5fdc0',
            },
            {
              from: 800,
              to: 1000,
              color: '#ffffc6',
            },
          ],
        },
        fieldObj1: null,
        dateFilter: null,
        min: null,
        max: null,
        buildings: {
          selectedBuilding: null,
          selectedAsset: null,
          selectedReading: null,
          readingField: null,
          assetdetails: null,
        },
        building: {},
        expressions: [],
      },
      assets: [],
      buildingVsAssets: {},
      readings: [],
      keyToName: {
        profilemini: 'Energy',
        energycostmini: 'Energy Cost',
        carbonmini: 'Carbon Emission',
        controlCommandmini: 'Control Command',
      },
      aggregateFunctions: [
        {
          label: 'Sum',
          value: 3,
          name: 'sum',
        },
        {
          label: 'Avg',
          value: 2,
          name: 'avg',
        },
        {
          label: 'Min',
          value: 4,
          name: 'min',
        },
        {
          label: 'Max',
          value: 5,
          name: 'max',
        },
        {
          label: 'Current Value',
          value: 6,
          name: 'lastValue',
        },
      ],
    }
  },
  mounted() {},
  components: {
    SpaceAssetChooser,
  },
  created() {
    if (this.visibility) {
      Promise.all([
        this.$store.dispatch('loadSites'),
        this.$store.dispatch('loadBuildings'),
      ]).then(() => {
        this.getControlableAssets()
        if (this.cardData) {
          this.data = this.cardData
        }
        this.loadDefaults()
        this.loadReadingFields()
      })
    }
  },
  computed: {
    ...mapState({
      sites: state => state.sites,
      buildings: state => state.buildings,
    }),
    controlableAssets() {
      return this.getControlableAssets()
    },
    filter() {},
  },
  methods: {
    associate(selectedObj) {
      // this.confirmRequest.resource = selectedObj
      this.data.buildings.selectedAsset = selectedObj.id
      this.loadAssetFields()
      this.selectedResourceLabel = selectedObj.name
      this.pickerVisibility = false
      this.$forceUpdate()
    },
    loadDefaults() {
      if (this.data.buildings.selectedAsset) {
        this.loadAssetFields()
      }
      this.data.key = this.type || ''
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
    getworkFlowString() {
      let string = ''
      string = `Map test(){  date = new NameSpace("date");
           period = date.getDateRange("${this.data.dateFilter.queryStr}");
           db = {   criteria : [parentId == ${this.data.buildings.selectedAsset} &&  ttime == period],   field : "${this.data.buildings.selectedReading}",   aggregation : "sum" };
              db1 = {   criteria : [parentId == ${this.data.buildings.selectedAsset} &&  ttime == period],   field : "${this.data.buildings.selectedReading}",   aggregation : "min" };
              db2 = {   criteria : [parentId == ${this.data.buildings.selectedAsset} &&  ttime == period],   field : "${this.data.buildings.selectedReading}",   aggregation : "max" };
              db3 = {   criteria : [parentId == ${this.data.buildings.selectedAsset} &&  ttime == period],   field : "${this.data.buildings.selectedReading}",   aggregation : "avg" };
              db4 = {   criteria : [parentId == ${this.data.buildings.selectedAsset} &&  ttime == period],   field : "${this.data.buildings.selectedReading}",   aggregation : "lastValue" };
           sumvalue = Module("${this.data.fieldObj1.module.name}").fetch(db);
           minvalue = Module("${this.data.fieldObj1.module.name}").fetch(db1);
           maxvalue = Module("${this.data.fieldObj1.module.name}").fetch(db2);
           avgvalue = Module("${this.data.fieldObj1.module.name}").fetch(db3);
           lastvalue = Module("${this.data.fieldObj1.module.name}").fetch(db4);
           data = {};
           data["sum"] = sumvalue;
            data["min"] = minvalue;
           data["max"] = maxvalue;
           data["avg"] = avgvalue;
           data["lastValue"] = lastvalue;

           fieldObj = new NameSpace("module").getField("${this.data.buildings.selectedReading}","${this.data.fieldObj1.module.name}");
           data["fieldObj"] = null;
           if (fieldObj != null) { data["fieldObj"] = fieldObj;}
           data["minValue"] = ${this.data.min};
          data["maxValue"] = ${this.data.max};
           return data;
          }`
      return string
    },
    setValues() {
      this.data.fieldObj1 = this.readings.find(
        rt => rt.name === this.data.buildings.selectedReading
      )
      this.data.building = [...this.buildings, ...this.sites].find(
        rt => rt.id === this.data.buildings.selectedBuilding
      )
      this.data.dateFilter = NewDateHelper.getDatePickerObject(
        this.data.operatorId
      )
      this.data.dateFilter['queryStr'] = this.getdateOperators().find(
        rt => rt.value === this.data.operatorId
      ).queryStr
      this.data.workflowV2String = this.getworkFlowString()
    },
    loadAssetFields() {
      let assetId = this.data.buildings.selectedAsset
      this.$util.loadAssetReadingFields(assetId).then(response => {
        this.readings = response
      })
    },
    loadReadingFields() {
      let self = this
      self.$http.get('/asset/getreadings').then(function(response) {
        self.assets =
          response && response.data && response.data.assets
            ? response.data.assets
            : {}
      })
    },
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
