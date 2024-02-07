<script>
import ViewHeader from 'newapp/components/ViewHeader'
import { isEmpty } from '@facilio/utils/validation'
import ApprovalViewMixin from './ApprovalViewMixin'

export default {
  extends: ViewHeader,
  mixins: [ApprovalViewMixin],
  props: [
    'moduleName',
    'pathPrefix',
    'isActivityView',
    'showEditIcon',
    'moduleList',
  ],
  data() {
    return {
      moduleVsViewList: {},
      showAllViews: false,
    }
  },
  async created() {
    await this.loadApprovalModules()
    this.initializeViews()
    if (!this.isActivityView) {
      this.loadViewDetails()
    }
  },
  computed: {
    currentGroup() {
      let { groupViews, moduleName } = this
      if (isEmpty(moduleName) || isEmpty(groupViews)) {
        return null
      } else {
        return groupViews.find(group => group.moduleName === moduleName)
      }
    },
    currentViews() {
      let { currentGroup } = this
      if (isEmpty(currentGroup)) {
        return []
      }

      let { views: selectedViews = [] } = currentGroup
      return selectedViews.map(view => ({
        label: view.displayName,
        id: view.id,
        name: view.name,
        isCustom: !view.isDefault,
        primary: view.primary,
      }))
    },
  },
  watch: {
    moduleName: {
      handler: function() {
        this.initializeViews()
        if (!this.isActivityView) {
          this.loadViewDetails()
        }
      },
    },
    currentView(newVal, oldVal) {
      if (newVal !== oldVal) {
        if (!this.isActivityView) {
          this.loadViewDetails()
        }
      }
    },
  },
  methods: {
    init() {},
    initializeViews() {
      let { currentView, groupViews, $getProperty } = this
      let firstGroupName = $getProperty(groupViews, '0.name') || null

      if (firstGroupName && isEmpty(currentView)) this.openGroup(firstGroupName)
    },
  },
}
</script>
