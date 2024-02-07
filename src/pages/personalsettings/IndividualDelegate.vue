<template>
  <div>
    <SetupLoader v-if="loading">
      <template #setupLoading>
        <spinner :show="loading" size="80"></spinner>
      </template>
    </SetupLoader>
    <div v-else>
      <div class="fc-black-15 fwBold pL20 pT20">Delegates</div>

      <div class="fc-setup-actions-con pT0 pL0">
        <div class="flex-middle">
          <el-select
            v-model="delegateViewList"
            placeholder="Select"
            @change="delegateListLoad"
            class="fc-input-full-border2 mT20 mL20 mB10"
          >
            <el-option
              v-for="delegateList in delegateListShow"
              :key="delegateList.value"
              :label="delegateList.label"
              :value="delegateList.value"
            >
            </el-option>
          </el-select>
          <el-date-picker
            v-if="delegateActive === 'custom'"
            v-model="delegateTimePicker"
            type="datetimerange"
            range-separator="To"
            start-placeholder="Start date"
            end-placeholder="End date"
            class="mL20"
            @change="delegateListLoad()"
            value-format="timestamp"
          >
          </el-date-picker>
        </div>
        <div class="flex-middle">
          <f-search
            class="mL20"
            v-model="delegationList"
            v-if="delegationList.length > 0"
          ></f-search>
          <!-- <el-button class="fc-setup-green-inner-btn" @click="addDelegate()"
            >New Delegate</el-button
          > -->
        </div>
      </div>
      <el-table
        :data="delegationList"
        style="width: 100%"
        height="calc(100vh - 320px)"
        class="fc-setup-table fc-setup-table-p0 fc-setup-table-th-borderTop"
        :fit="true"
      >
        <template slot="empty">
          <div class="height57vh fc-align-center-column">
            <inline-svg
              src="svgs/list-empty"
              iconClass="icon text-center icon-xxxxlg"
            ></inline-svg>
            <div class="label-txt-black f18 bold">
              {{ $t('setup.empty.empty_delegate') }}
            </div>
          </div>
        </template>
        <el-table-column prop="name" label="Name">
          <template v-slot="delegate">
            {{ delegate.row.name }}
          </template>
        </el-table-column>
        <el-table-column
          prop="delegate"
          label="Delegated To"
          v-if="delegateViewList === 'delegated'"
        >
          <template v-slot="delegate">
            {{
              delegate.row &&
                delegate.row.delegateUser &&
                delegate.row.delegateUser.name
            }}
          </template>
        </el-table-column>
        <el-table-column
          prop="delegate"
          label="Delegated From"
          v-if="delegateViewList === 'delegateAssign'"
        >
          <template v-slot="delegate">
            {{ delegate.row && delegate.row.user && delegate.row.user.name }}
          </template>
        </el-table-column>
        <el-table-column prop="features" label="Responsibilities">
          <template v-slot="delegate">
            <div v-if="$validation.isEmpty(getDelegateType(delegate.row))">
              ---
            </div>
            <div
              v-for="delegated in getDelegateType(delegate.row)"
              :key="delegated"
            >
              <div class="fc-delegate-tag">
                {{ delegated }}
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="time" label="start and end Time" width="250">
          <template v-slot="delegate">
            {{ delegate.row.fromTime | formatDate(true) }} -
            {{ delegate.row.toTime | formatDate(true) }}
          </template>
        </el-table-column>
      </el-table>
    </div>
    <delegateForm
      v-if="showAddDelegate"
      :delegateData="selectedDelegate"
      :isNew="isNew"
      @onSave="delegateListLoad"
      @onClose="showAddDelegate = false"
    >
    </delegateForm>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import FSearch from '@/FSearch'
import DelegateForm from 'pages/setup/users/delegate/DelegateForm'
import SetupLoader from 'pages/setup/components/SetupLoader'
export default {
  params: ['userInformation'],
  data() {
    return {
      delegationList: [],
      loading: true,
      totalCount: null,
      perPage: 50,
      showAddDelegate: false,
      selectedDelegate: null,
      isNew: false,
      delegateTimePicker: null,
      delegateActive: null,
      delegateView: [
        {
          label: 'Active & Upcoming',
          value: 'Active',
        },
        {
          label: 'Custom',
          value: 'custom',
        },
      ],
      delegateListShow: [
        {
          label: 'My Delegates',
          value: 'delegated',
        },
        {
          label: 'Delegated to me',
          value: 'delegateAssign',
        },
      ],
      delegateViewList: 'delegated',
    }
  },
  components: {
    FSearch,
    DelegateForm,
    SetupLoader,
  },
  created() {
    this.delegateListLoad()
  },
  methods: {
    async delegateListLoad() {
      this.loading = true
      let params = {}
      if (this.delegateViewList === 'delegated') {
        params.filters = JSON.stringify({
          userId: {
            operatorId: 9,
            value: [this.$account.user.id.toString()],
          },
        })
      } else if (this.delegateViewList === 'delegateAssign') {
        params.filters = JSON.stringify({
          delegateUserId: {
            operatorId: 9,
            value: [this.$account.user.id.toString()],
          },
        })
      }
      if (this.delegateTimePicker) {
        params.filters = JSON.stringify({
          datetime: {
            operatorId: 20,
            value: this.delegateTimePicker.map(a => a.toString()),
          },
        })
      }
      let { error, data } = await API.get('v2/userDelegation/list', params)
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.delegationList = data.delegationList || []
      }
      this.loading = false
    },
    addDelegate() {
      this.showAddDelegate = true
      this.isNew = true
      this.selectedDelegate = null
    },
    editDelegate(delegate) {
      this.showAddDelegate = true
      this.isNew = false
      this.selectedDelegate = delegate
    },
    deleteDelegate(delegate) {
      this.$dialog
        .confirm({
          title: this.$t('common.header.delete_delegate'),
          message: this.$t(
            'common.header.are_you_sure_you_want_to_delete_this_delegate'
          ),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(async value => {
          if (!value) return

          let { error } = await API.post('v2/userDelegation/delete', {
            id: delegate.id,
          })

          if (error) {
            this.$message.error(
              error.message || this.$t('setup.delete.delete_delegate_failed')
            )
          } else {
            this.$message.success(this.$t('setup.delete.delete_delegate'))
            this.delegateListLoad()
          }
        })
    },
    getDelegateType({ delegationType }) {
      let delegationTypeObj = {
        1: this.$t('setup.users_management.receive_notifications'),
        2: this.$t('setup.users_management.state_transitions'),
        4: this.$t('setup.users_management.approval_actions'),
        8: this.$t('common.header.custom_buttons'),
        16: this.$t('common.header.dashboards'),
        32: this.$t('common._common.views'),
        64: this.$t('setup.users_management.data_scoping'),
      }

      return Object.entries(delegationTypeObj)
        .filter(([key]) => delegationType & parseInt(key))
        .map(([, value]) => value)
    },
  },
}
</script>
