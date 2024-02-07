<template>
  <div class="height100 overflow-hidden">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title text-capitalize">
          {{ $t('setup.setup.slapolicies') }}
        </div>
        <div class="heading-description">
          {{ $t('setup.setupLabel.list_sla_policie') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button type="primary" class="setup-el-btn" @click="newSLA">
          {{ $t('setup.add.add_sla_policy') }}
        </el-button>
      </div>
    </div>
    <div class="flex-middle justify-between" style="padding: 0px 30px 20px;">
      <portal-target name="sla-modules"></portal-target>
      <pagination
        :pageNo.sync="page"
        :total="totalCount"
        :perPage="10"
        @onPageChanged="val => (page = val)"
      >
      </pagination>
    </div>
    <slot name="subHeaderMenu"></slot>
    <el-tabs
      class="alarm-action-tab"
      v-model="currentTab"
      @tab-click="switchTab"
      :before-leave="() => false"
    >
      <el-tab-pane label="SLA Policies" name="sla.list"></el-tab-pane>
      <el-tab-pane
        :label="$t('setup.setupLabel.sla_tab_switch_title')"
        name="sla.types"
      ></el-tab-pane>
    </el-tabs>
    <div class="container-scroll mT15" style="height: calc(100vh - 250px);">
      <div class="setting-Rlayout setting-list-view-table">
        <el-row class="header-row">
          <el-col :span="5" class="setting-table-th setting-th-text">
            {{ $t('common.roles.name') }}
          </el-col>
          <el-col :span="6" class="setting-table-th setting-th-text">
            {{ $t('maintenance._workorder.description') }}
          </el-col>
          <el-col
            :span="5"
            class="setting-table-th setting-th-text text-center"
          >
            {{ $t('space.sites._site') }}
          </el-col>
          <el-col
            :span="4"
            class="setting-table-th setting-th-text text-center"
          >
            {{ $t('maintenance._workorder.status') }}
          </el-col>
          <el-col :span="4" class="setting-table-th setting-th-text">
            &nbsp;
          </el-col>
        </el-row>
        <el-row v-if="loading">
          <el-col :span="24" class="text-center">
            <spinner :show="loading" size="80"></spinner>
          </el-col>
        </el-row>
        <el-row v-else-if="$validation.isEmpty(slaPolicies)">
          <el-col class="body-row-cell text-center" :span="24">
            {{ $t('setup.empty.empty_sla_policy') }}
          </el-col>
        </el-row>
        <draggable
          v-else
          v-model="slaPolicies"
          v-bind="dragOptions"
          draggable=".is-draggable"
          handle=".task-handle"
          @change="reorderSLA()"
        >
          <el-row
            :class="['is-draggable', 'body-row tablerow']"
            v-for="policy in slaPolicies"
            :key="policy.id"
          >
            <el-col class="body-row-cell d-flex" :span="5">
              <div class="vertical-middle task-handle inline pR10">
                <img src="~assets/drag-grey.svg" />
              </div>
              <div @click="editSLA(policy)" class="pointer">
                {{ policy.name }}
              </div>
            </el-col>
            <el-tooltip
              :content="policy.description"
              placement="top"
              :open-delay="1200"
              :manual="
                $validation.isEmpty(policy.description) ||
                  policy.description.length < 30
              "
            >
              <el-col class="body-row-cell" :span="6">
                {{ policy.description || '---' }}
              </el-col>
            </el-tooltip>
            <el-col :span="5" class="body-row-cell text-center">
              {{ siteList[policy.siteId] }}
            </el-col>
            <el-col :span="4" class="body-row-cell text-center">
              <el-switch
                v-model="policy.status"
                @change="changeStatus(policy)"
                class="Notification-toggle"
                active-color="rgba(57, 178, 194, 0.8)"
                inactive-color="#e5e5e5"
              ></el-switch>
            </el-col>
            <el-col class="body-row-cell" :span="4">
              <div class="text-left actions mT0 text-center d-flex">
                <div class="mR15" @click="editSLA(policy)">
                  <i class="el-icon-edit pointer"></i>
                </div>
                <div v-if="canShowDelete" @click="deleteSLA(policy)">
                  <inline-svg
                    src="svgs/delete"
                    class="f-delete pointer"
                    iconClass="icon icon-sm icon-remove"
                  ></inline-svg>
                </div>
              </div>
            </el-col>
          </el-row>
        </draggable>
      </div>
    </div>
  </div>
</template>
<script>
import draggable from 'vuedraggable'
import isEqual from 'lodash/isEqual'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import Pagination from 'src/components/list/FPagination'
import { getFieldOptions } from 'util/picklist'

export default {
  props: ['moduleName'],
  components: { draggable, Pagination },
  title() {
    return 'SLA Policies'
  },
  data() {
    return {
      module: null,
      loading: false,
      slaPolicies: [],
      totalCount: null,
      dragOptions: {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'tasksection',
        sort: true,
      },
      currentTab: 'sla.list',
      page: 1,
      siteList: { '-1': 'All Sites' },
    }
  },
  computed: {
    canShowDelete() {
      return process.env.NODE_ENV === 'development'
    },
  },
  watch: {
    moduleName: {
      handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal) && !isEmpty(newVal)) {
          this.loadData()
        }
      },
      immediate: true,
    },
    page: function(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.getPolicyList()
      }
    },
  },
  methods: {
    loadData() {
      this.getPolicyList()
      this.loadCount()
    },
    async getPolicyList() {
      this.loading = true

      let { moduleName, page } = this
      let params = {
        ruleType: 12, //SLA ruletype is 12 for SLA policy
        moduleName,
        page,
        perPage: 10,
      }
      let { data, error } = await API.post('v2/modules/rules/list', params)

      if (error) {
        this.$message.error(error.message || 'Error loading SLA Policies')
      } else {
        this.slaPolicies = data.workflowRuleList || []
        await this.loadSites()
      }
      this.loading = false
    },

    async loadCount() {
      let params = {
        ruleType: 12, //SLA ruletype is 12 for SLA policy
        moduleName: this.moduleName,
      }
      let { data, error } = await API.get('v2/modules/rules/getCount', params)

      if (error) {
        this.$message.error(error.message)
      } else {
        this.totalCount = this.$getProperty(data, 'count', null)
      }
    },
    async loadSites() {
      let siteIds = (this.slaPolicies || [])
        .map(policy => policy.siteId)
        .filter(siteId => !isEmpty(siteId))
      let defaultIds = [...new Set(siteIds)]
      let perPage = defaultIds.length

      if (perPage === 0) return

      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'site', skipDeserialize: true },
        defaultIds,
        perPage,
      })

      if (!error) {
        this.siteList = { ...this.siteList, ...options }
      }
    },
    newSLA() {
      this.$router.push({
        name: 'sla.new',
        params: { moduleName: this.moduleName },
      })
    },
    editSLA(slaPolicy) {
      this.$router.push({
        name: 'sla.edit',
        params: {
          id: slaPolicy.id,
          moduleName: this.moduleName,
        },
      })
    },
    async deleteSLA({ id }) {
      let { error } = await API.post('/v2/slaPolicy/delete', { id })

      if (error) {
        this.$message.error('Could not delete SLA Policy')
      } else {
        let index = this.slaPolicies.findIndex(policy => policy.id === id)

        if (index !== -1) {
          this.slaPolicies.splice(index, 1)
          this.totalCount--
          this.$message.success('SLA Policy deleted')
        }
      }
    },
    async reorderSLA() {
      let { moduleName, slaPolicies } = this
      let ids = slaPolicies.map(policy => policy.id)
      let { error } = await API.post('/v2/slaPolicy/reorder', {
        moduleName,
        ids,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.$message.success('Reorder Successful')
      }
    },
    async changeStatus(policy) {
      let { id, status: isActive } = policy
      let url = isActive ? 'setup/turnonrule' : 'setup/turnoffrule'
      let { error } = await API.post(url, { workflowId: id })

      if (error) {
        this.$set(policy, 'status', !isActive)
        this.$message.error('Could not change policy status')
      } else {
        this.$message.success(
          isActive
            ? 'SLA Policy marked as active'
            : 'SLA Policy marked as inactive'
        )
      }
    },
    switchTab(tab) {
      let params = { moduleName: this.moduleName }

      this.$router.push({ name: tab.name, params })
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
.body-row:not(.is-default):not(.is-draggable) {
  border: 1px solid transparent;
  border-bottom: 1px solid #f2f5f6;
  background-color: #f7f8f9;
}
.body-row:hover:not(.is-default):not(.is-draggable) {
  border: 1px solid #b0dbe1;
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
</style>
