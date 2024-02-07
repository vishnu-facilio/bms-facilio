<script>
import InspectionTemplateList from 'pages/inspection/inspection-template/InspectionTemplateList'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: InspectionTemplateList,
  name: 'InductionTemplateList',
  computed: {
    createText() {
      return this.$t('qanda.template.new_induction')
    },
    parentPath() {
      return '/app/induction/template'
    },
    viewManagerRoute() {
      return `/app/induction/${this.moduleName}/viewmanager`
    },
  },
  methods: {
    openSummary(id) {
      let { viewname } = this
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

          params: { id, viewname },
          query: this.$route.query,
        })
      }
    },
    openList() {
      let { viewname } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'inductionTemplateList',
          params: { viewname },
          query: this.$route.query,
        })
      }
    },
    openCreate() {
      let { viewname } = this
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.CREATE) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'inductionTemplateCreate',
          params: { viewname },
          query: this.$route.query,
        })
      }
    },
    editRecord(record) {
      let { id } = record || {}
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT)
        name && this.$router.push({ name, params: { id } })
      } else {
        this.$router.push({
          name: 'inductionTemplateEdit',
          params: { id },
        })
      }
    },
  },
}
</script>

<style></style>
