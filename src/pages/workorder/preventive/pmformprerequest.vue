<template>
  <div class="scrollable130-y60">
    <div class="fc-pm-form-right-main">
      <div v-if="!model.isEdit" class="fc-grey-text mT10">
        NEW PLANNED MAINTENANCE
      </div>
      <div v-else class="fc-grey-text mT10">
        EDIT PLANNED MAINTENANCE
      </div>
      <div class="heading-black22 mB20">
        Maintenance for {{ resourceName() }}
      </div>
      <div class="fc-pm-main-bg fc-pm-main-bg2">
        <div class="fc-pm-main-content">
          <div class="fc-pm-main-content-H">
            PREREQUISITES
          </div>
          <div class="fc-heading-border-width43"></div>
          <div class="fc-pm-main-inner-container">
            <div class="fc-pm-task-form-table-con">
              <div
                class="label-txt-black"
                v-if="model.preRequestData.preRequestSections.length > 0"
              >
                Validation
              </div>
              <el-checkbox
                v-if="model.preRequestData.preRequestSections.length > 0"
                v-model="model.preRequestData.photoMandatory"
                class="pT20"
                >Photo Mandatory</el-checkbox
              >
              <!-- <el-checkbox @change="changeAllowNegativePrerequisiteValue()" v-if="model.preRequestData.preRequestSections.length > 0" v-model="model.preRequestData.allowNegative" class="pT20 block">Allow user to start work only after approve on negative inputs</el-checkbox> -->
              <div
                v-if="
                  model.preRequestData.preRequestSections.length > 0 &&
                    model.preRequestData.allowNegativePreRequisite === 3
                "
              >
                <el-form :model="model.preRequestData" :label-position="'top'">
                  <div class="fc-input-label-txt pT20 pB0">
                    Specify users, roles or groups who can approve
                  </div>
                  <el-form-item
                    prop="approvers"
                    class="mB10"
                    style="margin-left: -10px;"
                  >
                    <el-row
                      v-for="(approver, key) in model.preRequestData.approvers"
                      class="criteria-condition-block visibility-visible-actions max-width650px"
                      :key="key"
                    >
                      <el-col :span="6">
                        <el-select
                          v-model="approver.sharingType"
                          @change="getFieldDrop(approver)"
                          placeholder="Select"
                          class="fc-input-full-border-select2 width100"
                        >
                          <el-option
                            v-for="(field, index) in model.preRequestData
                              .approvalTypes"
                            :key="index"
                            :label="field.name"
                            :value="field.id"
                          ></el-option>
                        </el-select>
                      </el-col>
                      <el-col :span="6" class="mL20">
                        <el-select
                          v-model="approver.approversId"
                          placeholder="Select"
                          class="fc-input-full-border-select2 width100"
                        >
                          <el-option
                            v-for="(objList,
                            index) in approver.approvesDataType"
                            :key="index"
                            :label="objList.name"
                            :value="objList.id"
                          ></el-option>
                        </el-select>
                      </el-col>
                      <el-col
                        :span="2"
                        class="mL5 border-left visibility-hide-actions"
                      >
                        <img
                          src="~assets/add-icon.svg"
                          v-if="
                            key + 1 === model.preRequestData.approvers.length
                          "
                          style="height:18px;width:18px;"
                          class="delete-icon vertical-middle"
                          @click="addApprover"
                        />
                        <img
                          src="~assets/remove-icon.svg"
                          v-if="!(model.preRequestData.approvers.length === 1)"
                          style="height:18px;width:18px;"
                          class="delete-icon vertical-middle mL5"
                          @click="deleteApprovers(key)"
                        />
                      </el-col>
                    </el-row>
                  </el-form-item>
                </el-form>
              </div>
              <draggable
                :options="{ draggable: '.asf' }"
                :element="'div'"
                :list="model.preRequestData.preRequestSections"
              >
                <table
                  class="fc-pm-form-table mT40 fc-pm-form-task-table asf"
                  v-for="(preRequestSection, key) in model.preRequestData
                    .preRequestSections"
                  :key="key"
                >
                  <thead>
                    <tr
                      @click="
                        setCurrentPosition(
                          key,
                          model.preRequestData.preRequestSections[key]
                            .preRequests.length - 1
                        )
                      "
                      class="visibility-visible-actions"
                    >
                      <th style="width: 60%;">
                        <el-input
                          v-model="preRequestSection.name"
                          class="fc-input-border-remove task-input-bold"
                          style="width: 100%;margin-top: 20px;"
                        ></el-input>
                      </th>
                      <th></th>
                      <th></th>
                      <th style="width: 50px;text-align: right;">
                        <div
                          class="actions task-delete-icon-block"
                          style="right: 25px;"
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
                    :list="
                      model.preRequestData.preRequestSections[key].preRequests
                    "
                  >
                    <tr
                      v-for="(task, taskKey) in preRequestSection.preRequests"
                      :key="taskKey"
                      @click="setCurrentSection(key), setCurrentTask(taskKey)"
                      class="visibility-visible-actions pointer table-hover asd"
                    >
                      <td style="width: 60%;">
                        <div class="flex-middle">
                          <div class="dot-green mL5"></div>
                          <el-input
                            :ref="'task-' + key + '-' + taskKey"
                            @keyup.native.enter="
                              onTaskEnter($event, taskKey, task.subject)
                            "
                            @paste.native="
                              onTaskPaste(
                                preRequestSection.preRequests,
                                taskKey,
                                $event
                              )
                            "
                            v-model="
                              preRequestSection.preRequests[taskKey].name
                            "
                            style="width: 100%;"
                            class="fc-input-border-remove mL10"
                            placeholder="Write a prerequisite name"
                          ></el-input>
                        </div>
                      </td>
                      <td style="clear:both;">
                        <div class="flex-middle mR10 justify-content-end">
                          <div class="flex-middle">
                            <div class="dot-green-postivite"></div>
                            <div class="pL5 label-txt-black">
                              {{
                                preRequestSection.preRequests[taskKey]
                                  .additionInfo.truevalue
                              }}
                            </div>
                          </div>
                          <div class="flex-middle pL30">
                            <div class="dot-red-negative"></div>
                            <div class="pL5 label-txt-black">
                              {{
                                preRequestSection.preRequests[taskKey]
                                  .additionInfo.falsevalue
                              }}
                            </div>
                          </div>
                        </div>
                      </td>
                      <td class="fc-pm-popover-btn2" style="width: 50px;">
                        <f-popover
                          placement="right"
                          v-model="
                            preRequestSection.preRequests[taskKey].additionInfo
                              .show
                          "
                          popper-class="calender-popup"
                          title="Options"
                          trigger="click"
                          width="250"
                          @save="savePreRequestSettings"
                          @show="
                            showPreRequestSettingsDialog(
                              preRequestSection.preRequests[taskKey]
                            )
                          "
                          @close="cancelPreRequestSettings"
                          @hide="cancelPreRequestSettings"
                          confirmTitle="SAVE"
                        >
                          <i
                            class="el-icon-edit edit-icon-border visibility-hide-actions"
                            slot="reference"
                          ></i>
                          <template slot="content">
                            <el-row class="mT20">
                              <el-col :span="10">
                                <div>
                                  <el-input
                                    type="text"
                                    v-model="preRequestSettingsEdit.truevalue"
                                    :placeholder="'+ve value'"
                                    class="fc-input-full-border2 width200px"
                                  >
                                  </el-input>
                                </div>
                              </el-col>
                            </el-row>
                            <el-row class="mT20">
                              <el-col :span="10">
                                <div>
                                  <el-input
                                    type="text"
                                    v-model="preRequestSettingsEdit.falsevalue"
                                    :placeholder="'-ve value'"
                                    class="fc-input-full-border2 width200px"
                                  >
                                  </el-input>
                                </div>
                              </el-col>
                            </el-row>
                          </template>
                        </f-popover>
                      </td>
                      <td style="width: 50px;">
                        <div
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
              <div
                class="mT30 text-center"
                v-if="!model.preRequestData.preRequestSections.length"
              >
                <div>
                  <inline-svg
                    src="svgs/emptystate/cartempty"
                    iconClass="icon icon-xxxlg"
                  ></inline-svg>
                </div>
                <el-button
                  class="fc-btn-green-medium-fill mT20 mL30"
                  @click="addNewPreRequest"
                  >ADD PREREQUISITE</el-button
                >
                <el-button
                  v-if="model.preRequestData.preRequestSections.length > 0"
                  class="fc-btn-green-medium-border mL20"
                  @click="addNewSection"
                  >ADD SECTION</el-button
                >
              </div>
              <div class="text-left" v-else>
                <el-button
                  class="fc-btn-green-medium-fill mT20"
                  @click="addNewPreRequest"
                  >ADD PREREQUISITE</el-button
                >
                <el-button
                  v-if="model.preRequestData.preRequestSections.length > 0"
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
            >PROCEED TO NEXT
            <img
              src="~assets/arrow-pointing-white-right.svg"
              width="17px"
              class="fR"
          /></el-button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import PMMixin from '@/mixins/PMMixin'
import Draggable from 'vuedraggable'
import FPopover from '@/FPopover'
export default {
  mixins: [PMMixin],
  props: ['model'],
  components: {
    Draggable,
    FPopover,
  },
  data() {
    return {
      currentIndex: 0,
      currentTaskIndex: 0,
      inputType: null,
      showSectionSettings: false,
      sectionSettingsEdit: {},
      preRequestSettingsEdit: { truevalue: null, falsevalue: null },
      showPreRequestSettings: false,
    }
  },
  created() {
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')
  },
  computed: {
    ...mapState({
      users: state => state.users,
      teams: state => state.groups,
      roles: state => state.roles,
    }),
  },
  methods: {
    changeAllowNegativePrerequisiteValue() {
      if (this.model.preRequestData.allowNegative) {
        this.model.preRequestData.allowNegativePreRequisite = 3
      } else {
        this.model.preRequestData.allowNegativePreRequisite = 2
      }
    },
    addApprover() {
      this.model.preRequestData.approvers.push({
        sharingType: null,
        approversId: null,
      })
    },
    deleteApprovers(index) {
      this.$delete(this.model.preRequestData.approvers, index)
    },
    getFieldDrop(approver) {
      approver.approversId = null
      approver.approvesDataType = []
      if (approver.sharingType === 1) {
        approver.approvesDataType = this.users
      } else if (approver.sharingType === 2) {
        approver.approvesDataType = this.roles
      } else if (approver.sharingType === 3) {
        approver.approvesDataType = this.teams
      }
    },
    onTaskEnter($event, index, subject) {
      if (!$event.target || $event.target.selectionStart !== 0 || !subject) {
        index++
      }
      this.addNewPreRequest(index)
    },
    showPreRequestSettingsDialog(prerequisite) {
      this.preRequestSettingsEdit = {
        truevalue: null,
        falsevalue: null,
      }
      this.preRequestSettingsEdit.truevalue =
        prerequisite.additionInfo.truevalue
      this.preRequestSettingsEdit.falsevalue =
        prerequisite.additionInfo.falsevalue
    },
    savePreRequestSettings() {
      let task = this.model.preRequestData.preRequestSections[this.currentIndex]
        .preRequests[this.currentTaskIndex]
      task.additionInfo.truevalue = this.preRequestSettingsEdit.truevalue
      task.additionInfo.falsevalue = this.preRequestSettingsEdit.falsevalue
      task.additionInfo.show = false
    },
    cancelPreRequestSettings() {
      this.showPreRequestSettings = false
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
    addNewPreRequest(preRequestIndex) {
      if (this.model.preRequestData.preRequestSections.length > 0) {
        let preRequestObj = {
          name: '',
          description: '',
          inputType: null,
          attachmentRequired: false,
          enableInput: false,
          readingFieldId: null,
          selectedResourceList: [],
          isIncludeResource: null,
          readingFields: [],
          additionInfo: {
            truevalue: null,
            falsevalue: null,
            defaultValue: '',
            failureValue: '',
            woCreateTemplateId: null,
          },
        }

        preRequestObj.attachmentRequired = this.model.preRequestData.preRequestSections[
          this.currentIndex
        ].attachmentRequired
        preRequestObj.enableInput = this.model.preRequestData.preRequestSections[
          this.currentIndex
        ].enableInput
        preRequestObj.validation = this.model.preRequestData.preRequestSections[
          this.currentIndex
        ].validation
        if (
          this.model.preRequestData.preRequestSections[this.currentIndex]
            .additionInfo.truevalue
        ) {
          preRequestObj.additionInfo.truevalue = this.model.preRequestData.preRequestSections[
            this.currentIndex
          ].additionInfo.truevalue
        }
        if (
          this.model.preRequestData.preRequestSections[this.currentIndex]
            .additionInfo.falsevalue
        ) {
          preRequestObj.additionInfo.falsevalue = this.model.preRequestData.preRequestSections[
            this.currentIndex
          ].additionInfo.falsevalue
        }
        preRequestObj.inputType = this.model.preRequestData.preRequestSections[
          this.currentIndex
        ].inputType

        preRequestIndex =
          preRequestIndex >= 0
            ? preRequestIndex
            : this.model.preRequestData.preRequestSections[this.currentIndex]
                .preRequests.length
        this.model.preRequestData.preRequestSections[
          this.currentIndex
        ].preRequests.splice(preRequestIndex, 0, preRequestObj)

        this.currentTaskIndex = this.currentTaskIndex + 1
        this.$nextTick(() => {
          let ref = this.$refs[
            'task-' + this.currentIndex + '-' + preRequestIndex
          ]
          if (ref) {
            ref[0].focus()
          }
        })
      } else {
        this.addNewSection()
      }
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
              this.addNewPreRequest(event, ++index)
              tasks[index].name = subject
            }
          })
          event.preventDefault()
        }
      }
    },
    addNewSection() {
      let count = this.model.preRequestData.preRequestSections.length
      this.model.preRequestData.preRequestSections.push({
        name: `Untitled Section ${count + 1}`,
        dummyValue: [1],
        selectedResourceList: [],
        inputType: null,
        isIncludeResource: null,
        attachmentRequired: false,
        enableInput: false,
        readingFieldId: null,
        validation: null,
        additionInfo: {
          truevalue: 'Yes',
          falsevalue: 'No',
          defaultValue: '',
          failureValue: '',
          woCreateTemplateId: null,
        },
        preRequests: [
          {
            name: '',
            description: '',
            inputType: null,
            attachmentRequired: false,
            enableInput: false,
            readingFieldId: null,
            selectedResourceList: [],
            isIncludeResource: null,
            readingFields: [],
            additionInfo: {
              truevalue: 'Yes',
              falsevalue: 'No',
              defaultValue: '',
              failureValue: '',
              woCreateTemplateId: null,
            },
          },
        ],
      })
      this.currentIndex = count
      this.currentTaskIndex = 0
    },
    moveToNext() {
      let validated = this.validatePreRequestForm()
      if (validated) {
        this.$emit('next')
      }
    },
    moveToPrevious() {
      this.$emit('previous')
    },
    deleteTask(sectionIndex, preRequestIndex) {
      this.currentTaskIndex = 0
      this.model.preRequestData.preRequestSections[
        sectionIndex
      ].preRequests.splice(preRequestIndex, 1)
      if (
        this.model.preRequestData.preRequestSections[sectionIndex].preRequests
          .length === 0
      ) {
        this.model.preRequestData.preRequestSections.splice(sectionIndex, 1)
      }
    },
    deleteSection(sectionIndex) {
      this.currentIndex = 0
      this.currentTaskIndex = 0
      this.model.preRequestData.preRequestSections.splice(sectionIndex, 1)
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
