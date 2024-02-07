<template>
  <div>
    <div class="visitor-hor-card scale-up-left" v-if="loading">
      <spinner :show="loading" size="80"></spinner>
    </div>

    <div
      class="visitor-hor-card scale-up-left"
      v-else-if="printers.length === 0"
    >
      <div class="text-center">
        {{ $t('common.visitor_forms.no_printers_available') }}
      </div>
    </div>

    <div v-else class="mT20">
      <div
        class="visitor-hor-card scale-up-left"
        v-for="(printer, index) in printers"
        :key="printer.id"
        v-loading="loading"
      >
        <el-row class="flex-middle">
          <el-col :span="6">
            <div class="label-txt3-14">{{ printer.name || '---' }}</div>
          </el-col>
          <el-col :span="6">
            <div class="label-txt3-14">{{ printer.ip || '---' }}</div>
          </el-col>
          <el-col :span="5">
            <div class="label-txt3-14">
              {{
                getSystemEnumDisplayVal('printerModel', printer.printerModel)
              }}
            </div>
          </el-col>
          <el-col :span="5">
            <div class="label-txt3-14">
              {{ getSystemEnumDisplayVal('connectMode', printer.connectMode) }}
            </div>
          </el-col>

          <el-col :span="2" class="text-right">
            <div v-on:click.stop>
              <el-dropdown
                @command="onOptionsSelect($event, printer, index)"
                slot="reference"
                trigger="click"
              >
                <span class="el-dropdown-link">
                  <i class="el-icon-more controller-more"></i>
                </span>
                <el-dropdown-menu
                  slot="dropdown"
                  class="controller-dropdown-item"
                >
                  <el-dropdown-item command="edit">{{
                    $t('common._common.edit')
                  }}</el-dropdown-item>
                  <el-dropdown-item command="delete">
                    {{ $t('common._common.delete') }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    <el-drawer
      :visible.sync="showForm"
      direction="rtl"
      size="40%"
      class="fc-drawer-hide-header"
      @close="handleBeforeClose"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{ $t('common.visitor_forms.printer') }}
          </div>
        </div>
      </div>
      <div class="new-body-modal">
        <div class="">
          <p class="fc-input-label-txt pB10">
            {{ $t('common.products.name') }}
          </p>
          <el-input
            v-model="printerFormModel.name"
            :autofocus="true"
            :placeholder="$t('common.products.new_printer')"
            class="fc-input-full-border2 width100"
          >
          </el-input>
        </div>
        <div class="pT20">
          <p class="fc-input-label-txt pB10">
            {{ $t('common.visitor_forms.ip_address') }}
          </p>
          <el-input
            v-model="printerFormModel.ip"
            :autofocus="true"
            placeholder="192.168.1.1"
            class="fc-input-full-border2 width100"
          >
          </el-input>
        </div>
        <div class="pT20">
          <p class="fc-input-label-txt pB10">
            {{ $t('common.visitor_forms.select_printer_model') }}
          </p>
          <el-select
            v-model="printerFormModel['printerModel']"
            filterable
            clearable
            :placeholder="$t('common._common.select')"
            class="fc-input-full-border-select2 width100"
          >
            <el-option
              v-for="(option, index) in printerForm.printerModelOptions"
              :key="index"
              :label="option.label"
              :value="option.value"
            >
            </el-option>
          </el-select>
        </div>

        <div class="pT20">
          <el-radio-group v-model="printerFormModel['connectMode']">
            <el-radio
              class="fc-radio-btn"
              v-for="(option, index) in printerForm.connectModeOptions"
              :key="index"
              :label="option.value"
            >
              {{ option.label }}
            </el-radio>
          </el-radio-group>
        </div>
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="closeForm" class="modal-btn-cancel">{{
          $t('common._common.cancel')
        }}</el-button>
        <!-- <el-button type="primary" @click="addVisitorType" class="modal-btn-save"
            >Save</el-button > -->
        <async-button
          buttonType="primary"
          buttonClass="modal-btn-save"
          :clickAction="saveBtnAction"
          >{{ $t('common._common._save') }}</async-button
        >
      </div>
    </el-drawer>
  </div>
</template>

<script>
//import NewAgent from 'pages/setup/agents/NewAgent'
import AsyncButton from '@/AsyncButton'
import { getDisplayValue } from 'src/util/field-utils'
import { constructFieldOptions } from '@facilio/utils/utility-methods'

export default {
  props: [],
  title() {
    return 'Printers'
  },
  components: {
    AsyncButton,
  },
  data() {
    return {
      showForm: false,
      saveBtnAction: null,
      printerFormModel: {
        id: null,
        name: null,
        connectMode: null,
        printerModel: null,
        ip: null,
      },
      printerForm: {
        connectModeOptions: [],
        printerModelOptions: [],
      },
      loading: true,
      printers: [],
      metaInfo: null,
    }
  },
  created() {
    this.$util.loadModuleMeta('printers').then(meta => {
      console.log('printers module meta', meta)
      this.metaInfo = meta
      this.printerForm.connectModeOptions = constructFieldOptions(
        this.metaInfo.fields.find(e => e.name == 'connectMode').enumMap
      )
      this.printerForm.printerModelOptions = constructFieldOptions(
        this.metaInfo.fields.find(e => e.name == 'printerModel').enumMap
      )
      this.loadPrinters()
    })
  },

  mounted() {},
  methods: {
    handleBeforeClose() {
      for (let key in this.printerFormModel) {
        this.printerFormModel[key] = null
      }
    },
    openAddPrinterDialog() {
      this.showForm = true
      this.saveBtnAction = this.addPrinter
    },
    closeForm() {
      this.showForm = false
    },

    getSystemEnumDisplayVal(fieldName, enumVal) {
      return getDisplayValue(
        this.metaInfo.fields.find(e => e.name == fieldName),
        enumVal
      )
    },
    async addPrinter() {
      await this.$http
        .post('v2/printer/add', { printer: this.printerFormModel })
        .then(() => {
          this.showForm = false
          this.loadPrinters()
          return
        })
    },
    async editPrinter() {
      await this.$http
        .post('v2/printer/update', { printer: this.printerFormModel })
        .then(() => {
          this.showForm = false
          this.loadPrinters()
          return
        })
    },
    loadPrinters() {
      this.loading = true
      this.$http
        .get('/v2/printer/list')
        .then(response => {
          this.loading = false
          this.printers = response.data.result.printers
        })
        .catch(() => {
          this.printers = []
          this.loading = false
        })
    },

    onOptionsSelect(command, printer, index) {
      if (command === 'edit') {
        this.printerFormModel = this.$helpers.cloneObject(printer)
        this.saveBtnAction = this.editPrinter
        this.showForm = true
      } else if (command === 'delete') {
        this.$dialog
          .confirm({
            title: 'Delete printer',
            message: 'Are you sure you want to delete this Printer?',
            rbDanger: true,
            rbLabel: this.$t('common._common.delete'),
          })
          .then(value => {
            if (value) {
              this.deletePrinter(printer.id)
            }
          })
      }
    },

    async deletePrinter(printerId) {
      this.$http
        .post('v2/printer/delete', {
          printer: {
            id: printerId,
          },
        })
        .then(() => {
          this.loadPrinters()
        })
        .catch()

      this.loadPrinters()
    },
  },
}
</script>
