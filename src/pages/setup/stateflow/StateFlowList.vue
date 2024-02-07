<template>
  <div class="height100 overflow-hidden">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title text-capitalize">{{ title }}</div>
        <div class="heading-description">
          {{ $t('setup.stateflow.list_desc') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          class="setup-el-btn"
          @click="createStateFlow()"
        >
          {{ $t('setup.stateflow.header_add') }}
        </el-button>
      </div>
    </div>
    <div class="pL30 pB15">
      <portal-target name="automation-modules"></portal-target>
    </div>
    <slot name="subHeaderMenu"></slot>
    <el-tabs
      class="alarm-action-tab"
      v-model="currentTab"
      @tab-click="switchTab"
      :before-leave="() => false"
    >
      <el-tab-pane label="Stateflows" name="stateflow.list"></el-tab-pane>
      <el-tab-pane label="States" name="stateflow.states" lazy></el-tab-pane>
    </el-tabs>
    <div class="container-scroll mT15" style="height: calc(100vh - 250px);">
      <div class="setting-Rlayout setting-list-view-table">
        <el-row class="header-row">
          <el-col :span="5" class="setting-table-th setting-th-text">
            {{ $t('setup.stateflow.name') }}
          </el-col>
          <el-col :span="6" class="setting-table-th setting-th-text">
            {{ $t('maintenance._workorder.description') }}
          </el-col>
          <el-col
            :span="5"
            class="setting-table-th setting-th-text text-center"
          >
            {{ $t('setup.setupLabel.last_published') }}
          </el-col>
          <el-col
            :span="4"
            class="setting-table-th setting-th-text text-center"
          >
            {{ $t('maintenance._workorder.status') }}
          </el-col>
          <el-col :span="4" class="setting-table-th setting-th-text">
            {{ '\xa0' }}
          </el-col>
        </el-row>
        <el-row v-if="loading">
          <el-col :span="24" class="text-center">
            <spinner :show="loading" size="80"></spinner>
          </el-col>
        </el-row>
        <el-row v-else-if="$validation.isEmpty(stateFlows)">
          <el-col class="body-row-cell text-center" :span="24">{{
            $t('setup.empty.empty_stateflows')
          }}</el-col>
        </el-row>
        <draggable
          v-else
          v-model="stateFlows"
          v-bind="dragOptions"
          draggable=".is-draggable"
          handle=".task-handle"
          @change="reorderStateFlows()"
        >
          <el-row
            :class="[
              !stateflow.defaltStateFlow ? 'is-draggable' : 'is-default',
              'body-row tablerow',
            ]"
            v-for="stateflow in stateFlows"
            :key="stateflow.id"
          >
            <el-col
              class="body-row-cell d-flex"
              :span="5"
              @click.native="goToBuilder(stateflow)"
            >
              <div class="vertical-middle task-handle inline pR10">
                <img src="~assets/drag-grey.svg" />
              </div>
              <div>{{ stateflow.name }}</div>
            </el-col>
            <el-tooltip
              :content="stateflow.description"
              placement="top"
              :open-delay="1200"
              :manual="
                $validation.isEmpty(stateflow.description) ||
                  stateflow.description.length < 30
              "
            >
              <el-col
                class="body-row-cell"
                :span="6"
                @click.native="goToBuilder(stateflow)"
                >{{ stateflow.description || '---' }}
              </el-col>
            </el-tooltip>
            <el-col
              :span="5"
              class="body-row-cell pointer pT13 pB13 text-center"
              v-if="stateflow.draft"
            >
              <async-button
                buttonType="primary"
                buttonClass="publish-button"
                size="mini"
                :clickAction="publish"
                :actionParams="[stateflow]"
                >{{ $t('commissioning.list.publish') }}</async-button
              >
            </el-col>
            <el-col :span="5" v-else class="body-row-cell text-center">
              {{ getFormattedDate(stateflow) }}
            </el-col>
            <el-col :span="4" class="body-row-cell text-center">
              <el-switch
                v-if="!stateflow.defaltStateFlow"
                v-model="stateflow.status"
                @change="changeStatus(stateflow)"
                class="Notification-toggle"
                active-color="rgba(57, 178, 194, 0.8)"
                inactive-color="#e5e5e5"
              ></el-switch>
            </el-col>
            <el-col class="body-row-cell stateflow-actions-cell" :span="4">
              <div
                v-if="!stateflow.locked"
                @click="editStateFlow(stateflow)"
                class="text-left actions mT0 mR15 text-center cursor-pointer"
                v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
                :content="$t('common._common.edit')"
              >
                <i class="el-icon-edit pointer"></i>
              </div>
              <div
                @click="cloneStateFlow(stateflow)"
                class="text-left actions mT0 mR15 text-center cursor-pointer"
                v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
                :content="$t('common._common.clone')"
              >
                <inline-svg
                  src="svgs/copy"
                  iconClass="icon clone-icon"
                ></inline-svg>
              </div>
            </el-col>
          </el-row>
        </draggable>
      </div>
    </div>
    <new-state-flow
      v-if="showNewStateFlowDialog"
      :stateflow="selectedStateFlowObj"
      :isNew="isNew"
      :closeDialog="closeDialog"
      :module="module"
    ></new-state-flow>
    <CloneStateFlow
      v-if="showCloneDialog"
      :referenceStateflowId="referenceStateflowId"
      :stateflow="selectedStateFlowObj"
      :closeDialog="closeDialog"
      :module="module"
    ></CloneStateFlow>
  </div>
</template>
<script>
import NewStateFlow from './NewStateFlow'
import CloneStateFlow from './CloneStateFlow'
import draggable from 'vuedraggable'
import { formatDate } from 'src/util/filters'
import AsyncButton from '@/AsyncButton'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: [
    'moduleName',
    'transitionRouteName',
    'isCustomModule',
    'moduleDisplayName',
  ],
  components: { NewStateFlow, CloneStateFlow, draggable, AsyncButton },
  title() {
    return `Stateflows`
  },
  data() {
    return {
      module: null,
      loading: false,
      stateFlows: [],
      isNew: false,
      showNewStateFlowDialog: false,
      showCloneDialog: false,
      selectedStateFlowObj: {},
      referenceStateflowId: null,
      dragOptions: {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'tasksection',
        sort: true,
      },
      currentTab: 'stateflow.list',
    }
  },
  computed: {
    title() {
      let { module, moduleDisplayName } = this
      if (!isEmpty(moduleDisplayName)) {
        return `${moduleDisplayName} Stateflows`
      }
      return `${module} Stateflows`
    },
  },
  created() {
    if (isEmpty(this.module) && !isEmpty(this.moduleName)) {
      this.module = this.moduleName
    }
    this.getStateFlowList()
  },
  methods: {
    getFormattedDate(stateflow) {
      let { publishedDate, modifiedTime } = stateflow
      if (!isEmpty(publishedDate)) {
        return formatDate(publishedDate, true)
      } else if (!isEmpty(modifiedTime)) {
        return formatDate(modifiedTime, true)
      }
      return '__'
    },
    switchTab(tab) {
      let params = {
        moduleName: this.module,
      }
      this.$router.push({ name: tab.name, params })
    },
    getStateFlowList() {
      this.loading = true
      return this.$http
        .post('/v2/stateflow/list', { moduleName: this.module })
        .then(({ data }) => {
          if (!isEmpty(data.result.stateFlows) && data.responseCode == 0) {
            // Sort stateflows so that the default stateflow is at the last
            let stateFlows = data.result.stateFlows
            let defaultStateFlow = stateFlows.findIndex(
              stateflow => stateflow.defaltStateFlow
            )
            // Move the default stateflow to the end of the list
            defaultStateFlow = stateFlows.splice(defaultStateFlow, 1)[0]
            stateFlows.push(defaultStateFlow)

            this.stateFlows = stateFlows
          }
        })
        .catch(({ message = 'Error loading stateflows' }) => {
          this.$message.error(message)
        })
        .finally(() => (this.loading = false))
    },
    createStateFlow() {
      this.showNewStateFlowDialog = true
      this.isNew = true
      this.selectedStateFlowObj = {}
    },
    editStateFlow(stateFlowObj) {
      this.selectedStateFlowObj = this.$helpers.cloneObject(stateFlowObj)
      this.isNew = false
      this.showNewStateFlowDialog = true
    },
    cloneStateFlow(stateflow) {
      let { id, name, description, defaultStateId } = stateflow
      this.selectedStateFlowObj = { name, description, defaultStateId }
      this.referenceStateflowId = id
      this.showCloneDialog = true
    },
    closeDialog(needsRefresh = false) {
      this.showNewStateFlowDialog = false
      this.showCloneDialog = false
      this.isNew = false
      this.selectedStateFlowObj = {}
      this.referenceStateflowId = null
      if (needsRefresh) this.getStateFlowList()
    },
    goToBuilder(stateflow) {
      if (stateflow.locked) {
        return
      }
      let { isCustomModule } = this
      let params = {
        stateFlowId: stateflow.id,
        moduleId: stateflow.moduleId,
        isCustomModule,
      }
      this.$router.push({ name: 'stateflow.edit', params })
    },
    goToTransitions(stateflow) {
      let params = {
        stateFlowId: stateflow.id,
        moduleId: stateflow.moduleId,
      }

      if (!isEmpty(this.transitionRouteName)) {
        this.$router.push({ name: this.transitionRouteName, params })
      } else {
        this.$router.push({ name: 'stateflow.transitions', params })
      }
    },
    reorderStateFlows() {
      return this.$http
        .post('/v2/stateflow/rearrange', {
          moduleName: this.module,
          stateFlows: this.stateFlows
            .filter(stateflow => !stateflow.defaltStateFlow)
            .map(stateflow => ({ id: stateflow.id })),
        })
        .then(response => {
          if (response.data.responseCode !== 0) {
            this.$message.error(response.data.message || 'Error Occurred')
          } else {
            this.$message.success('Reorder Successful')
          }
        })
        .catch(response => {
          this.$message.error(response.data.message || 'Error Occurred')
        })
    },
    publish(stateflow) {
      return this.$http
        .get('/v2/stateflow/publish?stateFlowId=' + stateflow.id)
        .then(response => {
          if (response.data.responseCode == 0) {
            this.getStateFlowList()
          }
        })
        .catch(() => {
          this.$message.error('Failed to publish.')
        })
    },
    changeStatus({ id, status }) {
      return this.$http
        .post('/v2/stateflow/changeStatus', {
          id,
          status,
          stateFlows: this.stateFlows
            .filter(stateflow => !stateflow.defaltStateFlow)
            .map(stateflow => ({ id: stateflow.id })),
          moduleName: this.module,
        })
        .then(response => {
          if (response.data.responseCode !== 0) {
            this.$message.error(response.data.message || 'Error Occurred')
          }
        })
        .catch(response => {
          this.$message.error(response.data.message || 'Error Occurred')
        })
    },
  },
}
</script>
<style scoped>
.alarm-action-tab {
  position: sticky;
  top: 82px;
  z-index: 5;
  background-color: #f8f9fa;
}
.setting-list-view-table .setting-table-th {
  vertical-align: middle;
  background-color: #fff;
  border: none;
}
.body-row {
  border: 1px solid transparent;
  border-bottom: 1px solid #f2f5f6;
  background-color: #fff;
}
.body-row:hover {
  background-color: #fff;
  border: 1px solid #b0dbe1;
  z-index: 1;
}
.body-row:not(.is-draggable),
.body-row:hover:not(.is-draggable) {
  border-color: #d5efff;
  background-color: #eff5f9;
}
.body-row:first-of-type {
  border-top: 1px solid #f2f5f6;
}
.body-row:first-of-type:hover {
  border: 1px solid #b0dbe1;
}
.body-row-cell {
  border-top: none;
  border-left: none;
  border-right: none;
  color: #333;
  font-size: 14px;
  border-collapse: separate;
  padding: 15px 30px;
  letter-spacing: 0.6px;
  font-weight: 400;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.body-row-cell:first-of-type {
  padding-left: 15px;
}
.body-row.is-draggable .task-handle {
  cursor: move;
}
.body-row .actions {
  visibility: hidden;
}
.body-row:hover .actions {
  visibility: visible;
}
.publish-button {
  background-color: rgb(57, 178, 194) !important;
  border-color: rgb(57, 178, 194) !important;
  padding-bottom: 5px;
  padding-top: 5px;
}
.publish-button:hover {
  background-color: #33a6b5 !important;
}
</style>
<style lang="scss">
.stateflow-actions-cell {
  display: flex;
  flex-direction: row;
  .clone-icon {
    fill: none;
    stroke: #319aa8;
  }
}
</style>
