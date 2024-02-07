<template>
  <el-dialog
    :visible="true"
    :append-to-body="true"
    :before-close="closeForm"
    :title="$t('common._common.choose_visitor_type')"
    custom-class="fc-dialog-center-container scale-up-center"
  >
    <div class="max-height500 overflow-scroll pB50">
      <spinner v-if="loading" :show="loading" size="80"></spinner>

      <template v-else>
        <div
          v-if="$validation.isEmpty(visitorTypes)"
          class="attendance-transaction-no-data pT0"
        >
          <img src="~statics/noData-light.png" width="100" height="100" />
          <div class="mT10 label-txt-black f14">
            {{ $t('common.products.no_visitor_type_available') }}
          </div>
        </div>

        <div v-else class="log-visitor-select-con">
          <div
            class="log-visitor-avatar"
            v-for="visitorType in visitorTypes"
            :key="visitorType.id"
            @click="$emit('loadForm', visitorType)"
          >
            <inline-svg
              :src="visitorType.visitorLogoEnum.value"
              iconClass="icon icon-xxlg"
            ></inline-svg>
            <div class="fc-log-16">
              {{ visitorType.name }}
            </div>
          </div>
        </div>

        <div v-if="showInviteCode" class="modal-dialog-footer d-flex mT20 mB20">
          <div
            @click="openInviteCodeForm"
            class="pointer fc-text2-pink14 letter-spacing0_3 fc-text-underline margin-auto"
          >
            {{ $t('common.dialog.have_an_invite_code') }}
          </div>
        </div>
      </template>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { getApp } from '@facilio/router'
export default {
  props: ['showInviteCode', 'moduleName'],
  data() {
    return {
      loading: false,
      visitorTypes: [],
      appId: null,
    }
  },
  created() {
    this.loadVisitorTypes()
  },
  methods: {
    async loadVisitorTypes() {
      this.loading = true
      let param = { parentModuleName: this.moduleName }
      let { list, error } = await API.fetchAll('visitorType', param)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.visitorTypes = (list || []).filter(v => v.enabled)
      }
      //temp hardcoded for nmdp employee portal will be removed
      this.appId = (getApp() || {}).id
      if (this.appId === 1511 && this.$account.org.orgId === 429) {
        this.visitorTypes = this.visitorTypes.filter(v => v.id !== 6) || [] //Excluded Vendor type
      }
      this.loading = false
    },
    openInviteCodeForm() {
      this.$emit('openInviteCode')
    },
    closeForm() {
      this.$emit('onClose')
    },
  },
}
</script>
