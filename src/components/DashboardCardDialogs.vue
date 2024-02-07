<template>
  <el-dialog
    :title="keyToName[data.key]"
    :append-to-body="true"
    :custom-class="{
      'building-dialog': true,
      'select-building-dialog': true,
      'fc-dialog-center-container': true,
      'dashboard-card-dialog': true,
      'control-command-card-dialog':
        data.key && data.key === 'controlCommandmini',
    }"
    :visible.sync="visibility"
    width="30%"
    :before-close="cancel"
  >
    <div v-if="data.key && data.key === 'controlCommandmini'">
      <contol-command-dialog
        :modelData.sync="data.buildings"
        v-show="data.key === 'controlCommandmini'"
      ></contol-command-dialog>
    </div>
    <div v-else>
      <div v-if="data.key && data.key === 'profilemini'">
        <div class="reading-card-header fc-input-label-txt">Image Field</div>
        <el-select
          v-model="data.buildings.selectedBuilding"
          placeholder="Please select a building"
          class="fc-input-full-border2"
          filterable
        >
          <el-option-group label="Sites">
            <el-option
              v-for="(site, idx) in sites"
              :key="idx"
              :label="site.name"
              :value="site.id"
            ></el-option>
          </el-option-group>
          <el-option-group label="Buildings">
            <el-option
              v-for="(building, index) in buildings"
              :key="index"
              :label="building.name"
              :value="building.id"
            ></el-option>
          </el-option-group>
        </el-select>
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
          @change="setModulename(data.buildings.selectedReading)"
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
        <div class="reading-card-header fc-input-label-txt pT20">
          Aggregation
        </div>
        <el-select
          v-model="data.aggregation"
          placeholder="Please select a aggregation"
          class="fc-input-full-border2"
        >
          <el-option
            :label="agg.label"
            :value="agg.name"
            v-for="(agg, index) in aggregateFunctions"
            :key="index"
          ></el-option>
        </el-select>
      </div>
      <div v-if="data.key && data.key === 'energycostmini'">
        <div class="reading-card-header fc-input-label-txt pT20">
          Cost - Multiplication factor
        </div>
        <el-input
          placeholder="cost"
          v-model="data.cost"
          class="fc-input-full-border2"
        ></el-input>
        <div class="fc-grey2 f11 pT5">* (Multiplication factor * Reading )</div>
      </div>
      <div v-if="data.key && data.key === 'carbonmini'">
        <div class="reading-card-header fc-input-label-txt pT20">
          Carbon - Multiplication factor
        </div>
        <el-input
          placeholder="cost"
          v-model="data.carbon"
          class="fc-input-full-border2"
        ></el-input>
        <div class="fc-grey2 f11 pT5">* (Multiplication factor * Reading )</div>
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
import ContolCommandDialog from '@/ControlCommandDialog'
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
        edit: false,
        operatorId: 28,
        aggregation: 'sum',
        dateFilter: null,
        cost: 1,
        carbon: 1,
        buildings: {
          selectedBuilding: null,
          selectedAsset: null,
          selectedReading: null,
          SelectedModuleName: null,
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
    ContolCommandDialog,
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
    setModulename(moduleName) {
      let reading = this.readings.find(rt => rt.name === moduleName)
      this.data.buildings.SelectedModuleName =
        reading.module && reading.module.name
          ? reading.module.name
          : 'energydata'
    },
    associate(selectedObj) {
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
      if (this.validate()) {
        this.setValues()
        this.$emit('save', this.data)
        this.cancel()
      }
    },
    validate() {
      if (
        this.data.key &&
        this.data.key === 'profilemini' &&
        !this.data.buildings.selectedBuilding
      ) {
        this.$message.error('Select image Field')
        return false
      }
      if (!this.data.buildings.selectedAsset) {
        this.$message.error('Select asset details')
        return false
      }
      if (!this.data.buildings.selectedReading) {
        this.$message.error('Select readings details')
        return false
      }
      return true
    },
    update() {
      if (this.validate()) {
        this.setValues()
        this.$emit('update', this.data)
      }
    },
    getworkFlowString() {
      let string = ''
      if (this.data.key === 'energycostmini') {
        string =
          'Map test(){  date = new NameSpace("date");    period = date.getDateRange("' +
          this.data.dateFilter.queryStr +
          '");    lastMonth = date.getDateRange("Last Month"); db = {   criteria : [parentId == ' +
          this.data.buildings.selectedAsset +
          ' &&  ttime == period],   field : "' +
          this.data.buildings.selectedReading +
          '",   aggregation : "' +
          this.data.aggregation +
          '" };   db1 = {   criteria : [parentId == ' +
          this.data.buildings.selectedAsset +
          ' && ttime == lastMonth],   field : "' +
          this.data.buildings.selectedReading +
          '",   aggregation : "' +
          this.data.aggregation +
          '" };  thisMonthEnergy = Module("' +
          this.data.buildings.SelectedModuleName +
          '").fetch(db); if (thisMonthEnergy == null) {thisMonthEnergy = 0;} lastEnergy = Module("' +
          this.data.buildings.SelectedModuleName +
          '").fetch(db1);  data = {}; cost = (thisMonthEnergy * ' +
          this.data.cost +
          ');data["currentVal"] = cost;data["lastVal"] = lastEnergy;return data;}'
      } else if (this.data.key === 'carbonmini') {
        string =
          'Map test(){  date = new NameSpace("date");    period = date.getDateRange("' +
          this.data.dateFilter.queryStr +
          '");     lastMonth = date.getDateRange("Last Month"); db = {   criteria : [parentId == ' +
          this.data.buildings.selectedAsset +
          ' &&  ttime == period],   field : "' +
          this.data.buildings.selectedReading +
          '",   aggregation : "' +
          this.data.aggregation +
          '" };   db1 = {   criteria : [parentId == ' +
          this.data.buildings.selectedAsset +
          ' && ttime == lastMonth],   field : "' +
          this.data.buildings.selectedReading +
          '",   aggregation : "' +
          this.data.aggregation +
          '" };   thisMonthEnergy = Module("' +
          this.data.buildings.SelectedModuleName +
          '").fetch(db); if (thisMonthEnergy == null) {thisMonthEnergy = 0;} lastEnergy = Module("' +
          this.data.buildings.SelectedModuleName +
          '").fetch(db1); data = {}; cost = (thisMonthEnergy * ' +
          this.data.carbon +
          ');data["currentVal"] = cost;data["lastVal"] = lastEnergy;return data;}'
      } else if (this.data.key === 'controlCommandmini') {
        // comso
        let moduleName = this.data.buildings.readingField[0].module.name
        string =
          'Map test(){   date = new NameSpace("date");    period = date.getDateRange("' +
          this.data.dateFilter.queryStr +
          '");    lastMonth = date.getDateRange("Last Month"); db = {   criteria : [parentId == ' +
          this.data.buildings.selectedAsset +
          ' &&  ttime == period],   field : "' +
          this.data.buildings.selectedReading +
          '",   aggregation : "' +
          'lastValue' +
          '" };   db1 = {   criteria : [parentId == ' +
          this.data.buildings.selectedAsset +
          ' && ttime == lastMonth],   field : "' +
          this.data.buildings.selectedReading +
          '",   aggregation : "' +
          this.data.aggregation +
          '" };   thisMonthEnergy = Module("' +
          moduleName +
          '").fetch(db); if (thisMonthEnergy == null) {thisMonthEnergy = 0;} fieldObj = new NameSpace("module").getField("' +
          this.data.buildings.selectedReading +
          '", "' +
          moduleName +
          '"); lastEnergy = Module("' +
          moduleName +
          '").fetch(db1); readingField = ' +
          this.data.buildings.readingField[0].id +
          '; parentId =' +
          this.data.buildings.selectedAsset +
          ';data = {}; data["currentVal"] = thisMonthEnergy;data["readingField"]= readingField;data["fieldObj"]=fieldObj;data["parentId"]= parentId ;data["lastVal"] = lastEnergy;return data;}'
      } else {
        string =
          'Map test(){  date = new NameSpace("date");    period = date.getDateRange("' +
          this.data.dateFilter.queryStr +
          '");    lastMonth = date.getDateRange("Last Month"); db = {   criteria : [parentId == ' +
          this.data.buildings.selectedAsset +
          ' &&  ttime == period],   field : "' +
          this.data.buildings.selectedReading +
          '",   aggregation : "' +
          this.data.aggregation +
          '" };   db1 = {   criteria : [parentId == ' +
          this.data.buildings.selectedAsset +
          ' && ttime == lastMonth],   field : "' +
          this.data.buildings.selectedReading +
          '",   aggregation : "' +
          this.data.aggregation +
          '" };   thisMonthEnergy = Module("' +
          this.data.buildings.SelectedModuleName +
          '").fetch(db); if (thisMonthEnergy == null) {thisMonthEnergy = 0;} lastEnergy = Module("' +
          this.data.buildings.SelectedModuleName +
          '").fetch(db1); data = {}; data["currentVal"] = thisMonthEnergy;data["lastVal"] = lastEnergy;return data;}'
      }
      return string
    },
    setValues() {
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
.dashboard-card-dialog .el-dialog__body {
  height: 530px !important;
}
.control-command-card-dialog .el-dialog__body {
  height: 275px !important;
}
</style>
