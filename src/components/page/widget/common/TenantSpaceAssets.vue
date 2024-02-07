<template>
  <div class="space-asset-widget-container">
    <div
      v-if="canShowCreate && currentTab == 'current'"
      class="pull-right self-center table-btn"
    >
      <el-button
        @click="addSpacesDialog"
        icon="el-icon-plus"
        class="el-button f12 fc-sites-btn el-button--text sh-button sh-button-add button-add sp-sh-btn-sm"
        type="text"
        :title="'Add Spaces'"
        v-tippy
        data-size="small"
      ></el-button>
    </div>
    <el-tabs v-model="currentTab">
      <el-tab-pane
        lazy
        class="mT10"
        key="current"
        name="current"
        label="Occupying Units"
        style="margin-bottom:0"
      >
        <el-table
          :data="currentSpaces"
          :fit="true"
          height="350"
          class="fc-table-widget-scroll unit-table"
        >
          <template slot="empty">
            <img
              class="mT100"
              src="~statics/noData-light.png"
              width="100"
              height="100"
            />
            <div class="mT10 label-txt-black f14">
              {{ $t('common._common.no_units_available') }}
            </div>
          </template>

          <el-table-column label="ID" width="150">
            <template v-slot="item">
              <div class="fc-id mL30">#{{ item.row.space.id }}</div>
            </template>
          </el-table-column>

          <el-table-column label="Name" width="220">
            <template v-slot="item">
              <div class="pointer mL30" @click="redirectToOverview(item.row)">
                {{ item.row.space.name }}
              </div>
            </template>
          </el-table-column>

          <el-table-column label="TYPE" width="220">
            <template v-slot="item">
              <div class="mL30">{{ item.row.space.spaceTypeVal }}</div>
            </template>
          </el-table-column>

          <el-table-column label="AREA" width="220">
            <template v-slot="item">
              <div class="mL30">
                {{ item.row.space.area | positive }}
                {{ $t('common.header.sqft') }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop label class="visibility-visible-actions">
            <template v-slot="item">
              <div
                v-if="$hasPermission('tenantunit:UPDATE')"
                class="text-center"
              >
                <span @click="markVacant(item.row.space)">
                  <el-button class="small-border-btn visibility-hide-actions">
                    {{ 'Mark Vacant' }}
                  </el-button>
                </span>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane lazy name="old" class="mT10" key="old" label="Vacated Units">
        <el-table
          :data="oldSpaces"
          :fit="true"
          height="350"
          class="fc-table-widget-scroll unit-table"
        >
          <template slot="empty">
            <img
              class="mT100"
              src="~statics/noData-light.png"
              width="100"
              height="100"
            />
            <div class="mT10 label-txt-black f14">
              {{ $t('common._common.no_units_available') }}
            </div>
          </template>

          <el-table-column label="ID" width="150">
            <template v-slot="item">
              <div class="fc-id mL30">#{{ item.row.space.id }}</div>
            </template>
          </el-table-column>

          <el-table-column label="Name" width="220">
            <template v-slot="item">
              <div class="pointer mL30" @click="redirectToOverview(item.row)">
                {{ item.row.space.name }}
              </div>
            </template>
          </el-table-column>

          <el-table-column label="TYPE" width="220">
            <template v-slot="item">
              <div class="mL30">{{ item.row.space.spaceTypeVal }}</div>
            </template>
          </el-table-column>

          <el-table-column label="AREA">
            <template v-slot="item">
              <div class="mL30">
                {{ item.row.space.area | positive }}
                {{ $t('common.header.sqft') }}
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    <FLookupFieldWizard
      v-if="canShowWizard"
      :selectedLookupField="unitField"
      :canShowLookupWizard.sync="canShowWizard"
      :siteId="selectedSiteId"
      @setLookupFieldValue="setSelectedSpaces"
      :specialFilterValue="unitFilter"
    ></FLookupFieldWizard>
  </div>
</template>

<script>
import SpaceMixin from '@/mixins/SpaceMixin'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['details'],
  mixins: [SpaceMixin],
  components: { FLookupFieldWizard },
  data() {
    return {
      activeModuleName: 'spaces',
      currentSpaces: [],
      oldSpaces: [],
      assets: [],
      page: 1,
      perPage: 10,
      canShowWizard: false,
      staticWidgetHeight: null,
      canShowLookupWizard: false,
      selectedSiteId: null,
      unitField: {
        displayName: this.$t('common.header.tenantUnit'),
        name: 'tenantunit',
        lookupModule: {
          displayName: this.$t('common.header.tenantUnit'),
          name: 'tenantunit',
        },
        multiple: true,
        selectedItems: [],
      },
      unitFilter: {
        isOccupied: { operator: 'is', value: ['false'] },
        decommission: {
          operator: 'is',
          value: ['false'],
        },
      },
      tabs: ['Current Units', 'Old Units'],
      currentTab: 'current',
    }
  },
  async created() {
    this.initial()
  },
  computed: {
    canShowCreate() {
      return (
        this.activeModuleName === 'spaces' &&
        this.$hasPermission('tenant:UPDATE')
      )
    },
    moduleName() {
      return 'tenant'
    },
  },
  methods: {
    initial() {
      let { details } = this
      if (details.siteId > 0) {
        this.selectedSiteId = details.siteId
      }
      let { tenantSpaces } = details
      this.spaces = tenantSpaces || []
      this.setTenantSpacesForTypes(tenantSpaces)
    },
    setTenantSpacesForTypes(tenantSpaces) {
      this.currentSpaces = []
      this.oldSpaces = []
      if (tenantSpaces) {
        tenantSpaces.forEach(tenantSpace => {
          if (this.checkCurrentUnit(tenantSpace)) {
            this.currentSpaces.push(tenantSpace)
          } else {
            this.oldSpaces.push(tenantSpace)
          }
        })
      }
    },
    async loadTenant() {
      let { details } = this
      let { tenant, error } = await API.fetchRecord(
        this.moduleName,
        {
          id: details.id,
        },
        { force: true }
      )
      if (error) {
        this.$message.error(error.message)
      } else {
        this.setTenantSpacesForTypes(tenant.tenantSpaces)
      }
    },
    markVacant(space) {
      this.$dialog
        .confirm({
          title: 'Confirmation',
          message: 'Are you sure you want to mark the unit vacant?',
          rbDanger: true,
          rbLabel: 'Mark Vacant',
        })
        .then(value => {
          if (value) {
            let params = {
              data: {},
              id: space.id,
              moduleName: 'tenantunit',
            }
            API.post('v3/modules/data/patch?markVacant=true', params)
              .then(async ({ error }) => {
                if (error) {
                  this.$message.error(error.message)
                } else {
                  this.unitField.selectedItems = []
                  this.$message.success('Tenant unit is marked as vacant')
                  this.loadTenant()
                }
              })
              .finally(() => (this.saving = false))
          }
        })
    },
    addSpacesDialog() {
      this.canShowWizard = true
    },
    setSelectedSpaces(units) {
      let { details, moduleName } = this
      this.saving = true
      let data = {
        spacesUpdate: true,
        id: details.id,
        spaces: units.field.selectedItems.map(unit => ({ id: unit.value })),
      }
      let params = {
        data,
        id: details.id,
        moduleName,
      }
      API.post('v3/modules/data/patch?spacesUpdate=true', params)
        .then(async ({ error }) => {
          if (error) {
            this.$message.error(error.message)
          } else {
            this.unitField.selectedItems = []
            this.$message.success('Unit associated successfully!')
            this.loadTenant()
          }
        })
        .finally(() => (this.saving = false))
    },

    redirectToOverview(field) {
      let { space } = field || {}
      let { id: spaceId } = space || {}
      let { activeModuleName } = this
      let route
      let params = { id: spaceId, viewname: 'all' }

      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(activeModuleName, pageTypes.OVERVIEW) || {}

        if (name) {
          route = this.$router.resolve({
            name,
            params,
          }).href
        }
      } else {
        let summaryRouteHash = {
          spaces: {
            name: 'tenantUnitSummary',
            params,
          },
        }
        route = this.$router.resolve(summaryRouteHash[activeModuleName]).href
      }
      route && window.open(route, '_blank')
    },
    checkCurrentUnit(space) {
      if (space && space.currentlyOccupied) {
        return true
      }
      return false
    },
  },
}
</script>
<style lang="scss" scoped>
.space-asset-widget-container {
  .table-btn {
    z-index: 1;
    right: 10px;
    top: 10px;
  }
  .el-table th > .cell {
    padding-left: 0px;
  }
  .widget-card {
    .el-tabs__nav-wrap {
      padding-left: 20px;
    }
  }
}
.tenant-unit-widget-header {
  height: 40px;
  padding-left: 20px;
  padding-top: 20px;
  color: #385571;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 1px;
}
</style>
