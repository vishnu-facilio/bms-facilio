<template>
  <CommonListLayout :getPageTitle="() => viewName" pathPrefix="/app/em/mv/">
    <template #views>
      <ul class="subheader-tabs pull-left d-flex">
        <div v-for="(item, index) in views" :key="index">
          <li
            :key="item.name + index"
            :class="[
              currentView === item.name && 'active',
              item.name === 'open' ? 'border-right' : '',
            ]"
          >
            <router-link :to="item.path">
              <span>{{ item.label }}</span>
            </router-link>

            <div class="sh-selection-bar"></div>
          </li>
        </div>
      </ul>
    </template>

    <template #header>
      <button
        class="fc-create-btn pB25"
        style="margin-top: -10px;"
        @click="redirectToFormCreation()"
      >
        New PROJECT
      </button>
    </template>

    <!-- List Content -->
    <div class="mv-list-container height100">
      <div
        class="fc-list-view p10 pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
      >
        <div v-if="isLoading" class="flex-middle fc-empty-white">
          <spinner :show="isLoading" size="80"></spinner>
        </div>
        <div
          v-if="$validation.isEmpty(mvProjectList) && !isLoading"
          class="height100vh fc-empty-white flex-middle justify-content-center flex-direction-column"
        >
          <inline-svg
            src="svgs/list-empty"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="mT10 fc-black-dark f16 fw6">
            <div class="mT10 label-txt-black f14">No Project Available</div>
          </div>
        </div>
        <el-table
          v-if="!isLoading && !$validation.isEmpty(mvProjectList)"
          :data="mvProjectList"
          style="width: 100%"
          height="auto"
          :fit="true"
        >
          <el-table-column
            v-for="(header, index) in headerColumns"
            :key="index"
            :prop="header.prop"
            :label="header.label"
            :min-width="header.minWidth"
          >
            <template v-slot="scope">
              <div v-if="header.prop === 'siteId'">
                <span>{{ getSiteName(scope.row[header.prop]) }}</span>
              </div>
              <div v-else-if="header.prop === 'reportingPeriod'">
                <div
                  class="fc__wo__task__bar fc__wo__task__bar-stacked"
                  :title="getStatusPercentage(scope.row) + '%'"
                  v-tippy
                >
                  <span
                    class="fc-progress-bar"
                    :style="`width: ${getStatusPercentage(scope.row)}%`"
                  ></span>
                </div>
              </div>
              <div v-else-if="header.prop === 'owner'">
                <div v-if="$validation.isEmpty(scope.row[header.prop])">
                  ---
                </div>
                <user-avatar
                  v-else
                  size="md"
                  :user="scope.row[header.prop]"
                  :id="scope.row[header.prop].id"
                >
                </user-avatar>
              </div>
              <div v-else-if="header.prop === 'edit/delete'">
                <div class="d-flex justify-content-center edit-delete">
                  <span @click="editMVProject(scope.row.id)">
                    <inline-svg
                      src="svgs/edit"
                      class="vertical-middle"
                      iconClass="icon icon-sm mR25 icon-edit"
                    >
                    </inline-svg>
                  </span>
                  <span @click="showConfirmDelete(scope.row.id)">
                    <inline-svg
                      src="svgs/delete"
                      class="vertical-middle"
                      iconClass="icon icon-sm icon-remove"
                    >
                    </inline-svg>
                  </span>
                </div>
              </div>
              <div v-else-if="header.prop === 'meter'">
                {{ scope.row[header.prop].name }}
              </div>
              <div
                v-else-if="header.prop === 'name'"
                @click="redirectToSummary(scope.row.id)"
              >
                {{ scope.row[header.prop] }}
              </div>
              <div v-else>{{ scope.row[header.prop] }}</div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </CommonListLayout>
</template>

<script>
import http from 'util/http'
import { mapGetters } from 'vuex'
import Spinner from '@/Spinner'
import UserAvatar from '@/avatar/User'
import CommonListLayout from 'newapp/list/DeprecatedCommonLayout'
import {
  isWebTabsEnabled,
  findRouteForTab,
  tabTypes,
  getApp,
} from '@facilio/router'

export default {
  components: {
    Spinner,
    UserAvatar,
    CommonListLayout,
  },
  computed: {
    ...mapGetters(['getSite']),
    currentView() {
      return this.$route.params.viewname
    },
    viewName() {
      let viewList = {
        open: 'Open Projects',
        closed: 'Closed Projects',
      }
      let title = this.$getProperty(viewList, this.currentView, null)

      if (title) return title
      return 'M&V Projects'
    },
    parentPath() {
      let appLinkName = getApp()?.linkName
      let { path } = findRouteForTab(tabTypes.CUSTOM, {
        config: { type: 'mandv' },
      })
      return `/${appLinkName}/${path}`
    },
  },
  watch: {
    currentView: {
      handler() {
        this.loadMVList()
      },
    },
  },
  data() {
    return {
      isLoading: false,
      headerColumns: [
        {
          label: 'Name',
          prop: 'name',
          minWidth: '25%',
        },
        {
          label: 'Site',
          prop: 'siteId',
          minWidth: '15%',
        },
        {
          label: 'Asset',
          prop: 'meter',
          minWidth: '15%',
        },
        {
          label: 'Project Owner',
          prop: 'owner',
          minWidth: '15%',
        },
        {
          label: 'Status',
          prop: 'reportingPeriod',
          minWidth: '20%',
        },
        {
          label: '',
          prop: 'edit/delete',
          minWidth: '10%',
        },
      ],
      mvProjectList: [],
      views: [
        {
          name: 'open',
          label: 'Open Projects',
          path: '/app/em/mv/open',
        },
        {
          name: 'closed',
          label: 'Closed Projects',
          path: '/app/em/mv/closed',
        },
      ],
    }
  },
  created() {
    if (isWebTabsEnabled()) {
      this.setViewPaths()
    }
    this.loadMVList()
  },
  methods: {
    setViewPaths() {
      let { parentPath } = this
      this.views[0].path = `${parentPath}/open`
      this.views[1].path = `${parentPath}/closed`
    },
    getSiteName(siteId) {
      let siteName = (this.getSite(siteId) || {}).name
      return siteName
    },
    getStatusPercentage(project) {
      let width = 0
      if (project.reportingPeriodStartTime > 0) {
        let totalPeriod =
          project.reportingPeriodEndTime - project.reportingPeriodStartTime
        let completedPeriod =
          this.$options.filters.now() - project.reportingPeriodStartTime
        width = Math.round((completedPeriod / totalPeriod) * 100)
      }
      return width
    },
    editMVProject(projectId) {
      if (isWebTabsEnabled()) {
        let { parentPath } = this
        let path = `${parentPath}/edit/${projectId}`
        this.$router.push({
          path,
        })
      } else {
        this.$router.push({
          name: 'mv-project-edit',
          params: {
            id: projectId,
          },
        })
      }
    },
    showConfirmDelete(projectId) {
      let dialogObj = {
        title: 'Delete Project',
        message: 'Are you sure you want to delete this project?',
        rbDanger: true,
        rbLabel: 'Delete',
      }
      this.$dialog.confirm(dialogObj).then(value => {
        if (value) {
          this.deleteMVProject(projectId)
        }
      })
    },
    deleteMVProject(projectId) {
      let { mvProjectList } = this
      let deletedProjectIndex = mvProjectList.findIndex(
        project => project.id === projectId
      )
      let deletedProjectId = mvProjectList[deletedProjectIndex].id
      let data = {
        mvProjectWrapper: {
          mvProject: {
            id: deletedProjectId,
          },
        },
      }
      let url = `/v2/mv/deleteMVProject`
      http
        .post(url, data)
        .then(({ data: { message, responseCode } }) => {
          if (responseCode === 0) {
            this.mvProjectList.splice(deletedProjectIndex, 1)
            this.$message.success('Project deleted successfully')
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
    },
    loadMVList() {
      let { currentView } = this
      let isOpen = currentView === 'open'
      let url = `/v2/mv/getMVProjectList?isOpen=${isOpen}`
      this.isLoading = true
      let promise = http
        .get(url)
        .then(({ data: { message, responseCode, result = {} } }) => {
          if (responseCode === 0) {
            let { mvprojects } = result
            this.mvProjectList = mvprojects
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
      Promise.all([promise]).finally(() => (this.isLoading = false))
    },
    redirectToSummary(projectId) {
      if (isWebTabsEnabled()) {
        let { currentView = 'open', parentPath } = this
        let path = `${parentPath}/${currentView}/${projectId}/overview`
        this.$router.push({
          path,
        })
      } else {
        this.$router.push({
          name: 'mv-project-summary',
          params: {
            id: projectId,
          },
        })
      }
    },
    redirectToFormCreation() {
      if (isWebTabsEnabled()) {
        let { parentPath } = this
        let path = `${parentPath}/new`
        this.$router.push({
          path,
        })
      } else {
        this.$router.push({
          path: '/app/em/mv/project/new',
        })
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.subheader-tabs {
  list-style-type: none;
  margin: 0;
  padding: 0;
  overflow: hidden;
  background-color: transparent;

  li {
    float: left;
    padding: 0px 15px;
  }
  li.active span {
    font-weight: 500;
  }
  li.active .sh-selection-bar {
    border-right: 0px solid #e0e0e0;
    border-left: 0px solid #e0e0e0;
    border-color: var(--fc-theme-color);
  }
  li .sh-selection-bar {
    border: 1px solid transparent;
    width: 25px;
    margin-top: 7px;
    position: absolute;
  }
}
.border-right {
  border-right: 1px solid #e5e4e4 !important;
}
</style>

<style lang="scss">
.mv-list-container {
  .edit-delete {
    svg {
      display: none;
    }
  }

  .el-table__row:hover {
    .edit-delete {
      svg {
        display: block;
      }
    }
  }

  .fc__wo__task__bar.fc__wo__task__bar-stacked {
    .fc-progress-bar {
      margin: 0;
    }
  }
}
</style>
