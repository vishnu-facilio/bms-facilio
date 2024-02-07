<script>
import AnnoucementSummary from 'src/pages/community/announcements/AnnouncementSummary'
import { API } from '@facilio/api'
import { findRouteForModule, pageTypes, getApp } from '@facilio/router'
import { mapGetters, mapState } from 'vuex'

export default {
  extends: AnnoucementSummary,
  computed: {
    ...mapState({
      currentTab: state => state.webtabs.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),
    canShowActionButtons() {
      let { linkName } = getApp()

      if (['vendor', 'tenant', 'service'].includes(linkName)) {
        let { $getProperty, $account, record } = this
        let { hasUpdatePermission, hasDeletePermission } = this

        return (
          (hasUpdatePermission || hasDeletePermission) &&
          $getProperty(record, 'sysCreatedBy.id') ===
            $getProperty($account, 'user.id')
        )
      }
      return false
    },
    hasUpdatePermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('UPDATE', currentTab)
    },
    hasDeletePermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('DELETE', currentTab)
    },
  },
  watch: {
    loading: {
      handler(newVal) {
        if (!newVal && this.record && !this.record.isRead) {
          this.markAsRead()
        }
      },
      immediate: true,
    },
  },
  methods: {
    async markAsRead() {
      let { moduleName } = this
      let param = { id: this.record.id, data: { isRead: true } }
      let { error } = await API.updateRecord(moduleName, param)

      if (error) {
        this.$message.error(
          error.message || 'Error Occurred while marking as read'
        )
      }
    },
    goToList() {
      let { viewname } = this
      let route = findRouteForModule('peopleannouncement', pageTypes.LIST)

      if (route) {
        this.$router.push({ name: route.name, params: { viewname } })
      } else {
        console.warn('Could not resolve route')
      }
    },
    goToEdit() {
      let route = findRouteForModule('peopleannouncement', pageTypes.EDIT)

      if (route) {
        let { id } = this
        this.$router.push({ name: route.name, params: { id } })
      } else {
        console.warn('Could not resolve route')
      }
    },
    dropdownActionHandler(command) {
      if (command === 'edit') {
        this.goToEdit()
      } else if (command === 'delete') {
        this.deleteRecord()
      }
    },
  },
}
</script>
