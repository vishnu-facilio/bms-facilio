<template>
  <div class="email-settings-container">
    <portal to="wo-email-settings-header-from-address">
      <div class="display-flex-between-space m20">
        <div class="setting-title-block">
          <div class="setting-form-title">
            {{ $t('setup.from_address.heading') }}
          </div>
          <div class="heading-description">
            {{ $t('setup.from_address.heading_desc') }}
          </div>
        </div>
        <div class="action-btn setting-page-btn">
          <el-button
            type="primary"
            class="el-button setup-el-btn el-button--primary"
            @click="addRecord"
          >
            {{ $t('setup.from_address.add') }}
          </el-button>
        </div>
      </div>
    </portal>
    <div v-if="loading" class="flex-middle">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div
      v-else-if="$validation.isEmpty(fromMails)"
      class="flex-middle height100 justify-content-center flex-direction-column white-bg"
    >
      <inline-svg
        src="svgs/emptystate/notes"
        iconClass="icon text-center icon-130"
      ></inline-svg>
      <div class="nowo-label">
        {{ $t('setup.from_address.no_data') }}
      </div>
    </div>
    <div v-else>
      <el-table
        ref="associateTaxTable"
        :data="fromMails"
        class="setup-tax-table fc-form-address-table"
        height="calc(100vh - 206px)"
        v-loading="loading"
        row-key="index"
      >
        <el-table-column
          sortable
          :label="$t('setup.from_address.display_name')"
          prop="displayName"
        >
        </el-table-column>
        <el-table-column
          sortable
          :label="$t('setup.from_address.email')"
          prop="email"
        ></el-table-column>
        <el-table-column
          sortable
          :label="$t('setup.from_address.source_type')"
          prop="sourceType"
        >
          <template slot-scope="data">
            <span>{{
              $constants.fromEmailAddressSourceType[data.row.sourceType]
            }}</span>
          </template>
        </el-table-column>
        <el-table-column
          sortable
          label="Verification Status"
          prop="verificationStatus"
        >
          <template slot-scope="data">
            <div class="flex-middle">
              <div
                :class="
                  data.row.verificationStatus
                    ? 'fc-green-status'
                    : 'fc-red-status'
                "
              >
                {{
                  data.row.verificationStatus
                    ? $t('setup.from_address.verified')
                    : $t('setup.from_address.not_verified')
                }}
              </div>

              <inline-svg
                v-if="!data.row.verificationStatus"
                @click.native="sendVerificationEmail(data.row)"
                v-tippy="{
                  placement: 'top',
                  animation: 'shift-away',
                  arrow: true,
                  content: 'Resend Invite mail',
                }"
                :src="`svgs/service-requests/email`"
                iconClass="icon icon-xs fill-blue6 visibility-hide-actions"
                class="mL10 vertical-middle"
              ></inline-svg>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="130" class="visibility-visible-actions">
          <template slot-scope="data">
            <div class="text-center">
              <span @click="openEditForm(data.row)">
                <inline-svg
                  src="svgs/edit"
                  class="edit-icon-color visibility-hide-actions"
                  iconClass="icon icon-sm mR5 icon-edit"
                ></inline-svg>
              </span>
              <span
                v-if="data.row.creationType !== 1"
                @click="deleteRecord(data.row.id)"
              >
                <inline-svg
                  src="svgs/delete"
                  class="pointer edit-icon-color visibility-hide-actions mL10"
                  iconClass="icon icon-sm icon-remove"
                ></inline-svg>
              </span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <FromAddressForm
      v-if="formVisibility"
      :visibility.sync="formVisibility"
      :record="editRecord"
      @saved="refreshList"
    />
  </div>
</template>
<script>
import FromAddressForm from './FromAddressForm'
import { API } from '@facilio/api'
export default {
  components: {
    FromAddressForm,
  },
  data() {
    return {
      loading: false,
      fromMails: [],
      formVisibility: false,
      editRecord: null,
    }
  },
  created() {
    this.loadList()
  },
  methods: {
    addRecord() {
      this.editRecord = null
      this.formVisibility = true
    },
    refreshList() {
      this.loadList(true)
    },
    async loadList(forceFetch = false) {
      this.loading = true
      let { list = [], error } = await API.fetchAll(
        `emailFromAddress`,
        {
          withCount: false,
          viewName: 'all',
        },
        { force: forceFetch }
      )
      if (error) {
        let { message } = error
        this.$message.error(
          message || 'Error Occured while fetching From address list'
        )
      } else {
        this.fromMails = list || []
      }
      this.loading = false
    },
    openEditForm(data) {
      this.editRecord = data
      this.formVisibility = true
    },
    async deleteRecord(id) {
      let value = await this.$dialog.confirm({
        title: this.$t('setup.from_address.delete'),
        message: this.$t('setup.from_address.delete_confirm'),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })

      if (value) {
        this.loading = true

        let { error } = await API.deleteRecord('emailFromAddress', [id])
        if (error) {
          let { message } = error
          this.$message.error(
            message || this.$t('setup.from_address.error_delete')
          )
          this.loading = false
        } else {
          this.$message.success(this.$t('setup.from_address.success_delete'))
          this.loadList(true)
          // Setting loading false in list API
        }
      }
    },
    async sendVerificationEmail(record) {
      let { email } = record || {}
      let { error } = await API.post(
        `/v3/mailmessage/reSendVerificationEmail`,
        {
          fromAddress: {
            email,
          },
        }
      )
      if (error) {
        let { message } = error
        this.$message.error(
          message || 'Error occurred while sending invite mail'
        )
      } else {
        this.$message.success('Invite mail sent successfully')
        this.loadList(true)
      }
    },
  },
}
</script>
<style lang="scss">
.fc-form-address-table {
  .el-table td.el-table__cell div {
    padding-left: 10px;
    padding-right: 10px;
  }
  .el-table__body td.el-table__cell {
    padding-left: 20px;
    padding-right: 20px;
  }
  thead .el-table__cell .cell {
    display: flex;
    align-items: center;
    flex-wrap: nowrap;
    white-space: nowrap;
    padding-left: 0;
  }
}
</style>
