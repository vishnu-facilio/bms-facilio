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
        <el-button type="primary" class="setup-el-btn" @click="addState"
          >Add State</el-button
        >
        <el-dialog
          v-if="showStateEditDialog"
          :visible="true"
          :fullscreen="true"
          :append-to-body="true"
          :before-close="() => closeDialog(false)"
          custom-class="fc-dialog-form fc-dialog-right setup-dialog35 setup-dialog state-list-dialog"
          style="z-index: 1999"
        >
          <new-state
            :isNew="isNew"
            :stateObj="activeStateObj"
            :module="module"
            @onClose="closeDialog"
            @onStateCreate="addStateToList"
            @onStateUpdate="closeDialog(true)"
            @onError="errorMessage"
          ></new-state>
        </el-dialog>
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
    <div class="container-scroll mT15">
      <div class="row setting-Rlayout">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">
                  {{ $t('common.roles.name') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('setup.setupLabel.locked') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('setup.setupLabel.timer_enabled') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('setup.setupLabel.link_name') }}
                </th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody v-if="loading">
              <tr>
                <td colspan="100%" class="text-center">
                  <spinner :show="loading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="!states.length">
              <tr>
                <td colspan="100%" class="text-center">No states found.</td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="(state, index) in states"
                :key="index"
                @click="editState(state)"
              >
                <td>{{ state.displayName }}</td>
                <td>{{ state.recordLocked ? 'Yes' : 'No' }}</td>
                <td>{{ state.timerEnabled ? 'Yes' : 'No' }}</td>
                <td>{{ state.status }}</td>
                <td @click="editState(state)">
                  <div class="text-left actions mT0 mR15 text-center">
                    <i class="el-icon-edit pointer"></i>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import NewState from './NewState'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['moduleName', 'moduleDisplayName'],
  components: { NewState },
  data() {
    return {
      module: null,
      loading: false,
      states: [],
      showStateEditDialog: false,
      isNew: false,
      activeStateObj: {},
      currentTab: 'stateflow.states',
    }
  },
  created() {
    if (isEmpty(this.module) && !isEmpty(this.moduleName)) {
      this.module = this.moduleName
    }
  },
  mounted() {
    this.getStateList()
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
  methods: {
    switchTab(tab) {
      let params = {
        moduleName: this.module,
      }
      this.$router.push({ name: tab.name, params })
    },
    getStateList() {
      this.loading = true

      return this.$http
        .get(`v2/state/list?parentModuleName=${this.moduleName}`)
        .then(({ data }) => {
          this.states = data.result.status || []
        })
        .catch(({ message = 'Error loading states' }) => {
          this.$message.error(message)
        })
        .finally(() => (this.loading = false))
    },

    addState() {
      this.isNew = true
      this.activeStateObj = {}
      this.showStateEditDialog = true
    },

    editState(state) {
      this.activeStateObj = this.$helpers.cloneObject(state)
      this.isNew = false
      this.showStateEditDialog = true
    },

    addStateToList(state) {
      this.states.push(state)
      this.closeDialog()
    },

    closeDialog(needsRefresh = false, state, message) {
      if (needsRefresh) {
        this.$message({
          showClose: true,
          message:
            message || this.$t('maintenance._workorder.state_update_success'),
          type: 'success',
        })
        this.getStateList()
      }

      this.showStateEditDialog = false
      this.isNew = false
      this.activeStateObj = {}
    },

    goToStateFlowList() {
      this.$router.push({ path: 'stateflows' })
    },

    errorMessage(error) {
      this.$message({
        showClose: true,
        message: error.message || 'Could not update state',
        type: 'error',
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
</style>
