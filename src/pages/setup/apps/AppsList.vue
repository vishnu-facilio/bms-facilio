<template>
  <div class="apps-list">
    <div class="header-container">
      <div>
        <div class="header-title">Apps</div>
        <div class="header-content">List of all featured apps</div>
      </div>
      <button @click="editApp()" class="pL30 pR30 setup-el-btn">Add App</button>
    </div>

    <div class="app-cards-section">
      <spinner v-if="loadingApps" :show="loadingApps" size="80"></spinner>

      <div v-else-if="$validation.isEmpty(availableApps)" class="empty-state">
        <inline-svg
          src="product-icons/app"
          iconClass="icon icon-xxlg"
        ></inline-svg>
        <div class="empty-state-text">No apps available.</div>
      </div>

      <div
        v-else
        v-for="app in availableApps"
        :key="app.id"
        class="app-card"
        @click="openAppDetails(app.id)"
      >
        <div>
          <img
            v-if="app.logoUrl"
            class="app-icon"
            :src="$prependBaseUrl(app.logoUrl)"
            :style="{ backgroundColor: '#a3cc85' }"
          />

          <inline-svg
            v-else
            src="product-icons/app-white"
            class="app-icon"
            iconClass="icon icon-xxllg p5"
            :style="{ backgroundColor: '#a3cc85' }"
          />
        </div>

        <div class="app-content flex-align-start">
          <div class="app-name">{{ app.name }}</div>
          <div class="app-link-name">
            <div class="fc-app-link-color f14">{{ appUrl(app) }}</div>
          </div>
          <div class="app-description">{{ app.description }}</div>
        </div>
      </div>
    </div>

    <NewApp
      v-if="showAddApp"
      :selectedApp="selectedApp"
      @onSave="loadAvailableApps"
      @onClose="showAddApp = false"
    ></NewApp>
  </div>
</template>
<script>
import { loadApps } from 'util/appUtil'
import NewApp from './NewApp'

export default {
  components: { NewApp },

  data() {
    return {
      availableApps: [],
      loadingApps: false,
      selectedApp: null,
      showAddApp: false,
    }
  },

  created() {
    this.loadAvailableApps()
  },

  methods: {
    async loadAvailableApps() {
      this.loadingApps = true

      let { error, data } = await loadApps()
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let {
          appCategory: { FEATURE_GROUPING },
        } = this.$constants

        this.availableApps = data.filter(
          app =>
            app.linkName !== 'newapp' && app.appCategory === FEATURE_GROUPING
        )
      }

      this.loadingApps = false
    },
    appUrl(app) {
      let { appDomain, linkName } = app
      let { domain } = appDomain || {}
      return `https://${domain || 'domain'}/${linkName}`
    },
    editApp(app = null) {
      this.selectedApp = app
      this.showAddApp = true
    },

    openAppDetails(id) {
      this.$router.push({ name: 'app-details', params: { appId: id } })
    },
  },
}
</script>
<style lang="scss">
.apps-list {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;

  .header-container {
    display: flex;
    justify-content: space-between;
    padding: 20px;
    border-bottom: 1px solid #ecf2f7;
    background: #fff;

    .header-title {
      font-size: 18px;
      letter-spacing: 0.5px;
      color: #324056;
      padding-bottom: 5px;
    }
    .header-content {
      font-size: 13px;
      letter-spacing: 0.5px;
      color: #808080;
    }
  }
  .app-cards-section {
    height: 100%;
    display: flex;
    flex-wrap: wrap;
    flex-basis: 0;
    margin: 10px;
    margin-left: 20px;
  }
  .empty-state {
    display: flex;
    margin: auto;
    height: 100%;
    align-items: center;
    font-size: 14px;
    letter-spacing: 0.5px;
    text-align: center;
    color: #324056;
    flex-direction: column;
    justify-content: center;

    .empty-state-text {
      font-size: 14px;
      letter-spacing: 0.5px;
      text-align: center;
      color: #324056;
      margin-top: 5px;
    }
  }
  .app-card {
    margin: 10px;
    padding: 20px;
    border-radius: 5px;
    box-shadow: 0 4px 8px 2px rgb(0 0 0 / 5%);
    background-color: #fff;
    align-items: center;
    cursor: pointer;
    max-width: 328px;
    flex: 3 0 30%;

    &:hover {
      box-shadow: 0px 4px 8px 5px rgb(0 0 0 / 8%);
    }

    .app-content {
      display: flex;
      align-items: center;
      flex-direction: column;

      .app-name {
        font-size: 18px;
        font-weight: 500;
        font-style: normal;
        font-stretch: normal;
        line-height: normal;
        letter-spacing: 0.4px;
        color: #324056;
        text-transform: capitalize;
        margin-top: 10px;
      }
      .app-description {
        -webkit-line-clamp: 2;
        overflow: hidden;
        -webkit-box-orient: vertical;
        display: -webkit-box;
        word-break: break-word;
        line-height: 18px;
        margin-top: 5px;
        font-size: 13px;
        letter-spacing: 0.5px;
        color: #324056;
      }
      .app-link-name {
        display: flex;
        font-weight: 400;
        letter-spacing: 0.5px;
        color: #324056;
        font-size: 13px;
        svg {
          width: 12px;
          height: 20px;
          g {
            fill: #808080;
          }
        }
      }
      .app-link {
        color: #808080;
        padding-left: 5px;
        line-height: 20px;
        overflow: hidden;
        -webkit-box-orient: vertical;
        display: -webkit-box;
        word-break: break-word;
        -webkit-line-clamp: 1;
      }
    }
    .app-icon svg {
      fill: #fff;
    }
    img.app-icon {
      width: 58px;
    }
    .app-icon {
      border: none;
      border-radius: 5px;
      margin-right: 15px;
      svg {
        fill: #fff;
      }
    }
    .delete-app {
      cursor: pointer;
      color: #de7272;
      font-size: 16px;
    }
  }
}
</style>
