<template>
  <div>
    <el-dialog
      :visible="true"
      width="50%"
      title="User"
      class="fieldchange-Dialog pB15 fc-dialog-center-container"
      custom-class="dialog-header-padding"
      :append-to-body="true"
      :before-close="close"
    >
      <div class="height330 overflow-y-scroll pB50">
        <el-row v-if="activeApprovers && activeApprovers.length > 1">
          <el-col :span="24">
            <el-form-item prop="name" class="mL10">
              <p class="grey-text2">To be perfomed by</p>
              <el-radio-group v-model="allApprovalRequired" class="mT10">
                <el-radio
                  v-for="requiredApproverType in requiredApprovers"
                  :key="`radio-${requiredApproverType.value}`"
                  :label="requiredApproverType.value"
                  class="fc-radio-btn"
                >
                  {{ requiredApproverType.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row
          v-for="(approver, key) in activeApprovers"
          class="criteria-condition-block"
          :key="key"
        >
          <el-col :span="6">
            <el-select
              v-model="approver.type"
              placeholder="Select"
              class="fc-input-full-border-select2 width100"
              @change="resetApproverId(key)"
              filterable
            >
              <el-option
                v-for="(type, index) in approverTypes"
                :key="`type-${index}`"
                :label="type.name"
                :value="type.id"
              ></el-option>
            </el-select>
          </el-col>
          <el-col :span="15" class="pL20">
            <el-select
              v-model="approver.approverId"
              placeholder="Select"
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
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="close()" class="modal-btn-cancel">CANCEL</el-button>
        <el-button type="primary" class="modal-btn-save" @click="save()"
          >Save</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { getApproverOptions, approverTypes } from 'newapp/utils/approverUtils'
import { mapState } from 'vuex'

export default {
  props: ['transitionObj', 'moduleFields'],

  data() {
    return {
      approverTypes,
      requiredApprovers: [
        { label: 'Any one', value: false },
        { label: 'All', value: true },
      ],
      typeOptionMap: [],
      activeApprovers: [],
      allApprovalRequired: false,
    }
  },
  created() {
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')
    this.init()
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
  },
  watch: {
    typeOptions: {
      handler: async function(typeOptions) {
        this.typeOptionMap = await getApproverOptions(typeOptions)
      },
      immediate: true,
    },
  },
  methods: {
    init() {
      if (isEmpty(this.transitionObj.approvers)) {
        this.activeApprovers = []
        this.addApprover()
      } else {
        this.activeApprovers = this.transitionObj.approvers
        this.allApprovalRequired = !!this.transitionObj.allApprovalRequired
      }
    },
    addApprover() {
      this.activeApprovers.push({ type: null, approverId: null })
    },
    deleteApprovers(index) {
      let approver = this.activeApprovers[index]

      if (
        this.activeApprovers.length === 1 &&
        (!isEmpty(approver.approverId) || !isEmpty(approver.type))
      ) {
        this.addApprover()
      }
      this.activeApprovers.splice(index, 1)
    },
    resetApproverId(index) {
      let approver = this.activeApprovers[index]
      this.$set(this.activeApprovers, index, { ...approver, approverId: null })
    },
    close() {
      this.$emit('onClose')
    },
    save() {
      let approvers = [
        ...this.activeApprovers.filter(
          a => !isEmpty(a.type) && !isEmpty(a.approverId)
        ),
      ]
      if (!isEmpty(approvers)) {
        this.transitionObj.approvers = this.activeApprovers
        this.transitionObj.allApprovalRequired = this.allApprovalRequired
      } else {
        this.transitionObj.approvers = []
        this.transitionObj.allApprovalRequired = false
      }
      this.$emit('onSave')
      this.activeApprovers = []
      this.allApprovalRequired = false
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
