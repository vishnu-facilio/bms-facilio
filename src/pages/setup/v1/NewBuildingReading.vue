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
      ref="newBuildingReadingForm"
      :model="model"
      :label-position="'top'"
    >
      <div class="new-header-container">
        <div class="new-header-modal">
          <div class="new-header-text">
            <div class="setup-modal-title">New Building Reading</div>
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
        <el-row>
          <el-col :span="12">
            <div v-if="siteListOptions.length !== 0">
              <div class="label-txt-black pB10">Select Site</div>
              <el-select
                v-model="model.parentName"
                @change="clearList"
                filterable
                clearable
                placeholder="Select Site"
                class="width250px fc-input-full-border-select2"
              >
                <el-option
                  v-for="(option, index) in siteListOptions"
                  :key="index"
                  :label="option"
                  :value="index"
                ></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="label-txt-black pB10">Select Building</div>
            <el-select
              v-model="model.buildingId"
              filterable
              clearable
              placeholder="Select Building"
              class="width250px fc-input-full-border-select2"
            >
              <el-option
                v-for="building in buildingListOptions"
                :key="building.id"
                :label="building.name"
                :value="building.id"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
        <!-- <div class="label-txt-black">Select Building</div> -->
        <div>
          <f-module-builder
            @input="input"
            :isNew="isNew"
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
      isNew: true,
      formTitle: 'New Asset Reading',
      categoryList: [],
      dataTypeEnum: ['Text', 'Number', 'Decimal', 'Boolean'],
      model: {
        parentName: '',
        moduleName: '',
        buildingId: '',
        fields: [
          {
            displayName: '',
            dataType: 3,
            dataTypeTemp: 3,
            counterField: false,
          },
        ],
      },
      siteListOptions: {},
      buildingListOptions: {},
    }
  },
  created() {
    this.$store.dispatch('loadBuildings')
  },
  mounted() {
    this.init()
  },
  watch: {
    'model.parentName'(siteId) {
      this.$util
        .loadSpace(2, null, [{ key: 'site', value: siteId }])
        .then(response => {
          this.buildingListOptions = response.basespaces
        })

      // this.buildingListOptions = this.buildingListOptions.find(option => option.siteId === siteId)
    },
  },
  methods: {
    init: function() {
      this.loadPickLists()
    },
    closeDialog(ruleForm) {
      this.$emit('update:visibility', false)
      // this.$refs[ruleForm].resetFields()
    },
    async loadPickLists() {
      this.siteListOptions = {}
      let { error: siteError, options: siteOptions } = await getFieldOptions({
        field: { lookupModuleName: 'site', skipDeserialize: true },
      })

      if (siteError) {
        this.$message.error(siteError.message || 'Error Occured')
      } else {
        this.siteListOptions = siteOptions
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
        this.model.buildingId = ''
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
      let self = this
      self.$http
        .post('/reading/addsetupreading', {
          resourceType: 'Building',
          fieldJsons: self.model.fields,
          parentCategoryId: formModel.buildingId,
          readingName: self.model.fields[0].displayName,
        })
        .then(function(response) {
          let resp = response.data
          console.log('####### resp :', resp)
          self.$message.success(' New Reading added Successfully.')
          self.$refs.newBuildingReadingForm.buildingId = ''
          self.$refs.newBuildingReadingForm.fields = []
          self.$emit('saved')
          self.closeDialog()
        })
    },
  },
}
</script>
