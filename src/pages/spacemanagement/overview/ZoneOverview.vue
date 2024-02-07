<template>
  <div class="scrollable-y100">
    <div v-if="loading">
      <spinner :show="loading"></spinner>
    </div>
    <div class="fc-space-o-layout p15 pR0" v-else>
      <div class="sp-div-row"></div>
      <div class="fc-space-header row">
        <div class="col-3 col-sm-3 col-lg-3 pull-left self-center f18 fw5 pL30">
          Zone Overview
        </div>
      </div>
      <div class="fc-space">
        <div class="fc-space-content">
          <div class="row sm-gutter" style="margin-top: 10px">
            <div class="col-12">
              <div
                class="container db-container db-space-second-row fc-position-relative fc-border-radius sp-row-1 fc-border-1"
              >
                <div class="sp-div-row"></div>
                <div class="sp-div-row"></div>
                <div class="row item-center text-center self-center p10">
                  <div class="col-4 fc-margin-auto">
                    <space-avatar
                      size="shuge"
                      name="false"
                      :space="zone"
                    ></space-avatar>
                    <div class="sp-div-row"></div>
                    <div class="text-center p5 f16 fw5">{{ zone.name }}</div>
                    <div class="text-center f12 space-secondary-color">
                      Zone ID:
                      <span class="fc-identify-color-1 f11 fw5"
                        >#{{ zone.id }}</span
                      >
                    </div>
                    <div class="sp-div-row"></div>
                    <div class="sp-div-row"></div>
                    <div
                      class="text-center p5 f16 fw5"
                      v-if="zone.tenantZone && tenant.id && tenant.id > 0"
                    >
                      Occupied By {{ tenant.name }}
                    </div>
                    <div
                      class="text-center f12 space-secondary-color"
                      v-if="zone.tenantZone && tenant.id && tenant.id > 0"
                      style="cursor: pointer"
                      @click="openTenant(tenant.id)"
                    >
                      Tenant ID:
                      <span class="fc-identify-color-1 f11 fw5"
                        >#{{ tenant.id }}</span
                      >
                    </div>
                  </div>
                </div>

                <div class="row">
                  <div class="col-12 fc-margin-auto text-center">
                    <div class="row fc-margin-auto text-center justify-center">
                      <div class="col-3">
                        <div
                          class="fc-avatar sp-avatar-xxlg primary-color fc-border-none sp-color-1 f30"
                        >
                          {{ children.length }}
                        </div>
                        <div class="f12 space-secondary-color mtb10 uppercase">
                          spaces
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="sp-div-row"></div>
                <div class="sp-div-row"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="sp-div-row"></div>
      <div class="sp-div-row"></div>
      <div class="row sp-card-container ellipsis site-cards">
        <div
          class="col-3"
          v-for="(reportcard, index) in reportcards"
          :key="index"
        >
          <div
            class="container db-container fc-position-relative fc-border-radius p0 text-center fc-border-1"
          >
            <space-card
              :mainMeter="mainMeter"
              :tempData="reportcard"
              @ValueClick="filterAll"
            ></space-card>
          </div>
        </div>
      </div>
      <div class="row pT30" v-if="subZones && subZones.length">
        <div class="col-12">
          <div class="container db-container fc-border-1 fc-border-radius">
            <div class="q-list q-list-header db-list-header">
              <div class="pull-left pL30">
                Zones
                <span class="secondary-color">({{ subZones.length }})</span>
              </div>
            </div>
            <table class="q-table striped-odd db-tabel hover-effect">
              <thead>
                <tr>
                  <th class="text-left uppercase space-secondary-color sp-td">
                    Zone id
                  </th>
                  <th class="text-left uppercase space-secondary-color sp-td">
                    Zone name
                  </th>
                  <th class="text-left uppercase space-secondary-color sp-td">
                    Zone description
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="(zone, index) in subZones"
                  :key="index"
                  class="pointer"
                  @click="openZoneLink(zone.id)"
                >
                  <td class="text-left db-bold uppercase sp-td">
                    #{{ zone.id }}
                  </td>
                  <td class="text-left db-bold fw5 sp-td">{{ zone.name }}</td>
                  <td class="text-left db-bold fw5 sp-td">
                    {{ zone.description }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <div class="row pT30" v-if="subSites && subSites.length">
        <div class="sp-div-row"></div>
        <div class="sp-div-row"></div>
        <div class="col-12">
          <div class="container db-container fc-border-1 fc-border-radius">
            <div class="q-list q-list-header db-list-header">
              <div class="pull-left p10 pL30">
                Sites
                <span class="secondary-color">({{ subSites.length }})</span>
              </div>
            </div>
            <table class="q-table striped-odd db-tabel hover-effect">
              <thead>
                <tr>
                  <th class="text-left uppercase space-secondary-color sp-td">
                    Site id
                  </th>
                  <th class="text-left uppercase space-secondary-color sp-td">
                    Site name
                  </th>
                  <th class="uppercase space-secondary-color text-center sp-td">
                    Buildings
                  </th>
                  <th class="uppercase space-secondary-color text-center sp-td">
                    Managed By
                  </th>
                  <th class="uppercase space-secondary-color text-center sp-td">
                    Floor Area
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="(site, index) in subSites"
                  :key="index"
                  class="pointer"
                  @click="openSiteLink(site.id)"
                >
                  <td class="text-left db-bold uppercase sp-td">
                    #{{ site.id }}
                  </td>
                  <td class="text-left db-bold fw5 sp-td">{{ site.name }}</td>
                  <td class="text-center db-bold fw5 sp-td">
                    {{ site.noOfBuildings | positive }}
                  </td>
                  <td class="text-center db-bold fw5 sp-td">
                    <user-avatar
                      size="md"
                      :user="site.managedBy"
                      v-if="site.managedBy"
                    ></user-avatar>
                    <div class="col-8 self-center fw5 ellipsis" v-else>--</div>
                  </td>
                  <td class="text-center db-bold-3 fw5 sp-td">
                    {{
                      $util.formateSqft(site.area) +
                        ' / ' +
                        $util.formateSqft(site.grossFloorArea)
                    }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <div class="row pT30" v-if="subBuildings && subBuildings.length">
        <div class="sp-div-row"></div>
        <div class="sp-div-row"></div>
        <div class="col-12">
          <div class="container db-container fc-border-1 fc-border-radius">
            <div class="q-list q-list-header db-list-header">
              <div class="pull-left p10 pL30">
                Buildings
                <span class="secondary-color">({{ subBuildings.length }})</span>
              </div>
            </div>
            <table class="q-table striped-odd db-tabel hover-effect">
              <thead>
                <tr>
                  <th class="text-left uppercase space-secondary-color sp-td">
                    Space id
                  </th>
                  <th class="text-left uppercase space-secondary-color sp-td">
                    Space name
                  </th>
                  <th class="uppercase space-secondary-color text-center sp-td">
                    CURRENT / MAX OCCUPANCY
                  </th>
                  <th class="uppercase space-secondary-color text-center sp-td">
                    AREA
                  </th>
                  <th class="uppercase space-secondary-color text-center sp-td">
                    FLOORS
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="(building, index) in subBuildings"
                  :key="index"
                  class="pointer"
                  @click="openBuildingLink(building.siteId, building.id)"
                >
                  <td class="text-left db-bold uppercase sp-td">
                    #{{ building.id }}
                  </td>
                  <td class="text-left db-bold fw5 sp-td">
                    {{ building.name }}
                  </td>
                  <td
                    class="text-center db-bold-3 clickable fw5 text-center sp-td"
                  >
                    {{ building.lastCurrentOccupancy | positive }} /
                    {{ building.maxOccupancy | positive }}
                  </td>
                  <td class="text-center db-bold-3 clickable fw5 sp-td">
                    {{ $util.formateSqft(building.area) }}
                  </td>
                  <td class="text-center db-bold-3 clickable fw5 sp-td">
                    {{ building.noOfFloors | positive }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <div class="row pT30" v-if="subFloors && subFloors.length">
        <div class="sp-div-row"></div>
        <div class="sp-div-row"></div>
        <div class="col-12">
          <div class="container db-container fc-border-1 fc-border-radius">
            <div class="q-list q-list-header db-list-header">
              <div class="pull-left p10 pL30">
                Floors
                <span class="secondary-color">({{ subFloors.length }})</span>
              </div>
            </div>
            <table class="q-table striped-odd db-tabel hover-effect">
              <thead>
                <tr>
                  <th class="text-left uppercase space-secondary-color sp-td">
                    Floor id
                  </th>
                  <th class="text-left uppercase space-secondary-color sp-td">
                    Floor name
                  </th>
                  <th class="uppercase space-secondary-color text-center sp-td">
                    CURRENT / MAX OCCUPANCY
                  </th>
                  <th class="uppercase space-secondary-color text-center sp-td">
                    AREA
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="(floor, index) in subFloors"
                  :key="index"
                  class="pointer"
                  @click="openFloorLink(floor.siteId, floor.id, floor)"
                >
                  <td class="text-left db-bold uppercase sp-td">
                    #{{ floor.id }}
                  </td>
                  <td class="text-left db-bold fw5 sp-td">{{ floor.name }}</td>
                  <td
                    class="text-center db-bold-3 clickable fw5 text-center sp-td"
                  >
                    {{ floor.lastCurrentOccupancy | positive }} /
                    {{ floor.maxOccupancy | positive }}
                  </td>
                  <td class="text-center db-bold-3 clickable fw5 sp-td">
                    {{ $util.formateSqft(floor.area) }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <div class="row pT30" v-if="subSpaces && subSpaces.length">
        <div class="col-12">
          <div class="container db-container fc-border-1 fc-border-radius">
            <div class="q-list q-list-header db-list-header">
              <div class="pull-left pL30">
                Spaces
                <span class="secondary-color">({{ subSpaces.length }})</span>
              </div>
            </div>
            <table class="q-table striped-odd db-tabel hover-effect">
              <thead>
                <tr>
                  <th class="text-left uppercase space-secondary-color sp-td">
                    Space id
                  </th>
                  <th class="text-left uppercase space-secondary-color sp-td">
                    Space name
                  </th>
                  <th class="text-left uppercase space-secondary-color sp-td">
                    Floor
                  </th>
                  <th class="uppercase space-secondary-color text-center sp-td">
                    Category
                  </th>
                  <th class="uppercase space-secondary-color text-center sp-td">
                    Area
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="(space, index) in subSpaces"
                  :key="index"
                  class="pointer"
                  @click="openSpaceLink(space.siteId, space.id)"
                >
                  <td class="text-left db-bold uppercase sp-td">
                    #{{ space.id }}
                  </td>
                  <td class="text-left db-bold fw5 sp-td">{{ space.name }}</td>
                  <td class="text-left db-bold fw5 sp-td">
                    {{ space.floor ? space.floor.name : '---' }}
                  </td>
                  <td
                    class="text-center db-bold clickable fw5 text-center sp-td"
                  >
                    {{ space.spaceCategory ? space.spaceCategory : '---' }}
                  </td>
                  <td class="text-center db-bold-3 clickable fw5 sp-td">
                    {{ $util.formateSqft(space.area) }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
import SpaceAvatar from '@/avatar/Space'
import SpaceMixin from '@/mixins/SpaceMixin'
import SpaceCard from 'pages/spacemanagement/components/cards/SpaceReadingCards'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  title() {
    return 'Space Management'
  },
  mixins: [SpaceMixin],
  components: {
    UserAvatar,
    SpaceAvatar,
    SpaceCard,
  },
  data() {
    return {
      mainMeter: null,
      zone: {},
      reportcards: [],
      childrenLoading: true,
      children: [],
      tenant: {},
      loading: false,
    }
  },
  created() {
    this.$store.dispatch('loadSpaceCategory')
    this.$store.dispatch('loadBuildings')
  },
  computed: {
    zoneId() {
      return parseInt(this.$route.params.zoneid)
    },
    subZones() {
      return this.children.filter(child => child.spaceTypeVal === 'Zone')
    },
    subSites() {
      return this.children.filter(child => child.spaceTypeVal === 'Site')
    },
    subBuildings() {
      return this.children.filter(child => child.spaceTypeVal === 'Building')
    },
    subFloors() {
      return this.children.filter(child => child.spaceTypeVal === 'Floor')
    },
    subSpaces() {
      return this.children.filter(child => child.spaceTypeVal === 'Space')
    },
  },
  mounted() {
    this.loadZone()
  },
  watch: {
    $route() {
      this.loadZone()
      this.loadChildren()
    },
  },
  methods: {
    loadZone() {
      let self = this
      self.loading = true
      self.$http.get('/zone/' + this.zoneId).then(function(response) {
        self.zone = response.data.zone
        if (self.zone.tenantZone) {
          self.loadTenant()
        }
        self.loadChildren()
        self.loading = false
      })
    },
    loadTenant() {
      let self = this
      self.loading = true
      self.$http
        .get('/zone/getTenant?zoneId=' + this.zoneId)
        .then(function(response) {
          self.tenant = response.data.tenant
        })
    },
    openTenant(id) {
      if (id) {
        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule('tenant', pageTypes.OVERVIEW) || {}
          if (name) {
            this.$router.replace({ name, params: { viewname: 'all', id } })
          }
        } else {
          let url = '/app/tm/tenants/all/' + id + '/overview'
          this.$router.replace({ path: url })
        }
      }
    },
    filterAll(param) {
      if (param) {
        let filter = { operatorId: 38, value: [`${this.zone.id}`] }
        let routeDetails = {
          work_orders: {
            path: '/app/wo/orders/open',
            module: 'workorder',
            viewname: 'open',
          },
          fire_alarms: {
            path: '/app/fa/faults/active',
            module: 'firealarm',
            viewname: 'active',
          },
          assets: {
            path: '/app/at/assets/all',
            module: 'asset',
            viewname: 'all',
          },
        }

        if (['work_orders', 'fire_alarms'].includes(param)) {
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
        } else if (param === 'energy') {
          this.$router.push({ path: '/app/em/dashboard/default' })
        }
      } else {
        return null
      }
    },
    fontsize(value) {
      if (!value) {
        return 'f30'
      }
      value = value + ''
      if (value.length < 3) {
        return 'f30'
      } else if (value.length < 6) {
        return 'f18'
      } else if (value.length < 8) {
        return 'f13'
      } else if (value.length < 10) {
        return 'f12'
      } else if (value.length < 10) {
        return 'f11'
      } else {
        return 'f10'
      }
    },
    colorpicker(color) {
      if (color === 'Work Orders') {
        return 'workordercolor'
      } else if (color === 'Alarms') {
        return 'alarmcolor'
      } else if (color === 'Assets') {
        return 'assetscolor'
      } else if (color === 'ENERGY CONSUMED') {
        return 'energycolor'
      } else {
        return 'assetscolor'
      }
    },
    bgcolorpicker(color) {
      if (color === 'Work Orders') {
        return 'workordercolor-o'
      } else if (color === 'Alarms') {
        return 'alarmcolor-o'
      } else if (color === 'Assets') {
        return 'assetscolor-o'
      } else if (color === 'ENERGY CONSUMED') {
        return 'energycolor-o'
      } else {
        return 'assetscolor-o'
      }
    },
    loadReportCards() {
      let self = this
      self.reportsLoading = true
      let url = '/campus/reportcards?campusId=' + this.zoneId
      self.$http.get(url).then(function(response) {
        self.mainMeter = response.data.mainEnergyMeter
        self.reports = response.data.reports
        self.reportcards = response.data.reportcards
        let data = response.data.reportcards.find(
          rt => rt.label === 'ENERGY CONSUMED'
        )
        if (data) {
          self.getEnergyData(data, self.reportcards)
        }
        self.reportsLoading = false
      })
    },
    getEnergyData(data) {
      let params = {
        staticKey: 'energyDataCard',
        paramsJson: {
          dateOperator: 28,
          baseSpaceId: parseInt(this.zoneId),
          dateValue: null,
          moduleName: 'energyData',
          fieldName: 'totalEnergyConsumptionDelta',
        },
      }
      let self = this
      let index = self.reportcards.indexOf(data)
      self.$http.post('dashboard/getCardData', params).then(function(response) {
        if (response.data.cardResult) {
          data.data = response.data.cardResult.result
            ? response.data.cardResult.result
            : 0
          if (
            response.data.cardResult.unit &&
            response.data.cardResult.unit.symbol
          ) {
            if (data.data > 999) {
              data.data = data.data / 1000
              data.unit = ' MWh'
            } else {
              data.unit = ' KWh'
            }
            if (data.data > 1) {
              data.data = parseInt(data.data)
            }
            self.reportcards.splice(index, 1, data)
          }
        }
      })
    },
    loadChildren() {
      let self = this
      self.childrenLoading = true
      let url = '/zone/children?zoneId=' + this.zoneId
      self.$http.get(url).then(function(response) {
        self.childrenLoading = false
        self.children = response.data.children ? response.data.children : []
        self.loadReportCards()
      })
    },
  },
}
</script>
<style>
.site-cards .col-3:first-child {
  padding-left: 0px;
}
.site-cards .col-3 {
  padding: 10px;
}
.site-cards .col-3:last-child {
  padding-right: 0px;
}
</style>
