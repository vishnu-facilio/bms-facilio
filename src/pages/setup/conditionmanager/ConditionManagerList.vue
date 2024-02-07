<template>
  <div class="condition-manager-list">
    <div class="condition-manager-header">
      <div>
        <div class="condition-manager-name">
          {{ $t('common.header.condition_manager') }}
        </div>
        <div class="condition-manager-desc">
          {{
            $t('common._common.list_of_all_modulename_conditon', {
              moduleName: moduleDisplayName || moduleName,
            })
          }}
        </div>
      </div>

      <el-button type="primary" class="setup-el-btn" @click="addConditon">
        {{ $t('common.header.add_conditions') }}
      </el-button>
    </div>

    <portal-target name="automation-modules" class="mB30"></portal-target>

    <spinner v-if="loading" :show="loading" size="80"></spinner>

    <el-table
      v-else
      :data="conditonList"
      :cell-style="{ padding: '12px 30px' }"
      empty-text="No conditions available."
      style="width: 100%"
      height="calc(100vh - 230px)"
    >
      <el-table-column
        :label="$t('common._common.name')"
        prop="name"
      ></el-table-column>

      <el-table-column class="visibility-visible-actions" width="200px">
        <template v-slot="conditon">
          <div class="text-center">
            <i
              class="el-icon-edit edit-icon visibility-hide-actions pR15"
              data-arrow="true"
              :title="$t('common.header.edit_condition')"
              v-tippy
              @click="editCondition(conditon.row)"
            ></i>
            <i
              class="el-icon-delete fc-delete-icon visibility-hide-actions"
              data-arrow="true"
              :title="$t('common.header.delete_condition')"
              v-tippy
              @click="deleteCondition(conditon.row)"
            ></i>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <ConditionManagerForm
      v-if="showAddConditon"
      :selectedConditon="selectedConditon"
      :isNew="isNew"
      :moduleName="moduleName"
      @onSave="loadConditonList"
      @onClose="showAddConditon = false"
    ></ConditionManagerForm>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import ConditionManagerForm from './ConditionManagerForm'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['moduleName', 'moduleDisplayName'],
  components: { ConditionManagerForm },

  data() {
    return {
      loading: false,
      conditonList: [],
      showAddConditon: false,
      selectedConditon: null,
      isNew: false,
    }
  },

  created() {
    this.loadConditonList()
  },

  methods: {
    async loadConditonList() {
      this.loading = true

      let { moduleName } = this
      let { error, data } = await API.post('v2/namedCriteria/list', {
        moduleName,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.conditonList = data.namedCriteriaList || []
      }
      this.loading = false
    },
    async addConditon() {
      this.selectedConditon = null
      this.isNew = true
      this.showAddConditon = true
    },
    async editCondition(conditon) {
      this.selectedConditon = conditon
      this.isNew = false
      this.showAddConditon = true
    },
    async deleteCondition({ id }) {
      let value = await this.$dialog.confirm({
        title: this.$t('common.header.delete_condition'),
        htmlMessage: this.$t(
          'common.wo_report.are_you_want_delete_this_condition'
        ),
        rbDanger: true,
        rbLabel: this.$t('common._common.confirm'),
      })

      if (!value) return

      let { error } = await API.post('v2/namedCriteria/delete', { id })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let idx = this.conditonList.findIndex(t => t.id === id)

        !isEmpty(idx) && this.conditonList.splice(idx, 1)
        this.$message.success(
          this.$t('common._common.deleted_condition_successfully')
        )
      }
    },
  },
}
</script>
<style lang="scss">
.condition-manager-list {
  padding: 20px 30px;
  height: calc(100vh - 50px);

  .condition-manager-header {
    margin-bottom: 20px;
    display: flex;
    justify-content: space-between;
  }
  .condition-manager-name {
    font-size: 18px;
    color: #000000;
    letter-spacing: 0.7px;
    padding-bottom: 5px;
    text-transform: capitalize;
  }
  .condition-manager-desc {
    font-size: 13px;
    color: #808080;
    letter-spacing: 0.3px;
  }
}
</style>
