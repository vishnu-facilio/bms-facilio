<template>
  <div class="triggers-list">
    <div class="trigger-header">
      <div>
        <div class="trigger-name">{{ $t('common.wo_report.triggers') }}</div>
        <div class="trigger-desc">
          {{ $t('common._common.list_of_all') }}
          {{ moduleDisplayName || moduleName }}
          {{ $t('common.wo_report.triggers') }}
        </div>
      </div>

      <el-button type="primary" class="setup-el-btn" @click="addTrigger">
        {{ $t('common.header.add_trigger') }}
      </el-button>
    </div>

    <portal-target name="automation-modules" class="mB30"></portal-target>

    <spinner v-if="loading" :show="loading" size="80"></spinner>

    <el-table
      v-else
      :data="triggersList"
      :cell-style="{ padding: '12px 30px' }"
      empty-text="No triggers available."
      style="width: 100%"
      height="calc(100vh - 200px)"
    >
      <el-table-column
        :label="$t('common._common.name')"
        prop="name"
      ></el-table-column>

      <el-table-column :label="$t('common.header.event_type')" width="300px">
        <template v-slot="trigger">
          {{ eventTypes[trigger.row.eventType] }}
        </template>
      </el-table-column>

      <el-table-column :label="$t('common.products.status')" width="150px">
        <template v-slot="trigger">
          <el-switch
            v-model="trigger.row.status"
            :disabled="trigger.row.isDefault"
            @change="changeStatus(trigger.row)"
            active-color="rgba(57, 178, 194, 0.8)"
            inactive-color="#e5e5e5"
          ></el-switch>
        </template>
      </el-table-column>

      <el-table-column class="visibility-visible-actions" width="200px">
        <template v-slot="trigger">
          <div class="text-center flex-middle">
            <div
              @click="openSummary(trigger.row)"
              class="visibility-hide-actions pR15 actions-txt"
            >
              {{ $t('common.products.actions') }}
            </div>
            <template v-if="!trigger.row.isDefault">
              <i
                class="el-icon-edit edit-icon visibility-hide-actions pR15"
                data-arrow="true"
                :title="$t('common.wo_report.edit_trigger')"
                v-tippy
                @click="editTrigger(trigger.row)"
              ></i>
              <i
                class="el-icon-delete fc-delete-icon visibility-hide-actions"
                data-arrow="true"
                :title="$t('common._common.delete_trigger')"
                v-tippy
                @click="deleteTrigger(trigger.row)"
              ></i>
            </template>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <TriggersForm
      v-if="showAddTrigger"
      :trigger="selectedTrigger"
      :isNew="isNew"
      :moduleName="moduleName"
      @onSave="loadTriggers"
      @onClose="showAddTrigger = false"
    ></TriggersForm>

    <TriggerActions
      v-if="showSummary"
      :trigger="selectedTrigger"
      @onClose="showSummary = false"
    ></TriggerActions>
  </div>
</template>
<script>
import TriggersForm from './TriggersForm'
import TriggerActions from './TriggerActions'
import { isEmpty } from '@facilio/utils/validation'
import { Trigger } from './TriggerModel'

const eventTypes = {
  1: 'Create',
  2: 'Edit',
  4: 'Delete',
  1073741824: 'Custom',
}

export default {
  props: ['moduleName', 'moduleDisplayName'],
  components: { TriggersForm, TriggerActions },

  data() {
    return {
      loading: false,
      triggersList: [],
      showAddTrigger: false,
      selectedTrigger: null,
      showSummary: false,
      isNew: false,
      eventTypes,
    }
  },

  async created() {
    this.loadTriggers()
  },

  methods: {
    async loadTriggers() {
      try {
        this.loading = true
        this.triggersList = await Trigger.fetchAll(this.moduleName)
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
      this.loading = false
    },
    addTrigger() {
      this.selectedTrigger = null
      this.isNew = true
      this.showAddTrigger = true
    },
    editTrigger(trigger) {
      this.selectedTrigger = trigger
      this.isNew = false
      this.showAddTrigger = true
    },
    async deleteTrigger({ id }) {
      let value = await this.$dialog.confirm({
        title: this.$t('common._common.delete_trigger'),
        htmlMessage: this.$t('common.wo_report.are_you_want_delete_trigger'),
        rbDanger: true,
        rbLabel: this.$t('common._common.confirm'),
      })

      if (!value) return

      try {
        await Trigger.delete({ id })
        let idx = this.triggersList.findIndex(t => t.id === id)

        if (!isEmpty(idx)) {
          this.triggersList.splice(idx, 1)
        }
        this.$message.success('Deleted trigger successfully')
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
    },
    async changeStatus(trigger) {
      try {
        await trigger.patch()
        this.$message.success(
          this.$t('common.wo_report.changed_trigger_status')
        )
      } catch (errorMsg) {
        this.$message.error(errorMsg)
        trigger.status = !trigger.status
      }
    },
    openSummary(trigger) {
      this.selectedTrigger = trigger
      this.showSummary = true
    },
  },
}
</script>
<style lang="scss">
.triggers-list {
  padding: 20px 30px;
  height: calc(100vh - 50px);

  .trigger-header {
    margin-bottom: 20px;
    display: flex;
    justify-content: space-between;
  }
  .trigger-name {
    font-size: 18px;
    color: #000000;
    letter-spacing: 0.7px;
    padding-bottom: 5px;
    text-transform: capitalize;
  }
  .trigger-desc {
    font-size: 13px;
    color: #808080;
    letter-spacing: 0.3px;
  }
  .actions-txt {
    font-size: 12px;
    letter-spacing: 0.5px;
    color: #6171db;
  }
}
</style>
