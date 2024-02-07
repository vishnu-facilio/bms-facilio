<template>
  <div class="site-details-card p30">
    <div class="flex-middle justify-content-space width100 height100">
      <div
        v-for="(module, index) in modulesList"
        :key="index"
        :class="[
          'text-center width33',
          index !== modulesList.length - 1 && 'space-card-border-right',
        ]"
      >
        <div v-if="loading">
          <spinner :show="true"></spinner>
        </div>
        <div v-else>
          <div>
            <div v-if="module.key === 'fire_alarms'" class="warning-alarm">
              <InlineSvg
                :src="`svgs/info-warning`"
                class="vertical-middle"
                iconClass="icon icon-xxs"
              ></InlineSvg>
              <InlineSvg
                :src="`svgs/${module.icon}`"
                class="vertical-middle"
                iconClass="icon icon-lg"
              ></InlineSvg>
            </div>
            <InlineSvg
              v-else
              :src="`svgs/${module.icon}`"
              class="vertical-middle"
              iconClass="icon icon-lg"
            ></InlineSvg>
            <div
              @click="redirectToModuleList(module.key, recordId)"
              class="fc-black-com f24 pT10 bold pointer"
            >
              {{ $getProperty(moduleCountMap, `${module.key}.count`, '---') }}
            </div>

            <div class="fc-black-com text-uppercase f12 bold mT5">
              {{
                $getProperty(moduleCountMap, `${module.key}.displayName`, '---')
              }}
            </div>
          </div>
        </div>
      </div>
      <div
        class="text-center border-left4 pL20"
        v-if="this.$org.id === 349 && details.data && details.data.number"
      >
        <InlineSvg
          src="svgs/spacemanagement/room"
          class="vertical-middle"
          iconClass="icon icon-xxll"
        ></InlineSvg>
        <div class="fc-black-com f24 pT10 bold pointer">
          {{ details.data.number }}
        </div>
        <div class="fc-black-com text-uppercase f12 bold mT5">
          {{ $t('space.sites.accomendation_rooms') }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { isArray } from '@facilio/utils/validation'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['details', 'moduleName'],
  data() {
    return {
      moduleCountMap: {},
      modulesList: [
        {
          key: 'work_orders',
          icon: 'spacemanagement/workorder',
        },
        {
          key: 'fire_alarms',
          icon: 'spacemanagement/alarm',
        },
        {
          key: 'bms_alarms',
          icon: 'spacemanagement/alarm',
        },
        {
          key: 'assets',
          icon: 'spacemanagement/asset',
        },
      ],
      moduleVsFetchUrl: {
        site: {
          url: 'campus/reportcards',
          paramKey: 'campusId',
        },
        building: {
          url: 'building/reportcards',
          paramKey: 'buildingId',
        },
        floor: {
          url: 'floor/reportcards',
          paramKey: 'floorId',
        },
        space: {
          url: 'space/reportcards',
          paramKey: 'spaceId',
        },
      },
      loading: false,
    }
  },
  computed: {
    recordId() {
      return (this.details || {}).id || -1
    },
  },
  created() {
    this.loadCardDetails()
  },
  methods: {
    loadCardDetails() {
      let { recordId, moduleVsFetchUrl, moduleName } = this
      if (recordId > 0) {
        this.loading = true
        let params = {
          [moduleVsFetchUrl[moduleName].paramKey]: recordId,
          fetchReportCardsMeta: ['moduleCount'],
        }
        this.$http
          .post(`${moduleVsFetchUrl[moduleName].url}`, params)
          .then(({ data: { reportcards }, status }) => {
            if (status === 200) {
              if (reportcards) {
                if (isArray(reportcards)) {
                  reportcards.forEach(card => {
                    this.$set(this.moduleCountMap, `${card.name}`, {
                      count: card.data,
                      displayName: card.label,
                    })
                  })
                }
              }
            } else {
              throw new Error('Error Occurred')
            }
          })
          .catch(({ message }) => {
            this.$message.error(message)
          })
          .finally(() => {
            this.loading = false
          })
      }
    },
    redirectToModuleList(param, id) {
      if (param) {
        let filter = { operatorId: 38, value: [`${id}`] }
        let routeDetails = {
          work_orders: {
            path: '/app/wo/orders/open',
            module: 'workorder',
            viewname: 'open',
          },
          fire_alarms: {
            path: '/app/fa/faults/active',
            module: 'newreadingalarm',
            viewname: 'active',
          },
          bms_alarms: {
            path: '/app/fa/bmsalarms/bmsActive',
            module: 'bmsalarm',
            viewname: 'bmsActive',
          },
          assets: {
            path: '/app/at/assets/all',
            module: 'asset',
            viewname: 'all',
          },
        }

        if (['work_orders', 'fire_alarms', 'bms_alarms'].includes(param)) {
          let { path, module, viewname } = routeDetails[param] || {}
          let searchFilter = { resource: { module, ...filter } }
          let query = {
            includeParentFilter: true,
            search: JSON.stringify(searchFilter),
          }

          if (isWebTabsEnabled()) {
            let { name } = findRouteForModule(module, pageTypes.LIST) || {}
            if (name) {
              this.$router.push({ name, params: { viewname }, query })
            }
          } else {
            this.$router.push({ path, query })
          }
        } else if (param === 'assets') {
          let { path, module, viewname } = routeDetails[param] || {}
          let searchFilter = { space: { ...filter } }
          let query = { search: JSON.stringify(searchFilter) }

          if (isWebTabsEnabled()) {
            let { name } = findRouteForModule(module, pageTypes.LIST) || {}
            if (name) {
              this.$router.push({ name, params: { viewname }, query })
            }
          } else {
            this.$router.push({ path, query })
          }
        }
      } else {
        return null
      }
    },
  },
}
</script>
<style scoped>
.warning-alarm {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  margin-top: -18px;
}
</style>
