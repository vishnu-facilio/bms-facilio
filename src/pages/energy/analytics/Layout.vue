<template>
  <div class="analystics-layout row height100">
    <div class="col-12">
      <div class="height100 layoutWhite ensidebar">
        <div class="analytics-header-title">Analytics</div>
        <div class="scrollable" style="padding-bottom: 100px">
          <div
            v-for="(category, cidx) in subheaderMenu"
            class="analytics-sidebar-category"
            :key="cidx"
          >
            <div style="padding-left: 20px;">
              <img
                v-if="category.icon === 'energy'"
                style="width: 18px; float: left; margin-right: 8px;"
                src="~assets/product-icons/light-bolt-black.svg"
              />
              <img
                v-else-if="category.icon === 'reading'"
                style="width: 18px; float: left; margin-right: 8px;"
                src="~assets/product-icons/speedometer.svg"
              />
              <span>
                {{ category.label }}
              </span>
            </div>
            <div class="analytics-sidebar-menulist">
              <router-link
                v-for="(menu, index) in category.menu"
                :to="menu.path"
                :title="menu.label"
                tag="div"
                class="analytics-sidebar-menu"
                :key="index"
              >
                {{ menu.label }}
              </router-link>
            </div>
          </div>
          <derivation
            v-if="this.$route.path === '/app/em/oldanalytics/reading'"
            :type="5"
            :isOld="true"
          ></derivation>
        </div>
      </div>
      <router-view class="analytic-summary"></router-view>
    </div>
  </div>
</template>
<script>
import Derivation from './Derivation'

export default {
  title() {
    return 'Analytics'
  },
  data() {
    return {
      subheaderMenu: [
        {
          label: 'Energy',
          icon: 'energy',
          menu: [
            {
              label: 'Consumption Analysis',
              path: { path: '/app/em/oldanalytics/consumption' },
            },
            {
              label: 'Load Analysis',
              path: { path: '/app/em/oldanalytics/load' },
            },
            {
              label: 'Heatmap Analysis',
              path: { path: '/app/em/oldanalytics/heatmap' },
            },
            {
              label: 'Peak Energy Analysis',
              path: { path: '/app/em/oldanalytics/peakenergy' },
            },
            {
              label: 'EnPI Analysis',
              path: { path: '/app/em/oldanalytics/enpi' },
            },
            {
              label: 'Regression Analysis',
              path: { path: '/app/em/oldanalytics/regression' },
            },
          ],
        },
        {
          label: 'Reading',
          icon: 'reading',
          menu: [
            {
              label: 'Reading Analysis',
              path: { path: '/app/em/oldanalytics/reading' },
            },
          ],
        },
      ],
    }
  },
  components: {
    Derivation,
  },
  mounted() {
    this.init()
  },
  watch: {
    $route: function(from, to) {
      this.init()
    },
  },
  methods: {
    init() {
      if (
        this.$route.path === '/app/em/oldanalytics' ||
        this.$route.path === '/app/em/oldanalytics/'
      ) {
        this.$router.replace({ path: '/app/em/oldanalytics/consumption' })
      }
    },
  },
}
</script>
<style>
.an-sidebar {
  padding: 20px;
  cursor: pointer;
}
.an-sidebar.active {
  background: #f3f4f7;
}
.ensidebar {
  height: 100vh;
  background: white;
  width: 270px;
  position: absolute;
}
.analytic-summary {
  height: 100%;
  background: white;
  position: relative;
  margin-left: 270px;
  border-left: 1px solid #e8e8e8;
}
.analytics-header-title {
  font-weight: 500;
  padding: 20px;
  border-bottom: solid 1px #e0e0e0;
  text-transform: uppercase;
  letter-spacing: 0.9px;
  font-size: 13px;
  color: #000000;
}
.analytics-sidebar-category {
  padding-top: 20px;
  padding-bottom: 10px;
}
.analytics-sidebar-category span {
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.6px;
  text-transform: uppercase;
  color: #000000;
}
.analytics-sidebar-menulist {
  padding-top: 10px;
}
.analytics-sidebar-menu {
  padding: 12px 45px;
  letter-spacing: 0.4px;
  cursor: pointer;
}
.analytics-sidebar-menu:hover,
.analytics-sidebar-menu.active {
  background-color: #f0f7f8;
}
@media print {
  .fc-layout-aside,
  .layout-header,
  .layoutWhite,
  .analytics-page-header,
  .filter-field .button-row,
  .chart-icon {
    display: none;
  }
  .height100,
  .layout-page-container {
    padding: 0px !important;
  }
  .analystics-layout .fchart-section {
    padding: 0px !important;
    page-break-after: auto;
  }
  .analytic-summary {
    border: none !important;
    margin: 0px !important;
  }
  .layout-page,
  .analytic-summary,
  .height100,
  .analystics-layout,
  .layoutWhite,
  .scrollable {
    height: auto !important;
  }
  table {
    page-break-after: auto;
  }
  tr {
    page-break-inside: avoid;
    page-break-after: auto;
  }
  td {
    page-break-inside: avoid;
    page-break-after: auto;
  }
  thead {
    display: table-header-group;
  }
  tfoot {
    display: table-footer-group;
  }
  body {
    overflow: scroll;
  }
  .row,
  .filter-field .pdfDateView,
  .filter-field .Reading-Analysis {
    display: block;
  }
  .scrollable {
    overflow: visible;
  }
  .scrollable {
    padding-top: 0px !important;
  }
}
</style>
