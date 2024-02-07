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
        getTotalConsumption
      }}</FText>
    </FContainer>
    <FContainer v-if="isReadingAdded" marginLeft="containerXLarge" paddingTop="containerXLarge">
      <FText color="textCaption" appearance="bodyReg14">{{
        constructDateMessage
      }}</FText>
    </FContainer>
  </FContainer>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { FContainer, FSpinner, FText } from '@facilio/design-system'
export default {
  props: ['details'],
  components: { FContainer, FSpinner, FText },
  data() {
    return {
      isLoading: false,
      totalConsumption: 0,
      dateTime: '',
      readingName: '',
      readingList: [],
    }
  },
  created() {
    this.loadTotalConsumption()
    this.getAllMeterReadings()
  },
  computed: {
    getTotalConsumption() {
      let totalConsumption = this.$getProperty(this, 'totalConsumption', '---') || '---'
      return totalConsumption + ' ' + this.getUnit
    },
    constructDateMessage() {
      let { dateTime } = this
      let message = `Till Now from ${dateTime}`
      return message
    },
    isReadingAdded() {
      let { dateTime } = this
      return !isEmpty(dateTime)
    },
    getUnit() {
      let { readingName, readingList } = this
      let reading = readingList.find(reading => reading.name === readingName)
      return !isEmpty(reading) ? this.$getProperty(reading, 'unit', '') : ''
    },
  },
  methods: {
    async loadTotalConsumption() {
      this.isLoading = true
      let meterId = this.$getProperty(this, 'details.id')
      let utilityTypeName = this.$getProperty(this, 'details.utilityType.name')
      let parameters = [meterId, utilityTypeName]

      let params = {
        defaultWorkflowId: 204,
        paramList: parameters,
      }

      let { data, error } = await API.post(
        '/v2/workflow/getDefaultWorkflowResult',
        params
      )
      if (data && data.workflow) {
        const result = this.$getProperty(data, 'workflow.returnValue')
        let totalVal = this.$getProperty(result, 'totalConsumption', '---')
        let dateTime = this.$getProperty(result, 'dateTime', null)
        let name = this.$getProperty(result, 'readingName', '')
        this.totalConsumption = totalVal
        this.dateTime = dateTime
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
