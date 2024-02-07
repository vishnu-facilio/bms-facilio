<template>
  <el-dialog
    class="field-dialog"
    :visible="true"
    width="50%"
    :append-to-body="true"
    :show-close="false"
    :before-close="handleclose"
  >
    <div style="height:610px">
      <el-row>
        <el-col class="dialog_header">
          {{ title ? title : 'FIELDS' }}
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="8" class="border-right overflow-y-scroll height500">
          <div
            class="d-flex pT5 pB5 pL30 pR5"
            style="border-bottom: 1px solid #e5e4e4;"
          >
            <i class="el-icon-search pT7 pR10" style="color: #25243e;"></i>

            <el-input
              v-model="filterValue"
              placeholder="Search Fields"
              :autofocus="true"
              @change="filterSearchQuery(true)"
              clearable
            ></el-input>
          </div>
          <div class="pL30 pR15">
            <el-checkbox-group v-model="fieldList" class="overflow-y-scroll">
              <el-checkbox
                v-for="(field, index) in filteredList"
                :key="index"
                :label="field.name"
                class="checkbox"
              >
                {{ field.displayName }}
              </el-checkbox>
            </el-checkbox-group>
          </div>
        </el-col>

        <el-col :span="16" class="overflow-y-scroll height500">
          <div class="selected_header">Selected Fields</div>
          <draggable
            :list="list"
            v-bind="fieldSectionDragOptions"
            group="list"
            class="pL20 pR20"
          >
            <div v-for="(field, index) in list" :key="index" class="mB10 mT15">
              <div
                v-if="field.hasOwnProperty('fieldId')"
                class="field-row d-flex"
              >
                <div v-if="isInFormConfig" class="task-handle mR10">
                  <img src="~assets/drag-grey.svg" />
                </div>

                <div class="mT-auto mB-auto fd-name">
                  {{ field.displayName || '' }}
                </div>

                <div class="mL-auto mT-auto mB-auto">
                  <div
                    v-if="isInFormConfig"
                    class="mR10 pR10 inline mandatory-checkbox field-checkbox"
                  >
                    <el-checkbox v-model="field.required">
                      Is Mandatory
                    </el-checkbox>
                  </div>

                  <div @click="remove(field)" class="mR10 inline">
                    <i class="el-icon-delete pointer trash-icon"></i>
                  </div>
                </div>
              </div>
            </div>
          </draggable>
        </el-col>
      </el-row>

      <div class="modal-dialog-footer">
        <el-button @click="handleclose()" class="modal-btn-cancel">
          CANCEL
        </el-button>
        <el-button type="primary" class="modal-btn-save" @click="save()">
          Save
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import draggable from 'vuedraggable'

export default {
  props: ['title', 'availableFields', 'isInFormConfig', 'selectedList'],

  components: { draggable },

  data() {
    return {
      list: [],
      filterValue: '',
      filteredList: [],
      fieldSectionDragOptions: {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'tasksection',
        sort: true,
      },
    }
  },

  created() {
    this.list = [...this.selectedList]
    this.filterSearchQuery()
  },

  computed: {
    fieldList: {
      get() {
        let { list } = this

        if (!isEmpty(list)) {
          return list
            .filter(field => field.hasOwnProperty('fieldId'))
            .map(field => field.name)
        } else {
          return []
        }
      },

      set(value) {
        let { availableFields } = this

        let selectedFields = value
          .map(fieldName => {
            let field = availableFields.find(field => field.name === fieldName)
            return !isEmpty(field) ? field : null
          })
          .filter(f => !isEmpty(f))

        this.list = [...selectedFields]
      },
    },
  },

  methods: {
    handleclose() {
      this.$emit('close')
    },

    save() {
      this.$emit('save', this.list)
      this.handleclose()
    },

    remove(field) {
      let index = this.list.findIndex(fld => fld.name === field.name)

      this.list.splice(index, 1)
    },

    filterSearchQuery(errorMessage) {
      let filterObj = this.availableFields.filter(field => {
        let name = field.displayName.toLowerCase()

        return name.includes(this.filterValue.toLowerCase())
      })

      if (!isEmpty(filterObj)) {
        this.filteredList = filterObj
      } else {
        errorMessage ? this.$message.error('Cannot find filtered field') : null
        this.filteredList = this.availableFields
      }
    },
  },
}
</script>
<style lang="scss">
.field-dialog {
  .el-dialog__header {
    padding: 0px;
  }

  .el-dialog__body {
    padding: 0px;
  }

  .el-input .el-input__inner {
    border: none;
  }

  .height500 {
    height: 500px;
  }

  .dialog_header {
    font-size: 14px;
    font-weight: bold;
    letter-spacing: 0.5px;
    padding: 20px 30px;
    color: #25243e;
    border-bottom: 1px solid #e5e4e4;
  }

  .selected_header {
    font-size: 13px;
    font-weight: 500;
    letter-spacing: 0.5px;
    padding: 10px 20px;
    color: #25243e;
  }

  .checkbox {
    display: block;
    padding-left: 10px;
    margin: 15px 0;
  }

  .field-row {
    padding: 10px 10px;
    border: 1px solid #f4f5f7;
    box-shadow: 0px 3px 5px #f4f5f7;
    display: flex;
    font-size: 13px;
  }

  .field-row:hover {
    background-color: #f1f8fa;
  }

  .field-checkbox {
    border-right: 1px solid #eae9e9;
    letter-spacing: 0.4px;

    .el-checkbox__label {
      font-size: 13px;
    }
  }
  .fd-name {
    width: 225px;
    word-break: break-word;
  }

  img {
    height: 19px;
    width: 12px;
  }
}
</style>
