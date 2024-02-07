<template>
  <div>
    <div class="white-bg">
      <!-- <div class="fc-black-15 fwBold pL20 pT20">
        {{ $t('setup.setup.delegates') }}
      </div> -->
      <div class="fc-setup-actions-con pT0 pB0">
        <div class="flex-middle">
          <el-select
            v-model="delegateViewList"
            placeholder="Select"
            @change="delegateListLoad"
            class="fc-input-full-border2 mT10 mB10 fc-select-border-remove"
          >
            <el-option
              v-for="delegateList in delegateListShow"
              :key="delegateList.value"
              :label="delegateList.label"
              :value="delegateList.value"
            >
            </el-option>
          </el-select>

          <!-- <el-select
            v-model="delegateActive"
            placeholder="Select"
            class="fc-input-full-border2 mL20"
          >
            <el-option
              v-for="delegate in delegateView"
              :key="delegate.value"
              :label="delegate.label"
              :value="delegate.value"
            >
            </el-option>
          </el-select> -->
          <el-date-picker
            v-if="delegateActive === 'custom'"
            v-model="delegateTimePicker"
            type="daterange"
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
          <pagination
            :total="totalCount"
            :perPage="perPage"
            ref="f-page"
          ></pagination>
          <div class="fc-delagate-search mR20">
            <el-input
              v-if="toggleDeleagateSearch"
              placeholder="Search"
              v-model="searchfilter"
              :autofocus="true"
              class="f-quick-search-input"
            >
              <i
                slot="suffix"
                class="el-icon-close pointer"
                @click="closeSearch"
              ></i>
            </el-input>
            <div
              class="pointer"
              @click="toggleQuickSearch"
              v-if="showSearchIcon"
            >
              <i class="el-icon-search"></i>
            </div>
          </div>
          <el-button class="fc-setup-green-inner-btn" @click="addDelegate()">{{
            $t('common.delegation.new_delegate')
          }}</el-button>
        </div>
      </div>
      <SetupLoader v-if="loading">
        <template #setupLoading>
          <spinner :show="loading" size="80"></spinner>
        </template>
      </SetupLoader>
      <el-table
        :data="getDelegateList"
        style="width: 100%"
        height="calc(100vh - 300px)"
        class="fc-setup-table fc-setup-table-p0 fc-setup-table-th-borderTop"
        :fit="true"
        v-else
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

        <el-table-column prop="time" label="start and end date" width="250">
          <template v-slot="delegate">
            <div class="nowrap">
              {{ delegate.row.fromTime | formatDate(true) }} -
              {{ delegate.row.toTime | formatDate(true) }}
            </div>
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
        <el-table-column
          class="visibility-visible-actions"
          v-if="delegateViewList === 'delegated'"
        >
          <template v-slot="delegate">
            <div class="pointer flex-middle">
              <div>
                <i
                  class="el-icon-edit visibility-hide-actions f14"
                  data-arrow="true"
                  :title="$t('common.delegation.edit_delegate')"
                  v-tippy
                  @click="editDelegate(delegate.row)"
                ></i>
              </div>
              <div @click="deleteDelegate(delegate.row)" class="pL20 pointer">
                <i
                  class="el-icon-delete visibility-hide-actions f14 pointer"
                  data-arrow="true"
                  :title="$t('common.delegation.delete_delegate')"
                  v-tippy
                ></i>
              </div>
            </div>
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
import Pagination from 'src/components/list/FPagination'
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
      toggleDeleagateSearch: false,
      showSearchIcon: true,
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
      searchfilter: '',
    }
  },
  components: {
    Pagination,
    DelegateForm,
    SetupLoader,
  },
  created() {
    this.delegateListLoad()
  },
  computed: {
    summaryId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    getDelegateList() {
      let delagate = this.delegationList.filter(player => {
        return player.name
          .toLowerCase()
          .includes(this.searchfilter.toLowerCase())
      })

      if (this.sort == 'views') {
        return delagate.sort(function(a, b) {
          return b.views - a.views
        })
      } else {
        return delagate
      }
    },
  },
  methods: {
    async delegateListLoad(force = false) {
      this.loading = true
      let params = {}
      if (this.delegateViewList === 'delegated') {
        params.filters = JSON.stringify({
          userId: {
            operatorId: 9,
            value: [this.summaryId.toString()],
          },
        })
      } else if (this.delegateViewList === 'delegateAssign') {
        params.filters = JSON.stringify({
          delegateUserId: {
            operatorId: 9,
            value: [this.summaryId.toString()],
          },
        })
      }
      // else if(this.delegateView === 'active') {
      //   params.filters =  JSON.stringify({
      //     toTime: {
      //       operatorId: 14,
      //       value: [this.summaryId.toString()],
      //     }
      //   })
      // }
      if (this.delegateTimePicker) {
        params.filters = JSON.stringify({
          toTime: {
            operatorId: 20,
            value: this.delegateTimePicker.map(a => a.toString()),
          },
        })
      }
      let { error, data } = await API.get('v2/userDelegation/list', params, {
        force,
      })
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
    toggleQuickSearch() {
      this.toggleDeleagateSearch = true
      this.showSearchIcon = false
    },
    closeSearch() {
      this.toggleDeleagateSearch = false
      this.showSearchIcon = true
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
            this.delegateListLoad(true)
          }
        })
    },
    getDelegateType({ delegationType }) {
      let delegationTypeObj = {
        4: this.$t('setup.users_management.approval_actions'),
        1: this.$t('setup.users_management.receive_notifications'),
        2: this.$t('setup.users_management.state_transitions'),
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
<style lang="scss">
.f-quick-search-input {
  transition: max-width 0.3s linear;
  border: none !important;
  outline: none;
  background: transparent;
  display: flex;
  align-items: center;
}
.fc-delagate-search {
  .el-input .el-input__inner {
    width: 200px;
    height: 40px;
    padding-top: 1px;
    line-height: 30px;
    padding-left: 15px;
    padding-right: 30px;
    background: #f6f6f6;
    transition: all ease-in-out 0.15s;
    -webkit-transition: all ease-in-out 0.15s;
    -moz-transition: all ease-in-out 0.15s;
    border-bottom: none;
    border-radius: 4px;
    &:active,
    &:hover {
      border-color: #39b2c2 !important;
      border: 1px solid #39b2c2;
      border-bottom: 1px solid #39b2c2 !important;
    }
  }
  .el-icon-search {
    &:hover {
      color: #3ab2c2;
    }
  }
  .el-icon-close {
    &:hover {
      color: #e87171;
    }
  }
  .el-icon-close {
    color: #4f4f4f;
    font-weight: bold;
    font-size: 14px;
    padding-top: 15px;
    padding-right: 5px;
  }
  .el-icon-search {
    color: #4f4f4f;
    font-weight: bold;
    font-size: 15px;
  }
}
</style>
