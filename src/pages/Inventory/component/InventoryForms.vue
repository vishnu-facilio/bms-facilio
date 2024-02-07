<template>
  <div>
    <div v-if="visibility" :key="moduleName">
      <el-dialog
        :visible.sync="visibility"
        :fullscreen="true"
        :append-to-body="true"
        :key="moduleName"
        :before-close="cancelForm"
        custom-class="fc-dialog-form fc-dialog-right setup-dialog100 setup-dialog assetaddvaluedialog fc-web-form-dialog fc-item-type-summary-dialog"
        style="z-index: 999999;"
      >
        <div
          v-if="moduleName === 'item' && currentModuleFormName === 'item_form'"
        >
          <div
            v-if="selecteditemType && selecteditemType.isRotating"
            :key="selecteditemType.id"
          >
            <facilio-web-form
              :emitForm="emitForm"
              :editObj="editObj"
              :name="'item_track_form'"
              @failed=";(saving = false), (emitForm = false)"
              @validated="data => submitForm(data)"
              :reset.sync="resetForm"
              class="facilio-inventory-web-form-body"
            ></facilio-web-form>
          </div>
          <div
            v-if="selecteditemType && !selecteditemType.isRotating"
            :key="selecteditemType.id"
          >
            <facilio-web-form
              :emitForm="emitForm"
              :editObj="editObj"
              :name="'item_form'"
              @failed=";(saving = false), (emitForm = false)"
              @validated="data => submitForm(data)"
              :reset.sync="resetForm"
              class="facilio-inventory-web-form-body"
            ></facilio-web-form>
          </div>
        </div>
        <div
          v-if="moduleName === 'tool' && currentModuleFormName === 'tool_form'"
        >
          <div
            v-if="selectedtoolType && selectedtoolType.isRotating"
            :key="selectedtoolType.id"
          >
            <facilio-web-form
              :emitForm="emitForm"
              :editObj="editObj"
              :name="'tool_track_form'"
              @failed=";(saving = false), (emitForm = false)"
              @validated="data => submitForm(data)"
              :reset.sync="resetForm"
              class="facilio-inventory-web-form-body"
            ></facilio-web-form>
          </div>
          <div
            v-if="selectedtoolType && !selectedtoolType.isRotating"
            :key="selectedtoolType.id"
          >
            <facilio-web-form
              :emitForm="emitForm"
              :editObj="editObj"
              :name="'tool_form'"
              @failed=";(saving = false), (emitForm = false)"
              @validated="data => submitForm(data)"
              :reset.sync="resetForm"
              class="facilio-inventory-web-form-body"
            ></facilio-web-form>
          </div>
        </div>
        <facilio-web-form
          v-if="
            currentModuleFormName !== 'tool_form' &&
              currentModuleFormName !== 'item_form'
          "
          :emitForm="emitForm"
          :name.sync="currentModuleFormName"
          @failed=";(saving = false), (emitForm = false)"
          @validated="data => submitForm(data)"
          :reset.sync="resetForm"
          class="facilio-inventory-web-form-body"
        ></facilio-web-form>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancelForm()">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="saveForm()"
            :loading="saving"
            >{{
              saving ? 'Submitting...' : $t('common._common._save')
            }}</el-button
          >
        </div>
      </el-dialog>
    </div>
    <div
      v-if="
        moduleName === 'item' &&
          currentModuleFormName === 'item_form' &&
          itemTypeFormChooser
      "
    >
      <el-dialog
        :visible.sync="itemTypeFormChooser"
        :fullscreen="false"
        open="top"
        width="30%"
        :title="$t('common.products.select_item_type')"
        custom-class="assetaddvaluedialog fc-dialog-center-container inventory-dialog fc-web-form-dialog"
      >
        <div class="inventory-select-item-con">
          <el-select
            filterable
            clearable
            v-model="selecteditemTypeid"
            @change="changeItemTypeinForm(selecteditemTypeid)"
            class="fc-input-full-border-select2 width500px"
          >
            <el-option
              v-for="(itemType, index) in itemTypes"
              :key="index"
              :label="itemType.name"
              :value="itemType.id"
            ></el-option>
          </el-select>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancelForm()">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="selectItemTypeForForm()"
            >{{ $t('common._common.ok') }}</el-button
          >
        </div>
      </el-dialog>
    </div>
    <div
      v-if="
        moduleName === 'tool' &&
          currentModuleFormName === 'tool_form' &&
          toolTypeFormChooser
      "
    >
      <el-dialog
        :visible.sync="toolTypeFormChooser"
        :fullscreen="false"
        :title="$t('common.products.select_tool_type')"
        open="top"
        width="30%"
        custom-class="assetaddvaluedialog inventory-dialog fc-web-form-dialog"
      >
        <div class="inventory-select-item-con">
          <el-select
            filterable
            clearable
            v-model="selectedtoolTypeid"
            @change="changeToolTypeinForm(selectedtoolTypeid)"
            class="fc-input-full-border-select2 width500px"
          >
            <el-option
              v-for="(toolType, index) in toolTypes"
              :key="index"
              :label="toolType.name"
              :value="toolType.id"
            ></el-option>
          </el-select>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancelForm()">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="selectToolTypeForForm()"
            >{{ $t('common._common.ok') }}</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import FacilioWebForm from '@/FacilioWebForm'
import { API } from '@facilio/api'
import {
  getFilteredItemTypeList,
  getFilteredToolTypeList,
} from 'pages/Inventory/InventoryUtil'

export default {
  components: {
    FacilioWebForm,
  },
  props: [
    'moduleName',
    'editData',
    'visibility',
    'currentModuleFormName',
    'itemTypeFormChooser',
    'toolTypeFormChooser',
    'isEdit',
  ],
  data() {
    return {
      editObj: null,
      emitForm: false,
      selectedtoolTypeid: null,
      resetForm: false,
      saving: false,
      selectedtoolType: null,
      selecteditemTypeid: null,
      selecteditemType: null,
      itemTypes: [],
      toolTypes: [],
    }
  },
  computed: {
    isItemModule() {
      let { moduleName, currentModuleFormName, itemTypeFormChooser } = this
      return (
        moduleName === 'item' &&
        currentModuleFormName === 'item_form' &&
        itemTypeFormChooser
      )
    },
    isToolModule() {
      let { moduleName, currentModuleFormName, toolTypeFormChooser } = this
      return (
        moduleName === 'tool' &&
        currentModuleFormName === 'tool_form' &&
        toolTypeFormChooser
      )
    },
  },
  async created() {
    if (this.isItemModule) {
      this.itemTypes = await getFilteredItemTypeList()
    }
    if (this.isToolModule) {
      this.toolTypes = await getFilteredToolTypeList()
    }
  },
  mounted() {
    if (this.editData) {
      this.editObj = this.editData
    }
  },
  methods: {
    cancelForm() {
      this.resetForm = true
      this.$emit('update:visibility', false)
      this.$emit('update:itemTypeFormChooser', false)
      this.$emit('update:toolTypeFormChooser', false)
    },
    async submitForm(data) {
      let param
      let modname = null
      if (this.moduleName === 'storeRoom') {
        param = { storeRoom: data }
      } else if (this.moduleName === 'tool') {
        param = { storeRoom: data.storeRoom.id, tool: data }
      } else if (this.moduleName === 'item') {
        let itemData = data
        if (!this.selecteditemType.isRotating) {
          for (let k = itemData.purchasedItems.length - 1; k >= 0; k--) {
            if (
              data.purchasedItems[k].hasOwnProperty('unitcost') &&
              data.purchasedItems[k].unitcost === null &&
              data.purchasedItems[k].hasOwnProperty('serialNumber') &&
              data.purchasedItems[k].serialNumber === null
            ) {
              itemData.purchasedItems.splice(k, 1)
            }
          }
        }
        param = { storeRoom: data.storeRoom.id, item: itemData }
        modname = 'item'
      } else if (this.moduleName === 'itemTypes') {
        let itemData = data
        if (this.currentModuleFormName === 'item_form') {
          for (let i = itemData.purchasedItems.length - 1; i >= 0; i--) {
            if (
              data.purchasedItems[i].hasOwnProperty('unitcost') &&
              data.purchasedItems[i].unitcost === null &&
              data.purchasedItems[i].hasOwnProperty('serialNumber') &&
              data.purchasedItems[i].serialNumber === null
            ) {
              itemData.purchasedItems.splice(i, 1)
            }
          }
          param = { storeRoom: data.storeRoom.id, item: itemData }
          modname = 'item'
        } else {
          if (data.unit === '') {
            delete data.unit
          }
          param = { itemTypes: data }
        }
      } else if (this.moduleName === 'toolTypes') {
        if (this.currentModuleFormName === 'tool_form') {
          param = { storeRoom: data.storeRoom.id, tool: data }
          modname = 'tool'
        } else {
          if (data.unit === '') {
            delete data.unit
          }
          param = { toolTypes: data }
        }
      }
      let url =
        '/v2/' +
        (modname ? modname : this.moduleName) +
        '/' +
        (this.isEdit ? 'update' : 'add')
      this.saving = true

      let { error } = await API.post(url, param)

      if (error) {
        this.$message.error(
          error.message || this.$t('common.wo_report.unable_to_update')
        )
      } else {
        this.$emit('update:visibility', false)
        this.$message.success(this.$t('common._common.added_successfully'))
        this.$emit('saved')
        this.cancelForm()
      }
      this.emitForm = false
    },
    saveForm() {
      this.emitForm = true
    },
    changeItemTypeinForm(itemTypeid) {
      if (itemTypeid) {
        this.selecteditemType = this.itemTypes.find(it => it.id === itemTypeid)
      }
    },
    changeToolTypeinForm(toolTypeid) {
      if (toolTypeid) {
        this.selectedtoolType = this.toolTypes.find(t => t.id === toolTypeid)
      }
    },
    selectItemTypeForForm() {
      this.$emit('update:itemTypeFormChooser', false)
      this.visibility = true
      this.editObj = {
        itemType: { id: this.selecteditemType.id, disabled: true },
        minimumQuantity: this.selecteditemType.minimumQuantity,
      }
    },
    selectToolTypeForForm() {
      this.$emit('update:toolTypeFormChooser', false)
      this.visibility = true
      this.editObj = {
        toolType: { id: this.selectedtoolType.id, disabled: true },
      }
    },
  },
}
</script>
