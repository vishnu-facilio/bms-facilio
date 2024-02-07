<template>
  <el-dialog
    :visible.sync="showDialog"
    :fullscreen="true"
    custom-class="cc-dialog"
    :title="$t('common._common.column_settings')"
    :append-to-body="true"
  >
    <div class="scrollable" style="padding: 20px 0; max-height: 85vh">
      <div class="fixed-column" v-if="fixedFields && fixedFields.length">
        <el-checkbox-group v-model="selectedFixedColumns">
          <el-checkbox
            style="border: none"
            class="column-item"
            v-for="field in fixedFields"
            :disabled="field.disabled"
            :key="field.fieldId"
            :label="field.fieldId"
            >{{ getDisplayName(field) }}</el-checkbox
          >
        </el-checkbox-group>
      </div>
      <el-divider class="mT0 mB20"></el-divider>
      <el-checkbox-group
        v-model="selectedColumns"
        class="fc-column-checkbox pL20 pR20 pT0 pB0"
      >
        <draggable
          :options="dragOptions"
          :list="sortedFields"
          data-parent="default"
        >
          <el-checkbox
            class="column-item"
            v-for="field in sortedFields"
            :key="field.name"
            :label="field.name"
            :value="field.name"
            :class="{ checkboxSelected: field.edit === true }"
          >
            <el-row>
              <el-col :span="1">
                <inline-svg
                  src="drag-drop"
                  class="vertical-middle cursor-drag icon-right column-drag-icon"
                  iconClass="icon icon-sm-md"
                  style="cursor: -webkit-grabbing"
                ></inline-svg>
              </el-col>
              <el-col :span="22">
                <el-input
                  ref="columnRenameInp"
                  placeholder="Change your column name"
                  class="fc-input-border-remove width90 column-name-change-input"
                  :autofocus="true"
                  v-if="field.edit"
                  @blur="
                    field.edit = false
                    $emit('update')
                  "
                  @keyup.enter="
                    field.edit = false
                    $emit('update')
                  "
                  v-model="field.columnDisplayName"
                ></el-input>
                <div class="" v-else>
                  {{ field.columnDisplayName }}
                  <span
                    class="fc-grey-text12 pL5"
                    v-if="
                      field.field &&
                        field.field.displayName !== field.columnDisplayName
                    "
                    >{{ field.field.displayName }}</span
                  >
                </div>
              </el-col>
              <el-col :span="1" class="visibility-hide-actions text-right">
                <i
                  class="el-icon-edit pR10 fc-grey3-text14 pL25 column-edit-icon"
                  @click.stop.prevent="showRenameColumn(field)"
                ></i>
              </el-col>
            </el-row>
          </el-checkbox>
        </draggable>
        <draggable
          :options="dragOptions"
          :list="selectedlookupColumns"
          :data-parent="selectedLookup"
        >
          <el-checkbox
            class="column-item"
            v-for="field in selectedlookupColumns"
            :key="field.fieldId"
            :label="field.fieldId"
            @change="onLookupSelected(field)"
            :data-field-id="field.fieldId"
          >
            {{ field.columnDisplayName }}
          </el-checkbox>
        </draggable>
      </el-checkbox-group>
    </div>
    <div slot="footer" class="modal-dialog-footer">
      <el-button @click="showDialog = false" class="modal-btn-cancel">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button
        type="primary"
        class="modal-btn-save"
        @click="save"
        :loading="isSaving"
        :disabled="isSaving"
        >{{
          isSaving ? $t('common._common._saving') : $t('common._common._save')
        }}</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import { mapState } from 'vuex'
import draggable from 'vuedraggable'
import { isEmpty } from '@facilio/utils/validation'
import $ from 'jquery'
import { getColumnConfig } from 'src/newapp/components/column-customization/columnConfigUtil'

export default {
  props: [
    'visible',
    'moduleName',
    'viewName',
    'relatedViewDetail',
    'relatedMetaInfo',
    'refreshRelatedList',
    'excludeMainField',
  ],
  data() {
    return {
      selectedColumns: [],
      selectedFixedColumns: [],
      showDialog: this.visible,
      sortedFields: this.allFields,
      nonSelectableColumns: ['localId', 'photoId'],
      lookupMetaColumns: {},
      selectedLookup: '',
      dragOptions: {
        handle: '.icon-right',
        ghostClass: 'drag-ghost',
        dragClass: 'custom-drag',
        animation: 150,
      },
      columnNameShow: false,
      isSaving: false,
      editedColumn: null,
    }
  },
  components: {
    draggable,
  },
  created() {
    let nonSelectableColumns = this.$getProperty(
      this.columnConfig,
      'nonSelectableColumns'
    )

    if (!isEmpty(nonSelectableColumns)) {
      this.nonSelectableColumns.push(...nonSelectableColumns)
    }

    this.sortedFields = this.allFields
  },
  computed: {
    ...mapState({
      stateMetaInfo: state => state.view.metaInfo,
      stateViewDetail: state => state.view.currentViewDetail,
    }),
    metaInfo() {
      let { stateMetaInfo, relatedMetaInfo } = this
      if (!isEmpty(relatedMetaInfo)) {
        return relatedMetaInfo
      }
      return stateMetaInfo
    },
    viewDetail() {
      let { stateViewDetail, relatedViewDetail } = this
      if (!isEmpty(relatedViewDetail)) {
        return relatedViewDetail
      }
      return stateViewDetail
    },
    currentView() {
      if (this.viewName) {
        return this.viewName
      }
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return 'all'
    },
    columnConfig() {
      let { columnConfig } = this.$attrs || {}

      return !isEmpty(columnConfig)
        ? columnConfig
        : getColumnConfig(this.moduleName)
    },
    fixedColumns() {
      // Will be disabled. User wont be able to select
      let { columnConfig } = this
      if (!isEmpty(columnConfig)) {
        let { fixedColumns } = columnConfig
        return fixedColumns
      }
      return []
    },
    fixedSelectableColumns() {
      // Cannot change the order
      let { columnConfig } = this
      if (!isEmpty(columnConfig)) {
        let { fixedSelectableColumns } = columnConfig
        return fixedSelectableColumns
      }
      return []
    },
    availableColumns() {
      let { columnConfig } = this
      if (!isEmpty(columnConfig)) {
        let { availableColumns } = columnConfig
        return availableColumns
      }
      return []
    },
    metaColumns() {
      let columns = []
      if (this.metaInfo && this.metaInfo.fields && this.viewDetail) {
        columns = this.metaInfo.fields.map(field => {
          field.columnDisplayName =
            this.viewDetail.defaultModuleFields &&
            this.viewDetail.defaultModuleFields[field.name]
              ? this.viewDetail.defaultModuleFields[field.name]
                  .columnDisplayName
              : field.displayName
          return field
        })
      }
      return columns
    },
    allFields() {
      let viewFields = this.viewFields.filter(
        field => this.$getProperty(field, 'field.dataType') !== 17
      )
      let metaColumns = this.metaColumns.filter(
        field =>
          !this.viewFields.find(val => val.name === field.name) &&
          !(this.nonSelectableColumns || []).includes(field.name) &&
          (!(this.availableColumns || {}).length ||
            this.availableColumns.indexOf(field.name) !== -1 ||
            !field.default) &&
          field.dataType !== 17
      )
      let columns = [...viewFields, ...metaColumns].filter(
        field => !(this.allFixedColumns || []).includes(field.name)
      )
      return columns
    },
    viewFields() {
      let columns = []
      if (this.viewDetail && this.viewDetail.fields) {
        columns = this.viewDetail.fields.map(vf => {
          vf.name = (vf.field || {}).name || vf.fieldName
          vf.fieldId = (vf.field || {}).fieldId
          let defaultColumnName =
            this.viewDetail.defaultModuleFields &&
            this.viewDetail.defaultModuleFields[vf.name]
              ? this.viewDetail.defaultModuleFields[vf.name].columnDisplayName
              : null
          vf.columnDisplayName =
            vf.columnDisplayName || defaultColumnName || vf.field.displayName
          return vf
        })
      }
      return columns
    },
    fixedFields() {
      return this.metaColumns
        .filter(field => (this.allFixedColumns || []).includes(field.name))
        .map(field => {
          field.disabled = (this.fixedColumns || []).includes(field.name)
          return field
        })
    },

    allFixedColumns() {
      if (!isEmpty(this.fixedSelectableColumns)) {
        return this.fixedColumns.concat(this.fixedSelectableColumns)
      }
      return this.fixedColumns
    },

    lookupList() {
      let fields = []
      if ((this.columnConfig || {}).lookupToShow && this.metaColumns) {
        fields = this.metaColumns.filter(
          field =>
            field.lookupModule &&
            (!this.columnConfig.lookupToShow.length ||
              (this.columnConfig.lookupToShow || []).includes(field.name))
        )
      }
      return fields
    },
    selectedlookupColumns() {
      return this.lookupMetaColumns[this.selectedLookup]
        ? this.lookupMetaColumns[this.selectedLookup]
            .filter(
              field =>
                this.sortedFields.findIndex(
                  vf => vf.fieldId === field.fieldId
                ) === -1
            )
            .map(field => {
              field.columnDisplayName = field.displayName
              field.parentFieldId = this.selectedLookupObj.fieldId
              return field
            })
        : []
    },
    selectedLookupObj() {
      return this.lookupList.find(field => field.name === this.selectedLookup)
    },
    mainField() {
      return this.metaColumns.find(field => field.mainField)
    },
  },
  watch: {
    visible(val, oldVal) {
      if (val) {
        this.reInit()
      }
      this.showDialog = val
    },
    showDialog(val, oldVal) {
      this.$emit('update:visible', val)
    },
    allFields(val) {
      this.sortedFields = val
    },
  },
  methods: {
    showRenameColumn(field) {
      this.$set(field, 'edit', true)
      this.$nextTick(() => {
        this.$refs['columnRenameInp'][0].focus()
      })
    },
    save() {
      this.isSaving = true
      let { excludeMainField } = this
      let fields = []
      if (!isEmpty(excludeMainField)) {
        fields = [
          ...this.sortedFields
            .filter(sf => (this.selectedColumns || []).includes(sf.name))
            .map(sf => ({
              fieldId: sf.fieldId,
              parentFieldId: sf.parentFieldId,
              columnDisplayName: sf.columnDisplayName,
              fieldName: sf.name || sf.fieldName,
            })),
        ]
      } else {
        fields = [
          { fieldId: this.mainField.id },
          ...this.sortedFields
            .filter(sf => (this.selectedColumns || []).includes(sf.name))
            .map(sf => ({
              fieldId: sf.fieldId,
              parentFieldId: sf.parentFieldId,
              columnDisplayName: sf.columnDisplayName,
              fieldName: sf.name || sf.fieldName,
            })),
        ]
      }
      fields = [
        ...fields,
        ...this.selectedFixedColumns.map(id => ({ fieldId: id })),
      ]
      let self = this
      this.$store
        .dispatch('view/customizeColumns', {
          moduleName: this.moduleName,
          viewName: this.currentView,
          fields: fields,
        })
        .then(function() {
          self.isSaving = false
          self.showDialog = false
          self.$emit('refreshRelatedList')
          self.$message.success(
            self.$t('common._common.view_updated_successfully')
          )
        })
        .catch(error => {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        })
    },
    editColumn() {
      this.editedColumn = this.field
    },
    reInit() {
      this.selectedLookup = ''
      this.selectedColumns = []
      this.selectedFixedColumns = []
      this.viewFields.forEach(vf => {
        if (
          !(this.allFixedColumns || []).includes(vf.name) ||
          vf.parentFieldId !== -1
        ) {
          this.selectedColumns.push(vf.name)
        } else if ((this.fixedSelectableColumns || []).includes(vf.name)) {
          this.selectedFixedColumns.push(vf.fieldId)
        }
      })
    },
    loadModuleMeta() {
      if (this.selectedLookup && !this.lookupMetaColumns[this.selectedLookup]) {
        this.$http
          .get(
            '/module/meta?moduleName=' +
              this.selectedLookupObj.lookupModule.name
          )
          .then(response => {
            this.$set(
              this.lookupMetaColumns,
              this.selectedLookup,
              response.data.meta.fields
            )
          })
      }
    },
    getDisplayName(field) {
      let displayName = field.columnDisplayName
      if (this.viewDetail && this.viewDetail.fields) {
        let vf = this.viewDetail.fields.find(vf => vf.name === field.name)
        if (vf) {
          displayName = vf.columnDisplayName || displayName
        }
      }
      return displayName
    },
    onLookupSelected(field) {
      this.animate(field.fieldId, function() {
        this.selectedlookupColumns = this.selectedlookupColumns.filter(
          lf => lf.fieldId !== field.fieldId
        )
        field.columnDisplayName +=
          ' (' + this.selectedLookupObj.displayName + ')'
        this.sortedFields.push(field)
      })
    },
    animate(fieldId, cbk) {
      let $selectedElement = $('[data-field-id="' + fieldId + '"]')
      let oldOffset = $selectedElement.offset()
      $selectedElement.appendTo($("[data-parent='default']"))
      let newOffset = $selectedElement.offset()

      let temp = $selectedElement.clone().appendTo('body')
      temp.css({
        position: 'absolute',
        left: oldOffset.left,
        top: oldOffset.top,
        zIndex: 3000,
        width: $selectedElement[0].offsetWidth,
      })
      $selectedElement.css('visibility', 'hidden')
      let self = this
      temp.animate(
        {
          top: newOffset.top,
          left: newOffset.left,
        },
        350,
        function() {
          // $selectedElement.css('visibility', 'visible')
          $selectedElement.remove()
          temp.remove()
          cbk.call(self)
        }
      )
    },
  },
}
</script>
<style>
@import './../assets/styles/customization.css';
</style>
