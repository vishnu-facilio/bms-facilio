<template>
  <div class="fc-bulk-form-page portal-group-invite">
    <div class="bulk-form-data-creation">
      <el-header height="80" class="bulk-form-header">
        <div class="header d-flex justify-content-space">
          <el-input
            v-model="groupName"
            placeholder="Group Invite Name"
            :autofocus="true"
            class="width300px"
          ></el-input>
          <el-select
            v-if="forms.length > 1"
            v-model="selectedForm"
            value-key="name"
            class="fc-input-full-border-select2 mL-auto width25"
          >
            <el-option
              v-for="(form, index) in forms"
              :key="index"
              :value="form"
              :label="form.displayName"
            ></el-option>
          </el-select>

          <div class="flex-middle width15">
            <el-button
              class="small-border-btn width50 bR3 pB13 pT13 f12 text-uppercase"
              @click="redirectToList"
            >
              {{ $t('common._common.cancel') }}
            </el-button>
            <el-button
              class="small-border-btn-save width50 bR3 pB13 pT13 f12 text-uppercase"
              @click="bulkSubmit"
              :loading="isSaving"
            >
              {{ $t('common._common._save') }}
            </el-button>
          </div>
        </div>
      </el-header>
      <div v-if="isLoading" class="loading-container d-flex">
        <Spinner :show="isLoading"></Spinner>
      </div>
      <bulk-form
        ref="bulkform"
        v-else
        :form.sync="formObj"
        :module="moduleName"
        :moduleDisplayName="moduleDisplayName"
        :isSaving="isSaving"
        :canShowPrimaryBtn="false"
        :canShowSecondaryBtn="false"
        :isEdit="isEdit"
        :customClass="customClass"
      ></bulk-form>
    </div>
  </div>
</template>
<script>
import InviteForm from 'src/pages/visitors/invites/OldInviteGroupForm.vue'
import { findRouteForModule, pageTypes } from '@facilio/router'
export default {
  extends: InviteForm,
  methods: {
    afterSerializeHook({ data }) {
      let { visitorTypeId: id } = this
      data.invitevisitor.forEach(element => {
        ;(element.requestedBy = { id: this.$portaluser.ouid }),
          (element.visitorType = { id })
      })
      return data
    },
    redirectToList() {
      let moduleName = 'invitevisitor'
      let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
      name && this.$router.push({ name })
    },
  },
}
</script>
<style lang="scss">
@import '../../../node_modules/handsontable/dist/handsontable.full.min.css';

.portal-group-invite {
  &.fc-bulk-form-page {
    .bulk-form-container {
      height: calc(100vh - 100px);
    }
  }
  .handsontable tbody th.ht__highlight,
  .handsontable thead th.ht__highlight {
    background-color: #f1f3f5;
  }

  .handsontable th {
    background-color: #ffffff;
    color: #879eb5;
    font-size: 12px;
    font-weight: 500;
    letter-spacing: 1px;
    text-transform: uppercase;
  }

  .handsontable th,
  .handsontable td {
    border-right: 1px solid rgb(230, 235, 240) !important;
    border-bottom: 1px solid rgb(230, 235, 240) !important;
  }

  .handsontable .htNoFrame + td,
  .handsontable .htNoFrame + th,
  .handsontable.htRowHeaders thead tr th:nth-child(2),
  .handsontable td:first-of-type,
  .handsontable th:first-child,
  .handsontable th:nth-child(2) {
    border-left: none !important;
  }

  .handsontable tr:first-child td,
  .handsontable tr:first-child th {
    border-top: none !important;
  }

  .handsontable table thead th {
    white-space: pre-line;
  }

  .handsontable thead th .relative {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .handsontable .cornerHeader::after {
    content: 'No';
  }

  .handsontable thead th {
    padding: 10px !important;
  }

  .handsontable th {
    background-color: #ffffff;
    color: #879eb5;
    font-size: 12px;
    font-weight: 500;
    letter-spacing: 1px;
    text-transform: uppercase;
  }

  .handsontable th,
  .handsontable td {
    border-right: 1px solid rgb(230, 235, 240) !important;
    border-bottom: 1px solid rgb(230, 235, 240) !important;
  }

  .handsontable .htNoFrame + td,
  .handsontable .htNoFrame + th,
  .handsontable.htRowHeaders thead tr th:nth-child(2),
  .handsontable td:first-of-type,
  .handsontable th:first-child,
  .handsontable th:nth-child(2) {
    border-left: none !important;
  }

  .handsontable tr:first-child td,
  .handsontable tr:first-child th {
    border-top: none !important;
  }

  .wtHolder tr th div {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
  }

  .wtHolder tr td,
  .wtHolder tr td,
  .ht_master tr td {
    vertical-align: middle;
    color: #333 !important;
    padding: 10px;
  }
}
</style>
