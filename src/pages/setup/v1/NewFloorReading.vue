<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog45 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <el-form
      ref="newFloorReadingForm"
      :model="model"
      :label-position="'top'"
      class="fc-form"
    >
      <div class="new-header-container">
        <div class="new-header-modal">
          <div class="new-header-text">
            <div class="setup-modal-title">New Floor Reading</div>
          </div>
          <!-- <div class="new-header-btn">
              <el-form-item>
                    <el-button @click="closeDialog()" class="btn-modal-border">Cancel</el-button>
                    <el-button type="primary" @click="save" class="btn-modal-fill">Save</el-button>
              </el-form-item>
          </div> -->
        </div>
      </div>
      <div class="new-body-modal mT20">
        <el-row :gutter="20" class="mT20">
          <el-col :span="12">
            <div v-if="buildingListOptions.length !== 0">
              <div class="label-txt-black pB10">Select Building</div>
              <el-select
                v-model="model.parentName"
                @change="clearList"
                filterable
                clearable
                placeholder="Select Building"
                class="width250px fc-input-full-border-select2"
              >
                <el-option
                  v-for="(option, index) in buildingListOptions"
                  :key="index"
                  :label="option"
                  :value="index"
                ></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="label-txt-black pB10">Select Floor</div>
            <el-select
              v-model="model.floorId"
              filterable
              clearable
              placeholder="Select Floor"
              class="width250px fc-input-full-border-select2"
            >
              <el-option
                v-for="floor in floorListOptions"
                :key="floor.id"
                :label="floor.name"
                :value="floor.id"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
        <div class="modal-form-input">
          <f-module-builder
            v-model="model"
            :settingsIcon="false"
          ></f-module-builder>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button type="primary" @click="save" class="modal-btn-save"
          >Save</el-button
        >
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import FModuleBuilder from 'pages/setup/new/v1/NewFmoduleBuilder'
import { getFieldOptions } from 'util/picklist'

export default {
  props: ['data', 'visibility'],
  components: {
    FModuleBuilder,
  },
  data() {
    return {
      dataTypeEnum: ['Text', 'Number', 'Decimal', 'Boolean'],
      model: {
        parentName: '',
        moduleName: '',
        floorId: '',
        fields: [
          {
            displayName: '',
            dataType: 3,
            dataTypeTemp: 3,
            counterField: false,
          },
        ],
      },
      buildingListOptions: {},
      floorListOptions: {},
    }
  },
  mounted() {
    this.init()
  },
  watch: {
    'model.parentName'(buildingId) {
      this.model.floorId = ''
      this.$util
        .loadSpace(3, null, [{ key: 'building', value: buildingId }])
        .then(response => {
          this.floorListOptions = response.basespaces
        })
    },
  },
  methods: {
    init: function() {
      this.loadPickLists()
    },
    closeDialog(ruleForm) {
      this.$emit('update:visibility', false)
      this.$refs[ruleForm].resetFields()
    },
    async loadPickLists() {
      this.floorListOptions = {}
      let { error: floorError, options: floorOptions } = await getFieldOptions({
        field: { lookupModuleName: 'floor', skipDeserialize: true },
      })

      if (floorError) {
        this.$message.error(floorError.message || 'Error Occured')
      } else {
        this.floorListOptions = floorOptions
      }

      this.buildingListOptions = {}
      let {
        error: buildingError,
        options: buildingOptions,
      } = await getFieldOptions({
        field: { lookupModuleName: 'building', skipDeserialize: true },
      })

      if (buildingError) {
        this.$message.error(buildingError.message || 'Error Occured')
      } else {
        this.buildingListOptions = buildingOptions
      }
    },
    clearList() {
      if (this.model.parentName === '') {
        this.model.floorId = ''
      }
    },
    input(val) {
      this.model.fields = val.fields
    },
    cancel() {
      this.$emit('canceled')
    },
    save() {
      let formModel = this.model
      if (formModel.floorId === '') {
        this.$message.error(' Floor cannot be empty')
        return
      }
      let self = this
      self.$http
        .post('/reading/addsetupreading', {
          resourceType: 'floor',
          fieldJsons: self.model.fields,
          parentCategoryId: formModel.floorId,
          readingName: self.model.fields[0].displayName,
        })
        .then(function(response) {
          let resp = response.data
          console.log('####### resp :', resp)
          self.$message.success(' New Reading added Successfully.')
          self.$refs.newFloorReadingForm.floorId = ''
          self.$refs.newFloorReadingForm.fields = []
          self.$emit('saved')
          self.closeDialog()
        })
    },
  },
}
</script>
<style>
.fc-create-record {
  width: 40% !important;
}
.new-header-container {
  margin-top: 0 !important;
}
</style>
