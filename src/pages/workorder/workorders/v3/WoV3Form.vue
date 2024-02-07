<script>
import CustomModulesCreation from 'src/pages/custom-module/CustomModulesCreation'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import Tasks from '@/mixins/tasks/TasksMixin'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { deepCloneObject } from 'util/utility-methods'
import { isEmpty } from '@facilio/utils/validation'
export default {
  extends: CustomModulesCreation,
  mixins: [FetchViewsMixin, Tasks],
  name: 'WoV3Form',

  methods: {
    async redirectToList() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'workorderhomev1',
        })
      }
    },
    async redirectToSummary(id) {
      this.isSaving = true
      let currentView = await this.fetchView(this.moduleName)
      this.isSaving = false
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: currentView,
              id,
            },
          })
      } else {
        this.$router.push({
          name: 'wosummarynew',
          params: {
            viewname: currentView,
            id,
          },
        })
      }
    },
    afterSerializeHook({ data }) {
      const comp = this.$refs['f-webform']

      let tasksList = comp.getTasksList()
      let tasksSerializedData = {}
      let sequenceNumber = 1
      let sectionNameList = []

      // Handling for workorder: Adding taskString
      tasksList.forEach(task => {
        let { tasks, section } = task
        tasksSerializedData[section] = []
        tasks.forEach(task => {
          let _task = this.serializeTaskData(
            deepCloneObject(task),
            sequenceNumber++
          )
          tasksSerializedData[section].push(_task)
        })
        if (!isEmpty(section)) {
          sectionNameList.push(section)
        }
      })

      data = {
        ...data,
        tasksString: tasksSerializedData,
        sectionNameList,
      }

      return data
    },
    isDependantWO() {
      let { parentWO } = this.$route.query || {}
      return !isEmpty(parentWO)
    },
    modifyFieldPropsHook(field) {
      let { parentWO } = this.$route.query || {}
      let { siteId } = this.$route.query || {}
      let { name } = field || {}
      if (this.isDependantWO() && name === 'parentWO') {
        return { ...field, isDisabled: true, value: parseInt(parentWO) }
      } else if (this.isDependantWO() && name === 'siteId') {
        return { ...field, isDisabled: true, value: siteId }
      }
    },
  },
}
</script>
