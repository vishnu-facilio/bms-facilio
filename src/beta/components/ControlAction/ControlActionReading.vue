<template>
  <FContainer class="reading-container">
    <FContainer
      :class="index === 0 ? 'pT15' : 'pT10'"
      paddingRight="containerLarge"
    >
      <FContainer
        class="reading-number-cont"
        backgroundColor="backgroundNeutralGrey01Dark"
        border="1px solid"
      >
        {{ count }}
      </FContainer></FContainer
    >
    <FContainer width="30%" paddingRight="sectionXSmall">
      <FText v-if="index === 0" appearance="bodyReg14" display="flex">
        {{ 'Reading' }}</FText
      >
      <FSelect
        v-model="readingDataObj.readingFieldId"
        :options="readingOptionList"
        placeholder="Select Reading"
        :disabled="isEmpty(readingOptionList)"
        @change="setDefaultValue"
        size="small"
        class="reading-top"
      />
    </FContainer>
    <FContainer width="30%" paddingRight="sectionXSmall">
      <FText v-if="index === 0" appearance="bodyReg14" display="flex">
        {{ 'Operator' }}</FText
      >
      <FSelect
        v-model="readingDataObj.scheduledActionOperatorType"
        :options="operatorType"
        placeholder="Select Schedule Operator"
        :disabled="!showOptionDropDown"
        class="reading-top"
        size="small"
      />
    </FContainer>
    <FContainer width="30%" paddingRight="sectionXSmall">
      <FText v-if="index === 0" appearance="bodyReg14" display="flex">
        {{ 'Value' }}</FText
      >
      <FInput
        v-if="showOptionDropDown"
        class="reading-top"
        v-model="readingDataObj.scheduleActionValue"
        appearance="default"
        type="number"
        size="small"
        placeholder="--"
        :disabled="isEmpty(readingDataObj.readingFieldId)"
        ><template #suffix>{{ getPrefix }}</template></FInput
      >
      <FSelect
        v-else
        :options="[
          { label: 'True', value: 'true' },
          { label: 'False', value: 'false' },
        ]"
        class="reading-top"
        v-model="readingDataObj.scheduleActionValue"
        :multiple="false"
        :disabled="isEmpty(readingDataObj.readingFieldId)"
        placeholder="Select Schedule Value"
        size="small"
      />
    </FContainer>
    <FContainer v-if="enableRevertAction" width="60%" display="flex">
      <FContainer width="50%" paddingRight="sectionXSmall">
        <FText v-if="index === 0" appearance="bodyReg14" display="flex">{{
          'Revert Operator'
        }}</FText>
        <FSelect
          class="reading-top"
          v-model="readingDataObj.revertActionOperatorType"
          :options="operatorType"
          placeholder="Select Revert Operator"
          :disabled="!showOptionDropDown"
          size="small"
        />
      </FContainer>
      <FContainer width="50%" paddingRight="sectionXSmall">
        <FText v-if="index === 0" appearance="bodyReg14" display="flex">{{
          'Revert Value'
        }}</FText>
        <FInput
          v-if="showOptionDropDown"
          v-model="readingDataObj.revertActionValue"
          appearance="default"
          type="number"
          size="small"
          placeholder="--"
          :disabled="isEmpty(readingDataObj.readingFieldId)"
          class="reading-top"
          ><template #suffix>{{ readingDataObj.unit }}</template></FInput
        >
        <FSelect
          v-else
          :options="[
            { label: 'True', value: 'true' },
            { label: 'False', value: 'false' },
          ]"
          v-model="readingDataObj.revertActionValue"
          :multiple="false"
          :disabled="isEmpty(readingDataObj.readingFieldId)"
          placeholder="Select Revert Value"
          size="small"
          class="reading-top"
        />
      </FContainer>
    </FContainer>
    <FContainer :class="index === 0 ? 'pT15' : 'pT10'">
      <FIcon
        v-if="showDeleteButton"
        group="action"
        name="delete"
        size="16"
        @click="deleteReading(index)"
      />
    </FContainer>
  </FContainer>
</template>
<script>
import {
  FContainer,
  FSelect,
  FText,
  FInput,
  FIcon,
} from '@facilio/design-system'
import { isEmpty } from '@facilio/utils/validation'
import { cloneDeep } from 'lodash'
const operatorType = [
  { label: 'Fixed', value: 1 },
  { label: 'Increased By', value: 2 },
  { label: 'Decreased By', value: 3 },
]
const readingType = [
  { label: 'Asset Reading', value: 1 },
  { label: 'Related Reading', value: 2 },
]
export default {
  props: [
    'readingObj',
    'index',
    'showDeleteButton',
    'readingOptionList',
    'readingList',
    'showRevertOption',
  ],
  components: { FContainer, FSelect, FText, FInput, FIcon },
  data() {
    return {
      readingType,
      operatorType,
      isBooleanField: false,
      prefix: '',
      readingDataObj: {},
      enableRevertAction: false,
    }
  },
  created() {
    let { readingObj, showRevertOption } = this
    this.enableRevertAction = showRevertOption
    this.readingDataObj = cloneDeep(readingObj)
    this.setPrefix(readingObj)
    this.isEmpty = isEmpty
  },
  computed: {
    count() {
      let { index } = this
      return (index + 1).toString()
    },
    getPrefix() {
      let { prefix } = this
      return prefix
    },
    showOptionDropDown() {
      let { readingDataObj, isBooleanField } = this
      return !isEmpty(readingDataObj.readingFieldId) && !isBooleanField
    },
  },
  watch: {
    readingDataObj: {
      async handler(newVal) {
        let data = {
          index: this.index,
          newField: newVal,
        }
        this.$emit('onReadingDataChange', data)
      },
      deep: true,
    },
    showRevertOption: {
      async handler(newVal) {
        this.enableRevertAction = newVal
      },
      deep: true,
    },
  },
  methods: {
    deleteReading(index) {
      this.$emit('deleteReading', index)
    },
    setPrefix(readingObj) {
      let { readingList } = this
      let { readingFieldId } = readingObj || {}
      let selectedReading = readingList.find(
        reading => reading.id === readingFieldId
      )
      let { unit, dataType, typeAsString } = selectedReading || {}
      if (typeAsString == 'Boolean') {
        this.readingDataObj.revertActionOperatorType = 1
        this.readingDataObj.scheduledActionOperatorType = 1
        this.readingDataObj.operatorType = 'Boolean'
        this.isBooleanField = true
      } else {
        let { readingDataObj } = this
        let { revertActionOperatorType, scheduledActionOperatorType } =
          readingDataObj || {}
        this.readingDataObj.revertActionOperatorType = revertActionOperatorType
        this.readingDataObj.scheduledActionOperatorType = scheduledActionOperatorType
        this.isBooleanField = false
      }
      this.readingDataObj.unit = isEmpty(unit) ? '' : unit
      this.prefix = isEmpty(unit) ? '' : unit
      return dataType
    },
    setDefaultValue(val) {
      let { readingDataObj } = this
      this.readingDataObj.readingFieldId = val
      let dataType = this.setPrefix(readingDataObj)
      if (!isEmpty(dataType))
        this.readingDataObj.readingFieldDataType = dataType
    },
  },
}
</script>
<style lang="scss" scoped>
.reading-number-cont {
  width: 24px;
  height: 24px;
  border-radius: 100%;
  text-align: center;
  font-size: 14px;
  font-weight: bold;
  color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
}
.reading-container {
  display: flex;
  align-items: center;
  margin-top: 16px;
}
.reading-top {
  margin-top: 8px;
}
</style>
