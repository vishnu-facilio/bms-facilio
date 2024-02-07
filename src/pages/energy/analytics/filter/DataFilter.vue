<template>
  <div class="height400 overflow-y-scroll pB80">
    <div v-if="config.conditions.length > 0">
      <el-row
        v-for="(condition, index) in config.conditions"
        class="row mB10 flex-middle"
        :key="index"
        :gutter="10"
      >
        <el-col :span="10" class="mR10">
          <el-input
            v-model="condition.parentName"
            :disabled="true"
            type="text"
            placeholder="Space/Asset"
            class="fc-input-full-border-select2"
          >
            <i
              @click="condition.pickerVisibility = true"
              slot="suffix"
              style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
              class="el-input__icon el-icon-search"
            ></i>
          </el-input>
          <space-asset-chooser
            @associate="
              (resource, resourceType) =>
                associate(index, resource, resourceType)
            "
            :visibility.sync="condition.pickerVisibility"
            :closeOnClickModel="true"
          ></space-asset-chooser>
        </el-col>
        <el-col :span="10" class="mR10">
          <el-select
            v-model="condition.fieldId"
            filterable
            placeholder="Reading"
            class="report-dropDown el-input-textbox-full-border"
            @change="() => setDatapoint(index, condition.fieldId)"
          >
            <el-option
              v-for="(field, idx) in readingFields[condition.parentId]"
              :key="idx"
              :label="field.displayName"
              :value="field.id"
            >
              <!-- {{field}} -->
            </el-option>
          </el-select>
        </el-col>
        <el-col :span="10" class="mR10">
          <el-select
            v-model="condition.operatorId"
            filterable
            placeholder="Operator"
            class="report-dropDown el-input-textbox-full-border"
            @change="() => setOperator(index, condition.operatorId)"
          >
            <el-option
              v-for="(operatorObj, idx2) in condition.operators"
              :key="idx2"
              :label="operatorObj.operator"
              :value="operatorObj.operatorId"
            ></el-option>
          </el-select>
        </el-col>
        <el-col :span="10" class="mR10">
          <el-input
            v-if="condition.valueNeeded"
            v-model="condition.value"
            class="fc-border-select"
          ></el-input>
          <el-input
            v-else
            placeholder="Input not needed"
            disabled
            class="fc-border-select"
          ></el-input>
        </el-col>
        <el-col :span="5">
          <div class="flex-middle">
            <img
              src="~assets/add-icon.svg"
              style="height:18px;width:18px;cursor:pointer;"
              class="delete-icon"
              @click="addCondition"
            />
            <img
              src="~assets/remove-icon.svg"
              style="height:18px;width:18px;margin-left:15px;cursor:pointer;"
              class="delete-icon"
              @click="deleteCondition(index)"
            />
          </div>
        </el-col>
      </el-row>
    </div>
    <div v-else style="text-align: center;">
      <el-button
        type="primary"
        class="addFilter modal-btn-save"
        @click="addCondition"
        >Add Filter</el-button
      >
    </div>
  </div>
</template>

<script>
import SpaceAssetChooser from '@/SpaceAssetChooser'
import FilterMixin from 'pages/energy/analytics/filter/FilterMixin'
import { isEmpty } from '@facilio/utils/validation'

export default {
  name: 'DataFilter',
  components: {
    SpaceAssetChooser,
  },
  mixins: [FilterMixin],
  props: ['config'],
  data() {
    return {
      excludeOperators: [74, 75, 76, 77, 78, 79, 35, 87],
      readingFields: {},
      resources: {},
    }
  },
  created() {
    this.config.conditions = isEmpty(this.config.conditions)
      ? [this.getEmptyCondition()]
      : this.config.conditions
    this.initDependendData()
  },
  methods: {
    getEmptyCondition() {
      return {
        pickerVisibility: false,
        parentType: null,
        parentName: null,
        parentId: null,
        moduleName: null,
        fieldName: null,
        fieldId: null,
        operatorId: null,
        value: null,
      }
    },
    addCondition() {
      if (this.config.conditions.length <= 10) {
        this.config.conditions.push(this.getEmptyCondition())
        this.$set(this.config, 'conditions', this.config.conditions)
        this.$forceUpdate()
      }
    },
    deleteCondition(index) {
      this.$delete(this.config.conditions, index)
    },
    setOperator(index, operatorId) {
      let operators = this.config.conditions[index].operators
      for (let key in operators) {
        let operator = operators[key]
        if (operatorId === operator.operatorId) {
          this.config.conditions[index].valueNeeded = operator.valueNeeded
        }
      }
    },
    setDatapoint(index, fieldId) {
      let parentId = this.config.conditions[index].parentId
      let fields = this.readingFields[parentId]
      let fieldobj = fields.find(rt => rt.id === fieldId)
      this.config.conditions[index].moduleName = fieldobj.module.name
      this.config.conditions[index].fieldName = fieldobj.name
      this.config.conditions[index].fieldId = fieldobj.id
      let operators = fieldobj.dataTypeEnum.operators
      for (let key in operators) {
        let operator = operators[key]
        if (this.excludeOperators.includes(operator.operatorId)) {
          delete operators[key]
        }
      }
      this.config.conditions[index].operators = operators
    },
    loadReadingFields(parentId, resourceType) {
      let promise =
        resourceType === 'space'
          ? this.$util.loadSpaceReadingFields(parentId)
          : this.$util.loadAssetReadingFields(parentId)

      return promise.then(readingFields => {
        this.$set(this.readingFields, `${parentId}`, readingFields)
      })
    },
    associate(index, resource, resourceType) {
      this.$set(this.config.conditions, index, this.getEmptyCondition())

      this.config.conditions[index].pickerVisibility = false
      this.config.conditions[index].parentName = resource.name
      this.config.conditions[index].parentId = resource.id
      this.config.conditions[index].parentType = resourceType
      this.loadReadingFields(resource.id, resourceType)
    },
    initDependendData() {
      let conditions = this.config.conditions
      let parentIds = []
      for (let key in conditions) {
        let condition = conditions[key]
        if (condition.parentId !== null) {
          parentIds.push(condition.parentId)
        }
      }
      if (!isEmpty(parentIds)) {
        this.$util.loadResource(parentIds).then(response => {
          for (let resource of response) {
            let parentId = resource.id
            let resourceObj = {}
            resourceObj.parentType = resource.resourceType
            resourceObj.parentId = parentId
            resourceObj.parentName = resource.name
            this.$set(this.resources, `${parentId}`, resourceObj)

            let promise =
              resource.resourceType === 2
                ? this.$util.loadAssetReadingFields(parentId)
                : this.$util.loadSpaceReadingFields(parentId)
            promise.then(readingFields => {
              this.$set(this.readingFields, `${parentId}`, readingFields)
              this.getRenderObj()
            })
          }
        })
      } else {
        this.$set(this.config, 'conditions', [this.getEmptyCondition()])
      }
    },
    getRenderObj() {
      let renderObj = []
      let conditions = this.$helpers.cloneObject(this.config.conditions)
      for (let key in conditions) {
        let condition = conditions[key]
        let resourceObj = this.resources[condition.parentId]
        condition.pickerVisibility = false
        condition.parentType = resourceObj.parentType
        condition.parentId = resourceObj.parentId
        condition.parentName = resourceObj.parentName

        let fieldObj = this.readingFields[condition.parentId].find(
          field => field.name === condition.fieldName
        )
        condition.moduleName = fieldObj.module.name
        condition.fieldName = fieldObj.name
        condition.fieldId = fieldObj.id
        let operators = fieldObj.dataTypeEnum.operators
        for (let key in operators) {
          let operator = operators[key]
          if (this.excludeOperators.includes(operator.operatorId)) {
            delete operators[key]
          }
        }
        condition.operators = operators
        condition.valueNeeded = condition.value !== null
        renderObj.push(condition)
      }
      this.$set(this.config, 'conditions', renderObj)
    },
  },
}
</script>
<style>
.el-col-10 {
  width: 21% !important;
}

.el-col-5 {
  width: 10% !important;
}

.el-input__inner {
  height: 40px !important;
}

.addFilter {
  width: 15%;
  padding-top: 15px;
  padding-bottom: 15px;
  float: none;
}
</style>
