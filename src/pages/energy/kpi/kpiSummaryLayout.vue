<template>
  <div class="height100 d-flex flex-direction-row">
    <div class="sidebar">
      <div class="height100vh full-layout-white fc-border-left fc-border-right">
        <div class="row p15 fc-border-bottom pointer">
          <div class="col-1 text-left">
            <i
              class="el-icon-back fw6"
              @click="goBack"
              style="vertical-align: sub;"
            ></i>
          </div>
          <div class="mT4 mL2">
            {{ $t('common._common.reading_kpi') }}
          </div>
        </div>

        <div class="row sp-navbar2">
          <ul class="sp-ul">
            <v-infinite-scroll
              :loading="loading"
              @bottom="loadData()"
              :offset="20"
              style="height: 100vh; padding-bottom: 100px;overflow-y: scroll;"
            >
              <div
                class="menu-item space-secondary-color sp-li ellipsis f12 pointer asset-item p20"
                v-for="kpi in KPIlist"
                :key="kpi.id"
                :class="{ active: String(id) === String(kpi.id) }"
                @click="changeKpi(kpi.id)"
              >
                <span
                  class="label"
                  :title="kpi.name"
                  v-tippy="{
                    placement: 'top',
                    animation: 'shift-away',
                    arrow: true,
                  }"
                  >{{ kpi.name }}</span
                >
              </div>
            </v-infinite-scroll>
          </ul>
          <ul v-if="loading" class="sp-ul">
            <spinner :show="loading" size="80"></spinner>
          </ul>
        </div>
      </div>
    </div>
    <div class="container">
      <Summary
        :key="id"
        :viewname="viewname"
        :id="id"
        :refresh="loadData"
      ></Summary>
    </div>
  </div>
</template>

<script>
import Summary from './kpiSummary'
import VInfiniteScroll from 'v-infinite-scroll'
import { isEmpty } from '@facilio/utils/validation'
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'

export default {
  components: {
    Summary,
    VInfiniteScroll,
  },
  props: ['viewname', 'id'],
  mounted() {
    this.loadData()
  },
  data() {
    return {
      moduleName: 'kpi',
      toggle: false,
      loading: false,
      KPIlist: [],
      page: 1,
      perPage: 20,
    }
  },
  methods: {
    loadModuleMeta(moduleName) {
      return this.$store.dispatch('view/loadModuleMeta', moduleName)
    },

    loadViews() {
      let { moduleName } = this
      return this.$store.dispatch('view/loadGroupViews', { moduleName })
    },

    goBack() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.READING_KPI_TEMPLATE) || {}
        this.$router.push({ name, query: this.$route.query })
      } else {
        this.$router.push({
          path: '/app/em/kpi/reading/templates',
          query: this.$route.query,
        })
      }
    },

    loadData() {
      this.loading = true

      this.loadKPI()
        .then(() => {
          this.loading = false
        })
        .catch(() => (this.loading = false))
    },

    changeKpi(id) {
      if (!id) return
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.READING_KPI_SUMMARY) || {}
        this.$router.push({
          name,
          params: {
            id,
          },
          query: this.$route.query,
        })
      } else {
        this.$router.push({
          path: `/app/em/kpi/reading/templates/${id}/overview`,
          query: this.$route.query,
        })
      }
    },
    loadKPI() {
      let url = '/v2/kpi/list?type=1'

      if (!isEmpty(this.page)) {
        url += `&page=${this.page}&perPage=${this.perPage}`
      }

      this.$set(this, 'page', this.page + 1)

      return this.$http
        .get(url)
        .then(response => {
          this.loading = false

          if (response.data.responseCode === 0) {
            let { formulaList = [] } = response.data.result || {}

            this.KPIlist = [...this.KPIlist, ...formulaList]
          }
        })
        .catch(error => {
          throw error
        })
    },
  },
}
</script>
<style scoped>
.sidebar {
  flex: 0 0 300px;
  max-width: 300px;
}
.container {
  flex-grow: 1;
}

/* Common */
.sp-navbar2 {
  height: 100%;
  padding: 0 0 100px !important;
  overflow-x: hidden;
  overflow-y: scroll !important;
}
.sp-navbar2 {
  padding: 0 15px;
}
.sp-navbar2 ul li {
  list-style: none;
  padding: 0px;
}
.sp-navbar2 ul li .menu-item,
.sp-navbar2 ul li .node-label {
  display: flex;
  align-items: center;
  padding: 10px;
  cursor: pointer;
}
.sp-navbar2 ul li .menu-item .label {
  margin-left: 8px;
}
.sp-navbar2 ul li.active .menu-item {
  background: #eef5f7;
}
.sp-navbar2 .sp-ul li:hover .menu-item,
.sp-navbar2 .sp-ul li.active .menu-item .asset-item,
.menu-item:hover,
.asset-item.active {
  background: #eef5f7;
  cursor: pointer;
}
.sp-ul {
  width: 100%;
  padding: 0px;
}
.menu-item {
  border-bottom: 1px solid #f4f6f8;
}
.menu-item .label {
  font-size: 14px;
  color: #324056;
  letter-spacing: 0.5px;
  font-weight: 400;
}
</style>
