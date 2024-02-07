<template>
  <el-drawer
    :append-to-body="false"
    :visible.sync="drawerVisibility"
    direction="rtl"
    size="40%"
    class="fc-drawer-hide-header"
    :destroy-on-close="true"
    @close="$emit('update:drawerVisibility', false)"
  >
    <div class="new-header-container">
      <div class="new-header-text">
        <div class="fc-setup-modal-title">
          {{ $t('common._common.kiosk_button') }}
        </div>
      </div>
    </div>
    <div class="new-body-modal">
      <el-form
        ref="customkioskForm"
        :label-position="labelPosition"
        :rules="rules"
        :model="formModel"
      >
        <el-form-item style="width:30%;" v-model="formModel.icon">
          <FFileUpload
            :imgContentUrl.sync="formModel.photoUrl"
            :isFileType="false"
            :showWebCamPhoto="false"
            :addImgFile="addImgFile"
            @removeImgFile="removeImgFile"
          >
            <div slot="placeHolderSlot" class="d-flex flex-direction-column">
              <inline-svg
                src="svgs/photo"
                class="folder-icon self-center"
                iconClass="icon icon-xxl"
              ></inline-svg>
              <div class="self-center">
                <p class="upload-img-text">
                  {{ $t('common._common.upload_logo') }}
                </p>
              </div>
            </div>
          </FFileUpload>
        </el-form-item>
        <el-form-item
          :label="$t('common._common.name')"
          prop="label"
          class="mb15"
          :required="true"
        >
          <el-input
            :placeholder="$t('common._common.enter_name')"
            v-model="formModel.label"
            class="fc-input-full-border-select2"
          ></el-input>
        </el-form-item>
        <el-form-item label="Button type" prop="buttonType" class="mb15">
          <el-select
            style="width: 90%"
            filterable
            collapse-tags
            v-model="formModel.buttonType"
          >
            <el-option label="CHECK-IN" :value="1"></el-option>
            <el-option label="CHECK-OUT" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <div class="flex mB15">
          <el-form-item
            label="Connected App"
            :required="true"
            class="mB15"
            style="padding: 13px;
                   flex: 50%;
                   padding-left: 2px;
"
          >
            <el-select
              placeholder="select APP"
              filterable
              v-model="selectedConnectedApp"
              @change="setConnectedAppWidget()"
              class="fc-input-full-border-select2"
            >
              <el-option
                v-for="widget in connectedAppsList"
                :key="widget"
                :label="widget.name"
                :value="widget.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item
            prop="connectedAppWidgetId"
            :label="$t('common.products.widget_type')"
            :required="true"
            class="mb15"
            style="padding: 13px;
                     flex: 50%;"
          >
            <el-select
              class="fc-input-full-border-select2"
              :placeholder="$t('common._common.choose_widget_type')"
              v-model="selectedConnectedAppWidget"
              filterable
              :disabled="getIsAvailabeConnectedAppWidget"
              @change="setConnectedAppWidgetId()"
            >
              <el-option
                v-for="widget in connectedAppWidgetsList"
                :key="widget.id"
                :label="widget.widgetName"
                :value="widget.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
      </el-form>
    </div>

    <div class="modal-dialog-footer">
      <el-button
        @click="$emit('update:drawerVisibility', false)"
        class="modal-btn-cancel"
        >{{ $t('common.roles.cancel') }}</el-button
      >
      <el-button
        :loading="isSaving"
        type="primary"
        class="modal-btn-save"
        @click="saveRecord"
        >{{ $t('common._common.confirm') }}</el-button
      >
    </div>
  </el-drawer>
</template>

<script>
import cloneDeep from 'lodash/cloneDeep'
import { areValuesEmpty, isEmpty } from '@facilio/utils/validation'
import FFileUpload from '@/FFileUpload'
import { getBaseURL } from 'util/baseUrl'
const fields = {
  widget: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'connectedAppWidgets',
    field: {
      lookupModule: {
        name: 'connectedAppWidgets',
        displayName: 'Connected App Widgets',
      },
    },
  },
}

export default {
  props: ['drawerVisibility', 'isEdit', 'kioskContext'],
  components: { FFileUpload },
  data() {
    return {
      connectedAppWidgetsList: [],
      connectedAppsList: [],
      selectedConnectedApp: null,
      loading: false,
      selectedConnectedAppWidget: null,
      formModel: {
        label: '',
        icon: -1,
        buttonType: 1,
        connectedAppWidgetId: null,
        photoUrl: null,
      },
      labelPosition: 'left',
      isSaving: false,
      rules: {
        label: [
          {
            required: true,
          },
        ],
        connectedAppWidgetId: [
          {
            required: true,
            message: this.$t('common._common.please_input_widget_type'),
          },
        ],
      },
    }
  },

  mounted() {
    this.loadConnectedWidget()
    if (this.isEdit && this.kioskContext?.id) {
      this.setForm()
    }
  },

  computed: {
    getIsAvailabeConnectedAppWidget() {
      let { connectedAppWidgetsList } = this
      return isEmpty(connectedAppWidgetsList)
    },
    getIsAvailabeConnectedApp() {
      let { selectedConnectedApp } = this
      return isEmpty(selectedConnectedApp)
    },
  },
  methods: {
    setConnectedAppWidgetId() {
      this.formModel.connectedAppWidgetId = this.selectedConnectedAppWidget
    },

    addImgFile(file) {
      this.formModel.icon = file
      this.$set(this, 'image', file)
    },
    removeImgFile() {
      this.$set(this, 'image', null)
      this.formModel.icon = -1
      this.formModel.photoUrl = null
    },

    setForm() {
      this.formModel = this.kioskContext
      this.selectedConnectedApp = this.$getProperty(
        this.kioskContext,
        'connectedAppWidgetContext.0.connectedAppId'
      )
      this.selectedConnectedAppWidget = this.kioskContext.connectedAppWidgetContext[0].widgetName
      if (this.formModel.icon && this.formModel.icon != -1) {
        this.formModel.photoUrl = `${getBaseURL()}/v2/files/preview/${
          this.formModel.icon
        }?fetchOriginal=true`
      }
    },
    saveRecord() {
      this.$refs['customkioskForm'].validate(valid => {
        if (valid) {
          this.isSaving = true
          let formObj = cloneDeep(this.formModel)
          this.$emit('save', formObj)
        }
      })
    },

    loadConnectedWidget() {
      this.$http.get('/v2/connectedApps/all').then(response => {
        let { data } = response || {}
        let { result } = data || {}
        let { connectedApps } = result || {}
        this.connectedAppsList = !isEmpty(connectedApps) ? connectedApps : []
      })
    },
    setConnectedAppWidget() {
      let { connectedAppsList, selectedConnectedApp } = this
      connectedAppsList.forEach(element => {
        if (element.id === selectedConnectedApp)
          this.connectedAppWidgetsList = !isEmpty(
            element.connectedAppWidgetsList
          )
            ? element.connectedAppWidgetsList
            : null
      })
    },
  },
}
</script>

<style></style>
