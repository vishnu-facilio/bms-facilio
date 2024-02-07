<template>
  <div class="space-readings-card-con">
    <div class="space-readings-card">
      <div
        v-for="(displayReading, index) in readingsDisplayFieldsList"
        :key="index"
        :class="[
          'readings-content',
          index != readingsDisplayFieldsList.length - 1 &&
            'space-readings-border',
        ]"
        :style="{
          width: 100 / readingsDisplayFieldsList.length + '%',
        }"
      >
        <div
          class="f18 text-align-center"
          :style="{
            color:
              displayReading.colorCode || colorCodes[index % colorCodes.length],
          }"
        >
          <template v-if="displayReading.hasOwnProperty('value')">
            {{
              displayReading.value
                ? displayReading.value + ' ' + (displayReading.unit || '')
                : '---'
            }}
          </template>
          <template v-else>
            {{ readingsMap[displayReading.name] || '---' }}
          </template>
        </div>
        <div class="text mT5">
          {{ displayReading.displayName }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { eventBus } from '../utils/eventBus'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { getDisplayValue } from 'util/field-utils'
export default {
  props: ['details', 'widget'],
  data() {
    return {
      readingsMap: {},
      loading: false,
      readingsDisplayFieldsList: [],
      readings: [
        {
          name: 'temperature',
          displayName: this.$t('space.sites.temperature'),
          colorCode: '#3478f6',
        },
        {
          name: 'setPoint',
          displayName: this.$t('space.sites.set_point'),
          colorCode: '#3478f6',
        },
        {
          name: 'humidity',
          displayName: this.$t('space.sites.humidity'),
          colorCode: '#5fb2e0',
        },
        {
          name: 'co2',
          displayName: this.$t('space.sites.co2'),
          colorCode: '#4bbfad',
        },
        {
          name: 'currentOccupancy',
          displayName: this.$t('space.sites.current_occup'),
          colorCode: '#f0886b',
        },
      ],
      colorCodes: ['#3478f6', '#5fb2e0', '#4bbfad', '#f0886b'],
    }
  },
  created() {
    this.loadSpaceReadings()
    let workflowId = this.$getProperty(this.widget, 'widgetParams.workflowId')
    if (workflowId) {
      this.runWorkflow(workflowId)
    } else {
      this.readingsDisplayFieldsList = this.readings
    }
  },
  methods: {
    async runWorkflow(workflowId) {
      let { data } = await API.post('/v2/workflow/runWorkflow', {
        workflowId: workflowId,
        paramList: [this.details.id],
      })
      if (data && data.workflow) {
        let { workflow } = data
        let { returnValue = [] } = workflow
        if (returnValue.length) {
          this.readingsDisplayFieldsList = returnValue
          return
        }
      }
      this.readingsDisplayFieldsList = this.readings
    },
    loadSpaceReadings() {
      let { details = {} } = this
      let { id } = details
      let excludeEmptyFields = this.$account.org.orgId === 210 ? false : true
      this.loading = true
      this.$util
        .loadLatestReading(id, excludeEmptyFields)
        .then(readingsData => {
          if (readingsData) {
            readingsData.forEach(reading => {
              let { field = {}, value } = reading
              let { name: fieldName } = field
              this.$set(
                this.readingsMap,
                `${fieldName}`,
                !isEmpty(value) ? getDisplayValue(field, value) : '--'
              )
            })
            eventBus.$emit('occupancyCount', this.readingsMap.assignedOccupancy)
          }
          this.loading = false
        })
    },
  },
}
</script>
