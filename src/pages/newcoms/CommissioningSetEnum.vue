<template>
  <f-dialog
    v-if="showInputDialog"
    :visible.sync="showInputDialog"
    title="Set Input Values"
    width="30%"
    maxHeight="500px"
    @save="setInputValue"
    customClass="input-value-dialog"
  >
    <div v-if="isLoading" class="loading-container d-flex">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <div v-else>
      <div v-if="rowData.dataType === 4" class="pT10 pB10">
        <el-row
          :gutter="10"
          class="pB20 input-value-item d-flex text-capitalize"
        >
          <el-col class="self-center" :span="8">{{
            rowData.trueVal || 'True'
          }}</el-col>
          <el-col :span="10"
            ><el-input
              v-model="valuesArr[1].inputValue"
              class="el-input-textbox-full-border"
            ></el-input
          ></el-col>
        </el-row>
        <el-row
          :gutter="10"
          class="pB20 input-value-item d-flex text-capitalize"
        >
          <el-col class="self-center" :span="8">{{
            rowData.falseVal || 'False'
          }}</el-col>
          <el-col :span="10"
            ><el-input
              v-model="valuesArr[0].inputValue"
              class="el-input-textbox-full-border"
            ></el-input
          ></el-col>
        </el-row>
      </div>
      <div v-else class="pT10 pB10">
        <el-row
          :gutter="10"
          class="pB20 input-value-item d-flex"
          v-for="(option, index) in valuesArr"
          :key="index"
        >
          <el-col class="self-center" :span="8">{{ option.label }}</el-col>
          <el-col :span="10">
            <div v-if="$validation.isEmpty(enumStates)">
              <el-input
                :key="`input-${index}`"
                v-model="option.inputValue"
                class="el-input-textbox-full-border"
              ></el-input>
            </div>
            <div v-else>
              <el-select
                class="el-input-textbox-full-border"
                v-model="option.inputValue"
                allow-create
                filterable
                @change="updateLabel"
                clearable
              >
                <el-option
                  v-for="item in enumStates"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                  :disabled="isDisable(item.value)"
                >
                </el-option>
              </el-select>
            </div>
          </el-col>
        </el-row>
      </div>
      <div>
        <el-row :gutter="10" class="pB20">
          <el-col :span="8">
            <el-checkbox v-model="applySameValues"
              >Apply same values for the same reading mapped</el-checkbox
            >
          </el-col>
        </el-row>
      </div>
    </div>
  </f-dialog>
</template>
<script>
import FDialog from '@/FDialogNew'
import cloneDeep from 'lodash/cloneDeep'
import { isEmpty } from '@facilio/utils/validation'
import { constructEnumFieldOptions } from '@facilio/utils/utility-methods'
import Spinner from '@/Spinner'
import { API } from '@facilio/api'

export default {
  props: {
    canShowEnumWizard: {
      type: Boolean,
    },
    currentRowData: {
      type: Object,
    },
    selectedEnumReading: {
      type: Object,
    },
    activeSetInputRowIndex: {
      type: Number,
    },
    getPasteDataValue: {
      type: Function,
    },
  },
  components: {
    FDialog,
    Spinner,
  },
  computed: {
    mappedStates() {
      return this.valuesArr
        .filter(v => !isEmpty(v.inputValue))
        .map(v => v.inputValue)
    },
    showInputDialog: {
      get() {
        return this.canShowEnumWizard
      },
      set(value) {
        this.$emit('update:canShowEnumWizard', value)
        if (!value) {
          this.$emit('update:activeSetInputRowIndex', null)
        }
      },
    },
    rowData() {
      let { currentRowData } = this
      return cloneDeep(currentRowData)
    },
  },
  data() {
    return {
      valuesArr: [],
      applySameValues: false,
      isLoading: false,
      enumStates: [],
      newInputValues : null,
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      let { rowData, selectedEnumReading } = this
      this.$set(this, 'isLoading', true)
      this.constructInputValues({
        selectedOption: selectedEnumReading,
        rowData,
      })
        .then(inputValues => {
          let valuesArr = (inputValues || []).map(option => {
            let { inputValue } = option || {}
            return {
              ...(option || {}),
              inputValue: !isEmpty(inputValue) ? `${inputValue}` : null,
            }
          })

          this.valuesArr = valuesArr
        })
        .finally(() => this.$set(this, 'isLoading', false))
    },
    setEnumStates() {
      if (!isEmpty(this.newInputValues)) {
        this.newInputValues.forEach(item => {
            let keylabel = {}
            keylabel.value = item.inputValue
            keylabel.label = item.inputLabel
            this.enumStates.push(keylabel)
          })
        }
    },
    async setInputValues(rowData){
      let pointId = this.getPasteDataValue('pointId', { rowData })
      let url = `/v2/commissioning/inputValues?pointId=${pointId}`
      let { data } = await API.get(url)
      let { inputValues } = data
      this.newInputValues = inputValues
      this.setEnumStates()
    },
    async constructInputValues(props) {
      let { selectedOption, rowData } = props
      await this.setInputValues(rowData)
      if (!isEmpty(selectedOption)) {
        let { dataType, trueVal, falseVal, values } = selectedOption
        let { inputValues } = rowData
        let filteredValues = constructEnumFieldOptions(values)
        if (isEmpty(inputValues)) {
          if (dataType === 4) {
            rowData.inputValues = [
              {
                idx: '0',
                pointId: rowData.id,
                inputValue: isEmpty(this.newInputValues)
                  ? 'false'
                  : this.newInputValues[0].inputValue,
              },
              {
                idx: '1',
                pointId: rowData.id,
                inputValue: isEmpty(this.newInputValues)
                  ? 'true'
                  : this.newInputValues[1].inputValue,
              },
            ]
            rowData.trueVal = trueVal
            rowData.falseVal = falseVal
          } else {
            rowData.inputValues = filteredValues.map(options => {
              let { value, label } = options
              let existingInputValue = null
              let inputValue = null
              let inputLabel = null
              if (!isEmpty(this.newInputValues)) {
                existingInputValue =
                  (
                    this.newInputValues.find(input => input.idx === value) ||
                    {}
                  ).inputValue || ''
                inputValue = existingInputValue
                  ? existingInputValue
                  : null
                inputLabel =
                  (this.enumStates.find(v => v.value == inputValue) || {})
                    .label || ''
              }
              return {
                idx: value,
                label,
                pointId: rowData.id,
                inputValue: inputValue,
                inputLabel: inputLabel,
              }
            })
          }
          rowData.dataType = dataType
          return Promise.resolve(rowData.inputValues)
        } else {
          if (dataType === 4) {
            rowData.trueVal = trueVal
            rowData.falseVal = falseVal
          } else {
            rowData.inputValues = filteredValues.map(options => {
              let { value, label } = options
              let existingInputValue =
                (inputValues.find(input => input.idx === value) || {})
                  .inputValue || ''
              let inputValue = existingInputValue ? existingInputValue : value
              let inputLabel =
                (this.enumStates.find(v => v.value == inputValue) || {})
                  .label || ''
              return {
                idx: value,
                label,
                pointId: rowData.id,
                inputValue: inputValue,
                inputLabel: inputLabel,
              }
            })
          }
        }
      }
      return Promise.resolve(rowData.inputValues)
    },
    updateLabel(value) {
      let newLabel =
        (this.enumStates.find(v => v.value == value) || {}).label || ''
      let valueObj = this.valuesArr.find(v => v.inputValue == value) || {}
      if (isEmpty(newLabel)) {
        valueObj.inputLabel = value
      } else {
        valueObj.inputLabel = newLabel
      }
    },
    setInputValue() {
      let { valuesArr, applySameValues, activeSetInputRowIndex } = this
      this.$emit('updateEnumInputValues', {
        valuesArr,
        applySameValues,
        activeSetInputRowIndex,
      })
    },
    isDisable(value) {
      return this.mappedStates.includes(value)
    },
  },
}
</script>
