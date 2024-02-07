<template>
  <el-dialog
    :visible.sync="dialogVisibility"
    :append-to-body="true"
    v-if="dialogVisibility"
    :before-close="closeDialog"
    :custom-class="customClass ? customClass : 'width60 map-location-dialog'"
    :title="title ? title : $t('space.sites.location_picker')"
  >
    <div class="pB50">
      <FLocationPicker :model="model" ref="location-picker"></FLocationPicker>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="closeDialog">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button class="modal-btn-save" type="primary" @click="saveLocation">{{
        $t('common._common.done')
      }}</el-button>
    </div>
  </el-dialog>
</template>
<script>
import FLocationPicker from '@/FLocationPicker'
export default {
  props: ['title', 'customClass', 'model', 'dialogVisibility'],
  components: {
    FLocationPicker,
  },
  methods: {
    closeDialog() {
      this.$emit('update:dialogVisibility', false)
    },
    saveLocation() {
      let location = this.$refs['location-picker'].getChoosenLocation()
      this.$emit('updateLocation', location)

      this.closeDialog()
    },
  },
}
</script>
