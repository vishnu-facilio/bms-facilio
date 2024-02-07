<script>
import Constants from 'util/constant'
import { isEmpty } from '@facilio/utils/validation'

export default {
  methods: {
    serializeTaskData(task, index) {
      // TODO: Have to move all these handling to server side
      if (Number(task.inputType) === 4) {
        if (task.validation === 'safeLimit') {
          let readingRules = Constants.TASK_READING_RULES_SAFELIMIT
          readingRules.workflow.expressions = [
            {
              name: 'a',
              constant: '${inputValue}',
            },
            {
              name: 'b',
              constant: task.minSafeLimit,
            },
            {
              name: 'c',
              constant: task.maxSafeLimit,
            },
          ]
          readingRules.id = task.inputValidationRuleId
          task.readingRules.push(readingRules)
        }
      }
      if (Number(task.inputType) === 5) {
        task.options = task.options.map(option => option.name)
      } else {
        task.options = []
      }
      if (Number(task.inputType) !== 2) {
        task.readingFieldId = null
      }
      if (!isEmpty(task.resource)) {
        let resourceObj = {}
        if (!isEmpty(task.resource.id)) {
          resourceObj.id = task.resource.id
        } else {
          resourceObj = null
        }
        task.resource = resourceObj
        delete task.selectedLookupField
      }
      if (!isEmpty(task.taskReadings)) {
        delete task.taskReadings
      }
      for (let taskObj in task) {
        if (isEmpty(task[taskObj])) {
          delete task[taskObj]
        }
      }
      // TODO: Have to move this to server side
      task.sequence = index
      return task
    },
    /*
      Data from server,
      tasks: {
        "Untitled Section": [
          {
            ...task1
          },
          {
            ...task2
          }
          ...
        ]
      }
      after deserializing data,
      tasksList: [
        {
          section: "Untitled Section",
          tasks: [
            {
              ...task1
            },
            {
              ...task2
            }
            ...
          ]
        }
      ]
    */
    deserializeTaskData(jobPlan) {
      let { tasks = {} } = jobPlan
      let tasksList = []
      for (let task in tasks) {
        let taskArr = tasks[task]
        let taskObj = {
          tasks: [],
        }
        taskArr.forEach(item => {
          let taskItem = {
            visibility: false,
          }
          taskObj.section = task
          // In client, to check the enable input checkbox for the task
          if (item.inputType > 1) {
            item.enableInput = true
          }
          if (!isEmpty(item.options)) {
            let itemOptions = item.options.map(option => {
              let optionMap = {}
              optionMap.name = option
              return optionMap
            })
            item.options = itemOptions
            taskItem.defaultValue = item.defaultValue
          } else {
            item.options = Constants.TASK_DEFAULT_OPTIONS
          }
          // TODO: Change the inputtype as number
          item.inputType = String(item.inputType)
          Constants.TASK_RESOURCE_PROPS.forEach(prop => {
            let value = item[prop]
            if (prop === 'resource') {
              if (isEmpty(value)) {
                taskItem[prop] = {
                  name: '',
                }
                taskItem.selectedLookupField = {
                  ...Constants.RESOURCE_DEFAULT_OBJ,
                }
              } else {
                taskItem[prop] = value
                taskItem.selectedLookupField = {
                  ...Constants.RESOURCE_DEFAULT_OBJ,
                  selectedItems: [
                    {
                      name: value.name,
                      id: value.id,
                      resourceType: value.resourceType,
                    },
                  ],
                  value: {
                    id: value.id,
                  },
                }
              }
            } else {
              taskItem[prop] = value
            }
          })
          taskItem.readingRules = []
          taskObj.tasks.push(taskItem)
        })
        tasksList.push(taskObj)
      }
      return tasksList
    },
  },
}
</script>

<style></style>
