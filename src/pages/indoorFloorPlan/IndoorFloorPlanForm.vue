<template>
  <el-dialog
    :visible="visibility"
    :before-close="closeDialog"
    :append-to-body="true"
    :fullscreen="true"
    custom-class="fc-dialog-form setup-dialog-right setup-dialog40 setup-dialog fc-web-form-dialog f-webform-right-dialog"
  >
    <div class="fc-pm-main-content-H">New Floor Plan</div>
    <div v-if="isLoading" class="loading-container d-flex">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <f-webform
      class="pT20"
      v-else
      :form.sync="formObj"
      :module="moduleName"
      :moduleData="moduleData"
      :moduleDataId="moduleDataId"
      :moduleDisplayName="moduleDisplayName"
      :isSaving="isSaving"
      formLabelPosition="top"
      :isV3Api="isV3Api"
      :isEdit="isEdit"
      :canShowPrimaryBtn="true"
      :canShowSecondaryBtn="true"
      @onBlur="onBlurHook"
      @save="saveRecord"
      @cancel="closeDialog"
    ></f-webform>
  </el-dialog>
</template>
<script>
import FWebform from '@/FWebform'
import FormCreation from '@/base/FormCreation'
import { API } from '@facilio/api'

export default {
  extends: FormCreation,
  components: {
    FWebform,
  },
  props: ['visibility', 'formData', 'recordData', 'moduleName'],
  data() {
    return {
      isSaving: false,
      formList: [],
      defaultForm: null,
      floor: null,
    }
  },
  computed: {
    moduleDisplayName() {
      if (this.formObj && this.formObj.module) {
        return this.formObj.module.displayName
      }
      return 'Indoor Floorplan'
    },
    moduleDataId() {
      let { moduleData } = this
      let { id } = moduleData || {}
      return id || null
    },
    isV3Api() {
      return true
    },
  },
  methods: {
    init() {
      if (this.formData) {
        if (this.formData) {
          this.formObj = this.formData
          this.selectedForm = this.formData
        }
        if (this.recordData) {
          this.moduleData = this.recordData
        }
      } else {
        this.loadFloorplanForms()
      }
    },
    async loadFloorplanForms() {
      this.isLoading = true
      let { data, error } = await API.get('/v2/forms', {
        moduleName: 'indoorfloorplan',
      })
      if (error) {
        this.$message.error('error loading floorplan')
      } else {
        this.formList = data.forms

        this.defaultForm = this.formList.find(
          form =>
            form.name == 'default_workplace_floorplan_web' ||
            form.name == 'default_floorplan_web' || form.name == 'default_floorplan_web_iwms'
        )
        if (this.defaultForm) {
          this.formObj = this.defaultForm
          this.selectedForm = this.defaultForm
        }
      }
    },
    afterSerializeHook({ data }) {
      //why this is needed ?.-> copied from

      this.$set(data, 'fileSource', 1)
      this.$set(data, 'height', 0)
      this.$set(data, 'width', 0)
      return data
    },
    afterSaveHook({ error, indoorfloorplan }) {
      if (!error) {
        this.loadFloor(indoorfloorplan)
        this.closeDialog()
        this.$emit('saved', indoorfloorplan)
      }
    },
    async loadFloor(indoorfloorplan) {
      let floorId = indoorfloorplan.floor.id
      let { error, data } = await API.get(
        `v2/module/data/${floorId}?moduleName=floor`
      )
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.floor = data.moduleData || []
        this.updateFloor(indoorfloorplan, this.floor)
      }
    },
    async updateFloor(indoorfloorplan, floor) {
      if (floor && indoorfloorplan && indoorfloorplan.id) {
        let url = 'floor/update'
        this.$set(floor, 'indoorFloorPlanId', indoorfloorplan.id)
        await API.post(url, { floor: floor })
      }
    },
    closeDialog() {
      this.$emit('update:visibility', false)
      this.$emit('close')
    },
  },
}
</script>
