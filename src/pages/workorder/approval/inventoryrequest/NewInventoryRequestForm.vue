<template>
  <div>
    <div v-if="woRequest">
      <el-dialog
        :visible.sync="woRequest"
        :fullscreen="true"
        :before-close="closeWOForm"
        :append-to-body="true"
        custom-class="fc-dialog-form fc-dialog-right setup-dialog100 setup-dialog assetaddvaluedialog fc-web-form-dialog fc-item-type-summary-dialog"
        style="z-index: 999999;"
      >
        <facilio-web-form
          :editObj="editData"
          :disableForm="disableForm"
          :emitForm="emitForm"
          :name="'inventoryRequestWOForm'"
          @failed=";(saving = false), (emitForm = false)"
          @validated="data => save(data)"
          :reset.sync="resetForm"
          class="facilio-inventory-web-form-body"
        >
        </facilio-web-form>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="closeWOForm">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            class="modal-btn-save"
            type="primary"
            :disabled="disableForm"
            @click="initaSave()"
            :loading="saving"
            >{{
              saving
                ? $t('common._common.submitting')
                : $t('common._common._save')
            }}</el-button
          >
        </div>
      </el-dialog>
    </div>
    <div v-else class="new-approval-scroll">
      <div class="fc-pm-form-right-main3">
        <div class="heading-black22 mT20 mB20 flex-middle">
          <error-banner
            :error.sync="error"
            :errorMessage.sync="errorText"
          ></error-banner>
          <div v-if="!isEdit" class="heading-black22">
            {{ $t('common.header.create_inventory_request') }}
          </div>
          <div v-else class="heading-black22">
            {{ $t('common.header.edit_inventory_request') }}
          </div>
        </div>
        <div v-if="loading" class="fc-pm-main-bg">
          <spinner :show="loading" size="80"></spinner>
        </div>
        <div v-if="!loading" class="fc-pm-main-bg">
          <div class="fc-pm-main-content2">
            <facilio-web-form
              :editObj="editData"
              :emitForm="emitForm"
              :name="'inventoryRequestForm'"
              @failed=";(saving = false), (emitForm = false)"
              @validated="data => save(data)"
              :reset.sync="resetForm"
              class="facilio-purchase-web-form-body"
            >
            </facilio-web-form>
            <div class="modal-dialog-footer">
              <el-button class="modal-btn-cancel" @click="goBack">{{
                $t('common._common.cancel')
              }}</el-button>
              <el-button
                type="primary"
                class="modal-btn-save"
                :loading="saving"
                @click="initaSave()"
                >{{
                  saving
                    ? $t('common._common._saving')
                    : $t('common._common._save')
                }}</el-button
              >
            </div>
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
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  props: ['woRequest', 'woDetails', 'irEditData'],
  mixins: [FormMixin],
  components: {
    FacilioWebForm,
    ErrorBanner,
  },
  data() {
    return {
      prIds: null,
      emitForm: false,
      loading: true,
      resetForm: false,
      editData: null,
      saving: false,
      error: false,
      errorText: '',
      isEdit: false,
      errorIndex: null,
      disableForm: false,
    }
  },
  mounted() {
    this.loading = true
    let id = this.$route.query.edit
    if (id) {
      this.isEdit = true
      this.loadData()
    }
    if (this.woRequest && this.woDetails) {
      this.editData = {
        name:
          'Required Inventory Items for WO -' +
          ' [#' +
          this.woDetails.localId +
          ']',
        workOrder: '[#' + this.woDetails.id + '] ' + this.woDetails.name,
        requestedBy: { id: this.$store.state.account.user.id },
        requestedFor: { id: this.$store.state.account.user.id },
      }
    } else {
      this.editData = {
        requestedBy: { id: this.$store.state.account.user.id },
        requestedFor: { id: this.$store.state.account.user.id },
      }
    }
    if (!isEmpty(this.irEditData)) {
      this.irEditData['workOrder'] =
        '[#' + this.woDetails.id + '] ' + this.woDetails.name
      this.editData = this.irEditData
      if (this.irEditData.status !== 1) {
        this.disableForm = true
      }
    }
    this.loading = false
  },
  computed: {
    formLayout() {
      if (this.$route.params.layout) {
        return this.$route.params.layout
      }
      return null
    },
    id() {
      return parseInt(this.$route.query.edit)
    },
    moduleName() {
      return 'inventoryrequest'
    },
  },
  methods: {
    loadData() {
      let url = '/v2/inventoryrequest/details'
      let params = {
        recordId: this.id,
      }
      this.$http
        .post(url, params)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.editData =
              response.data.result && response.data.result.inventoryrequest
                ? response.data.result.inventoryrequest
                : null
          }
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    initaSave() {
      this.emitForm = true
    },
    save(rule) {
      this.emitForm = false
      rule = this.formateData(rule)
      if (rule.lineItems.length === 0) {
        this.$message.error(this.$t('common._common.enter_atleast_one_item'))
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
        return
      }
      this.validation(rule)
      this.submitForm(rule)
    },
    validation(rule) {
      this.error = false
      this.errorText = ''
      if (rule.name === null || rule.name === '') {
        this.errorText = this.$t('common.header.please_enter_the_name')
        this.error = true
      }
    },
    submitForm(data) {
      let url = 'v2/inventoryrequest/addOrUpdate'
      if (this.id) {
        data.id = this.id
      } else if (this.irEditData && this.irEditData.id) {
        data.id = this.irEditData.id
      }
      let param = {
        inventoryRequest: data,
      }
      this.saving = true
      this.$http.post(url, param).then(({ data }) => {
        if (data.responseCode === 0) {
          this.saving = false
          this.resetForm = false
          this.isFormSaved = true
          if (this.woRequest) {
            this.$emit('update:woRequest', false)
            this.$emit('saved')
            this.$message.success(
              this.$t('common.header.inventory_request_has_been_sent')
            )
            return
          }
          if (url) {
            let { inventoryrequest } = data.result || {}
            let { id } = inventoryrequest || {}

            if (isWebTabsEnabled()) {
              let { name } =
                findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
              name &&
                this.$router.push({
                  name,
                  params: {
                    id,
                    viewname: 'all',
                  },
                })
            } else {
              this.$router.push({
                path: `/app/inventory/inventoryrequest/all/${id}/overview`,
              })
            }
          }
          url += data.result.inventoryrequest.id
        } else {
          this.saving = false
          this.isFormSaved = false
          this.$message.error(data.message)
          // this.goBack()
        }
      })
      this.emitForm = false
    },
    formateData(rule) {
      if (rule.siteId === null) {
        delete rule.siteId
      }
      if (rule.workOrder) {
        delete rule.workOrder
        rule['parentId'] = this.woDetails.id
      }
      if (rule.requestedBy.id === -1 || rule.requestedBy.id === null) {
        delete rule.requestedBy
      }
      if (rule.storeRoom.id === null) {
        rule.storeRoom.id = -1
      }
      if (isNaN(rule.requestedTime) || rule.requestedTime === null) {
        delete rule.requestedTime
      }
      if (isNaN(rule.requiredTime) || rule.requiredTime === null) {
        delete rule.requiredTime
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
          rule.lineItems[i].quantity === 0 ||
          rule.lineItems[i].quantity === '' ||
          rule.lineItems[i].quantity === null ||
          rule.lineItems[i].unitPrice === '' ||
          rule.lineItems[i].unitPrice === null
        ) {
          rule.lineItems.splice(i, 1)
        }
      }
      return rule
    },
    goBack() {
      window.history.go(-1)
    },
    closeWOForm() {
      this.$emit('update:woRequest', false)
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
