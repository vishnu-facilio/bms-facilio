<template>
  <div>
    <!-- <el-tab-pane label="HISTORY" name="fourth"> -->
    <div class="pm-summary-inner-container mT20">
      <div
        v-if="
          (pm.preventivemaintenance.pmCreationType === 1 &&
            resourceList.length > 0) ||
            (pm.preventivemaintenance.pmCreationType === 3 &&
              resourceList.length > 0)
        "
        class="white-bg-block width100"
      >
        <div class="flex-middle width100 justify-content-space p30 pB0">
          <div
            v-if="
              pm.preventivemaintenance.pmCreationType === 1 &&
                resourceList.length > 0
            "
          >
            <div class="fc-black-com f14 bold">
              {{ pm.workorder.resource.name }}
            </div>
          </div>
          <el-dropdown
            class="fc-dropdown-menu-border"
            v-bind:class="{ disable: resources.length == 0 }"
            trigger="click"
          >
            <el-button type="primary" class="fc-btn-group-white">
              <div
                v-if="
                  pm.preventivemaintenance.pmCreationType === 3 &&
                    pm.preventivemaintenance.assignmentType === 4 &&
                    pm.preventivemaintenance.assetCategoryId > -1
                "
              >
                {{
                  resourceName !== null
                    ? resourceName
                    : 'All ' +
                      `${
                        assetcategory.find(
                          i =>
                            i.id ===
                            Number(pm.preventivemaintenance.assetCategoryId)
                        ).name
                      }`
                }}<i class="el-icon-arrow-down"></i>
              </div>
              <div
                v-else-if="
                  pm.preventivemaintenance.pmCreationType === 3 &&
                    pm.preventivemaintenance.assignmentType === 3 &&
                    pm.preventivemaintenance.spaceCategoryId > -1
                "
              >
                {{
                  resourceName !== null
                    ? resourceName
                    : 'All ' +
                      `${
                        spacecategory[pm.preventivemaintenance.spaceCategoryId]
                      }`
                }}<i class="el-icon-arrow-down"></i>
              </div>
              <div
                v-else-if="
                  pm.preventivemaintenance.pmCreationType === 3 &&
                    pm.preventivemaintenance.assignmentType === 1
                "
              >
                {{ resourceName !== null ? resourceName : 'All ' + 'FLOORS'
                }}<i class="el-icon-arrow-down"></i>
              </div>
              <div v-else>
                {{ resourceName !== null ? resourceName : 'All ' + resourceType
                }}<i class="el-icon-arrow-down"></i>
              </div>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>
                <div
                  v-if="
                    pm.preventivemaintenance.pmCreationType === 3 &&
                      pm.preventivemaintenance.assignmentType === 4 &&
                      pm.preventivemaintenance.assetCategoryId > -1
                  "
                  @click="getResourceBasedWo(null)"
                  class="pointer"
                >
                  All
                  {{
                    `${
                      assetcategory.find(
                        i =>
                          i.id ===
                          Number(pm.preventivemaintenance.assetCategoryId)
                      ).name
                    }`
                  }}
                </div>
                <div
                  v-else-if="
                    pm.preventivemaintenance.pmCreationType === 3 &&
                      pm.preventivemaintenance.assignmentType === 3 &&
                      pm.preventivemaintenance.spaceCategoryId > -1
                  "
                  @click="getResourceBasedWo(null)"
                  class="pointer"
                >
                  All
                  {{
                    `${spacecategory[pm.preventivemaintenance.spaceCategoryId]}`
                  }}
                </div>
                <div
                  v-else-if="
                    pm.preventivemaintenance.pmCreationType === 3 &&
                      pm.preventivemaintenance.assignmentType === 1
                  "
                  @click="getResourceBasedWo(null)"
                  class="pointer"
                >
                  All FLOORS
                </div>
                <div v-else @click="getResourceBasedWo(null)" class="pointer">
                  All {{ resourceType }}
                </div>
              </el-dropdown-item>
              <el-dropdown-item
                :command="resource"
                v-for="(resource, index) in resources"
                :key="index"
              >
                <div @click="getResourceBasedWo(resource)">
                  {{ resource.name }}
                </div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <div class="" v-if="workordersList.length">
            <div class="flex-middle">
              <div>
                <span v-if="from !== to" class="fc-black-small-txt-12"
                  >{{ from }} -
                </span>
                <span class="fc-black-small-txt-12">{{ to }}</span>
                <template>
                  of {{ workordersList.length }}
                  <span
                    class="el-icon-arrow-left fc-black-small-txt-12 fw-bold f16 pointer mL10 mR5"
                    @click="from > 1 ? prev() : null"
                    v-bind:class="{ disable: from <= 1 }"
                  ></span>
                  <span
                    class="el-icon-arrow-right fc-black-small-txt-12 f16 pointer mR10 fw-bold"
                    @click="hasMorePage ? next() : null"
                    v-bind:class="{
                      disable: to === workordersList.length,
                    }"
                  ></span>
                </template>
              </div>
            </div>
          </div>
        </div>
        <div
          class="fc-pm-history-table-con fc-list-view pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-td-pm fc-table-viewchooser pB100 mT20 clearboth"
        >
          <el-table
            :data="resourceList"
            :index="indexMethod"
            style="width: 100%"
            height="auto"
            :fit="true"
            class="fc-pm-history-table"
          >
            <template slot="empty">
              <img src="~statics/noData-light.png" width="100" height="100" />
              <div class="mT10 label-txt-black f14">No History available</div>
            </template>

            <el-table-column label="Created time" width="180">
              <template v-slot="resourceList">
                <div
                  v-if="resourceList.row && resourceList.row.createdTime !== -1"
                >
                  {{ resourceList.row.createdTime | formatDate() }}
                </div>
                <div v-else>---</div>
              </template>
            </el-table-column>

            <el-table-column
              v-if="resourceName === null && resources.length"
              :label="resourceType"
              width="180"
            >
              <template v-slot="resourceList">
                <div>{{ resourceMap[resourceList.row.resource.id] }}</div>
              </template>
            </el-table-column>

            <el-table-column label="Workorder" width="180">
              <template v-slot="resourceList">
                <div @click="redirectToWo(resourceList.row.id)" class="fc-id">
                  {{ '#' + resourceList.row.serialNumber }}
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Assigned to" width="180">
              <template v-slot="resourceList">
                <div
                  v-if="
                    resourceList &&
                      resourceList.row &&
                      resourceList.row.assignedTo &&
                      resourceList.row.assignedTo.id
                  "
                >
                  {{ getUser(resourceList.row.assignedTo.id).name }}
                </div>
                <div v-else>---</div>
              </template>
            </el-table-column>

            <el-table-column label="Resolved" width="180">
              <template v-slot="resourceList">
                <div
                  v-if="
                    resourceList.row && resourceList.row.actualWorkEnd !== -1
                  "
                >
                  {{ resourceList.row.actualWorkEnd | formatDate() }}
                </div>
                <div v-else>---</div>
              </template>
            </el-table-column>
            <el-table-column label="Overdue Status" width="180">
              <template v-slot="resourceList">
                <div
                  v-bind:class="[
                    {
                      'work-progress-txt':
                        resourceList.row.actualWorkStart !== -1 &&
                        resourceList.row.actualWorkEnd === -1,
                    },
                    {
                      'wo-error-status':
                        resourceList.row.dueDate !== -1 &&
                        resourceList.row.actualWorkEnd !== -1,
                    },
                  ]"
                >
                  {{ toCalculateDue(resourceList.row) }}
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div
          v-if="
            !resourceList.length > 0 &&
              this.pm.preventivemaintenance &&
              this.workordersList.length > 0
          "
        >
          <template slot="empty">
            <img src="~statics/noData-light.png" width="100" height="100" />
            <div class="mT10 label-txt-black f14">
              No Workorders available For This Resources
            </div>
          </template>
        </div>
      </div>
      <div v-else>
        <div
          class="flex-middle width100 height80vh justify-center white-bg-block flex-direction-column"
        >
          <inline-svg
            src="svgs/emptystate/history"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="nowo-label">
            No History Available
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import InlineSvg from '@/InlineSvg'
import { mapState, mapGetters } from 'vuex'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  components: {
    InlineSvg,
  },
  props: ['pm', 'resources', 'resourceType', 'workorder', 'pmWorkorders'],
  data() {
    return {
      value: '',
      resourceList: [],
      resourceName: null,
      resourceMap: {},
      perPage: 30,
      page: 1,
      from: 0,
      to: 0,
      hasMorePage: false,
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
      assetcategory: state => state.assetCategory,
    }),

    ...mapGetters([
      'getSpaceCategoryPickList',
      'getBuildingsPickList',
      'getUser',
    ]),

    spacecategory() {
      return this.getSpaceCategoryPickList()
    },

    buildingList() {
      return this.getBuildingsPickList()
    },
    workordersList: {
      get() {
        return this.pmWorkorders || []
      },
      set(value) {
        this.$emit('update:pmWorkorders', value)
      },
    },
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSpaceCategory')
    this.$store.dispatch('loadBuildings')
  },
  watch: {
    page() {
      this.from = (this.page - 1) * this.perPage + 1
      this.to = this.from + this.perPage - 1
      if (this.to >= this.workordersList.length) {
        this.to = this.workordersList.length
        this.hasMorePage = true
      }
      let a = []
      for (let i = 0; i < this.workordersList.length; i++) {
        if (i >= this.from - 1 && i < this.to) {
          console.log('*****', i)
          a.push(this.workordersList[i])
        }
      }
      if (a.length) {
        this.resourceList = a
      }
    },
    workordersList: {
      handler: function(newVal, oldVal) {
        if (this.workordersList.length) {
          this.from = 1
          if (this.perPage >= this.workordersList.length) {
            this.to = this.workordersList.length
            this.hasMorePage = false
          } else {
            this.to = this.perPage
            this.hasMorePage = true
          }
        }
      },
      // deep: true,
      immediate: true,
    },
  },
  mounted() {
    this.initData()
  },
  // computed: {
  //   resourceList () {
  //   return this.workordersList
  // }
  // },
  methods: {
    prev() {
      this.page = this.page - 1
    },
    next() {
      this.page = this.page + 1
    },

    initData() {
      if (this.resourceName === null) {
        this.resourceList = this.workordersList
      }
      if (this.resources && this.resources.length) {
        this.resources.forEach(d => {
          this.resourceMap[d.id] = d.name
        })
      }
      let a = []
      if (this.workordersList.length > this.perPage) {
        for (let i = 0; i < this.workordersList.length; i++) {
          if (this.perPage > i) {
            a.push(this.workordersList[i])
          }
        }
        if (a.length) {
          this.resourceList = a
        }
      }
    },
    indexMethod(index) {
      return index * 2
    },
    getResourceBasedWo(resource) {
      if (resource && resource.name) {
        this.resourceName = resource.name
      } else {
        this.resourceName = null
      }

      if (resource === null) {
        this.resourceList = this.workordersList
      } else {
        console.log('resource', resource)
        // let value = []
        // resource.forEach(d => {
        // value.push(d.id)
        // })
        let filters = {
          resource: [
            {
              operatorId: 36,
              value: ['' + resource.id + ''],
            },
          ],
          pm: [
            {
              operatorId: 36,
              value: ['' + this.pm.preventivemaintenance.id + ''],
            },
          ],
        }
        let self = this
        let queryObj = {
          viewname: 'all',
          page: 1,
          filters: filters,
          orderBy: 'createdTime',
          orderType: 'desc',
          criteriaIds: this.$route.query ? this.$route.query.criteriaIds : null,
          includeParentFilter: true,
          isNew: true,
        }
        self.$store
          .dispatch('workorder/fetchWorkOrders', queryObj)
          .then(() => {
            self.resourceList = self.$store.state.workorder.workorders
          })
          .catch(() => {})
      }
    },
    redirectToWo(id) {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('workorder', pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.push({
            name,
            params: {
              viewname: 'all',
              id,
            },
          })
        }
      } else {
        this.$router.push({
          path: '/app/wo/orders/summary/' + id,
        })
      }
    },
    toCalculateDue(res) {
      let status = this.$store.getters.getTicketStatusByLabel(
        'Skipped',
        'workorder'
      )
      if (status != null && status.id == res.moduleState.id) {
        return 'Skipped'
      } else if (res.actualWorkStart !== -1 && res.actualWorkEnd === -1) {
        return 'Work in progress'
      } else if (res.dueDate !== -1 && res.actualWorkEnd !== -1) {
        if (res.actualWorkStart !== -1 && res.actualWorkEnd !== -1) {
          if (res.actualWorkEnd < res.dueDate) {
            return 'On time'
          } else if (res.actualWorkEnd > res.dueDate && res.dueDate !== -1) {
            return 'Over due'
          }
          return '---'
        }
      } else if (res.actualWorkStart !== -1 && res.actualWorkEnd !== -1) {
        return '---'
      } else if (res.actualWorkStart === -1 && res.actualWorkEnd === -1) {
        return 'Not Yet Started'
      }
    },
  },
}
</script>
<style>
.fc-pm-history-table .el-table__body,
.fc-pm-history-table .el-table__empty-block {
  width: 100% !important;
}
.fc-pm-history-table-con .el-table td {
  padding: 17px 20px;
}
.fc-pm-history-table-con .fc-pm-history-table {
  height: calc(100vh - 280px) !important;
  margin-bottom: 100px;
}
.fc-pm-history-table-con {
  padding-left: 30px;
  padding-right: 30px;
}
.fc-pm-history-table {
  border-left: 1px solid #f7f8f9;
  border-right: 1px solid #f7f8f9;
}
.fc-pm-history-table th {
  background-color: #f7fafa !important;
  font-size: 11px;
  font-weight: bold;
}
.el-icon-arrow-right.disable {
  color: #50506c;
  font-weight: bold;
  opacity: 0.4;
}
.fc-dropdown-menu-border.disable {
  visibility: hidden;
}
.el-icon-arrow-left.disable {
  color: #50506c;
  font-weight: bold;
  opacity: 0.4;
}
.work-progress-txt {
  color: #3d8bc4;
}
.overdue-overstart {
  color: #da6a1d;
}
.wo-error-status {
  color: #da6a1c;
}
</style>
