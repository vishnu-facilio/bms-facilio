<script>
import CustomModulesCreation from 'src/pages/custom-module/CustomModulesCreation'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import { isEmpty, isFunction } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { mapGetters } from 'vuex'

export default {
  extends: CustomModulesCreation,
  props: ['viewname'],
  mixins: [FetchViewsMixin],
  mounted() {
    this.$store.dispatch('loadSpaceCategory')
  },
  computed: {
    ...mapGetters(['getSpaceCategoryPickList']),
    spaceCategories() {
      return this.getSpaceCategoryPickList()
    },
    isEdit() {
      return this.getId ? true : false
    },
    getId() {
      let { $route } = this
      let { params } = $route || {}
      let { id } = params || {}
      return id || null
    },
    title() {
      let { moduleDisplayName } = this
      return this.isEdit
        ? `Edit ${moduleDisplayName}`
        : `Create ${moduleDisplayName}`
    },
    isWidgetsSupported() {
      return true
    },
    isV3Api() {
      return true
    },
    moduleName() {
      return 'tenantunit'
    },
    moduleDisplayName() {
      return 'Tenant Unit'
    },
  },
  methods: {
    afterSaveHook(response) {
      let { error, [this.moduleName]: record } = response || {}

      if (isEmpty(error)) this.redirectToSummary(record.id)
    },
    async redirectToSummary(id) {
      let currentView = await this.fetchView(this.moduleName)
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
          name: 'tenantUnitSummary',
          params: {
            viewname: currentView,
            id,
          },
        })
      }
    },
    redirectToList() {
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
          name: 'tenantunit-list',
          params: { viewname },
          query: this.$route.query,
        })
      }
    },
    async saveRecord(formModel) {
      let { formObj, afterSaveHook, afterSerializeHook } = this

      let tenantUnitCategoryId
      Object.keys(this.spaceCategories).forEach(key => {
        if (this.spaceCategories[key] === 'Tenant Unit') {
          tenantUnitCategoryId = key
        }
      })
      if (!isEmpty(tenantUnitCategoryId)) {
        formModel['spaceCategory'] = {
          id: tenantUnitCategoryId,
        }
      }
      this.isSaving = true
      //same params as serialize method
      let response = await this.moduleData.save(
        formObj,
        formModel,
        afterSerializeHook
      )
      this.isSaving = false

      // Hook to handle notification after crud operation
      this.notificationHandler(response)

      // Hook to handle response after crud operation
      if (!isEmpty(afterSaveHook) && isFunction(afterSaveHook)) {
        this.afterSaveHook(response)
      }
    },
  },
}
</script>
