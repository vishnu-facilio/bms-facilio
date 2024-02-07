<template>
  <el-dialog
    :title="
      currentFilterConfig ? currentFilterConfig.name + ': Configure filter' : ''
    "
    :visible.sync="visibility"
    width="60%"
    class="fc-dialog-center-container"
    :append-to-body="true"
    :show-close="false"
    @keydown.esc="closeConfigDialog"
    :close-on-click-modal="false"
  >
    <div class="height400 overflow-y-scroll pB80">
      <el-row>
        <el-col :span="14">
          <div class="fc-grey-text-input-label"></div>

          <el-select
            class="fc-input-full-border-select2 width100"
            v-model="currentFilterConfig.component.componentType"
          >
            <el-option
              v-for="(compo, compoIdx) in Object.keys(getAvailableComponents)"
              :key="compoIdx"
              :value="parseInt(compo)"
              :label="componentType[compo].displayLabel"
            ></el-option>
          </el-select>
        </el-col>
      </el-row>

      <div class="border-bottom4 pT20 width100 inline-block">
        <div class="fL">
          <div class="pT10 pB10">
            <el-checkbox @change="selectAll()" v-model="showAll"
              >Show All</el-checkbox
            >
          </div>
        </div>
        <div class="fR">
          <div class="flex-middle pT10 pB10">
            <div class="label-txt-black pointer" @click="clearSelection()">
              Clear All
            </div>
            <div class="fc-pink-circle-txt mL10">
              {{
                Object.keys(fieldValues).filter(
                  field => fieldValues[field] === true
                ).length
              }}
            </div>
          </div>
        </div>
      </div>

      <div class="clearboth">
        <el-row v-for="(row, rowIdx) in computedValueList" :key="rowIdx">
          <el-col
            v-for="(field, fieldIdx) in Object.keys(row)"
            :key="fieldIdx"
            :span="12"
            class="pR10"
          >
            <div
              class="flex-middle visibility-visible-actions pointer justify-content-space fc-check-grey-border"
            >
              <el-checkbox
                @click="setField(field, $event)"
                :disabled="showAll === true"
                v-model="fieldValues[field]"
                >{{ row[field] }}</el-checkbox
              >
              <div
                v-if="
                  currentFilterConfig &&
                    currentFilterConfig.defaultValues[
                      currentFilterConfig.defaultValues.length - 1
                    ] !== field
                "
                @click="setDefault(field)"
                class="text-right fc-text-pink visibility-hide-actions"
              >
                Set Default
              </div>
              <div v-else class="text-right fc-text-pink">Default</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <div class="pT10 pB10">
        <el-checkbox
          :disabled="currentFilterConfig.chooseValue.type === 1"
          v-model="currentFilterConfig.chooseValue.otherEnabled"
          >Others</el-checkbox
        >
      </div>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="closeConfigDialog"
        >CANCEL</el-button
      >
      <el-button type="primary" class="modal-btn-save" @click="addUserFilter"
        >SAVE</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import modularUserFilter from 'src/pages/report/mixins/modularUserFilter'
export default {
  props: ['visibility', 'moduleName', 'config'],
  watch: {
    config: {
      handler: function(oldValue, newValue) {
        if (this.config === null) {
          this.resetUserFilters()
        } else {
          this.loadAll()
        }
      },
      deep: true,
    },
  },
  mixins: [modularUserFilter],
  computed: {
    getAvailableComponents() {
      let temp = {}
      for (let component of Object.keys(this.componentType)) {
        if (
          this.currentFilterConfig &&
          this.currentFilterConfig.component.availableComponents.includes(
            parseInt(component)
          )
        ) {
          temp[component] = this.componentType[component]
        }
      }
      return temp
    },
    computedValueList() {
      let values = []
      if (this.config) {
        for (
          let i = 0;
          i <= this.currentFilterConfig.allValues.length - 2;
          i = i + 2
        ) {
          let row = {
            ...this.currentFilterConfig.allValues[i],
            ...this.currentFilterConfig.allValues[i + 1],
          }
          values.push(row)
        }
      }
      return values
    },
  },
  created() {
    if (typeof this.config !== 'undefined' && this.config !== null) {
      this.loadAll()
    }
  },
  data() {
    return {
      currentFilterConfig: null,
      showAll: false,
      showAny: false,
      isDefault: false,
      defaultField: null,
      showOthers: false,
      fieldValues: {},
    }
  },
  methods: {
    loadAll() {
      if (typeof this.config !== 'undefined' && this.config !== null) {
        this.fieldValues = {}
        this.currentFilterConfig = this.$helpers.cloneObject(this.config)
        if (this.config.chooseValue.type === this.type.all) {
          this.showAll = true
          this.showAny = false
          if (this.config.defaultValues.length !== 0) {
            this.isDefault = true
          }
          for (let value of this.currentFilterConfig.allValues) {
            this.$set(this.fieldValues, Object.keys(value)[0], true)
          }
        } else {
          this.showAny = true
          this.showAll = false
          if (this.config.defaultValues.length !== 0) {
            this.isDefault = true
          }
          for (let value of this.currentFilterConfig.allValues) {
            if (
              this.currentFilterConfig.chooseValue.values.includes(
                Object.keys(value)[0]
              )
            ) {
              this.$set(this.fieldValues, Object.keys(value)[0], true)
            } else {
              this.$set(this.fieldValues, Object.keys(value)[0], false)
            }
          }
        }
      }
    },
    setDefault(field) {
      if (this.isDefault === true) {
        if (this.showAll === false) {
          this.$set(this.fieldValues, this.defaultField, false)
        }
        this.$set(this.fieldValues, field, true)
        this.defaultField = field
        this.currentFilterConfig.defaultValues = []
        this.currentFilterConfig.defaultValues.push(field)
      } else {
        this.defaultField = field
        this.$set(this.fieldValues, field, true)
        this.currentFilterConfig.defaultValues = []
        this.currentFilterConfig.defaultValues.push(field)
        this.isDefault = true
      }
    },
    selectAll() {
      if (this.showAll === true) {
        this.$set(this.currentFilterConfig.chooseValue, 'otherEnabled', false)
        this.showAny = false
        this.currentFilterConfig.chooseValue.type = this.type.all
        for (let field in this.fieldValues) {
          this.$set(this.fieldValues, field, true)
        }
      } else {
        this.showAny = true
        this.currentFilterConfig.chooseValue.type = this.type.any
        this.isDefault = false
        this.currentFilterConfig.defaultValues = []
        for (let field in this.fieldValues) {
          this.$set(this.fieldValues, field, false)
        }
      }
    },
    clearSelection() {
      for (let key in this.fieldValues) {
        this.$set(this.fieldValues, key, false)
      }
      this.showAll = false
    },
    resetUserFilters() {
      ;(this.currentFilterConfig = null),
        (this.currentFilterConfig = null),
        (this.showAll = false),
        (this.showAny = false),
        (this.isDefault = false),
        (this.fieldValues = {})
    },
    // resetUserFilters(){
    //   console.log('Resetting User filters!!!')
    //   Object.keys(this.selectedFields).forEach((field) =>{
    //     this.selectedFields[field] = false
    //   })
    //   this.fieldConfigs = {}
    //   this.currentFilterConfig = null
    // },
    filterLookupValues(val) {
      for (let fieldId of val) {
        this.currentFilterConfig.filterLookupValues.push(
          this.currentFilterConfig.values[fieldId]
        )
      }
    },
    setField(field) {
      this.showAny = true
      this.showAll = false
      if (this.fieldValues[field]) {
        this.$set(this.fieldValues, field, true)
      } else {
        this.$set(this.fieldValues, field, false)
      }
    },
    setValues() {
      this.currentFilterConfig.chooseValue.values = []
      for (let key in this.fieldValues) {
        if (this.fieldValues[key] === true) {
          this.currentFilterConfig.chooseValue.values.push(key)
        }
      }
    },
    addUserFilter() {
      this.setValues()
      this.$emit('filter', this.currentFilterConfig)
      this.closeConfigDialog()
    },
    closeConfigDialog() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>

<style></style>
