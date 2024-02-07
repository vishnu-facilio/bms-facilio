<template>
  <div class="new-group-container">
    <div class="f20 mL40 mT40">
      {{
        isGroupEdit
          ? $t('common.header.edit_group')
          : $t('common.header.new_group')
      }}
    </div>
    <div class="new-group-sub-container">
      <div v-if="loading" class="flex-middle fc-empty-white">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div v-else class="p30">
        <el-form
          :model="groupObj"
          :rules="rules"
          ref="form"
          label-position="left"
          label-width="150px"
        >
          <el-form-item :label="$t('common.header.group_name')" prop="name">
            <el-input
              class="fc-input-full-border2 width65"
              v-model="groupObj.name"
              :placeholder="$t('common._common.enter_a_group_name')"
            ></el-input>
          </el-form-item>
          <el-form-item
            :label="$t('common._common.location')"
            prop="location"
            class="flookup-field-groups"
          >
            <FLookupField
              class="width65"
              :model.sync="(groupObj.location || {}).value"
              :fetchOptionsOnLoad="true"
              :canShowLookupWizard="showLookupFieldWizard"
              :field="fieldObj"
              :hideDropDown="true"
              @showLookupWizard="showLookupWizard"
              @setLookupFieldValue="setLookupFieldValue"
            ></FLookupField>
          </el-form-item>
          <el-form-item :label="$t('common.header.schedule')" prop="schedule">
            <el-select
              class="fc-input-full-border-select2 width65"
              v-model="groupObj.schedule"
              clearable
              :placeholder="$t('common.products.select_a_schedule_event')"
            >
              <template v-if="scheduleList">
                <el-option
                  v-for="schedule in scheduleList"
                  :key="schedule.id"
                  :label="schedule.name"
                  :value="schedule.id"
                ></el-option>
              </template>
            </el-select>
          </el-form-item>
        </el-form>
        <div class="section-container">
          <template v-for="(group, index) in groups">
            <div
              :key="index"
              class="rule-border-blue mB15 position-relative"
              style="border-left: 1px solid rgb(228, 235, 241);"
            >
              <div class="action-group-container">
                <div class="delete-group pointer" @click="removeGroup(index)">
                  <inline-svg
                    v-if="groups.length > 1"
                    :key="`delete-${index}`"
                    src="svgs/delete"
                    class="f-delete vertical-middle"
                    iconClass="icon icon-sm icon-remove"
                    :title="$t('common.wo_report.delete_group')"
                    v-tippy="{ placement: 'top', arrow: true }"
                  ></inline-svg>
                </div>
                <div
                  class="arrow-group pointer"
                  @click="group.isCollapsed = !group.isCollapsed"
                >
                  <i
                    :class="{
                      'el-icon-arrow-up fR f16 mT5': true,
                      rotate180: !group.isCollapsed,
                    }"
                  ></i>
                </div>
              </div>
              <div class="p20 pT30">
                <el-form
                  :model="group"
                  label-width="220px"
                  label-position="left"
                >
                  <el-row class="mB10">
                    <el-col :span="20">
                      <el-form-item
                        prop="typeName"
                        :label="$t('common.wo_report.section_name')"
                        class="mB10"
                      >
                        <el-input
                          v-model="group.name"
                          class="fc-input-full-border2 width50"
                          :placeholder="$t('common._common.enter_a_name')"
                        ></el-input>
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-collapse-transition>
                    <div v-show="group.isCollapsed">
                      <el-row class="mB10">
                        <el-col :span="20">
                          <el-form-item
                            prop="type"
                            :label="$t('common.header.group_type')"
                            class="mB10"
                          >
                            <el-select
                              v-model="group.type"
                              class="fc-input-full-border2 width50"
                              :placeholder="$t('common.products.select_a_type')"
                            >
                              <el-option
                                v-for="option in groupTypeOptions"
                                :key="option.value"
                                :label="option.label"
                                :value="option"
                              ></el-option>
                            </el-select>
                          </el-form-item>
                        </el-col>
                      </el-row>
                      <template v-if="group.category">
                        <div
                          v-for="(category, index) in group.category"
                          :key="index"
                          class="pT20 pB20"
                        >
                          <CategoryTable
                            @editTable="editCategory"
                            :category="category"
                            :group="group"
                            :isEdit="isEdit(category)"
                          />
                        </div>
                      </template>
                      <el-row class="mB10">
                        <el-col :span="10">
                          <el-button
                            class="task-add-btn mT5"
                            @click="openCategoryDialog(group)"
                            :disabled="$validation.isEmpty(group.type)"
                          >
                            <img src="~assets/add-blue.svg" />
                            <span class="btn-label mL5">
                              {{ $t('common.header.new_category') }}
                            </span>
                          </el-button>
                        </el-col>
                      </el-row>
                    </div>
                  </el-collapse-transition>
                </el-form>
              </div>
            </div>
          </template>
        </div>
        <div class="flex flex-row justify-center">
          <el-button class="task-add-btn mT5" @click="addNewGroup">
            <img src="~assets/add-blue.svg" />
            <span class="btn-label mL5">
              {{ $t('common.header.add_new_section') }}
            </span>
          </el-button>
        </div>
      </div>

      <div class="flex-grow flex-shrink white-background"></div>
      <div class="schedule-btn-footer">
        <el-button
          class="modal-btn-cancel text-uppercase"
          @click="redirectToList()"
          >{{ $t('common._common.cancel') }}</el-button
        >
        <el-button class="modal-btn-save mL0" @click="save" :loading="saving">
          {{
            saving ? $t('common._common._saving') : $t('common._common._save')
          }}
        </el-button>
      </div>
    </div>
    <NewCategory
      v-if="showAddCategoryDialog"
      :openDialog="showAddCategoryDialog"
      :closeDialog="closeAddAssetDialog"
      :selectedGroup="selectedGroup"
      :selectedCategory="selectedCategory"
      @onAddNewCategory="onAddNewCategory"
      :isEdit="isCategoryEdit"
    />
    <FLookupFieldWizard
      v-if="showLookupFieldWizard"
      :canShowLookupWizard.sync="showLookupFieldWizard"
      :selectedLookupField="fieldObj"
      :withReadings="true"
      @setLookupFieldValue="setLookupFieldValue"
    ></FLookupFieldWizard>
  </div>
</template>

<script>
import FLookupFieldWizard from '@/FLookupFieldWizard'
import FLookupField from '@/forms/FLookupField'
import cloneDeep from 'lodash/cloneDeep'
import { isEmpty } from '@facilio/utils/validation'
import NewCategory from './components/NewCategory'
import CategoryTable from './components/CategoryTable'
import { API } from '@facilio/api'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  components: { FLookupFieldWizard, NewCategory, CategoryTable, FLookupField },
  data() {
    return {
      groups: [],
      groupObj: {
        name: '',
        location: null,
        schedule: null,
      },
      fieldObj: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'basespace',
        field: {
          lookupModule: {
            name: 'basespace',
            displayName: 'Base space',
          },
        },
        forceFetchAlways: true,
        filters: {},
        isDisabled: false,
      },
      showLookupFieldWizard: false,
      scheduleList: null,
      showAddCategoryDialog: false,
      selectedGroup: null,
      selectedCategory: null,
      groupTypeOptions: [
        { label: 'HVAC', value: 1 },
        { label: 'Lightning', value: 2 },
        { label: 'Elevator', value: 3 },
      ],
      isCategoryEdit: false,
      saving: false,
      loading: false,
      rules: {
        name: [
          {
            required: true,
            message: this.$t('common._common.please_enter_group_name'),
            trigger: 'blur',
          },
        ],
        location: [
          {
            required: true,
            message: this.$t('common._common.please_select_location'),
            trigger: 'change',
          },
        ],
        schedule: [
          {
            required: true,
            message: this.$t('common._common.please_select_schedule'),
            trigger: 'change',
          },
        ],
      },
    }
  },
  created() {
    if (isEmpty(this.groups))
      this.groups.push({
        type: null,
        name: '',
        category: [],
        isCollapsed: true,
      })
    this.init()
    if (this.recordId) this.deserialize(this.recordId)
  },
  computed: {
    groupObject() {
      return {
        type: null,
        name: '',
        category: [],
        isCollapsed: false,
      }
    },
    recordId() {
      let { id } = this.$route.params
      return id
    },
    isGroupEdit() {
      let { id } = this.$route.params
      return !isEmpty(id)
    },
  },
  methods: {
    async deserialize(id) {
      this.loading = true
      let { controlGroupv2, error } = await API.fetchRecord('controlGroupv2', {
        id,
      })
      if (error) this.$message.error(error.message || 'Error Occured')
      else {
        let record = controlGroupv2
        let { sections, space } = controlGroupv2

        if (!isEmpty(space)) {
          let { id: spaceId } = space

          this.groupObj.location = { value: spaceId }
        }

        let {
          name,
          controlSchedule: { id: scheduleId },
        } = record
        this.groupObj.name = name
        this.groupObj.schedule = scheduleId
        if (!isEmpty(sections)) {
          this.groups = sections.map(section => {
            let { name: sectionName, type, categories } = section

            let currCategories = categories.map(category => {
              let { assetCategory, controlAssets } = category
              let { controlFields } = controlAssets[0]
              return {
                assets: controlAssets.map(currAsset => currAsset.asset.id),
                assetList: controlAssets.map(currAsset => {
                  let { asset } = currAsset
                  let { id, name } = asset || {}
                  return { label: name, id }
                }),
                type: assetCategory,
                controlPoints: controlFields,
                isEditCategory: true,
              }
            })
            return {
              type: this.groupTypeOptions.find(
                grpType => grpType.value === type
              ),
              name: sectionName,
              category: currCategories,
              isCollapsed: false,
            }
          })
        }
      }

      this.loading = false
    },
    async init() {
      let { list, error } = await API.fetchAll('controlSchedule')
      if (!isEmpty(error)) this.$message.error(error.message || 'Error Occured')
      this.scheduleList = list
    },
    isEdit(category) {
      let { isEditCategory } = category
      return isEditCategory
    },
    setLookupFieldValue(value) {
      let { field } = value
      let { selectedItems } = field
      this.groupObj.location = selectedItems[0]
      this.showLookupFieldWizard = false
    },
    addNewGroup() {
      let { groups } = this
      groups = groups || []
      groups.push(cloneDeep(this.groupObject))
    },

    removeGroup(index) {
      let { groups } = this
      groups.splice(index, 1)
    },
    closeAddAssetDialog() {
      this.showAddCategoryDialog = false
    },
    openCategoryDialog(group) {
      this.isCategoryEdit = false
      this.selectedGroup = group
      this.showAddCategoryDialog = true
    },
    onAddNewCategory(newCategory) {
      this.showAddCategoryDialog = false
      let { category, group, isEdit } = newCategory
      let selectedGroup = this.groups.find(
        currGroup =>
          currGroup.type === group.type && currGroup.name === group.name
      )
      let currGroupCategory = this.$getProperty(selectedGroup, 'category')
      if (!isEmpty(currGroupCategory)) {
        let categoryIndex = currGroupCategory.findIndex(currCategory => {
          if (currCategory.type === category.type) {
            return true
          } else {
            return false
          }
        })
        if (isEdit) {
          currGroupCategory.splice(categoryIndex, 1, category)
        } else {
          currGroupCategory.push(cloneDeep(category))
        }
      } else {
        currGroupCategory.push(cloneDeep(category))
      }
    },
    editCategory({ group, category }) {
      this.selectedGroup = group
      this.selectedCategory = category
      this.isCategoryEdit = true
      this.showAddCategoryDialog = true
    },

    showLookupWizard(field, canShow) {
      this.selectedLookupField = field
      this.showLookupFieldWizard = canShow
    },
    async save() {
      let data = this.serialize()

      this.$refs['form'].validate(async valid => {
        let isSectionEmpty = false
        this.groups.forEach(group => {
          let { name, type, category } = group
          if (isEmpty(name) || isEmpty(type) || isEmpty(category)) {
            isSectionEmpty = true
          }
        })
        if (valid && !isSectionEmpty) {
          this.saving = true
          let { id } = this.$route.params
          let currId
          if (!isEmpty(id)) {
            let { error } = await API.updateRecord('controlGroupv2', {
              id,
              data,
            })
            if (!isEmpty(error)) {
              this.$message.error(error.message || 'Error Occured')
            } else {
              currId = id

              this.$message.success(
                this.$t('common._common.group_edited_successfully')
              )
            }
          } else {
            let { controlGroupv2, error } = await API.createRecord(
              'controlGroupv2',
              {
                data,
              }
            )
            if (!isEmpty(error)) {
              this.$message.error(error.message || 'Error Occured')
            } else {
              currId = this.$getProperty(controlGroupv2, 'id')
              this.$message.success(
                this.$t('common._common.group_created_successfully')
              )
            }
          }
          this.saving = false
          if (!isEmpty(currId)) {
            if (isWebTabsEnabled()) {
              let { name } =
                findRouteForModule('controlGroupv2', pageTypes.OVERVIEW) || {}
              let route = {
                name,
                params: {
                  viewname: 'all',
                  id: currId,
                },
              }
              this.$router.push(route)
            } else {
              this.$router.push({
                name: 'group-summary',
                params: {
                  id: currId,
                  moduleName: this.moduleName,
                  viewname: 'all',
                },
                query: {
                  ...this.$route.query,
                },
              })
            }
          }
        } else if (isSectionEmpty) {
          this.$message.error(
            this.$t('common._common.sections_name_type_cannot_empty')
          )
        }
      })
    },
    serialize() {
      let id = this.$getProperty(this, 'groupObj.location.value')
      let data = {
        name: this.groupObj.name,
        controlSchedule: {
          id: this.groupObj.schedule,
        },
        space: { id },
        sections: [],
      }
      this.groups.forEach(group => {
        let { name, type, category } = group
        let currGroupType = this.$getProperty(type, 'value')
        let sectionParams = {
          name: name,
          type: currGroupType,
          categories: [],
        }
        category.forEach(currCategory => {
          let { type, assetList, controlPoints } = currCategory
          let { id } = type
          let categoryParam = {
            name: name,
            assetCategory: {
              id: id,
            },
            controlAssets: assetList.map(asset => {
              let formattedControlpoints = controlPoints.map(currPoint => {
                let fieldId = this.$getProperty(currPoint, 'point.fieldId')
                if (isEmpty(fieldId))
                  fieldId = this.$getProperty(currPoint, 'fieldId')
                return {
                  type: 1,
                  fieldId: fieldId,
                  trueVal: currPoint.trueVal,
                  falseVal: currPoint.falseVal,
                }
              })
              return {
                asset: {
                  id: asset.value || asset.asset.id,
                },
                controlFields: formattedControlpoints,
              }
            }),
          }
          sectionParams.categories.push(categoryParam)
        })
        data.sections.push(sectionParams)
      })
      return data
    },
    redirectToList() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('controlGroupv2', pageTypes.LIST) || {}
        let route = {
          name,
          params: {
            viewname: 'all',
          },
        }
        this.$router.push(route)
      } else {
        this.$router.push({ name: 'group-list', params: { viewname: 'all' } })
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.new-group-container {
  .new-group-sub-container {
    height: calc(100vh - 150px);
    overflow: scroll;
    width: 875px;
    background-color: #ffffff;
    margin: 20px 0px 30px 40px;
    display: flex;
    flex-direction: column;
  }

  .schedule-btn-footer {
    bottom: 0px;
    display: flex;
  }
  .task-add-btn {
    padding: 10px 20px;
    border: 1px solid #39b2c2;
    background-color: #f7feff;
    min-height: 36px;
    &:hover {
      border: 1px solid #39b2c2;
      background-color: #f7feff;
    }
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
  .action-group-container {
    display: flex;
    flex-direction: row;
    position: absolute;
    right: 10px;
    top: 10px;
  }
  .delete-group {
    z-index: 1;
    color: #ff0000;
    padding-right: 23px;
    margin-top: 5px;
  }
  .arrow-group {
    z-index: 1;
    margin-bottom: 3px;
  }
}
</style>
<style lang="scss">
.new-group-container {
  .el-radio__input.is-checked .el-radio__inner {
    background-color: #39b2c2;
    border-color: #39b2c2;
  }
  .flookup-field-groups {
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
}
</style>
