<template>
  <el-dialog
    :visible="true"
    :fullscreen="false"
    :title="$t('common.header.input_storeroom_details')"
    open="top"
    width="30%"
    :before-close="closeDialog"
    custom-class="inventory-dialog fc-web-form-dialog"
  >
    <div class="inventory-select-item-con">
      <el-select
        filterable
        clearable
        v-model="selectedStore"
        class="fc-input-full-border-select2 width500px"
      >
        <el-option
          v-for="(store, index) in storeRoom"
          :key="index"
          :label="store.name"
          :value="store.id"
        ></el-option>
      </el-select>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="closeDialog">
        {{ $t('common._common.cancel') }}
      </el-button>
      <el-button
        class="modal-btn-save"
        type="primary"
        :loading="saving"
        @click="selectStoreRoom()"
        >{{ $t('common._common.ok') }}</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import { getFilteredStoreRoomList } from 'pages/Inventory/InventoryUtil'

export default {
  props: ['selectedStoreId', 'inventoryrequest', 'updateStatus'],

  data() {
    return {
      storeRoom: [],
      saving: false,
    }
  },

  computed: {
    selectedStore: {
      get() {
        return this.selectedStoreId
      },
      set(value) {
        this.$emit('update:selectedStoreId', value)
      },
    },
  },

  async created() {
    this.storeRoom = await getFilteredStoreRoomList()
  },

  methods: {
    selectStoreRoom() {
      if (this.selectedStore) {
        let store = this.storeRoom.find(
          store => store.id === this.selectedStore
        )
        this.saving = true
        this.updateStatus({ store })
      }
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
