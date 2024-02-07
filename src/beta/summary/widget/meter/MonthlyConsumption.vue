<template>
  <FContainer
    v-if="isLoading"
    display="flex"
    justifyContent="center"
    alignItems="center"
  >
    <FSpinner :show="isLoading" :size="30" />
  </FContainer>
  <FContainer v-else>
    <FContainer paddingBottom="containerXxLarge" marginTop="containerXxLarge">
      <FText marginLeft="containerXxLarge" color="textMain" appearance="headingMed16">{{
        getThisMonthConsumption
      }}</FText>
      <FContainer
        tag="span"
        v-if="isDiff"
        class="mL10 pL5 pR5 pB5 pT5 f12"
        :class="getRange"
      >
        <fc-icon v-if="scale" group="navigation" name="accordion-up"></fc-icon>
        <fc-icon v-else group="navigation" name="accordion-down"></fc-icon>
        {{ percentVal }}%</FContainer
      >
      <!--<FTags v-if="isDiff" appearance="status" :text="getPercentage" :statusType="getStatusType">
      </FTags>-->
    </FContainer>
    <FContainer marginTop="containerXLarge">
      <FText marginLeft="containerXxLarge" paddingRight="containerXLarge" color="textMain" appearance="captionMed12">
        {{ $t('common.meter.last_month') }}
      </FText>
      <FText color="textDescription" appearance="bodyReg14">{{
        getLastMonthConsumption
      }}</FText>
    </FContainer>
  </FContainer>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { FContainer, FSpinner, FText, FTags } from '@facilio/design-system'
export default {
  props: ['details'],
  components: { FContainer, FSpinner, FText, FTags },
  data() {
    return {
      isLoading: false,
      thisMonthConsumption: 0,
      lastMonthConsumption: 0,
      percentVal: 0,
      scale: 0,
      readingName: '',
      readingList: [],
    }
  },
  created() {
    this.loadMonthlyConsumption()
    this.getAllMeterReadings()
  },
  computed: {
    getThisMonthConsumption() {
      let thisMonthConsumption = this.$getProperty(this, 'thisMonthConsumption', '---') || '---'
      return thisMonthConsumption + ' ' + this.getUnit
    },
    getLastMonthConsumption() {
      let lastMonthConsumption = this.$getProperty(this, 'lastMonthConsumption', '---') || '---'
      return lastMonthConsumption + ' ' + this.getUnit
    },
    getRange() {
      let { scale } = this
      return scale ? 'percent_positive' : 'percent_negative'
    },
    isDiff() {
      let { percentVal } = this
      return !isEmpty(percentVal) && percentVal != 0
    },
    getUnit() {
      let { readingName, readingList } = this
      let reading = readingList.find(reading => reading.name === readingName)
      return !isEmpty(reading) ? this.$getProperty(reading, 'unit', '') : ''
    },
    getPercentage() {
      return this.percentVal + '%'
    },
    getStatusType() {
      return this.scale ? 'success' : 'danger'
    },
  },
  methods: {
    async loadMonthlyConsumption() {
      this.isLoading = true
      let meterId = this.$getProperty(this, 'details.id')
      let utilityTypeName = this.$getProperty(this, 'details.utilityType.name')
      let parameters = [meterId, utilityTypeName]

      let params = {
        defaultWorkflowId: 203,
        paramList: parameters,
      }

      let { data, error } = await API.post(
        '/v2/workflow/getDefaultWorkflowResult',
        params
      )
      if (data && data.workflow) {
        const result = this.$getProperty(data, 'workflow.returnValue')
        let thisMonthVal = this.$getProperty(
          result,
          'thisMonthConsumption',
          '---'
        )
        let lastMonthVal = this.$getProperty(
          result,
          'lastMonthConsumption',
          '---'
        )
        let percentVal = this.$getProperty(result, 'percentDifference', 0)
        let scale = this.$getProperty(result, 'scale', 0)
        let name = this.$getProperty(result, 'readingName', '')
        this.thisMonthConsumption = thisMonthVal
        this.lastMonthConsumption = lastMonthVal
        this.percentVal = percentVal
        this.scale = scale
        this.readingName = name
      } else if (error) {
        this.$message.error(error.message || 'Error occured')
      }
      this.isLoading = false
    },
    async getAllMeterReadings() {
      let { details } = this
      let utilityTypeId = this.$getProperty(details, 'utilityType.id')
      let readings = await this.$util.loadMeterReadingFields(
        null,
        utilityTypeId,
        false,
        null,
        true
      )
      this.readingList = readings || []
    },
  },
}
</script>
<style scoped>
.percent_positive {
  background-color: #e3fcdf;
  color: #1d384e;
  border-radius: 5px;
}
.percent_negative {
  background-color: #fce2df;
  color: #1d384e;
  border-radius: 5px;
}
</style>
