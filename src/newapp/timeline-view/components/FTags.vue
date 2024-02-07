<template>
  <div v-if="!$validation.isEmpty(tagsArr)" class="scheduler-tags-container">
    <div class="scheduler-tags-layout">
      <el-tag
        v-for="(tag, index) in tagsArr"
        :key="index"
        class="scheduler-tag"
      >
        <div class="pR3">{{ tag.fieldDisplayName }}</div>
        <div class="pR3">{{ tag.operatorDisplayName }}</div>
        <div class="pR5 truncate-text">{{ tag.valueStr }}</div>
        <InlineSvg
          v-if="!hideClose"
          class="d-flex"
          src="svgs/close"
          iconClass="icon icon-xxxs self-center mR5 cursor-pointer scheduler-close-icon"
          @click.native="clearFilter(tag)"
        ></InlineSvg>
      </el-tag>
    </div>
    <div v-if="!hideActionBtn" class="scheduler-tags-action">
      <div class="scheduler-tags-clear" @click="resetFilters">
        {{ $t('filters.tags.clear_all') }}
      </div>
      <div v-if="canSaveView" class="scheduler-tags-save">
        <span v-if="isSystemView" @click="savingView('new')">
          {{ $t('filters.tags.save_view') }}
        </span>
        <el-dropdown v-else @command="savingView">
          <span class="el-dropdown-link">
            {{ $t('filters.tags.save_view_as') }}
            <i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item class="text-capitalize" command="new">
              {{ $t('filters.tags.save_as_new') }}
            </el-dropdown-item>
            <el-dropdown-item class="text-capitalize" command="edit">
              {{ $t('filters.tags.save_to_existing') }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>
<script>
import FTags from 'src/newapp/components/search/FTags.vue'
import {
  findRouteForTab,
  getApp,
  pageTypes,
  isWebTabsEnabled,
} from '@facilio/router'

export default {
  extends: FTags,
  created() {
    this.init()
  },
  methods: {
    init() {
      this.loadOperators().then(() => {
        let { appliedFilters } = this
        let operatorsList = this.getOperatorsList()
        this.$set(this, 'operatorsList', operatorsList)
        this.constructTags(appliedFilters)
      })
    },
    savingView(command) {
      if (command === 'edit') {
        this.openConfirmDialog()
      } else if (command === 'new') {
        this.redirectToViewCreation()
      }
    },
    redirectToViewCreation() {
      let { moduleName, $route, currentViewDetail } = this
      let viewname = this.$getProperty(currentViewDetail, 'name', null)
      let { query } = $route
      let { search } = query || {}
      let appId = (getApp() || {}).id

      if (isWebTabsEnabled()) {
        let { name: routeName } = findRouteForTab(pageTypes.TIMELINE_CREATE, {
          moduleName,
        })

        if (routeName) {
          this.$router.push({
            name: routeName,
            query: { appId, search, saveAsNew: true, viewname },
          })
        }
      } else {
        this.$router.push({
          name: 'timeline-view-create',
          query: { appId, search, saveAsNew: true, viewname },
        })
      }
    },
    async saveFiltersInExistingView() {
      let { currentViewDetail, moduleName, appliedFilters } = this

      delete currentViewDetail.defaultModuleFields

      let params = {
        moduleName,
        view: {
          ...currentViewDetail,
          filtersJson: JSON.stringify(appliedFilters),
        },
      }

      try {
        await this.editView(params)
        this.$message.success(this.$t('filters.views.edit_success'))

        let currentQuery = { ...this.$route.query }

        delete currentQuery.search
        this.$router.push({ query: currentQuery })
      } catch (error) {
        this.$message.error(error.message || 'View edited failed!')
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.scheduler-tags-container {
  background: #fff;
  border: solid 1px #ececec;
  display: flex;
  padding: 10px;
  margin-bottom: 10px;
  overflow: auto;
  box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.15);

  .scheduler-tags-layout {
    display: flex;
    flex-grow: 1;
    overflow-x: scroll;

    .scheduler-tag {
      display: flex;
      margin-right: 10px;
      border-radius: 14px;
      font-weight: 500;
      font-size: 12px;
      letter-spacing: 0.5px;
      color: #324056;
      border: solid 1px #2a94fd;
      background-color: #f4f8fe;

      .scheduler-close-icon {
        fill: #bbc5cc;
      }
    }
  }
  .scheduler-tags-action {
    display: flex;
    border: 1px solid #dae0e8;
    height: 32px;
    flex-shrink: 0;
    margin-left: 20px;

    .scheduler-tags-clear,
    .scheduler-tags-save,
    .scheduler-tags-save .el-dropdown-link {
      align-self: center;
      padding: 8px;
      text-transform: uppercase;
      letter-spacing: 1px;
      color: #324056;
      font-size: 12px;
      font-weight: 500;
      cursor: pointer;
    }
    .scheduler-tags-save {
      border-left: 1px solid #dae0e8;
    }
  }
}
</style>
