<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  data() {
    return {
      readingAlarmCategory: null,
      alarmSeverity: null,
    }
  },
  methods: {
    async loadReadingAlarmCategory() {
      let url = 'v2/module/data/list?moduleName=readingAlarmCategory'
      let { data, error } = await API.get(url)
      if (isEmpty(error)) {
        this.readingAlarmCategory = data.moduleDatas
      }
    },
    getReadingAlarmCategory(id) {
      let { readingAlarmCategory } = this
      return (readingAlarmCategory || []).find(
        readingAlarmCategory => readingAlarmCategory.id === id
      )
    },
    async loadAlarmSeverity() {
      let url = 'v2/module/data/list?moduleName=alarmseverity'
      let { data, error } = await API.get(url)
      if (isEmpty(error)) {
        this.alarmSeverity = data.moduleDatas
      }
    },
    getAlarmSeverityWithId(id) {
      let { alarmSeverity } = this
      return alarmSeverity
        ? (alarmSeverity || []).find(alarmSeverity => alarmSeverity.id === id)
        : null
    },
  },
}
</script>
