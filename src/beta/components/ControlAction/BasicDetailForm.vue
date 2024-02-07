<template>
  <FContainer class="basic-detail-form">
    <FContainer class="header-style seperator">
      <FText appearance="headingMed16">{{ 'Control Details' }}</FText>
    </FContainer>
    <FContainer class="body-style">
      <FContainer class="seperator">
        <FContainer class="mB16">
          <FText appearance="headingMed16" class="heading-style mB4">{{
            'Control Action Name'
          }}</FText>
          <FText color="backgroundSemanticRedMedium">{{ '*' }}</FText>
          <FInput
            placeholder="Enter name"
            v-model="basicDetails.name"
            appearance="default"
            type="text"
            size="medium"
          />
        </FContainer>
        <FContainer class="mB16">
          <FText appearance="headingMed16" class="heading-style mB4"
            >{{ 'Description' }}
            <FText appearance="captionMed12"
              >&nbsp;{{ '(optional)' }}
            </FText></FText
          >
          <FTextArea
            placeholder="Enter description"
            v-model="basicDetails.description"
            appearance="default"
            :rows="4"
          />
        </FContainer>
        <FContainer class="mB16">
          <FText appearance="headingMed16" class="heading-style mB4"
            >{{ 'Type' }}
          </FText>
          <FText color="backgroundSemanticRedMedium">{{ '*' }}</FText>
          <FSelect
            v-model="basicDetails.controlActionType"
            :options="TYPE_LIST"
            :disabled="true"
            placeholder="Select Type"
          />
        </FContainer>
        <FContainer class="mB16">
          <FText appearance="headingMed16" class="heading-style mB4"
            >{{ 'Asset Category' }}
          </FText>
          <FText color="backgroundSemanticRedMedium">{{ '*' }}</FText>
          <Lookup :field="field" v-model="basicDetails.assetCategory" />
          <LookupWizard
            v-if="canShowLookupWizard"
            :canShowLookupWizard.sync="canShowLookupWizard"
            @setLookupFieldValue="setLookupFieldValue"
            :field.sync="field"
          />
        </FContainer>
        <FContainer v-if="isControlActionTemplateModule">
          <FText appearance="headingMed16" class="heading-style mB4"
            >{{ 'Scheduler' }}
          </FText>
          <FContainer class="flex recurring-container">
            <FCheckbox v-model="basicDetails.showIsRecurring" />
            <FText appearance="headingMed16" class="heading-style"
              >{{ 'Is Recurring' }}
            </FText>
          </FContainer>
        </FContainer>
        <FContainer class="mB16" v-if="!basicDetails.showIsRecurring">
          <FText appearance="headingMed16" class="heading-style mB4"
            >{{ 'Schedule Action Date Time' }}
          </FText>
          <FDatePicker
            placeholder="DD-MM-YYYY | HH:MM"
            v-model="basicDetails.scheduledActionDateTime"
            type="date-time"
            :timeFormat="{ is12Hour: false, interval: 30 }"
            displayFormat="YYYY-MM-DD HH:mm"
          />
        </FContainer>
        <FContainer v-if="basicDetails.showIsRecurring">
          <FContainer class="mB16">
            <FText appearance="headingMed16" class="heading-style mB4"
              >{{ 'Control Action calendar' }}
            </FText>
            <Lookup :field="calField" v-model="basicDetails.calendar"/>
            <LookupWizard
              v-if="canShowCalLookupWizard"
              :canShowLookupWizard.sync="canShowCalLookupWizard"
              @setLookupFieldValue="setLookupFieldValue"
              :field.sync="calField"
          /></FContainer>
        </FContainer>
      </FContainer>
      <FContainer>
        <FContainer class="pTB12 flex">
          <FCheckbox v-model="basicDetails.showRevertOption" />
          <FText appearance="headingMed16" class="heading-style"
            >{{ 'Enable Revert Action' }}
          </FText>
        </FContainer>
        <FContainer
          v-if="basicDetails.showRevertOption && !basicDetails.showIsRecurring"
          class="mB16"
        >
          <FText appearance="headingMed16" class="heading-style mB4"
            >{{ 'Revert Action Date Time' }}
          </FText>
          <FDatePicker
            placeholder="DD-MM-YYYY | HH:MM"
            v-model="basicDetails.revertActionDateTime"
            type="date-time"
            :timeFormat="{ is12Hour: false, interval: 30 }"
            displayFormat="YYYY-MM-DD HH:mm"
          />
        </FContainer>
      </FContainer>
    </FContainer>
  </FContainer>
</template>
<script>
import {
  FSelect,
  FContainer,
  FText,
  FInput,
  FTextArea,
  FDatePicker,
  FCheckbox,
} from '@facilio/design-system'
import { Lookup, LookupWizard } from '@facilio/ui/new-forms'
import { cloneDeep } from 'lodash'
import { isEmpty } from '@facilio/utils/validation'
const TYPE_LIST = [
  { label: 'Set Point Change', value: 1 },
  { label: 'Schedule Change', value: 2 },
]
export default {
  props: ['prefillRecordDetails', 'isControlActionTemplateModule'],
  data() {
    return {
      basicDetails: {
        name: null,
        description: null,
        controlActionSourceType: null,
        controlActionType: 1,
        assetCategory: null,
        calendar: null,
        scheduledActionDateTime: null,
        revertActionDateTime: null,
        showRevertOption: false,
        showIsRecurring: false,
      },
      TYPE_LIST,
      canShowLookupWizard: false,
      canShowCalLookupWizard: false,
      field: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'assetcategory',
        field: {
          lookupModule: {
            name: 'assetcategory',
            displayName: 'Asset Category',
          },
        },
      },
      calField: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'calendar',
        field: {
          lookupModule: {
            name: 'calendar',
            displayName: 'Calendar',
          },
        },
        filters: {
          calendarType: {
            operatorId: 54,
            value: ['2'],
          },
        },
      },
    }
  },
  created() {
    this.basicDetails = cloneDeep(this.prefillRecordDetails)
  },
  components: {
    FContainer,
    FText,
    FSelect,
    FInput,
    FTextArea,
    FDatePicker,
    FCheckbox,
    Lookup,
    LookupWizard,
  },
  watch: {
    basicDetails: {
      async handler(newVal) {
        this.$emit('onFormDetailsChange', newVal)
      },
      deep: true,
    },
  },
  methods: {
    setLookupFieldValue(props) {
      let { field: selectedLookupField, isControlActionTemplateModule } = this
      let { field } = props

      if (isEmpty(selectedLookupField)) this.selectedLookupField = field

      let { selectedItems, options = [], multiple } = field || {}
      let selectedItemIds = []

      if (!isEmpty(selectedItems)) {
        selectedItemIds = selectedItems.map(item => item.value)
        if (!isEmpty(options)) {
          let ids = options.map(item => item.value)
          let newOptions = selectedItems.filter(
            item => !ids.includes(item.value)
          )
          options.unshift(...newOptions)
        } else {
          options = [...selectedItems]
        }
      }

      this.$set(this.field, 'options', options)
      if (isControlActionTemplateModule) {
        if (multiple) {
          this.basicDetails.calendar = selectedItemIds
        } else {
          this.basicDetails.calendar = selectedItemIds[0]
        }
      } else {
        if (multiple) {
          this.basicDetails.assetCategory = selectedItemIds
        } else {
          this.basicDetails.assetCategory = selectedItemIds[0]
        }
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.basic-detail-form {
  height: 100%;

  .heading-style {
    font-weight: 400;
    padding-right: 2px;
    font-size: 14px;
    display: inline-flex;
  }
  .header-style {
    padding: 8px 16px;
  }
  .seperator {
    border-bottom: 1px solid #eae9e9;
  }
  .body-style {
    padding: 5px 15px;
    height: calc(100% - 36px);
    overflow: scroll;
  }
  .recurring-container {
    margin: 10px 0px;
  }
  .mB4 {
    margin-bottom: 4px;
  }
  .mB16 {
    margin-bottom: 16px;
  }
  .pTB12 {
    padding: 12px 0px;
  }
}
</style>
