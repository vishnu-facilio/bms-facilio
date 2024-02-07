<template>
  <FContainer>
    <FTable
      :columns="columns"
      :data="details.readings"
      hideBorder
      :showSelect="false"
    >
      <template #[`cell.readingId`]="record">
        <FText color="textMain" appearance="bodyReg14">{{
          getReadingId(record.row)
        }}</FText>
      </template>
      <template #[`cell.name`]="record">
        <FText color="textMain" appearance="bodyReg14">{{
          getReadingName(record.row)
        }}</FText>
      </template>
      <template #[`cell.type`]="record">
        <FText color="textMain" appearance="bodyReg14">{{
          getReadingDataType(record.row)
        }}</FText>
      </template>
      <template #[`cell.interval`]="record">
        <FText color="textMain" appearance="bodyReg14">{{
          getInterval(record.row)
        }}</FText>
      </template>
      <template #[`cell.unit`]="record">
        <FText color="textMain" appearance="bodyReg14">{{
          getUnit(record.row)
        }}</FText>
      </template>
      <template #[`cell.status`]="record">
        <FSwitch
          v-model="record.row.status"
          @change="changeStatus(record.row)"
        />
      </template>
    </FTable>
  </FContainer>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { FTable, FContainer, FSwitch, FText } from '@facilio/design-system'

const getFreqInterval = freq => {
  const frequencies = {
    1: '1 Min',
    2: '2 Mins',
    3: '3 Mins',
    4: '4 Mins',
    5: '5 Mins',
    6: '10 Mins',
    7: '15 Mins',
    8: '20 Mins',
    9: '30 Mins',
    10: '1 Hr',
    11: '2 Hr',
    12: '3 Hr',
    13: '4 Hr',
    14: '8 Hr',
    15: '12 Hr',
    16: '1 Day',
  }
  if (frequencies[freq]) {
    return frequencies[freq]
  } else {
    return '--'
  }
}

export default {
  props: ['details', 'widget'],
  components: {
    FTable,
    FContainer,
    FSwitch,
    FText,
  },
  data() {
    return {
      isLoading: false,
      status: true,
      readingList: [],
      columns: [
        { displayName: 'ID', name: 'readingId', id: 1 },
        { displayName: 'Reading Name', name: 'name', id: 2 },
        { displayName: 'Type', name: 'type', id: 3 },
        { displayName: 'Interval', name: 'interval', id: 4 },
        { displayName: 'Unit', name: 'unit', id: 5 },
        { displayName: 'Status', name: 'status', id: 6, type: 'status' },
      ],
    }
  },
  async created() {
    await this.getAllMeterReadings()
  },
  methods: {
    getReadingId(reading) {
      let id = this.$getProperty(reading, 'readingFieldId', '--')
      return '#' + id
    },
    getReadingName(reading) {
      let { readingList } = this
      let id = this.$getProperty(reading, 'readingFieldId')
      let readingName = (readingList.find(readings => readings.id === id) || {})
        .displayName
      return !isEmpty(readingName) ? readingName : '--'
    },
    async changeStatus(reading) {
      let recordId = this.$getProperty(reading, 'id')
      let response = {}
      if (!isEmpty(recordId)) {
        response = await API.updateRecord('virtualMeterTemplateReading', {
          id: recordId,
          data: reading,
        })
        let { error } = response || {}
        if (!error) {
          this.$message.success(
            this.$t('asset.virtual_meters.reading_status_updated')
          )
        } else {
          this.$message.error(error.message || 'Error Occurred')
        }
      }
    },
    getInterval(reading) {
      let frequency = this.$getProperty(reading, 'frequency')
      return getFreqInterval(frequency)
    },
    getReadingDataType(reading) {
      let { readingList } = this
      let id = this.$getProperty(reading, 'readingFieldId')
      let counterField = (
        readingList.find(readings => readings.id === id) || {}
      ).counterField
      let dataType = (readingList.find(readings => readings.id === id) || {})
        .dataType
      return counterField ? 'Counter' : this.$constants.dataType[dataType]
    },
    getUnit(reading) {
      let { readingList } = this
      let id = this.$getProperty(reading, 'readingFieldId')
      let unit = (readingList.find(readings => readings.id === id) || {}).unit
      return !isEmpty(unit) ? unit : '--'
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