<template>
  <el-dialog
    style="z-index: 999999"
    custom-class="f-kfi-card-builder fc-dialog-center-container kpi-map-setup-dilaog"
    :append-to-body="true"
    :visible.sync="visibility"
    :width="'50%'"
    title="OBJECT SETTINGS"
    :before-close="closeDialog"
  >
    <div class="object-setting-body" v-if="!loading">
      <el-tabs type="settings" v-model="activeTab">
        <el-tab-pane label="Style" name="style">
          <el-row v-if="object.type === 'group'">
            <el-col
              :span="8"
              class="p10"
              v-for="(obj, index) in object._objects"
              :key="index"
            >
              <template v-if="obj.fill">
                <p>{{ `${obj.type}: color` }}</p>
                <el-color-picker
                  :predefine="getPredefinedColors()"
                  v-model="obj.fill"
                ></el-color-picker>
              </template>
              <template v-if="obj.hasOwnProperty('visible')">
                <p>{{ `${obj.type}: show` }}</p>
                <el-checkbox v-model="obj.visible">Show</el-checkbox>
              </template>
            </el-col>
          </el-row>
          <el-row v-else>
            <el-col :span="8" class="p10">
              <p>color</p>
              <el-color-picker
                :predefine="getPredefinedColors()"
                v-model="object.fill"
              ></el-color-picker>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="closeDialog" class="modal-btn-cancel">
        {{ $t('setup.users_management.cancel') }}</el-button
      >
      <el-button type="primary" class="modal-btn-save" @click="save()">
        Save
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import colors from 'charts/helpers/colors'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['visibility', 'object'],
  data() {
    return {
      activeTab: 'style',
      currentObject: null,
      loading: false,
      defaultStyleProps: {
        fill: '#000',
        opacity: 0.8,
        visible: true,
      },
    }
  },
  mounted() {
    console.log('********** object', this.object)
    // this.getObjects()
  },
  methods: {
    getObjects() {
      this.loading = true
      let { floorplan } = this.object
      let { styles } = floorplan
      this.currentObject = floorplan
      if (!isEmpty(floorplan) && !isEmpty(styles)) {
        this.$set(this.currentObject, 'styles', {
          ...this.defaultStyleProps,
          ...styles,
        })
      } else if (!isEmpty(floorplan) && isEmpty(styles)) {
        this.$set(this.currentObject, 'styles', {
          ...this.defaultStyleProps,
        })
      }
      this.loading = false
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    getPredefinedColors() {
      return colors.readingcardColors
    },
    save() {
      this.$emit('updateObject', this.object)
      this.closeDialog()
    },
  },
}
</script>
<style>
.object-setting-body {
  padding-bottom: 60px;
}
</style>
