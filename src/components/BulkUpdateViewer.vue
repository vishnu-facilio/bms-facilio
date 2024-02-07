<template>
  <div>
    <f-dialog
      :append-to-body="true"
      :visible.sync="fieldUpdateValue"
      :width="'45%'"
      maxHeight="300px"
      title="Bulk Update"
      @save="actionSave(fieldMatcher)"
      @close=";(fieldUpdateValue = false), actioncancel()"
      confirmTitle="UPDATE"
    >
      <div class="height330 overflow-y-scroll pB50">
        <div v-for="(element, index) in fieldMatcher" :key="index">
          <el-row
            class="visibility-visible-actions fc-row-hover pT10 pB10 pL10 pointer"
          >
            <el-col :span="1">
              <div class="criteria-alphabet-block pT6">
                <div class="alphabet-circle">
                  {{ index + 1 }}
                </div>
              </div>
            </el-col>
            <el-col :span="9" class="mL10">
              <el-select
                v-model="element.field"
                @change="statusFieldName(element, index)"
                filterable
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="(field, index) in updateFields"
                  :disabled="!checkUpdate(field)"
                  :key="index"
                  :label="field.displayName"
                  :value="field.name"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="9">
              <div>
                <div v-if="!element.fieldObj">
                  <el-input
                    v-model="element.value"
                    @change="checkinput(element)"
                    class="fc-input-full-border-select2 mL40 width100"
                  ></el-input>
                </div>
                <div v-else-if="element.isSpacePicker">
                  <el-input
                    v-model="selectedspace"
                    @change="checkinput(element)"
                    type="text"
                    :placeholder="$t('common._common.to_search_type')"
                    class="fc-input-full-border-select2 width100 mL40"
                  >
                    <i
                      v-if="searchbool"
                      @click=";(chooserVisibility = true), assignindex(index)"
                      slot="suffix"
                      style="
                        line-height: 0px !important;
                        font-size: 16px !important;
                        cursor: pointer;
                      "
                      class="el-input__icon el-icon-search"
                    ></i>
                  </el-input>
                  <space-asset-chooser
                    @associate="associate"
                    :visibility.sync="chooserVisibility"
                  ></space-asset-chooser>
                </div>
                <div v-else-if="element.isAssigned">
                  <div class="fc-border-input-div2 width100 mL40">
                    <span>{{ getTeamStaffLabel(confirmRequest) }}</span>
                    <span style="float: right; padding-right: 12px">
                      <img
                        class="svg-icon team-down-icon"
                        src="~assets/down-arrow.svg"
                      />
                    </span>
                  </div>
                  <f-assignment
                    :model="confirmRequest"
                    viewtype="form"
                    @value="updateConfirmRequest($event, index)"
                  ></f-assignment>
                </div>

                <div v-else>
                  <el-input
                    type="number"
                    v-if="
                      element.fieldObj[0].dataTypeEnum._name === 'NUMBER' ||
                        element.fieldObj[0].dataType === 'DECIMAL'
                    "
                    v-model="element.value"
                    @change="checkinput(element)"
                    class="fc-input-full-border-select2 mL40 width140px"
                  ></el-input>
                  <el-select
                    v-else-if="
                      element.fieldObj[0].dataTypeEnum._name === 'BOOLEAN'
                    "
                    @change="checkinput(element)"
                    v-model="element.value"
                    class="fc-input-full-border-select2 mL40 width140px"
                  >
                    <el-option
                      :label="
                        element.fieldObj[0].trueVal
                          ? element.fieldObj[0].trueVal
                          : 'Yes'
                      "
                      value="true"
                    ></el-option>
                    <el-option
                      :label="
                        element.fieldObj[0].falseVal
                          ? element.fieldObj[0].falseVal
                          : 'No'
                      "
                      value="false"
                    ></el-option>
                  </el-select>
                  <el-select
                    v-else-if="
                      element.fieldObj[0].dataTypeEnum._name === 'ENUM'
                    "
                    @change="checkinput(element)"
                    filterable
                    collapse-tags
                    v-model="element.value"
                    class="fc-input-full-border-select2 width100 mL40"
                  >
                    <el-option
                      v-for="(value, key) in picklistOptions[element.field]"
                      :key="key"
                      :label="value"
                      :value="key"
                    ></el-option>
                  </el-select>

                  <el-row
                    class="mL40"
                    v-else-if="
                      element.fieldObj[0].dataTypeEnum._name === 'DATE' ||
                        element.fieldObj[0].dataTypeEnum._name === 'DATE_TIME'
                    "
                  >
                    <el-date-picker
                      @change="
                        changeDataType(element, index), checkinput(element)
                      "
                      v-model="element.value"
                      :type="'datetime'"
                      class="fc-input-full-border-select2 width100 date-editor"
                      style="width: 290px"
                    ></el-date-picker>
                  </el-row>

                  <FLookupField
                    v-else-if="
                      element.fieldObj[0].dataTypeEnum._name === 'LOOKUP' &&
                        (element.fieldObj[0].displayType._name ===
                          'LOOKUP_SIMPLE' ||
                          element.fieldObj[0].displayType._name ===
                            'LOOKUP_POPUP')
                    "
                    @recordSelected="
                      changeDataType(element, index), checkinput(element)
                    "
                    :data-test-selector="`${element.fieldObj[0].name}`"
                    :key="`${index} ${element.fieldObj[0].id}`"
                    :model.sync="element.valueArray"
                    :field="element.fieldObj[0]"
                    class="width100 mL40 bulk-update-flookup"
                    :canShowLookupWizard="showLookupFieldWizard"
                    @showLookupWizard="showLookupWizard"
                    @setLookupFieldValue="setLookupFieldValue"
                  ></FLookupField>

                  <FLookupField
                    v-else-if="isMultiLookUpField(element)"
                    @recordSelected="
                      changeDataType(element, index), checkinput(element)
                    "
                    :data-test-selector="`${element.fieldObj[0].name}`"
                    :key="`${index} ${element.fieldObj[0].id}`"
                    :model.sync="element.valueArray"
                    :field="getMultiLookUpFieldObj(element.fieldObj[0])"
                    class="width100 mL40 bulk-update-flookup"
                    :canShowLookupWizard="showLookupFieldWizard"
                    @showLookupWizard="showLookupWizard"
                    @setLookupFieldValue="setLookupFieldValue"
                  ></FLookupField>

                  <el-input
                    v-else
                    v-model="element.value"
                    @change="checkinput(element)"
                    class="fc-input-full-border-select2 mL40 width100"
                  ></el-input>
                </div>
              </div>
            </el-col>
            <el-col :span="4">
              <div class="visibility-hide-actions pT15 pL55 pointer">
                <img
                  src="~assets/add-icon.svg"
                  @click="addRow(element)"
                  class="pointer"
                />
                <img
                  src="~assets/remove-icon.svg"
                  v-if="indexbool"
                  @click="deleteRow(element, index)"
                  class="pointer mL5"
                />
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
      <FLookupFieldWizard
        v-if="showLookupFieldWizard"
        :canShowLookupWizard.sync="showLookupFieldWizard"
        :selectedLookupField="selectedLookupField"
        :withReadings="true"
        @setLookupFieldValue="setLookupFieldValue"
      ></FLookupFieldWizard>
    </f-dialog>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import { getFieldOptions } from 'util/picklist'
import FAssignment from '@/FAssignment'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import FDialog from '@/FDialogNew'
import { isEmpty } from '@facilio/utils/validation'
import FLookupField from '@/forms/FLookupField'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import TeamStaffMixin from '@/mixins/TeamStaffMixin'

export default {
  created() {
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadGroups')
  },
  mixins: [TeamStaffMixin],
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      ticketcategory: state => state.ticketCategory,
      assetCategory: state => state.assetCategory,
      ticketpriority: state => state.ticketPriority,
      moduleFields: state => state.view.metaInfo.fields,
    }),
  },
  data() {
    return {
      confirmRequest: {
        assignedTo: {
          id: null,
        },
        assignmentGroup: {
          id: null,
        },
      },
      showLookupFieldWizard: false,
      indexcount: 0,
      checkbool: false,
      indexbool: false,
      updateFields: [],
      fieldbool: false,
      fielddeletelist: [],
      fieldupdatelist: [],
      fieldnewlist: [],
      searchbool: true,
      selectedspace: null,
      referindex: -1,
      index: null,
      fieldContext: '',
      chooserVisibility: false,
      picklistOptions: {},
      fieldUpdateValue: true,
      spacePicker: false,
      fieldMatcher: [],
      selectedLookupField: null,
    }
  },
  components: {
    FAssignment,
    FDialog,
    SpaceAssetChooser,
    FLookupField,
    FLookupFieldWizard,
  },
  props: ['module', 'fieldlist'],
  watch: {},
  mounted() {
    this.addRow()
    this.update()
  },
  methods: {
    updateConfirmRequest(value, index) {
      if (value.assignedTo.id != null) {
        this.confirmRequest.assignedTo.id = value.assignedTo.id
        this.fieldMatcher[index].value = value.assignedTo.id
      }
      if (value.assignmentGroup.id != null) {
        this.confirmRequest.assignmentGroup.id = value.assignmentGroup.id
        this.fieldMatcher[index].valueArray = value.assignmentGroup.id
      }
    },
    checkinput(selectedField) {
      this.fieldupdatelist.push(selectedField.field)
    },
    actioncancel() {
      this.$emit('closed')
    },
    assignindex(val) {
      this.index = val
    },
    associate(selectedObj) {
      this.searchbool = false
      this.fieldMatcher[this.index].parseLabel = selectedObj
      this.selectedspace = selectedObj.name

      this.chooserVisibility = false
    },
    update() {
      this.fieldnewlist = this.fieldlist
      if (this.moduleFields && this.fieldnewlist) {
        this.updateFields = this.moduleFields.filter(
          field => !field.default || this.fieldnewlist.includes(field.name)
        )
        this.updateFields.find(function(fm) {
          if (fm.name === 'assignedTo') {
            fm.displayName = 'Team / Staff'
          }
        })
      }
    },
    checkUpdate(field) {
      if (
        this.fieldupdatelist.find(function(fm) {
          return fm === field.name
        })
      ) {
        return false
      } else {
        return true
      }
    },
    statusFieldName(selectedField, index) {
      if (selectedField != undefined)
        this.fieldupdatelist.push(selectedField.field)

      let field = this.moduleFields.filter(
        field => field.name === selectedField.field
      )
      this.fieldMatcher[index].fieldObj = field
      this.fieldMatcher[index].isSpacePicker = false
      this.fieldMatcher[index].isAssigned = false
      if (field.length > 0) {
        this.fieldMatcher[index].columnName = field[0].completeColumnName
        if (field[0].dataTypeEnum._name === 'ENUM') {
          this.$set(this.picklistOptions, field[0].name, field[0].enumMap)
        }
        if (field[0].dataTypeEnum._name === 'LOOKUP' && field[0].specialType) {
          if (field[0].specialType === 'requester') {
            this.loadPickList(field[0].lookupModule.name, field[0].name)
          } else {
            this.loadSpecialTypePickList(
              field[0].specialType,
              field[0].name,
              index
            )
          }
        } else if (
          field[0].dataTypeEnum._name === 'LOOKUP' &&
          field[0].lookupModule
        ) {
          if (field[0].lookupModule.name === 'ticketpriority') {
            let priority = {}
            this.ticketpriority.forEach(d => {
              priority[d.id] = d.displayName
            })
            this.$set(this.picklistOptions, field[0].name, priority)
          } else if (field[0].lookupModule.name === 'resource') {
            this.loadSpecialTypePickList(
              field[0].lookupModule.name,
              field[0].name,
              index
            )
          } else {
            this.loadPickList(field[0].lookupModule.name, field[0].name)
          }
        }
        this.$forceUpdate()
      }
    },
    async loadPickList(moduleName, fieldName) {
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: moduleName, skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$set(this.picklistOptions, fieldName, options)
      }
    },
    changeDataType(criteria, index) {
      if (
        !isEmpty(index) &&
        !isEmpty(criteria[index]) &&
        (criteria[index].fieldObj[0].dataTypeEnum._name === 'DATE_TIME' ||
          criteria[index].fieldObj[0].dataTypeEnum._name === 'DATE')
      ) {
        if (criteria[index].value) {
          let interval = this.$helpers.daysHoursMinuToSec(criteria[index].value)
          criteria[index].value = interval * 1000
          this.fieldMatcher[index].value = criteria[index].value
        }
      } else {
        if (criteria.valueArray) {
          criteria.value = { id: criteria.valueArray }
        }
      }
      this.$forceUpdate()
    },
    actionSave(datas) {
      let data = this.$helpers.cloneObject(datas)
      if (data) {
        this.$emit('submit', data)
      }
    },
    addRow() {
      this.indexcount++
      let { fieldMatcher } = this
      if (this.indexcount > 1) {
        this.indexbool = true
      } else {
        this.indexbool = false
      }
      if (this.moduleFields && this.fieldlist) {
        this.updateFields = this.moduleFields.filter(
          field => !field.default || this.fieldlist.includes(field.name)
        )
        this.updateFields.find(function(fm) {
          if (fm.name === 'assignedTo') {
            fm.displayName = 'Team / Staff'
          }
        })
      }

      let newField = {
        field: '',
        isSpacePicker: false,
        isAssigned: false,
        value: null,
        parseLabel: null,
        valueArray: null,
        dateObject: {},
        fieldObj: [
          {
            displayName: '',
            dataTypeEnum: {
              _name: '',
            },
          },
        ],
      }

      this.$set(this.fieldMatcher, fieldMatcher.length, newField)
    },
    deleteRow(element, index) {
      this.indexcount--
      if (this.indexcount > 1) {
        this.indexbool = true
      } else {
        this.indexbool = false
      }

      for (let i = 0; i < this.fieldupdatelist.length; i++) {
        if (this.fieldupdatelist[i] === element.field) {
          this.referindex = i
        }
      }
      if (this.referindex != -1) {
        this.fieldupdatelist.splice(this.referindex, 1)
      }
      this.referindex = -1
      // this.fielddeletelist.push(element.field)
      this.fieldMatcher.splice(index, 1)
    },
    loadSpecialTypePickList(specialType, fieldName, index) {
      let self = this
      let pickOption = {}
      if (specialType === 'users' || specialType === 'groups') {
        this.fieldMatcher[index].isAssigned = true
      } else if (specialType === 'basespace') {
        this.fieldMatcher[index].isSpacePicker = true
        let spaceList = self.$store.state.spaces
        for (let space of spaceList) {
          pickOption[space.id] = space.name
        }
        self.$set(self.picklistOptions, fieldName, pickOption)
      } else if (specialType === 'resource') {
        this.fieldMatcher[index].isSpacePicker = true
      } else if (specialType === 'sourceType') {
        self.$set(self.picklistOptions, fieldName, this.$constants.SourceType)
      }
    },
    showLookupWizard(field, canShow) {
      this.selectedLookupField = { ...field, config: {} }
      this.showLookupFieldWizard = canShow
    },
    setLookupFieldValue(value) {
      let { field } = value
      let { selectedItems } = field
      let { fieldMatcher, selectedLookupField } = this
      let currentFieldIndex = fieldMatcher.findIndex(field => {
        let name = this.$getProperty(field, 'fieldObj.0.name')
        return name === selectedLookupField.name
      })
      let currentField = fieldMatcher[currentFieldIndex]
      if (field.multiple) {
        let valueArrayItems = []
        selectedItems.forEach(selectedItem => {
          let { value } = selectedItem || {}
          valueArrayItems.push(value)
        })
        currentField = { ...currentField, valueArray: valueArrayItems }
      } else {
        currentField = { ...currentField, valueArray: selectedItems[0].value }
      }
      if (!isEmpty(fieldMatcher[currentFieldIndex])) {
        this.$set(this.fieldMatcher, currentFieldIndex, currentField)
        this.changeDataType(currentField, currentFieldIndex)
        this.checkinput(currentField)
      }
      this.showLookupFieldWizard = false
    },
    isMultiLookUpField(element) {
      let { fieldObj } = element || {}
      let { displayType, dataTypeEnum } = fieldObj[0] || {}
      let { _name: displayTypeName } = displayType || {}
      let { _name: dataTypeEnumName } = dataTypeEnum || {}
      if (
        dataTypeEnumName === 'MULTI_LOOKUP' &&
        ['MULTI_LOOKUP_POPUP', 'MULTI_LOOKUP_SIMPLE'].includes(displayTypeName)
      ) {
        return true
      }
      return false
    },
    getMultiLookUpFieldObj(variable) {
      variable['multiple'] = true
      return variable
    },
  },
}
</script>

<style lang="scss">
.bulk-update-flookup {
  .el-input {
    .el-input__prefix {
      right: 5px;
      left: 95%;
      z-index: 10;
      .fc-lookup-icon {
        margin-top: 12px;
      }
    }
    .el-input__suffix {
      .el-icon-circle-close {
        padding-left: 30px;
      }
    }
  }
}
</style>
