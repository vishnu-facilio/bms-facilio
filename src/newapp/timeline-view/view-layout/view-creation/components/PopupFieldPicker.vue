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
          <div class="selected_header">{{ `Selected Fields` }}</div>
          <draggable
            :list="list"
            v-bind="fieldSectionDragOptions"
            group="list"
            class="pL20 pR20"
          >
            <div v-for="(field, index) in list" :key="index" class="mB10 mT15">
              <div class="field-row d-flex">
                <div class="task-handle mR10 cursor-drag">
                  <img src="~assets/drag-grey.svg" />
                </div>
                <div class="mT-auto mB-auto fd-name">
                  {{ field.displayName || '' }}
                </div>
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
  </el-dialog>
</template>
<script>
import FieldPickerDialog from '@/fields/FieldPicker.vue'

export default {
  extends: FieldPickerDialog,
}
</script>
