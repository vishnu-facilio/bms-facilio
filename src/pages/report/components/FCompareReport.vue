<template>
  <div class="text-left">
    <div class="p10 fc-report-popover-conetnt">
      <div class="p5">
        <div class="header">Type</div>
        <el-select
          v-model="compareObject.type"
          filterable
          class="report-dropDown"
        >
          <el-option label="Space" value="space"></el-option>
          <el-option label="Asset" value="asset"></el-option>
        </el-select>
      </div>
      <div class="p5" v-if="compareObject.type === 'space'">
        <div class="header">Space</div>
        <el-select
          v-model="compareObject.spaceId"
          @change="selectSpace"
          filterable
          class="report-dropDown"
        >
          <el-option
            v-for="(space, index) in spaceList"
            :key="index"
            :label="space.name + ' (' + space.spaceTypeVal + ')'"
            :value="space.id"
          >
          </el-option>
        </el-select>
      </div>
      <div class="p5" v-if="compareObject.type === 'asset'">
        <div class="header">Asset</div>
        <el-select
          v-model="compareObject.assetId"
          filterable
          @change="selectAsset"
          class="report-dropDown"
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
      <div class="p5">
        <div class="header">Data Point</div>
        <el-select
          v-model="compareObject.readingFieldId"
          filterable
          class="report-dropDown"
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
      <div class="p5" v-if="compareObject.readingFieldId">
        <div class="header">Aggregate</div>
        <el-select
          v-model="compareObject.aggregateFunc"
          filterable
          @change="applyFilter"
          placeholder="Choose Function"
          class="en-dropDown"
          v-tippy
          title="Choose Function"
        >
          <el-option
            v-for="(func, funcidx) in aggregateFunctions"
            :key="funcidx"
            :label="func.label"
            :value="func.value"
          >
          </el-option>
        </el-select>
      </div>
      <div class="p5">
        <el-checkbox
          v-model="compareObject.hasBaseline"
          style="width:100%"
          class="form-item "
          >Apply Baseline</el-checkbox
        >
      </div>
      <div class="p5" v-if="compareObject.hasBaseline">
        <div class="header">Baseline</div>
        <el-select
          v-model="compareObject.baseLineId"
          filterable
          @change="applyFilter"
          placeholder="Choose Baseline"
          class="en-dropDown"
          v-tippy
          title="Choose Baseline"
        >
          <el-option
            v-for="(baseline, index) in baselineList"
            :key="index"
            :label="baseline.name"
            :value="baseline.id"
          >
          </el-option>
        </el-select>
      </div>
    </div>
    <div class="row mT30">
      <div class="el-report-cancel-btn col-6 fc-el-btn" @click="cancel">
        cancel
      </div>
      <div class="el-report-save-btn fc-el-btn col-6" @click="save">Ok</div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      compareObject: {
        type: 'space',
        spaceId: '',
        assetId: '',
        readingFieldId: '',
        aggregateFunc: 0,
        hasBaseline: false,
        baseLineId: '',
      },
      baselineList: [],
      assetList: [],
      spaceList: [],
      readingFields: [],
      aggregateFunctions: [
        {
          label: 'Actual',
          value: 0,
        },
        {
          label: 'Sum',
          value: 3,
        },
        {
          label: 'Avg',
          value: 2,
        },
        {
          label: 'Min',
          value: 4,
        },
        {
          label: 'Max',
          value: 5,
        },
      ],
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      this.loadAssets()
      this.loadSpaces()
      this.loadBaselines()
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
      }
    },
    save() {
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
        this.compareObject.readingField = this.readingFields.find(
          a => a.id === this.compareObject.readingFieldId
        )
        reportName += ' - ' + this.compareObject.readingField.displayName
      }
      this.compareObject.name = reportName
      this.$emit('save', this.compareObject)
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
    loadAssets() {
      let self = this
      self.loading = true
      let url = '/asset/all'
      this.$http.get(url).then(function(response) {
        if (response.data) {
          self.assetList = response.data.assets
          self.loading = false
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
    loadReadingFields() {
      let self = this
      if (self.compareObject.type === 'asset' && this.compareObject.assetId) {
        let obj = self.getAsset(this.compareObject.assetId)
        let url =
          '/module/meta?moduleName=asset&assetId=' +
          obj.id +
          '&categoryId=' +
          obj.category.id +
          '&resourceType=asset'
        self.$http.get(url).then(function(response) {
          self.readingFields = response.data.meta.fields
          if (!self.compareObject.readingFieldId) {
            let energyDelta = self.readingFields.find(
              r => r.columnName === 'TOTAL_ENERGY_CONSUMPTION_DELTA'
            )
            if (energyDelta) {
              self.compareObject.readingFieldId = energyDelta.id + ''
            } else {
              self.compareObject.readingFieldId = self.readingFields[0].id + ''
            }
          }
        })
      } else if (
        self.compareObject.type === 'space' &&
        self.compareObject.spaceId
      ) {
        let url =
          '/reading/getspacespecificreadings?parentId=' +
          self.compareObject.spaceId
        self.$http.get(url).then(function(response) {
          let fields = []
          if (response.data) {
            for (let i = 0; i < response.data.length; i++) {
              let readingModule = response.data[i]
              for (let j = 0; j < readingModule.fields.length; j++) {
                let field = readingModule.fields[j]
                field.module = { name: readingModule.name }
                fields.push(field)
              }
            }
            self.readingFields = fields
            if (!self.compareObject.readingFieldId) {
              self.compareObject.readingFieldId = self.readingFields[0].id
            }
          }
        })
      }
    },
  },
}
</script>

<style></style>
