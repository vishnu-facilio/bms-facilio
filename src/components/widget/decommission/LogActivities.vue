<template>
  <div class="block mL30 mT20">
    <el-timeline class="decommission-activity">
      <spinner :size="80" :show="loading" v-if="loading"></spinner>
      <div v-else-if="isLogsEmpty">
        <div
          class="flex-middle width100 height80vh justify-center shadow-none white-bg-block flex-direction-column"
        >
          <InlineSvg
            src="svgs/emptystate/history"
            iconClass="icon icon-xxxxlg mR10"
          ></InlineSvg>
          <div class="nowo-label text-center pT10">
            {{ $t('setup.decommission.no_logs') }}
          </div>
        </div>
      </div>
      <template v-else v-for="(activity, index) in activities">
        <el-timeline-item
          type="primary"
          :color="activity.color"
          :timestamp="activity.commissionedTime | formatDate"
          :key="index"
          v-if="!$validation.isEmpty(getMessage(activity))"
        >
          <div class="pT4">
            <avatar
              size="sm"
              :user="{ name: getUserName(activity) }"
            ></avatar>
            {{ getUserName(activity) }}
            <span v-html="getMessage(activity)"></span>
            <span
              @click="viewLogDialog(activity)"
              class="tag-style pL25 pointer f14"
              type="text"
            >
              {{ $t('setup.decommission.log') }}
            </span>
          </div>
        </el-timeline-item>
      </template>
    </el-timeline>
    <DecommissionLogDialog
      v-if="showLogDialog"
      :centerDialogVisible="showLogDialog"
      :rowData="logResourceData"
      :moduleName="selectedModule"
      @handleClose="handleClose"
    />
  </div>
</template>
<script>
import { API } from '@facilio/api'
import DecommissionLogDialog from './DecommissionLogsDialog.vue'
import { isEmpty } from '@facilio/utils/validation'
import Avatar from '@/Avatar'
export default {
  props: ['recordId', 'selectedModule', 'details'],
  data() {
    return {
      activities: [],
      loading: false,
      showLogDialog: false,
      logResourceData: {},
    }
  },
  created() {
    this.loadLogs()
  },
  computed: {
    isLogsEmpty() {
      let { activities } = this
      return isEmpty(activities)
    },
  },
  components: {
    DecommissionLogDialog,
    Avatar
  },
  methods: {
    getMessage(act) {
      let { selectedModule } = this
      let { decommission } = act || {}
      return `${
        decommission ? 'Decommissioned' : 'Recommissioned'
      } the  <b class="text-capitalize"> ${selectedModule} </b>.`
    },
    viewLogDialog(data) {
      this.showLogDialog = true
      let { commissionedTime, id } = data || {}
      this.logResourceData = {
        commissionedTime,
        name: this.details.name,
        id,
      }
    },
    handleClose() {
      this.showLogDialog = false
    },
    getUserName(data){
      let {commissionedBy} = data || {}
      let { name } = commissionedBy || {}
      return !isEmpty(name) ? name : 'Invalid User'
    },
    async loadLogs() {
      this.loading = true
      let { recordId } = this
      let filters = {
        resourceId: {
          operatorId: 9,
          value: [`${recordId}`],
        },
      }
      let params = {
        filters: JSON.stringify(filters),
        force: true,
      }
      let { error, list } = await API.fetchAll('decommissionLog', params)
      if (!error) {
        this.activities = list
        this.loading = false
        this.$emit('autoResizeWidget')
      } else {
        this.$message.error(
          isEmpty(error.message) ? 'Error Occured' : error.message
        )
      }
    },
  },
}
</script>
<style lang="scss">
.decommission-activity {
  .el-timeline-item__timestamp.is-bottom {
    margin-top: 8px;
    position: absolute;
    left: -70px;
    top: -1px;
  }
  .el-timeline-item__wrapper {
    position: relative;
    padding-left: 130px;
    top: -3px;
  }
  .el-timeline-item {
    position: relative;
    padding-bottom: 20px;
    margin-left: 40px;
  }
  .el-timeline-item__node--normal {
    left: 80px;
    width: 10px;
    height: 10px;
    top: 5px;
    letter-spacing: 0.3px;
    font-size: 12px;
    border: solid 2px #ff3184;
    background: #fff;
  }
  .el-timeline-item__tail {
    margin-left: 80px;
    margin-top: 10px;
  }
  .el-timeline-item__timestamp.is-bottom {
    color: #8ca1ad;
    line-height: 1;
    font-size: 12px;
  }
}
.tag-style {
  color: #107dd3;
  &:hover {
    text-decoration: underline;
  }
}
</style>
