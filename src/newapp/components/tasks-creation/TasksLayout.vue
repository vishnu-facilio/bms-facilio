<template>
  <div
    class="task-container"
    :class="[isTasksEmpty ? 'task-empty-container' : '']"
  >
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
        @click="addNewSection()"
        class="task-add-btn empty mT5"
      >
        <img v-if="!isPreRequisite" src="~assets/add-blue.svg" />
        <span class="btn-label mL5">
          {{ sectionButtonTitle }}
        </span>
      </el-button>
    </div>
    <TasksList
      ref="task-list-jp"
      id="task-list-container"
      :taskList="taskList"
      :disableDrag="disableDrag"
      :isPreRequisite="isPreRequisite"
      :isJobPlan="isJobPlan"
      :formModel="formModel"
      @openTaskSetting="openTaskSetting"
      @openSectionSetting="openSectionSetting"
    />
    <TaskSectionDetails
      v-if="showSectionSettings"
      :showSectionSettings.sync="showSectionSettings"
      :selectedSection="selectedSection"
      :isPreRequisite="isPreRequisite"
      :formModel="formModel"
      @sectionUpdated="updateSection"
    />
    <TaskDetails
      v-if="canShowTaskDetails"
      :canShowTaskDetails.sync="canShowTaskDetails"
      :selectedTaskInfo="selectedTaskInfo"
      :isPreRequisite="isPreRequisite"
      :selectedTask="{}"
      :formModel="formModel"
      @taskUpdated="updateTaskDetails"
    ></TaskDetails>
  </div>
</template>

<script>
import cloneDeep from 'lodash/cloneDeep'
import { v4 as uuid } from 'uuid'
//import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import TaskSectionDetails from './TaskSectionDetails.vue'
import TaskDetails from './TaskDetails.vue'
import TasksList from './TasksList'
import isEqual from 'lodash/isEqual'
import { JOB_PLAN_SCOPE, addResourceParams } from './utils/scope-util.js'

export default {
  name: 'TasksLayout',
  data() {
    return {
      sectionIndex: 1,
      disableDrag: false,
      taskList: [],
      selectedSection: {},
      selectedTaskInfo: {},
      showSectionSettings: false,
      canShowTaskDetails: false,
      //woForms: null,
      oldFormModel: {},
      preventWatcher: false,
    }
  },
  props: ['type', 'isEdit', 'value', 'formModel', 'isJobPlan'],
  components: { TaskSectionDetails, TaskDetails, TasksList },
  created() {
    let { value, isEdit } = this
    //this.loadWoForms()
    if (!isEmpty(value) && isEdit) {
      this.prefillJobPlanTasks()
    }
    this.setPreviousModel()
  },
  computed: {
    isTasksEmpty() {
      let { taskList } = this
      return isEmpty(taskList)
    },
    sectionButtonTitle() {
      let { isPreRequisite } = this
      return !isPreRequisite
        ? this.$t('maintenance._workorder.add_task')
        : this.$t('jobplan.add_prerequisite')
    },
    isPreRequisite() {
      let { type } = this
      return type === 'JP_PREREQUISITE'
    },
  },
  watch: {
    taskList: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          this.serializeTasks(newVal)
        }
      },
      deep: true,
      immediate: true,
    },
    formModel: {
      async handler(newVal) {
        let { taskList, preventWatcher, oldFormModel } = this
        let { jobPlanCategory } = oldFormModel || {}
        let { jobPlanCategory: newJobPlanCategory, jobplansection = [] } =
          newVal || {}
        let oldJobPlanCategoryId = this.$getProperty(
          jobPlanCategory,
          'id',
          null
        )
        let newJobPlanCategoryId = this.$getProperty(
          newJobPlanCategory,
          'id',
          null
        )
        if (
          !isEqual(oldJobPlanCategoryId, newJobPlanCategoryId) &&
          !preventWatcher &&
          !isEmpty(oldJobPlanCategoryId)
        ) {
          await this.showConfirmCategorySwitch()
        }
        if (
          !isEmpty(jobplansection) &&
          !isEqual(jobplansection, taskList) &&
          !preventWatcher
        ) {
          this.$set(this, 'taskList', jobplansection)
        }

        this.$nextTick(() => {
          this.setPreviousModel()
          this.preventWatcher = false
        })
      },
      deep: true,
    },
  },
  methods: {
    async showConfirmCategorySwitch() {
      let dialogObj = {
        htmlMessage: this.$t('jobplan.scope_switch'),
        rbLabel: this.$t('maintenance.pm.proceed'),
        rbClass: 'jp-edit-dialog-btn',
        className: 'jp-edit-dialog',
      }
      await this.$dialog.confirm(dialogObj).then(value => {
        if (value) {
          this.resetScoping()
        } else {
          let { oldFormModel, formModel } = this
          let { jobPlanCategory } = oldFormModel || {}

          this.$emit('updateFormModel', { ...formModel, jobPlanCategory })
          this.preventWatcher = true
        }
      })
    },
    setPreviousModel() {
      let { formModel } = this
      let prevModel = cloneDeep(formModel)

      this.oldFormModel = prevModel
    },
    resetScoping() {
      let { formModel } = this
      let { jobplansection = [] } = formModel || {}
      if (!isEmpty(jobplansection)) {
        let serializedTaskList = jobplansection.map(section => {
          let { tasks } = section || {}
          let serializedTasks = tasks.map(task => {
            return {
              ...task,
              jobPlanSectionCategory: 5,
              jobPlanTaskCategory: 5,
              resource: null,
              assetCategory: null,
              spaceCategory: null,
              readings: [],
              readingFieldId: null,
            }
          })

          let serializedSection = {
            ...section,
            jobPlanSectionCategory: 5,
            resource: null,
            assetCategory: null,
            spaceCategory: null,
            tasks: serializedTasks,
          }
          serializedSection = addResourceParams(formModel, serializedSection)
          return serializedSection
        })
        this.taskList = serializedTaskList
        this.setPreviousModel()
        this.preventWatcher = true
      }
    },
    prefillJobPlanTasks() {
      let { value: jobPlanSections = [], formModel } = this
      jobPlanSections = jobPlanSections.map(section => {
        let sectionId = uuid().substring(0, 4)
        let { tasks = [], name, jobPlanSectionCategory } = section || {}

        name = name.replace('JP-', '')
        section = { ...section, name, sectionId }
        let serializedTasks = []
        if (!isEmpty(tasks)) {
          serializedTasks = tasks.map(task => {
            let { subject, sequence } = task || {}
            let taskSubject = subject.replace('JP-', '')
            let taskId = sequence
            let serializedTask = {
              ...task,
              subject: taskSubject,
              taskId,
              jobPlanSectionCategory,
            }
            return serializedTask
          })
        }
        let serializedSection = { ...section, tasks: serializedTasks }
        serializedSection = addResourceParams(formModel, serializedSection)
        serializedSection = this.fillInputConfig(serializedSection)
        return serializedSection
      })
      this.taskList = jobPlanSections
      this.serializeTasks(this.taskList)
    },
    fillInputConfig(section) {
      let inputHash = { 2: 'Reading', 3: 'Text', 4: 'Number', 5: 'Option' }
      let { inputType, tasks } = section || {}

      //Filling Section Input Configuration  if its Option Type
      if (inputHash[inputType] === 'Option') {
        let {
          inputOptions = [],
          remarkOptionValues,
          attachmentOptionValues,
          defaultValue,
          failureValue,
        } = section || {}
        if (!isEmpty(defaultValue)) {
          defaultValue = inputOptions.findIndex(
            option => option.value === defaultValue
          )
        }
        if (!isEmpty(failureValue)) {
          failureValue = inputOptions.findIndex(
            option => option.value === failureValue
          )
        }
        if (!isEmpty(attachmentOptionValues)) {
          attachmentOptionValues = (attachmentOptionValues || []).map(
            optionValue => {
              let selectedValue = inputOptions.findIndex(
                option => option.value === optionValue
              )
              return selectedValue
            }
          )
        }
        if (!isEmpty(remarkOptionValues)) {
          remarkOptionValues = (remarkOptionValues || []).map(optionValue => {
            let selectedValue = inputOptions.findIndex(
              option => option.value === optionValue
            )
            return selectedValue
          })
        }

        section = {
          ...section,
          remarkOptionValues,
          attachmentOptionValues,
          defaultValue,
          failureValue,
        }
      }

      tasks = tasks.map(task => {
        let { inputType: taskInput } = task || {}

        //Filling Task Input Configuration if its Option Type
        if (inputHash[taskInput] === 'Option') {
          let {
            inputOptions: taskInputOptions = [],
            remarkOptionValues: taskRemarkOptions,
            attachmentOptionValues: taskAttachmentOptions,
            defaultValue: taskDefaultValue,
            failureValue: taskFailureValue,
          } = task || {}

          if (!isEmpty(taskDefaultValue)) {
            taskDefaultValue = taskInputOptions.findIndex(
              option => option.value === taskDefaultValue
            )
          }
          if (!isEmpty(taskFailureValue)) {
            taskFailureValue = taskInputOptions.findIndex(
              option => option.value === taskFailureValue
            )
          }
          if (!isEmpty(taskAttachmentOptions)) {
            taskAttachmentOptions = (taskAttachmentOptions || []).map(
              optionValue => {
                let selectedValue = taskInputOptions.findIndex(
                  option => option.value === optionValue
                )
                return selectedValue
              }
            )
          }
          if (!isEmpty(taskRemarkOptions)) {
            taskRemarkOptions = (taskRemarkOptions || []).map(optionValue => {
              let selectedValue = taskInputOptions.findIndex(
                option => option.value === optionValue
              )
              return selectedValue
            })
          }

          task = {
            ...task,
            remarkOptionValues: taskRemarkOptions,
            attachmentOptionValues: taskAttachmentOptions,
            defaultValue: taskDefaultValue,
            failureValue: taskFailureValue,
          }
        }
        return task
      })
      section = { ...section, tasks }
      return section
    },
    // async loadWoForms() {
    //   let url = `/v2/forms?moduleName=workorder`
    //   let { error, data } = await API.get(url)

    //   if (error) {
    //     this.$message.error('Error Occured')
    //   } else {
    //     let { forms = [] } = data || {}
    //     this.woForms = forms.filter(
    //       form => form.id > 0 && form.name !== 'default_workorder_web'
    //     )
    //   }
    // },
    addNewSection() {
      let taskListComp = this.$refs['task-list-jp']
      if (!isEmpty(taskListComp)) taskListComp.addNewSection()
      this.preFillSectionSettings(this.taskList, 0)
    },
    openSectionSetting(props) {
      let { sectionIndex } = props || {}
      let { taskList } = this
      this.selectedSection = taskList[sectionIndex]
      this.showSectionSettings = true
    },
    openTaskSetting(props) {
      let { sectionIndex, taskIndex } = props || {}
      let { taskList } = this
      let selectedSection = taskList[sectionIndex] || {}
      let { tasks = [] } = selectedSection || {}
      let taskObj = tasks[taskIndex]

      this.selectedTaskInfo = { task: taskObj, sectionIndex, taskIndex }
      this.canShowTaskDetails = true
    },
    preFillSectionSettings(taskList, sectionIndex, sectionInfo = {}) {
      let { formModel } = this
      let section = !isEmpty(sectionInfo) ? sectionInfo : taskList[sectionIndex]

      //Prefilling Task with Section setting Values
      if (!isEmpty(section)) {
        let sectionData = cloneDeep(section)
        let { tasks = [] } = sectionData || {}
        let excludeFields = ['tasks', 'assetCategory', 'spaceCategory']
        excludeFields.forEach(
          excludeField => delete (sectionData || {})[excludeField]
        )
        let updatedTasks = tasks.map(task => {
          let { jobPlanSectionCategory: sectionCategory } = sectionData || {}
          let {
            jobPlanSectionCategory: previousSectionCategory,
            subject,
            assetCategory,
            spaceCategory,
            resource,
            description,
            taskId,
          } = task || {}
          let scoping = {}

          if (
            !isEqual(sectionCategory, previousSectionCategory) &&
            !isEmpty(previousSectionCategory)
          ) {
            scoping = {
              jobPlanSectionCategory: sectionCategory,
              jobPlanTaskCategory: null,
              resource: null,
            }
          }
          return {
            ...sectionData,
            subject,
            assetCategory,
            spaceCategory,
            resource,
            description,
            taskId,
            ...scoping,
          }
        })

        section.tasks = updatedTasks
        section = addResourceParams(formModel, section)
        let taskListComp = this.$refs['task-list-jp']
        if (!isEmpty(taskListComp))
          taskListComp.setJobPlanSection(sectionIndex, '', section)
      }
    },
    updateSection(section) {
      let { sectionId } = section || {}
      let { taskList } = this
      let sectionIndex = taskList.findIndex(
        section => section.sectionId === sectionId
      )

      this.preFillSectionSettings(taskList, sectionIndex, section)
      this.serializeTasks(taskList)
      this.showSectionSettings = false
    },
    updateTaskDetails(taskInfo) {
      let { sectionIndex, taskIndex, task } = taskInfo || {}
      let { taskList } = this
      let section = taskList[sectionIndex] || {}
      let { tasks = [] } = section || {}
      tasks.splice(taskIndex, 1, task)

      let taskListComp = this.$refs['task-list-jp']
      if (!isEmpty(taskListComp))
        taskListComp.setJobPlanSection(sectionIndex, 'tasks', tasks)
      this.serializeTasks(this.taskList)
      this.canShowTaskDetails = false
    },
    setCurrentResourceScope(tasksList) {
      let { formModel } = this
      let { jobPlanCategory } = formModel || {}
      let { id: jobPlanScope } = jobPlanCategory || {}

      let serializedTaskList = tasksList
      if (['ASSETS'].includes(JOB_PLAN_SCOPE[jobPlanScope])) {
        serializedTaskList = tasksList.map(section => {
          let { tasks } = section || {}
          let serializedTasks = tasks.map(task => {
            return { ...task, jobPlanTaskCategory: 5 }
          })

          return {
            ...section,
            jobPlanSectionCategory: 5,
            tasks: serializedTasks,
          }
        })
      }
      return serializedTaskList
    },
    serializeTasks(taskList) {
      taskList = this.setCurrentResourceScope(taskList)
      this.preventWatcher = true
      this.$emit('input', taskList)
    },
  },
}
</script>

<style lang="scss" scoped>
.task-empty-container {
  display: flex;
  align-items: center;
  justify-content: center;
}
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
      color: #39b2c2;
      font-size: 13px;
      letter-spacing: 0.5px;
      text-align: center;
      font-weight: 500;
      text-transform: capitalize;
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
    &:focus {
      background: rgb(58 178 194 / 10%);
      color: #3ab2c2;
    }
    &:active {
      color: #3ab2c2;
      background-color: #ffffff;
      border: solid 1px #3ab2c2;
    }
  }
  .jp-section-header {
    height: 65px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    .section-left {
      display: flex;
      .section-name {
        font-size: 14px;
        font-weight: 500;
        letter-spacing: 0.5px;
        color: #324056;
      }
    }
    .section-customization {
      display: flex;
      .section-setting i {
        font-size: 16px;
      }
    }
  }

  .task-row {
    height: 65px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    .task-left {
      display: flex;
      .task-name {
        font-size: 12px;
        font-weight: 400;
      }
    }
    .delete-icon {
      color: #ec7c7c;
    }
    &:hover {
      background-color: #fafeff;
    }
  }
  .add-task-btn {
    margin-top: 35px;
    margin-bottom: 30px;
  }
  .add-section {
    display: flex;
    flex-direction: column;
    .add-section-btn {
      position: relative;
      margin-top: -5px;
      align-self: center;
    }
  }
  .seperator-line {
    position: absolute;
    width: 100%;
    margin: 15px 0px;
    border-bottom: 0.5px solid #d8d8d8;
  }
}
</style>

<style lang="scss">
.jp-edit-dialog {
  .f-dialog-header {
    display: none;
  }
  .f-dialog-content {
    padding-top: 30px;
    padding-left: 30px;
    padding-right: 10px;
    min-height: 185px;
    width: 650px;
    text-align: justify;
  }
  .f-dialog-body {
    padding: 0px;
  }
  .del-cancel-btn {
    width: 50%;
  }
  .jp-edit-dialog-btn {
    width: 50%;
    background-color: #39b2c2 !important;
    border: transparent;
    margin-left: 0;
    padding-top: 20px;
    padding-bottom: 20px;
    border-radius: 0;
    font-size: 13px;
    font-weight: bold;
    letter-spacing: 1.1px;
    text-align: center;
    color: #ffffff;
    &:hover {
      background-color: #3cbfd0 !important;
    }
  }
}
.task-customization {
  .el-input__suffix {
    margin-top: 7px;
  }
}
.task-name-input {
  width: 400px;
  .el-input__inner {
    width: 300px;
    word-wrap: break-word;
    word-break: break-word;
  }
}
</style>
