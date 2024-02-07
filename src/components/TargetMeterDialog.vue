<template>
  <el-dialog
    :title="keyToName[data.key]"
    :modal-append-to-body="false"
    custom-class="building-dialog meter-dialog-page fc-dialog-header-hide text-center"
    :visible.sync="visibility"
    width="90%"
    :before-close="cancel"
  >
    <!-- Change chart -->
    <div class="meter-dialog-body" v-if="data.template === 0">
      <div class="width100 tr-header ">
        <div class="fL pointer" @click="cancel">
          <i class="el-icon-arrow-left pL5"></i>Back
        </div>
        <div class="fR pointer" @click="cancel">
          <i class="el-icon-close"></i>
        </div>
      </div>
      <div class="fc-black-com f14 fwBold pB20 header-new-1">
        Choose Target meter style
      </div>
      <div class="graybg"></div>
      <div class="meter-dialog-body-bg">
        <div class="flex-middle p20">
          <div class="width100">
            <div
              class="meter-card-bg mR10 active-border"
              @click="data.template = 1"
            >
              <!-- <div class="meter-select-icon">
              <i class="el-icon-success"></i>
              </div> -->
              <div class="text-center">
                <img src="~src/statics/cardIcon/group1.png" />
              </div>
            </div>
            <div class="label-txt-black pB10 text-center pT10 pR10">
              Dial Gauge
            </div>
          </div>
          <div class="width100">
            <div class="meter-card-bg mL10" @click="data.template = 1">
              <!-- <div class="meter-select-icon">
                <i class="el-icon-success"></i>
              </div> -->
              <div class="text-center">
                <img src="~src/statics/cardIcon/group2.png" />
              </div>
            </div>
            <div class="label-txt-black pB10 text-center pT10 pL10">
              Traffic Lights
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- second section -->
    <div v-if="data.template === 1" class="chart-transition">
      <!-- <div class="width100 tr-header">
            <div class="fL pointer" @click="data.template = 0"><i class="el-icon-arrow-left pL5"></i>Back</div>
            <div class="fR pointer" @click="cancel"><i class="el-icon-close"></i></div>
        </div>
        <div class="fc-black-com f14 fwBold pB40 text-uppercase pointer">
        </div> -->
      <el-row class="target-meter-body-conatiner">
        <el-col :span="16" class="pL40">
          <div>
            <div class="reading-card-header fc-input-label-txt">
              Title
            </div>
            <el-input
              placeholder="Title"
              v-model="data.title"
              class="fc-input-full-border2 pT10"
            ></el-input>
          </div>
          <div class="reading-card-container pT20">
            <div class="reading-card-header fc-input-label-txt">
              Add Reading
            </div>
            <el-popover
              placement="right"
              width="300"
              popper-class="select-card-reading  fc-popover-p0"
              v-model="addreadingVisible"
            >
              <f-add-data-point
                :showLastValue="true"
                ref="addDataPointForm"
                @save="getBaseDatapoint"
                @cancel="cancelDataPoint"
                :source="'dashboardedit'"
              ></f-add-data-point>
              <el-input
                slot="reference"
                :autofocus="true"
                class="addReading-title fc-input-full-border2"
                v-model="data.selectedReading1"
                :placeholder="'Click to add Reading'"
                :disabled="true"
              ></el-input>
            </el-popover>
          </div>
          <div class="reading-card-container pT20">
            <div class="reading-card-header fc-input-label-txt">
              Add Target Reading
            </div>
            <div class="">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-select
                    v-model="data.targetmode"
                    placeholder="Select the option"
                    class="fc-input-full-border2 width100"
                  >
                    <el-option
                      label="Set constant"
                      value="constant"
                    ></el-option>
                    <el-option label="Reading" value="reading"></el-option>
                  </el-select>
                </el-col>
                <el-col :span="12">
                  <el-popover
                    placement="right"
                    width="300"
                    popper-class="select-card-reading  fc-popover-p0"
                    v-model="addreadingVisible2"
                    v-if="data.targetmode === 'reading'"
                  >
                    <f-add-data-point
                      :showLastValue="true"
                      ref="addDataPointForm1"
                      @save="getTragetDatapoint"
                      @cancel="cancelDataPoint"
                      :source="'dashboardedit'"
                      :newReading="true"
                    ></f-add-data-point>
                    <el-input
                      slot="reference"
                      :autofocus="true"
                      class="width100 addReading-title fc-input-full-border2"
                      v-model="data.selectedReading2"
                      :placeholder="'Click to add target reading'"
                      :disabled="true"
                    ></el-input>
                  </el-popover>
                  <el-input
                    v-if="data.targetmode === 'constant'"
                    :autofocus="true"
                    class="addReading-title fc-input-full-border2 width100"
                    v-model="data.targetConstant"
                    :placeholder="'Enter only traget value'"
                  ></el-input>
                </el-col>
              </el-row>
            </div>
          </div>
          <el-row class="pT20" :gutter="20">
            <el-col :span="12">
              <div class="reading-card-header fc-input-label-txt">
                Period
              </div>
              <el-select
                v-model="data.operatorId"
                placeholder="Please select a period"
                class="fc-input-full-border2 width100"
              >
                <el-option
                  :label="dateRange.label"
                  :value="dateRange.value"
                  v-for="(dateRange, index) in getdateOperators()"
                  :key="index"
                  v-if="dateRange.label !== 'Range'"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="12">
              <div class="reading-card-header fc-input-label-txt">
                Aggregation
              </div>
              <el-select
                v-model="data.aggregation"
                placeholder="Please select a aggregation"
                class="fc-input-full-border2 width100"
              >
                <el-option
                  :label="agg.label"
                  :value="agg.name"
                  v-for="(agg, index) in aggregateFunctions"
                  :key="index"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
        </el-col>
        <el-col :span="8">
          <div class="text-center">
            <img src="~src/statics/cardIcon/group1.png" style="width: 180px;" />
          </div>
          <!-- <div class="fc-blue-txt-14 pT10 text-center pointer" @click="data.template = 0">
                    Change template
                </div> -->
        </el-col>
      </el-row>
    </div>
    <div
      slot="footer"
      class="dialog-footer row modal-dialog-footer"
      v-if="data.template !== 0"
    >
      <el-button @click="cancel()" class="modal-btn-cancel">Cancel</el-button>
      <el-button
        type="primary"
        @click="update()"
        class="modal-btn-save"
        v-if="data.edit"
        >Update</el-button
      >
      <el-button type="primary" @click="save()" class="modal-btn-save" v-else
        >Confirm</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import DateHelper from '@/mixins/DateHelper'
import NewDateHelper from '@/mixins/NewDateHelper'
import FAddDataPoint from 'pages/energy/analytics/components/FAddDataPointNew'
export default {
  props: ['visibility', 'cardData', 'buildings', 'type'],
  mixins: [DateHelper],
  data() {
    return {
      data: {
        key: '',
        title: '',
        parentType: 'asset',
        template: 1,
        parentId1: null,
        fieldName1: null,
        fieldObj1: null,
        fieldObj2: null,
        parentId2: null,
        fieldName2: null,
        selectedReading1: null,
        selectedReading2: null,
        targetConstant: 1,
        targetmode: 'constant',
        edit: false,
        operatorId: 28,
        aggregation: 'sum',
        dateFilter: null,
        building: {},
        expressions: [],
      },
      addreadingVisible: false,
      addreadingVisible2: false,
      readingingfields: {},
      assets: [],
      buildingVsAssets: {},
      readings: [],
      keyToName: {
        profilemini: 'Building Energy',
        energycostmini: 'Building Energy Cost',
        carbonmini: 'Carbon Emission',
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
  components: {
    FAddDataPoint,
  },
  created() {
    this.$store.dispatch('loadBuildings')
  },
  mounted() {
    if (this.visibility) {
      if (this.cardData) {
        this.data = this.cardData
      }
      this.loadDefaults()
      this.loadReadingFields()
    }
  },
  methods: {
    loadDefaults() {
      this.data.key = this.type || ''
    },
    back() {
      this.$router.go(-1)
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
      if (this.data && this.data.targetmode === 'constant') {
        string =
          'Map test(){  date = new NameSpace("date");  period = date.getDateRange("' +
          this.data.dateFilter.queryStr +
          '"); db = {   criteria : [parentId == ' +
          this.data.parentId1 +
          ' &&  ttime == period],   field : "' +
          this.data.fieldName1 +
          '",   aggregation : "' +
          this.data.aggregation +
          '" };   baseValue = Module("' +
          (this.data.fieldObj1 &&
          this.data.fieldObj1.module &&
          this.data.fieldObj1.module.name
            ? this.data.fieldObj1.module.name
            : 'energydata') +
          '").fetch(db); data = {}; data["baseValue"] = baseValue;data["targetValue"] = ' +
          this.data.targetConstant +
          ';fieldObj = new NameSpace("module").getField("' +
          this.data.fieldName1 +
          '","' +
          (this.data.fieldObj1 &&
          this.data.fieldObj1.module &&
          this.data.fieldObj1.module.name
            ? this.data.fieldObj1.module.name
            : 'energydata') +
          '");  data["fieldObj"] = null;	if (fieldObj != null) {   data["fieldObj"] = fieldObj;}return data;}'
      } else {
        string =
          'Map test(){  date = new NameSpace("date");  period = date.getDateRange("' +
          this.data.dateFilter.queryStr +
          '"); db = {   criteria : [parentId == ' +
          this.data.parentId1 +
          ' &&  ttime == period],   field : "' +
          this.data.fieldName1 +
          '",   aggregation : "' +
          this.data.aggregation +
          '" };   db1 = {   criteria : [parentId == ' +
          this.data.parentId2 +
          ' &&  ttime == period],   field : "' +
          this.data.fieldName2 +
          '",   aggregation : "' +
          this.data.aggregation +
          '" }   baseValue =  Module("' +
          (this.data.fieldObj1 &&
          this.data.fieldObj1.module &&
          this.data.fieldObj1.module.name
            ? this.data.fieldObj1.module.name
            : 'energydata') +
          '"); targetValue =  Module("' +
          (this.data.fieldObj2 &&
          this.data.fieldObj2.module &&
          this.data.fieldObj2.module.name
            ? this.data.fieldObj2.module.name
            : 'energydata') +
          '").fetch(db1); data = {}; data["baseValue"] = baseValue;data["targetValue"] = targetValue;fieldObj = new NameSpace("module").getField("' +
          this.data.fieldName1 +
          '","' +
          (this.data.fieldObj1 &&
          this.data.fieldObj1.module &&
          this.data.fieldObj1.module.name
            ? this.data.fieldObj1.module.name
            : 'energydata') +
          '");  data["fieldObj"] = null;	if (fieldObj != null) {   data["fieldObj"] = fieldObj;}return data;}'
      }
      return string
    },
    setValues() {
      this.data.building = this.buildings.find(
        rt => rt.id === this.data.selectedBuilding
      )
      this.data.dateFilter = NewDateHelper.getDatePickerObject(
        this.data.operatorId
      )
      this.data.dateFilter['queryStr'] = this.getdateOperators().find(
        rt => rt.value === this.data.operatorId
      ).queryStr
      this.data.workflowV2String = this.getworkFlowString()
    },
    cancelDataPoint(data) {
      this.addreadingVisible = false
    },
    getBaseDatapoint(data) {
      if (data) {
        if (data.type === 'asset') {
          this.data.parentId1 = data.assetId
          this.data.fieldName1 = data.readingField.name
          this.data.fieldObj1 = data.readingField
          this.data.selectedReading1 = data.name
        } else {
          this.data.parentId1 = data.spaceId
          this.data.fieldName1 = data.readingField.name
          this.data.fieldObj1 = data.readingField
          this.data.selectedReading1 = data.name
        }
      }
      this.addreadingVisible = false
    },
    getTragetDatapoint(data) {
      if (data) {
        if (data.type === 'asset') {
          this.data.parentId2 = data.assetId
          this.data.fieldName2 = data.readingField.name
          this.data.fieldObj2 = data.readingField
          this.data.selectedReading2 = data.name
        } else {
          this.data.parentId2 = data.spaceId
          this.data.fieldName2 = data.readingField.name
          this.data.fieldObj2 = data.readingField
          this.data.selectedReading2 = data.name
        }
      }
      this.addreadingVisible2 = false
    },
    loadAssetFields() {
      let assetId = this.data.parentId
      this.$util.loadAssetReadingFields(assetId).then(response => {
        this.readings = response
      })
    },
    loadReadingFields() {
      let self = this
      self.$http.get('/asset/getreadings').then(function(response) {
        if (response) {
          self.readingingfields = response.data
        }
      })
    },
  },
}
</script>

<style lang="scss">
.meter-dialog-page {
  .meter-dialog-body {
    height: 400px;
    -webkit-transition: all 0.4s ease-in-out;
    transition: all 0.4s ease-in-out;
  }

  .meter-dialog-body-bg {
    background: transparent;
    height: 320px;
    justify-items: center;
    align-items: center;
    margin: auto;
    width: 600px;
    left: 25%;
    position: absolute;
    z-index: 10;
    top: 116px;
  }

  .meter-card-bg {
    background: #fff;
    border-radius: 4px;
    cursor: pointer;
    border-radius: 4px;
  }

  .meter-card-bg img {
    width: 270px;
    height: 216px;
    box-shadow: 0 8px 12px 0 rgba(215, 215, 215, 0.5);
  }

  .el-dialog__body {
    height: 80vh !important;
    padding-top: 20px;
  }

  .active-border > img,
  .meter-card-bg img:hover {
    border: 2px solid #32c5ff;
  }

  .meter-dialog-body,
  .chart-transition {
    transition: all 0.4s ease-in-out;
    -webkit-transition: all 0.4s ease-in-out;
    -moz-transition: all 0.4s ease-in-out;
  }

  .graybg {
    background: #fff;
    width: 100%;
    height: 50%;
    position: absolute;
    z-index: 1;
    top: 50%;
    left: 0px;
    bottom: 30px !important;
  }
}
</style>
<style>
.meter-dialog-page .el-dialog__body {
  /* background: #2e2e49; */
}

.header-new-1 {
  font-size: 28px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: justify;
  color: #ffffff;
  text-align: center;
}
.target-meter-body-conatiner {
  background: #fff;
  margin-left: -20px;
  margin-right: -20px;
  height: calc(85vh - 100px);
  padding-bottom: 20px;
  padding-top: 20px;
}
.target-meter-body-conatiner .reading-card-header {
  text-align: left;
}
.tr-header {
  color: #fff;
}
</style>
