<template>
  <el-drawer
    v-bind:append-to-body="false"
    :visible.sync="drawerVisibility"
    direction="rtl"
    size="40%"
    class="fc-drawer-hide-header"
    v-bind:destroy-on-close="true"
    @close="handleDrawerClose"
  >
    <div class="new-header-container">
      <div class="new-header-text">
        <div class="fc-setup-modal-title">
          {{ $t('common.visitor_forms.visitor_kiosk') }}
        </div>
      </div>
    </div>
    <div class="new-body-modal">
      <el-form ref="visitorkioskForm" :rules="rules" :model="formModel">
        <el-form-item label="Name" prop="name" class="mb15" :required="true">
          <el-input
            :placeholder="$t('common._common.enter_name')"
            v-model="formModel.name"
            class="fc-input-full-border-select2"
          ></el-input>
        </el-form-item>
        <el-form-item
          :label="$t('common.wo_report.report_description')"
          prop="description"
          class="mb15"
        >
          <el-input
            :placeholder="$t('common._common.enter_desc')"
            v-model="formModel.description"
            class="fc-input-full-border-select2"
          ></el-input>
        </el-form-item>
        <el-form-item
          :label="$t('common.visitor_forms.printer')"
          prop="printerId"
          class="mB10"
        >
          <el-select
            :placeholder="$t('common.visitor_forms.select_printer')"
            v-model="formModel.printerId"
            class="fc-input-full-border-select2 width100"
          >
            <el-option
              v-for="printer in printers"
              :key="printer.id"
              :label="printer.name"
              :value="printer.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          :label="$t('common.products.site')"
          prop="siteId"
          class="mB10"
          :required="true"
        >
          <f-site-field
            v-bind:resetFields="true"
            :model.sync="formModel.siteId"
            :isEdit="isEdit"
            :filter.sync="filter"
            @handleSiteSwitch="siteSwitched"
          ></f-site-field>
        </el-form-item>

        <el-form-item
          :label="$t('common.header.associated_space')"
          prop="associatedResource"
          class="mB10"
          :required="true"
        >
          <el-input
            @change="canShowLookupWizard = true"
            v-model="spaceDisplayName"
            style="width:100%"
            type="text"
            :placeholder="$t('common._common.to_search_type')"
            class="fc-input-full-border-select2"
          >
            <i
              @click="canShowLookupWizard = true"
              slot="suffix"
              style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
              class="el-input__icon el-icon-search"
            ></i>
          </el-input>
          <div v-if="canShowLookupWizard">
            <FLookupFieldWizard
              :canShowLookupWizard.sync="canShowLookupWizard"
              :selectedLookupField="SpaceField"
              @setLookupFieldValue="setSpace"
              :showFilters="true"
              :siteId="selectedSiteId"
              :disableTabSwitch="true"
            ></FLookupFieldWizard>
          </div>
        </el-form-item>
        <el-form-item
          label="Default Country Code"
          class="mB10"
          prop="countryCode"
        >
          <CountryCode :fieldValue.sync="formModel.countryCode"></CountryCode>
        </el-form-item>
      </el-form>
    </div>

    <div class="modal-dialog-footer">
      <el-button
        @click="$emit('update:drawerVisibility', false)"
        class="modal-btn-cancel"
        >{{ $t('common._common.cancel') }}</el-button
      >
      <!-- <el-button type="primary" @click="addVisitorType" class="modal-btn-save"
            >Save</el-button > -->
      <el-button
        :loading="isSaving"
        type="primary"
        class="modal-btn-save"
        @click="saveRecord"
        >{{ $t('common.login_expiry.rbLabel') }}</el-button
      >
    </div>
  </el-drawer>
</template>

<script>
import FSiteField from '@/FSiteField'
import { deepClean } from '@facilio/utils/utility-methods'
import { API } from '@facilio/api'
import { areValuesEmpty, isEmpty } from '@facilio/utils/validation'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import CountryCode from 'src/components/CountryCodePhoneField.vue'
export default {
  props: ['drawerVisibility', 'isEdit', 'kioskContext'],
  components: {
    FSiteField,
    FLookupFieldWizard,
    CountryCode,
  },
  data() {
    return {
      formModel: {
        id: -1,
        name: '',
        description: '',
        printerId: null,
        siteId: null,
        associatedResource: {
          id: null,
        },
        countryCode: null,
      },
      chooserVisibility: false,
      spaceDisplayName: '',
      quickSearchQuery: '',
      filter: { site: null },
      printers: [],
      selectedSiteId: null,
      SpaceField: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'basespace',
        selectedItems: [],
        displayTypeEnum: 'WOASSETSPACECHOOSER',
        displayTypeVal: 'wo-asset-space-chooser',
        field: {
          lookupModule: {
            name: 'resource',
          },
        },
      },
      canShowLookupWizard: false,
      isPrinterLoading: true,
      isSaving: false,
      rules: {
        name: [
          {
            required: true,
            message: this.$t('common._common.please_input_kiosk_name'),
            trigger: 'blur',
          },
        ],

        siteId: [
          {
            required: true,
            message: this.$t('common._common.please_choose_a_site'),
            trigger: 'change',
          },
        ],
        associatedResource: [
          {
            validator: (rule, value, callback) => {
              if (areValuesEmpty(value)) {
                callback(
                  new Error(this.$t('common._common.please_choose_a_space'))
                )
              } else {
                callback()
              }
            },
            // required: true,
            message: this.$t('common._common.please_choose_a_space'),
            trigger: 'change',
          },
        ],
      },
    }
  },
  created() {
    this.loadPrinters()
    if (this.isEdit) {
      this.setFormModel(this.kioskContext)
    }

    this.$store.dispatch('loadSite')
  },

  methods: {
    handleDrawerClose() {
      this.$emit('update:drawerVisibility', false)
    },
    setFormModel(kioskContext) {
      Object.keys(this.formModel).forEach(key => {
        if (!isEmpty(kioskContext[key])) {
          this.$set(this.formModel, key, kioskContext[key])
        }
      })
      if (this.formModel.associatedResource.id) {
        this.spaceDisplayName = this.formModel.associatedResource.name
      }
      this.filter.site = this.formModel.siteId
      this.selectedSiteId = this.formModel.siteId
    },

    setSpace(space) {
      this.formModel.associatedResource.id = this.$getProperty(
        space,
        'field.selectedItems.0.value'
      )
      this.spaceDisplayName = this.$getProperty(
        space,
        'field.selectedItems.0.label'
      )
    },
    siteSwitched() {
      this.selectedSiteId = this.formModel.siteId
      this.formModel.associatedResource.id = null
      this.quickSearchQuery = ''
      this.spaceDisplayName = ''
    },
    saveRecord() {
      this.$refs['visitorkioskForm'].validate(valid => {
        if (valid) {
          this.isSaving = true

          this.$emit('save', deepClean(this.formModel))
        }
      })
    },

    async loadPrinters() {
      this.loading = true
      let { data, error } = await API.get('/v2/printer/list')
      if (error) {
        this.$message.error(
          this.$t('common._common.error_fetching_printer_list')
        )
        console.error(error)
      } else {
        this.printers = data.printers
        this.loading = false
      }
    },
  },
}
</script>

<style src="vue-tel-input/dist/vue-tel-input.css"></style>
<style lang="scss">
.vue-tel-input-facilio {
  width: 100%;
  height: 40px;
  border: 1px solid #d0d9e2;
  &:focus-within {
    box-shadow: none;
    border-color: #d0d9e2;
  }
}
</style>
