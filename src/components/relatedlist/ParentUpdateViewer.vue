<template>
  <el-dialog
    :visible.sync="canShowWizard"
    width="70%"
    top="5vh"
    class="fc-dialog-center-container f-lookup-wizard scale-up-center"
    :append-to-body="true"
    :show-close="false"
    :before-close="resetDefaultValues"
  >
    <template slot="title">
      <div class="header d-flex">
        <div class="el-dialog__title self-center">{{ title }}</div>
        <el-input
          ref="mainFieldSearchInput"
          v-if="showMainFieldSearch && !hidePaginationSearch"
          class="fc-input-full-border2 width-auto mL-auto"
          suffix-icon="el-icon-search"
          v-model="searchText"
          @blur="hideMainFieldSearch"
        ></el-input>
        <span
          v-else-if="!hidePaginationSearch"
          class="self-center mL-auto"
          @click="openMainFieldSearch"
        >
          <inline-svg
            src="svgs/search"
            class="vertical-middle cursor-pointer"
            iconClass="icon icon-sm mT5 mR5 search-icon"
          ></inline-svg>
        </span>
        <span
          v-if="!hidePaginationSearch && !$validation.isEmpty(totalCount)"
          class="separator self-center"
          >|</span
        >
        <pagination
          v-if="!hidePaginationSearch"
          :currentPage.sync="page"
          :total="totalCount"
          :perPage="perPage"
          class="self-center"
        ></pagination>
        <span
          v-if="!$validation.isEmpty(totalCount) && !hidePaginationSearch"
          class="separator pL0 self-center"
          >|</span
        >
        <div
          class="close-btn self-center cursor-pointer"
          v-bind:class="hidePaginationSearch ? 'mL-auto' : ''"
          @click="canShowWizard = false"
        >
          <i class="el-dialog__close el-icon el-icon-close"></i>
        </div>
      </div>
    </template>
    <div>
      <div
        v-if="isLoading"
        class="loading-container d-flex justify-content-center height550"
      >
        <Spinner :show="isLoading"></Spinner>
      </div>
      <div v-else class="height550">
        <div
          class="fc-list-view fc-list-table-container fc-table-td-height fc-table-viewchooser"
        >
          <div
            v-if="
              isUserLookupModule ||
                viewDetailsExcludedModules.includes(moduleName)
            "
          >
            <component
              :is="(specialListComponentMap[moduleName] || {}).componentName"
              :modulesList="modulesList"
              :moduleDisplayName="moduleDisplayName"
              :moduleName="moduleName"
              :selectedItemId.sync="selectedItemId"
              @setSelectedItem="setSelectedItem"
            ></component>
          </div>
          <div v-else>
            <el-table
              :data="modulesList"
              style="width: 100%"
              @selection-change="selectedItems"
              :fit="true"
              height="250"
            >
              <template slot="empty">
                <img
                  class="mT20"
                  src="~statics/noData-light.png"
                  width="100"
                  height="100"
                />
                <div class="mT10 label-txt-black f14">
                  {{
                    $t('common.products.no_module_available', {
                      moduleName: moduleDisplayName
                        ? moduleDisplayName
                        : moduleName,
                    })
                  }}
                </div>
              </template>
              <el-table-column
                type="selection"
                width="60"
                fixed
              ></el-table-column>
              <el-table-column
                v-if="!$validation.isEmpty(mainFieldColumn)"
                :label="mainFieldColumn.displayName"
                :prop="mainFieldColumn.name"
                fixed
                min-width="200"
              >
                <template v-slot="item">
                  <div class="table-subheading">
                    <div class="d-flex">
                      <div class="self-center name bold">
                        {{
                          getColumnDisplayValue(mainFieldColumn, item.row) ||
                            '---'
                        }}
                      </div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                v-for="(field, index) in filteredViewColumns"
                :key="index"
                :prop="field.name"
                :label="field.displayName"
                :align="
                  field.field.dataTypeEnum === 'DECIMAL' ? 'right' : 'left'
                "
                min-width="200"
              >
                <template v-slot="scope">
                  <keep-alive v-if="isSpecialHandlingField(field)">
                    <component
                      :is="(listComponentsMap[moduleName] || {}).componentName"
                      :field="field"
                      :moduleData="scope.row"
                    ></component>
                  </keep-alive>
                  <div
                    v-else
                    class="table-subheading"
                    :class="{
                      'text-right': field.field.dataTypeEnum === 'DECIMAL',
                    }"
                  >
                    {{ getColumnDisplayValue(field, scope.row) || '---' }}
                  </div>
                </template>
              </el-table-column>
            </el-table>
            <div
              v-if="!$validation.isEmpty(selectedItemName)"
              class="pT20 pL20 pB10 fc-black-color letter-spacing0_5 f13 bold"
            >
              {{ selectedLabel }} {{ selectedItemName }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="!isLoading" class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="canShowWizard = false">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button type="primary" class="modal-btn-save" @click="updateWos()">{{
        $t('common._common.update')
      }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
import RelatedListWidget from '@/page/widget/common/RelatedListWidget'
import Constants from 'util/constant'
import WorkOrderSpecialFieldsList from '@/list/WorkOrderSpecialFieldsList'
import { isEmpty } from '@facilio/utils/validation'
import Spinner from '@/Spinner'
import UserSpecialFields from '@/list/UserSpecialFields'
import BuildingSpecialFields from '@/list/BuildingSpecialFields'
import SiteSpecialFields from '@/list/SiteSpecialFields'
import SpaceSpecialFields from '@/list/SpaceSpecialFields'

export default {
  name: 'ParentUpdateViewer',
  components: { Spinner },
  extends: RelatedListWidget,
  props: ['canShowWoWizard', 'siteId', 'parentWOId'],
  computed: {
    canShowWizard: {
      get() {
        return this.canShowWoWizard
      },
      set(value) {
        this.$emit('update:canShowWoWizard', value)
      },
    },
    moduleDisplayName() {
      return 'Work Order'
    },
    moduleName() {
      return 'workorder'
    },
    filters() {
      let { mainField, searchText } = this
      let filterObj = {}
      // search filter
      if (
        !isEmpty(mainField) &&
        !isEmpty(searchText) &&
        searchText.length > 0
      ) {
        let { name, field } = mainField
        let { dataTypeEnum } = field
        let value = [searchText]
        let operatorId = Constants.FILTER_OPERATORID_HASH[dataTypeEnum]
        filterObj[name] = {
          operatorId,
          value,
        }
      }

      filterObj.parentWO = {
        operatorId: 37,
        value: [`${this.parentWOId}`],
      }

      return filterObj
    },
    title() {
      let { moduleDisplayName } = this
      return `${this.$t('common.text.choose')} ${moduleDisplayName}`
    },
    selectedLabel() {
      let { moduleDisplayName } = this
      return `Selected ${moduleDisplayName} :`
    },
    selectedItem() {
      let { selectedItemId, modulesList } = this
      let selectedItem = null
      if (!isEmpty(selectedItemId)) {
        selectedItem = modulesList.find(module => module.id === selectedItemId)
      }
      return selectedItem
    },
    selectedItemName() {
      let { selectedItem, mainField } = this
      if (!isEmpty(mainField)) {
        mainField = {
          name: 'name',
        }
      }
      let { name } = mainField
      return selectedItem ? selectedItem[name] : null
    },
  },
  data() {
    return {
      selectedItemId: null,
      selectedWos: [],
      defaultHideColumns: ['photo', 'moduleState', 'stateFlowId'],
      specialListComponentMap: {
        users: {
          componentName: UserSpecialFields,
        },
        building: {
          componentName: BuildingSpecialFields,
        },
        site: {
          componentName: SiteSpecialFields,
        },
        space: {
          componentName: SpaceSpecialFields,
        },
      },
      listComponentsMap: {
        workorder: {
          componentName: WorkOrderSpecialFieldsList,
          specialHandlingFields: [
            'resource',
            'assignedTo',
            'noOfNotes',
            'noOfTasks',
            'noOfAttachments',
          ],
        },
      },
    }
  },
  created() {
    this.init()
  },
  methods: {
    selectedItems(selectedItem) {
      this.selectedWos = selectedItem
    },
    updateWos() {
      if (this.selectedWos.length) {
        let ids = []
        this.selectedWos.filter(wo => {
          ids.push(wo.id)
        })
        let updateObj = {
          id: ids,
          fields: {
            parentWO: {
              id: parseInt(this.parentWOId) || -1,
            },
          },
        }
        this.$store
          .dispatch('workorder/updateWorkOrder', updateObj)
          .then(response => {
            this.$nextTick(() => {
              this.$emit('updatedItems', this.selectedWos)
              this.canShowWizard = false
            })
            this.$message.success(
              this.$t('common._common.updated_successfully')
            )
          })
          .catch(() => {
            this.$message.success('Failed')
          })
      } else {
        return
      }
    },
    setSelectedItem() {
      let { selectedItem, mainField } = this
      if (isEmpty(mainField)) {
        mainField = {
          name: 'name',
        }
      }
      // this.$emit('setLookupFieldValue', selectedItem, mainField)
      this.canShowWizard = false
    },
    resetDefaultValues() {
      this.$set(this, 'showMainFieldSearch', false)
    },
  },
}
</script>

<style lang="scss">
.fc-dialog-center-container {
  &.f-lookup-wizard {
    overflow: hidden;
    .header {
      min-height: 35px;
    }
    .el-dialog__header {
      padding: 15px 20px;
    }
    .el-dialog__body {
      padding: 0;
      .fc-table-td-height,
      .fc-list-table-container {
        .el-table {
          tr {
            td:first-child + td,
            th:first-child + th {
              padding-left: 0px !important;
            }
          }
        }
      }
    }
    .fc-input-full-border2 {
      .el-input__inner {
        height: 35px !important;
      }
    }
    .separator {
      font-size: 18px;
    }
    .cell {
      .name {
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
      }
    }
    .search-icon {
      fill: #91969d;
    }
  }
  .fc-list-table-container {
    .el-table {
      height: 500px !important;
    }
  }
  .task-icon {
    fill: #dddddd;
  }
}
</style>
