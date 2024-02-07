<template>
  <Draggable
    :list="tasks"
    :options="taskSectionDragOptions"
    :disabled="disableDrag"
    handle=".section-handle"
  >
    <div
      class="task-section"
      v-for="(section, section_index) in tasks"
      :key="section.sectionId"
    >
      <div class="jp-section-header visibility-visible-actions">
        <div class="section-left">
          <div class="section-handle cursor-move mT15 mR10">
            <fc-icon
              group="default"
              name="drag"
              size="22"
              color="#cbc9c9"
            ></fc-icon>
          </div>
          <el-input
            data-test-selector="Section_Subject"
            class="mL2 fc-input-border-remove mT9 section-name"
            :ref="`${section.section} - ${section_index}`"
            v-model="section.name"
            @blur="onSectionInputBlur(...arguments)"
            type="text"
          >
          </el-input>
        </div>
        <div class="task-setup d-flex">
          <div class="section-customization">
            <div
              @click="openSectionSetting(section_index)"
              class="pT10 pointer pR10"
            >
              <fc-icon group="default" name="settings" size="18"></fc-icon>
            </div>
          </div>
          <div v-if="tasks.length === 1" class="space-filler"></div>
          <div
            v-else
            @click="deleteSection(section_index)"
            class="pT10 space-filler pointer"
          >
            <fc-icon group="default" name="trash-can" size="18"></fc-icon>
          </div>
        </div>
      </div>
      <div class="task-list-container">
        <Draggable
          v-model="section.tasks"
          v-bind="taskDragOptions"
          handle=".task-handle"
          @change="handleTaskDrag(section_index)"
          :disabled="disableDrag"
        >
          <div v-for="(task, taskIndex) in section.tasks" :key="taskIndex">
            <div class="task-row visibility-visible-actions">
              <div class="task-left">
                <div class="task-prefix d-flex">
                  <div class="task-handle cursor-move mT10 mR10">
                    <fc-icon
                      group="default"
                      name="drag"
                      size="22"
                      color="#cbc9c9"
                    ></fc-icon>
                  </div>
                </div>
                <div class="d-flex task-id-section mT10">
                  <el-input
                    data-test-selector="Section_ID"
                    type="number"
                    class="fc-input-border-remove"
                    @blur="onSequenceBlur(...arguments)"
                    v-model="task.taskId"
                  ></el-input>
                </div>
                <el-input
                  data-test-selector="Section_Subject"
                  class="mL10 fc-input-border-remove mT10"
                  :ref="`${section.section} - ${section_index} ~ ${taskIndex}`"
                  v-model="task.subject"
                  @blur="onInputBlur(...arguments)"
                  @keyup.native.enter="addNewTask(section_index)"
                  type="text"
                  :placeholder="taskPlaceHolder"
                ></el-input>
              </div>
              <div class="task-setup d-flex visibility-visible-actions">
                <div class="task-customization d-flex pT10">
                  <div
                    @click="openTaskSetting(section_index, taskIndex)"
                    class="pR10 visibility-hide-actions"
                  >
                    <fc-icon
                      group="default"
                      name="settings"
                      size="18"
                    ></fc-icon>
                  </div>
                </div>
                <div
                  v-if="section.tasks.length === 1"
                  class="space-filler"
                ></div>
                <div
                  @click="deleteTask(section_index, taskIndex)"
                  v-else
                  class="pT10 space-filler visibility-hide-actions"
                >
                  <fc-icon group="default" name="trash-can" size="18"></fc-icon>
                </div>
              </div>
            </div>
          </div>
        </Draggable>
      </div>
      <div class="add-task">
        <el-button
          @click="addNewTask(section_index)"
          class="fc-secondary-btn add-task-btn"
          >{{ taskButtonTitle }}</el-button
        >
      </div>
      <div class="add-section">
        <div class="seperator-line"></div>
        <el-button
          @click="addNewSection(section_index)"
          class="fc-secondary-btn add-section-btn"
          >{{ $t('jobplan.add_section') }}</el-button
        >
      </div>
    </div>
  </Draggable>
</template>

<script>
import Draggable from 'vuedraggable'
import cloneDeep from 'lodash/cloneDeep'
import { isEmpty } from '@facilio/utils/validation'
import { v4 as uuid } from 'uuid'
import { addResourceParams } from './utils/scope-util.js'

const INIT_TASK = {
  subject: null,
  statusNew: 1,
  taskId: 1,
}

const INIT_SECTION = {
  name: null,
  sectionId: null,
  jobPlanSectionCategory: 5,
  statusNew: 1,
  tasks: [
    {
      subject: null,
      statusNew: 1,
      taskId: 1,
      jobPlanSectionCategory: 5,
      jobPlanTaskCategory: 5,
    },
  ],
}

export default {
  props: [
    'taskList',
    'disableDrag',
    'isPreRequisite',
    'isJobPlan',
    'formModel',
  ],
  components: { Draggable },
  data: () => ({
    sectionIndex: 1,
  }),
  computed: {
    tasks: {
      get() {
        return this.taskList
      },
      set(value) {
        this.$emit('update:taskList', value)
      },
    },
    taskPlaceHolder() {
      let { isPreRequisite } = this
      return isPreRequisite
        ? this.$t('jobplan.enter_preq')
        : this.$t('jobplan.task_placeholder')
    },
    taskSectionDragOptions() {
      return {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'tasksection',
        sort: true,
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
    taskButtonTitle() {
      let { isPreRequisite } = this
      return !isPreRequisite
        ? this.$t('maintenance._workorder.add_task')
        : this.$t('jobplan.add_prerequisite')
    },
  },
  methods: {
    addNewTask(sectionIndex) {
      let taskObject = cloneDeep(INIT_TASK)
      let { tasks: taskList } = this
      let taskSection = taskList[sectionIndex] || {}
      let tasks = this.$getProperty(taskSection, 'tasks', [])
      let canAddTask = this.validateTasks(tasks)

      if (canAddTask) {
        let taskId = tasks.length + 1
        taskObject = { ...taskObject, taskId: taskId }
        tasks.push(taskObject)

        this.setJobPlanSection(sectionIndex, 'tasks', tasks)
        this.$nextTick(() => {
          this.preFillSectionSettings(this.tasks, sectionIndex)
        })
      } else {
        this.$message.error('Specify Task Subject')
      }
    },
    validateTasks(tasks) {
      let validTasks = true
      tasks.forEach(task => {
        let { subject } = task || {}
        validTasks = validTasks && !isEmpty(subject)
      })
      return validTasks
    },
    preFillSectionSettings(taskList, sectionIndex, sectionInfo = {}) {
      let { formModel } = this
      let section = !isEmpty(sectionInfo) ? sectionInfo : taskList[sectionIndex]

      //Prefilling Task with Section setting Values
      if (!isEmpty(section)) {
        let sectionData = cloneDeep(section)
        let excludeFields = ['assetCategory', 'spaceCategory']
        excludeFields.forEach(
          excludeField => delete (sectionData || {})[excludeField]
        )
        delete sectionData['tasks']
        delete sectionData['sectionId']
        section.tasks = section.tasks.map(task => {
          return {
            ...sectionData,
            ...task,
          }
        })
      }
      section = addResourceParams(formModel, section)
      this.setJobPlanSection(sectionIndex, '', section)
    },
    deleteTask(sectionIndex, taskIndex) {
      let { tasks: taskList } = this
      let section = taskList[sectionIndex] || {}
      let { tasks = [] } = section || {}

      tasks.splice(taskIndex, 1)
      section.tasks = tasks
      this.setJobPlanSection(sectionIndex, '', section)
      this.handleTaskDrag(sectionIndex)
    },
    handleTaskDrag(sectionIndex) {
      let { tasks: taskList } = this
      let taskSection = taskList[sectionIndex] || {}

      if (!isEmpty(taskSection)) {
        let { tasks = [] } = taskSection || {}
        this.setJobPlanSection(sectionIndex, 'tasks', tasks)
      }
    },
    addNewSection(currentSectionIndex) {
      let { taskList, formModel } = this
      let currentSection = taskList[currentSectionIndex]
      let { tasks = [] } = currentSection || {}

      if (this.validateTasks(tasks)) {
        let sectionId = uuid().substring(0, 4)
        let name = `Untitled Section ${taskList.length + 1}`
        let newTaskSection = cloneDeep(INIT_SECTION)

        newTaskSection = { ...newTaskSection, sectionId, name }
        newTaskSection = addResourceParams(formModel, newTaskSection)
        if (currentSectionIndex < taskList.length - 1) {
          this.tasks.splice(currentSectionIndex + 1, 0, newTaskSection)
        } else {
          this.tasks.push(newTaskSection)
        }
        this.sectionIndex += 1
      } else {
        this.$message.error('Specify Task Subject')
      }
    },
    deleteSection(sectionIndex) {
      this.tasks.splice(sectionIndex, 1)
    },
    setJobPlanSection(sectionIndex, property, value) {
      if (isEmpty(property)) {
        this.$set(this.tasks, sectionIndex, value)
      } else {
        this.$set(this.tasks, `${sectionIndex}.${property}`, value)
      }
    },
    onInputBlur(event) {
      let {
        target: { value },
      } = event
      if (isEmpty(value)) {
        this.$message.error(this.$t('jobplan.specify_task_subject'))
      }
    },
    onSectionInputBlur(event) {
      let {
        target: { value },
      } = event
      if (isEmpty(value)) {
        this.$message.error(this.$t('jobplan.specify_section_input'))
      }
    },
    onSequenceBlur(event) {
      let {
        target: { value },
      } = event
      if (isEmpty(value)) {
        this.$message.error(this.$t('jobplan.sequence_empty'))
      }
    },
    openTaskSetting(sectionIndex, taskIndex) {
      this.$emit('openTaskSetting', { sectionIndex, taskIndex })
    },
    openSectionSetting(sectionIndex) {
      this.$emit('openSectionSetting', { sectionIndex })
    },
  },
}
</script>

<style lang="scss" scoped>
.task-container {
  height: 100%;
  .text-center {
    padding-top: 150px;
    padding-bottom: 150px;
  }
  .task-add-btn {
    padding: 10px 20px;
    border: 1px solid transparent;
    min-height: 36px;
    .btn-label {
      font-size: 12px;
      font-weight: 500;
      color: #39b2c2;
      letter-spacing: 0.5px;
    }
    img {
      width: 9px;
    }
  }
  .task-add-btn:hover,
  .task-add-btn.empty {
    background-color: #f7feff;
    border: 1px solid #39b2c2;
  }
  .task-empty-icon {
    width: 49px;
    height: 49px;
    vertical-align: middle;
  }
  .task-section {
    margin-bottom: 20px;
    cursor: pointer;
  }
  .fc-secondary-btn {
    font-size: 12px;
    color: #3ab2c2;
    background-color: #ffffff;
    height: 42px;
    line-height: 1;
    display: inline-block;
    border: none;
    text-transform: uppercase;
    letter-spacing: 0.7px;
    font-weight: bold;
    cursor: pointer;
    border-radius: 3px;
    border: 1px solid #3ab2c2;
    -webkit-transition: all 0.3s;
    -moz-transition: all 0.3s;
    transition: all 0.3s;
    &:hover {
      background-color: #3ab2c2;
      color: #ffffff;
    }
    &:active {
      color: #3ab2c2;
      background-color: #ffffff;
      border: solid 1px #3ab2c2;
    }
  }
  .jp-section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 0.5px solid #d8d8d8;
    height: 70px;
    .section-left {
      display: flex;
      width: 100%;
      .section-name {
        font-size: 14px;
        letter-spacing: 0.5px;
      }
    }
    .section-customization {
      display: flex;
    }
  }
  .task-row {
    height: 55px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 0.5px solid #d8d8d8;
    .task-left {
      display: flex;
      width: 100%;
    }
    .delete-icon {
      color: #ec7c7c;
    }
    &:hover {
      background-color: #fafeff;
      .task-id-section {
        border: 1px solid #3ab2c1;
      }
    }
  }
  .add-task-btn {
    margin-top: 35px;
    margin-bottom: 30px;
    font-size: 13px;
    letter-spacing: 0.5px;
    text-align: center;
    font-weight: 500;
    text-transform: capitalize;
  }
  .add-section {
    display: flex;
    flex-direction: column;
    .add-section-btn {
      position: relative;
      margin-top: -5px;
      align-self: center;
      letter-spacing: 0.5px;
      font-size: 13px;
      text-align: center;
      font-weight: 500;
      text-transform: capitalize;
    }
  }
  .seperator-line {
    position: absolute;
    width: 100%;
    margin: 15px 0px;
    border-bottom: 0.5px solid #d8d8d8;
  }
}
.section-jp {
  width: 35px;
  font-weight: normal;
  color: #606266;
}
.task-jp {
  font-weight: normal;
  color: #606266;
  width: 45px;
}
.space-filler {
  width: 35px;
}
</style>

<style lang="scss">
.task-customization {
  .el-input__suffix {
    margin-top: 7px;
  }
}
.task-section {
  .fill-light {
    color: #324056 !important;
  }
}
.section-name {
  .el-input__inner {
    font-weight: bold !important;
  }
}
.task-id-section {
  height: 30px;
  width: 38px;
  padding-left: 5px;
  border: 1px solid #d8d8d8;
  border-radius: 3px !important;
  overflow: hidden;
  .el-input__inner {
    padding-right: 0px !important;
    padding-left: 5px !important;
    width: 40px;
  }
}
.task-name-input {
  .el-input__inner {
    word-wrap: break-word !important;
    word-break: break-word !important;
  }
}
</style>
