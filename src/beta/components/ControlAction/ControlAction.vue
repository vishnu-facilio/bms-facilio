<template>
  <FContainer class="control-action-container">
    <FContainer paddingBottom="sectionXSmall" maxHeight="94%">
      <FContainer backgroundColor="backgroundCanvas" width="100%">
        <FText appearance="headingMed16">{{ 'Control Actions' }}</FText>
      </FContainer>
      <FContainer class="reading-container" v-if="showReadings">
        <ControlActionReading
          v-for="(readingObject, index) in readingObjList"
          :key="index"
          :readingObj="readingObject"
          @deleteReading="deleteReading"
          :index="index"
          :showDeleteButton="readingObjList.length > 1"
          :readingOptionList="readingOptionList"
          :readingList="readingList"
          :showRevertOption="showRevertOption"
          @onReadingDataChange="onReadingDataChange"
        />
      </FContainer>
    </FContainer>
    <FContainer width="100%">
      <FDivider width="100%" />
      <FContainer
        @click="addNewReading"
        display="flex"
        alignItems="center"
        padding="containerXLarge containerLarge containerNone"
        class="width15 pointer"
      >
        <FButton
          appearance="primaryInverse"
          :disabled="isEmpty(readingOptionList)"
          size="small"
        >
          <FIcon
            group="navigation"
            name="addition"
            size="18"
            color="backgroundPrimaryPressed"
            :pressable="false"
          />
          <FText
            color="backgroundPrimaryPressed"
            appearance="headingMed14"
            paddingLeft="containerLarge"
            >{{ 'Add Action' }}</FText
          ></FButton
        >
      </FContainer>
    </FContainer>
  </FContainer>
</template>
<script>
import { cloneDeep } from 'lodash'
import ControlActionReading from './ControlActionReading.vue'
import {
  FContainer,
  FText,
  FButton,
  FDivider,
  FIcon,
} from '@facilio/design-system'
import { isEmpty } from '@facilio/utils/validation'
export default {
  components: {
    FContainer,
    FText,
    ControlActionReading,
    FButton,
    FDivider,
    FIcon,
  },
  props: [
    'prefillRecordDetails',
    'readingOptionList',
    'readingList',
    'showRevertOption',
  ],
  data() {
    return {
      readingObjList: [],
      showReadings: true,
    }
  },
  created() {
    this.readingObjList = cloneDeep(this.prefillRecordDetails)
    this.isEmpty = isEmpty
  },
  watch: {
    readingObjList: {
      async handler(newVal) {
        this.$emit('onControlActionChange', newVal)
      },
      deep: true,
    },
  },
  methods: {
    addNewReading() {
      let readingObj = {
        actionVariableType: 1,
        readingFieldId: null,
        readingFieldDataType: null,
        scheduledActionOperatorType: null,
        scheduleActionValue: null,
        revertActionOperatorType: null,
        revertActionValue: null,
      }
      this.readingObjList.push(readingObj)
    },
    deleteReading(index) {
      let { readingObjList } = this
      this.showReadings = false
      let filteredReadingObjList = (readingObjList || []).filter(
        (_, readingIndex) => readingIndex !== index
      )
      this.readingObjList = filteredReadingObjList
      this.$nextTick(() => {
        this.showReadings = true
      })
    },
    onReadingDataChange(data) {
      let { index, newField } = data || {}
      let { readingObjList } = this
      let copyData = cloneDeep(readingObjList)
      copyData[index] = newField
      this.readingObjList = copyData
    },
  },
}
</script>
<style lang="scss" scoped>
.control-action-container {
  width: 100%;
  border-radius: 8px;
  max-height: 99%;
  border: 1px solid rgba(5, 16, 30, 0.1);
  padding: 12px 16px;
  box-shadow: 0px 2px 4px 0px rgba(5, 16, 30, 0.1),
    0px 1px 2px 0px rgba(5, 16, 30, 0.25);
  .mB16 {
    margin-bottom: 16px;
  }
  .reading-container {
    max-height: 424px;
    overflow: scroll;
  }
}
</style>
