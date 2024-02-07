<template>
  <FContainer>
    <portal :to="`title-${widget.id}-${widget.name}`">
      <FText margin="containerXLarge" appearance="headingMed14">{{
        getTitle
      }}</FText>
    </portal>
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
        getPeakDemand
      }}</FText>
    </FContainer>
      <FContainer v-if="isReadingAdded" marginLeft="containerXxLarge" paddingTop="containerXLarge">
        <FText color="textCaption" appearance="bodyReg14">{{
          constructPeakTimeMessage
        }}</FText>
      </FContainer>
    </FContainer>
  </FContainer>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { FContainer, FSpinner, FText } from '@facilio/design-system'
export default {
  props: ['details', 'widget'],
  components: { FContainer, FSpinner, FText },
  data() {
    return {
      isLoading: false,
      peakDemand: 0,
      peakTime: '',
      readingName: '',
      widgetName: '',
      readingList: [],
    }
  },
  created() {
    this.loadPeakDemand()
    this.getAllMeterReadings()
  },
  computed: {
    getPeakDemand() {
      let peakDemand = this.$getProperty(this, 'peakDemand', '---') || '---'
      return peakDemand + ' ' + this.getUnit
    },
    isReadingAdded() {
      let { peakTime } = this
      return !isEmpty(peakTime)
    },
    getUnit() {
      let { readingName, readingList } = this
      let reading = readingList.find(reading => reading.name === readingName)
      return !isEmpty(reading) ? this.$getProperty(reading, 'unit', '') : ''
    },
    getTitle() {
      let { widgetName } = this
      return !isEmpty(widgetName) ? widgetName : ''
    },
    constructPeakTimeMessage() {
      let { peakTime } = this
      let message = `At ${peakTime}`
      return message
    },
  },
  methods: {
    async loadPeakDemand() {
      this.isLoading = true
      let meterId = this.$getProperty(this, 'details.id')
      let utilityTypeName = this.$getProperty(this, 'details.utilityType.name')
      let parameters = [meterId, utilityTypeName]

      let params = {
        defaultWorkflowId: 205,
        paramList: parameters,
      }

      let { data, error } = await API.post(
        '/v2/workflow/getDefaultWorkflowResult',
        params
      )
      if (data && data.workflow) {
        const result = this.$getProperty(data, 'workflow.returnValue')
        let peakVal = this.$getProperty(result, 'peakDemand', '---')
        let peakTime = this.$getProperty(result, 'peakTime', null)
        let name = this.$getProperty(result, 'readingName', '')
        let title = this.$getProperty(result, 'widgetName', '')
        this.peakDemand = peakVal
        this.peakTime = peakTime
        this.readingName = name
        this.widgetName = title
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
