<script>
import NewInspectionTemplate from 'pages/inspection/inspection-template/NewInspectionTemplate'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: NewInspectionTemplate,
  mixins: [FetchViewsMixin],
  computed: {
    moduleName() {
      return 'inductionTemplate'
    },
  },
  methods: {
    async redirectToSummary(id) {
      let viewname = await this.fetchView(this.moduleName)

      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname,
              id,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'inductionTemplateSummary',
          params: { id, viewname: 'all' },
        })
      }
    },
    async redirectToList() {
      let viewname = await this.fetchView(this.moduleName)
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            query: this.$route.query,
            params: {
              viewname: viewname,
            },
          })
      } else {
        this.$router.push({
          name: 'inductionTemplateList',
          params: { viewname: 'all' },
        })
      }
    },
  },
}
</script>

<style></style>
