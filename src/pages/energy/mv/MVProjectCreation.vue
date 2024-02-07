<template>
  <div class="mv-container">
    <FStepper
      :steps="steps"
      :initialStep="steps[0]"
      :keepAliveData="true"
      :disableGoto="true"
      @finalStep="saveRecord"
      :isEdit="!$validation.isEmpty(projectId)"
    >
      <div
        slot="title"
        class="title-container d-flex flex-direction-column justify-content-end"
      >
        <div class="title f10 bold">PROJECT</div>
        <div class="mT5 f20 fw3 letter-spacing0_5">
          <portal-target name="mv-project-title"></portal-target>
        </div>
      </div>
    </FStepper>
  </div>
</template>

<script>
import FStepper from '@/FStepper'
import MVProjectNew from 'pages/energy/mv/MVProjectNew'
import MVProjectBaseline from 'pages/energy/mv/MVProjectBaseline'
import MVNonRoutineAdjustments from 'pages/energy/mv/MVNonRoutineAdjustments'
import MVDefineReporting from 'pages/energy/mv/MVDefineReporting'
import http from 'util/http'
import { deepCloneObject } from 'util/utility-methods'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForTab,
  tabTypes,
  getApp,
} from '@facilio/router'

const resourceToBeDeletedHash = [
  'adjustments',
  'baselines',
  'projectId',
  'adjustmentsResObj',
  'baselinesResObj',
  'defineReportObj',
]

export default {
  components: {
    FStepper,
  },
  data() {
    return {
      steps: [
        {
          title: `${this.$t('mv.creation.project_details')}`,
          component: MVProjectNew,
        },
        {
          title: `${this.$t('mv.creation.adjusted_baseline')}`,
          component: MVProjectBaseline,
        },
        {
          title: `${this.$t('mv.creation.non_routine_adjustments')}`,
          component: MVNonRoutineAdjustments,
        },
        {
          title: `${this.$t('mv.creation.define_reporting_period')}`,
          component: MVDefineReporting,
        },
      ],
    }
  },
  computed: {
    projectId() {
      return this.$route.params.id
    },
    parentPath() {
      let appLinkName = getApp()?.linkName
      let { path } = findRouteForTab(tabTypes.CUSTOM, {
        config: { type: 'mandv' },
      })
      return `/${appLinkName}/${path}`
    },
  },
  methods: {
    serializedMVData(finalData) {
      let clonedData = deepCloneObject(finalData)
      let { adjustments, baselines, projectId } = clonedData
      clonedData.id = projectId
      resourceToBeDeletedHash.forEach(resource => {
        delete clonedData[resource]
      })
      return {
        mvProject: clonedData,
        baselines,
        adjustments,
      }
    },
    saveRecord(finalData) {
      let mvProjectWrapper = this.serializedMVData(finalData)
      let {
        mvProject: { id },
      } = mvProjectWrapper
      let url = isEmpty(id) ? '/v2/mv/addMVProject' : '/v2/mv/updateMVProject'
      let successMsg = isEmpty(id)
        ? 'Project successfully created'
        : 'Project successfully updated'
      let data = {
        mvProjectWrapper,
      }
      http
        .post(url, data)
        .then(({ data: { message, responseCode } }) => {
          if (responseCode === 0) {
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
            this.$message.success(successMsg)
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
    },
  },
}
</script>

<style lang="scss">
.mv-container {
  .title {
    letter-spacing: 1.5px;
    color: #999999;
  }
  .title-desc {
    color: #333333;
  }
  .title-container {
    height: 80px;
  }
}
</style>
