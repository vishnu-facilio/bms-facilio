<template>
  <el-row class="mT20 flex items-center">
    <el-col :span="10">
      <p class="details-Heading">{{ name }}</p>
      <p class="small-description-txt2 width65 break-word">
        {{ description }}
      </p>
    </el-col>

    <el-col :span="10" class="flex items-center">
      <div
        v-if="isActionConfigure"
        class="pR10 configured-green f13 border-right2"
      >
        {{ $t('forms.rules.configured') }}
      </div>
      <el-button
        v-else
        type="button"
        @click="configureAction"
        class="small-border-btn"
        >{{ isActionConfigure ? 'Configured' : 'Configure' }}</el-button
      >

      <span v-if="isActionConfigure" class="mL10">
        <i
          class="el-icon-edit pointer"
          @click="configureAction"
          :title="$t('forms.rules.edit_actions')"
          v-tippy
        ></i>
        <span class="mL10 reset-txt pointer" @click="reset">{{
          $t('forms.rules.reset')
        }}</span>
      </span>
    </el-col>
  </el-row>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
let actionsHash = {
  SHOW_HIDE: [1, 2],
  ENABLE_DISABLE: [3, 4],
  SET_VALUE: [5],
  SET_FILTER: [6],
  EXECUTE_SCRIPT: [8],
  SHOW_HIDE_SECTION: [9, 10],
  SET_MANDATORY: [11, 12],
}
export default {
  name: 'ActionsConfiguredHelper',
  props: ['name', 'description', 'type', 'configureAction', 'reset', 'actions'],
  computed: {
    isActionConfigure() {
      let { actions, type, isActionEmpty } = this
      let currentActionIds = actionsHash[type] || {}
      let currentActions = []
      ;(currentActionIds || []).forEach(actionId => {
        currentActions.push(actions[actionId])
      })
      return currentActions.some(action => {
        return isActionEmpty(action)
      })
    },
  },
  methods: {
    isActionEmpty(action) {
      let { type } = this
      if (type === 'SET_VALUE') {
        let actionMeta = this.$getProperty(
          action,
          'formRuleActionFieldsContext.0.actionMeta'
        )
        return !isEmpty(actionMeta)
      } else if (type === 'SET_FILTER') {
        let picklistVal = this.$getProperty(
          action,
          'formRuleActionFieldsContext.0.values'
        )
        return !isEmpty(picklistVal)
      } else if (type === 'EXECUTE_SCRIPT') {
        let workflowString = this.$getProperty(
          action,
          'workflow.workflowV2String'
        )
        return !isEmpty(workflowString)
      } else {
        return !isEmpty(action.values)
      }
    },
  },
}
</script>
