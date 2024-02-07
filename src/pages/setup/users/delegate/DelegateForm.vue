<template>
  <el-dialog
    :title="
      isNew ? $t('setup.create.add_delegate') : $t('setup.create.edit_delegate')
    "
    :visible="true"
    width="45%"
    custom-class="fc-setup-dialog-form fc-setup-dialog-form fc-setup-rightSide-dialog-scroll"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <el-form
      ref="delegateForm"
      :model="delegate"
      :label-position="'top'"
      :rules="rules"
    >
      <el-form-item :label="$t('setup.approvalprocess.name')" prop="name">
        <el-input
          class="width100 fc-input-full-border2"
          :autofocus="true"
          v-model="delegate.name"
          type="text"
          :placeholder="$t('setup.placeholder.enter_bundle_name')"
        />
      </el-form-item>
      <el-form-item prop="description" label="description">
        <el-input
          class="fc-input-full-border-textarea text-capitalize"
          type="textarea"
          resize="none"
          v-model="delegate.description"
          :autosize="{ minRows: 3, maxRows: 3 }"
          :placeholder="$t('maintenance._workorder.description')"
        />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="Start date" prop="fromTime">
            <el-date-picker
              v-model="delegate.fromTime"
              type="date"
              placeholder="Select start date and time"
              class="width100 fc-input-full-border2"
              value-format="timestamp"
            >
            </el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="End date" prop="endTime">
            <el-date-picker
              v-model="delegate.toTime"
              type="date"
              placeholder="Select end date and time"
              class="width100 fc-input-full-border2"
              value-format="timestamp"
            >
            </el-date-picker>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item prop="user" label="Delegated To">
        <el-select
          v-model="delegate.delegateUserId"
          placeholder="Select"
          class="width100 fc-input-full-border2"
          filterable
        >
          <el-option
            v-for="(users, i) in userlist"
            :key="i"
            :label="users.name"
            :value="users.id"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item prop="features" label="Responsibilities">
        <el-checkbox-group
          v-model="delegate.delegationType"
          class="d-flex flex-direction-column"
        >
          <el-checkbox label="4" class="mB15">
            {{ $t('setup.users_management.approval_actions') }}
          </el-checkbox>
          <el-checkbox label="1" class="mB15">
            {{ $t('setup.users_management.receive_notifications') }}
          </el-checkbox>
          <el-checkbox label="2" class="mB15">
            {{ $t('setup.users_management.state_transitions') }}
          </el-checkbox>
          <el-checkbox label="8" class="mB15">
            {{ $t('common.header.custom_buttons') }}
          </el-checkbox>
          <el-checkbox label="16" class="mB15">
            {{ $t('common.header.dashboards') }}
          </el-checkbox>
          <el-checkbox label="32" class="mB15">
            {{ $t('common._common.views') }}
          </el-checkbox>
          <el-checkbox label="64" class="mB15">
            {{ $t('setup.users_management.data_scoping') }}
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
    </el-form>
    <div class="modal-dialog-footer">
      <el-button @click="closeDialog" class="modal-btn-cancel">
        {{ $t('setup.users_management.cancel') }}
      </el-button>
      <el-button
        type="primary"
        :loading="saving"
        class="modal-btn-save"
        @click="saveDelegate()"
      >
        {{ $t('panel.dashboard.confirm') }}
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
export default {
  props: ['isNew', 'delegateData'],
  data() {
    return {
      delegate: {
        name: '',
        description: '',
        delegationType: [],
        fromTime: '',
        toTime: '',
        delegateUserId: null,
      },
      rules: {
        name: [
          {
            required: true,
            trigger: 'blur',
            message: this.$t('setup.users_management.please_enter_name'),
          },
        ],
        delegateUserId: [
          {
            required: true,
            trigger: 'blur',
            message: this.$t('setup.users_management.selct_one_delegate'),
          },
        ],
      },
      saving: false,
      userlist: [],
    }
  },
  async created() {
    await this.loadusers()
    if (!this.isNew) {
      let { toTime, delegationType } = this.delegateData
      let delegationTypeObj = {
        4: this.$t('setup.users_management.approval_actions'),
        1: this.$t('setup.users_management.receive_notifications'),
        2: this.$t('setup.users_management.state_transitions'),
        8: this.$t('common.header.custom_buttons'),
        16: this.$t('common.header.dashboards'),
        32: this.$t('common._common.views'),
        64: this.$t('setup.users_management.data_scoping'),
      }
      this.delegate = {
        ...this.delegateData,
        toTime: toTime - 86400000 + 1,
        delegationType: Object.keys(delegationTypeObj).filter(
          key => (delegationType & parseInt(key)) !== 0
        ),
      }
    }
  },
  computed: {
    summaryId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
  },
  methods: {
    closeDialog() {
      this.$emit('onClose')
    },
    saveDelegate() {
      this.$refs['delegateForm'].validate(async valid => {
        if (!valid) return false
        this.saving = true
        let url = 'v2/userDelegation/addOrUpdate'

        let delegationType = this.delegate.delegationType.reduce(
          (totalValue, currentValue) => {
            totalValue += parseInt(currentValue)
            return totalValue
          },
          0
        )

        let params = {
          delegation: {
            name: this.delegate.name,
            description: this.delegate.description,
            fromTime: this.$helpers.getTimeInOrg(this.delegate.fromTime),
            toTime: this.$helpers.getTimeInOrg(
              this.delegate.toTime + 86400000 - 1
            ),
            userId: this.summaryId,
            delegateUserId: this.delegate.delegateUserId,
            delegationType,
            id: !this.isNew ? this.delegateData.id : null,
          },
        }
        let { error, data } = await API.post(url, params)

        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.$message.success(
            this.$t('common._common.delegate_saved_successfully')
          )
          this.$emit('onSave', data.delegateContext)
          this.closeDialog()
        }
        this.saving = false
        this.$forceUpdate()
      })
    },
    loadusers() {
      this.loading = true
      let { appId } = this
      appId = this.$account.user.applicationId
      return API.get('/v2/application/users/list', { appId }).then(
        ({ data, error }) => {
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            this.userlist = data.users || []
          }
          this.loading = false
        }
      )
    },
  },
}
</script>
