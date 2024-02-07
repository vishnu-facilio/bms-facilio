<template>
  <div>
    <template v-if="!hideTitleSection">
      <div class="widget-topbar mL0 mR0" style="justify-content: flex-start;">
        <div class="widget-title mL0">Approvers -</div>
        <span class="fw4 mL5 approval-rule-desc">{{
          formattedApprovalText
        }}</span>
        <div class="flex-end">
          <portal-target :name="widget.key + '-topbar'"></portal-target>
        </div>
      </div>
    </template>
    <table
      class="setting-list-view-table"
      ref="content-container"
      style="width: 100%"
    >
      <thead>
        <tr v-if="!$validation.isEmpty(list)">
          <th class="setting-table-th setting-th-text">
            Approver
          </th>
          <th
            class="setting-table-th setting-th-text text-center"
            style="width: 100px"
          >
            Status
          </th>
          <th class="setting-table-th setting-th-text text-center width350px">
            Modified Time
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(approver, index) in list" :key="index" class="nohover">
          <td>
            <div style="width:90%" class="text-ellipsis">
              <ApproverTypeAvatar
                size="md"
                :user="getApprover(approver)"
                :userType="getApproverType(approver)"
                :moduleName="moduleName"
                :approvalMeta="approvalMeta"
                :approverObj="approver"
              ></ApproverTypeAvatar>
            </div>
          </td>
          <td>
            <div v-if="getStatus(approver)" class="text-center">
              <inline-svg src="checked" iconClass="icon icon-md"></inline-svg>
            </div>
            <div v-else class="text-center fc-grey2 f13">Pending</div>
          </td>
          <td class="text-center">
            <div
              v-if="approver.actionTime"
              style="width:100%"
              class="text-ellipsis"
            >
              {{ approver.actionTime | formatDate }}
            </div>
            <div v-else class="fc-grey2 f13">--</div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
<script>
import ApproverTypeAvatar from 'pages/approvals/components/ApprovalUserAvatar'
import { mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: [
    'widget',
    'moduleName',
    'details',
    'layoutParams',
    'resizeWidget',
    'primaryFields',
    'approvalMeta',
    'hideTitleSection',
  ],
  components: { ApproverTypeAvatar },
  computed: {
    ...mapGetters(['getUser']),
    list() {
      let { approvalMeta = {} } = this
      let approvers = this.$getProperty(approvalMeta, 'approvalList', []) || []

      return approvers.filter(approver => {
        if (approver.type === 'FIELD' && isEmpty(approver.value)) return false
        else return true
      })
    },
    formattedApprovalText() {
      let { approvalRule } = this.approvalMeta || {}
      let { allApprovalRequired } = approvalRule || {}

      if (allApprovalRequired) return 'Everyone Should Approve'
      else return 'Anyone Can Approve'
    },
  },
  watch: {
    details() {
      this.$nextTick(this.autoResize)
    },
    list() {
      this.$nextTick(this.autoResize)
    },
  },
  methods: {
    getApprover(approver) {
      let { type, value, actionBy } = approver
      if (type === 'USER') return value
      else if (actionBy) return this.getUser(actionBy)
      else return null
    },
    getApproverType(approver) {
      let { type, value } = approver
      return { type, value }
    },
    getStatus(approver) {
      return approver.actionBy
    },
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['content-container']
        if (container) {
          let height = container.scrollHeight + 70
          let width = container.scrollWidth
          this.resizeWidget({ height, width })
        }
      })
    },
  },
}
</script>
<style lang="scss" scoped>
.approval-rule-desc {
  color: #385571;
}
.setting-list-view-table {
  tr {
    cursor: unset;
  }
  .setting-table-th {
    font-weight: 600;
  }
  tbody {
    tr:last-of-type {
      border-bottom: none;
    }
    td {
      padding: 12px 30px;
      height: 55px;
    }
  }
}
</style>
