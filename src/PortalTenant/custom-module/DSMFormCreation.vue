<script>
import DSMFormCreation from 'pages/custom-module/DSMFormCreation.vue'
import { findRouteForModule, pageTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import FetchViewsMixin from '@/base/FetchViewsMixin'
export default {
  extends: DSMFormCreation,
  mixins: [FetchViewsMixin],
  computed: {
    moduleName() {
      return this.$attrs.moduleName
    },
    moduleDisplayName() {
      if (this.formObj && this.formObj.module) {
        return this.$getProperty(this.formObj, 'module.displayName')
      }
      return ''
    },
    isV3Api() {
      return true
    },
  },
  methods: {
    afterSaveHook(response) {
      let { [this.moduleName]: data } = response
      if (!isEmpty(data)) {
        let { id } = data || {}
        this.redirectToSummary(id)
      }
    },
    redirectToList() {
      let route = findRouteForModule(this.moduleName, pageTypes.LIST)
      if (route) {
        this.$router.push({ name: route.name })
      } else {
        console.warn('Could not resolve route')
      }
    },
    async redirectToSummary(id) {
      let viewname = await this.fetchView(this.moduleName)
      let { name } = findRouteForModule(this.moduleName, pageTypes.OVERVIEW)
      if (name) {
        this.$router.push({ name, params: { viewname, id } })
      } else {
        console.warn(this.$t('common._common.could_not_resolve_route'))
      }
    },
  },
}
</script>

<style scoped>
.dsm-main-container {
  background-color: #f7f8f9;
  height: 100vh;
  display: flex;
  align-items: center;
  flex-direction: column;
  position: relative;
}
.dsm-form-wrapper {
  width: 100%;
}
.form-empty-container {
  width: 700px;
  height: calc(100vh - 100px);
  margin-top: 60px;
  background-color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  border-radius: 8px;
  box-shadow: 0 0 1px rgba(67, 90, 111, 0.3),
    0 2px 4px -2px rgba(67, 90, 111, 0.47);
}

.dsm-form-container {
  z-index: 20;
  height: calc(100vh - 10px);
  overflow-y: scroll;
  padding: 0px 10px;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.loading-container {
  width: 700px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}
.form-switch {
  width: 600px;
}
.form-pattern-container {
  width: 100%;
  height: 100%;
  opacity: 0.05;
  top: 0px;
  z-index: 10;
  position: absolute;
}
.form-background-pattern {
  width: 100%;
}
</style>
