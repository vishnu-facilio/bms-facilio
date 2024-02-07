<template>
  <div class="view-creation-form-customize-column-container">
    <div class="cc-fc-sub-title-desc">
      {{ $t('viewsmanager.customize.drag_drop_text') }}
    </div>
    <div class="wo-rearrage-container">
      <div class="cc-rearrange-box mR40">
        <div class="container-header">
          <div class="available-column-header d-flex pL15">
            {{ $t('viewsmanager.customize.available_columns') }}
          </div>
          <div class="d-flex user-selecter">
            <el-input
              class="avail-select width100 border-right2"
              prefix-icon="el-icon-search"
              placeholder="Search Column"
              v-model="searchText"
            ></el-input>
          </div>
        </div>
        <div v-if="isLoading" class="pT20">
          <div
            class="lines loading-shimmer"
            v-for="i in 6"
            :key="`available_list-${i}`"
          ></div>
        </div>
        <div v-else class="rearrange-box-body">
          <div
            class="avail-block d-flex"
            v-for="(element, index) in filteredColumns"
            :key="`filtered-fields-${index}`"
          >
            <span class="rearrange-txt block-word-break">{{
              element.label
            }}</span>
            <div
              class="rearrange-plus-icon pointer"
              v-on:click="addcolumn(element)"
            >
              <i class="el-icon-plus action-icon "></i>
            </div>
          </div>
        </div>
      </div>
      <div class="cc-rearrange-box vc-container-right">
        <p class="rearrange-box-sticky-container right-column-container ">
          <span class="vc-rearrange-box-H">{{
            $t('viewsmanager.customize.selected_columns')
          }}</span>
        </p>
        <div class="rearrange-box-body">
          <div v-if="isLoading" class="pT20">
            <div
              class="lines loading-shimmer"
              v-for="i in 3"
              :key="`selected_list-${i}`"
            ></div>
          </div>
          <div v-else>
            <div
              class="avail-block"
              style="border: solid 1px #f0f0f0"
              v-for="(col, index) in fixedSelectedCol"
              :key="index + 'fixed-fields'"
            >
              <span
                class="rearrange-txt2 block-word-break"
                style="opacity: 0.5"
                >{{ col.label }}</span
              >
            </div>
            <draggable
              v-model="selectedColumns"
              class="dragArea"
              :group="'people'"
              handle=".rearrange-icon"
            >
              <div
                class="avail-block right-column-container d-flex"
                v-for="(element, index) in selectedColumns"
                :key="index + 'selected-fields'"
              >
                <div class="d-flex">
                  <el-tooltip
                    effect="dark"
                    :content="$t('viewsmanager.sharing_permission.reorder')"
                    placement="top"
                  >
                    <inline-svg
                      src="svgs/ic-drag-handle"
                      iconClass="text-center icon icon-sm-md"
                      class="vertical-middle rearrange-icon cursor-move"
                    ></inline-svg>
                  </el-tooltip>
                  <span class="rearrange-txt2 block-word-break"
                    >{{ element.label }}
                  </span>
                </div>
                <i
                  class="el-icon-close action-icon  "
                  v-on:click="removeColumn(element, index)"
                ></i>
              </div>
            </draggable>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import ViewMixinHelper from '@/mixins/ViewMixin'
import cloneDeep from 'lodash/cloneDeep'
import { API } from '@facilio/api'
import draggable from 'vuedraggable'

export default {
  props: ['viewDetail', 'isNewView', 'moduleName', 'saveAsNew'],
  mixins: [ViewMixinHelper],
  components: { draggable },
  data() {
    return {
      isLoading: true,
      searchText: '',
      metaInfoObj: {},
      availableColumns: [],
      selectedColumns: [],
      config: {
        availableColumns: [],
      },
      allColumns: [],
    }
  },
  created() {
    this.fetchModuleMetaInfo(this.moduleName)
  },
  watch: {
    viewColumns: {
      handler() {
        this.availableColumns = []
        this.selectedColumns = []
      },
    },
    allFields: {
      handler(val) {
        this.allColumns = cloneDeep(val)
        this.checkColumns()
      },
      deep: true,
    },
  },
  mounted() {
    this.allColumns = cloneDeep(this.allFields)
  },
  computed: {
    columnDisplayNameMap() {
      let { viewDetail } = this
      let { fields } = viewDetail || {}
      let map = {}
      if (!isEmpty(fields)) {
        fields.forEach(field => {
          map[field.name] = field.columnDisplayName || field.displayName
        })
      }
      return map
    },
    fixedCols() {
      let { metaInfoObj } = this
      let mainField = null
      let fixedColumns = []
      if (!isEmpty(metaInfoObj)) {
        let { fields } = metaInfoObj
        mainField = (fields || []).find(field => {
          return field.mainField
        })
        fixedColumns.push(mainField.name)
      } else {
        fixedColumns = ['name']
      }
      return fixedColumns
    },
    allFields() {
      let fieldsList = []
      if (this.metaInfoObj && this.metaInfoObj.fields) {
        fieldsList = this.metaInfoObj.fields
          .filter(field => {
            return (
              (this.config.availableColumns ||
                this.config.availableColumns.indexOf(field.name) !== -1 ||
                !field.default) &&
              field.parentFieldId !== -1 &&
              field.dataType !== 17 &&
              (!this.fixedCols || !this.fixedCols.includes(field.name))
            )
          })
          .filter(f => f.id && f.name)
          .map(field => {
            let columnDisplayNameLable =
              this.columnDisplayNameMap[field.name] || null
            return {
              id: field.id,
              label:
                columnDisplayNameLable ||
                field.columnDisplayName ||
                field.displayName,
              key: field.name || '',
            }
          })
      }
      return fieldsList
    },

    fixedSelectedCol() {
      return this.fixedCols && this.metaInfoObj && this.metaInfoObj.fields
        ? this.metaInfoObj.fields
            .filter(field => this.fixedCols.includes(field.name))
            .map(field => ({
              id: field.id,
              label: field.columnDisplayName || field.displayName,
            }))
        : []
    },
    filteredColumns() {
      let { availableColumns } = this
      if (this.searchText) {
        return availableColumns.filter(search =>
          search.label.toLowerCase().includes(this.searchText.toLowerCase())
        )
      }
      return availableColumns
    },
  },
  methods: {
    fetchModuleMetaInfo(moduleName) {
      if (!isEmpty(moduleName)) {
        API.get('/module/metafields?moduleName=' + moduleName).then(
          ({ data, error }) => {
            if (error) {
              this.$message.error(error.message || 'Error Occured')
            } else {
              this.metaInfoObj = this.$getProperty(data, 'meta', {}) || {}
            }
          }
        )
      }
    },
    checkColumns() {
      this.isLoading = true
      let { fields } = this.viewDetail || {}
      let nonSelectableColumns = [
        'assignmentGroup',
        'asset',
        'week',
        'hour',
        'month',
        'day',
        'localId',
        'photoId',
        'space',
        'hideToCustomer',
        'parentAssetId',
        'stateFlowId',
        'id',
      ]
      this.selectedColumns = []

      for (let column in this.allColumns) {
        let isColumnSelected = false
        for (let viewColumn in this.viewColumns) {
          if (
            this.allColumns[column].label ===
              this.viewColumns[viewColumn].displayName &&
            (!this.isNewView || this.saveAsNew)
          ) {
            isColumnSelected = true
          }
        }
        if (
          !isColumnSelected &&
          !nonSelectableColumns.includes(this.allColumns[column].key)
        ) {
          this.availableColumns.push(this.allColumns[column])
        }
      }
      if (!isEmpty(fields)) {
        fields.forEach(fld => {
          let { fieldId, name, displayName, columnDisplayName, field } =
            fld || {}
          let { mainField, name: fieldName } = field || {}
          if (!mainField) {
            this.selectedColumns.push({
              id: fieldId,
              key: name || fieldName,
              label: displayName || columnDisplayName,
            })
          }
        })
      }
      this.isLoading = false
    },
    addcolumn(element) {
      this.selectedColumns.push(element)

      let index = this.availableColumns.findIndex(c => c.id === element.id)
      if (index !== -1) {
        this.availableColumns.splice(index, 1)
      }
    },
    removeColumn(element, index) {
      this.availableColumns.push(element)
      this.selectedColumns.splice(index, 1)
    },
    async validate() {
      return new Promise(resolve => {
        if (isEmpty(this.selectedColumns)) {
          this.$message({
            message: 'Please select atleast one customize column',
            type: 'error',
          })
          resolve(false)
        }
        resolve(true)
      })
    },
    serializeData() {
      let columns = [...this.fixedSelectedCol, ...this.selectedColumns]
      let fields = columns.map(col => ({
        fieldId: col.id,
        columnDisplayName: col.label,
        fieldName: col.key || '',
      }))
      return { fields }
    },
  },
}
</script>
<style lang="scss">
.view-creation-form-customize-column-container {
  .cc-fc-sub-title-desc {
    font-size: 12px;
    letter-spacing: 0.6px;
    color: #999999;
  }
  .lines {
    margin: 5px 10px;
    width: 93%;
    height: 24px;
    border-radius: 5px;
  }
  .cc-rearrange-box {
    width: 330px;
    height: 342px;
    border: solid 1px #c5e7eb;
    overflow: hidden;
    background: #fff;
    display: inline-block;
    vertical-align: middle;
    box-sizing: border-box;
    position: relative;
    height: calc(55vh - 20px);
    overflow: scroll;

    .searchText .el-input__inner {
      padding-left: 30px !important;
    }
    .avail-select .el-input__inner,
    .el-textarea__inner,
    .el-input {
      line-height: 40px !important;
      padding-left: 15px !important;
      padding-right: 15px !important;
      background-color: #ffffff;
      border: none !important;
      font-size: 14px;
      font-weight: normal;
      letter-spacing: 0.4px;
      color: #324056;
      text-overflow: ellipsis;
      font-weight: 400;
      padding-right: 30px;
      white-space: nowrap;
    }
    .user-selecter {
      background: #ffff;
      border-bottom: 1px solid #d0d9e2;
    }
    .available-column-header {
      height: 40px;
      width: 100%;
      background-color: #f5f9fa;
      color: #000;
      font-size: 14px;
      font-weight: 500;
      letter-spacing: 0.5px;
      align-items: center;
    }
    .container-header {
      position: sticky;
      top: 0;
      z-index: 1;
    }
    .avail-block {
      justify-content: space-between;
      min-height: 40px;
      border-radius: 2px;
      background-color: #ffffff;
      border: solid 1px #e3e1e1;
      padding: 10px 10px 6px;
      margin-bottom: 9px;
    }
    .block-word-break {
      word-break: break-word;
    }
    .action-icon {
      margin-top: 1px;
      float: right;
      font-size: 16px;
      color: #aeaeae;
      font-weight: 700;
      cursor: pointer;
    }
    .rearrange-box-sticky-container {
      position: sticky;
      top: 0;
      z-index: 1;
      height: 40px;
      line-height: 40px;
      background-color: #f5f9fa;
      margin: 0;
      box-sizing: border-box;
      color: #000;

      &.right-column-container {
        padding-left: 15px;
      }
    }

    .vc-rearrange-box-H {
      float: left;
      text-align: left;
      font-size: 14px;
      font-weight: 500;
      letter-spacing: 0.5px;
    }
    .rearrange-icon {
      margin-right: 4px;
      margin-top: 1px;
    }
  }
  .vc-container-right {
    border: 1.5px dashed #c7d0d9;
  }
}
</style>
