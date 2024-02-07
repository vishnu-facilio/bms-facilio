<template>
  <div class="text-left">
    <div
      class="p10 fc-report-popover-conetnt"
      v-if="dataPointType === 'reading'"
    >
      <div class="p5">
        <div class="fc-input-label-txt">Type</div>
        <el-select
          v-model="compareObject.type"
          filterable
          :disabled="canDisableTypePicker"
          class="report-dropDown fc-input-full-border2 "
        >
          <el-option label="Space" value="space"></el-option>
          <el-option label="Asset" value="asset"></el-option>
        </el-select>
      </div>
      <div class="p5" v-if="compareObject.type === 'space'">
        <div class="fc-input-label-txt pT10">Space</div>
        <el-select
          v-model="compareObject.parentIds"
          @change="loadReadingsOnly"
          multiple
          filterable
          class="report-dropDown fc-input-full-border2 "
          v-if="multipleParent"
        >
          <el-option
            v-for="(space, index) in spaceList"
            :key="index"
            :label="space.name + ' (' + space.spaceTypeVal + ')'"
            :value="parseInt(space.id)"
          >
          </el-option>
        </el-select>
        <el-select
          v-model="compareObject.spaceId"
          @change="selectSpace"
          filterable
          class="report-dropDown fc-input-full-border2 "
          v-else
        >
          <el-option
            v-for="(space, index) in spaceList"
            :key="index"
            :label="space.name + ' (' + space.spaceTypeVal + ')'"
            :value="parseInt(space.id)"
          >
          </el-option>
        </el-select>
      </div>
      <div class="p5" v-if="compareObject.type === 'asset'">
        <div class="fc-input-label-txt">Asset</div>
        <el-select
          v-model="compareObject.parentIds"
          filterable
          multiple
          @change="loadReadingsOnly"
          class="report-dropDown fc-input-full-border2 "
          v-if="multipleParent"
        >
          <el-option
            v-for="(asset, index) in assetList"
            :key="index"
            :label="asset.name"
            :value="asset.id"
          >
          </el-option>
        </el-select>
        <el-select
          v-model="compareObject.assetId"
          filterable
          @change="selectAsset"
          class="report-dropDown fc-input-full-border2 "
          v-else
        >
          <el-option
            v-for="(asset, index) in assetList"
            :key="index"
            :label="asset.name"
            :value="asset.id"
          >
          </el-option>
        </el-select>
      </div>
      <div class="p5" v-if="newReading">
        <div class="fc-input-label-txt pT10">Reading</div>
        <el-select
          v-model="compareObject.readingFieldId"
          filterable
          @change="setReading"
          class="report-dropDown fc-input-full-border2 "
        >
          <el-option
            v-for="(field, index) in readingFields"
            :key="index"
            :label="field.field.displayName"
            :value="field.field.id"
          >
          </el-option>
        </el-select>
      </div>
      <div class="p5" v-else>
        <div class="fc-input-label-txt">Reading</div>
        <el-select
          v-model="compareObject.readingFieldId"
          filterable
          class="report-dropDown fc-input-full-border2 "
        >
          <el-option
            v-for="(field, index) in readingFields"
            :key="index"
            :label="field.displayName"
            :value="field.id"
          >
          </el-option>
        </el-select>
      </div>
      <div
        class="p5b hide"
        v-if="
          compareObject.readingFieldId &&
            (typeof aggregate === 'undefined' || aggregate !== 0)
        "
        v-bind:class="{ hide: !source }"
      >
        <div class="fc-input-label-txt">Aggregate</div>
        <el-select
          v-model="compareObject.aggregateFunc"
          filterable
          placeholder="Choose Function"
          class="report-dropDown"
          v-tippy
          title="Choose Function"
        >
          <el-option
            v-for="(func, funcidx) in aggregateFunctions"
            v-if="
              func.value > 0 && (func.name !== 'lastValue' || showLastValue)
            "
            :key="funcidx"
            :label="func.label"
            :value="func.value"
          >
          </el-option>
        </el-select>
      </div>
    </div>
    <div class="p10 fc-report-popover-conetnt" v-if="dataPointType === 'meter'">
      <div class="p5">
        <div class="fc-input-label-txt">Building</div>
        <el-select
          v-model="compareObject.buildingId"
          @change="selectBuilding"
          filterable
          class="report-dropDown fc-input-full-border2 "
        >
          <el-option
            v-for="(building, key) in $store.getters.getBuildingsPickList()"
            :key="key"
            :label="building"
            :value="key"
          >
          </el-option>
        </el-select>
      </div>
      <div class="p5">
        <div class="fc-input-label-txt">Meter</div>
        <el-select
          v-model="compareObject.assetId"
          filterable
          @change="selectAsset"
          class="report-dropDown fc-input-full-border2 "
        >
          <el-option
            v-for="(meter, index) in subMeters"
            :key="index"
            :label="meter.name"
            :value="meter.id"
          >
          </el-option>
        </el-select>
      </div>
      <div
        class="p5 hide"
        v-if="
          compareObject.readingFieldId &&
            (typeof aggregate === 'undefined' || aggregate !== 0)
        "
        v-bind:class="{ hide: !source }"
      >
        <div class="fc-input-label-txt">Aggregate</div>
        <el-select
          v-model="compareObject.aggregateFunc"
          filterable
          placeholder="Choose Function"
          class="report-dropDown fc-input-full-border2 "
          v-tippy
          title="Choose Function"
        >
          <el-option
            v-for="(func, funcidx) in aggregateFunctions"
            v-if="
              func.value > 0 && (func.name !== 'lastValue' || showLastValue)
            "
            :key="funcidx"
            :label="func.label"
            :value="parseInt(func.value)"
          >
          </el-option>
        </el-select>
      </div>
    </div>
    <div class="p10 fc-report-popover-conetnt" v-if="dataPointType === 'enpi'">
      <div class="p5">
        <div class="fc-input-label-txt">Building</div>
        <el-select
          v-model="compareObject.buildingId"
          @change="selectBuilding"
          filterable
          class="report-dropDown fc-input-full-border2 "
        >
          <el-option
            v-for="(building, key) in buildingList"
            :key="key"
            :label="building"
            :value="key"
          >
          </el-option>
        </el-select>
      </div>
      <div class="p5">
        <div class="fc-input-label-txt">EnPI</div>
        <el-select
          v-model="compareObject.readingFieldId"
          filterable
          class="report-dropDown fc-input-full-border2 "
        >
          <el-option
            v-for="(field, index) in readingFields"
            :key="index"
            :label="field.readingFields"
            :value="field.id"
          >
          </el-option>
        </el-select>
      </div>
    </div>
    <div class="row mT30">
      <div class="modal-btn-cancel col-6 fc-el-btn" @click="cancel">cancel</div>
      <div class="modal-btn-save fc-el-btn col-6" @click="save">Ok</div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: [
    'type',
    'aggregate',
    'reading',
    'showLastValue',
    'source',
    'newReading',
    'editdata',
    'multipleParent',
    'readingType',
  ],
  data() {
    return {
      compareObject: {
        type: 'space',
        buildingId: '',
        spaceId: '',
        parentIds: [],
        assetId: '',
        readingFieldId: '',
        aggregateFunc: 3,
        hasBaseline: false,
        baseLineId: '',
      },
      canDisableTypePicker: false,
      baselineList: [],
      assetList: [],
      spaceList: [],
      subMeters: [],
      readingFields: [],
      aggregateFunctions: [
        {
          label: 'Actual',
          value: 0,
          name: 'actual',
        },
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
  computed: {
    ...mapGetters(['getBuildingsPickList']),
    buildingList() {
      return this.getBuildingsPickList()
    },
    dataPointType() {
      if (this.type) {
        return this.type
      }
      return 'reading'
    },
    'compareObject.assetId'() {
      return parseInt(this.compareObject.assetId)
    },
  },
  created() {
    this.$store.dispatch('loadEnergyMeters')
    this.$store.dispatch('loadBuildings').then(() => {
      this.init()
    })
  },
  mounted() {},
  methods: {
    init() {
      this.loadSpaces()
      this.loadAssets()
      this.loadBaselines()

      if (this.readingType) {
        this.compareObject.type = this.readingType
        this.canDisableTypePicker = true
      }
    },
    reset() {
      this.compareObject = null
      this.compareObject = {
        type: 'space',
        spaceId: '',
        assetId: '',
        readingFieldId: '',
        aggregateFunc: 0,
        hasBaseline: false,
        baseLineId: '',
      }
    },
    open(options) {
      this.reset()
      if (options) {
        this.compareObject = Object.assign(
          this.compareObject,
          this.compareObject,
          options
        )
        this.compareObject.type = this.compareObject.parentType
        if (this.compareObject.buildingId) {
          this.compareObject.assetId = parseInt(this.compareObject.parentId)
          this.loadSubMeters()
        } else if (this.compareObject.parentType === 'space') {
          if (this.compareObject.parentId) {
            this.compareObject.spaceId = parseInt(this.compareObject.parentId)
          }
        } else {
          if (this.compareObject.parentId) {
            this.compareObject.assetId = parseInt(this.compareObject.parentId)
          }
        }

        if (this.compareObject.readingFieldId) {
          this.loadReadingFields()
        }
      }
      if (this.aggregate === 0) {
        this.compareObject.aggregateFunc = this.aggregate
      } else {
        if (this.compareObject.yAggr) {
          this.compareObject.aggregateFunc = this.compareObject.yAggr
        } else {
          this.compareObject.aggregateFunc = 3
        }
      }
    },
    save() {
      if (this.compareObject.readingFieldId) {
        let reportName = ''
        if (this.compareObject.spaceId) {
          this.compareObject.space = this.spaceList.find(
            a => a.id === this.compareObject.spaceId
          )
          reportName = this.compareObject.space.name
        }
        if (this.compareObject.assetId) {
          this.compareObject.asset = this.assetList.find(
            a => a.id === this.compareObject.assetId
          )
          reportName = this.compareObject.asset.name
        }
        if (this.compareObject.readingFieldId) {
          if (this.newReading) {
            this.compareObject.readingField = this.readingFields.find(
              a => a.field.id === this.compareObject.readingFieldId
            ).field
            if (!this.reading) {
              reportName +=
                ' (' + this.compareObject.readingField.displayName + ')'
            }
          } else {
            this.compareObject.readingField = this.readingFields.find(
              a => a.id === this.compareObject.readingFieldId
            )
            if (!this.reading) {
              reportName +=
                ' (' + this.compareObject.readingField.displayName + ')'
            }
          }
        }
        this.compareObject.name = reportName
        this.compareObject.aggregateFunctions = this.aggregateFunctions
        this.$emit('save', this.compareObject)
      } else {
        this.$message.error('Please select the reading')
      }
    },
    cancel() {
      this.$emit('cancel')
    },
    selectSpace() {
      this.compareObject.readingFieldId = ''
      this.loadReadingFields()
    },
    selectAsset() {
      this.compareObject.readingFieldId = ''
      this.loadReadingFields()
    },
    loadReadingsOnly() {
      this.loadReadingFieldsArray()
    },
    loadAssets() {
      this.loading = true
      let self = this
      this.$util
        .loadAsset({
          withReadings: true,
        })
        .then(response => {
          if (response.assets) {
            this.assetList = response.assets
            if (self.editdata && self.editdata.editmode) {
              if (
                self.$store.getters.getBuildingsPickList()[
                  self.editdata.parentId
                ]
              ) {
                self.compareObject.type = 'space'
                self.compareObject.spaceId = self.editdata.parentId
              } else if (
                this.assetList.find(rt => rt.id === self.editdata.parentId)
              ) {
                self.compareObject.type = 'asset'
                self.compareObject.assetId = self.editdata.parentId
                self.compareObject.readingFieldId = self.editdata.reading
                  ? self.editdata.reading.readingFieldId
                  : self.editdata.readingFieldId
              }
            }
            if (this.compareObject.assetId) {
              this.loadReadingFields()
            }
            this.loading = false
          }
        })
    },
    getAsset(id) {
      return this.assetList.find(obj => obj.id === id)
    },
    loadSpaces() {
      let self = this
      let url = '/basespace'
      this.$http.get(url).then(function(response) {
        if (response.data) {
          self.spaceList = response.data.basespaces
          if (self.compareObject.spaceId) {
            self.loadReadingFields()
          }
        }
      })
    },
    getSpace(id) {
      return this.spaceList.find(obj => obj.id === id)
    },
    loadBaselines() {
      let self = this
      self.$http.get('/baseline/all').then(function(response) {
        if (response.status === 200) {
          self.baselineList = response.data ? response.data : []
        }
      })
    },
    setReading() {},
    loadReadingFieldsArray() {
      let assetId = this.compareObject.parentIds.length
        ? this.compareObject.parentIds[0]
        : null
      let spaceId = this.compareObject.parentIds.length
        ? this.compareObject.parentIds[0]
        : null
      if (this.compareObject.type === 'asset' && assetId) {
        this.$util.loadAssetReadingFields(assetId).then(readingFields => {
          this.readingFields = readingFields
          if (!this.compareObject.readingFieldId) {
            let defaultReadingName = this.reading
              ? this.reading
              : 'TOTAL_ENERGY_CONSUMPTION_DELTA'
            let energyDelta = this.readingFields.find(
              r => r.columnName === defaultReadingName
            )
            if (energyDelta) {
              this.compareObject.readingFieldId = energyDelta.id
            } else {
              this.compareObject.readingFieldId = this.readingFields[0].id
            }
          }
        })
      } else if (this.compareObject.type === 'space' && spaceId) {
        this.$util.loadSpaceReadingFields(spaceId).then(readingFields => {
          this.readingFields =
            this.type !== 'enpi'
              ? readingFields
              : readingFields.filter(type => type.typeEnum._name === 'ENPI')
          if (!this.compareObject.readingFieldId && this.readingFields.length) {
            this.compareObject.readingFieldId = this.readingFields[0].id
          }
        })
      }
    },
    loadReadingFields() {
      let self = this
      if (this.newReading) {
        if (this.compareObject.type === 'asset' && this.compareObject.assetId) {
          this.$util
            .loadLatestReading(this.compareObject.assetId)
            .then(readingFields => {
              self.readingFields = readingFields
              let defaultReadingName = self.reading
                ? self.reading
                : 'TOTAL_ENERGY_CONSUMPTION_DELTA'
              let energyDelta = self.readingFields.find(
                r => r.field.columnName === defaultReadingName
              )
                ? self.readingFields.find(
                    r => r.field.columnName === defaultReadingName
                  ).field
                : []
              energyDelta.readingType = self.readingFields.find(
                r => r.field.columnName === defaultReadingName
              )
                ? self.readingFields.find(
                    r => r.field.columnName === defaultReadingName
                  ).readingType
                : -1
              if (energyDelta) {
                if (isEmpty(self.compareObject.readingFieldId)) {
                  self.compareObject.readingFieldId = energyDelta.id
                }
                self.compareObject.fullList = self.$helpers.cloneObject(
                  readingFields
                )
                // if (self.editdata && self.editdata.editmode) {
                //   self.save()
                // }
              } else {
                if (isEmpty(self.compareObject.readingFieldId)) {
                  self.compareObject.readingFieldId =
                    self.readingFields[0].field.id
                }
              }
            })
        } else {
          if (
            self.compareObject.type === 'space' &&
            self.compareObject.spaceId
          ) {
            self.$util
              .loadLatestReading(self.compareObject.spaceId)
              .then(readingFields => {
                self.readingFields =
                  self.type !== 'enpi'
                    ? readingFields
                    : readingFields.filter(
                        type => type.field.dataTypeEnum === 'ENPI'
                      )
                // if (self.editdata && self.editdata.editmode) {
                //   self.save()
                // }
                if (
                  !self.compareObject.readingFieldId &&
                  self.readingFields.length
                ) {
                  self.compareObject.readingFieldId =
                    self.readingFields[0].field.id
                }
              })
          }
        }
      } else {
        if (this.compareObject.type === 'asset' && this.compareObject.assetId) {
          this.$util
            .loadAssetReadingFields(this.compareObject.assetId)
            .then(readingFields => {
              this.readingFields = readingFields
              if (!this.compareObject.readingFieldId) {
                let defaultReadingName = this.reading
                  ? this.reading
                  : 'TOTAL_ENERGY_CONSUMPTION_DELTA'
                let energyDelta = this.readingFields.find(
                  r => r.columnName === defaultReadingName
                )
                if (energyDelta) {
                  this.compareObject.readingFieldId = energyDelta.id
                } else {
                  this.compareObject.readingFieldId = this.readingFields[0].id
                }
              }
            })
        } else if (
          this.compareObject.type === 'space' &&
          this.compareObject.spaceId
        ) {
          this.$util
            .loadSpaceReadingFields(this.compareObject.spaceId)
            .then(readingFields => {
              this.readingFields =
                this.type !== 'enpi'
                  ? readingFields
                  : readingFields.filter(type => type.typeEnum._name === 'ENPI')
              if (
                !this.compareObject.readingFieldId &&
                this.readingFields.length
              ) {
                this.compareObject.readingFieldId = this.readingFields[0].id
              }
            })
        }
      }
    },
    selectBuilding() {
      if (this.type === 'meter') {
        this.compareObject.type = 'asset'
        this.loadSubMeters()
      } else if (this.type === 'enpi') {
        this.compareObject.type = 'space'
        this.compareObject.spaceId = parseInt(this.compareObject.buildingId)
        this.loadReadingFields()
      }
    },
    loadSubMeters() {
      let self = this
      let url =
        '/dashboard/getSubMeters?buildingId=' + this.compareObject.buildingId
      self.$http.get(url).then(function(response) {
        let energyAssets = response.data.energyMeters
        let subMeterList = []
        if (energyAssets && self.assetList) {
          for (let ea of energyAssets) {
            let asset = self.assetList.find(aa => aa.id === ea.id)
            if (asset) {
              subMeterList.push(ea)
            }
          }
        } else {
          subMeterList = energyAssets
        }
        self.subMeters = subMeterList
        if (self.compareObject.assetId) {
          self.loadReadingFields()
        }
      })
    },
  },
}
</script>

<style></style>
