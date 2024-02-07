<template>
  <div class="scrollable-y100">
    <div class="fc-pm-form-right-main">
      <div v-if="!model.isEdit" class="fc-grey-text mT10">
        NEW PLANNED MAINTENANCE
      </div>
      <div v-else class="fc-grey-text mT10">EDIT PLANNED MAINTENANCE</div>
      <div class="heading-black22 mB20">
        Maintenance for {{ resourceName() }}
      </div>
      <div class="fc-pm-main-bg fc-pm-main-bg2">
        <div class="fc-pm-main-content">
          <div class="fc-pm-main-content-H">TASKS</div>
          <div class="fc-heading-border-width43"></div>
          <div class="fc-pm-main-inner-container">
            <div class="fc-pm-task-form-table-con">
              <draggable
                :options="{ draggable: '.asf' }"
                :element="'div'"
                :list="model.taskData.taskSections"
              >
                <table
                  class="fc-pm-form-table mT40 fc-pm-form-task-table asf"
                  v-for="(taskSection, key) in model.taskData.taskSections"
                  :key="key"
                >
                  <thead>
                    <tr
                      @click="
                        setCurrentPosition(
                          key,
                          model.taskData.taskSections[key].tasks.length - 1
                        )
                      "
                      class="visibility-visible-actions"
                    >
                      <th style="width: 10px"></th>
                      <th style="width: 600px">
                        <el-input
                          v-model="taskSection.name"
                          class="fc-input-border-remove task-input-bold pR20 position-relative mT20 width100"
                          style="left: -50px"
                        ></el-input>
                      </th>
                      <th
                        v-if="
                          (model.woData.workOrderType === 'bulk' &&
                            model.taskData.taskSections[key].category) ||
                            (model.woData.workOrderType === 'single' &&
                              model.woData.singleResource &&
                              model.woData.singleResource.resourceTypeEnum ===
                                'SPACE')
                        "
                        class="position-relative fc-elselect-dpdown-txt"
                      >
                        <el-select
                          :ref="`section-${key}`"
                          @visible-change="
                            setCurrentPosition(
                              key,
                              model.taskData.taskSections[key].tasks.length - 1
                            ),
                              beforeChange($event)
                          "
                          filterable
                          v-model="
                            model.taskData.taskSections[key].resourceLabel
                          "
                          @change="handleSelectLabel"
                          class="fc-input-full-border-select2"
                        >
                          <el-option
                            :label="categoryOptions[0].label"
                            :value="{
                              index: key,
                              categ: [categoryOptions[0].value],
                            }"
                            class="visibility-visible-actions pm-task-active-remove"
                          >
                            <div class="fL width200px textoverflow-ellipsis">
                              {{ categoryOptions[0].label }}
                            </div>
                            <div
                              v-if="model.woData.workOrderType !== 'single'"
                              class="fR visibility-hide-actions"
                              @click.stop="
                                setCurrentSection(key),
                                  handleSearchSelect(key, [
                                    categoryOptions[0].value,
                                  ])
                              "
                            >
                              <img
                                src="~assets/FilterIcon.svg"
                                height="15px"
                                width="15px"
                              />
                            </div>
                          </el-option>
                          <el-option-group
                            v-if="index !== 0"
                            v-for="(categ, index) in categoryOptions"
                            :key="index"
                            :label="categoryOptions[index].label"
                          >
                            <el-option
                              v-for="(child, ci) in categoryOptions[index]
                                .children"
                              :key="ci"
                              :label="categoryOptions[index].children[ci].label"
                              :value="{
                                index: key,
                                categ: [
                                  categoryOptions[index].value,
                                  categoryOptions[index].children[ci].value,
                                ],
                              }"
                              class="visibility-visible-actions pm-task-active-remove"
                            >
                              <div class="fL width200px textoverflow-ellipsis">
                                {{ categoryOptions[index].children[ci].label }}
                              </div>
                              <div
                                class="fR visibility-hide-actions"
                                @click.stop="
                                  setCurrentSection(key),
                                    handleSearchSelect(key, [
                                      categoryOptions[index].value,
                                      categoryOptions[index].children[ci].value,
                                    ])
                                "
                              >
                                <img
                                  src="~assets/FilterIcon.svg"
                                  height="15px"
                                  width="15px"
                                />
                              </div>
                            </el-option>
                          </el-option-group>
                        </el-select>
                      </th>
                      <th class="fc-pm-popover-btn" style="width: 60px">
                        <el-button
                          @click="
                            setCurrentSection(key), showSectionSettingsDialog()
                          "
                          class="mT10"
                        >
                          <img
                            src="~assets/settings-grey.svg"
                            height="16px"
                            width="16px"
                          />
                        </el-button>
                      </th>
                      <th>
                        <div
                          v-if="model.taskData.taskSections.length > 1"
                          class="actions task-delete-icon-block"
                          @click.stop="
                            setCurrentSection(key), deleteSection(key)
                          "
                        >
                          <i
                            class="el-icon-delete pointer fc-delete-icon visibility-hide-actions task-th-delete-icon"
                          ></i>
                        </div>
                      </th>
                    </tr>
                  </thead>
                  <draggable
                    :options="{ draggable: '.asd' }"
                    :element="'tbody'"
                    :list="model.taskData.taskSections[key].tasks"
                  >
                    <tr
                      v-for="(task, taskKey) in taskSection.tasks"
                      :key="taskKey"
                      @click="setCurrentSection(key), setCurrentTask(taskKey)"
                      class="visibility-visible-actions pointer table-hover asd"
                    >
                      <td style="width: 50px">
                        <el-input
                          type="number"
                          class="wo-task-list-num"
                          v-model="
                            taskSection.tasks[taskKey].additionInfo.uniqueId
                          "
                        ></el-input>
                      </td>
                      <td style="width: 386px">
                        <el-input
                          :ref="'task-' + key + '-' + taskKey"
                          @keyup.native.enter="
                            onTaskEnter($event, taskKey, task.subject)
                          "
                          @paste.native="
                            onTaskPaste(taskSection.tasks, taskKey, $event)
                          "
                          v-model="taskSection.tasks[taskKey].name"
                          style="width: 90%"
                          class="fc-input-border-remove fL mL10 pR20"
                          placeholder="Enter a task"
                        ></el-input>
                      </td>
                      <td
                        v-if="
                          model.woData.workOrderType === 'bulk' ||
                            (model.woData.workOrderType === 'single' &&
                              model.woData.singleResource &&
                              model.woData.singleResource.id &&
                              model.woData.singleResource.resourceTypeEnum ===
                                'SPACE')
                        "
                      >
                        <el-select
                          :ref="`${key}-${taskKey}`"
                          @visible-change="
                            setCurrentPosition(key, taskKey),
                              beforeTaskChange($event)
                          "
                          filterable
                          @change="handleTaskSelectLabel"
                          v-model="
                            model.taskData.taskSections[key].tasks[taskKey]
                              .resourceLabel
                          "
                          class="fc-input-full-border-select-hover fc-input-border-remove fc-elselect-dpdown-txt pR0"
                        >
                          <el-option
                            :label="getCategoryOptionsForTasksLabel(0)"
                            :value="{
                              index: key,
                              taskIndex: taskKey,
                              categ: [getCategoryOptionsForTasksValue(0)],
                            }"
                            class="visibility-visible-actions pm-task-active-remove"
                          >
                            <div class="fL width200px textoverflow-ellipsis">
                              {{ getCategoryOptionsForTasksLabel(0) }}
                            </div>
                            <div
                              v-if="model.woData.workOrderType !== 'single'"
                              class="fR visibility-hide-actions"
                              @click.stop="
                                setCurrentPosition(key, taskKey),
                                  handleTaskSearchSelect(key, taskKey, [
                                    getCategoryOptionsForTasksValue(0),
                                  ])
                              "
                            >
                              <img
                                src="~assets/FilterIcon.svg"
                                height="15px"
                                width="15px"
                              />
                            </div>
                          </el-option>
                          <el-option-group
                            v-if="index !== 0"
                            v-for="(categ, index) in categoryOptionsForTasks"
                            :key="index"
                            :label="categoryOptionsForTasks[index].label"
                          >
                            <el-option
                              v-for="(child, ci) in categoryOptionsForTasks[
                                index
                              ].children"
                              :key="ci"
                              :label="
                                categoryOptionsForTasks[index].children[ci]
                                  .label
                              "
                              :value="{
                                index: key,
                                taskIndex: taskKey,
                                categ: [
                                  categoryOptionsForTasks[index].value,
                                  categoryOptionsForTasks[index].children[ci]
                                    .value,
                                ],
                              }"
                              class="visibility-visible-actions pm-task-active-remove"
                            >
                              <div class="fL width200px textoverflow-ellipsis">
                                {{
                                  categoryOptionsForTasks[index].children[ci]
                                    .label
                                }}
                              </div>
                              <div
                                class="fR visibility-hide-actions"
                                @click.stop="
                                  setCurrentPosition(key, taskKey),
                                    handleTaskSearchSelect(key, taskKey, [
                                      categoryOptionsForTasks[index].value,
                                      categoryOptionsForTasks[index].children[
                                        ci
                                      ].value,
                                    ])
                                "
                              >
                                <img
                                  src="~assets/FilterIcon.svg"
                                  height="15px"
                                  width="15px"
                                />
                              </div>
                            </el-option>
                          </el-option-group>
                        </el-select>
                      </td>
                      <td class="fc-pm-popover-btn2">
                        <el-button
                          @click="
                            setCurrentPosition(key, taskKey),
                              showTaskSettingsDialog()
                          "
                          class="mT0 btn-border-hide visibility-hide-actions"
                        >
                          <img
                            src="~assets/settings-grey.svg"
                            height="16px"
                            width="16px"
                          />
                        </el-button>
                      </td>
                      <td>
                        <div
                          v-if="
                            model.taskData.taskSections[key].tasks.length > 1
                          "
                          @click.stop="
                            setCurrentPosition(key, taskKey),
                              deleteTask(key, taskKey)
                          "
                          class="task-delete-icon-block position-relative"
                        >
                          <i
                            class="el-icon-delete fc-delete-icon pointer visibility-hide-actions"
                          ></i>
                        </div>
                      </td>
                    </tr>
                  </draggable>
                </table>
              </draggable>
              <div class="mT30">
                <el-button class="fc-btn-green-medium-fill" @click="addNewTask"
                  >ADD TASK</el-button
                >
                <el-button
                  class="fc-btn-green-medium-border mL20"
                  @click="addNewSection"
                  >ADD SECTION</el-button
                >
              </div>
            </div>
          </div>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel f13" @click="moveToPrevious"
            >PREVIOUS</el-button
          >
          <el-button
            type="primary"
            class="modal-btn-save f13"
            @click="moveToNext"
          >
            PROCEED TO NEXT
            <img
              src="~assets/arrow-pointing-white-right.svg"
              width="17px"
              class="fR"
            />
          </el-button>
        </div>
      </div>
    </div>
    <el-dialog
      :visible.sync="showTaskSettings"
      :append-to-body="true"
      title="TASK DETAILS"
      class="fc-dialog-center-container taskdialog2"
    >
      <div class="task-container-scroll2">
        <div class="fc-dark-grey-txt14 mT20 mb5">Task name</div>
        <el-input
          v-model="taskSettingsEdit.name"
          placeholder="Enter a task"
          class="fc-input-full-border2"
        ></el-input>
        <div class="fc-dark-grey-txt14 mT20 mb5">Description</div>
        <el-input
          v-model="taskSettingsEdit.description"
          type="textarea"
          :autosize="{ minRows: 3, maxRows: 3 }"
          class="fc-input-txt fc-desc-input fc-input-full-border-textarea mT5"
          autocomplete="off"
          :rows="1"
          resize="none"
          placeholder="Enter Description"
        />
        <div class="mT20 mB10">
          <div class="fc-dark-grey-txt14 mT30 mB30">Task Type</div>
          <el-checkbox v-model="taskSettingsEdit.enableInput"
            >Enable Input</el-checkbox
          >
        </div>
        <div v-if="taskSettingsEdit.enableInput" class="mT20">
          <el-radio
            @change="onTaskSelectInput"
            :disabled="
              !(
                model.woData.workOrderType === 'single' &&
                model.woData.singleResource.resourceTypeEnum === 'ASSET'
              ) &&
                !model.taskData.taskSections[currentIndex].category &&
                !model.taskData.taskSections[currentIndex].tasks[
                  currentTaskIndex
                ].category
            "
            v-model="taskSettingsEdit.inputType"
            label="2"
            class="fc-radio-btn mT20"
          >
            Reading
            {{
              !(
                model.woData.workOrderType === 'single' &&
                model.woData.singleResource.resourceTypeEnum === 'ASSET'
              ) &&
              !model.taskData.taskSections[currentIndex].tasks[currentTaskIndex]
                .category
                ? ' ( Please select Space/Asset for the task to enable )'
                : ''
            }}
          </el-radio>
          <el-radio
            @change="onTaskSelectInput"
            v-model="taskSettingsEdit.inputType"
            label="3"
            class="fc-radio-btn mT20"
            >Text</el-radio
          >
          <el-radio
            @change="onTaskSelectInput"
            v-model="taskSettingsEdit.inputType"
            label="4"
            class="fc-radio-btn mT20"
            >Number</el-radio
          >
          <el-radio
            @change="onTaskSelectInput"
            v-model="taskSettingsEdit.inputType"
            label="5"
            class="fc-radio-btn mT20"
            >Option</el-radio
          >
        </div>
        <div
          v-if="
            taskSettingsEdit.enableInput && taskSettingsEdit.inputType === '2'
          "
          class="mT20"
        >
          <div class="fc-dark-grey-txt14 mb5">Reading Field</div>
          <el-select
            v-if="taskSettingsEdit.readingFields"
            v-model="taskSettingsEdit.readingFieldId"
            filterable
            placeholder=" "
            class="fc-input-full-border-select2 width200px"
          >
            <el-option
              v-for="readingField in taskSettingsEdit.readingFields"
              :key="readingField.id"
              :label="readingField.displayName"
              :value="readingField.id"
            ></el-option>
          </el-select>
        </div>
        <div
          v-if="
            taskSettingsEdit.enableInput &&
              (taskSettingsEdit.inputType === '5' ||
                taskSettingsEdit.inputType === '6')
          "
        >
          <div class="fc-dark-grey-txt14 mT20">Options</div>
          <el-row
            class="mT20"
            v-for="(option, index) in taskSettingsEdit.options"
            :key="index"
          >
            <el-col :span="10">
              <div>
                <el-input
                  type="text"
                  v-model="option.name"
                  @change="setDefaultValueForTaskSettings(taskSettingsEdit)"
                  :placeholder="`Option ${index + 1}`"
                  class="fc-input-full-border2 width200px task-option-input"
                >
                  <template slot="prepend">{{ index + 1 }}</template>
                </el-input>
              </div>
            </el-col>
            <el-col
              v-if="taskSettingsEdit.inputType === '5'"
              :span="1"
              class="task-add-delete"
            >
              <div
                @click="addTaskOption(taskSettingsEdit)"
                class="mT10 pointer"
              >
                <img src="~assets/add-icon.svg" width="16" height="16" />
              </div>
            </el-col>
            <el-col v-if="taskSettingsEdit.inputType === '5'" :span="2">
              <div
                @click="
                  removeOption(taskSettingsEdit, index, (isTaskSettings = true))
                "
                v-show="taskSettingsEdit.options.length > 2"
                class="mT10 mL10"
              >
                <i class="el-icon-delete fc-delete-icon pointer"></i>
              </div>
            </el-col>
          </el-row>
        </div>
        <div
          v-if="
            taskSettingsEdit.enableInput &&
              taskSettingsEdit.inputType &&
              taskSettingsEdit.inputType !== '2'
          "
        >
          <div class="fc-dark-grey-txt14 mT20 mb5">Default Value</div>
          <el-select
            v-if="
              taskSettingsEdit.inputType === '5' ||
                taskSettingsEdit.inputType === '6'
            "
            v-model="taskSettingsEdit.defaultValue"
            placeholder="Select Default Value"
            class="fc-input-full-border-select2 width200px"
            clearable
          >
            <el-option
              v-for="(option, index) in taskSettingsEdit.options"
              :label="option.name"
              :key="index"
              :value="option.name"
            ></el-option>
          </el-select>
          <el-input
            v-else
            :type="taskSettingsEdit.inputType === '4' ? 'number' : 'text'"
            v-model="taskSettingsEdit.defaultValue"
            placeholder="Enter default value"
            class="fc-input-full-border2 width200px task-option-input"
          ></el-input>
        </div>
        <div
          v-if="
            taskSettingsEdit.enableInput &&
              (taskSettingsEdit.inputType === '4' ||
                taskSettingsEdit.inputType === '5' ||
                taskSettingsEdit.inputType === '6')
          "
        >
          <template v-if="taskSettingsEdit.inputType === '4'">
            <div class="fc-dark-grey-txt14 f15 mT20 mb5">
              Deviation Condition
            </div>
            <el-row :gutter="20">
              <el-col :span="11">
                <div class="fc-dark-grey-txt14 mT10 mb5">Operator</div>
                <el-select
                  v-model="taskSettingsEdit.deviationOperatorId"
                  placeholder="Select Operator"
                  class="fc-input-full-border-select2 width200px"
                >
                  <el-option label="=" :value="9"></el-option>
                  <el-option label="!=" :value="10"></el-option>
                  <el-option label="<" :value="11"></el-option>
                  <el-option label="<=" :value="12"></el-option>
                  <el-option label=">" :value="13"></el-option>
                  <el-option label=">=" :value="14"></el-option>
                </el-select>
              </el-col>
              <el-col :span="11">
                <div class="fc-dark-grey-txt14 mT10 mb5">Value</div>
                <el-input
                  type="number"
                  v-model="taskSettingsEdit.failureValue"
                  placeholder="Enter value"
                  class="fc-input-full-border2 width200px task-option-input"
                ></el-input>
              </el-col>
            </el-row>
          </template>
          <div v-else>
            <div class="fc-dark-grey-txt14 mT20 mb5">Deviation Value</div>
            <el-select
              v-model="taskSettingsEdit.failureValue"
              placeholder="Select Deviation Value"
              class="fc-input-full-border-select2 width200px"
              clearable
            >
              <el-option
                v-for="(option, index) in taskSettingsEdit.options"
                :label="option.name"
                :key="index"
                :value="option.name"
              ></el-option>
            </el-select>
          </div>
          <div class="mT10">
            <el-checkbox
              v-model="taskSettingsEdit.createWoOnFailure"
              class="width-full pT20 pB10"
              >Create Work Order on Deviation</el-checkbox
            >
            <!-- Disabling the form selection
              <el-select
              v-if="taskSettingsEdit.createWoOnFailure"
              v-model="taskSettingsEdit.woCreateFormId"
              placeholder="Select Template"
              class="fc-input-full-border-select2 width200px"
            >
              <el-option
                v-for="(form, index) in woForms"
                :label="form.displayName"
                :key="index"
                :value="form.id"
              ></el-option>
            </el-select> -->
          </div>
        </div>
        <!-- Disabling the Validation Selection
        <div
          v-if="
            taskSettingsEdit.enableInput && taskSettingsEdit.inputType === '4'
          "
        >
          <div class="fc-dark-grey-txt14 mT20 mb5">Validation</div>
          <el-select
            v-model="taskSettingsEdit.validation"
            placeholder
            class="fc-input-full-border-select2 width200px"
          >
            <el-option label="None" key="none" value="none"></el-option>
            <el-option
              label="Incremental"
              key="incremental"
              value="incremental"
            ></el-option>
            <el-option
              label="Decremental"
              key="decremental"
              value="decremental"
            ></el-option>
            <el-option
              label="Safe Limit"
              key="safeLimit"
              value="safeLimit"
            ></el-option>
          </el-select>
          <div
            v-if="
              taskSettingsEdit.enableInput &&
                taskSettingsEdit.validation === 'safeLimit'
            "
          >
            <el-row class="mT20">
              <el-col :span="12">
                <div class="fc-dark-grey-txt14 mb5">Maximum Value</div>
                <el-input
                  type="number"
                  v-model="taskSettingsEdit.minSafeLimit"
                  class="fc-input-full-border-select2 width200px"
                ></el-input>
              </el-col>
              <el-col :span="12">
                <div class="fc-dark-grey-txt14 mb5">Minimum Value</div>
                <el-input
                  type="number"
                  v-model="taskSettingsEdit.minSafeLimit"
                  class="fc-input-full-border-select2 width200px"
                ></el-input>
              </el-col>
            </el-row>
          </div>
        </div> -->
        <div class="fc-dark-grey-txt14 mT20 mb5">Validation</div>
        <div class="mT20">
          <el-checkbox v-model="taskSettingsEdit.attachmentRequired"
            >Photo Mandatory</el-checkbox
          >
        </div>
        <div
          v-if="
            taskSettingsEdit.attachmentRequired &&
              taskSettingsEdit.enableInput &&
              taskSettingsEdit.inputType === '5'
          "
        >
          <el-row class="mT20">
            <el-col :span="12">
              <div class="fc-dark-grey-txt14 mb5">Attachment Options</div>
              <el-radio-group
                v-model="taskSettingsEdit.attachmentOption"
                class="mT10"
              >
                <el-radio label="all" class="fc-radio-btn">All</el-radio>
                <el-radio label="specific" class="fc-radio-btn"
                  >Specific</el-radio
                >
              </el-radio-group>
              <el-select
                v-if="taskSettingsEdit.attachmentOption === 'specific'"
                v-model="taskSettingsEdit.attachmentOptionValues"
                multiple
                collapse-tags
                placeholder="Select Values"
                class="fc-input-full-border-select2 fc-tag el-select-block width200px mT10"
              >
                <el-option
                  v-for="(option, index) in taskSettingsEdit.options"
                  :label="option.name"
                  :key="index"
                  :value="option.name"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
        </div>
        <div
          class="mT20"
          v-if="
            taskSettingsEdit.enableInput && taskSettingsEdit.inputType === '5'
          "
        >
          <el-checkbox v-model="taskSettingsEdit.remarksRequired"
            >Remarks Mandatory</el-checkbox
          >
          <div v-if="taskSettingsEdit.remarksRequired">
            <el-row class="mT20">
              <el-col :span="12">
                <div class="fc-dark-grey-txt14 mb5">Remarks Options</div>
                <el-radio-group
                  v-model="taskSettingsEdit.remarkOption"
                  class="mT10"
                >
                  <el-radio label="all" class="fc-radio-btn">All</el-radio>
                  <el-radio label="specific" class="fc-radio-btn"
                    >Specific</el-radio
                  >
                </el-radio-group>
                <el-select
                  v-if="taskSettingsEdit.remarkOption === 'specific'"
                  v-model="taskSettingsEdit.remarkOptionValues"
                  multiple
                  collapse-tags
                  placeholder="Select Values"
                  class="fc-input-full-border-select2 fc-tag el-select-block width200px mT10"
                >
                  <el-option
                    v-for="(option, index) in taskSettingsEdit.options"
                    :label="option.name"
                    :key="index"
                    :value="option.name"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="cancelTaskSettings"
          >CANCEL</el-button
        >
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="saveTaskSettings"
          >SAVE</el-button
        >
      </div>
    </el-dialog>
    <el-dialog
      :visible.sync="showSectionSettings"
      :append-to-body="true"
      title="CONFIGURE TASK SECTION"
      class="fc-dialog-center-container taskdialog"
    >
      <div class="fc-alert-msg-info">
        <i class="fa fa-exclamation-triangle pR10" aria-hidden="true"></i> This
        setting is applied to every task in this section but can also be edited
        individually
      </div>
      <div class="mT10 mB10">
        <div class="fc-dark-grey-txt14 mT30">Section Name</div>
        <el-input
          v-model="sectionSettingsEdit.name"
          placeholder="Write Section Name"
          class="fc-input-full-border2 mT10"
        ></el-input>
      </div>
      <div
        class="fc-dark-grey-txt14 mT20"
        v-if="
          model.triggerData.triggers && model.triggerData.triggers.length > 1
        "
      >
        Triggers
      </div>
      <div
        class="mT10 mB10 flex-middle"
        v-if="
          model.triggerData.triggers && model.triggerData.triggers.length > 1
        "
      >
        <el-select
          @change="triggerOptionHandler"
          v-model="sectionSettingsEdit.triggers"
          multiple
          collapse-tags
          class="fc-input-full-border-select2 width-full fc-tag width90"
        >
          <el-option :label="'All'" :value="'All'"></el-option>
          <el-option
            v-for="(trigger, index) in model.triggerData.triggers"
            :key="index"
            :label="trigger.name"
            :value="trigger.name"
          ></el-option>
        </el-select>
        <img
          v-if="$helpers.isLicenseEnabled('SCHEDULED_WO')"
          src="~assets/customize-icon.svg"
          class="mL10 pointer"
          @click="showTriggerContextDialog"
        />
      </div>
      <div class="mT20 mB10">
        <div class="width100">
          <div class="fc-dark-grey-txt14 mT20">Enable Input</div>
          <el-checkbox v-model="sectionSettingsEdit.enableInput" class="mT10"
            >Enable Input</el-checkbox
          >
        </div>
      </div>
      <div class>
        <div
          v-if="sectionSettingsEdit.enableInput"
          class="mT20 form-task-radio"
        >
          <div class="fc-dark-grey-txt14 mT30">Task Type</div>
          <el-radio
            @change="onSelectInput"
            v-model="sectionSettingsEdit.inputType"
            label="3"
            class="fc-radio-btn mT20"
            >Text</el-radio
          >
          <el-radio
            @change="onSelectInput"
            v-model="sectionSettingsEdit.inputType"
            label="4"
            class="fc-radio-btn mT20"
            >Number</el-radio
          >
          <el-radio
            @change="onSelectInput"
            v-model="sectionSettingsEdit.inputType"
            label="5"
            class="fc-radio-btn mT20"
            >Option</el-radio
          >
        </div>
        <div
          v-if="
            sectionSettingsEdit.enableInput &&
              (sectionSettingsEdit.inputType === '5' ||
                sectionSettingsEdit.inputType === '6')
          "
          class="mT30"
        >
          <div class="fc-dark-grey-txt14">Options</div>
          <div
            :key="index"
            v-for="(option, index) in sectionSettingsEdit.options"
            class="mT20"
          >
            <el-row class="visibility-visible-actions">
              <el-col :span="10">
                <el-input
                  type="text"
                  v-model="option.name"
                  @change="
                    setDefaultValueForSectionSettings(sectionSettingsEdit)
                  "
                  :placeholder="`Option ${index + 1}`"
                  class="fc-input-full-border2 width200px task-option-input"
                >
                  <template slot="prepend">{{ index + 1 }}</template>
                </el-input>
              </el-col>
              <el-col
                v-if="sectionSettingsEdit.inputType === '5'"
                :span="1"
                class="visibility-hide-actions pointer task-add-delete"
              >
                <div @click="addTaskOption(sectionSettingsEdit)" class="mT12">
                  <img src="~assets/add-icon.svg" width="16" height="16" />
                </div>
              </el-col>
              <el-col
                v-if="sectionSettingsEdit.inputType === '5'"
                :span="2"
                class="visibility-hide-actions pointer"
              >
                <div
                  @click="
                    removeOption(
                      sectionSettingsEdit,
                      index,
                      (isTaskSectionSettings = true)
                    )
                  "
                  v-show="sectionSettingsEdit.options.length > 2"
                  class="mT12 mL10"
                >
                  <img src="~/assets/remove-icon.svg" width="16" height="16" />
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>
      <div
        v-if="
          sectionSettingsEdit.enableInput &&
            sectionSettingsEdit.inputType &&
            sectionSettingsEdit.inputType !== '2'
        "
      >
        <div class="fc-dark-grey-txt14 mT20 mb5">Default Value</div>
        <el-select
          v-if="
            sectionSettingsEdit.inputType === '5' ||
              sectionSettingsEdit.inputType === '6'
          "
          v-model="sectionSettingsEdit.defaultValue"
          placeholder="Select Default Value"
          class="fc-input-full-border-select2 width200px"
          clearable
        >
          <el-option
            v-for="(option, index) in sectionSettingsEdit.options"
            :label="option.name"
            :key="index"
            :value="option.name"
          ></el-option>
        </el-select>
        <el-input
          v-else
          :type="sectionSettingsEdit.inputType === '4' ? 'number' : 'text'"
          v-model="sectionSettingsEdit.defaultValue"
          placeholder="Enter default value"
          class="fc-input-full-border2 width200px task-option-input"
        ></el-input>
      </div>
      <div
        v-if="
          sectionSettingsEdit.enableInput &&
            (sectionSettingsEdit.inputType === '4' ||
              sectionSettingsEdit.inputType === '5' ||
              sectionSettingsEdit.inputType === '6')
        "
      >
        <template v-if="sectionSettingsEdit.inputType === '4'">
          <div class="fc-dark-grey-txt14 f15 mT20 mb5">Deviation Condition</div>
          <el-row :gutter="20">
            <el-col :span="11">
              <div class="fc-dark-grey-txt14 mT10 mb5">Operator</div>
              <el-select
                v-model="sectionSettingsEdit.deviationOperatorId"
                placeholder="Select Operator"
                class="fc-input-full-border-select2 width200px"
              >
                <el-option label="=" :value="9"></el-option>
                <el-option label="!=" :value="10"></el-option>
                <el-option label="<" :value="11"></el-option>
                <el-option label="<=" :value="12"></el-option>
                <el-option label=">" :value="13"></el-option>
                <el-option label=">=" :value="14"></el-option>
              </el-select>
            </el-col>
            <el-col :span="11">
              <div class="fc-dark-grey-txt14 mT10 mb5">Value</div>
              <el-input
                type="number"
                v-model="sectionSettingsEdit.failureValue"
                placeholder="Enter value"
                class="fc-input-full-border2 width200px task-option-input"
              ></el-input>
            </el-col>
          </el-row>
        </template>
        <div v-else>
          <div class="fc-dark-grey-txt14 mT20 mb5">Deviation Value</div>
          <el-select
            v-model="sectionSettingsEdit.failureValue"
            placeholder="Select Deviation Value"
            class="fc-input-full-border-select2 width200px"
            clearable
          >
            <el-option
              v-for="(option, index) in sectionSettingsEdit.options"
              :label="option.name"
              :key="index"
              :value="option.name"
            ></el-option>
          </el-select>
        </div>
        <div class="mT10">
          <el-checkbox
            v-model="sectionSettingsEdit.createWoOnFailure"
            class="width-full pT20 pB10"
            >Create Work Order on Deviation</el-checkbox
          >
          <!-- Disabling the form selection
            <el-select
            v-if="sectionSettingsEdit.createWoOnFailure"
            v-model="sectionSettingsEdit.woCreateFormId"
            placeholder="Select Template"
            class="fc-input-full-border-select2 width200px"
          >
            <el-option
              v-for="(template, index) in woForms"
              :label="template.name"
              :key="index"
              :value="template.id"
            ></el-option>
          </el-select> -->
        </div>
      </div>
      <!-- Disabling the Validation Selection
        <div
        v-if="
          sectionSettingsEdit.enableInput &&
            sectionSettingsEdit.inputType === '4'
        "
        class="mT30"
      >
        <div class="fc-dark-grey-txt14 mb5">Validation</div>
        <el-select
          v-model="sectionSettingsEdit.validation"
          placeholder
          class="fc-input-full-border-select2 width200px"
        >
          <el-option label="None" key="none" value="none"></el-option>
          <el-option
            label="Incremental"
            key="incremental"
            value="incremental"
          ></el-option>
          <el-option
            label="Decremental"
            key="decremental"
            value="decremental"
          ></el-option>
          <el-option
            label="Safe Limit"
            key="safeLimit"
            value="safeLimit"
          ></el-option>
        </el-select>
        <div
          v-if="
            sectionSettingsEdit.enableInput &&
              sectionSettingsEdit.validation === 'safeLimit'
          "
        >
          <el-row class="mT30">
            <el-col :span="12">
              <div class="fc-dark-grey-txt14 mb5">Maximum Value</div>
              <el-input
                type="number"
                v-model="sectionSettingsEdit.maxSafeLimit"
                class="fc-input-full-border-select2 width200px"
              ></el-input>
            </el-col>
            <el-col :span="12">
              <div class="fc-dark-grey-txt14 mb5">Minimum Value</div>
              <el-input
                type="number"
                v-model="sectionSettingsEdit.minSafeLimit"
                class="fc-input-full-border-select2 width200px"
              ></el-input>
            </el-col>
          </el-row>
        </div>
      </div> -->
      <div>
        <div class="fc-dark-grey-txt14 mT20">Validation</div>
        <el-checkbox
          v-model="sectionSettingsEdit.attachmentRequired"
          class="mT10"
          >Photo Mandatory</el-checkbox
        >
        <div
          v-if="
            sectionSettingsEdit.attachmentRequired &&
              sectionSettingsEdit.enableInput &&
              sectionSettingsEdit.inputType === '5'
          "
        >
          <el-row class="mT20">
            <el-col :span="12">
              <div class="fc-dark-grey-txt14 mb5">Attachment Options</div>
              <el-radio-group
                v-model="sectionSettingsEdit.attachmentOption"
                class="mT10"
              >
                <el-radio label="all" class="fc-radio-btn">All</el-radio>
                <el-radio label="specific" class="fc-radio-btn"
                  >Specific</el-radio
                >
              </el-radio-group>
              <el-select
                v-if="sectionSettingsEdit.attachmentOption === 'specific'"
                v-model="sectionSettingsEdit.attachmentOptionValues"
                multiple
                collapse-tags
                placeholder="Select Values"
                class="fc-input-full-border-select2 fc-tag el-select-block width200px mT10"
              >
                <el-option
                  v-for="(option, index) in sectionSettingsEdit.options"
                  :label="option.name"
                  :key="index"
                  :value="String(option.name)"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
        </div>
      </div>
      <div
        class="mT20"
        v-if="
          sectionSettingsEdit.enableInput &&
            sectionSettingsEdit.inputType === '5'
        "
      >
        <el-checkbox v-model="sectionSettingsEdit.remarksRequired"
          >Remarks Mandatory</el-checkbox
        >
        <div v-if="sectionSettingsEdit.remarksRequired">
          <el-row class="mT20">
            <el-col :span="12">
              <div class="fc-dark-grey-txt14 mb5">Remarks Options</div>
              <el-radio-group
                v-model="sectionSettingsEdit.remarkOption"
                class="mT10"
              >
                <el-radio label="all" class="fc-radio-btn">All</el-radio>
                <el-radio label="specific" class="fc-radio-btn"
                  >Specific</el-radio
                >
              </el-radio-group>
              <el-select
                v-if="sectionSettingsEdit.remarkOption === 'specific'"
                v-model="sectionSettingsEdit.remarkOptionValues"
                multiple
                collapse-tags
                placeholder="Select Values"
                class="fc-input-full-border-select2 fc-tag el-select-block width200px mT10"
              >
                <el-option
                  v-for="(option, index) in sectionSettingsEdit.options"
                  :label="option.name"
                  :key="index"
                  :value="String(option.name)"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="cancelSectionSettings"
          >CANCEL</el-button
        >
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="saveSectionSettings"
          >SAVE</el-button
        >
      </div>
    </el-dialog>
    <el-dialog
      :visible.sync="showTriggerContextEdit"
      :append-to-body="true"
      class="fc-dialog-center-container taskdialog"
    >
      <template slot="title">
        <div class="fw-bold">
          <img
            src="~assets/black-arrow-right.svg"
            @click="showTriggerContextEdit = false"
            class="rotate-180 mR10 pointer"
            width="15"
            height="15"
            style="vertical-align: bottom"
          />
          CUSTOMIZE TRIGGER
        </div>
      </template>
      <div class="height330 overflow-y-scroll pB50">
        <draggable :list="triggerContextEdit" data-parent="default">
          <div
            v-for="(context, draggableIndex) in triggerContextEdit"
            :key="draggableIndex"
            class="border-bottom10 pT10 pB10 taskhover-config pL20 pR20 visibility-visible-actions pointer"
          >
            <div
              v-bind:class="{
                'fc-dark-blue4-12': draggableIndex === 0,
                'fc-grey-text-input-label': draggableIndex !== 0,
                pB0: draggableIndex === 0,
              }"
            >
              {{ context.triggerName }}
            </div>
            <template v-if="draggableIndex !== 0">
              <el-checkbox v-model="context.isExecuteIfNotInTime"></el-checkbox>
              <label class="pm-label">Avoid if triggered before</label>
              <el-input
                v-bind:class="{
                  'hover-actions-view': !context.isExecuteIfNotInTime,
                }"
                type="number"
                v-model="context.executeIfNotInTime"
                class="fc-input-full-border2 width150px task-option-input fc-form-trigger-input fw-bold"
              >
                <template slot="append">days</template>
              </el-input>
            </template>
            <img
              v-if="draggableIndex === 0"
              src="~assets/drag-drap-grey.svg"
              class="fR mT10 visibility-hide-actions cursor-drag"
              style="margin-top: -15px"
            />
            <img
              v-else
              src="~assets/drag-drap-grey.svg"
              class="fR mT10 visibility-hide-actions cursor-drag"
            />
          </div>
        </draggable>
      </div>
      <div class="modal-dialog-footer clearboth">
        <el-button
          class="modal-btn-cancel"
          @click="showTriggerContextEdit = false"
          >CANCEL</el-button
        >
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="saveTriggerContextDialog"
          >SAVE</el-button
        >
      </div>
    </el-dialog>
    <space-asset-multi-chooser
      v-if="chooserVisibility"
      :hideBanner="true"
      @associate="associateResource"
      :visibility.sync="chooserVisibility"
      :showAsset="showAsset"
      :disable="true"
      :initialValues="initialValues"
      :filter="filter"
      :resourceType="resourceFilter"
    ></space-asset-multi-chooser>
    <space-asset-multi-chooser
      v-if="chooserVisibilityForTask"
      :hideBanner="true"
      @associate="associateResourceForTask"
      :visibility.sync="chooserVisibilityForTask"
      :showAsset="showAsset"
      :disable="true"
      :initialValues="initialValuesForTask"
      :filter="filter"
      :resourceType="resourceFilter"
    ></space-asset-multi-chooser>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import PMMixin from '@/mixins/PMMixin'
import Draggable from 'vuedraggable'
import { isEmpty, isArray } from '@facilio/utils/validation'

export default {
  mixins: [PMMixin],
  props: ['model'],
  components: {
    SpaceAssetMultiChooser,
    Draggable,
  },
  data() {
    return {
      taskPopover: {},
      sectionPopover: {},
      currentIndex: 0,
      currentTaskIndex: 0,
      chooserVisibility: false,
      chooserVisibilityForTask: false,
      showAsset: false,
      showSettings: false,
      inputType: null,
      showSectionSettings: false,
      sectionSettingsEdit: {},
      taskSettingsEdit: {},
      showTaskSettings: false,
      fetchedAssets: [],
      fetchedSpaces: [],
      categoryOptions: [
        {
          value: 'CURRENT_FLOOR',
          label: 'Current Floor',
        },
        {
          value: 'asset',
          label: 'Asset Categories',
        },
        {
          value: 'space',
          label: 'Space Categories',
        },
      ],
      resourceFilter: null,
      oldSectionLabel: '',
      oldTasklabel: '',
      triggerContextEdit: [],
      showTriggerContextEdit: false,
      woForms: [],
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSpaceCategory')
    this.$store.dispatch('loadBuildings')
  },
  computed: {
    ...mapState({
      assetcategory: state => state.assetCategory,
    }),
    ...mapGetters(['getSpaceCategoryPickList']),
    spacecategory() {
      return this.getSpaceCategoryPickList()
    },
    categoryOptionsForTasks() {
      let category = this.model.taskData.taskSections[this.currentIndex]
        .category
      let options = []
      if (!category) {
        return []
      }
      if (category[0] === 'CURRENT_FLOOR') {
        options = [
          {
            value: 'CURRENT_FLOOR',
            label: `Every Floor`,
          },
        ]
        if (this.fetchedAssets.length) {
          let opt = {
            value: 'asset',
            label: `Asset Categories Under ${this.model.woData.resourceLabel}`,
            children: [],
          }
          this.assetcategory.forEach(j => {
            if (this.fetchedAssets.includes(j.id)) {
              opt.children.push({ value: j.id, label: j.name })
            }
          })
          options.push(opt)
        }

        if (this.fetchedSpaces.length) {
          let opt = {
            value: 'space',
            label: `Space Categories Under ${this.model.woData.resourceLabel}`,
            children: [],
          }
          for (let j in this.spacecategory) {
            if (this.fetchedSpaces.includes(Number(j))) {
              opt.children.push({ value: j, label: this.spacecategory[j] })
            }
          }
          options.push(opt)
        }
        return options
      } else if (category[0] === 'CURRENT_ASSET') {
        // Old handling for assetCategory
        let assetCategory = this.assetcategory.find(
          i => i.id === Number(this.model.woData.assetCategoryId)
        )
        if (!isEmpty(assetCategory)) {
          options = [
            {
              value: 'CURRENT_ASSET',
              label: `Every ${assetCategory.name}`,
            },
          ]
        }
      } else if (category[0] === 'ALL_SITES') {
        // Handling for Site Level PM, When category[0] is ALL_SITES.
        options = [
          {
            value: 'ALL_SITES',
            label: `Every Site`,
          },
        ]
        if (this.fetchedAssets.length) {
          let assetLabel = `Asset Categories Under ${this.model.woData.resourceLabel}`
          let opt = {
            value: 'asset',
            label: assetLabel,
            children: [],
          }
          this.assetcategory.forEach(j => {
            if (this.fetchedAssets.includes(j.id)) {
              opt.children.push({ value: j.id, label: j.name })
            }
          })
          options.push(opt)
        }

        if (this.fetchedSpaces.length) {
          let spaceLabel = `Space Categories Under ${this.model.woData.resourceLabel}`
          let opt = {
            value: 'space',
            label: spaceLabel,
            children: [],
          }
          for (let j in this.spacecategory) {
            if (this.fetchedSpaces.includes(Number(j))) {
              opt.children.push({ value: j, label: this.spacecategory[j] })
            }
          }
          options.push(opt)
        }
        return options
      } else if (category[0] === 'CURRENT_SPACE') {
        let currentSpaceLabel = `Every ${
          this.spacecategory[this.model.woData.spacecategoryId]
        }`
        if (this.model.woData.workOrderType === 'single') {
          currentSpaceLabel = this.model.woData.spaceAssetDisplayName
        }
        options = [
          {
            value: 'CURRENT_SPACE',
            label: currentSpaceLabel,
          },
        ]
        if (this.fetchedAssets.length) {
          let assetLabel = `Asset Categories Under ${this.model.woData.resourceLabel}`
          if (this.model.woData.workOrderType === 'single') {
            assetLabel = `Asset Categories Under ${this.model.woData.spaceAssetDisplayName}`
          }
          let opt = {
            value: 'asset',
            label: assetLabel,
            children: [],
          }
          this.assetcategory.forEach(j => {
            if (this.fetchedAssets.includes(j.id)) {
              opt.children.push({ value: j.id, label: j.name })
            }
          })
          options.push(opt)
        }

        if (this.fetchedSpaces.length) {
          let spaceLabel = `Space Categories Under ${this.model.woData.resourceLabel}`
          if (this.model.woData.workOrderType === 'single') {
            spaceLabel = `Space Categories Under ${this.model.woData.spaceAssetDisplayName}`
          }
          let opt = {
            value: 'space',
            label: spaceLabel,
            children: [],
          }
          for (let j in this.spacecategory) {
            if (this.fetchedSpaces.includes(Number(j))) {
              opt.children.push({ value: j, label: this.spacecategory[j] })
            }
          }
          options.push(opt)
        }
        return options
      } else if (category[0] === 'space') {
        let spaceCateg = this.spacecategory[Number(category[1])]
        let label = `Every ${spaceCateg}`
        options = [
          {
            value: 'CURRENT_SPACE',
            label: label,
          },
          {
            value: 'asset',
            label: `Asset Categories Under ${label}`,
            children: [],
          },
          {
            value: 'space',
            label: `Space Categories Under ${label}`,
            children: [],
          },
        ]

        let includeIds = ''
        if (
          this.model.taskData.taskSections[this.currentIndex]
            .selectedResourceList.length
        ) {
          this.model.taskData.taskSections[
            this.currentIndex
          ].selectedResourceList.forEach(x => {
            includeIds = includeIds + `&includeIds=${x.id}`
          })
        }
        let parentResourceIds = ''
        if (this.model.woData.resourceType === 'ALL_FLOORS') {
          if (this.model.woData.selectedFloorList.length) {
            this.model.woData.selectedFloorList.forEach(x => {
              parentResourceIds =
                parentResourceIds + `&parentResourceIds=${x.id}`
            })
          }
        } else if (this.model.woData.resourceType === 'BUILDINGS') {
          this.model.woData.resourceList.forEach(x => {
            parentResourceIds = parentResourceIds + `&parentResourceIds=${x.id}`
          })
        } else {
          if (this.model.woData.selectedSpaceList.length) {
            this.model.woData.selectedSpaceList.forEach(x => {
              parentResourceIds =
                parentResourceIds + `&parentResourceIds=${x.id}`
            })
          }
        }
        let buildingId = ''
        if (this.model.woData.selectedBuilding) {
          buildingId = `buildingId=${this.model.woData.selectedBuilding}&`
        }

        let siteParams
        if (this.model.woData.sites.length === 1) {
          siteParams = `siteIds=${this.model.woData.sites[0]}`
        } else {
          siteParams = `siteIds=${this.model.woData.sites[0]}`
          for (let i = 1; i < this.model.woData.sites.length; i++) {
            siteParams = siteParams + `&siteIds=${this.model.woData.sites[i]}`
          }
        }
        let parentAssignmentType
        if (this.model.woData.resourceType === 'ALL_FLOORS') {
          parentAssignmentType = 1
        } else if (this.model.woData.resourceType === 'BUILDINGS') {
          parentAssignmentType = 7
        } else {
          parentAssignmentType = 3
        }
        this.$http
          .get(
            `/workorder/getScopeFilteredValuesForPM?${buildingId}${siteParams}&spaceCategoryId=${Number(
              category[1]
            )}${parentResourceIds}&parentAssignmentType=${parentAssignmentType}&assignmentType=3${includeIds}`
          )
          .then(response => {
            for (let i = options.length - 1; i >= 1; i--) {
              if (options[i].value === 'asset') {
                if (response.data.assetCategoryIds.length) {
                  options[i].children = []
                  this.assetcategory.forEach(j => {
                    if (response.data.assetCategoryIds.includes(j.id)) {
                      options[i].children.push({ value: j.id, label: j.name })
                    }
                  })
                } else {
                  options.splice(i, 1)
                }
              } else if (options[i].value === 'space') {
                if (response.data.spaceCategoryIds.length) {
                  options[i].children = []
                  for (let j in this.spacecategory) {
                    if (response.data.spaceCategoryIds.includes(Number(j))) {
                      options[i].children.push({
                        value: j,
                        label: this.spacecategory[j],
                      })
                    }
                  }
                } else {
                  options.splice(i, 1)
                }
              }
            }
          })
        return options
      } else if (category[0] === 'CURRENT_BUILDING') {
        let currentSpaceLabel = `Every Building`
        options = [
          {
            value: 'CURRENT_SPACE',
            label: currentSpaceLabel,
          },
        ]
        if (this.fetchedAssets.length) {
          let assetLabel = `Asset Categories Under ${this.model.woData.resourceLabel}`
          let opt = {
            value: 'asset',
            label: assetLabel,
            children: [],
          }
          this.assetcategory.forEach(j => {
            if (this.fetchedAssets.includes(j.id)) {
              opt.children.push({ value: j.id, label: j.name })
            }
          })
          options.push(opt)
        }

        if (this.fetchedSpaces.length) {
          let spaceLabel = `Space Categories Under ${this.model.woData.resourceLabel}`
          let opt = {
            value: 'space',
            label: spaceLabel,
            children: [],
          }
          for (let j in this.spacecategory) {
            if (this.fetchedSpaces.includes(Number(j))) {
              opt.children.push({ value: j, label: this.spacecategory[j] })
            }
          }
          options.push(opt)
        }
        return options
      } else if (category[0] === 'asset') {
        let assetCateg = this.assetcategory.find(
          i => i.id === Number(category[1])
        )
        let label = `Every ${assetCateg.name}`
        options = [
          {
            value: 'CURRENT_ASSET',
            label: label,
          },
        ]
      }

      options.forEach(i => {
        if (i.value === 'asset') {
          i.children = []
          this.assetcategory.forEach(j => {
            i.children.push({ value: j.id, label: j.name })
          })
        } else if (i.value === 'space') {
          i.children = []
          for (let j in this.spacecategory) {
            i.children.push({ value: j, label: this.spacecategory[j] })
          }
        }
      })
      return options
    },
    filter() {
      let filter = {}
      if (this.model.woData.sites && this.model.woData.sites.length === 1) {
        filter.site = Number(this.model.woData.sites[0])
      }
      if (this.model.woData.selectedBuilding) {
        filter.buildingId = Number(this.model.woData.selectedBuilding)
      }
      return filter
    },
    buildingList() {
      let buildings = []
      if (this.$store.getters.getBuildings && this.model.woData.sites) {
        for (let i = 0; i < this.model.woData.sites.length; i++) {
          for (let j = 0; j < this.$store.getters.getBuildings.length; j++) {
            if (
              this.$store.getters.getBuildings[j].siteId ===
              this.model.woData.sites[i]
            ) {
              buildings.push(this.$store.getters.getBuildings[j])
            }
          }
        }
      }
      return buildings
    },
    currentCategory() {
      if (this.model.taskData.taskSections[this.currentIndex]) {
        return this.model.taskData.taskSections[this.currentIndex].category
      }
      return null
    },
    currentTaskCategory() {
      if (
        this.model.taskData.taskSections[this.currentIndex] &&
        this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ]
      ) {
        return this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].category
      }
      return null
    },
    initialValuesForTask() {
      let category = null
      if (this.currentTaskCategory) {
        category = this.currentTaskCategory
      } else if (this.currentCategory) {
        category = this.currentCategory
      }
      if (category) {
        if (category[0] === 'asset') {
          return {
            assetCategory: Number(category[1]),
            isIncludeResource: this.model.taskData.taskSections[
              this.currentIndex
            ].tasks[this.currentTaskIndex].isIncludeResource,
            selectedResources: this.model.taskData.taskSections[
              this.currentIndex
            ].tasks[this.currentTaskIndex].selectedResourceList.map(
              resource => ({
                id:
                  resource && typeof resource === 'object'
                    ? resource.id
                    : resource,
              })
            ),
          }
        } else if (category[0] === 'space') {
          return {
            spaceCategory: Number(category[1]),
            isIncludeResource: this.model.taskData.taskSections[
              this.currentIndex
            ].tasks[this.currentTaskIndex].isIncludeResource,
            selectedResources: this.model.taskData.taskSections[
              this.currentIndex
            ].tasks[this.currentTaskIndex].selectedResourceList.map(
              resource => ({
                id:
                  resource && typeof resource === 'object'
                    ? resource.id
                    : resource,
              })
            ),
          }
        } else if (category[0] === 'CURRENT_FLOOR') {
          return {
            isIncludeResource: this.model.taskData.taskSections[
              this.currentIndex
            ].tasks[this.currentTaskIndex].isIncludeResource,
            selectedResources: this.model.taskData.taskSections[
              this.currentIndex
            ].tasks[this.currentTaskIndex].selectedResourceList.map(
              resource => ({
                id:
                  resource && typeof resource === 'object'
                    ? resource.id
                    : resource,
              })
            ),
          }
        } else if (category[0] === 'CURRENT_SPACE') {
          return {
            spaceCategory: Number(this.currentCategory[1]),
            isIncludeResource: this.model.taskData.taskSections[
              this.currentIndex
            ].tasks[this.currentTaskIndex].isIncludeResource,
            selectedResources: this.model.taskData.taskSections[
              this.currentIndex
            ].tasks[this.currentTaskIndex].selectedResourceList.map(
              resource => ({
                id:
                  resource && typeof resource === 'object'
                    ? resource.id
                    : resource,
              })
            ),
          }
        } else if (category[0] === 'CURRENT_ASSET') {
          return {
            assetCategory: Number(this.currentCategory[1]),
            isIncludeResource: this.model.taskData.taskSections[
              this.currentIndex
            ].tasks[this.currentTaskIndex].isIncludeResource,
            selectedResources: this.model.taskData.taskSections[
              this.currentIndex
            ].tasks[this.currentTaskIndex].selectedResourceList.map(
              resource => ({
                id:
                  resource && typeof resource === 'object'
                    ? resource.id
                    : resource,
              })
            ),
          }
        }
      }
      return {
        isIncludeResource: null,
        selectedResources: [],
      }
    },
    initialValues() {
      let category = this.currentCategory
      if (category) {
        if (category[0] === 'asset') {
          return {
            assetCategory: Number(category[1]),
            isIncludeResource: this.model.taskData.taskSections[
              this.currentIndex
            ].isIncludeResource,
            selectedResources: this.model.taskData.taskSections[
              this.currentIndex
            ].selectedResourceList.map(resource => ({
              id:
                resource && typeof resource === 'object'
                  ? resource.id
                  : resource,
            })),
          }
        } else if (category[0] === 'space') {
          return {
            spaceCategory: Number(category[1]),
            isIncludeResource: this.model.taskData.taskSections[
              this.currentIndex
            ].isIncludeResource,
            selectedResources: this.model.taskData.taskSections[
              this.currentIndex
            ].selectedResourceList.map(resource => ({
              id:
                resource && typeof resource === 'object'
                  ? resource.id
                  : resource,
            })),
          }
        } else if (category[0] === 'CURRENT_FLOOR') {
          return {
            isIncludeResource: this.model.taskData.taskSections[
              this.currentIndex
            ].isIncludeResource,
            selectedResources: this.model.taskData.taskSections[
              this.currentIndex
            ].selectedResourceList.map(resource => ({
              id:
                resource && typeof resource === 'object'
                  ? resource.id
                  : resource,
            })),
          }
        } else if (category[0] === 'CURRENT_SPACE') {
          if (
            this.model.woData.workOrderType === 'single' &&
            this.model.woData.singleResource &&
            this.model.woData.singleResource.id &&
            this.model.woData.singleResource.resourceTypeEnum === 'SPACE'
          ) {
            return {
              isIncludeResource: this.model.taskData.taskSections[
                this.currentIndex
              ].isIncludeResource,
              selectedResources: this.model.taskData.taskSections[
                this.currentIndex
              ].selectedResourceList.map(resource => ({
                id:
                  resource && typeof resource === 'object'
                    ? resource.id
                    : resource,
              })),
            }
          }
          return {
            spaceCategory: Number(this.model.woData.spacecategoryId),
            isIncludeResource: this.model.taskData.taskSections[
              this.currentIndex
            ].isIncludeResource,
            selectedResources: this.model.taskData.taskSections[
              this.currentIndex
            ].selectedResourceList.map(resource => ({
              id:
                resource && typeof resource === 'object'
                  ? resource.id
                  : resource,
            })),
          }
        } else if (category[0] === 'CURRENT_ASSET') {
          return {
            assetCategory: Number(this.model.woData.assetCategoryId),
            isIncludeResource: this.model.taskData.taskSections[
              this.currentIndex
            ].isIncludeResource,
            selectedResources: this.model.taskData.taskSections[
              this.currentIndex
            ].selectedResourceList.map(resource => ({
              id:
                resource && typeof resource === 'object'
                  ? resource.id
                  : resource,
            })),
          }
        }
      }
      return {
        isIncludeResource: null,
        selectedResources: [],
      }
    },
  },
  watch: {
    'model.woData.singleResource': {
      handler: function(newVal, oldVal) {
        this.handleSingleResource(newVal, oldVal)
      },
      deep: true,
    },
    'model.woData.workOrderType': {
      handler: function(newVal, oldVal) {
        this.handleSingleResource(newVal, oldVal)
      },
      deep: true,
    },
    'model.woData.selectedBuildingList': {
      handler: function(newVal, oldVal) {
        this.handleWOChanges(newVal, oldVal)
      },
      deep: true,
    },
    'model.woData.selectedFloorList': {
      deep: true,
      handler: function(newVal, oldVal) {
        this.handleWOChanges(newVal, oldVal)
      },
    },
    'model.woData.selectedSpaceList': {
      deep: true,
      handler: function(newVal, oldVal) {
        this.handleWOChanges(newVal, oldVal)
      },
    },
    'model.woData.selectedResourceList': {
      deep: true,
      handler: function(newVal, oldVal) {
        //this.handleWOChanges(newVal, oldVal)
      },
    },
    'model.woData.spacecategoryId': {
      deep: true,
      handler: function(newVal, oldVal) {
        this.handleWOChanges(newVal, oldVal)
      },
    },
    'model.woData.assetCategoryId': {
      deep: true,
      handler: function(newVal, oldVal) {
        this.handleWOChanges(newVal, oldVal)
      },
    },
    'model.woData.resourceType': {
      handler: function(newVal, oldVal) {
        this.handleWOChanges(newVal, oldVal)
      },
      deep: true,
    },
  },
  mounted() {
    this.categoryOptions.forEach(i => {
      if (i.value === 'asset') {
        i.children = []

        if (!isEmpty(this.assetcategory)) {
          this.assetcategory.forEach(j => {
            i.children.push({ value: j.id, label: j.name })
          })
        }
      } else if (i.value === 'space') {
        i.children = []
        for (let j in this.spacecategory) {
          i.children.push({ value: j, label: this.spacecategory[j] })
        }
      }
    })
    if (this.model.isEdit) {
      this.handleMount()
    }
    this.loadWoForms()
  },
  methods: {
    triggerOptionHandler(val) {
      // when the options are emptied set all as default
      if (!val || !val.length) {
        this.sectionSettingsEdit.triggers = ['All']
        return
      }

      // when other option is selected, remove 'all'
      if (val.length > 1) {
        let index = this.sectionSettingsEdit.triggers.findIndex(
          i => i === 'All'
        )
        if (index > -1) {
          if (index === 0) {
            this.sectionSettingsEdit.triggers.splice(0, 1)
          } else {
            this.sectionSettingsEdit.triggers = ['All']
          }
        }
      }
      this.sectionSettingsEdit.triggerContextEdit = []
      this.sectionSettingsEdit.triggers.forEach(i => {
        if (i !== 'All') {
          this.sectionSettingsEdit.triggerContextEdit.push({
            executeIfNotInTime: -1,
            triggerName: i,
            isExecuteIfNotInTime: false,
          })
        }
      })
    },
    onTaskEnter($event, index, subject) {
      if (!$event.target || $event.target.selectionStart !== 0 || !subject) {
        index++
      }
      this.addNewTask(index)
    },
    beforeChange(val) {
      if (val) {
        this.oldSectionLabel = this.model.taskData.taskSections[
          this.currentIndex
        ].resourceLabel
      }
    },
    beforeTaskChange(val) {
      if (val) {
        this.oldTasklabel = this.model.taskData.taskSections[
          this.currentIndex
        ].tasks[this.currentTaskIndex].resourceLabel
      }
    },
    handleTaskSelectLabel({ index, taskIndex, categ }) {
      this.model.taskData.taskSections[index].tasks[taskIndex].category = categ
      let selectedDefault =
        categ[0] === 'CURRENT_FLOOR' ||
        categ[0] === 'CURRENT_ASSET' ||
        categ[0] === 'CURRENT_SPACE'
      this.changeTaskResourceLabel(selectedDefault)
    },
    handleSelectLabel({ index, categ }) {
      this.model.taskData.taskSections[index].category = categ
      let selectedDefault =
        categ[0] === 'CURRENT_FLOOR' ||
        categ[0] === 'CURRENT_ASSET' ||
        categ[0] === 'CURRENT_SPACE'
      this.changeLabel(index, selectedDefault)
    },
    handleTaskSearchSelect(sectionIndex, taskIndex, category) {
      let isChanged =
        JSON.stringify(
          this.model.taskData.taskSections[sectionIndex].tasks[taskIndex]
            .category
        ) !== JSON.stringify(category)
      this.model.taskData.taskSections[sectionIndex].tasks[
        taskIndex
      ].category = category
      if (category.length === 1) {
        if (category[0] === 'CURRENT_FLOOR') {
          this.showAsset = false
          this.resourceFilter = [3]
          this.chooserVisibilityForTask = true
        } else if (category[0] === 'CURRENT_ASSET') {
          this.showAsset = true
          this.resourceFilter = null
          this.chooserVisibilityForTask = true
        } else if (category[0] === 'ALL_SITES') {
          // for site level pm - making showAsset false so that task filter dialog box shows all the resources and not only assets.
          this.showAsset = false
          this.resourceFilter = null
          this.chooserVisibilityForTask = true
        } else if (category[0] === 'CURRENT_SPACE') {
          this.showAsset = false
          this.resourceFilter = null
          this.chooserVisibilityForTask = true
        }
      } else {
        if (category[0] === 'space') {
          this.showAsset = false
          this.resourceFilter = null
          this.chooserVisibilityForTask = true
        } else if (category[0] === 'asset') {
          this.showAsset = true
          this.resourceFilter = null
          this.chooserVisibilityForTask = true
        }
      }
      if (isChanged) {
        let selectedDefault =
          category[0] === 'CURRENT_FLOOR' ||
          category[0] === 'CURRENT_ASSET' ||
          category[0] === 'CURRENT_SPACE'
        this.changeTaskResourceLabel(selectedDefault)
      }
    },
    handleSearchSelect(sectionIndex, category) {
      let isChanged =
        JSON.stringify(
          this.model.taskData.taskSections[sectionIndex].category
        ) !== JSON.stringify(category)
      this.model.taskData.taskSections[sectionIndex].category = category
      if (category.length === 1) {
        if (category[0] === 'CURRENT_FLOOR') {
          this.showAsset = false
          this.resourceFilter = [3]
          this.chooserVisibility = true
        } else if (category[0] === 'CURRENT_ASSET') {
          this.showAsset = true
          this.resourceFilter = null
          this.chooserVisibility = true
        } else if (category[0] === 'CURRENT_SPACE') {
          this.showAsset = false
          this.resourceFilter = null
          this.chooserVisibility = true
        } else if (category[0] === 'ALL_SITES') {
          // for site level pm - making showAsset false so that task filter dialog box shows all the resources and not only assets.
          this.showAsset = false
          this.resourceFilter = null
          this.chooserVisibilityForTask = true
        }
      } else {
        if (category[0] === 'space') {
          this.showAsset = false
          this.resourceFilter = null
          this.chooserVisibility = true
        } else if (category[0] === 'asset') {
          this.showAsset = true
          this.resourceFilter = null
          this.chooserVisibility = true
        }
      }
      if (isChanged) {
        let selectedDefault =
          category[0] === 'CURRENT_FLOOR' ||
          category[0] === 'CURRENT_ASSET' ||
          category[0] === 'CURRENT_SPACE'
        this.changeLabel(sectionIndex, selectedDefault)
      }
    },
    handleMount() {
      let resourceType = this.model.woData.resourceType
      this.model.taskData.taskSections.forEach((i, index) => {
        let category = this.model.taskData.taskSections[index].category
        let setDefaultName = false
        if (resourceType === 'ALL_FLOORS' && category[0] === 'CURRENT_FLOOR') {
          setDefaultName = true
        } else if (
          resourceType === 'SPACE_CATEGORY' &&
          category[0] === 'CURRENT_SPACE'
        ) {
          setDefaultName = true
        } else if (
          resourceType === 'ASSET_CATEGORY' &&
          category[0] === 'CURRENT_ASSET'
        ) {
          setDefaultName = true
        }

        this.model.taskData.taskSections[
          index
        ].resourceLabel = this.resourceLabel(index, setDefaultName)
        if (category[0] === 'CURRENT_FLOOR') {
          this.handleFloorCategoryOptions()
        } else if (category[0] === 'CURRENT_SPACE') {
          this.handleSpaceCategoryOptions(
            this.model.woData.workOrderType === 'single'
          )
        }

        if (resourceType === 'ALL_FLOORS') {
          this.categoryOptions[0] = {
            value: 'CURRENT_FLOOR',
            label: `Every Floor`,
          }
        } else if (resourceType === 'SPACE_CATEGORY') {
          let spacecategory = this.spacecategory[
            this.model.woData.spacecategoryId
          ]
          this.categoryOptions[0] = {
            value: 'CURRENT_SPACE',
            label: `Every ${spacecategory}`,
          }
        } else if (resourceType === 'ASSET_CATEGORY') {
          let assetCategory = this.assetcategory.find(
            i => i.id === Number(this.model.woData.assetCategoryId)
          )
          this.categoryOptions[0] = {
            value: 'CURRENT_ASSET',
            label: `Every ${assetCategory.name}`,
          }
          if (this.categoryOptions.length === 3) {
            this.categoryOptions.splice(1, 1)
            this.categoryOptions.splice(1, 1)
          } else if (this.categoryOptions.length === 2) {
            this.categoryOptions.splice(1, 1)
          }
        } else if (
          this.model.woData.workOrderType === 'single' &&
          this.model.woData.singleResource.resourceTypeEnum === 'SPACE'
        ) {
          this.categoryOptions[0] = {
            value: 'CURRENT_SPACE',
            label: `${this.model.woData.spaceAssetDisplayName}`,
          }
          this.handleSpaceCategoryOptions(
            this.model.woData.workOrderType === 'single'
          )
        }
        // change
        this.model.taskData.taskSections[index].tasks.forEach(
          (task, taskIndex) => {
            /* if (category[0] === 'CURRENT_FLOOR') {
              task.category = ['CURRENT_FLOOR']
            }
            else if (category[0] === 'CURRENT_SPACE' || category[0] === 'space') {
              task.category = ['CURRENT_SPACE']
            }
            else if (category[0] === 'CURRENT_ASSET' || category[0] === 'asset') {
              task.category = ['CURRENT_ASSET']
            } */
            let setDefaultTaskName = false
            if (
              task.category[0] === 'CURRENT_ASSET' ||
              task.category[0] === 'CURRENT_SPACE' ||
              task.category[0] === 'CURRENT_FLOOR'
            ) {
              setDefaultTaskName = true
            }
            task.resourceLabel = this.resourceLabelForTask(
              index,
              taskIndex,
              setDefaultTaskName
            )
          }
        )
        this.$forceUpdate()
      })
    },
    forceUpdate() {
      let temp = this.$helpers.cloneObject(this.triggerContextEdit)
      while (this.triggerContextEdit.length > 0) {
        this.triggerContextEdit.pop()
      }
      while (temp.length > 0) {
        this.triggerContextEdit.push(temp.shift())
      }
    },
    handleFloorCategoryOptions() {
      if (!this.model.woData.selectedBuilding) {
        return
      }
      if (this.categoryOptions.length < 3) {
        this.categoryOptions.push({
          value: 'asset',
          label: `Asset Categories Under ${this.model.woData.resourceLabel}`,
          children: [],
        })
        this.categoryOptions.push({
          value: 'space',
          label: `Space Categories Under ${this.model.woData.resourceLabel}`,
          children: [],
        })
        let includeIds = ''
        if (this.model.woData.selectedFloorList.length) {
          this.model.woData.selectedFloorList.forEach(x => {
            includeIds = includeIds + `&includeIds=${x.id}`
          })
        }
        let buildingId = ''
        if (this.model.woData.selectedBuilding) {
          buildingId = `buildingId=${this.model.woData.selectedBuilding}&`
        }
        let siteParams
        if (this.model.woData.sites.length === 1) {
          siteParams = `siteIds=${this.model.woData.sites[0]}`
        } else {
          siteParams = `siteIds=${this.model.woData.sites[0]}`
          for (let i = 1; i < this.model.woData.sites.length; i++) {
            siteParams = siteParams + `&siteIds=${this.model.woData.sites[i]}`
          }
        }
        this.$http
          .get(
            `/workorder/getScopeFilteredValuesForPM?${buildingId}${siteParams}&assignmentType=1${includeIds}`
          )
          .then(response => {
            this.fetchedAssets = response.data.assetCategoryIds
            this.fetchedSpaces = response.data.spaceCategoryIds
            let assetFilled = false
            let spaceFilled = false
            let toDelete = []
            for (let i = this.categoryOptions.length - 1; i >= 1; i--) {
              if (this.categoryOptions[i].value === 'asset') {
                if (!assetFilled) {
                  if (this.fetchedAssets.length) {
                    this.categoryOptions[i].children = []
                    this.assetcategory.forEach(j => {
                      if (this.fetchedAssets.includes(j.id)) {
                        this.categoryOptions[i].children.push({
                          value: j.id,
                          label: j.name,
                        })
                      }
                    })
                  } else {
                    toDelete.push(i)
                  }
                  assetFilled = true
                } else {
                  toDelete.push(i)
                }
              } else if (this.categoryOptions[i].value === 'space') {
                if (!spaceFilled) {
                  if (this.fetchedSpaces.length) {
                    this.categoryOptions[i].children = []
                    for (let j in this.spacecategory) {
                      if (this.fetchedSpaces.includes(Number(j))) {
                        this.categoryOptions[i].children.push({
                          value: j,
                          label: this.spacecategory[j],
                        })
                      }
                    }
                  } else {
                    toDelete.push(i)
                  }
                  spaceFilled = true
                } else {
                  toDelete.push(i)
                }
              }
            }
            toDelete.forEach(n => this.categoryOptions.splice(n, 1))
          })
      } else {
        let includeIds = ''
        if (this.model.woData.selectedFloorList.length) {
          this.model.woData.selectedFloorList.forEach(x => {
            includeIds = includeIds + `&includeIds=${x.id}`
          })
        }
        let buildingId = ''
        if (this.model.woData.selectedBuilding) {
          buildingId = `buildingId=${this.model.woData.selectedBuilding}&`
        }
        let siteParams
        if (this.model.woData.sites.length === 1) {
          siteParams = `siteIds=${this.model.woData.sites[0]}`
        } else {
          siteParams = `siteIds=${this.model.woData.sites[0]}`
          for (let i = 1; i < this.model.woData.sites.length; i++) {
            siteParams = siteParams + `&siteIds=${this.model.woData.sites[i]}`
          }
        }
        this.$http
          .get(
            `/workorder/getScopeFilteredValuesForPM?${buildingId}${siteParams}&assignmentType=1${includeIds}`
          )
          .then(response => {
            this.fetchedAssets = response.data.assetCategoryIds
            this.fetchedSpaces = response.data.spaceCategoryIds
            let assetFilled = false
            let spaceFilled = false
            let toDelete = []
            for (let i = this.categoryOptions.length - 1; i >= 1; i--) {
              if (this.categoryOptions[i].value === 'asset') {
                if (!assetFilled) {
                  if (this.fetchedAssets.length) {
                    this.categoryOptions[
                      i
                    ].label = `Asset Categories Under ${this.model.woData.resourceLabel}`
                    this.categoryOptions[i].children = []
                    this.assetcategory.forEach(j => {
                      if (this.fetchedAssets.includes(j.id)) {
                        this.categoryOptions[i].children.push({
                          value: j.id,
                          label: j.name,
                        })
                      }
                    })
                  } else {
                    toDelete.push(i)
                  }
                  assetFilled = true
                } else {
                  toDelete.push(i)
                }
              } else if (this.categoryOptions[i].value === 'space') {
                if (!spaceFilled) {
                  if (this.fetchedSpaces.length) {
                    this.categoryOptions[
                      i
                    ].label = `Space Categories Under ${this.model.woData.resourceLabel}`
                    this.categoryOptions[i].children = []
                    for (let j in this.spacecategory) {
                      if (this.fetchedSpaces.includes(Number(j))) {
                        this.categoryOptions[i].children.push({
                          value: j,
                          label: this.spacecategory[j],
                        })
                      }
                    }
                  } else {
                    toDelete.push(i)
                  }
                  spaceFilled = true
                } else {
                  toDelete.push(i)
                }
              }
            }
            toDelete.forEach(n => this.categoryOptions.splice(n, 1))
          })
      }
    },
    handleAllBuildingsOptions() {
      let hasAsset = false
      let hasSpace = false
      this.categoryOptions.forEach(x => {
        if (x.value === 'asset') {
          hasAsset = true
        } else if (x.value === 'space') {
          hasSpace = true
        }
      })
      if (this.categoryOptions.length < 3) {
        if (!hasAsset) {
          this.categoryOptions.push({
            value: 'asset',
            label: `Asset Categories Under ${this.model.woData.resourceLabel}`,
            children: [],
          })
        }

        if (!hasSpace) {
          this.categoryOptions.push({
            value: 'space',
            label: `Space Categories Under ${this.model.woData.resourceLabel}`,
            children: [],
          })
        }

        let url = ``
        let siteParams
        if (this.model.woData.sites.length === 1) {
          siteParams = `siteIds=${this.model.woData.sites[0]}`
        } else {
          siteParams = `siteIds=${this.model.woData.sites[0]}`
          for (let i = 1; i < this.model.woData.sites.length; i++) {
            siteParams = siteParams + `&siteIds=${this.model.woData.sites[i]}`
          }
        }

        let buildingId = ''
        if (
          this.model.woData.selectedBuildingList &&
          this.model.woData.selectedBuildingList.length
        ) {
          if (this.model.woData.selectedBuildingList.length === 1) {
            buildingId = `buildingIds=${this.model.woData.selectedBuildingList[0]}&`
          } else {
            buildingId = `buildingIds=${this.model.woData.selectedBuildingList[0]}&`
            for (
              let i = 1;
              i < this.model.woData.selectedBuildingList.length;
              i++
            ) {
              buildingId =
                buildingId +
                `buildingIds=${this.model.woData.selectedBuildingList[i]}&`
            }
          }
        }
        url = `/workorder/getScopeFilteredValuesForPM?${buildingId}${siteParams}&assignmentType=7`
        this.$http.get(url).then(response => {
          this.fetchedAssets = response.data.assetCategoryIds || []
          this.fetchedSpaces = response.data.spaceCategoryIds || []

          for (let i = this.categoryOptions.length - 1; i >= 1; i--) {
            if (this.categoryOptions[i].value === 'asset') {
              if (this.fetchedAssets.length) {
                this.categoryOptions[i].children = []
                this.assetcategory.forEach(j => {
                  if (this.fetchedAssets.includes(j.id)) {
                    this.categoryOptions[i].children.push({
                      value: j.id,
                      label: j.name,
                    })
                  }
                })
              } else {
                this.categoryOptions.splice(i, 1)
              }
            } else if (this.categoryOptions[i].value === 'space') {
              if (this.fetchedSpaces.length) {
                this.categoryOptions[i].children = []
                for (let j in this.spacecategory) {
                  if (this.fetchedSpaces.includes(Number(j))) {
                    this.categoryOptions[i].children.push({
                      value: j,
                      label: this.spacecategory[j],
                    })
                  }
                }
              } else {
                this.categoryOptions.splice(i, 1)
              }
            }
          }
        })
      } else {
        let url = ``
        let siteParams
        if (this.model.woData.sites.length === 1) {
          siteParams = `siteIds=${this.model.woData.sites[0]}`
        } else {
          siteParams = `siteIds=${this.model.woData.sites[0]}`
          for (let i = 1; i < this.model.woData.sites.length; i++) {
            siteParams = siteParams + `&siteIds=${this.model.woData.sites[i]}`
          }
        }
        let buildingId = ''
        if (
          this.model.woData.selectedBuildingList &&
          this.model.woData.selectedBuildingList.length
        ) {
          if (this.model.woData.selectedBuildingList.length === 1) {
            buildingId = `buildingIds=${this.model.woData.selectedBuildingList[0]}&`
          } else {
            buildingId = `buildingIds=${this.model.woData.selectedBuildingList[0]}&`
            for (
              let i = 1;
              i < this.model.woData.selectedBuildingList.length;
              i++
            ) {
              buildingId =
                buildingId +
                `buildingIds=${this.model.woData.selectedBuildingList[i]}&`
            }
          }
        }
        url = `/workorder/getScopeFilteredValuesForPM?${buildingId}${siteParams}&assignmentType=7`

        this.$http.get(url).then(response => {
          this.fetchedAssets = response.data.assetCategoryIds || []
          this.fetchedSpaces = response.data.spaceCategoryIds || []
          for (let i = this.categoryOptions.length - 1; i >= 1; i--) {
            if (this.categoryOptions[i].value === 'asset') {
              if (this.fetchedAssets.length) {
                this.categoryOptions[
                  i
                ].label = `Asset Categories Under ${this.model.woData.resourceLabel}`
                this.categoryOptions[i].children = []
                this.assetcategory.forEach(j => {
                  if (this.fetchedAssets.includes(j.id)) {
                    this.categoryOptions[i].children.push({
                      value: j.id,
                      label: j.name,
                    })
                  }
                })
              } else {
                this.categoryOptions.splice(i, 1)
              }
            } else if (this.categoryOptions[i].value === 'space') {
              if (this.fetchedSpaces.length) {
                this.categoryOptions[
                  i
                ].label = `Space Categories Under ${this.model.woData.resourceLabel}`

                this.categoryOptions[i].children = []
                for (let j in this.spacecategory) {
                  if (this.fetchedSpaces.includes(Number(j))) {
                    this.categoryOptions[i].children.push({
                      value: j,
                      label: this.spacecategory[j],
                    })
                  }
                }
              } else {
                this.categoryOptions.splice(i, 1)
              }
            }
          }
        })
      }
    },
    handleSpaceCategoryOptions(isSingleWo) {
      let hasAsset = false
      let hasSpace = false
      this.categoryOptions.forEach(x => {
        if (x.value === 'asset') {
          hasAsset = true
        } else if (x.value === 'space') {
          hasSpace = true
        }
      })
      if (this.categoryOptions.length < 3) {
        if (isSingleWo) {
          if (!hasAsset) {
            this.categoryOptions.push({
              value: 'asset',
              label: `Asset Categories Under ${this.model.woData.spaceAssetDisplayName}`,
              children: [],
            })
          }

          if (!hasSpace) {
            this.categoryOptions.push({
              value: 'space',
              label: `Space Categories Under ${this.model.woData.spaceAssetDisplayName}`,
              children: [],
            })
          }
        } else {
          if (!hasAsset) {
            this.categoryOptions.push({
              value: 'asset',
              label: `Asset Categories Under ${this.model.woData.resourceLabel}`,
              children: [],
            })
          }

          if (!hasSpace) {
            this.categoryOptions.push({
              value: 'space',
              label: `Space Categories Under ${this.model.woData.resourceLabel}`,
              children: [],
            })
          }
        }

        let includeIds = ''
        if (isSingleWo) {
          includeIds = `&includeIds=${this.model.woData.singleResource.id}`
        } else {
          if (this.model.woData.selectedSpaceList.length) {
            this.model.woData.selectedSpaceList.forEach(x => {
              includeIds = includeIds + `&includeIds=${x.id}`
            })
          }
        }

        let url = ``
        let siteParams
        if (this.model.woData.sites.length === 1) {
          siteParams = `siteIds=${this.model.woData.sites[0]}`
        } else {
          siteParams = `siteIds=${this.model.woData.sites[0]}`
          for (let i = 1; i < this.model.woData.sites.length; i++) {
            siteParams = siteParams + `&siteIds=${this.model.woData.sites[i]}`
          }
        }
        if (isSingleWo) {
          url = `/workorder/getScopeFilteredValuesForPM?siteId=${this.model.woData.woModel.site.id}&assignmentType=3${includeIds}`
        } else {
          let buildingId = ''
          if (this.model.woData.selectedBuilding) {
            buildingId = `buildingId=${this.model.woData.selectedBuilding}&`
          }
          url = `/workorder/getScopeFilteredValuesForPM?${buildingId}${siteParams}&spaceCategoryId=${this.model.woData.spacecategoryId}&assignmentType=3${includeIds}`
        }
        this.$http.get(url).then(response => {
          this.fetchedAssets = response.data.assetCategoryIds
          this.fetchedSpaces = response.data.spaceCategoryIds

          for (let i = this.categoryOptions.length - 1; i >= 1; i--) {
            if (this.categoryOptions[i].value === 'asset') {
              if (this.fetchedAssets.length) {
                this.categoryOptions[i].children = []
                this.assetcategory.forEach(j => {
                  if (this.fetchedAssets.includes(j.id)) {
                    this.categoryOptions[i].children.push({
                      value: j.id,
                      label: j.name,
                    })
                  }
                })
              } else {
                this.categoryOptions.splice(i, 1)
              }
            } else if (this.categoryOptions[i].value === 'space') {
              if (this.fetchedSpaces.length) {
                this.categoryOptions[i].children = []
                for (let j in this.spacecategory) {
                  if (this.fetchedSpaces.includes(Number(j))) {
                    this.categoryOptions[i].children.push({
                      value: j,
                      label: this.spacecategory[j],
                    })
                  }
                }
              } else {
                this.categoryOptions.splice(i, 1)
              }
            }
          }
        })
      } else {
        let includeIds = ''
        if (isSingleWo) {
          includeIds = `&includeIds=${this.model.woData.singleResource.id}`
        } else {
          if (!this.model.woData.spacecategoryId) {
            return
          }
          if (this.model.woData.selectedSpaceList.length) {
            this.model.woData.selectedSpaceList.forEach(x => {
              includeIds = includeIds + `&includeIds=${x.id}`
            })
          }
        }

        let url = ``
        if (isSingleWo) {
          url = `/workorder/getScopeFilteredValuesForPM?siteId=${this.model.woData.woModel.site.id}&assignmentType=3${includeIds}`
        } else {
          let siteParams
          if (this.model.woData.sites.length === 1) {
            siteParams = `siteIds=${this.model.woData.sites[0]}`
          } else {
            siteParams = `siteIds=${this.model.woData.sites[0]}`
            for (let i = 1; i < this.model.woData.sites.length; i++) {
              siteParams = siteParams + `&siteIds=${this.model.woData.sites[i]}`
            }
          }
          let buildingId = ''
          if (this.model.woData.selectedBuilding) {
            buildingId = `buildingId=${this.model.woData.selectedBuilding}&`
          }
          url = `/workorder/getScopeFilteredValuesForPM?${buildingId}${siteParams}&spaceCategoryId=${this.model.woData.spacecategoryId}&assignmentType=3${includeIds}`
        }
        this.$http.get(url).then(response => {
          this.fetchedAssets = response.data.assetCategoryIds
          this.fetchedSpaces = response.data.spaceCategoryIds
          for (let i = this.categoryOptions.length - 1; i >= 1; i--) {
            if (this.categoryOptions[i].value === 'asset') {
              if (this.fetchedAssets.length) {
                if (isSingleWo) {
                  this.categoryOptions[
                    i
                  ].label = `Asset Categories Under ${this.model.woData.spaceAssetDisplayName}`
                } else {
                  this.categoryOptions[
                    i
                  ].label = `Asset Categories Under ${this.model.woData.resourceLabel}`
                }
                this.categoryOptions[i].children = []
                this.assetcategory.forEach(j => {
                  if (this.fetchedAssets.includes(j.id)) {
                    this.categoryOptions[i].children.push({
                      value: j.id,
                      label: j.name,
                    })
                  }
                })
              } else {
                this.categoryOptions.splice(i, 1)
              }
            } else if (this.categoryOptions[i].value === 'space') {
              if (this.fetchedSpaces.length) {
                if (isSingleWo) {
                  this.categoryOptions[
                    i
                  ].label = `Space Categories Under ${this.model.woData.spaceAssetDisplayName}`
                } else {
                  this.categoryOptions[
                    i
                  ].label = `Space Categories Under ${this.model.woData.resourceLabel}`
                }
                this.categoryOptions[i].children = []
                for (let j in this.spacecategory) {
                  if (this.fetchedSpaces.includes(Number(j))) {
                    this.categoryOptions[i].children.push({
                      value: j,
                      label: this.spacecategory[j],
                    })
                  }
                }
              } else {
                this.categoryOptions.splice(i, 1)
              }
            }
          }
        })
      }
    },
    handleAllSitesOptions() {
      let hasAsset = false
      let hasSpace = false
      let hasBuilding = false
      this.categoryOptions.forEach(x => {
        if (x.value === 'asset') {
          hasAsset = true
        } else if (x.value === 'space') {
          hasSpace = true
        } else if (x.value === 'building') {
          hasBuilding = true
        }
      })
      if (this.categoryOptions.length < 3) {
        if (!hasAsset) {
          this.categoryOptions.push({
            value: 'asset',
            label: `Asset Categories Under ${this.model.woData.resourceLabel}`,
            children: [],
          })
        }

        if (!hasSpace) {
          this.categoryOptions.push({
            value: 'space',
            label: `Space Categories Under ${this.model.woData.resourceLabel}`,
            children: [],
          })
        }

        let url = ``
        let siteParams
        if (this.model.woData.sites.length === 1) {
          siteParams = `siteIds=${this.model.woData.sites[0]}`
        } else {
          siteParams = `siteIds=${this.model.woData.sites[0]}`
          for (let i = 1; i < this.model.woData.sites.length; i++) {
            siteParams = siteParams + `&siteIds=${this.model.woData.sites[i]}`
          }
        }

        url = `/workorder/getScopeFilteredValuesForPM?${siteParams}&assignmentType=8`
        this.$http.get(url).then(response => {
          this.fetchedAssets = response.data.assetCategoryIds || []
          this.fetchedSpaces = response.data.spaceCategoryIds || []

          for (let i = this.categoryOptions.length - 1; i >= 1; i--) {
            if (this.categoryOptions[i].value === 'asset') {
              if (this.fetchedAssets.length) {
                this.categoryOptions[i].children = []
                this.assetcategory.forEach(j => {
                  if (this.fetchedAssets.includes(j.id)) {
                    this.categoryOptions[i].children.push({
                      value: j.id,
                      label: j.name,
                    })
                  }
                })
              } else {
                this.categoryOptions.splice(i, 1)
              }
            } else if (this.categoryOptions[i].value === 'space') {
              if (this.fetchedSpaces.length) {
                this.categoryOptions[i].children = []
                for (let j in this.spacecategory) {
                  if (this.fetchedSpaces.includes(Number(j))) {
                    this.categoryOptions[i].children.push({
                      value: j,
                      label: this.spacecategory[j],
                    })
                  }
                }
              } else {
                this.categoryOptions.splice(i, 1)
              }
            }
          }
        })
      } else {
        let url = ``
        let siteParams
        if (this.model.woData.sites.length === 1) {
          siteParams = `siteIds=${this.model.woData.sites[0]}`
        } else {
          siteParams = `siteIds=${this.model.woData.sites[0]}`
          for (let i = 1; i < this.model.woData.sites.length; i++) {
            siteParams = siteParams + `&siteIds=${this.model.woData.sites[i]}`
          }
        }

        url = `/workorder/getScopeFilteredValuesForPM?${siteParams}&assignmentType=8`

        this.$http.get(url).then(response => {
          this.fetchedAssets = response.data.assetCategoryIds || []
          this.fetchedSpaces = response.data.spaceCategoryIds || []
          for (let i = this.categoryOptions.length - 1; i >= 1; i--) {
            if (this.categoryOptions[i].value === 'asset') {
              if (this.fetchedAssets.length) {
                this.categoryOptions[
                  i
                ].label = `Asset Categories Under ${this.model.woData.resourceLabel}`
                this.categoryOptions[i].children = []
                this.assetcategory.forEach(j => {
                  if (this.fetchedAssets.includes(j.id)) {
                    this.categoryOptions[i].children.push({
                      value: j.id,
                      label: j.name,
                    })
                  }
                })
              } else {
                this.categoryOptions.splice(i, 1)
              }
            } else if (this.categoryOptions[i].value === 'space') {
              if (this.fetchedSpaces.length) {
                this.categoryOptions[
                  i
                ].label = `Space Categories Under ${this.model.woData.resourceLabel}`

                this.categoryOptions[i].children = []
                for (let j in this.spacecategory) {
                  if (this.fetchedSpaces.includes(Number(j))) {
                    this.categoryOptions[i].children.push({
                      value: j,
                      label: this.spacecategory[j],
                    })
                  }
                }
              } else {
                this.categoryOptions.splice(i, 1)
              }
            }
          }
        })
      }
    },
    handleWOChanges(newVal, oldVal) {
      if (newVal !== oldVal && newVal) {
        if (this.model.woData.resourceType === 'ALL_FLOORS') {
          this.categoryOptions[0] = {
            value: 'CURRENT_FLOOR',
            label: `Every Floor`,
          }
          this.model.taskData.taskSections.forEach((i, index) => {
            i.category = ['CURRENT_FLOOR']
            this.changeLabelForSection(index, true)
          })
          this.handleFloorCategoryOptions()
        } else if (this.model.woData.resourceType === 'SPACE_CATEGORY') {
          if (!this.model.woData.spacecategoryId) {
            return
          }
          let spacecategory = this.spacecategory[
            this.model.woData.spacecategoryId
          ]
          this.categoryOptions[0] = {
            value: 'CURRENT_SPACE',
            label: `Every ${spacecategory}`,
          }
          this.model.taskData.taskSections.forEach((i, index) => {
            i.category = ['CURRENT_SPACE']
            this.changeLabelForSection(index, true)
          })
          this.handleSpaceCategoryOptions()
        } else if (this.model.woData.resourceType === 'ASSET_CATEGORY') {
          let assetCategory = this.assetcategory.find(
            i => i.id === Number(this.model.woData.assetCategoryId)
          )
          if (!isEmpty(assetCategory)) {
            this.categoryOptions[0] = {
              value: 'CURRENT_ASSET',
              label: `Every ${assetCategory.name}`,
            }
            this.model.taskData.taskSections.forEach((i, index) => {
              i.category = ['CURRENT_ASSET']
              this.changeLabelForSection(index, true)
            })
            if (this.categoryOptions.length === 3) {
              this.categoryOptions.splice(2, 1)
              this.categoryOptions.splice(1, 1)
            } else if (this.categoryOptions.length === 2) {
              this.categoryOptions.splice(1, 1)
            }
          }
        } else if (this.model.woData.resourceType === 'BUILDINGS') {
          this.categoryOptions = [
            {
              value: 'CURRENT_BUILDING',
              label: `Every Building`,
            },
          ]

          this.model.taskData.taskSections.forEach((i, index) => {
            i.category = ['CURRENT_BUILDING']
            this.changeLabelForSection(index, true)
          })
          this.handleAllBuildingsOptions()
        } else if (this.model.woData.resourceType === 'ALL_SITES') {
          // Handling Site Level PM
          this.categoryOptions = [
            {
              value: 'ALL_SITES',
              label: `Every Sites`,
            },
          ]
          this.model.taskData.taskSections.forEach((i, index) => {
            i.category = ['ALL_SITES']
            this.changeLabelForSection(index, true)
          })
          this.handleAllSitesOptions()
        }
      }
    },
    showTaskSettingsDialog() {
      this.taskSettingsEdit = {
        attachmentRequired: false,
        enableInput: false,
        readingFieldId: null,
        validation: null,
        minSafeLimit: null,
        maxSafeLimit: null,
        inputType: null,
        options: [],
        description: '',
        name: '',
        defaultValue: '',
        failureValue: '',
        createWoOnFailure: false,
        woCreateFormId: null,
        deviationOperatorId: null,
        readingFields: [],
        remarkOption: 'all',
        remarkOptionValues: [],
        remarksRequired: false,
        attachmentOptionValues: [],
        attachmentOption: 'all',
      }
      this.taskSettingsEdit.name = this.model.taskData.taskSections[
        this.currentIndex
      ].tasks[this.currentTaskIndex].name
      this.taskSettingsEdit.description = this.model.taskData.taskSections[
        this.currentIndex
      ].tasks[this.currentTaskIndex].description
      this.taskSettingsEdit.attachmentRequired = this.model.taskData.taskSections[
        this.currentIndex
      ].tasks[this.currentTaskIndex].attachmentRequired
      this.taskSettingsEdit.enableInput = this.model.taskData.taskSections[
        this.currentIndex
      ].tasks[this.currentTaskIndex].enableInput
      this.taskSettingsEdit.readingFieldId = this.model.taskData.taskSections[
        this.currentIndex
      ].tasks[this.currentTaskIndex].readingFieldId
      this.taskSettingsEdit.validation = this.model.taskData.taskSections[
        this.currentIndex
      ].tasks[this.currentTaskIndex].validation
      this.taskSettingsEdit.minSafeLimit = this.model.taskData.taskSections[
        this.currentIndex
      ].tasks[this.currentTaskIndex].minSafeLimit
      this.taskSettingsEdit.maxSafeLimit = this.model.taskData.taskSections[
        this.currentIndex
      ].tasks[this.currentTaskIndex].maxSafeLimit
      this.taskSettingsEdit.readingFields = this.model.taskData.taskSections[
        this.currentIndex
      ].tasks[this.currentTaskIndex].readingFields
      if (
        this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].options &&
        this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].options.length
      ) {
        this.taskSettingsEdit.options = this.$helpers.cloneObject(
          this.model.taskData.taskSections[this.currentIndex].tasks[
            this.currentTaskIndex
          ].options
        )
      } else {
        this.taskSettingsEdit.options = [{ name: '' }, { name: '' }]
      }
      this.taskSettingsEdit.inputType = this.model.taskData.taskSections[
        this.currentIndex
      ].tasks[this.currentTaskIndex].inputType
      if (
        this.taskSettingsEdit.inputType === '2' &&
        (!this.taskSettingsEdit.readingFields ||
          !this.taskSettingsEdit.readingFields.length)
      ) {
        this.fillTaskReadingsForTasks()
      }
      let task = this.model.taskData.taskSections[this.currentIndex].tasks[
        this.currentTaskIndex
      ]
      this.taskSettingsEdit.defaultValue = task.additionInfo.defaultValue
      this.taskSettingsEdit.failureValue = task.additionInfo.failureValue
      this.taskSettingsEdit.deviationOperatorId =
        task.additionInfo.deviationOperatorId
      this.taskSettingsEdit.woCreateFormId = task.additionInfo.woCreateFormId
      this.taskSettingsEdit.remarksRequired = task.additionInfo.remarksRequired
      this.taskSettingsEdit.remarkOptionValues =
        task.additionInfo.remarkOptionValues
      this.taskSettingsEdit.remarkOption =
        !isEmpty(task.additionInfo.remarkOptionValues) &&
        isArray(task.additionInfo.remarkOptionValues) &&
        task.additionInfo.remarkOptionValues.length > 0
          ? 'specific'
          : 'all'
      this.taskSettingsEdit.attachmentOptionValues =
        task.additionInfo.attachmentOptionValues
      this.taskSettingsEdit.attachmentOption =
        !isEmpty(task.additionInfo.attachmentOptionValues) &&
        isArray(task.additionInfo.attachmentOptionValues) &&
        task.additionInfo.attachmentOptionValues.length > 0
          ? 'specific'
          : 'all'
      if (!isEmpty(task.additionInfo.createWoOnFailure)) {
        this.taskSettingsEdit.createWoOnFailure =
          task.additionInfo.createWoOnFailure
      } else {
        this.taskSettingsEdit.createWoOnFailure = false
        // task.additionInfo.woCreateFormId &&
        // task.additionInfo.woCreateFormId !== -1
      }
      this.showTaskSettings = true
    },
    saveTaskSettings() {
      this.model.taskData.taskSections[this.currentIndex].tasks[
        this.currentTaskIndex
      ].name = this.taskSettingsEdit.name
      this.model.taskData.taskSections[this.currentIndex].tasks[
        this.currentTaskIndex
      ].description = this.taskSettingsEdit.description
      this.model.taskData.taskSections[this.currentIndex].tasks[
        this.currentTaskIndex
      ].attachmentRequired = this.taskSettingsEdit.attachmentRequired
      this.model.taskData.taskSections[this.currentIndex].tasks[
        this.currentTaskIndex
      ].enableInput = this.taskSettingsEdit.enableInput

      if (
        (isEmpty(
          this.model.taskData.taskSections[this.currentIndex].tasks[
            this.currentTaskIndex
          ].inputType
        ) &&
          this.taskSettingsEdit.inputType === '2') ||
        (this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].inputType !== this.taskSettingsEdit.inputType &&
          this.taskSettingsEdit.inputType === '2') ||
        this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].inputType === this.taskSettingsEdit.inputType
      ) {
        /** Will be true, when 
            1) Reading is selected initially.
            2) Reading is selected after selecting other input type. 
            3) Same Input Type is selected saved/no changes is done in input type.
        **/
        this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].readingFieldId = this.taskSettingsEdit.readingFieldId
      } else {
        // INPUT_TYPE has been changed.
        // Assigning - 1, so that readingFieldId gets updated to new value in backend.
        this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].readingFieldId = -1
      }

      this.model.taskData.taskSections[this.currentIndex].tasks[
        this.currentTaskIndex
      ].validation = this.taskSettingsEdit.validation
      this.model.taskData.taskSections[this.currentIndex].tasks[
        this.currentTaskIndex
      ].minSafeLimit = this.taskSettingsEdit.minSafeLimit
      this.model.taskData.taskSections[this.currentIndex].tasks[
        this.currentTaskIndex
      ].maxSafeLimit = this.taskSettingsEdit.maxSafeLimit
      this.model.taskData.taskSections[this.currentIndex].tasks[
        this.currentTaskIndex
      ].readingFields = this.taskSettingsEdit.readingFields
      if (this.taskSettingsEdit.enableInput && this.taskSettingsEdit.options) {
        this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].options = this.$helpers.cloneObject(this.taskSettingsEdit.options)
      } else {
        this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].options = []
      }
      this.model.taskData.taskSections[this.currentIndex].tasks[
        this.currentTaskIndex
      ].inputType = this.taskSettingsEdit.inputType
      if (!this.taskSettingsEdit.enableInput) {
        this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].inputType = null
      }
      let task = this.model.taskData.taskSections[this.currentIndex].tasks[
        this.currentTaskIndex
      ]
      if (task.inputType === '2' && task.readingFieldId > -1) {
        task.additionInfo.defaultValue = ''
      } else {
        task.additionInfo.defaultValue = this.taskSettingsEdit.defaultValue
      }
      task.additionInfo.failureValue = this.taskSettingsEdit.failureValue
      task.additionInfo.deviationOperatorId = this.taskSettingsEdit.deviationOperatorId
      task.additionInfo.remarksRequired = this.taskSettingsEdit.remarksRequired
      task.additionInfo.remarkOptionValues = this.taskSettingsEdit.remarkOptionValues
      task.additionInfo.attachmentOptionValues =
        this.taskSettingsEdit.attachmentOption === 'specific'
          ? this.taskSettingsEdit.attachmentOptionValues
          : []
      task.additionInfo.createWoOnFailure = this.taskSettingsEdit.createWoOnFailure
      // below support for formId might be removed soon
      // if (this.taskSettingsEdit.createWoOnFailure) {
      //   task.additionInfo.woCreateFormId = this.taskSettingsEdit.woCreateFormId
      // } else {
      //   delete task.additionInfo.woCreateFormId
      // }
      this.showTaskSettings = false
    },
    cancelTaskSettings() {
      this.showTaskSettings = false
    },
    showTriggerContextDialog() {
      if (this.sectionSettingsEdit.triggers[0] === 'All') {
        this.triggerContextEdit = []
        this.model.triggerData.triggers.forEach(i => {
          this.triggerContextEdit.push({
            executeIfNotInTime: 1,
            triggerName: i.name,
            isExecuteIfNotInTime: false,
          })
        })
      } else {
        this.triggerContextEdit = this.$helpers.cloneObject(
          this.sectionSettingsEdit.triggerContextEdit
        )
        this.triggerContextEdit.forEach(i => {
          if (i.isExecuteIfNotInTime < 0) {
            i.isExecuteIfNotInTime = 1
          }
        })
      }
      this.showTriggerContextEdit = true
    },
    saveTriggerContextDialog() {
      this.sectionSettingsEdit.triggerContextEdit = this.$helpers.cloneObject(
        this.triggerContextEdit
      )
      if (!this.sectionSettingsEdit.triggerContextEdit) {
        this.sectionSettingsEdit.triggerContextEdit = []
      }
      this.sectionSettingsEdit.triggers = []
      this.sectionSettingsEdit.triggerContextEdit.forEach((i, index) => {
        if (!i.isExecuteIfNotInTime) {
          i.executeIfNotInTime = 1
        }
        this.sectionSettingsEdit.triggers.push(i.triggerName)
      })
      this.showTriggerContextEdit = false
    },
    showSectionSettingsDialog() {
      this.sectionSettingsEdit = {
        attachmentRequired: false,
        enableInput: false,
        validation: null,
        minSafeLimit: null,
        maxSafeLimit: null,
        inputType: null,
        options: [],
        name: '',
        triggers: [],
        triggerContextEdit: [],
        showTriggerContextEdit: false,
        currentIndex: this.currentIndex,
        defaultValue: '',
        failureValue: '',
        deviationOperatorId: null,
        createWoOnFailure: false,
        woCreateFormId: null,
        remarkOption: 'all',
        remarkOptionValues: [],
        remarksRequired: false,
        attachmentOptionValues: [],
        attachmentOption: 'all',
      }
      this.sectionSettingsEdit.attachmentRequired = this.model.taskData.taskSections[
        this.currentIndex
      ].attachmentRequired
      this.sectionSettingsEdit.enableInput = this.model.taskData.taskSections[
        this.currentIndex
      ].enableInput
      this.sectionSettingsEdit.validation = this.model.taskData.taskSections[
        this.currentIndex
      ].validation
      this.sectionSettingsEdit.minSafeLimit = this.model.taskData.taskSections[
        this.currentIndex
      ].minSafeLimit
      this.sectionSettingsEdit.maxSafeLimit = this.model.taskData.taskSections[
        this.currentIndex
      ].maxSafeLimit
      this.sectionSettingsEdit.name = this.model.taskData.taskSections[
        this.currentIndex
      ].name
      if (
        this.model.taskData.taskSections[this.currentIndex].options &&
        this.model.taskData.taskSections[this.currentIndex].options.length
      ) {
        this.sectionSettingsEdit.options = this.$helpers.cloneObject(
          this.model.taskData.taskSections[this.currentIndex].options
        )
      } else {
        this.sectionSettingsEdit.options = [{ name: '' }, { name: '' }]
      }
      this.sectionSettingsEdit.inputType = this.model.taskData.taskSections[
        this.currentIndex
      ].inputType
      let triggerNames = []
      if (
        this.model.taskData.taskSections[this.currentIndex].triggers &&
        this.model.taskData.taskSections[this.currentIndex].triggers.length
      ) {
        this.model.taskData.taskSections[this.currentIndex].triggers.forEach(
          i => {
            triggerNames.push(i.triggerName)
            if (i.triggerName !== 'All') {
              this.sectionSettingsEdit.triggerContextEdit.push({
                executeIfNotInTime:
                  i.executeIfNotInTime > 0 ? i.executeIfNotInTime : 1,
                triggerName: i.triggerName,
                isExecuteIfNotInTime: i.executeIfNotInTime
                  ? i.executeIfNotInTime > -1
                  : false,
              })
            }
          }
        )
      }
      this.sectionSettingsEdit.triggers = triggerNames

      let task = this.model.taskData.taskSections[this.currentIndex]
      this.sectionSettingsEdit.defaultValue = task.additionInfo.defaultValue
      this.sectionSettingsEdit.failureValue = task.additionInfo.failureValue
      this.sectionSettingsEdit.deviationOperatorId =
        task.additionInfo.deviationOperatorId
      this.sectionSettingsEdit.woCreateFormId = task.additionInfo.woCreateFormId
      this.sectionSettingsEdit.remarksRequired =
        task.additionInfo.remarksRequired
      this.sectionSettingsEdit.remarkOptionValues =
        task.additionInfo.remarkOptionValues
      this.sectionSettingsEdit.remarkOption =
        !isEmpty(task.additionInfo.remarkOptionValues) &&
        isArray(task.additionInfo.remarkOptionValues) &&
        task.additionInfo.remarkOptionValues.length > 0
          ? 'specific'
          : 'all'
      this.sectionSettingsEdit.attachmentOptionValues =
        task.additionInfo.attachmentOptionValues
      this.sectionSettingsEdit.attachmentOption =
        !isEmpty(task.additionInfo.attachmentOptionValues) &&
        isArray(task.additionInfo.attachmentOptionValues) &&
        task.additionInfo.attachmentOptionValues.length > 0
          ? 'specific'
          : 'all'

      if (!isEmpty(task.additionInfo.createWoOnFailure)) {
        this.sectionSettingsEdit.createWoOnFailure =
          task.additionInfo.createWoOnFailure
      } else {
        this.sectionSettingsEdit.createWoOnFailure = false
        // task.additionInfo.woCreateFormId &&
        // task.additionInfo.woCreateFormId !== -1
      }

      this.showSectionSettings = true
    },
    saveSectionSettings() {
      this.model.taskData.taskSections[
        this.currentIndex
      ].attachmentRequired = this.sectionSettingsEdit.attachmentRequired
      this.model.taskData.taskSections[
        this.currentIndex
      ].enableInput = this.sectionSettingsEdit.enableInput
      this.model.taskData.taskSections[
        this.currentIndex
      ].validation = this.sectionSettingsEdit.validation
      this.model.taskData.taskSections[
        this.currentIndex
      ].minSafeLimit = this.sectionSettingsEdit.minSafeLimit
      this.model.taskData.taskSections[
        this.currentIndex
      ].maxSafeLimit = this.sectionSettingsEdit.maxSafeLimit
      this.model.taskData.taskSections[this.currentIndex].triggers = []

      let triggerContextMap = {}
      if (
        this.sectionSettingsEdit.triggerContextEdit &&
        this.sectionSettingsEdit.triggerContextEdit.length
      ) {
        this.sectionSettingsEdit.triggerContextEdit.forEach(i => {
          triggerContextMap[i.triggerName] = i
        })
      }

      if (
        this.sectionSettingsEdit.triggers &&
        this.sectionSettingsEdit.triggers.length
      ) {
        this.sectionSettingsEdit.triggers.forEach(i => {
          this.model.taskData.taskSections[this.currentIndex].triggers.push({
            triggerName: i,
            executeIfNotInTime:
              triggerContextMap[i] && triggerContextMap[i].isExecuteIfNotInTime
                ? triggerContextMap[i].executeIfNotInTime
                : -1,
          })
        })
      }

      if (this.model.taskData.taskSections[this.currentIndex].enableInput) {
        if (this.sectionSettingsEdit.options) {
          this.model.taskData.taskSections[
            this.currentIndex
          ].options = this.$helpers.cloneObject(
            this.sectionSettingsEdit.options
          )
        }
      } else {
        this.model.taskData.taskSections[this.currentIndex].options = []
      }
      this.model.taskData.taskSections[
        this.currentIndex
      ].inputType = this.sectionSettingsEdit.inputType
      if (!this.model.taskData.taskSections[this.currentIndex].enableInput) {
        this.model.taskData.taskSections[this.currentIndex].inputType = null
      }
      this.model.taskData.taskSections[
        this.currentIndex
      ].name = this.sectionSettingsEdit.name

      let section = this.model.taskData.taskSections[this.currentIndex]
      section.additionInfo.defaultValue = this.sectionSettingsEdit.defaultValue
      section.additionInfo.failureValue = this.sectionSettingsEdit.failureValue
      section.additionInfo.deviationOperatorId = this.sectionSettingsEdit.deviationOperatorId
      section.additionInfo.remarksRequired = this.sectionSettingsEdit.remarksRequired
      section.additionInfo.remarkOptionValues = this.sectionSettingsEdit.remarkOptionValues
      section.additionInfo.attachmentOptionValues =
        this.sectionSettingsEdit.attachmentOption == 'specific'
          ? this.sectionSettingsEdit.attachmentOptionValues
          : []

      section.additionInfo.createWoOnFailure = this.sectionSettingsEdit.createWoOnFailure
      // below formId support might be removed soon...
      // if (this.sectionSettingsEdit.createWoOnFailure) {
      //   section.additionInfo.woCreateFormId = this.sectionSettingsEdit.woCreateFormId
      // } else {
      //   delete section.additionInfo.woCreateFormId
      // }
      for (
        let i = 0;
        i < this.model.taskData.taskSections[this.currentIndex].tasks.length;
        i++
      ) {
        this.model.taskData.taskSections[this.currentIndex].tasks[
          i
        ].attachmentRequired = this.sectionSettingsEdit.attachmentRequired
        this.model.taskData.taskSections[this.currentIndex].tasks[
          i
        ].enableInput = this.sectionSettingsEdit.enableInput
        this.model.taskData.taskSections[this.currentIndex].tasks[
          i
        ].validation = this.sectionSettingsEdit.validation
        this.model.taskData.taskSections[this.currentIndex].tasks[
          i
        ].minSafeLimit = this.sectionSettingsEdit.minSafeLimit
        this.model.taskData.taskSections[this.currentIndex].tasks[
          i
        ].maxSafeLimit = this.sectionSettingsEdit.maxSafeLimit
        if (
          this.model.taskData.taskSections[this.currentIndex].options &&
          this.model.taskData.taskSections[this.currentIndex].options.length
        ) {
          this.model.taskData.taskSections[this.currentIndex].tasks[
            i
          ].options = this.$helpers.cloneObject(
            this.model.taskData.taskSections[this.currentIndex].options
          )
        } else {
          this.model.taskData.taskSections[this.currentIndex].tasks[
            i
          ].options = []
        }
        this.model.taskData.taskSections[this.currentIndex].tasks[
          i
        ].inputType = this.sectionSettingsEdit.inputType
        if (!this.model.taskData.taskSections[this.currentIndex].enableInput) {
          this.model.taskData.taskSections[this.currentIndex].tasks[
            i
          ].inputType = null
        }

        // INPUT_TYPE has been changed.
        // Assigning - 1, so that readingFieldId gets updated to new value in backend.
        this.model.taskData.taskSections[this.currentIndex].tasks[
          i
        ].readingFieldId = -1

        let task = this.model.taskData.taskSections[this.currentIndex].tasks[i]
        task.additionInfo.defaultValue = this.sectionSettingsEdit.defaultValue
        task.additionInfo.failureValue = this.sectionSettingsEdit.failureValue
        task.additionInfo.deviationOperatorId = this.sectionSettingsEdit.deviationOperatorId
        task.additionInfo.remarksRequired = this.sectionSettingsEdit.remarksRequired
        task.additionInfo.remarkOptionValues = this.sectionSettingsEdit.remarkOptionValues
        task.additionInfo.attachmentOptionValues =
          this.sectionSettingsEdit.attachmentOption === 'specific'
            ? this.sectionSettingsEdit.attachmentOptionValues
            : []
        task.additionInfo.createWoOnFailure = this.sectionSettingsEdit.createWoOnFailure
        // below formId support might be removed soon...
        // if (this.sectionSettingsEdit.createWoOnFailure) {
        //   task.additionInfo.woCreateFormId = this.sectionSettingsEdit.woCreateFormId
        //    = true
        // } else {
        //   delete task.additionInfo.woCreateFormId
        // }
      }

      this.showSectionSettings = false
    },
    cancelSectionSettings() {
      this.showSectionSettings = false
    },
    handleSingleResource(newVal, oldVal) {
      if (newVal !== oldVal && newVal) {
        let model = this.model
        let { woData = {} } = model
        let { singleResource = null } = woData

        if (!isEmpty(singleResource)) {
          if (
            this.model.woData.workOrderType === 'single' &&
            singleResource.resourceTypeEnum === 'SPACE'
          ) {
            this.categoryOptions[0] = {
              value: 'CURRENT_SPACE',
              label: `${this.model.woData.spaceAssetDisplayName}`,
            }
            this.handleSpaceCategoryOptions(true)
            this.model.taskData.taskSections.forEach((i, index) => {
              i.category = ['CURRENT_SPACE']
              this.changeLabelForSection(index, true)
            })
          } else if (
            this.model.woData.workOrderType === 'single' &&
            singleResource.resourceTypeEnum === 'ASSET'
          ) {
            this.model.taskData.taskSections.forEach(i => {
              i.category = ['CURRENT_ASSET']
              i.tasks.forEach(k => {
                k.category = ['CURRENT_ASSET']
              })
            })
          }
        }
      }
    },
    setDefaultValueForTaskSettings(taskSettingsEdit) {
      taskSettingsEdit.defaultValue = ''
      taskSettingsEdit.failureValue = ''
    },
    setDefaultValueForSectionSettings(sectionSettingsEdit) {
      sectionSettingsEdit.defaultValue = ''
      sectionSettingsEdit.failureValue = ''
    },
    addTaskOption(selectedTask) {
      selectedTask.options.push({ name: '' })
    },
    removeOption(
      settingObj,
      index,
      isTaskSettings = false,
      isTaskSectionSettings = false
    ) {
      let { options = [] } = settingObj || {}
      options.splice(index, 1)
      this.$setProperty(settingObj, 'options', options)

      if (isTaskSettings) {
        this.setDefaultValueForTaskSettings(settingObj)
      }
      if (isTaskSectionSettings) {
        this.setDefaultValueForSectionSettings(settingObj)
      }
    },
    onTaskSelectInput() {
      // following assignment is required so that, when user switches back to same input type
      // readingFieldId will be same, if other input Type is selected it will be reset to -1 in saveTaskSettings()
      this.taskSettingsEdit.readingFieldId = this.model.taskData.taskSections[
        this.currentIndex
      ].tasks[this.currentTaskIndex].readingFieldId

      if (this.taskSettingsEdit.inputType === '2') {
        this.fillTaskReadingsForTasks()
        // reset readingFieldId so that readingField will be empty while changing the input option to Reading
        this.taskSettingsEdit.readingFieldId = null
      } else if (this.taskSettingsEdit.inputType === '5') {
        this.taskSettingsEdit.options = []
        this.addTaskOption(this.taskSettingsEdit)
        this.addTaskOption(this.taskSettingsEdit)
      } else if (this.taskSettingsEdit.inputType === '6') {
        this.taskSettingsEdit.options = [{ name: '' }, { name: '' }]
        this.taskSettingsEdit.options[0].name = 'YES'
        this.taskSettingsEdit.options[1].name = 'NO'
      }
    },
    onSelectInput() {
      if (this.sectionSettingsEdit.inputType === '5') {
        this.sectionSettingsEdit.options = []
        this.addTaskOption(this.sectionSettingsEdit)
        this.addTaskOption(this.sectionSettingsEdit)
      } else if (this.sectionSettingsEdit.inputType === '6') {
        this.taskSettingsEdit.options = [{ name: '' }, { name: '' }]
        this.sectionSettingsEdit.options[0].name = 'YES'
        this.sectionSettingsEdit.options[1].name = 'NO'
      }
      this.$forceUpdate()
    },
    getFloorReadings(isSection) {
      this.$http
        .post('/reading/getallspacetypereadings', { spaceType: 'Floors' })
        .then(response => {
          for (let categoryId in response.data.moduleMap) {
            let moduleJson = response.data.moduleMap[categoryId]
            for (let moduleIndex in moduleJson) {
              if (moduleJson[moduleIndex].type !== 3) {
                continue
              }
              let readingFields = moduleJson[moduleIndex].fields
              for (let fieldIndex in readingFields) {
                let field = readingFields[fieldIndex]
                if (categoryId === -1) {
                  field['spaceId'] = 'Default Readings'
                  field.buildingName = 'All'
                } else {
                  field['spaceId'] = response.data.spaces[categoryId].name
                  field.buildingName =
                    response.data.spaces[categoryId].building.name
                }
                if (isSection) {
                  this.model.taskData.taskSections[
                    this.currentIndex
                  ].readingFields.push(field)
                } else {
                  this.taskSettingsEdit.readingFields.push(field)
                }
              }
            }
          }
        })
    },
    fillTaskReadingsForTasks() {
      this.taskSettingsEdit.readingFields = []
      if (
        this.model.woData.workOrderType === 'single' &&
        this.model.woData.singleResource.resourceTypeEnum === 'ASSET'
      ) {
        if (
          this.model.woData.singleResource &&
          this.model.woData.singleResource.id &&
          this.model.woData.singleResource.category
        ) {
          this.$util
            .loadAssetReadingFields(
              -1,
              this.model.woData.singleResource.category.id,
              false,
              true
            )
            .then(fields => {
              this.taskSettingsEdit.readingFields = fields
              this.$forceUpdate()
            })
        }
      } else if (
        this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].category[0] === 'CURRENT_FLOOR'
      ) {
        this.getFloorReadings(false)
      } else if (
        this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].category[0] === 'CURRENT_SPACE'
      ) {
        if (this.model.woData.workOrderType === 'single') {
          if (
            this.model.woData.singleResource &&
            this.model.woData.singleResource.id
          ) {
            let sectionCategory = this.model.taskData.taskSections[
              this.currentIndex
            ].category
            if (sectionCategory.length === 1) {
              this.$util
                .loadSpaceReadingFields(
                  this.model.woData.singleResource.id,
                  false,
                  null,
                  true
                )
                .then(fields => {
                  this.taskSettingsEdit.readingFields = fields
                  this.$forceUpdate()
                })
            } else if (sectionCategory.length === 2) {
              let sectionCategoryId = Number(sectionCategory[1])
              this.$util
                .loadSpaceReadingFields(
                  -1,
                  false,
                  sectionCategoryId,
                  true,
                  true
                )
                .then(fields => {
                  this.taskSettingsEdit.readingFields = fields
                  this.$forceUpdate()
                })
            }
          }
        } else {
          this.$util
            .loadSpaceReadingFields(
              -1,
              false,
              Number(this.model.woData.spacecategoryId),
              true
            )
            .then(fields => {
              this.taskSettingsEdit.readingFields = fields
              this.$forceUpdate()
            })
        }
      } else if (
        this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].category[0] === 'CURRENT_BUILDING'
      ) {
        this.taskSettingsEdit.readingFields = []
        this.model.woData.selectedBuildingList.forEach(x => {
          this.$util
            .loadSpaceReadingFields(x, false, null, true)
            .then(fields => {
              this.taskSettingsEdit.readingFields.push(...fields)
              this.$forceUpdate()
            })
        })
      } else if (
        this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].category[0] === 'CURRENT_ASSET'
      ) {
        let assetCategoryId = Number(this.model.woData.assetCategoryId)
        if (this.model.woData.workOrderType === 'single') {
          if (
            this.model.woData.singleResource &&
            this.model.woData.singleResource.id
          ) {
            let sectionCategory = this.model.taskData.taskSections[
              this.currentIndex
            ].category
            if (sectionCategory[0] === 'asset') {
              assetCategoryId = Number(sectionCategory[1])
            }
          }
        }
        let sectionCategory = this.model.taskData.taskSections[
          this.currentIndex
        ].category
        if (sectionCategory[0] === 'asset') {
          assetCategoryId = Number(sectionCategory[1])
        }
        this.$util
          .loadAssetReadingFields(-1, assetCategoryId, false, true)
          .then(fields => {
            this.taskSettingsEdit.readingFields = fields
            this.$forceUpdate()
          })
      } else if (
        this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].category[0] === 'space'
      ) {
        this.$util
          .loadSpaceReadingFields(
            -1,
            false,
            Number(
              this.model.taskData.taskSections[this.currentIndex].tasks[
                this.currentTaskIndex
              ].category[1]
            ),
            true
          )
          .then(fields => {
            this.taskSettingsEdit.readingFields = fields
            this.$forceUpdate()
          })
      } else if (
        this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].category[0] === 'asset'
      ) {
        this.$util
          .loadAssetReadingFields(
            -1,
            Number(
              this.model.taskData.taskSections[this.currentIndex].tasks[
                this.currentTaskIndex
              ].category[1]
            ),
            false,
            true
          )
          .then(fields => {
            this.taskSettingsEdit.readingFields = fields
            this.$forceUpdate()
          })
      }
    },
    reinitTaskSettings(sectionIndex, taskIndex, resourceLabel) {
      let obj = {
        resourceLabel: '',
        selectedResourceList: [],
        isIncludeResource: null,
        attachmentRequired: false,
        enableInput: false,
        readingFieldId: null,
        validation: null,
        minSafeLimit: null,
        maxSafeLimit: null,
        options: [],
        inputType: null,
        readingFields: [],
        createWoOnFailure: false,
        additionInfo: {
          defaultValue: '',
          failureValue: '',
          deviationOperatorId: null,
          woCreateFormId: null,
          remarksRequired: false,
          remarkOptionValues: [],
          attachmentOptionValues: [],
          uniqueId: null,
        },
      }
      if (resourceLabel) {
        obj.resourceLabel = resourceLabel
      }
      let oldUnique = this.model.taskData.taskSections[sectionIndex].tasks[
        taskIndex
      ].additionInfo.uniqueId
      Object.assign(
        this.model.taskData.taskSections[sectionIndex].tasks[taskIndex],
        obj
      )
      this.model.taskData.taskSections[sectionIndex].tasks[
        taskIndex
      ].additionInfo.uniqueId = oldUnique
    },
    reinitTaskSectionSettings(sectionIndex, resourceLabel) {
      let obj = {
        resourceLabel: '',
        selectedResourceList: [],
        isIncludeResource: null,
        attachmentRequired: false,
        enableInput: false,
        validation: null,
        minSafeLimit: null,
        maxSafeLimit: null,
        options: [],
        inputType: null,
        createWoOnFailure: false,
        additionInfo: {
          defaultValue: '',
          failureValue: '',
          deviationOperatorId: null,
          woCreateFormId: null,
          remarksRequired: false,
          remarkOptionValues: [],
          attachmentOptionValues: [],
        },
      }
      if (resourceLabel) {
        obj.resourceLabel = resourceLabel
      }
      Object.assign(this.model.taskData.taskSections[sectionIndex], obj)
      for (
        let j = 0;
        j < this.model.taskData.taskSections[sectionIndex].tasks.length;
        j++
      ) {
        this.reinitTaskSettings(sectionIndex, j)
      }
    },
    changeLabel(sectionIndex, selectedDefault) {
      this.changeLabelForSection(sectionIndex, selectedDefault)
      this.$forceUpdate()
    },
    changeLabelForSection(sectionIndex, fromWo) {
      this.model.taskData.taskSections[
        sectionIndex
      ].resourceLabel = this.resourceLabel(sectionIndex, fromWo)
      if (
        this.model.taskData.taskSections[sectionIndex].resourceLabel !==
        this.oldSectionLabel
      ) {
        this.reinitTaskSectionSettings(
          sectionIndex,
          this.model.taskData.taskSections[sectionIndex].resourceLabel
        )
        let category = this.model.taskData.taskSections[sectionIndex].category
        this.model.taskData.taskSections[sectionIndex].tasks.forEach(
          (task, taskIndex) => {
            if (category[0] === 'CURRENT_FLOOR') {
              task.category = ['CURRENT_FLOOR']
            } else if (
              category[0] === 'CURRENT_SPACE' ||
              category[0] === 'space'
            ) {
              task.category = ['CURRENT_SPACE']
            } else if (
              category[0] === 'CURRENT_ASSET' ||
              category[0] === 'asset'
            ) {
              task.category = ['CURRENT_ASSET']
            } else if (
              category[0] === 'CURRENT_BUILDING' ||
              category[0] === 'building'
            ) {
              task.category = ['CURRENT_BUILDING']
            } else if (category[0] === 'ALL_SITES' || category[0] === 'site') {
              task.category = ['ALL_SITES']
            }
            let selectedDefault =
              category[0] === 'CURRENT_FLOOR' ||
              category[0] === 'CURRENT_ASSET' ||
              category[0] === 'CURRENT_SPACE' ||
              category[0] === 'CURRENT_BUILDING' ||
              category[0] === 'ALL_SITES'
            !fromWo
            this.model.taskData.taskSections[sectionIndex].tasks[
              taskIndex
            ].resourceLabel = this.resourceLabelForTask(
              sectionIndex,
              taskIndex,
              selectedDefault
            )
          }
        )
      }
    },
    changeTaskResourceLabel(selectedDefault) {
      this.model.taskData.taskSections[this.currentIndex].tasks[
        this.currentTaskIndex
      ].resourceLabel = this.resourceLabelForTask(
        this.currentIndex,
        this.currentTaskIndex,
        selectedDefault
      )
      if (
        this.oldTasklabel !==
        this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].resourceLabel
      ) {
        this.reinitTaskSettings(
          this.currentIndex,
          this.currentTaskIndex,
          this.model.taskData.taskSections[this.currentIndex].tasks[
            this.currentTaskIndex
          ].resourceLabel
        )
      }
      this.$forceUpdate()
    },
    associateResource(selectedObj) {
      if (selectedObj.resourceList && selectedObj.resourceList.length) {
        this.model.taskData.taskSections[
          this.currentIndex
        ].selectedResourceList = selectedObj.resourceList
        this.model.taskData.taskSections[this.currentIndex].isIncludeResource =
          selectedObj.isInclude
      }
      this.chooserVisibility = false
      this.model.taskData.taskSections[
        this.currentIndex
      ].resourceLabel = this.resourceLabel(this.currentIndex)
      Object.keys(this.sectionPopover).forEach(key => {
        this.$set(this.sectionPopover, key, false)
      })
    },
    associateResourceForTask(selectedObj) {
      if (selectedObj.resourceList && selectedObj.resourceList.length) {
        this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].selectedResourceList = selectedObj.resourceList
        this.model.taskData.taskSections[this.currentIndex].tasks[
          this.currentTaskIndex
        ].isIncludeResource = selectedObj.isInclude
      }
      this.chooserVisibilityForTask = false
      let category = this.model.taskData.taskSections[this.currentIndex].tasks[
        this.currentTaskIndex
      ].category
      let selectedDefault =
        category[0] === 'CURRENT_FLOOR' ||
        category[0] === 'CURRENT_ASSET' ||
        category[0] === 'CURRENT_SPACE'
      this.model.taskData.taskSections[this.currentIndex].tasks[
        this.currentTaskIndex
      ].resourceLabel = this.resourceLabelForTask(
        this.currentIndex,
        this.currentTaskIndex,
        selectedDefault
      )
      Object.keys(this.taskPopover).forEach(key => {
        this.$set(this.taskPopover, key, false)
      })
    },
    setCurrentPosition(secIdx, taskIdx) {
      this.currentIndex = secIdx
      this.currentTaskIndex = taskIdx
    },
    setCurrentSection(idx) {
      this.currentIndex = idx
    },
    setCurrentTask(idx) {
      this.currentTaskIndex = idx
    },
    getMaxTaskUniqueId() {
      let max = this.model.taskData.taskSections[0].tasks[0].additionInfo
        .uniqueId
      this.model.taskData.taskSections.forEach(ts => {
        ts.tasks.forEach(t => {
          if (t.additionInfo.uniqueId > max) {
            max = t.additionInfo.uniqueId
          }
        })
      })
      return max
    },
    addNewTask(taskIndex) {
      if (isEmpty(this.model.woData.resourceType)) {
        this.$message.error(
          this.$t(
            'maintenance.old_pm.please_fill_the_details_in_configuration_page'
          )
        )
        return
      }
      let taskObj = {
        name: '',
        category: null,
        resourceLabel: '',
        description: '',
        inputType: null,
        attachmentRequired: false,
        enableInput: false,
        readingFieldId: null,
        minSafeLimit: null,
        maxSafeLimit: null,
        options: [],
        selectedResourceList: [],
        isIncludeResource: null,
        readingFields: [],
        createWoOnFailure: false,
        additionInfo: {
          defaultValue: '',
          failureValue: '',
          deviationOperatorId: null,
          woCreateFormId: null,
          remarksRequired: false,
          remarkOptionValues: [],
          attachmentOptionValues: [],
          uniqueId: null,
        },
      }
      taskObj.additionInfo.uniqueId = this.getMaxTaskUniqueId() + 1
      let category = this.model.taskData.taskSections[this.currentIndex]
        .category
      if (category && category.length) {
        if (category[0] === 'CURRENT_FLOOR') {
          taskObj.category = ['CURRENT_FLOOR']
        } else if (category[0] === 'CURRENT_SPACE' || category[0] === 'space') {
          taskObj.category = ['CURRENT_SPACE']
        } else if (category[0] === 'CURRENT_ASSET' || category[0] === 'asset') {
          taskObj.category = ['CURRENT_ASSET']
        } else if (category[0] === 'CURRENT_BUILDING') {
          taskObj.category = ['CURRENT_BUILDING']
        } else if (category[0] === 'ALL_SITES') {
          taskObj.category = ['ALL_SITES']
        }
      }

      taskObj.attachmentRequired = this.model.taskData.taskSections[
        this.currentIndex
      ].attachmentRequired
      taskObj.enableInput = this.model.taskData.taskSections[
        this.currentIndex
      ].enableInput
      taskObj.validation = this.model.taskData.taskSections[
        this.currentIndex
      ].validation
      taskObj.minSafeLimit = this.model.taskData.taskSections[
        this.currentIndex
      ].minSafeLimit
      taskObj.maxSafeLimit = this.model.taskData.taskSections[
        this.currentIndex
      ].maxSafeLimit
      taskObj.additionInfo.deviationOperatorId = this.model.taskData.taskSections[
        this.currentIndex
      ].additionInfo.deviationOperatorId
      taskObj.additionInfo.defaultValue = this.model.taskData.taskSections[
        this.currentIndex
      ].additionInfo.defaultValue
      taskObj.additionInfo.failureValue = this.model.taskData.taskSections[
        this.currentIndex
      ].additionInfo.failureValue
      taskObj.additionInfo.createWoOnFailure = this.model.taskData.taskSections[
        this.currentIndex
      ].additionInfo.createWoOnFailure
      taskObj.additionInfo.remarksRequired = this.model.taskData.taskSections[
        this.currentIndex
      ].additionInfo.remarksRequired
      taskObj.additionInfo.remarkOptionValues = this.model.taskData.taskSections[
        this.currentIndex
      ].additionInfo.remarkOptionValues
      taskObj.additionInfo.attachmentOptionValues = this.model.taskData.taskSections[
        this.currentIndex
      ].additionInfo.attachmentOptionValues
      if (
        this.model.taskData.taskSections[this.currentIndex].options &&
        this.model.taskData.taskSections[this.currentIndex].options.length
      ) {
        taskObj.options = this.$helpers.cloneObject(
          this.model.taskData.taskSections[this.currentIndex].options
        )
      }
      taskObj.inputType = this.model.taskData.taskSections[
        this.currentIndex
      ].inputType

      taskIndex =
        taskIndex >= 0
          ? taskIndex
          : this.model.taskData.taskSections[this.currentIndex].tasks.length
      this.model.taskData.taskSections[this.currentIndex].tasks.splice(
        taskIndex,
        0,
        taskObj
      )

      this.currentTaskIndex = this.currentTaskIndex + 1
      let setDefaultTaskName = false
      if (category && category.length) {
        if (
          taskObj.category[0] === 'CURRENT_ASSET' ||
          taskObj.category[0] === 'CURRENT_SPACE' ||
          taskObj.category[0] === 'CURRENT_FLOOR' ||
          taskObj.category[0] === 'CURRENT_BUILDING' ||
          taskObj.category[0] === 'ALL_SITES'
        ) {
          setDefaultTaskName = true
        }
        taskObj.resourceLabel = this.resourceLabelForTask(
          this.currentIndex,
          this.currentTaskIndex,
          setDefaultTaskName
        )
      }

      this.$nextTick(() => {
        let ref = this.$refs['task-' + this.currentIndex + '-' + taskIndex]
        if (ref) {
          ref[0].focus()
        }
      })
    },
    onTaskPaste(tasks, index, event) {
      let data = event.clipboardData.getData('text/plain')
      if (data) {
        let list = data.split(/[\n\r]/g)
        if (list.length > 1) {
          if (!tasks[index].name) {
            tasks[index].name = list.splice(0, 1)[0]
          }
          list.forEach(subject => {
            if (subject) {
              this.addNewTask(event, ++index)
              tasks[index].name = subject
            }
          })
          // tasks.splice(index + 1, 0, ...list.map(value => ({subject: value})))
          event.preventDefault()
        }
      }
    },
    addNewSection() {
      if (isEmpty(this.model.woData.resourceType)) {
        this.$message.error(
          this.$t(
            'maintenance.old_pm.please_fill_the_details_in_configuration_page'
          )
        )
        return
      }
      let count = this.model.taskData.taskSections.length
      if (this.model.woData.workOrderType === 'bulk') {
        if (this.model.woData.resourceType === 'ALL_FLOORS') {
          this.model.taskData.taskSections.push({
            name: `Untitled Section ${count + 1}`,
            category: ['CURRENT_FLOOR'],
            dummyValue: [1],
            resourceLabel: 'Current Floor',
            selectedResourceList: [],
            inputType: null,
            isIncludeResource: null,
            attachmentRequired: false,
            enableInput: false,
            readingFieldId: null,
            validation: null,
            minSafeLimit: null,
            maxSafeLimit: null,
            createWoOnFailure: false,
            triggers: [{ triggerName: 'All' }],
            additionInfo: {
              defaultValue: '',
              failureValue: '',
              deviationOperatorId: null,
              woCreateFormId: null,
              remarksRequired: false,
              remarkOptionValues: [],
              attachmentOptionValues: [],
            },
            tasks: [
              {
                name: '',
                category: ['CURRENT_FLOOR'],
                resourceLabel: '',
                description: '',
                inputType: null,
                attachmentRequired: false,
                enableInput: false,
                readingFieldId: null,
                minSafeLimit: null,
                maxSafeLimit: null,
                options: [],
                selectedResourceList: [],
                isIncludeResource: null,
                readingFields: [],
                createWoOnFailure: false,
                additionInfo: {
                  defaultValue: '',
                  failureValue: '',
                  deviationOperatorId: null,
                  woCreateFormId: null,
                  remarksRequired: false,
                  remarkOptionValues: [],
                  attachmentOptionValues: [],
                  uniqueId: this.getMaxTaskUniqueId() + 1,
                },
              },
            ],
          })
        } else if (this.model.woData.resourceType === 'SPACE_CATEGORY') {
          this.model.taskData.taskSections.push({
            name: `Untitled Section ${count + 1}`,
            category: ['CURRENT_SPACE'],
            dummyValue: [1],
            resourceLabel: 'Current Spaces',
            selectedResourceList: [],
            inputType: null,
            isIncludeResource: null,
            attachmentRequired: false,
            enableInput: false,
            readingFieldId: null,
            validation: null,
            minSafeLimit: null,
            maxSafeLimit: null,
            triggers: [{ triggerName: 'All' }],
            createWoOnFailure: false,
            additionInfo: {
              defaultValue: '',
              failureValue: '',
              deviationOperatorId: null,
              woCreateFormId: null,
              remarksRequired: false,
              remarkOptionValues: [],
              attachmentOptionValues: [],
            },
            tasks: [
              {
                name: '',
                category: ['CURRENT_SPACE'],
                resourceLabel: '',
                description: '',
                inputType: null,
                attachmentRequired: false,
                enableInput: false,
                readingFieldId: null,
                minSafeLimit: null,
                maxSafeLimit: null,
                options: [],
                selectedResourceList: [],
                isIncludeResource: null,
                readingFields: [],
                createWoOnFailure: false,
                additionInfo: {
                  defaultValue: '',
                  failureValue: '',
                  deviationOperatorId: null,
                  woCreateFormId: null,
                  remarksRequired: false,
                  remarkOptionValues: [],
                  attachmentOptionValues: [],
                  uniqueId: this.getMaxTaskUniqueId() + 1,
                },
              },
            ],
          })
        } else if (this.model.woData.resourceType === 'ASSET_CATEGORY') {
          this.model.taskData.taskSections.push({
            name: `Untitled Section ${count + 1}`,
            category: ['CURRENT_ASSET'],
            dummyValue: [1],
            resourceLabel: 'Current Asset',
            selectedResourceList: [],
            inputType: null,
            isIncludeResource: null,
            attachmentRequired: false,
            enableInput: false,
            readingFieldId: null,
            validation: null,
            minSafeLimit: null,
            maxSafeLimit: null,
            triggers: [{ triggerName: 'All' }],
            createWoOnFailure: false,
            additionInfo: {
              defaultValue: '',
              failureValue: '',
              deviationOperatorId: null,
              woCreateFormId: null,
              remarksRequired: false,
              remarkOptionValues: [],
              attachmentOptionValues: [],
            },
            tasks: [
              {
                name: '',
                category: ['CURRENT_ASSET'],
                resourceLabel: '',
                description: '',
                inputType: null,
                attachmentRequired: false,
                enableInput: false,
                readingFieldId: null,
                minSafeLimit: null,
                maxSafeLimit: null,
                options: [],
                selectedResourceList: [],
                isIncludeResource: null,
                readingFields: [],
                createWoOnFailure: false,
                additionInfo: {
                  defaultValue: '',
                  failureValue: '',
                  deviationOperatorId: null,
                  woCreateFormId: null,
                  remarksRequired: false,
                  remarkOptionValues: [],
                  attachmentOptionValues: [],
                  uniqueId: this.getMaxTaskUniqueId() + 1,
                },
              },
            ],
          })
        } else if (this.model.woData.resourceType === 'BUILDINGS') {
          this.model.taskData.taskSections.push({
            name: `Untitled Section ${count + 1}`,
            category: ['CURRENT_BUILDING'],
            dummyValue: [1],
            resourceLabel: 'Current Spaces',
            selectedResourceList: [],
            inputType: null,
            isIncludeResource: null,
            attachmentRequired: false,
            enableInput: false,
            readingFieldId: null,
            validation: null,
            minSafeLimit: null,
            maxSafeLimit: null,
            triggers: [{ triggerName: 'All' }],
            createWoOnFailure: false,
            additionInfo: {
              defaultValue: '',
              failureValue: '',
              deviationOperatorId: null,
              woCreateFormId: null,
              remarksRequired: false,
              remarkOptionValues: [],
              attachmentOptionValues: [],
            },
            tasks: [
              {
                name: '',
                category: ['CURRENT_BUILDING'],
                resourceLabel: '',
                description: '',
                inputType: null,
                attachmentRequired: false,
                enableInput: false,
                readingFieldId: null,
                minSafeLimit: null,
                maxSafeLimit: null,
                options: [],
                selectedResourceList: [],
                isIncludeResource: null,
                readingFields: [],
                createWoOnFailure: false,
                additionInfo: {
                  defaultValue: '',
                  failureValue: '',
                  deviationOperatorId: null,
                  woCreateFormId: null,
                  remarksRequired: false,
                  remarkOptionValues: [],
                  attachmentOptionValues: [],
                  uniqueId: this.getMaxTaskUniqueId() + 1,
                },
              },
            ],
          })
        } else if (this.model.woData.resourceType === 'ALL_SITES') {
          this.model.taskData.taskSections.push({
            name: `Untitled Section ${count + 1}`,
            category: ['ALL_SITES'],
            dummyValue: [1],
            resourceLabel: 'All Sites',
            selectedResourceList: [],
            inputType: null,
            isIncludeResource: null,
            attachmentRequired: false,
            enableInput: false,
            readingFieldId: null,
            validation: null,
            minSafeLimit: null,
            maxSafeLimit: null,
            triggers: [{ triggerName: 'All' }],
            createWoOnFailure: false,
            additionInfo: {
              defaultValue: '',
              failureValue: '',
              deviationOperatorId: null,
              woCreateFormId: null,
              remarksRequired: false,
              remarkOptionValues: [],
              attachmentOptionValues: [],
            },
            tasks: [
              {
                name: '',
                category: ['ALL_SITES'],
                resourceLabel: '',
                description: '',
                inputType: null,
                attachmentRequired: false,
                enableInput: false,
                readingFieldId: null,
                minSafeLimit: null,
                maxSafeLimit: null,
                options: [],
                selectedResourceList: [],
                isIncludeResource: null,
                readingFields: [],
                createWoOnFailure: false,
                additionInfo: {
                  defaultValue: '',
                  failureValue: '',
                  deviationOperatorId: null,
                  woCreateFormId: null,
                  remarksRequired: false,
                  remarkOptionValues: [],
                  attachmentOptionValues: [],
                  uniqueId: this.getMaxTaskUniqueId() + 1,
                },
              },
            ],
          })
        }
      } else {
        let category = []
        if (this.model.woData.singleResource.resourceType === 1) {
          category = ['CURRENT_SPACE']
        }
        this.model.taskData.taskSections.push({
          name: `Untitled Section ${count + 1}`,
          category,
          dummyValue: [1],
          resourceLabel: 'Current Spaces',
          selectedResourceList: [],
          inputType: null,
          isIncludeResource: null,
          attachmentRequired: false,
          enableInput: false,
          readingFieldId: null,
          validation: null,
          minSafeLimit: null,
          maxSafeLimit: null,
          triggers: [{ triggerName: 'All' }],
          createWoOnFailure: false,
          additionInfo: {
            defaultValue: '',
            failureValue: '',
            deviationOperatorId: null,
            woCreateFormId: null,
            remarksRequired: false,
            remarkOptionValues: [],
            attachmentOptionValues: [],
          },
          tasks: [
            {
              name: '',
              category,
              resourceLabel: '',
              description: '',
              inputType: null,
              attachmentRequired: false,
              enableInput: false,
              readingFieldId: null,
              minSafeLimit: null,
              maxSafeLimit: null,
              options: [],
              selectedResourceList: [],
              isIncludeResource: null,
              readingFields: [],
              createWoOnFailure: false,
              additionInfo: {
                defaultValue: '',
                failureValue: '',
                deviationOperatorId: null,
                woCreateFormId: null,
                remarksRequired: false,
                remarkOptionValues: [],
                attachmentOptionValues: [],
                uniqueId: this.getMaxTaskUniqueId() + 1,
              },
            },
          ],
        })
      }
      this.currentIndex = count
      this.currentTaskIndex = 0
      if (
        !(
          this.model.woData.workOrderType === 'single' &&
          this.model.woData.singleResource.resourceType === 2
        )
      ) {
        this.oldSectionLabel = null
        this.changeLabelForSection(this.currentIndex, true)
      }
    },
    moveToNext() {
      let validated = this.validateTaskForm()
      if (validated) {
        this.$emit('next')
      }
    },
    moveToPrevious() {
      this.$emit('previous')
    },
    deleteTask(sectionIndex, taskIndex) {
      this.currentTaskIndex = 0
      this.model.taskData.taskSections[sectionIndex].tasks.splice(taskIndex, 1)
    },
    deleteSection(sectionIndex) {
      this.currentIndex = 0
      this.currentTaskIndex = 0
      this.model.taskData.taskSections.splice(sectionIndex, 1)
    },
    loadWoForms() {
      let url = `/v2/forms?moduleName=workorder`
      this.$http.get(url).then(response => {
        if (response.data.responseCode === 0) {
          this.woForms = (response.data.result.forms || []).filter(
            form => form.id > 0 && form.name !== 'default_workorder_web'
          )
        }
      })
    },
    resourceLabelForTask(sectionIndex, taskIndex, fromWo) {
      if (
        this.model.taskData.taskSections[sectionIndex].tasks[taskIndex].category
      ) {
        let category = this.model.taskData.taskSections[sectionIndex].tasks[
          taskIndex
        ].category
        if (category.length >= 1) {
          if (category[0] === 'asset') {
            let assetCategory = this.assetcategory.find(
              i => i.id === Number(category[1])
            )
            if (
              !this.model.taskData.taskSections[sectionIndex].tasks[taskIndex]
                .selectedResourceList.length
            ) {
              if (fromWo) {
                return `Every ${assetCategory.name}`
              }
              return `All ${assetCategory.name}`
            }
            let count = this.model.taskData.taskSections[sectionIndex].tasks[
              taskIndex
            ].selectedResourceList.length
            return `${count} ${assetCategory.name}`
          } else if (category[0] === 'space') {
            let spacecategory = this.spacecategory[category[1]]
            if (
              !this.model.taskData.taskSections[sectionIndex].tasks[taskIndex]
                .selectedResourceList.length
            ) {
              if (fromWo) {
                return `Every ${spacecategory}`
              }
              return `All ${spacecategory}`
            }
            let count = this.model.taskData.taskSections[sectionIndex].tasks[
              taskIndex
            ].selectedResourceList.length
            return `${count} ${spacecategory}`
          } else if (category[0] === 'building') {
            if (
              !this.model.taskData.taskSections[sectionIndex].tasks[taskIndex]
                .selectedResourceList.length
            ) {
              if (fromWo) {
                return `Every Building`
              }
              return `All Buildings`
            }
            let count = this.model.taskData.taskSections[sectionIndex].tasks[
              taskIndex
            ].selectedResourceList.length
            return `${count} Building(s)`
          } else if (category[0] === 'CURRENT_FLOOR') {
            if (fromWo) {
              return `Every Floor`
            }
            return `All Floor`
          } else if (category[0] === 'CURRENT_ASSET') {
            let secCateg = this.model.taskData.taskSections[sectionIndex]
              .category
            let assetCategory = null
            if (secCateg[0] === 'CURRENT_ASSET') {
              assetCategory = this.assetcategory.find(
                i => i.id === Number(this.model.woData.assetCategoryId)
              )
            } else if (secCateg[0] === 'asset') {
              assetCategory = this.assetcategory.find(
                i => i.id === Number(secCateg[1])
              )
            }

            if (
              !this.model.taskData.taskSections[sectionIndex].tasks[taskIndex]
                .selectedResourceList.length
            ) {
              if (fromWo) {
                return `Every ${assetCategory.name}`
              }
              return `All ${assetCategory.name}`
            }

            let count = this.model.taskData.taskSections[sectionIndex].tasks[
              taskIndex
            ].selectedResourceList.length
            return `${count} ${assetCategory.name}`
          } else if (category[0] === 'CURRENT_SPACE') {
            let secCateg = this.model.taskData.taskSections[sectionIndex]
              .category
            if (secCateg[0] === 'CURRENT_SPACE') {
              let spacecategory = null
              if (this.model.woData.workOrderType === 'single') {
                spacecategory = this.model.woData.spaceAssetDisplayName
              } else {
                spacecategory = this.spacecategory[
                  this.model.woData.spacecategoryId
                ]
              }
              if (this.model.woData.workOrderType === 'single') {
                return `${spacecategory}`
              }
              if (fromWo) {
                return `Every ${spacecategory}`
              }
              return `All ${spacecategory}`
            } else if (secCateg[0] === 'space') {
              let spacecategory = null
              spacecategory = this.spacecategory[secCateg[1]]
              if (this.model.woData.workOrderType === 'single') {
                return `${spacecategory}`
              }
              if (fromWo) {
                return `Every ${spacecategory}`
              }
              return `All ${spacecategory}`
            }
          } else if (category[0] === 'CURRENT_BUILDING') {
            let secCateg = this.model.taskData.taskSections[sectionIndex]
              .category
            if (secCateg[0] === 'CURRENT_BUILDING') {
              if (fromWo) {
                return `Every Building`
              }
              return `All Building(s)`
            } else if (secCateg[0] === 'building') {
              if (fromWo) {
                return `Every Building`
              }
              return `All Building(s)`
            }
          } else if (category[0] === 'ALL_SITES') {
            let secCateg = this.model.taskData.taskSections[sectionIndex]
              .category
            if (secCateg[0] === 'ALL_SITES') {
              if (fromWo) {
                return `Every Site`
              }
              return `All Site(s)`
            } else if (secCateg[0] === 'sites') {
              if (fromWo) {
                return `Every Site`
              }
              return `All Site(s)`
            }
          }
        }
      }
    },
    resourceLabel(sectionIndex, fromWo) {
      this.model.taskData.taskSections[sectionIndex].dummyValue = [1]
      if (this.model.woData.workOrderType === 'single') {
        let category = this.model.taskData.taskSections[sectionIndex].category
        if (category.length === 1) {
          if (this.model.woData.singleResource.resourceTypeEnum === 'SPACE') {
            return `${this.model.woData.spaceAssetDisplayName}`
          }
        } else {
          if (category[0] === 'asset') {
            let assetCategory = this.assetcategory.find(
              i => i.id === Number(category[1])
            )
            if (
              !this.model.taskData.taskSections[sectionIndex]
                .selectedResourceList.length
            ) {
              if (fromWo) {
                return `Every ${assetCategory.name}`
              }
              return `All ${assetCategory.name}`
            }
            let count = this.model.taskData.taskSections[sectionIndex]
              .selectedResourceList.length
            return `${count} ${assetCategory.name}`
          } else if (category[0] === 'space') {
            let spacecategory = this.spacecategory[category[1]]
            if (
              !this.model.taskData.taskSections[sectionIndex]
                .selectedResourceList.length
            ) {
              if (fromWo) {
                return `Every ${spacecategory}`
              }
              return `All ${spacecategory}`
            }
            let count = this.model.taskData.taskSections[sectionIndex]
              .selectedResourceList.length
            return `${count} ${spacecategory}`
          }
        }
        this.model.taskData.taskSections[sectionIndex].dummyValue = []
      } else {
        if (this.model.taskData.taskSections[sectionIndex].category) {
          let category = this.model.taskData.taskSections[sectionIndex].category
          if (category.length === 1) {
            if (
              this.model.woData.resourceType === 'ALL_FLOORS' &&
              !this.model.taskData.taskSections[sectionIndex]
                .selectedResourceList.length
            ) {
              if (fromWo) {
                return 'Every Floor'
              }
              return 'Every Floor'
            } else if (
              this.model.woData.resourceType === 'SPACE_CATEGORY' &&
              !this.model.taskData.taskSections[sectionIndex]
                .selectedResourceList.length
            ) {
              let woCateg = this.model.woData.spacecategoryId
              if (fromWo) {
                return `Every ${this.spacecategory[Number(woCateg)]}`
              }
              return `All ${this.spacecategory[Number(woCateg)]}`
            } else if (
              this.model.woData.resourceType === 'BUILDINGS' &&
              !this.model.taskData.taskSections[sectionIndex]
                .selectedResourceList.length
            ) {
              if (fromWo) {
                return `Every Building`
              }
              return `All Building(s)`
            } else if (
              this.model.woData.resourceType === 'ASSET_CATEGORY' &&
              !this.model.taskData.taskSections[sectionIndex]
                .selectedResourceList.length
            ) {
              let assetCategory = this.assetcategory.find(
                i => i.id === Number(this.model.woData.assetCategoryId)
              )
              if (fromWo) {
                return `Every ${assetCategory.name}`
              }
              return `All ${assetCategory.name}`
            } else if (
              this.model.woData.resourceType === 'ALL_SITES' &&
              !this.model.taskData.taskSections[sectionIndex]
                .selectedResourceList.length
            ) {
              if (fromWo) {
                return `Every Sites`
              }
              return `All Site(s)`
            }
            let count = this.model.taskData.taskSections[sectionIndex]
              .selectedResourceList.length
            if (this.model.woData.resourceType === 'ALL_FLOORS') {
              return `${count} Floor`
            } else if (this.model.woData.resourceType === 'SPACE_CATEGORY') {
              let woCateg = this.model.woData.spacecategoryId
              return `${count} ${this.spacecategory[Number(woCateg)]}`
            } else if (this.model.woData.resourceType === 'ASSET_CATEGORY') {
              let assetCategory = this.assetcategory.find(
                i => i.id === Number(this.model.woData.assetCategoryId)
              )
              return `${count} ${assetCategory.name}`
            } else if (this.model.woData.resourceType === 'ALL_SITES') {
              return `${count} Site`
            }
            return ''
          } else {
            if (category[0] === 'asset') {
              let assetCategory = this.assetcategory.find(
                i => i.id === Number(category[1])
              )
              if (
                !this.model.taskData.taskSections[sectionIndex]
                  .selectedResourceList.length
              ) {
                if (fromWo) {
                  return `Every ${assetCategory.name}`
                }
                return `All ${assetCategory.name}`
              }
              let count = this.model.taskData.taskSections[sectionIndex]
                .selectedResourceList.length
              return `${count} ${assetCategory.name}`
            } else if (category[0] === 'space') {
              let spacecategory = this.spacecategory[category[1]]
              if (
                !this.model.taskData.taskSections[sectionIndex]
                  .selectedResourceList.length
              ) {
                if (fromWo) {
                  return `Every ${spacecategory}`
                }
                return `All ${spacecategory}`
              }
              let count = this.model.taskData.taskSections[sectionIndex]
                .selectedResourceList.length
              return `${count} ${spacecategory}`
            } else if (category[0] === 'building') {
              if (
                !this.model.taskData.taskSections[sectionIndex]
                  .selectedResourceList.length
              ) {
                if (fromWo) {
                  return `Every Building`
                }
                return `All Buildings`
              }
              let count = this.model.taskData.taskSections[sectionIndex]
                .selectedResourceList.length
              return `${count} Bulding(s)`
            } else if (category[0] === 'site') {
              if (
                !this.model.taskData.taskSections[sectionIndex]
                  .selectedResourceList.length
              ) {
                if (fromWo) {
                  return `Every Site`
                }
                return `All Sites`
              }
              let count = this.model.taskData.taskSections[sectionIndex]
                .selectedResourceList.length
              return `${count} Site(s)`
            }
          }
        } else {
          this.model.taskData.taskSections[sectionIndex].dummyValue = []
        }
      }
    },
    getCategoryOptionsForTasksLabel(index) {
      if (
        this.categoryOptionsForTasks &&
        this.categoryOptionsForTasks.length > 0
      ) {
        return this.$getProperty(this, `categoryOptionsForTasks.${index}.label`)
      } else {
        return ''
      }
    },
    getCategoryOptionsForTasksValue(index) {
      if (
        this.categoryOptionsForTasks &&
        this.categoryOptionsForTasks.length > 0
      ) {
        return this.$getProperty(this, `categoryOptionsForTasks.${index}.value`)
      } else {
        return ''
      }
    },
  },
}
</script>
<style>
.cascader-task-block .el-cascader__label {
  display: none;
}
.fc-pm-form-task-table th {
  padding-bottom: 5px;
}
.cascade-label-hide .el-cascader__label,
.cascade-label-hide2 .el-cascader__label {
  display: none;
}
.cascade-label-hide .el-input__suffix-inner .el-icon-arrow-down {
  position: absolute;
  font-size: 16px;
  font-weight: 600;
  left: 0;
}
.cascade-label-hide2 .el-input__suffix-inner .el-icon-arrow-down {
  position: absolute;
  font-size: 16px;
  font-weight: 600;
  left: 5px;
}
.cascade-search-icon .el-input__suffix-inner .el-icon-arrow-down {
  top: 0;
  right: 17px;
}
.task-delete-icon-block {
  position: relative;
}
/* .task-delete-icon-block .fc-delete-icon{
  position: absolute;
  right: -27px;
  top: -8px;
} */
.trigger-search {
  position: absolute;
  top: 18px;
  right: 110px;
  font-weight: 600;
  font-size: 14px;
  color: #5a7591;
}
.task-search-icon {
  position: absolute;
  right: 37px;
  top: 23px;
  font-weight: 600;
  font-size: 14px;
  color: #5a7591;
}
.task-heading-select .el-select__tags {
  padding-top: 3px;
}
.task-heading-select .el-input.is-disabled .el-input__inner {
  height: 35px !important;
  line-height: 35px;
}
.task-th-delete-icon {
  padding-top: 16px;
}
.task-bg-banner {
  padding-top: 10px;
  padding-bottom: 10px;
  color: #39b2c2;
  font-size: 12px;
  text-align: center;
  border-radius: 3px;
  letter-spacing: 0.3px;
  background-color: #f1fdff;
}
.task-container-scroll {
  height: 210px;
  padding-bottom: 40px;
  overflow-x: hidden;
  overflow-y: scroll;
}
.cascade-label-hide .el-cascader .el-icon-arrow-down {
  transform: none;
}
.cascade-label-hide .el-input__suffix {
  transition: none !important;
}
@media screen and (max-width: 1280px) and (min-width: 800px) {
  .task-add-delete {
    margin-left: 20px;
  }
}
.filter-icon-search {
  line-height: 0px !important;
  font-size: 16px !important;
  cursor: pointer;
  position: absolute;
  top: 5px;
  right: 0;
  color: #667570;
}
.pm-task-select-th {
  padding-bottom: 0 !important;
  padding-top: 7px;
}
.pm-task-active-remove {
  color: #333333 !important;
  letter-spacing: 0.6px;
}
.fc-pm-form-task-table .el-select-group__title {
  width: 100%;
  max-width: 270px;
  padding-right: 20px;
  font-size: 11px;
  text-transform: uppercase;
  font-weight: bold;
  letter-spacing: 1px;
  color: #ef4f8f;
  padding-left: 20px;
  line-height: 30px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  padding-top: 5px;
  padding-bottom: 5px;
}
.pm-task-select-th .el-select-group__wrap:not(:last-of-type)::after {
  background: none;
}
@media screen and (max-width: 1280px) and (min-width: 800px) {
  .taskdialog2 .el-dialog {
    width: 46% !important;
  }
}
</style>
