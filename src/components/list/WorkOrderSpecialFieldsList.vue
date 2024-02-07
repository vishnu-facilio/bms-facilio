<template>
  <div>
    <div
      v-if="field.name === 'resource'"
      class="q-item-division relative-position"
      style="min-width: 170px;"
    >
      <div
        v-if="
          !$validation.isEmpty(moduleData.resource) &&
            !$validation.isEmpty(moduleData.resource.id)
        "
      >
        <div class="flLeft mR7">
          <img
            v-if="moduleData.resource.resourceType === 1"
            src="~statics/space/space-resource.svg"
            style="height:12px; width:14px;"
          />
          <img
            v-else
            src="~statics/space/asset-resource.svg"
            style="height:11px; width:14px;"
          />
        </div>
        <span
          class="flLeft q-item-label ellipsis max-width140px"
          v-tippy
          small
          data-position="bottom"
          :title="moduleData.resource.name"
          >{{ moduleData.resource.name }}</span
        >
      </div>
      <div v-else>
        <span class="q-item-label secondary-color color-d"
          >--- {{ $t('maintenance.wr_list.space_asset') }} ---</span
        >
      </div>
    </div>
    <div
      v-else-if="field.name === 'assignedTo'"
      class="q-item-division relative-position wo_assignedto_avatarblock"
    >
      <div class="wo-assigned-avatar fL">
        <user-avatar
          size="md"
          :user="moduleData.assignedTo"
          :group="moduleData.assignmentGroup"
          :showLabel="true"
          moduleName="lookupModuleName"
        ></user-avatar>
      </div>
    </div>
    <div v-else-if="field.name === 'noOfNotes'">
      <span>
        <img src="~assets/comment.svg" style="width: 14px;height: 12.8px;" />
      </span>
      <span class="width5px q-item-sublabel comment-counnt-txt">{{
        notesLabel
      }}</span>
    </div>
    <div v-else-if="field.name === 'noOfTasks'">
      <inline-svg
        v-if="canShowTaskIcon"
        src="svgs/task-list"
        class="vertical-middle cursor-pointer"
        iconClass="icon icon-sm mT5 mR5 task-icon"
      ></inline-svg>
      <span class="q-item-sublabel pL5">{{ taskLabel }}</span>
    </div>
    <div style="float:left;" v-else-if="field.name === 'noOfAttachments'">
      <span>
        <img
          src="~assets/attachement-grey.svg"
          style="width: 14px;height: 12.8px;"
        />
      </span>
      <span class="width5px q-item-sublabel comment-counnt-txt">{{
        attachmentLabel
      }}</span>
    </div>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
import { isEmpty } from '@facilio/utils/validation'

export default {
  components: { UserAvatar },
  props: ['field', 'moduleData'],
  computed: {
    canShowTaskIcon() {
      let { moduleData } = this
      return !isEmpty(moduleData.noOfTasks) && moduleData.noOfNotes !== 0
    },
    taskLabel() {
      let { moduleData } = this
      let { noOfTasks, noOfNotes, noOfClosedTasks } = moduleData
      let taskLabel = '---'
      if (isEmpty(noOfTasks) || noOfNotes === 0) {
        return taskLabel
      } else {
        if (isEmpty(noOfClosedTasks)) {
          return '0'
        } else {
          return `${noOfClosedTasks}/${noOfTasks}`
        }
      }
    },
    notesLabel() {
      let { moduleData } = this
      let { noOfNotes } = moduleData
      return isEmpty(noOfNotes) || noOfNotes === 0 ? '0' : moduleData.noOfNotes
    },
    attachmentLabel() {
      let { moduleData } = this
      let { noOfAttachments } = moduleData || {}
      return isEmpty(noOfAttachments) || noOfAttachments === 0
        ? '0'
        : noOfAttachments
    },
  },
}
</script>
<style lang="scss"></style>
