<template>
  <div v-if="isLoading" class="mT60 loading-container d-flex">
    <Spinner :show="isLoading"></Spinner>
  </div>
  <div v-else class="mv-summary-container">
    <div class="mv-summary-header d-flex">
      <div class="mv-back-container cursor-pointer">
        <div class="flex-middle">
          <div @click="redirectToMvList" class="flex-middle">
            <inline-svg
              src="svgs/arrow"
              class="vertical-middle"
              iconClass="icon icon-xs mR5 arrow rotate-left"
            ></inline-svg>
            <span class="mv-back-btn f13">Back</span>
            <div class="fc-separator-lg mL20 mR20"></div>
            <div class="heading-black22">{{ projectName }}</div>
          </div>
        </div>
      </div>
      <!-- <div class="mv-download-btn mL-auto">
        <el-button class="fc-create-btn">DOWNLOAD REPORT</el-button>
      </div>
      <div class="mv-edit-btn">
        <inline-svg src="svgs/edit" class="vertical-middle" iconClass="icon icon-sm mR25 icon-edit"></inline-svg>
      </div>-->
      <div class="mL-auto flex-middle">
        <el-button
          type="button"
          class="fc-wo-border-btn pL15 pR15"
          @click="editMVProject"
        >
          <i class="el-icon-edit"></i>
        </el-button>
        <el-dropdown
          class="mL10 fc-btn-ico-lg pointer"
          trigger="click"
          style="padding-top: 4px; padding-bottom: 4px;"
        >
          <span class="el-dropdown-link">
            <img src="~assets/menu.svg" width="16" height="16" />
          </span>
          <el-dropdown-menu slot="dropdown" class="p10">
            <div
              class="pT10 pB10 fc-label-hover label-txt-black f14 pL10 pR10 pointer"
              @click="print"
            >
              Print
            </div>
            <!-- <div class="pT10 pB10 fc-label-hover label-txt-black f14 pL10 pR10 pointer">
            Download Summary
          </div> -->
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <page :module="moduleName" :id="mvId" :details="mvProjectObj"></page>
  </div>
</template>

<script>
import Page from '@/page/PageBuilder'
import Spinner from '@/Spinner'
import {
  isWebTabsEnabled,
  findRouteForTab,
  tabTypes,
  getApp,
} from '@facilio/router'

import http from 'util/http'
import { isEmpty } from '@facilio/utils/validation'

const frequencyHash = {
  1: 'Till yesterday',
  3: 'Till last month',
}

export default {
  components: {
    Page,
    Spinner,
  },
  data() {
    return {
      moduleName: 'mvproject',
      mvId: this.$route.params.id,
      isLoading: false,
      mvProjectObj: {},
    }
  },
  computed: {
    projectName() {
      let { mvProjectObj } = this
      let { mvProject = {} } = mvProjectObj
      return isEmpty(mvProject) ? '' : mvProject.name
    },
    pdfUrl() {
      let appName = getApp()?.linkName
      appName = appName === 'newapp' ? 'app' : appName
      return (
        window.location.protocol +
        '//' +
        window.location.host +
        `/${appName}/pdf/mv/${this.mvId}`
      )
    },
    parentPath() {
      let appLinkName = getApp()?.linkName
      let { path } = findRouteForTab(tabTypes.CUSTOM, {
        config: { type: 'mandv' },
      })
      return `/${appLinkName}/${path}`
    },
  },
  created() {
    let promises = []
    let { mvId } = this
    let fetchMVDetailsUrl = `/v2/mv/getMVProject?mvProjectId=${mvId}`
    let fetchMVCardDetailsUrl = `/v2/workflow/getDefaultWorkflowResult`
    let fetchMVSubmoduleDetailsUrl =
      '/v2/readings/getSubModuleRel?moduleName=mvproject'
    let params = {
      defaultWorkflowId: 3,
      paramList: [mvId],
    }
    this.isLoading = true
    promises.push(http.get(fetchMVDetailsUrl))
    promises.push(http.post(fetchMVCardDetailsUrl, params))
    promises.push(http.get(fetchMVSubmoduleDetailsUrl))
    Promise.all(promises)
      .then(([mvData, mvCardData, mvSubmodules]) => {
        let {
          data: {
            message: mvDataMessage,
            responseCode: mvDataResponseCode,
            result: mvDataResult,
          },
        } = mvData
        let {
          data: {
            message: mvCardDataMessage,
            responseCode: mvCardDataResponseCode,
            result: mvCardDataResult,
          },
        } = mvCardData
        let {
          data: {
            message: mvSubmodulesMessage,
            responseCode: mvSubmodulesResponseCode,
            result: mvSubmodulesResult,
          },
        } = mvSubmodules

        if (mvDataResponseCode === 0) {
          let { mvProjectWrapper = {} } = mvDataResult
          let {
            mvProject: { frequency },
          } = mvProjectWrapper
          this.mvProjectObj = mvProjectWrapper
          this.mvProjectObj.frequencyLabel = frequencyHash[frequency]
        } else {
          throw new Error(mvDataMessage)
        }
        if (mvCardDataResponseCode === 0) {
          let { workflow = {} } = mvCardDataResult
          let { returnValue = {} } = workflow
          this.mvProjectObj.returnValue = returnValue
        } else {
          throw new Error(mvCardDataMessage)
        }

        if (mvSubmodulesResponseCode === 0) {
          this.mvProjectObj.submodules = {}
          mvSubmodulesResult.submodules.forEach(submodule => {
            this.mvProjectObj.submodules[submodule.name] = submodule
          })
        } else {
          throw new Error(mvSubmodulesMessage)
        }
      })
      .catch(({ message }) => {
        this.$message.error(message)
      })
      .finally(() => (this.isLoading = false))
  },
  methods: {
    redirectToMvList() {
      if (isWebTabsEnabled()) {
        let { parentPath } = this
        let path = `${parentPath}/open`
        this.$router.push({
          path,
        })
      } else {
        this.$router.push({
          path: '/app/em/mv/open',
        })
      }
    },
    editMVProject() {
      if (isWebTabsEnabled()) {
        let { mvId, parentPath } = this
        let path = `${parentPath}/edit/${mvId}`
        this.$router.push({
          path,
        })
      } else {
        this.$router.push({
          name: 'mv-project-edit',
          params: {
            id: this.mvId,
          },
        })
      }
    },
    print() {
      window.open(this.pdfUrl)
    },
    download() {
      this.$http.get(`/v2/integ/pdf/create?url=${this.pdfUrl}&download=false`)
    },
  },
}
</script>

<style lang="scss">
.mv-summary-container {
  .mv-summary-header {
    padding: 20px 30px;
    background: #fff;
    .mv-back-btn {
      color: #39b2c2;
      letter-spacing: 0.45px;
    }
    .mv-title {
      letter-spacing: 0.47px;
      color: #324056;
    }
    .mv-edit-btn {
      border-radius: 3px;
      border: solid 1px #d9e0e7;
      background-color: #ffffff;
    }
  }
}
</style>
