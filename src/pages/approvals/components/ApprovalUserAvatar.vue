<template>
  <div class="inline">
    <div class="fc-avatar-element q-item-division relative-position">
      <div class="q-item-side-left q-item-section">
        <avatarPopover v-if="hasUser" :user="userObj" :moduleName="moduleName">
          <span slot="reference">
            <avatar :size="size" :user="userObj" :color="color"> </avatar>
          </span>
        </avatarPopover>

        <avatarPopover
          v-else-if="fieldType"
          :user="fieldTypeUserValue"
          :moduleName="moduleName"
        >
          <span slot="reference">
            <avatar :size="size" :user="fieldTypeUserValue" :color="color">
            </avatar>
          </span>
        </avatarPopover>

        <div
          v-else
          :class="['icon-border', userTypeIcon[userType.type].background]"
        >
          <inline-svg
            :src="userTypeIcon[userType.type].icon"
            :iconClass="
              `icon text-center fc-fill-path fill-path-white ${
                userTypeIcon[userType.type].iconSize
              }`
            "
            :class="['mT2', userTypeIcon[userType.type].marginLeft]"
          ></inline-svg>
        </div>
      </div>

      <div class="flex-middle">
        <span
          v-if="$validation.isObject(typeLabel)"
          class="q-item-label d-flex flex-wrap"
        >
          {{ typeLabel.value }}&nbsp;
          <span v-if="typeLabel.type" class="text-fc-grey mL5">
            ({{ typeLabel.type }})
          </span>
        </span>

        <span v-else class="q-item-label d-flex flex-wrap user-type">
          {{ typeLabel }}
        </span>

        <el-popover
          v-if="approvers.length > 1"
          placement="bottom"
          width="200"
          trigger="hover"
        >
          <slot :slot="'reference'">
            <div class="approvers-more-icon">+{{ approvers.length - 1 }}</div>
          </slot>
          <div>
            <div
              v-for="approver in approvers.slice(1)"
              :key="approver.name"
              class="mB10"
            >
              <ApprovalUserAvatar
                size="md"
                :user="approver"
                :userType="getApproverType(approver)"
                :moduleName="moduleName"
                :approvalMeta="approvalMeta"
              ></ApprovalUserAvatar>
            </div>
          </div>
        </el-popover>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty, isArray } from '@facilio/utils/validation'
import UserAvatar from '@/avatar/User'

export default {
  name: 'ApprovalUserAvatar',
  extends: UserAvatar,
  props: ['userType', 'approvalMeta', 'approverObj'],

  computed: {
    hasUser() {
      let id = this.$getProperty(this.userObj, 'id')
      return !isEmpty(id)
    },

    fieldType() {
      let { type, value } = this.userType
      return type === 'FIELD' && !isEmpty(value)
    },

    fieldTypeUserValue() {
      let { fieldType, userType } = this
      let { value } = userType

      if (fieldType) {
        if (isArray(value) && !isEmpty(value)) {
          return value[0]
        } else {
          return value
        }
      }
      return null
    },

    fieldName() {
      let { approverObj, approvalMeta } = this
      let { approvalList = [] } = approvalMeta || {}

      if (!isEmpty(approvalList)) {
        let { displayName } = approvalList.find(
          list => list.approverGroup === approverObj.approverGroup
        ).field

        return displayName
      }
      return null
    },

    color() {
      if (this.userObj.id !== -1 || this.fieldType) {
        return ''
      }
      return this.group ? '#c3c3c3' : '#e3e3e3'
    },

    userTypeIcon() {
      let userIcon = {
        TENANT: {
          icon: 'svgs/tenant',
          background: 'green',
          iconSize: 'icon-lg',
          marginLeft: 'mL4',
        },
        VENDOR: {
          icon: 'svgs/apps/vendor',
          background: 'orange',
          iconSize: 'icon-lg',
          marginLeft: 'mL4',
        },
        ROLE: {
          icon: 'svgs/role',
          background: 'yellow',
          iconSize: 'icon-xl',
          marginLeft: 'mL3',
        },
        GROUP: {
          icon: 'svgs/team',
          background: 'blue',
          iconSize: 'icon-xl',
          marginLeft: 'mL3',
        },
        FIELD: {
          icon: 'user',
          background: 'brown',
          iconSize: 'icon-lg',
          marginLeft: 'mL4',
        },
      }

      return userIcon
    },

    typeLabel() {
      let { type, value } = this.userType

      if (type === 'USER') {
        return { value: this.userObj.name, type: null }
      } else if (type === 'TENANT') {
        return value ? { value: value.name, type: 'Tenant' } : 'Tenant'
      } else if (type === 'VENDOR') {
        return value ? { value: value.name, type: 'Vendor' } : 'Vendor'
      } else if (type === 'FIELD') {
        let { name } = this.fieldTypeUserValue || {}
        return { value: name || this.fieldName, type: 'Field' }
      } else {
        return value.name
      }
    },
    approvers() {
      let { type, value } = this.userType
      if (type === 'USER') return [this.userObj]
      else return isArray(value) ? value : [value]
    },
  },

  methods: {
    getApproverType(approver) {
      return approver.id ? { type: 'USER', value: approver } : approver
    },
  },
}
</script>
<style scoped>
.icon-border {
  border-radius: 50%;
  border: 1px solid;
  height: 30px;
  width: 30px;
}
.green {
  background-color: #34bfa3;
  color: #34bfa3;
}
.blue {
  background-color: #6db1f4;
  color: #6db1f4;
}
.yellow {
  background-color: #ffba51;
  color: #ffba51;
}
.brown {
  background-color: #ac4352;
  color: #ac4352;
}
.orange {
  background-color: #ff8216;
  color: #ff8216;
}
.mL4 {
  margin-left: 4px;
}
.fc-avatar-element .q-item-label.user-type {
  margin-left: 12px;
}
.approvers-more-icon {
  background-color: #ecf2f7;
  border-radius: 50%;
  padding: 6px 8px 6px 4px;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
  display: inline;
  margin-left: 5px;
}
</style>
