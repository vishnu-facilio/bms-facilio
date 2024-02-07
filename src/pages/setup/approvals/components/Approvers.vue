<template>
  <div>
    <div id="approvers-header" class="section-header">
      {{ $t('common.header.approvers') }}
    </div>
    <div class="pL50 pR70 fc-input-label-txt" style="margin-top: -10px;">
      {{ $t('common._common.specify_users_groups_and_roles') }}
    </div>
    <div class="p50 pT20 pR70 pB30 el-form" style="min-height: 230px;">
      <el-row
        v-for="(approver, key) in activeApprovers"
        class="criteria-condition-block"
        :key="key"
      >
        <div class="criteria-alphabet-block">
          <div class="alphabet-circle">
            {{ key + 1 }}
          </div>
        </div>
        <el-col :span="6">
          <el-select
            v-model="approver.type"
            :placeholder="$t('common.products.select_type')"
            class="fc-input-full-border-select2 width100"
            @change="resetApproverId(key)"
            filterable
          >
            <el-option
              v-for="type in approverTypes"
              :key="`type-${type.id}`"
              :label="type.name"
              :value="type.id"
            ></el-option>
          </el-select>
        </el-col>
        <el-col :span="15" class="pL20">
          <el-select
            v-model="approver.approverId"
            :placeholder="$t('common._common.select')"
            class="fc-input-full-border-select2 width100"
            filterable
          >
            <el-option
              v-for="(option, index) in typeOptionMap[approver.type]"
              :key="`approveroption-${index}`"
              :label="option.displayName || option.name"
              :value="option.id"
            ></el-option>
          </el-select>
        </el-col>
        <el-col :span="3" class="mT5 mL5 border-left action">
          <img
            src="~assets/add-icon.svg"
            v-if="key + 1 === activeApprovers.length"
            style="height:18px;width:18px;"
            class="delete-icon"
            @click="addApprover"
          />
          <img
            src="~assets/remove-icon.svg"
            style="height:18px;width:18px;margin-right: 3px;"
            class="delete-icon mL5"
            @click="deleteApprovers(key)"
          />
        </el-col>
      </el-row>
      <el-row
        v-if="activeApprovers && activeApprovers.length > 1"
        class="mT20 mB20"
      >
        <el-col :span="24">
          <p class="text-fc-grey inline f14 pR30 mT10">
            {{ $t('common._common.to_be_performed') }}
          </p>
          <el-radio-group v-model="allApprovalRequired">
            <el-radio
              v-for="type in requiredApprovers"
              :key="type.value"
              :label="type.value"
              class="fc-radio-btn"
              >{{ type.label }}</el-radio
            >
          </el-radio-group>
          <el-select
            v-if="activeApprovers.length > 1 && allApprovalRequired"
            v-model="approvalOrder"
            class="fc-input-full-border-select2 width320px mL30 vertical-middle"
          >
            <el-option
              v-for="(field, index) in approvalOrders"
              :key="`approvalType-${index}`"
              :label="field.label"
              :value="field.value"
            ></el-option>
          </el-select>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import cloneDeep from 'lodash/cloneDeep'
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import {
  getApproverOptions,
  approverTypes,
  serializeApprover,
  deserializeApprover,
} from 'newapp/utils/approverUtils'

const requiredApprovers = [
  { label: 'Any one', value: false },
  { label: 'All', value: true },
]

const approvalOrders = [
  { label: 'Sequential', value: 1 },
  { label: 'Parallel', value: 2 },
]

export default {
  name: 'Approvers',
  props: {
    policy: {
      type: Object,
    },
    moduleFields: {
      type: Array,
    },
  },
  data() {
    return {
      approverTypes,
      requiredApprovers,
      approvalOrders,
      activeApprovers: [
        {
          approverId: null,
          type: null,
        },
      ],
      allApprovalRequired: false,
      approvalOrder: 1,
      typeOptionMap: [],
      canWatchForChanges: false,
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
      teams: state => state.groups,
      roles: state => state.roles,
    }),
    typeOptions() {
      return {
        users: [
          ...this.users,
          {
            name: 'Requester',
            id: 'requester',
          },
        ],
        roles: this.roles,
        teams: this.teams,
        fields: this.moduleFields,
      }
    },
    isNew() {
      return isEmpty(this.$route.params.id)
    },
    moduleName() {
      return this.$route.params.moduleName
    },
  },
  watch: {
    policy: {
      handler: function() {
        Promise.all([
          this.$store.dispatch('loadUsers'),
          this.$store.dispatch('loadRoles'),
          this.$store.dispatch('loadGroups'),
        ]).then(this.init)
      },
      immediate: true,
    },
    activeApprovers: {
      handler(value) {
        if (this.canWatchForChanges) this.$emit('modified')
        if (isEmpty(value)) {
          this.activeApprovers = [
            {
              approverId: null,
              type: null,
            },
          ]
        }
      },
      deep: true,
    },
    allApprovalRequired(value) {
      if (value) {
        this.approvalOrder = !isEmpty(this.approvalOrder)
          ? this.approvalOrder
          : 1
      } else {
        this.approvalOrder = -1
      }
    },
    typeOptions: {
      handler: async function(typeOptions) {
        this.typeOptionMap = await getApproverOptions(typeOptions)
      },
      immediate: true,
    },
  },
  methods: {
    init() {
      if (this.isNew) return

      let { approvers, allApprovalRequired, approvalOrder } = cloneDeep(
        this.policy
      )

      if (isEmpty(approvers)) {
        this.activeApprovers = [
          {
            approverId: null,
            type: null,
          },
        ]
      } else {
        this.activeApprovers = approvers.map(approver =>
          deserializeApprover(approver)
        )
      }

      this.allApprovalRequired = allApprovalRequired
      this.approvalOrder = approvalOrder ? approvalOrder : 1

      this.$nextTick(() => (this.canWatchForChanges = true))
    },

    isEmptyApprover(approver) {
      return isEmpty(approver.type) || isEmpty(approver.approverId)
    },

    deleteApprovers(index) {
      let approver = this.activeApprovers[index]

      if (
        this.activeApprovers.length === 1 &&
        !this.isEmptyApprover(approver)
      ) {
        this.addApprover()
      }

      this.activeApprovers.splice(index, 1)
    },

    addApprover() {
      this.activeApprovers.push({ type: null, approverId: null })
    },

    resetApproverId(index) {
      let approver = this.activeApprovers[index]
      this.$set(this.activeApprovers, index, { ...approver, approverId: null })
    },

    serialize() {
      let approvers = this.activeApprovers
        .map(approver =>
          serializeApprover(approver, { fields: this.moduleFields })
        )
        .filter(approver => !isEmpty(approver))

      if (isEmpty(approvers)) approvers = []

      return {
        approvers,
        allApprovalRequired:
          approvers.length < 1 ? false : this.allApprovalRequired,
        approvalOrder:
          approvers.length < 1
            ? -1
            : this.allApprovalRequired
            ? this.approvalOrder
            : -1,
      }
    },
  },
}
</script>
<style scoped>
.criteria-condition-block .action {
  visibility: hidden;
}
.criteria-condition-block:hover .action {
  visibility: visible;
}
</style>
