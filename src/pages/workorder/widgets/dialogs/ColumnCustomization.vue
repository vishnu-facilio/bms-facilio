<template>
  <el-dialog
    :visible.sync="showDialog"
    :fullscreen="true"
    custom-class="cc-dialog"
    :title="$t('common._common.column_settings')"
  >
    <div class="scrollable" style="padding: 30px; max-height: 85vh;">
      <div class="fixed-column">
        <el-checkbox-group v-model="selectedFixedColumns">
          <el-checkbox
            class="column-item"
            v-for="field in getFixedColumns"
            :disabled="field.disabled"
            :key="field.id"
            :label="field.id"
            >{{ field.displayName }}</el-checkbox
          >
        </el-checkbox-group>
      </div>
      <el-checkbox-group v-model="selectedColumns">
        <draggable
          :options="{
            handle: '.icon-right',
            ghostClass: 'drag-ghost',
            dragClass: 'custom-drag',
            animation: 150,
          }"
          :list="sortedFields"
        >
          <el-checkbox
            class="column-item"
            v-for="field in sortedFields"
            :key="field.id"
            :label="field.id"
          >
            {{ field.displayName }}
            <i class="fa fa-exchange icon-right" m aria-hidden="true"></i>
          </el-checkbox>
        </draggable>
      </el-checkbox-group>
    </div>
    <div slot="footer" class="row cc-dialog-footer">
      <button
        type="button"
        class="el-button col-6 uppercase ls9 f12 form-btn el-button--default"
        style="background-color: #f4f4f4;border: 0;border-left: 1px solid white;border-radius: 0px;padding: 18px;font-size: 13px;font-weight: 500;letter-spacing: 0.8px;text-align: center;color: #5f5f5f;margin: 0;"
        @click="showDialog = false"
      >
        <span>{{ $t('common._common.cancel') }}</span>
      </button>
      <button
        type="button"
        class="el-button col-6 uppercase ls9 f12 form-btn  el-button--default"
        style="margin-left: 0px;color: white;background-color: #39b2c2;border: 0;border-radius: 0px;font-size: 13px;font-weight: 500;letter-spacing: 0.8px;text-align: center;color: #ffffff;"
        @click="save"
      >
        <span>{{ $t('common._common._save') }}</span>
      </button>
    </div>
  </el-dialog>
</template>

<script>
import { mapState } from 'vuex'
import draggable from 'vuedraggable'

export default {
  props: ['visible'],
  data() {
    return {
      sortedFields: this.allFields,
      selectedColumns: [],
      selectedFixedColumns: [],
      showDialog: this.visible,
      fixedColumns: ['subject'],
      fixedSelectableColumns: ['noOfNotes', 'noOfTasks'],
      availableColumns: [
        'category',
        'dueDate',
        'status',
        'assignedTo',
        'createdTime',
        'priority',
        'space',
      ],
    }
  },
  components: {
    draggable,
  },
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
      viewDetail: state => state.view.currentViewDetail,
    }),
    currentView() {
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return 'all'
    },
    allFields() {
      let columns = this.currentColumns
      if (this.metaInfo && this.metaInfo.fields) {
        columns = [
          ...columns,
          ...this.metaInfo.fields.filter(
            field =>
              !this.currentColumns.find(val => val.id === field.id) &&
              (this.availableColumns.indexOf(field.name) !== -1 ||
                !field.default)
          ),
        ].filter(field => !this.allFixedColumns.includes(field.name))
      }
      return columns
    },
    currentColumns() {
      let columns = []
      if (this.viewDetail && this.viewDetail.fields && this.metaInfo.fields) {
        columns = this.viewDetail.fields.map(field => {
          return this.metaInfo.fields.find(val =>
            field.fieldId !== -1
              ? val.id === field.fieldId
              : val.name === field.name
          )
        })
      }
      return columns
    },
    getFixedColumns() {
      let obj = []
      if (this.metaInfo && this.metaInfo.fields) {
        obj = this.metaInfo.fields
          .filter(field => this.allFixedColumns.includes(field.name))
          .map(field => {
            field.disabled = this.fixedColumns.includes(field.name)
            return field
          })
      }
      return obj
    },
    allFixedColumns() {
      return this.fixedColumns.concat(this.fixedSelectableColumns)
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
    save() {
      let fields = this.sortedFields
        .filter(field => this.selectedColumns.includes(field.fieldId))
        .map(field => {
          return { fieldId: field.fieldId }
        })
      fields = [
        ...fields,
        ...this.selectedFixedColumns.map(id => {
          return { fieldId: id }
        }),
      ]
      let self = this
      this.$store
        .dispatch('view/customizeColumns', {
          moduleName: 'workorder',
          viewName: this.currentView,
          fields: fields,
        })
        .then(function() {
          self.showDialog = false
        })
    },
    reInit() {
      this.selectedColumns = []
      this.selectedFixedColumns = []
      this.currentColumns.forEach(field => {
        if (!this.allFixedColumns.includes(field.name)) {
          this.selectedColumns.push(field.id)
        } else if (this.fixedSelectableColumns.includes(field.name)) {
          this.selectedFixedColumns.push(field.id)
        }
      })
    },
  },
}
</script>
<style>
@import './../../../../assets/styles/customization.css';
</style>
