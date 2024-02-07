<template>
  <div class="height100 pL50">
    <router-view :key="uniqueKey"></router-view>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  async created() {
    let connectedAppFilters = encodeURIComponent(
      JSON.stringify({ entityType: { operatorId: 9, value: ['1'] } })
    )
    let customModulesUrl = '/v2/module/list'
    let connectedAppsUrl = `/v2/connectedApps/widgetList?filters=${connectedAppFilters}`

    let promises = []
    let modulesList = []
    let appsList = []
    promises.push(API.get(customModulesUrl))
    promises.push(API.get(connectedAppsUrl))

    let [modulesListData, connecedAppsData] = await Promise.all(promises)

    try {
      if (!isEmpty(modulesListData)) {
        let {
          data: { moduleList = {}, error },
        } = modulesListData
        if (error) {
          throw new Error(error.message)
        } else {
          modulesList = moduleList || []
        }
      }
      if (!isEmpty(connecedAppsData)) {
        let {
          data: { connectedAppWidgets = {}, error },
        } = connecedAppsData
        if (error) {
          throw new Error(error.message)
        } else {
          appsList = connectedAppWidgets || []
        }
      }
      if (!isEmpty(modulesList) || !isEmpty(appsList)) {
        this.deserializeModulesData({ modulesList, appsList })
      }
    } catch (error) {
      this.$message.error(error.message || 'Error Occurred')
    }
  },
  data() {
    return {
      product: {
        code: 'ca',
        label: `${this.$t('custommodules.list.connected_apps')}`,
        path: 'app/ca',
        modules: [],
      },
    }
  },
  computed: {
    uniqueKey() {
      let { params } = this.$route || {}
      let { moduleName } = params || {}
      return `moduleName-${moduleName}`
    },
  },
  methods: {
    deserializeModulesData({ modulesList, appsList }) {
      let finalArr = []
      let { product, $router } = this
      let { currentRoute } = $router
      let { params } = currentRoute || {}
      if (this.$account.user.email === 'moro@facilio.com') {
        // to be removed
        modulesList = modulesList.filter(
          m => !['custom_servicebilllineitems'].includes(m.name)
        )
      }
      if (this.$constants.isCustomModulePermissionsEnabled(this.$org.id)) {
        modulesList = modulesList.filter(moduleDetail => {
          return this.$hasPermission(`${moduleDetail.name}:READ`)
        })
      }
      let modulesArr = (modulesList || []).map(modules => {
        return {
          label: modules.displayName,
          path: {
            path: `/app/ca/modules/${modules.name}`,
          },
        }
      })
      // Urgent fix to hide connectedApp widgets in investa will be removed when mainApp to webtab migration
      if ([17, 659].includes(this.$org.id)) {
        appsList = []
      }
      let appsArr = appsList.map(widget => {
        return {
          label: widget.widgetName,
          path: {
            path: `/app/ca/apps/${widget.id}`,
          },
        }
      })
      finalArr = modulesArr.concat(appsArr)
      if (!isEmpty(finalArr)) {
        finalArr = finalArr.concat([
          {
            label: this.$t('common.header.reports'),
            path: { path: '/app/ca/reports' },
            icon: 'fa fa-bar-chart-o',
          },
        ])
      }
      this.$set(this.product, 'modules', finalArr)
      this.$store.dispatch('switchProduct', product)
      if (this?.$route?.query?.module && this?.$route?.query?.reportId) {
        this.$router.push(this.$route.fullPath)
        return
      }
      if (this?.$route?.fullPath === '/app/ca/reports') {
        this.$router.push(this.$route.fullPath)
        return
      }
      if (isEmpty(params) && !isEmpty(finalArr[0])) {
        this.$router.push(finalArr[0].path)
      }
    },
  },
}
</script>

<style></style>
