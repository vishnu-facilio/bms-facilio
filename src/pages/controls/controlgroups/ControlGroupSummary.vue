<template>
  <div class="control-group-summary">
    <div
      class="summaryHeader fc-pm-summary-header fc-new-pm-summary-header d-flex"
    >
      <div class="d-flex flex-row justify-between width100">
        <div class="primary-field summary-header-heading pT5">
          <div>
            <template v-if="!$validation.isEmpty(record)">
              <span class="fc-id">#{{ record.id }}</span>
              <div
                class="heading-black18 f16 max-width500px textoverflow-ellipsis"
              >
                {{ record.name }}
              </div>
            </template>
          </div>
        </div>
        <div class="display-flex mB30">
          <el-button
            v-if="isTenantGroup"
            @click="resetChanges"
            :loading="resetLoading"
            class="reset-btn stateflow-btn-wrapper el-button el-button--primary letter-spacing0_6"
          >
            {{ $t('common._common.reset_changes') }}
          </el-button>
          <el-button
            v-if="isTenantGroup"
            @click="showChangeSchedule = true"
            type="button"
            class="fc-wo-border-btn letter-spacing-normal"
          >
            {{ $t('common.profile.change_schedule') }}
          </el-button>
          <button
            v-if="!isTenantGroup"
            @click="editRecord"
            type="button"
            class="d-flex el-button fc-wo-border-btn pL15 pR15 self-center el-button--button"
          >
            <span><i class="el-icon-edit" tabindex="0"></i></span>
          </button>
        </div>
      </div>
    </div>
    <div class="clearboth fc-pm-summary-tab wo-summary-container">
      <el-tabs v-model="activeTab">
        <el-tab-pane :label="$t('common.header.schedule')" name="schedule">
          <ScheduleTab
            :moduleName="moduleName"
            :group="record"
            :isLoading="loading"
            @deleteException="deleteException"
            @editException="editException"
          />
        </el-tab-pane>
        <el-tab-pane
          :label="$t('common.header.group_types')"
          name="groupType"
          lazy
        >
          <GroupTypesTab
            :group="record"
            :groupSections="groupTypes"
            :isLoading="loading"
            :moduleName="moduleName"
          />
        </el-tab-pane>
        <el-tab-pane
          v-if="!isTenantGroup"
          :label="$t('common._common.tenants_override')"
          name="tenantOverride"
          lazy
        >
          <TenantsOverride :group="record" :moduleName="moduleName" />
        </el-tab-pane>
        <el-tab-pane
          v-if="isTenantGroup"
          :label="$t('common.header.schedule_changes')"
          name="scheduleChanges"
          lazy
        >
          <ExceptionList
            v-if="activeTab === 'scheduleChanges'"
            :group="record"
            :isLoading="loading"
            :tenantId="tenantId"
            style="height: calc(100vh - 195px)"
            @onDelete="scheduleChange"
            @editRecord="editException"
            :moduleName="
              isTenantGroup
                ? 'controlScheduleExceptionTenant'
                : 'controlScheduleException'
            "
          />
        </el-tab-pane>
        <el-tab-pane
          :label="$t('common.header.Commands')"
          name="commands"
          lazy
          v-if="!isTenantGroup"
        >
          <CommandsList
            v-if="activeTab === 'commands'"
            :group="record"
            :isLoading="loading"
            style="height: calc(100vh - 195px)"
            :moduleName="moduleName"
          />
        </el-tab-pane>
      </el-tabs>
    </div>

    <ChangeSchedule
      v-if="showChangeSchedule"
      :closeDialog="closeDialog"
      :group="record"
      @onSave="scheduleChange"
      :record="editDeleteSlot"
      :isTenantGroup="isTenantGroup"
      :tenantId="tenantId"
      :moduleName="
        isTenantGroup
          ? 'controlScheduleExceptionTenant'
          : 'controlScheduleException'
      "
    />

    <DeleteSchedule
      v-if="showDeleteDialog"
      :closeDialog="closeDialog"
      :recordId="exceptionId"
      :slotData="editDeleteSlot"
      moduleName="controlScheduleExceptionTenant"
    />
  </div>
</template>

<script>
import { API } from '@facilio/api'
import ScheduleTab from './components/ScheduleTab'
import GroupTypesTab from './components/GroupTypesTab'
import TenantsOverride from './components/TenantsOverride'
import CommandsList from './components/CommandsList'
import ExceptionList from './components/ExceptionList'
import { isEmpty } from '@facilio/utils/validation'
import ChangeSchedule from './components/ChangeSchedule'
import DeleteSchedule from './components/DeleteSchedule'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['moduleName', 'id'],
  components: {
    ScheduleTab,
    GroupTypesTab,
    TenantsOverride,
    CommandsList,
    ChangeSchedule,
    ExceptionList,
    DeleteSchedule,
  },
  data() {
    return {
      record: [],
      activeTab: 'schedule',
      loading: true,
      showChangeSchedule: false,
      resetLoading: false,
      showDeleteDialog: false,
      editDeleteSlot: null,
    }
  },
  computed: {
    recordId() {
      let { id } = this
      return id
    },
    tenantId() {
      let {
        tenant: { id },
      } = this.record
      return id
    },
    groupTypes() {
      let {
        record: { sections },
      } = this

      if (!isEmpty(sections)) return sections
      else return []
    },
    isTenantGroup() {
      let { moduleName } = this
      return moduleName === 'controlGroupv2TenantSharing'
    },
  },
  watch: {
    recordId: {
      handler() {
        this.loadRecord()
        this.activeTab = 'schedule'
      },
      immediate: true,
    },
  },
  methods: {
    async loadRecord() {
      this.loading = true
      let { id } = this.$route.params
      let { moduleName } = this
      let { [moduleName]: record, error } = await API.fetchRecord(moduleName, {
        id,
      })
      if (isEmpty(error)) {
        this.record = record
      } else {
        this.$message.error(error.message || 'Error Occured')
      }
      this.loading = false
    },
    editRecord() {
      let { moduleName } = this
      let id = this.$getProperty(this.$route, 'params.id', '')
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        let route = {
          name,
          params: { id },
        }
        this.$router.push(route)
      } else {
        this.$router
          .push({
            name: 'group-edit',
            params: { id, moduleName },
            query: {
              ...this.$route.query,
            },
          })
          .catch(() => {})
      }
    },

    closeDialog(isSaved) {
      this.showChangeSchedule = false
      this.showDeleteDialog = false
      this.editDeleteSlot = null
      if (isSaved) this.loadRecord()
    },
    async resetChanges() {
      this.resetLoading = true

      let url = '/v3/control/resetTenantChanges'
      let { id, tenantId } = this
      let params = {
        group: { id: parseInt(id) },
        tenant: { id: parseInt(tenantId) },
      }

      let { error } = await API.post(url, params)

      if (!isEmpty(error)) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success(this.$t('common.dialog.changes_are_reset'))
      }

      this.resetLoading = false
      this.loadRecord()
    },
    scheduleChange() {
      this.showChangeSchedule = false
      this.loadRecord()
    },
    editException(slot) {
      this.showChangeSchedule = true
      this.editDeleteSlot = slot
    },
    deleteException({ id, slot }) {
      this.exceptionId = id
      this.editDeleteSlot = slot
      this.showDeleteDialog = true
    },
  },
}
</script>

<style scoped>
.control-group-summary {
  flex-grow: 1;
}
.wo-summary-container {
  margin: -40px 20px 0px;
}

.summaryHeader {
  min-height: 80px;
  padding: 15px 20px 10px 20px;
  background: #fff;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}
.reset-btn {
  box-shadow: 0 2px 4px 0 rgba(230, 230, 230, 0.5) !important;
  border: solid 1px #39b2c2;
  background-color: #39b2c2;
  color: #fff;
  text-transform: uppercase;
  font-size: 12px;
}
</style>
<style lang="scss">
.control-group-summary {
  .schedule-visualizer-container {
    height: calc(100vh - 300px);
  }
}
</style>
