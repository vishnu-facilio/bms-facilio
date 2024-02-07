<template>
  <div class="fc-bulk-form-page portal-group-invite">
    <div class="bulk-form-data-creation">
      <el-header height="80" class="bulk-form-header">
        <div class="header d-flex justify-content-space">
          <div class="form-header-name-description">
            <el-input
              v-model="groupName"
              placeholder="Group Invite Name"
              :autofocus="true"
              class="width300px"
            ></el-input>
            <button
              @click="groupinvitedialog = true"
              class="group-invite-description"
              style="position: relative;
                top: 5px;
                left: 14px;
                font-size: medium;"
            >
              <i class="el-icon-tickets" style="width: 13px;"></i>
            </button>

            <el-dialog
              title="Group description"
              :visible.sync="groupinvitedialog"
              class="el-dialog__body"
            >
              <el-input
                type="textarea"
                :autosize="{ minRows: 7, maxRows: 7 }"
                class="fc-input-full-border-textarea"
                :placeholder="$t('setup.setupLabel.add_a_decs')"
                v-model="groupDescription"
                resize="none"
              ></el-input>

              <div class="modal-dialog-footer">
                <el-button @click="canceldialog" class="modal-btn-cancel">
                  {{ $t('common._common.cancel') }}
                </el-button>
                <el-button
                  type="primary"
                  class="modal-btn-save"
                  @click="groupinvitedialog = false"
                >
                  {{ $t('common._common.confirm') }}
                </el-button>
              </div>
            </el-dialog>
          </div>
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
              v-if="!this.isEdit"
              class="small-border-btn-save width50 bR3 pB13 pT13 f12 text-uppercase"
              @click="bulkSubmit()"
              :loading="isSaving"
            >
              {{ $t('common._common._save') }}
            </el-button>
            <el-button
              v-if="this.isEdit"
              class="small-border-btn-save width50 bR3 pB13 pT13 f12 text-uppercase"
              @click="bulkSubmit()"
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
      <InviteBulkForm
        ref="invitebulkform"
        v-else-if="lookupLoading"
        :form.sync="formObj"
        :module="moduleName"
        :moduleDisplayName="moduleDisplayName"
        :isSaving="isSaving"
        :canShowPrimaryBtn="false"
        :canShowSecondaryBtn="false"
        :isEdit="isEdit"
        :customClass="customClass"
        :moduleDataId="moduleDataId"
        :moduleData="invitesList"
        :lookupOptions="lookupOptions"
      ></InviteBulkForm>
    </div>
  </div>
</template>
<script>
import InviteForm from 'pages/visitors/invites/InviteGroupForm.vue'
import { findRouteForModule, pageTypes } from '@facilio/router'
import InviteBulkForm from 'src/components/bulkform/InviteBulkForm.vue'
export default {
  extends: InviteForm,
  data() {
    return {
      groupDescription: '',
    }
  },
  components: { InviteBulkForm },

  methods: {
    afterSerializeHook({ data }) {
      data.requestedBy = { id: this.$portaluser.ouid }
      return data
    },
    redirectToList() {
      let dialogObj = {
        title: 'You have not saved your changes.',
        htmlMessage: 'Are you sure you want leave this page !',
        rbLabel: 'LEAVE',
      }
      this.$dialog.confirm(dialogObj).then(value => {
        if (value) {
          let moduleName = 'groupinvite'
          let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
          name && this.$router.push({ name })
        }
      })
    },
    canceldialog() {
      if (this.isEdit) {
        this.groupinvitedialog = false
        this.groupDescription = this.desc
      } else {
        this.groupinvitedialog = false
        this.groupDescription = ''
      }
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
  .group-invite-description {
    padding: 5px 8px 5px 4px;
    cursor: pointer;
    text-align: center;
    border: none;
    border-left: 2px solid transparent;
    background: none;
  }
  .group-invite-description:hover,
  .group-invite-description:focus {
    padding: 5px 8px 5px 4px;
    border-radius: 50%;
    background-color: rgb(202 212 216 / 0.5);
  }
  .el-dialog__body {
    display: block;
    margin-left: auto;
    margin-right: auto;
    padding-top: 1px;
  }
  .fc-input-full-border-textarea {
    height: 199px;
    position: relative;
    top: 7px;
  }
  .form-header-name-description {
    display: block ruby;
  }
}
</style>
<style lang="scss">
.employee-portal-homepage {
  .el-dialog__title {
    position: relative;
    bottom: 18px;
  }
  .fc-input-full-border-textarea {
    height: 199px;
    position: relative;
    top: -20px;
  }
  .bulk-form-header {
    height: 90px;
  }
  .justify-content-space {
    position: relative;
    bottom: 4px;
  }
}
</style>
