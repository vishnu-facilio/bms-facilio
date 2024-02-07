<template>
  <FContainer class="detail-action">
    <FContainer class="width25 basic-detail-form">
      <BasicDetailForm
        :prefillRecordDetails.sync="getBasicDetails"
        @onFormDetailsChange="onFormDetailsChange"
        :isControlActionTemplateModule="isControlActionTemplateModule"
      />
    </FContainer>
    <FContainer class="width70 control-action-form">
      <FContainer
        v-if="canShowEmptyState"
        height="100%"
        display="flex"
        justifyContent="center"
        alignItems="center"
      >
        <FEmptyState
          :description="emptyStateMessage"
          :title="emptyStateTitle"
          :vertical="true"
        />
      </FContainer>
      <FContainer height="100%">
        <FContainer
          v-if="isCategoryChange"
          display="flex"
          justifyContent="center"
          alignItems="center"
        >
          <FSpinner :size="30" />
        </FContainer>
        <ControlAction
          v-else
          :prefillRecordDetails.sync="getReadingList"
          @onControlActionChange="onControlActionChange"
          :readingList="readingList"
          :readingOptionList="readingOptionList"
          :showRevertOption="enableRevertOption"
          :assetCategory="$getProperty(getBasicDetails, 'assetCategory', null)"
        />
      </FContainer>
    </FContainer>
  </FContainer>
</template>
<script>
import { cloneDeep } from 'lodash'
import BasicDetailForm from './BasicDetailForm.vue'
import ControlAction from './ControlAction.vue'
import { FContainer, FSpinner, FEmptyState } from '@facilio/design-system'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

const emptyReadingObject = {
  actionVariableType: 1,
  readingFieldId: null,
  readingFieldDataType: null,
  scheduledActionOperatorType: null,
  scheduleActionValue: null,
  revertActionOperatorType: null,
  revertActionValue: null,
}
export default {
  props: ['basicDetail', 'isControlActionTemplateModule'],
  data() {
    return {
      emptyReadingObject,
      basicDetails: {
        name: null,
        description: null,
        controlActionSourceType: null,
        controlActionType: 1,
        assetCategory: null,
        scheduledActionDateTime: null,
        revertActionDateTime: null,
        showRevertOption: false,
        actionContextList: [],
      },
      enableRevertOption: false,
      enableIsRecurring: false,
      readingList: [],
      readingOptionList: [],
      isCategoryChange: false,
      canShowEmptyState: false,
      emptyStateTitle: 'No Asset Category selected.',
      emptyStateMessage:
        'Control Actions can be configured for readings when asset category is selected!',
    }
  },
  components: {
    BasicDetailForm,
    ControlAction,
    FContainer,
    FSpinner,
    FEmptyState,
  },
  created() {
    let { basicDetail } = this
    if (!isEmpty(this.basicDetail)) {
      let {
        assetCategory,
        revertActionDateTime,
        showRevertOption,
        showIsRecurring,
        calendar,
      } = basicDetail || {}
      if (revertActionDateTime || showRevertOption) {
        this.enableRevertOption = true
      }
      if (showIsRecurring || !isEmpty(calendar)) {
        this.enableIsRecurring = true
      }
      if (assetCategory) {
        this.updateReadingList(assetCategory)
      }
      let { enableRevertOption, enableIsRecurring } = this
      this.basicDetails = {
        ...basicDetail,
        showRevertOption: enableRevertOption,
        showIsRecurring: enableIsRecurring,
      }
    }
  },
  watch: {
    basicDetails: {
      async handler(newVal) {
        this.$emit('onDataChange', newVal)
      },
      deep: true,
    },
  },
  computed: {
    getBasicDetails() {
      let { basicDetails } = this
      return basicDetails
    },
    getReadingList() {
      let { basicDetails, emptyReadingObject } = this
      let { actionContextList } = basicDetails || {}
      return isEmpty(actionContextList)
        ? [emptyReadingObject]
        : actionContextList
    },
  },
  methods: {
    onFormDetailsChange(val) {
      let { basicDetails } = this
      let { assetCategory: newCategory, showRevertOption, showIsRecurring } =
        val || {}
      let { assetCategory: oldCategory } = basicDetails || {}
      if (newCategory != oldCategory) {
        this.updateReadingList(newCategory)
        this.resetActionList()
      }
      this.canShowEmptyState = isEmpty(newCategory)
      this.enableRevertOption = showRevertOption
      this.enableIsRecurring = showIsRecurring
      this.basicDetails = { ...basicDetails, ...val }
    },
    onControlActionChange(val) {
      this.basicDetails.actionContextList = cloneDeep(val)
    },
    resetActionList() {
      let { emptyReadingObject } = this
      this.basicDetails.actionContextList = [emptyReadingObject]
    },
    async updateReadingList(id) {
      this.canShowEmptyState = false
      this.isCategoryChange = true
      if (!isEmpty(id)) {
        let { data: readingsData, error: readingsError } = await API.get(
          'v2/readings/assetcategory',
          {
            id,
            excludeEmptyFields: true,
            fetchValidationRules: false,
            fetchControllableFields: true,
          }
        )
        if (!readingsError) {
          let { readings } = readingsData || {}
          if (isEmpty(readings)) {
            this.emptyStateTitle = 'No readings found.'
            this.emptyStateMessage = 'Please select other asset category'
            this.canShowEmptyState = true
          } else {
            this.readingOptionList = (readings || []).map(ele => {
              let { displayName: label, id: value } = ele || {}
              return { label, value }
            })
            this.readingList = readings.map(ele => {
              let { displayName, id, dataType, unit, dataTypeEnum } = ele || {}
              let { typeAsString } = dataTypeEnum || {}
              return { displayName, id, dataType, unit, typeAsString }
            })
          }
        }
      } else {
        this.emptyStateTitle = 'No Asset Category selected.'
        this.emptyStateMessage =
          'Control Actions can be configured for readings when asset category is selected!'
        this.canShowEmptyState = true
      }

      this.isCategoryChange = false
    },
  },
}
</script>
<style lang="scss" scoped>
.detail-action {
  display: flex;
  margin-top: 15px;
  height: 95%;
}
.basic-detail-form {
  background-color: #fafafa !important;
  border-radius: 2%;
}
.control-action-form {
  margin-left: 10px;
  overflow: scroll;
  max-height: 70vh;
}
</style>
