<template>
  <div class="new-approval-scroll">
    <div class="fc-pm-form-right-main3">
      <div class="heading-black22 mT20 mB20 flex-middle">
        <error-banner
          :error.sync="error"
          :errorMessage.sync="errorText"
        ></error-banner>

        <div v-if="!isEdit" class="heading-black22">
          Create {{ displayNameMap[moduleName] }}
        </div>
        <div v-else-if="duplicate" class="heading-black22">
          Duplicate {{ displayNameMap[moduleName] }}
        </div>
        <div v-else class="heading-black22">
          Edit {{ displayNameMap[moduleName] }}
        </div>
      </div>
      <div v-if="loading" class="fc-pm-main-bg">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div v-else class="fc-pm-main-bg">
        <div class="fc-pm-main-content2">
          <facilio-web-form
            :editObj="editData"
            :emitForm="emitForm"
            :name="moduleVsFormName[moduleName]"
            @failed=";(saving = false), (emitForm = false)"
            @validated="data => save(data)"
            :reset.sync="resetForm"
            class="facilio-purchase-web-form-body"
          >
          </facilio-web-form>
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel" @click="goBack"
              >CANCEL</el-button
            >
            <el-button
              type="primary"
              class="modal-btn-save"
              :loading="saving"
              @click="initaSave()"
              >{{ saving ? 'Saving...' : 'SAVE' }}</el-button
            >
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import FacilioWebForm from '@/FacilioWebForm'
import FormMixin from '@/mixins/FormMixin'
import ErrorBanner from '@/ErrorBanner'
import moment from 'moment-timezone'
import { isEmpty, isNullOrUndefined } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
export default {
  props: ['model'],
  mixins: [FormMixin],
  components: {
    FacilioWebForm,
    ErrorBanner,
  },
  data() {
    return {
      emitForm: false,
      loading: true,
      resetForm: false,
      editData: null,
      saving: false,
      error: false,
      errorText: '',
      isEdit: false,
      errorIndex: null,
      displayNameMap: {
        purchasecontracts: 'Purchase Contract',
        labourcontracts: 'Labour Contract',
        rentalleasecontracts: 'Lease/Rental Contract',
        warrantycontracts: 'Warranty Contract',
      },
      fetchDataMap: {
        purchasecontracts: {
          url: '/v2/purchasecontract/getById',
          key: 'purchasecontract',
        },
        labourcontracts: {
          url: '/v2/labourcontract/getById',
          key: 'labourcontract',
        },
        rentalleasecontracts: {
          url: '/v2/rentalleasecontract/getById',
          key: 'rentalleasecontract',
        },
        warrantycontracts: {
          url: '/v2/warrantycontract/getById',
          key: 'warrantycontract',
        },
      },
      moduleVsFormName: {
        purchasecontracts: 'purchaseContractForm',
        labourcontracts: 'labourContractForm',
        rentalleasecontracts: 'rentalLeaseContractForm',
        warrantycontracts: 'warrantyContractForm',
      },
      noLineItemBool: false,
    }
  },
  mounted() {
    let { id } = this
    if (id) {
      this.isEdit = true
      this.loadData()
    } else {
      this.loading = false
    }
  },
  computed: {
    moduleName() {
      if (this.$route.params.moduleName) {
        return this.$route.params.moduleName
      }
      return ''
    },
    id() {
      return parseInt(this.$route.params.id)
    },
    duplicate() {
      return Boolean(this.$route.query.duplicate)
    },
  },
  methods: {
    loadData() {
      let { moduleName, fetchDataMap } = this
      let url = (fetchDataMap[moduleName] || {}).url
      let key = (fetchDataMap[moduleName] || {}).key
      let params = {
        recordId: this.id,
      }
      this.loading = true
      this.$http
        .post(url, params)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.editData = response.data.result[key]
            this.formateEditData()
            this.loading = false
          } else {
            throw new Error(response.data.message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
          this.loading = false
        })
    },
    formateEditData() {
      if (this.editData.lineItems && this.editData.lineItems.length == 0) {
        delete this.editData.lineItems
      }
      if (this.editData.fromDate <= 0) {
        delete this.editData.fromDate
      }
      if (this.editData.endDate <= 0) {
        delete this.editData.endDate
      }
      if (this.editData.renewalDate <= 0) {
        delete this.editData.renewalDate
      }
      if (
        !isEmpty((this.editData || {}).data) &&
        !isEmpty(this.editData.data.tenant)
      ) {
        this.editData['tenant'] = this.editData.data.tenant
      }
      if (!isEmpty(this.editData.rentalLeaseContractType)) {
        this.editData.rentalLeaseContractType = String(
          this.editData.rentalLeaseContractType
        )
      }
      if (this.editData.frequencyType > 0) {
        this.editData['payment'] = {
          isCheckedPayment: true,
          paymentInterval: this.editData.paymentInterval,
          scheduleDay: this.editData.scheduleDay,
          scheduleMonth: this.editData.scheduleMonth,
          scheduleTime: moment()
            .startOf('day')
            .milliseconds(this.editData.scheduleTime)
            .valueOf(),
          frequencyType: this.editData.frequencyType,
        }
      }
    },
    initaSave() {
      this.emitForm = true
    },
    async save(obj) {
      this.emitForm = false
      let rule = this.$helpers.cloneObject(obj)
      rule = await this.formateData(rule)
      if (rule.lineItems.length === 0) {
        this.noLineItemBool = true
        if (
          this.moduleName === 'purchasecontracts' ||
          this.moduleName === 'rentalleasecontracts'
        ) {
          rule.lineItems.push({
            inventoryType: 1,
            itemType: {
              id: null,
            },
            toolType: {
              id: null,
            },
            quantity: null,
            unitPrice: null,
          })
        } else if (this.moduleName === 'labourcontracts') {
          rule.lineItems.push({
            labour: {
              id: null,
            },
            cost: null,
          })
        } else if (this.moduleName === 'warrantycontracts') {
          rule.lineItems.push({
            service: { id: null },
            toolCoverage: null,
            itemCoverage: null,
            labourCoverage: null,
          })
        }
      } else {
        this.noLineItemBool = false
      }
      this.validation(rule)
      this.submitForm(rule)
    },
    validation(rule) {
      this.error = false
      this.errorText = ''
      if (rule.name === null || rule.name === '') {
        this.errorText = 'Please enter the Name'
        this.error = true
      }
      if (rule.payment && rule.payment.isCheckedPayment) {
        if (rule.frequencyType === 1) {
          if (rule.paymentInterval < 0 || rule.scheduleTime < 0) {
            this.$message.error('Kindly Input Payment Details')
            return
          }
        } else if (rule.frequencyType === 2) {
          if (
            rule.paymentInterval < 0 ||
            rule.scheduleTime < 0 ||
            rule.scheduleDay < 0
          ) {
            this.$message.error('Kindly Input Payment Details')
            return
          }
        } else if (rule.frequencyType === 3) {
          if (
            rule.paymentInterval < 0 ||
            rule.scheduleTime < 0 ||
            rule.scheduleDay < 0
          ) {
            this.$message.error('Kindly Input Payment Details')
            return
          }
        } else if (rule.frequencyType === 4) {
          if (
            rule.paymentInterval < 0 ||
            rule.scheduleTime < 0 ||
            rule.scheduleDay < 0 ||
            rule.scheduleMonth < 0
          ) {
            this.$message.error('Kindly Input Payment Details')
            return
          }
        }
        rule.paymentInterval = rule.payment.paymentInterval
        rule.scheduleDay = rule.payment.scheduleDay
        rule.scheduleMonth = rule.payment.scheduleMonth
        rule.scheduleTime = this.getTimeinMilliSeconds(
          rule.payment.scheduleTime
        )
        rule.frequencyType = rule.payment.frequencyType
      }
    },
    submitForm(rule) {
      let data = this.$helpers.cloneObject(rule)
      if (this.noLineItemBool) {
        delete data.lineItems
      }
      let self = this
      let url = ''
      let message = this.displayNameMap[this.moduleName] + ''
      if (this.id && !this.duplicate) {
        data.id = this.id
        message += ' Edited Successfully'
      } else {
        message += ' Added Successfully'
      }
      let param
      if (this.moduleName === 'purchasecontracts') {
        url = 'v2/purchasecontract/addOrUpdate'
        param = {
          purchaseContract: data,
        }
      } else if (this.moduleName === 'labourcontracts') {
        param = {
          labourContract: data,
        }
        url = 'v2/labourcontract/addOrUpdate'
      } else if (this.moduleName === 'rentalleasecontracts') {
        param = {
          rentalLeaseContract: data,
        }
        url = 'v2/rentalleasecontract/addOrUpdate'
      } else if (this.moduleName === 'warrantycontracts') {
        param = {
          warrantyContract: data,
        }
        url = 'v2/warrantycontract/addOrUpdate'
      }
      this.saving = true
      this.$http.post(url, param).then(response => {
        if (response.data.responseCode === 0) {
          self.saving = false
          self.resetForm = false
          self.isFormSaved = true
          self.$message.success(message)

          if (isWebTabsEnabled()) {
            let { name } =
              findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
            if (name) {
              let { id } = response.data.result[this.moduleName]
              this.$router.replace({ name, params: { viewname: 'all', id } })
            }
          } else {
            if (self.moduleName === 'purchasecontracts') {
              url =
                '/app/ct/purchasecontracts/all/summary/' +
                response.data.result.purchasecontracts.id
            } else if (self.moduleName === 'labourcontracts') {
              url =
                '/app/ct/labourcontracts/all/summary/' +
                response.data.result.labourcontracts.id
            } else if (self.moduleName === 'rentalleasecontracts') {
              url =
                '/app/ct/rentalleasecontracts/all/summary/' +
                response.data.result.rentalleasecontract.id
            } else if (self.moduleName === 'warrantycontracts') {
              url =
                '/app/ct/warrantycontracts/all/summary/' +
                response.data.result.warrantycontracts.id
            }
            self.$router.replace({ path: url })
          }
        } else {
          self.isFormSaved = true
          self.$message.error(response.data.message)
          self.saving = false
        }
      })
      this.emitForm = false
    },
    async formateData(rule) {
      let url = `/v2/modules/fields/fields?moduleName=${this.moduleName}`
      let { data, error } = await API.get(url)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let customFields = data.fields.filter(field => field.default === false)
        if (customFields) {
          for (let cf of customFields) {
            if (rule[cf.name]) {
              if (!rule.data) {
                rule.data = {}
              }
              rule.data[cf.name] = rule[cf.name]
            }
          }
        }
      }

      if (isNaN(rule.endDate) || rule.endDate === null) {
        delete rule.endDate
      }
      if (isNaN(rule.fromDate) || rule.fromDate === null) {
        delete rule.fromDate
      }
      if (isNaN(rule.renewalDate) || rule.renewalDate === null) {
        delete rule.renewalDate
      }
      if (!isEmpty(rule.tenant)) {
        if (isNullOrUndefined(rule.data)) {
          rule.data = {}
        }
        rule.data.tenant = rule.tenant
        delete rule.tenant
      }
      for (let i = rule.lineItems.length - 1; i >= 0; i--) {
        if (rule.lineItems[i].inventoryType === 1) {
          delete rule.lineItems[i].toolType
          if (!rule.lineItems[i].itemType.id) {
            rule.lineItems.splice(i, 1)
            continue
          }
        } else if (rule.lineItems[i].inventoryType === 2) {
          delete rule.lineItems[i].itemType
          if (!rule.lineItems[i].toolType.id) {
            rule.lineItems.splice(i, 1)
            continue
          }
        }
        if (
          (this.moduleName === 'purchasecontracts' &&
            (isEmpty(rule.lineItems[i].quantity) ||
              isEmpty(rule.lineItems[i].unitPrice))) ||
          (this.moduleName === 'labourcontracts' &&
            (isEmpty(rule.lineItems[i].cost) ||
              isEmpty(rule.lineItems[i].labour.id))) ||
          (this.moduleName === 'warrantycontracts' &&
            isEmpty(rule.lineItems[i].service.id)) ||
          (this.moduleName === 'rentalleasecontracts' &&
            isEmpty(rule.lineItems[i].unitPrice))
        ) {
          rule.lineItems.splice(i, 1)
        }
      }
      return rule
    },
    goBack() {
      window.history.go(-1)
    },
    getTimeinMilliSeconds(val) {
      let dt = new Date(val)
      let millisecs = 60000 * (dt.getMinutes() + 60 * dt.getHours())
      return millisecs
    },
  },
}
</script>
<style>
.new-approval-scroll {
  height: 100vh;
  overflow-y: scroll;
  overflow-x: hidden;
  padding-bottom: 150px;
}
.new-approval-scroll .fc-pm-form-right-main3 {
  width: 100%;
  max-width: 1000px;
  float: left;
  margin-left: 60px;
}
.taskheight {
  min-height: calc(100vh - 300px) !important;
}
.facilio-purchase-web-form-body {
  overflow-y: scroll;
  overflow-x: hidden;
  text-align: left;
  height: inherit;
  margin-bottom: 0px;
}
</style>
