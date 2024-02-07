<template>
  <div>
    <!-- Header Section -->
    <setup-header>
      <template #heading>
        {{ $t('common._common.variables') }}
      </template>
      <template #description>
        {{ $t('common._common.list_of_all_variables') }}
      </template>
      <template #actions>
        <el-button
          type="primary"
          @click="addNewGroupOrVariable('group')"
          class="fc-secondary-btn"
          >{{ $t('common.header.add_group') }}</el-button
        >
        <el-button
          type="primary"
          @click="addNewGroupOrVariable('variable')"
          class="fc-primary-btn"
          >{{ $t('common.products.add_variable') }}</el-button
        >
      </template>
      <template #filter>
        <div class="group-select">
          <div class="fc-black-13 text-left bold pB5">
            {{ $t('setup.setupLabel.group_filter') }}
          </div>
          <el-select
            filterable
            v-model="selectedGroupId"
            @change="selectGroup()"
            class="fc-input-full-border-select2 fc-tag resource-search prefix-left-align skip-disable"
          >
            <el-option-group
              v-for="group in allGroups"
              :key="group.label"
              :label="group.label"
            >
              <el-option
                v-for="item in group.options"
                :key="item.value"
                :label="item.name"
                :value="item.value"
              >
              </el-option>
            </el-option-group>
          </el-select>
        </div>
      </template>
      <template #searchAndPagination>
        <i
          class="el-icon-search pointer fc-page-search"
          @click="searchInputShow"
          v-if="searchShow"
        ></i>

        <el-input
          size="medium"
          v-model="searchData"
          @input="searchListData()"
          class="width100 fc-input-full-border2 fc-input-full-border2-prefix fc-variable-search"
          placeholder="Search"
          v-if="searchInputHide"
        >
          <i
            slot="prefix"
            class="el-input__icon pointer fc-grey8 f14 fwBold el-icon-search mR5 pT3"
          ></i>
          <i class="el-icon-close" slot="suffix" @click="hideSearchInput"></i>
        </el-input>
      </template>
    </setup-header>
    <!-- table list -->
    <setup-loader v-if="loading">
      <template #setupLoading>
        <spinner :show="loading" size="80"></spinner>
      </template>
    </setup-loader>
    <setup-empty v-else-if="$validation.isEmpty(variableList) && !loading">
      <template #emptyImage>
        <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
      </template>
      <template #emptyHeading>
        {{ $t('common.products.no_variables_available') }}
      </template>
      <template #emptyDescription> </template>
    </setup-empty>
    <div
      v-else-if="!$validation.isEmpty(groupList) && !loading"
      class="p15 fc-setup-table-layout-scroll"
    >
      <div v-for="groupData in groupList" :key="groupData.id">
        <div
          v-if="!$validation.isEmpty(filteredData[groupData.id]) && !loading"
        >
          <div>
            <div class="fc-setup-group-header visibility-visible-actions">
              <div class="fc-white-14 bold text-left flex-middle">
                <div>{{ groupData.name }}</div>
                <div class="fc-white-12 fw4 pL10 fc-italic">
                  {{ '(' + groupData.linkName + ')' }}
                </div>
              </div>
              <div class="visibility-hide-actions flex-middle">
                <div
                  @click.stop="deleteGroup('group', { group: groupData })"
                  class="pointer"
                  :title="$t('setup.users_management.delete_group')"
                  v-tippy
                >
                  <inline-svg
                    src="svgs/delete"
                    iconClass="icon icon-sm fc-lookup-icon fill-white"
                    class="mR10"
                  ></inline-svg>
                </div>
                <div
                  @click.stop="
                    editGroupOrVariable('group', { group: groupData })
                  "
                  class="pointer"
                >
                  <inline-svg
                    src="svgs/edit"
                    iconClass="icon icon-sm fc-lookup-icon fill-white edit-icon"
                    :title="$t('common.header.edit_group')"
                    v-tippy
                  ></inline-svg>
                </div>
              </div>
            </div>
            <div class="mB20">
              <el-header class="fc-table-header-sticky" height="40">
                <el-row>
                  <el-col :span="6" class="pL10">
                    {{ $t('common._common.name') }}
                  </el-col>
                  <el-col :span="6" class="pL8">
                    {{ $t('setup.setupLabel.link_name') }}
                  </el-col>
                  <el-col :span="4" class="pL5 type-width">
                    {{ $t('common._common.type') }}
                  </el-col>
                  <el-col :span="4" class="">
                    {{ $t('asset.readings.value') }}
                  </el-col>
                </el-row>
              </el-header>
              <el-table
                ref="variableTable"
                :data="filteredData[groupData.id]"
                :cell-style="{ padding: '12px 30px' }"
                style="width: 100%"
                height="100%"
                class="fc-setup-table fc-setup-table-header-hide"
                :fit="true"
              >
                <el-table-column
                  label=""
                  prop="name"
                  width="250px"
                ></el-table-column>

                <el-table-column
                  prop="displayLinkName"
                  class="visibility-visible-actions"
                  width="250px"
                  label=""
                >
                  <template v-slot="variable">
                    <div class="flex-middle">
                      <div>
                        {{ variable.row.displayLinkName }}
                      </div>
                      <div
                        @click="copyLinkName(variable.row.displayLinkName)"
                        class="pointer pL5 visibility-hide-actions"
                      >
                        <inline-svg
                          src="svgs/copy2"
                          iconClass="icon icon-lg vertical-bottom"
                        ></inline-svg>
                      </div>
                    </div>
                  </template>
                </el-table-column>

                <el-table-column
                  label=""
                  prop="displayType"
                  class-name="text-capitalize"
                  width="150px"
                >
                </el-table-column>

                <el-table-column label="" prop="displayValue" width="200px">
                </el-table-column>

                <el-table-column
                  class="visibility-visible-actions"
                  width="200px"
                >
                  <template v-slot="variable">
                    <div class="text-center flex-middle">
                      <i
                        class="el-icon-delete fc-delete-icon visibility-hide-actions pR15"
                        data-arrow="true"
                        :title="$t('common.header.delete_variable')"
                        @click="deleteVariable(variable.row)"
                        v-tippy
                      ></i>
                      <i
                        class="el-icon-edit edit-icon visibility-hide-actions"
                        data-arrow="true"
                        :title="$t('common.products.edit_variable')"
                        @click="
                          editGroupOrVariable('variable', {
                            variable: variable.row,
                          })
                        "
                        v-tippy
                      ></i>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- group dialog -->
    <el-dialog
      :visible="showGroupDeletionWarning"
      width="30%"
      :title="$t('common.header.warning')"
      class="fieldchange-Dialog pB15 fc-dialog-center-container"
      custom-class="dialog-header-padding"
      :append-to-body="true"
      :before-close="
        () => {
          showGroupDeletionWarning = false
        }
      "
    >
      <div class="overflow-y-scroll pB40 pT10" style="word-break: break-word;">
        {{ $t('common._common.group_associated_with_variables') }}
        <br />
        <br />
        {{ $t('common.header.remove_variables_before_delete') }}
      </div>
    </el-dialog>

    <!-- add new form -->
    <AddNewForm
      v-if="showAddForm"
      :groupList="groupList"
      :selectedVariable="selectedVariable"
      :selectedGroup="selectedGroup"
      :selectedGroupId="selectedGroupId"
      :formType="type"
      :isNew="isNew"
      @onClose="showAddForm = false"
      @onSave="saveRecord"
    >
    </AddNewForm>
  </div>
</template>

<script>
import AddNewForm from './GroupAndVariableForm'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import moment from 'moment-timezone'
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import SetupHeader from 'pages/setup/components/SetupHeaderTabs'

export default {
  components: {
    AddNewForm,
    SetupHeader,
    SetupLoader,
    SetupEmpty,
  },
  data() {
    return {
      bulkVariableEdit: false,
      type: null,
      showAddForm: false,
      selectedVariable: null,
      selectedGroup: null,
      isNew: false,
      groupList: [],
      groupVariablesMap: [],
      selectedGroupId: 0,
      loading: true,
      showGroupDeletionWarning: false,
      totalCount: null,
      perPage: 50,
      searchData: null,
      saving: false,
      showVariable: null,
      filteredData: {},
      searchShow: true,
      searchInputHide: false,
    }
  },
  created() {
    this.loadRecords()
  },
  watch: {
    page() {
      this.loadRecords()
    },
  },
  computed: {
    allGroups() {
      let { groupList } = this
      let allGroupList = [{ label: '', options: [{ name: 'All', value: 0 }] }]
      let options = groupList.map(group => {
        let { name, id } = group
        return { name, value: id }
      })

      allGroupList.push({ label: 'Groups', options })
      return allGroupList
    },
    variableList() {
      let { selectedGroupId, groupVariablesMap, loading } = this
      if (!loading) {
        return selectedGroupId === 0
          ? Object.values(groupVariablesMap).reduce(
              (variableList, eachGrpVariable) => {
                variableList = [...variableList, ...eachGrpVariable]
                return variableList
              },
              []
            )
          : groupVariablesMap[selectedGroupId]
      } else {
        return []
      }
    },
  },
  methods: {
    async loadRecords() {
      await this.loadGroupList()
      await this.loadGroupVariableList()
      this.searchListData()
    },
    addNewGroupOrVariable(type) {
      this.selectedVariable = null
      this.selectedGroup = null
      this.isNew = true
      this.showAddForm = true
      this.type = type
    },
    editAllData(groupData, type) {
      this.selectedVariable = groupData.id
      this.showAddForm = false
      this.isNew = false
      this.type = type
    },
    editGroupOrVariable(type, { group, variable }) {
      this.selectedVariable = null
      this.selectedGroup = null
      if (type === 'group') {
        // let group = this.groupList.find(group => group.id === selectedGroupId)
        this.selectedGroup = group
      } else {
        this.selectedVariable = variable
      }

      this.showAddForm = true
      this.isNew = false
      this.type = type
    },
    async deleteGroup(type, { group, variable }) {
      // let { selectedGroupId } = this
      let groupVariableList = group
      if (groupVariableList.length !== 0) {
        this.showGroupDeletionWarning = true
      } else {
        let dialogObj = {
          title: this.$t('common.wo_report.delete_group'),
          htmlMessage: this.$t(
            'common._common.are_you_sure_want_to_delete_group_from_this_app'
          ),
          rbDanger: true,
          rbLabel: this.$t('common.login_expiry.rbLabel'),
        }

        this.$dialog.confirm(dialogObj).then(value => {
          if (value) {
            API.post('v3/globalVariable/deleteGroup', {
              id: selectedGroupId,
            }).then(({ error }) => {
              if (error) {
                this.$message.error(error.message || 'Error Occured')
              } else {
                let index = this.groupList.findIndex(
                  group => group.id === selectedGroupId
                )

                if (!isEmpty(index)) {
                  this.groupList.splice(index, 1)
                  this.selectedGroupId = 0
                }
                this.$message.success(
                  this.$t('common._common.group_deleted_sucess')
                )
              }
            })
          }
        })
      }
    },
    async deleteVariable({ id, groupId }) {
      let value = await this.$dialog.confirm({
        title: this.$t('common.header.delete_variable'),
        htmlMessage: this.$t(
          'common._common.are_you_want_delete_this_variable'
        ),
        rbDanger: true,
        rbLabel: this.$t('common._common.confirm'),
      })

      if (!value) return

      let { error } = await API.post('v3/globalVariable/delete', { id })
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let idx = this.groupVariablesMap[groupId].findIndex(s => s.id === id)

        if (!isEmpty(idx)) {
          this.groupVariablesMap[groupId].splice(idx, 1)
          this.$message.success(
            this.$t('common._common.variable_deleted_successfully')
          )
        }
      }
    },
    async loadGroupList() {
      let url = 'v3/globalVariable/listGroup'
      let { error, data } = await API.get(url)

      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let { list } = data || {}
        this.groupList = list || []
      }
    },
    serializeVariableList(variableList, groupId) {
      let { name: groupName, linkName: groupLinkName } = this.groupList.find(
        group => group.id === groupId
      )

      return variableList.map(variable => {
        let {
          groupId,
          id,
          linkName,
          name,
          type,
          typeEnum,
          valueString,
        } = variable
        let displayValue = valueString

        if (['BOOLEAN'].includes(typeEnum)) {
          displayValue = valueString === 'true' ? 'True' : 'False'
          valueString = valueString === 'true'
        } else if (['DATE', 'DATE_TIME'].includes(typeEnum)) {
          let displayDate = new Date(Number(valueString))
          let formatObj = {
            DATE: 'DD-MMM-YYYY',
            DATE_TIME: 'DD-MMM-YYYY HH:mm',
          }

          valueString = displayDate
          displayValue = moment(displayDate).format(formatObj[typeEnum])
        }

        let displayLinkName = groupLinkName + '.' + linkName

        return {
          groupId,
          id,
          linkName,
          displayLinkName,
          name,
          type,
          typeEnum,
          valueString,
          groupName: groupName,
          displayType: typeEnum.toLowerCase().replace('_', ' '),
          displayValue,
        }
      })
    },
    loadGroupVariableList() {
      this.groupVariablesMap = {}

      let { groupList } = this
      let promises = groupList.map(group => {
        return this.loadGroupVariables(group.id)
      })

      return Promise.all(promises)
        .then(data => {
          this.groupVariablesMap = data.reduce(
            (groupVariablesMap, eachGrpVariableList) => {
              let { groupId, variableList } = eachGrpVariableList || {}
              groupVariablesMap[groupId] = variableList
              return groupVariablesMap
            },
            {}
          )
        })
        .finally(() => {
          this.loading = false
        })
    },
    async loadGroupVariables(groupId) {
      let url = 'v3/globalVariable/list'
      let params = { groupId }
      let { error, data } = await API.get(url, params)

      if (!error) {
        let { list } = data || {}
        return {
          groupId,
          variableList: !isEmpty(list)
            ? this.serializeVariableList(list, groupId)
            : [],
        }
      }
    },
    saveRecord(formType, recordData) {
      if (formType === 'group') {
        let idx = this.groupList.findIndex(grp => grp.id === recordData.id)

        if (!isEmpty(idx)) {
          this.groupList.splice(idx, 1, recordData)
          let variableList = this.$getProperty(
            this.groupVariablesMap,
            `${recordData.id}`,
            []
          )

          this.groupVariablesMap[recordData.id] = this.serializeVariableList(
            variableList,
            recordData.id
          )
        } else {
          this.groupList.push(recordData)
          this.groupVariablesMap[recordData.id] = []
        }
      } else {
        let { groupId, id } = recordData
        let groupVariableList = this.groupVariablesMap[groupId]
        let idx = groupVariableList.findIndex(variable => variable.id === id)

        if (!isEmpty(idx)) {
          groupVariableList.splice(idx, 1, recordData)
        } else {
          groupVariableList.push(recordData)
        }

        this.$set(
          this.groupVariablesMap,
          groupId,
          this.serializeVariableList(groupVariableList, groupId)
        )

        this.selectedGroupId = groupId
      }
    },
    async copyLinkName(copy) {
      await navigator.clipboard.writeText(copy)
      this.$message({
        message: 'Copied - ' + copy,
        type: 'success',
      })
    },
    selectGroup() {
      this.searchListData()
    },
    searchListData() {
      let dataMap = this.filterList()
      this.filteredData = this.filterSelectedGroup(dataMap)
    },
    filterSelectedGroup(value) {
      let { selectedGroupId } = this
      return selectedGroupId > 0
        ? { [selectedGroupId]: value[selectedGroupId] }
        : value
    },
    filterList() {
      let { groupVariablesMap, searchData } = this

      let newgroupVariablesMap = {}
      Object.keys(groupVariablesMap).forEach(key => {
        let variables = groupVariablesMap[key]
        let list = []
        variables.forEach(variable => {
          if (searchData === null || searchData === '') {
            list.push(variable)
          } else if (
            variable.name.toLowerCase().indexOf(searchData.toLowerCase()) > -1
          ) {
            list.push(variable)
          }
        })
        this.$set(newgroupVariablesMap, key, list)
      })
      return newgroupVariablesMap
    },
    closeBulkDialog() {
      this.bulkVariableEdit = false
    },
    onGroupSelect(groupData) {
      this.bulkVariableEdit = true
      this.showVariable = groupData[0]
    },
    searchInputShow() {
      this.searchShow = false
      this.searchInputHide = true
    },
    hideSearchInput() {
      this.searchShow = true
      this.searchInputHide = false
    },
  },
}
</script>
<style lang="scss">
.variables-container {
  height: 100%;
  padding: 20px 30px;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  .header-container {
    display: flex;
    justify-content: space-between;
    margin-bottom: 30px;

    .header-title {
      font-size: 18px;
      color: #000000;
      letter-spacing: 0.7px;
      padding-bottom: 5px;
    }
    .header-sub-title {
      font-size: 11px;
      color: #808080;
      letter-spacing: 0.3px;
    }
  }
  .fill-disabled {
    cursor: not-allowed;
    pointer-events: none;
    fill: #c0c4cc;
    g {
      fill: #c0c4cc;
    }
    path {
      fill: #c0c4cc;
    }
  }
  .prefix-left-align {
    .el-input__prefix {
      left: auto;
      right: 25px;
    }
  }
  .edit-color {
    color: #319aa8;
  }
  .del-color {
    color: #de7272;
  }
}
.fc-variables-search {
  position: absolute;
  z-index: 100px;
  right: 0;
  z-index: 900;
  top: 139px;
}
.fc-variable-search {
  .el-input__inner {
    padding-left: 30px !important;
  }
  .el-icon-close {
    cursor: pointer;
    padding-top: 14px;
    padding-right: 5px;
    color: #324056;
    font-weight: bold;
  }
  .el-icon-search {
    color: rgb(50, 64, 86);
    font-weight: 700;
    font-size: 16px;
    opacity: 0.5;
  }
}
.fc-page-search {
  color: rgb(50, 64, 86);
  font-weight: 700;
  font-size: 16px;
  opacity: 0.5;
}
.type-width {
  width: 15.3%;
}
</style>
