<script>
import FormCreation from '@/base/FormCreation'
import { isEmpty } from '@facilio/utils/validation'
import FetchViewsMixin from '@/base/FetchViewsMixin'

import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import { API } from '@facilio/api'

export default {
  extends: FormCreation,
  mixins: [FetchViewsMixin],
  data() {
    return {
      viewname: null,
    }
  },
  mounted() {
    this.setViewName()
  },

  computed: {
    moduleName() {
      return 'announcement'
    },
    moduleDisplayName() {
      return 'Announcement'
    },

    isV3Api() {
      return true
    },
    isWidgetsSupported() {
      return true
    },
    title() {
      let { moduleDisplayName, moduleDataId } = this
      let title = ``
      if (this.isClone) {
        title = `Clone ${moduleDisplayName}`
      } else if (!isEmpty(moduleDataId)) {
        title = `Edit ${moduleDisplayName}`
      } else {
        title = `Create ${moduleDisplayName}`
      }
      return title
    },
    isClone() {
      return this.$route.query.clone
    },
  },
  methods: {
    async redirectToOverview({ id }) {
      let { moduleName, viewname } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.push({ name, params: { viewname, id } })
        }
      } else {
        this.$router.push({
          name: 'announcementSummary',
          params: { viewname, id },
        })
      }
    },
    async setViewName() {
      let { moduleName } = this
      this.viewname = await this.fetchView(moduleName)
    },
    redirectToList() {
      let { moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}

        if (name) {
          this.$router.push({ name })
        }
      } else {
        this.$router.push({
          name: 'announcementsList',
        })
      }
    },
    async saveRecord(formModel) {
      let { moduleDataId, formObj } = this
      let { formId } = formModel
      this.isSaving = true
      let moduleData = this.$helpers.cloneObject(
        this.serializedData(formObj, formModel)
      )
      if (!isEmpty(formId)) {
        moduleData.formId = formId
      }
      if (this.isClone || isEmpty(moduleDataId)) {
        await this.createAnnouncement(moduleData)
      } else {
        let { announcement, error } = await API.updateRecord(`announcement`, {
          id: moduleDataId,
          data: moduleData,
        })
        if (error) {
          let { message = 'Error Occured while Updating Announcement' } = error
          this.$message.error(message)
        } else {
          this.$message.success(`Announcement Updated successfully`)
          this.redirectToOverview(announcement)
        }
        this.isSaving = false
      }
    },
    async createAnnouncement(moduleData) {
      if (!isEmpty(moduleData.announcementattachments)) {
        moduleData.announcementattachments = moduleData.announcementattachments.map(
          item => {
            return {
              fileId: item.fileId,
              createdTime: item.createdTime,
            }
          }
        )
      }
      let { announcement, error } = await API.createRecord(`announcement`, {
        data: moduleData,
        params: {
          clone: this.isClone ? true : false,
        },
      })
      if (error) {
        let {
          message = this.isClone
            ? 'Error Occured while Cloning Announcement'
            : 'Error Occured while Creating Announcement',
        } = error
        this.$message.error(message)
      } else {
        this.$message.success(
          this.isClone
            ? 'Anouncement Cloned Successfully'
            : 'Anouncement Created Successfully'
        )
        this.redirectToOverview(announcement)
      }
      this.isSaving = false
    },
  },
}
</script>
