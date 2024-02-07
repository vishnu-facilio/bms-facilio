<template>
  <div class="height100 overflow-hidden">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title text-capitalize">
          {{ $t('common.products.approval_process') }}
        </div>
        <div class="heading-description">
          {{
            $t('common._common.list_of_approval_processes_for', {
              moduleName: moduleDisplayName || moduleName,
            })
          }}
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button type="primary" class="setup-el-btn" @click="newApproval">
          {{ $t('common.products.add_approval_process') }}
        </el-button>
      </div>
    </div>
    <div class="pL30 pB15">
      <portal-target name="automation-modules"></portal-target>
    </div>
    <slot name="subHeaderMenu"></slot>

    <div class="container-scroll mT15" style="height: calc(100vh - 250px);">
      <div class="setting-Rlayout setting-list-view-table">
        <el-row class="header-row">
          <el-col :span="5" class="setting-table-th setting-th-text">
            {{ $t('common.products.name') }}
          </el-col>
          <el-col :span="11" class="setting-table-th setting-th-text">
            {{ $t('maintenance._workorder.description') }}
          </el-col>
          <el-col
            :span="4"
            class="setting-table-th setting-th-text text-center"
          >
            {{ $t('common._common.status') }}
          </el-col>
          <el-col :span="4" class="setting-table-th setting-th-text">
            {{ ' ' }}
          </el-col>
        </el-row>
        <el-row v-if="loading">
          <el-col :span="24" class="text-center">
            <spinner :show="loading" size="80"></spinner>
          </el-col>
        </el-row>
        <el-row v-else-if="$validation.isEmpty(approvals)">
          <el-col class="body-row-cell text-center" :span="24">
            {{ $t('common.products.no_approval_process_found') }}
          </el-col>
        </el-row>
        <draggable
          v-else
          v-model="approvals"
          v-bind="dragOptions"
          draggable=".is-draggable"
          handle=".task-handle"
          @change="reorder()"
        >
          <el-row
            :class="['is-draggable', 'body-row tablerow']"
            v-for="policy in approvals"
            :key="policy.id"
          >
            <el-tooltip
              :content="policy.name"
              :open-delay="1500"
              :manual="policy.name.length < 25"
              placement="top"
            >
              <el-col class="body-row-cell d-flex" :span="5">
                <div class="vertical-middle task-handle inline pR10">
                  <img src="~assets/drag-grey.svg" />
                </div>
                <div @click="edit(policy)" class="pointer">
                  {{ policy.name }}
                </div>
              </el-col>
            </el-tooltip>
            <el-tooltip
              :content="policy.description"
              placement="top"
              :open-delay="1200"
              :manual="
                $validation.isEmpty(policy.description) ||
                  policy.description.length < 30
              "
            >
              <el-col class="body-row-cell" :span="11">
                {{ policy.description || '---' }}
              </el-col>
            </el-tooltip>
            <el-col :span="4" class="body-row-cell text-center">
              <el-switch
                v-model="policy.active"
                @change="changeStatus(policy)"
                class="Notification-toggle"
                active-color="rgba(57, 178, 194, 0.8)"
                inactive-color="#e5e5e5"
              ></el-switch>
            </el-col>
            <el-col class="body-row-cell" :span="4">
              <div class="text-left actions mT0 text-center d-flex">
                <div class="mR15" @click="edit(policy)">
                  <i class="el-icon-edit pointer"></i>
                </div>
                <div v-if="canShowDelete" @click="remove(policy)">
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
import remove from 'lodash/remove'
import { mapState } from 'vuex'

export default {
  props: ['moduleName', 'moduleDisplayName'],
  components: { draggable },
  title() {
    return `Approval Processes`
  },
  data() {
    return {
      module: null,
      loading: false,
      approvals: [],
      dragOptions: {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'tasksection',
        sort: true,
      },
    }
  },
  created() {
    this.$store.dispatch('loadSites')
    this.getData()
  },
  computed: {
    ...mapState({
      sites: state => state.sites,
    }),
    canShowDelete() {
      return process.env.NODE_ENV === 'development'
    },
    selectedModule: {
      get() {
        return this.$route.params.moduleName
      },
      set(moduleName) {
        let { name } = this.$route

        this.$router.replace({
          name,
          params: { moduleName },
        })
      },
    },
  },
  methods: {
    getData() {
      this.loading = true
      return this.$http
        .post(`v2/modules/rules/list`, {
          ruleType: 21,
          moduleName: this.moduleName,
        })
        .then(({ data }) => {
          if (data.responseCode == 0) {
            this.approvals = data.result.workflowRuleList || []
          }
        })
        .catch(({ message = 'Error loading approvals' }) => {
          this.$message.error(message)
        })
        .finally(() => (this.loading = false))
    },
    newApproval() {
      this.$router.push({
        name: 'approvals.new',
        params: { moduleName: this.moduleName },
      })
    },
    edit(approval) {
      this.$router.push({
        name: 'approvals.edit',
        params: {
          id: approval.id,
          moduleName: this.moduleName,
        },
      })
    },
    remove(approval) {
      return this.$http
        .post(`/v2/modules/rules/delete`, {
          ids: [approval.id],
        })
        .then(({ data }) => {
          if (data.responseCode === 0) {
            let { approvals } = this
            remove(approvals, ['id', approval.id])
            this.$set(this, 'approvals', approvals)
            this.$message.success(this.$t('common.products.approval_deleted'))
            this.$forceUpdate()
          } else {
            throw new Error()
          }
        })
        .catch(() => {
          this.$message.error(
            this.$t('common._common.could_not_delete_approval')
          )
        })
    },
    reorder() {
      return this.$http
        .post('/v2/approval/reorder', {
          moduleName: this.moduleName,
          ids: this.approvals.map(policy => policy.id),
        })
        .then(response => {
          if (response.data.responseCode !== 0) {
            this.$message.error(response.data.message || 'Error Occurred')
          } else {
            this.$message.success(this.$t('common._common.reorder_successfull'))
          }
        })
        .catch(response => {
          let message = this.$getProperty(response, 'data.message', null)
          this.$message.error(message || 'Error Occurred')
        })
    },
    changeStatus(policy) {
      let { id, active: isActive } = policy
      let url = isActive ? 'setup/turnonrule' : 'setup/turnoffrule'

      return this.$http
        .post(url, { workflowId: id })
        .then(({ data }) => {
          if (data.result === 'success') {
            this.$message.success(
              isActive
                ? this.$t('common.products.approval_marked_as_active')
                : this.$t('common.products.approval_marked_as_inactive')
            )
          } else {
            throw new Error()
          }
        })
        .catch(() => {
          this.$set(policy, 'active', !isActive)
          this.$message.error(this.$t('common._common.could_not_change_status'))
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
