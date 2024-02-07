<template>
  <el-dialog
    class="condition-manager-picker"
    :visible="true"
    width="50%"
    :append-to-body="true"
    :show-close="false"
    :before-close="handleclose"
  >
    <div style="height:610px">
      <el-row class="dialog_header">
        <el-col :span="18" class="header-title">
          {{ 'Configure Condition Manager' }}
        </el-col>
        <el-col :span="6" class="add-tab-container">
          <div @click="showCreate = true" class="d-flex pointer">
            <inline-svg
              src="svgs/plus-button"
              class="fill-greeny-blue"
            ></inline-svg>
            <div class="add-tab">
              {{ $t('common.header.add_new') }}
            </div>
          </div>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8" class="border-right overflow-y-scroll height500">
          <div
            class="d-flex pT5 pB5 pL20 pR5"
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
          <div class="pL20 pR15">
            <el-checkbox-group v-model="fieldList" class="overflow-y-scroll">
              <el-checkbox
                v-for="(field, index) in filteredList"
                :key="index"
                :label="field.id"
                class="checkbox"
              >
                {{ field.name }}
              </el-checkbox>
            </el-checkbox-group>
          </div>
        </el-col>
        <el-col :span="16" class="overflow-y-scroll height500">
          <div class="selected_header">{{ `Selected Condition Manager` }}</div>
          <draggable
            :list="list"
            v-bind="fieldSectionDragOptions"
            group="list"
            class="pL20 pR20"
          >
            <div
              v-for="(field, index) in list"
              :key="`${field.id}-${index}`"
              class="mB10 mT15"
            >
              <div class="field-row d-flex">
                <div class="task-handle mR10 cursor-drag">
                  <img src="~assets/drag-grey.svg" />
                </div>
                <div class="mT-auto mB-auto fd-name">{{ field.name }}</div>
                <div class="mL-auto mT-auto mB-auto">
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
          {{ $t('common._common.cancel') }}
        </el-button>
        <el-button type="primary" class="modal-btn-save" @click="save()">
          {{ $t('common._common._save') }}
        </el-button>
      </div>
    </div>
    <ConditionManagerForm
      v-if="showCreate"
      :isNew="true"
      :moduleName="moduleName"
      @onSave="setConditions"
      @onClose="showCreate = false"
    ></ConditionManagerForm>
  </el-dialog>
</template>
<script>
import draggable from 'vuedraggable'
import { isEmpty } from '@facilio/utils/validation'
import ConditionManagerForm from 'pages/setup/conditionmanager/ConditionManagerForm'

export default {
  props: ['availableFields', 'selectedList', 'moduleName'],
  components: { draggable, ConditionManagerForm },
  data() {
    return {
      list: [],
      filterValue: null,
      filteredList: [],
      fieldSectionDragOptions: {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'tasksection',
        sort: true,
      },
      showCreate: false,
    }
  },
  created() {
    this.filterSearchQuery()
  },
  computed: {
    fieldList: {
      get() {
        let { list } = this

        if (!isEmpty(list)) {
          return list.map(field => field.id)
        } else {
          return []
        }
      },

      set(value) {
        let { availableFields } = this

        let selectedFields = value
          .map(fieldId => {
            let field = availableFields.find(field => field.id === fieldId)
            return !isEmpty(field) ? field : null
          })
          .filter(f => !isEmpty(f))

        this.list = [...selectedFields]
      },
    },
  },
  watch: {
    selectedList: {
      handler(newVal) {
        this.list = [...newVal]
      },
      immediate: true,
    },
  },
  methods: {
    filterSearchQuery(errorMessage) {
      let filterObj = this.availableFields.filter(field => {
        let name = field.name.toLowerCase()

        return name.includes(this.filterValue)
      })

      if (!isEmpty(filterObj)) {
        this.filteredList = filterObj
      } else {
        errorMessage
          ? this.$message.error('Cannot find condition manager')
          : null
        this.filteredList = this.availableFields
      }
    },
    remove({ id }) {
      let index = this.list.findIndex(fld => fld.id === id)
      this.list.splice(index, 1)
    },
    handleclose() {
      this.$emit('onClose')
    },
    save() {
      this.$emit('onSave', this.list)
      this.handleclose()
    },
    setConditions(condition) {
      this.$emit('updateList', condition)
    },
  },
}
</script>
<style lang="scss">
.condition-manager-picker {
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
    padding: 20px 30px 20px 20px;
    border-bottom: 1px solid #e5e4e4;
    display: flex;
    align-items: center;
  }
  .header-title {
    font-size: 14px;
    font-weight: bold;
    letter-spacing: 0.5px;
    color: #25243e;
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
  .fd-name {
    width: 225px;
    word-break: break-word;
  }
  img {
    height: 19px;
    width: 12px;
  }
  .add-tab-container {
    display: flex;
    justify-content: flex-end;

    .fill-greeny-blue {
      svg {
        path {
          fill: #39b2c2;
        }
      }
    }
    .add-tab {
      color: #39b2c2;
      margin-left: 5px;
      font-size: 13px;
      font-weight: 500;
      letter-spacing: 0.46px;
    }
  }
}
</style>
