<template>
  <div class="row dashboard-filter">
    <div class="col-12">
      <div>
        <el-select
          v-model="dashboardFilter.siteId"
          filterable
          clearable
          multiple
          collapse-tags
          @change="applyFilter"
          placeholder="Site"
          class="db-filter fc-tag"
          v-tippy
        >
          <el-option
            v-for="(site, index) in sites"
            :key="index"
            :label="site.name"
            :value="site.id"
          >
          </el-option>
        </el-select>
      </div>
      <div>
        <el-select
          v-model="dashboardFilter.buildingId"
          filterable
          clearable
          multiple
          collapse-tags
          @change="applyFilter"
          placeholder="Building"
          class="db-filter fc-tag"
          v-tippy
        >
          <el-option
            v-for="(building, index) in filteredBuildings"
            :key="index"
            :label="building.name"
            :value="building.id"
          >
          </el-option>
        </el-select>
      </div>
      <div>
        <el-select
          v-model="dashboardFilter.teamId"
          v-if="!isBuildingOnlyFilter"
          filterable
          clearable
          multiple
          collapse-tags
          @change="applyFilter"
          placeholder="Team"
          class="db-filter fc-tag"
          v-tippy
        >
          <el-option
            v-for="(team, index) in $store.state.groups"
            :key="index"
            :label="team.name"
            :value="team.id"
          >
          </el-option>
        </el-select>
      </div>
      <div>
        <el-select
          v-model="dashboardFilter.techinicianId"
          v-if="!isBuildingOnlyFilter"
          filterable
          clearable
          multiple
          collapse-tags
          no-data-text="No technician available"
          no-match-text="No matching technician available"
          @change="applyFilter"
          placeholder="Technician"
          class="db-filter fc-tag"
          v-tippy
        >
          <el-option
            v-for="(techinician, index) in techinicians"
            :key="index"
            :label="techinician.name"
            :value="techinician.id"
          >
          </el-option>
        </el-select>
      </div>
      <div
        v-if="filters && filters !== null && Object.keys(filters).length !== 0"
        @click="clearFilters"
        class="clear-all-text pointer"
      >
        Clear all
      </div>
      <div class="filter-icon-up" @click="close">
        <i class="el-icon-arrow-up"></i>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  data() {
    return {
      dashboardFilter: {
        siteId: [],
        buildingId: [],
        teamId: [],
        techinicianId: [],
      },
      filterObj: {},
    }
  },
  created() {
    this.$store.dispatch('loadGroups')
    this.$store.dispatch('loadSite')
    this.$store.dispatch('loadBuildings')
  },
  mounted() {
    this.loadFilter()
  },
  watch: {
    filters: {
      handler(newData, oldData) {
        if (newData && Object.keys(newData).length === 0) {
          this.filterObj = {}
          this.dashboardFilter.siteId = []
          this.dashboardFilter.buildingId = []
          this.dashboardFilter.teamId = []
          this.dashboardFilter.techinicianId = []
        }
      },
      deep: true,
    },
  },
  computed: {
    filters() {
      if (this.$route.query.filters) {
        return JSON.parse(this.$route.query.filters)
      }
      return {}
    },
    isBuildingOnlyFilter() {
      return this.$route.path.endsWith('withbuilding')
    },
    ...mapState({
      sites: state => state.site,
      buildings: state => state.buildings,
    }),
    filteredBuildings() {
      if (!this.buildings) {
        return []
      }
      if (this.dashboardFilter.siteId.length) {
        return this.buildings.filter(
          b => this.dashboardFilter.siteId.indexOf(b.siteId) >= 0
        )
      }
      return this.buildings
    },
    techinicians() {
      if (this.dashboardFilter.teamId.length) {
        let memberList = []
        let memberMapping = {}
        for (let teamId of this.dashboardFilter.teamId) {
          let mbrs = this.$store.state.groups.find(grp => grp.id === teamId)
            .members
          for (let mbr of mbrs) {
            if (!memberMapping[mbr.id]) {
              memberList.push(mbr)
              memberMapping[mbr.id] = true
            }
          }
        }
        return memberList
      }
      return this.$account.data.users
    },
  },
  methods: {
    clearFilters() {
      this.filterObj = {}
      let url = this.$router.path
      this.$router.push({
        path: url,
        query: { showFilter: true, filter: this.filterObj },
      })
    },
    close() {
      // let url = this.$router.path
      let filter = this.$route.query.filters
      this.$router.replace({ query: { showFilter: false, filters: filter } })
      // this.$router.push({path: url, query: {showFilter: false, filter: })
    },
    loadFilter() {
      if (this.$route.query.filters) {
        let filterObj = JSON.parse(this.$route.query.filters)
        if (filterObj.resource) {
          for (let val of filterObj.resource.value) {
            let site = this.sites.find(sp => sp.id === parseInt(val))
            let building = this.buildings.find(bl => bl.id === parseInt(val))
            if (building) {
              this.dashboardFilter.siteId.push(building.siteId)
              this.dashboardFilter.buildingId.push(building.id)
            } else if (site) {
              this.dashboardFilter.siteId.push(site.id)
            }
          }
        }
        if (filterObj.assignmentGroup) {
          for (let val of filterObj.assignmentGroup.value) {
            this.dashboardFilter.teamId.push(parseInt(val))
          }
        }
        if (filterObj.assignedTo) {
          for (let val of filterObj.assignedTo.value) {
            this.dashboardFilter.techinicianId.push(parseInt(val))
          }
        }
      }
    },
    applyFilter() {
      let filterObj = {}
      if (this.dashboardFilter.buildingId.length) {
        filterObj.resource = {
          operatorId: 38,
          module: 'workorder',
          value: this.dashboardFilter.buildingId.map(String),
        }
      } else if (this.dashboardFilter.siteId.length) {
        filterObj.resource = {
          operatorId: 38,
          module: 'workorder',
          value: this.dashboardFilter.siteId.map(String),
        }
      }

      if (this.dashboardFilter.techinicianId.length) {
        filterObj.assignedTo = {
          operatorId: 36,
          module: 'workorder',
          value: this.dashboardFilter.techinicianId.map(String),
        }
      } else if (this.dashboardFilter.teamId.length) {
        filterObj.assignmentGroup = {
          operatorId: 36,
          module: 'workorder',
          value: this.dashboardFilter.teamId.map(String),
        }
      }
      this.filterObj = filterObj
      this.$router.replace({
        query: { showFilter: true, filters: JSON.stringify(filterObj) },
      })
    },
  },
}
</script>

<style>
.db-filter .el-input .el-input__inner {
  font-size: 13px;
  padding: 10px;
  height: 35px !important;
  border-radius: 3px;
  background-color: #ffffff;
  border: solid 1px #cff1f6;
}
.db-filter.el-select {
  margin-right: 10px;
  border: none;
  width: 250px;
}
.db-filter.el-select .el-tag {
  max-width: 130px;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  text-align: left;
  color: #31a4b4;
  background: #31a4b412;
}
.db-filter > .el-select__tags {
  display: inline-flex;
}
.dashboard-filter {
  background: #fff;
  box-shadow: 0 2px 4px 0 rgba(167, 167, 167, 0.2);
  padding: 20px;
  position: relative;
  margin-bottom: 15px;
}
.dashboard-filter > .col-12 {
  display: inline-flex;
}
.db-filter input.el-select__input {
  position: relative;
  cursor: pointer;
}
.db-filter .el-select .el-input .el-select__caret {
  color: #39b2c2;
  font-weight: 600;
  font-size: 16px;
}
.dashboard-filter .filter-icon-up {
  position: absolute;
  right: 20px;
  top: 30px;
  color: #39b2c2;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
}
.clear-all-text {
  font-size: 12px;
  position: absolute;
  margin-right: 40px;
  color: #39b2c2;
  right: 20px;
  top: 34px;
}
</style>
