<template>
  <div class="task-container mB20 wo-task-page">
    <div v-if="isTasksEmpty" class="text-center">
      <inline-svg
        src="svgs/emptystate/task"
        iconClass="icon text-center icon-xxxlg"
      ></inline-svg>
      <div class="nowo-label mB10">
        <!-- {{ $t('maintenance._workorder.task_body_text') }} -->
      </div>
      <el-button
        :data-test-selector="`${$t('maintenance._workorder.add_task')}`"
        @click="addNewTaskSection()"
        class="task-add-btn empty mT5"
      >
        <img src="~assets/add-blue.svg" />
        <span class="btn-label mL5">
          {{ $t('maintenance._workorder.add_task') }}
        </span>
      </el-button>
    </div>
    <draggable
      v-else
      v-model="tasks"
      v-bind="taskSectionDragOptions"
      handle=".task-handle"
      :disabled="disableDrag"
    >
      <div
        v-for="(section, sectionIndex) in tasks"
        :key="sectionIndex"
        class="task-section"
      >
        <div class="task-section-header d-flex mB10">
          <div class="task-handle">
            <img src="~assets/drag-grey.svg" />
          </div>
          <el-input
            data-test-selector="Section_Title"
            class="task-input fc-input-border-remove"
            v-model="section.section"
          ></el-input>
          <i
            v-if="!hasError"
            class="el-icon-delete pointer trash-icon"
            @click="deleteSection(sectionIndex)"
          ></i>
        </div>
        <draggable
          v-model="section.tasks"
          v-bind="taskDragOptions"
          :disabled="disableDrag"
          draggable=".task-item"
        >
          <div
            v-for="(task, taskIndex) in section.tasks"
            :key="taskIndex"
            class="task-item d-flex"
          >
            <el-input
              data-test-selector="Section_ID"
              type="number"
              class="fc-input-full-border2 wo-task-list-num"
              v-model="task.uniqueId"
            ></el-input>
            <el-input
              data-test-selector="Section_Subject"
              class="mL20 fc-input-border-remove mT7 task-name-input"
              :ref="`${section.section} - ${sectionIndex} ~ ${taskIndex}`"
              v-model="task.subject"
              @blur="onInputBlur(...arguments)"
              @keyup.native.enter="addNewTask(sectionIndex, section.section)"
              @paste.native="
                handlePaste(sectionIndex, section, task, ...arguments)
              "
              type="text"
              placeholder="Enter a task"
            ></el-input>
            <el-input
              data-test-selector="Section_Resource"
              placeholder="Space/Asset"
              v-model="(task.resource || {}).name"
              type="text"
              class="fc-input-border-remove task-spaceasset-chooser"
              :disabled="true"
            >
              <img
                slot="prefix"
                src="~assets/spacepin.svg"
                class="space-icon pointer"
                @click="showLookupWizard(task, true)"
              />
            </el-input>
            <i
              v-if="!$validation.isEmpty((task.resource || {}).name)"
              class="el-icon-circle-close pointer clear-icon mR10"
              @click="resetTaskResourceField(task)"
            ></i>
            <div
              @click="setSelectedTaskIndices(sectionIndex, taskIndex, task)"
              class="task-settings pointer mR10"
            >
              <img src="~assets/settings-grey.svg" />
            </div>
            <i
              class="el-icon-delete pointer trash-icon mR10"
              @click="deleteTask(section.tasks, taskIndex)"
            ></i>
          </div>
          <el-button
            :data-test-selector="`${$t('maintenance._workorder.add_task')}`"
            @click="addNewTask(sectionIndex, section.section)"
            class="task-add-btn mT5 mB15"
          >
            <img src="~assets/add-blue.svg" />
            <span class="btn-label mL5">
              {{ $t('maintenance._workorder.add_task') }}
            </span>
          </el-button>
          <div class="d-flex justify-content-space">
            <div class="dashed-line"></div>
            <el-button
              :data-test-selector="
                `${$t('maintenance._workorder.add_section')}`
              "
              @click="addNewTaskSection(sectionIndex + 1)"
              class="task-add-btn"
            >
              <img src="~assets/add-blue.svg" />
              <span class="btn-label mL5">
                {{ $t('maintenance._workorder.add_section') }}
              </span>
            </el-button>
            <div class="dashed-line"></div>
          </div>
        </draggable>
      </div>
    </draggable>
    <TaskDetails
      v-if="canShowTaskDetails"
      :canShowTaskDetails.sync="canShowTaskDetails"
      :selectedTask="selectedTask"
      @updateTaskDetails="updateTaskDetails"
    ></TaskDetails>
    <template v-if="canShowLookupWizard">
      <FLookupFieldWizard
        :canShowLookupWizard.sync="canShowLookupWizard"
        :selectedLookupField="activeResourceTask.selectedLookupField"
        :siteId="(filter || {}).site"
        @setLookupFieldValue="setLookupFieldValue"
      ></FLookupFieldWizard>
    </template>
  </div>
</template>

<script>
import draggable from 'vuedraggable'
import { isEmpty } from '@facilio/utils/validation'
import Constants from 'util/constant'
import { deepCloneObject } from 'util/utility-methods'
import WebFormMixin from '@/mixins/forms/WebFormMixin'
import TaskDetails from '@/TaskDetails'
import FLookupFieldWizard from '@/FLookupFieldWizard'

export default {
  mixins: [WebFormMixin],
  components: {
    draggable,
    TaskDetails,
    FLookupFieldWizard,
  },
  props: {
    tasksList: {
      type: Array,
      required: true,
    },
    spaceAssetResourceObj: {
      type: Object,
    },
    hasError: {
      type: Boolean,
      required: true,
    },
    filter: {
      type: Object,
    },
    isFromBuilder: {
      type: Boolean,
    },
  },
  data() {
    return {
      canShowTaskDetails: false,
      selectedTask: {},
      selectedTaskIndices: {
        row: '',
        column: '',
      },
      disableDrag: false,
      canShowLookupWizard: false,
      activeResourceTask: {},
    }
  },
  computed: {
    tasks: {
      get() {
        return this.tasksList
      },
      set(value) {
        this.$emit('update:tasksList', value)
      },
    },
    taskSectionDragOptions() {
      return {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'tasksection',
        sort: true,
        class: 'draggable-section',
      }
    },
    taskDragOptions() {
      return {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'tasks',
        sort: true,
      }
    },
    isTasksEmpty() {
      let { tasks } = this
      return isEmpty(tasks)
    },
  },
  methods: {
    getMaxTaskNumber() {
      let max
      if (this.tasksList.length && this.tasksList[0].tasks.length) {
        max = this.tasksList[0].tasks[0].uniqueId
      } else {
        return 0
      }
      this.tasksList.forEach(obj => {
        obj.tasks.forEach(t => {
          if (t.uniqueId > max) {
            max = t.uniqueId
          }
        })
      })
      return max
    },
    addNewTask(sectionIndex, sectionName, subject) {
      let { tasks, canAddNewTaskOrSection } = this
      let canAddTask = canAddNewTaskOrSection()
      if (canAddTask || subject) {
        let task = this.createNewTaskInstance(subject)
        task.uniqueId = this.getMaxTaskNumber() + 1
        let _tasks = (tasks[sectionIndex] || {}).tasks || []
        _tasks.push(task)
        this.$nextTick(() => {
          this.setFocus(sectionName, sectionIndex, _tasks.length - 1)
        })
      } else {
        this.$message.error('Please specify the task subject')
      }
    },
    addNewTaskSection(sectionIndex = 0) {
      let { tasks, canAddNewTaskOrSection } = this
      let canAddSection = canAddNewTaskOrSection()
      if (canAddSection) {
        let taskLength = tasks.length
        let sectionName =
          taskLength === 0
            ? 'Untitled Section'
            : `Untitled Section ${taskLength}`
        let task = this.createNewTaskInstance()
        task.uniqueId = this.getMaxTaskNumber() + 1
        tasks.splice(sectionIndex, 0, {
          section: sectionName,
          tasks: [task],
        })
        this.$nextTick(() => {
          this.setFocus(sectionName, sectionIndex, 0)
        })
      } else {
        this.$message.error('Please specify the task subject')
      }
    },
    handlePaste(sectionIndex, section, task, event) {
      let { section: sectionName } = section
      let tasksPlainText = event.clipboardData.getData('text/plain')
      if (!isEmpty(tasksPlainText)) {
        let tasksArr = tasksPlainText.split(/[\n\r]/g)
        let taskSubject = tasksArr.shift()
        task.subject = taskSubject
        if (!isEmpty(tasksArr)) {
          tasksArr.forEach(task => {
            this.addNewTask(sectionIndex, sectionName, task)
          })
        }
      }
      event.preventDefault()
    },
    deleteSection(sectionIndex) {
      let { tasks } = this
      tasks.splice(sectionIndex, 1)
    },
    deleteTask(task = [], taskIndex) {
      task.splice(taskIndex, 1)
      this.$emit('update:hasError', false)
    },
    setFocus(sectionName, sectionIndex, taskIndex) {
      // eg: Untitled Section - 0 ~ 0
      let inputRef = this.$refs[
        `${sectionName} - ${sectionIndex} ~ ${taskIndex}`
      ]
      if (!isEmpty(inputRef)) {
        inputRef[0].focus()
      }
    },
    onInputBlur(event) {
      let {
        target: { value },
      } = event
      if (isEmpty(value)) {
        this.$message.error('Please specify the task subject')
        this.$emit('update:hasError', true)
      } else {
        this.$emit('update:hasError', false)
      }
    },
    createNewTaskInstance(subject = '') {
      let task = deepCloneObject(Constants.TASK_DEFAULT_FIELDS)
      let { spaceAssetResourceObj = {} } = this
      let { id, name, config } = spaceAssetResourceObj
      task.selectedLookupField = {
        ...Constants.RESOURCE_DEFAULT_OBJ,
        config,
        value: {
          id,
        },
      }
      if (!isEmpty(spaceAssetResourceObj)) {
        task.resource = {
          id,
          name,
        }
        task.selectedLookupField.selectedItems = [
          {
            label: name,
            value: id,
          },
        ]
      }

      task.subject = subject
      return task
    },
    setSelectedTaskIndices(row, column, task) {
      this.selectedTaskIndices = {
        row,
        column,
      }
      this.showHideTaskDetails(task, true)
    },
    updateTaskDetails(task) {
      let {
        selectedTaskIndices: { row, column },
      } = this
      this.tasks[row].tasks[column] = task
      this.showHideTaskDetails({}, false)
    },
    canAddNewTaskOrSection() {
      let { tasks = [] } = this
      let canAdd = true
      tasks.forEach(task => {
        let { tasks } = task
        if (canAdd) {
          canAdd = !tasks.some(task => isEmpty(task.subject))
        }
      })
      return canAdd
    },
    showHideTaskDetails(task = {}, canShow) {
      this.$set(this, 'selectedTask', task)
      this.canShowTaskDetails = canShow
    },
    showLookupWizard(task, canShow) {
      this.$set(this, 'activeResourceTask', task)
      this.$set(this, 'canShowLookupWizard', canShow)
    },
    setLookupFieldValue(props) {
      let { field } = props
      let { selectedItems } = field || {}
      let [selectedItem] = selectedItems
      let { value, label } = selectedItem
      let { activeResourceTask } = this
      if (!isEmpty(activeResourceTask)) {
        activeResourceTask.resource = { name: label, id: value }
        activeResourceTask.readingFieldId = null
        activeResourceTask.taskReadings = []
      }
    },
  },
}
</script>

<style lang="scss">
.wo-task-page {
  .task-empty-icon {
    width: 49px;
    height: 49px;
    vertical-align: middle;
  }
  .task-section {
    margin-bottom: 20px;
  }
  .task-container .task-add-btn {
    padding: 10px 20px;
    border: 1px solid transparent;
    min-height: 36px;
  }
  .task-add-btn .btn-label {
    font-size: 12px;
    font-weight: 500;
    color: #39b2c2;
    letter-spacing: 0.5px;
  }
  .task-container .task-add-btn:hover,
  .task-add-btn.empty {
    background-color: #f7feff;
    border: 1px solid #39b2c2;
  }
  .task-add-btn img {
    width: 9px;
  }
  .task-spaceasset-chooser {
    width: 20%;
    font-size: 13px;
    color: #324056;
  }
  .job-plan .task-spaceasset-chooser {
    width: 30%;
  }
  .task-spaceasset-chooser >>> .el-input__inner {
    padding: 0 0 0 25px;
  }
  .trash-icon,
  .task-settings,
  .clear-icon {
    display: none;
  }
  .space-icon {
    width: 14px;
    height: 14px;
    margin: 6px 5px 0 -26px;
  }
  .task-settings img {
    width: 14px;
    margin-top: 3px;
  }
  .job-plan .task-name-input {
    width: 50%;
  }
  .task-name-input {
    width: 60%;
  }
  .trash-icon {
    color: #de7272;
  }
  .task-section-header:hover .trash-icon,
  .task-item:hover .trash-icon,
  .task-item:hover .clear-icon,
  .task-item:hover .task-settings {
    display: block;
  }
  .task-item {
    padding: 10px 0px;
    border: 1px solid #ecf3fa;
    border-top: 0px;
    position: relative;
    align-items: center;
  }
  .task-item div:first-of-type {
    margin-top: 6px;
  }
  .task-item:first-child {
    border-top: 1px solid #ecf3fa;
  }
  .task-item {
    .wo-task-list-num {
      .el-input__inner {
        padding: unset !important;
      }
    }
  }
  .task-item {
    &:hover {
      background-color: #ebfafd;
      cursor: move;
      cursor: -webkit-grabbing;
      .wo-task-list-num {
        .el-input__inner {
          &:hover {
            border: 1px solid #d8dce5 !important;
          }
        }
      }
    }
  }
  .task-handle {
    margin: 5px 10px 0 0px;
  }
  .dashed-line {
    width: 100%;
    margin: 15px 0px;
    border-bottom: 1px dashed #dbeff3;
  }
  .task-section-header .task-input.el-input >>> .el-input__inner {
    font-size: 14px;
    font-weight: 500;
    letter-spacing: 0.5px;
    color: #333333;
    border-bottom: none;
    min-height: 29px;
  }
  .task-item .fc-input-border-remove.el-input >>> .el-input__inner {
    height: 30px;
  }
  .task-section.sortable-chosen {
    background: #ebfafd;
    padding: 10px;
  }
  .task-section.sortable-chosen .task-input.el-input >>> .el-input__inner {
    background: #ebfafd;
  }

  .task-spaceasset-chooser {
    &.el-input {
      &.is-disabled {
        .el-input__inner {
          cursor: auto !important;
          background: transparent;
          color: #606266;
        }
      }
    }
    .el-input__inner {
      &::placeholder {
        font-size: 12px;
        color: #8ca1ad;
      }
    }
  }
}
</style>
