<template>
  <div>
    <div id="resend-header" class="section-header">
      {{ $t('common.header.resend_permissions') }}
    </div>
    <div class="pL50 pR70 fc-input-label-txt" style="margin-top: -10px;">
      {{ $t('common._common.specify_users_groups_roles_resend_record') }}
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
    </div>
  </div>
</template>
<script>
import Approvers from './Approvers'
import cloneDeep from 'lodash/cloneDeep'
import { isEmpty } from '@facilio/utils/validation'
import {
  serializeApprover,
  deserializeApprover,
} from 'newapp/utils/approverUtils'

export default {
  name: 'Resend',
  extends: Approvers,
  methods: {
    init() {
      if (this.isNew) return

      let { resendApprovers } = cloneDeep(this.policy)

      if (isEmpty(resendApprovers)) {
        this.activeApprovers = [
          {
            approversId: null,
            type: null,
          },
        ]
      } else {
        this.activeApprovers = resendApprovers.map(approver =>
          deserializeApprover(approver)
        )
      }

      this.$nextTick(() => (this.canWatchForChanges = true))
    },
    serialize() {
      let approvers = this.activeApprovers
        .map(approver =>
          serializeApprover(approver, { fields: this.moduleFields })
        )
        .filter(approver => !isEmpty(approver))

      if (isEmpty(approvers)) approvers = []

      if (isEmpty(approvers)) approvers = []

      return {
        resendApprovers: approvers,
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
