<template>
  <el-table
    :data="actionData"
    class="rules-action-form-table"
    :fit="true"
    style="width: 100%;"
  >
    <el-table-column prop="name" label="NAME">
      <template v-slot="action">
        <div class="label-txt-black">{{ action.row.name }}</div>
      </template>
    </el-table-column>
    <el-table-column prop="event" label="EVENT">
      <template v-slot="action">
        <div class="label-txt-black">
          {{ getEventType(action.row) }}
        </div>
      </template>
    </el-table-column>
    <el-table-column prop="actions" label="ACTION">
      <template v-slot="action">
        <div
          v-if="!$validation.isEmpty($getProperty(action, 'row.actions'))"
          class="rule-action-container"
        >
          <div
            class="rule-action-badge"
            v-for="(currAction, index) in action.row.actions"
            :key="`actions-${index}`"
          >
            {{ getActionType(currAction) }}
          </div>
        </div>
        <div v-else>
          ---
        </div>
      </template>
    </el-table-column>
    <el-table-column
      prop=""
      label=""
      min-width="20px"
      class="visibility-visible-actions"
    >
      <template v-slot="action">
        <div
          class="visibility-hide-actions"
          @click="deleteActionData(action.row)"
        >
          <inline-svg
            src="remove-icon"
            class="vertical-middle"
            iconClass="icon icon-md mR5"
          ></inline-svg>
        </div>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
import { isEmpty } from 'util/validation'
const ACTIVITY_TYPES = {
  1: 'Create',
  1024: 'Severity Change',
  1025: 'Create and Severity Change',
  2048: 'Clear',
  524288: 'On Date',
  1048576: 'Field Change',
  4: 'Delete',
}
const ACTION_TYPES = {
  3: 'SMS',
  4: 'Email',
  7: 'Mobile',
  17: 'Severity change',
}
export default {
  props: ['actionData', 'deleteAction'],
  methods: {
    getEventType(severity) {
      let activityType = this.$getProperty(severity, 'event.activityType')
      if (!isEmpty(activityType)) {
        let typeArr = ACTIVITY_TYPES[activityType]
        return typeArr
      } else {
        return '---'
      }
    },
    getActionType(action) {
      return ACTION_TYPES[action.actionType]
    },
    deleteActionData(action) {
      this.deleteAction(action)
    },
  },
}
</script>

<style lang="scss">
.rules-action-form-table {
  .rule-action-container {
    display: flex;
    flex-wrap: wrap;
  }
  .rule-action-badge {
    border-radius: 100px !important;
    background-color: #f8feff !important;
    border: solid 1px #39b1c1 !important;
    color: #39b1c1;
    display: flex;
    padding: 0px 10px;
    margin: 3px 5px 0px 0px;
  }
  .trash-icon {
    color: #de7272;
  }
  td {
    border-left: solid 1px #f2f5f6;
    border-right: solid 1px #f2f5f6;
  }
  th {
    border-left: solid 1px #f2f5f6;
    border-right: solid 1px #f2f5f6;
    border-top: solid 1px #f2f5f6;
  }
  td:last-child {
    border: none !important;
  }
  th:last-child {
    border: none !important;
  }
  tr:hover > td:last-child {
    background-color: #fff;
  }
}
</style>
