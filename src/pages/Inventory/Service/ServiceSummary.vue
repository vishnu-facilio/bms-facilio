<template>
  <div class="inventory-page-new-con">
    <div v-if="isLoading">
      <spinner :show="isLoading"></spinner>
    </div>
    <template v-else>
      <div class="inventory-header p20">
        <!-- man header -->
        <div class="fc-dark-grey-txt18">
          <div class="fc-id">#{{ summaryData.id }}</div>
          <div class="fc-black-17 bold inline-flex">
            <el-tooltip
              placement="bottom"
              effect="dark"
              :content="summaryData.name"
            >
              <span class="whitespace-pre-wrap custom-header">{{
                summaryData.name
              }}</span>
            </el-tooltip>
          </div>
        </div>
        <div class="fc__layout__align inventory-overview-btn-group">
          <template v-if="isStateFlowEnabled">
            <TransitionButtons
              class="mR10"
              :key="`${summaryData.id}transitions`"
              :moduleName="moduleName"
              :record="summaryData"
              :disabled="false"
              buttonClass="asset-el-btn"
              @currentState="() => {}"
              @transitionSuccess="loadSummary(true)"
              @transitionFailure="() => {}"
            ></TransitionButtons>
          </template>
          <el-dropdown
            v-if="
              $hasPermission('inventory:UPDATE') ||
                $hasPermission('inventory:DELETE')
            "
            class="mL15 fc-btn-ico-lg pT5 pB5 pL8 pR8 pointer"
            @command="handleCommand"
          >
            <span class="el-dropdown-link">
              <img src="~assets/menu.svg" height="18" width="18" />
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                v-if="$hasPermission('inventory:UPDATE')"
                command="edit"
                >{{ this.$t('common._common.edit') }}</el-dropdown-item
              >
              <el-dropdown-item
                v-if="$hasPermission('inventory:DELETE')"
                command="delete"
                >{{ this.$t('common._common.delete') }}</el-dropdown-item
              >
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <Page
        :key="summaryData.id"
        :module="moduleName"
        :id="summaryData.id"
        :details="summaryData"
        :primaryFields="[]"
        :isV3Api="true"
      ></Page>
    </template>
  </div>
</template>
<script>
import Page from '@/page/PageBuilder'
import TransitionButtons from '@/stateflow/TransitionButtons'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['moduleName', 'viewname'],
  components: { Page, TransitionButtons },

  data() {
    return {
      isLoading: false,
      summaryData: null,
    }
  },
  created() {
    this.loadSummary()
  },
  computed: {
    id() {
      return parseInt(this.$route.params.id)
    },
    getCurrentModule() {
      return 'service'
    },
    isStateFlowEnabled() {
      let { summaryData } = this
      return this.$getProperty(summaryData, 'moduleState.id')
    },
  },
  watch: {
    id(newVal) {
      if (!isEmpty(newVal)) {
        this.loadSummary()
      }
    },
  },
  methods: {
    async loadSummary(force = false) {
      let { id, moduleName } = this

      this.isLoading = true

      let { error, [moduleName]: data } = await API.fetchRecord(
        moduleName,
        {
          id,
        },
        { force }
      )

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.summaryData = data
      }
      this.isLoading = false
    },

    handleCommand(command) {
      if (command === 'edit') {
        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule('service', pageTypes.EDIT) || {}
          name &&
            this.$router.push({
              name,
              params: {
                id: this.summaryData.id,
              },
            })
        } else {
          this.$router.push({
            path: '/app/inventory/service/edit/' + this.summaryData.id,
          })
        }
      } else if (command === 'delete') {
        this.deleteRecord()
      }
    },
    async deleteRecord() {
      let value = await this.$dialog.confirm({
        title: this.$t('common.header.delete_service'),
        message: this.$t('common._common.delete_confirmation'),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })

      if (value) {
        let { moduleName, summaryData, viewname } = this
        let { id } = summaryData || {}
        let { error } = await API.deleteRecord(moduleName, id)

        if (error) {
          this.$message.error(error.message || 'Error Occurred')
        } else {
          this.$message.success(this.$t('common._common.delete_success'))
          if (isWebTabsEnabled()) {
            let { name } = findRouteForModule('service', pageTypes.LIST) || {}
            name &&
              this.$router.push({
                name,
                params: {
                  viewname,
                },
              })
          } else {
            this.$router.push({
              path: '/app/inventory/service/' + viewname,
            })
          }
        }
      }
    },
  },
}
</script>
