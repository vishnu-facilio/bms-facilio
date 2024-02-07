<template>
  <div class="width80">
    <div class="summaryHeader d-flex">
      <div class="primary-field summary-header-heading pT5">
        <template v-if="!$validation.isEmpty(schedule)">
          <span class="fc-id">#{{ schedule.id }}</span>
          <div class="heading-black18 f16 max-width500px textoverflow-ellipsis">
            {{ schedule.name }}
          </div>
        </template>
      </div>

      <div class="display-flex mL-auto mB10">
        <button
          type="button"
          @click="openEditRecord"
          class="d-flex el-button fc-wo-border-btn pL15 pR15 self-center el-button--button"
        >
          <span><i class="el-icon-edit" tabindex="0"></i></span>
        </button>
        <el-dropdown class="mL10 pointer" trigger="click">
          <button
            type="button"
            class="d-flex el-button fc-wo-border-btn pL15 pR15 self-center el-button--button"
          >
            <span> <img src="~assets/menu.svg" width="16" height="12"/></span>
          </button>
          <el-dropdown-menu>
            <div @click="deleteSchedule" class="p10 pointer">
              {{ $t('common._common.delete') }}
            </div>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <div v-if="isLoading" class="text-center width100 pT50 mT50">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <div
      v-else-if="$validation.isEmpty(currBusinessHour)"
      class="height100vh fc-empty-white flex-middle justify-content-center flex-direction-column"
    >
      <inline-svg
        src="svgs/emptystate/quotation"
        iconClass="icon text-center icon-xxxxlg"
      ></inline-svg>
      <div class="q-item-label nowo-label">
        {{ $t('quotation.list.no_data') }}
      </div>
    </div>
    <template v-else>
      <div class="m20 d-flex flex-column">
        <ScheduleVisualizer
          v-if="!$validation.isEmpty(currBusinessHour)"
          :schedule="currBusinessHour"
          :isSameTimingForAll="false"
          class="visualizer-container"
        />
      </div>
    </template>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import ScheduleVisualizer from './components/ScheduleVisualizer'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['moduleName'],
  components: { ScheduleVisualizer },
  data() {
    return {
      businessHoursList: null,
      schedule: null,
      isLoading: false,
      currBusinessHour: null,
    }
  },
  created() {
    this.getCurrRecord()
  },
  computed: {
    recordId() {
      let { id } = this.$route.params
      return id
    },
  },
  watch: {
    recordId() {
      this.getCurrRecord()
    },
  },
  methods: {
    async getCurrRecord() {
      this.isLoading = true
      let id = this.recordId
      let { controlSchedule, error } = await API.fetchRecord(
        'controlSchedule',
        { id }
      )
      if (error) this.$message.error(error.message || 'Error Occured')
      this.schedule = controlSchedule
      let {
        businessHoursContext: { singleDaybusinessHoursList },
      } = controlSchedule
      this.currBusinessHour = singleDaybusinessHoursList
      this.isLoading = false
    },
    openEditRecord() {
      let id = this.$getProperty(this, 'schedule.id', '')
      if (isWebTabsEnabled()) {
        let { moduleName } = this
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        let route = {
          name,
          params: { id },
        }

        this.$router.push(route)
      } else {
        this.$router.push({
          name: 'schedule-edit',
          params: { id },
        })
      }
    },
    async deleteSchedule() {
      let { id } = this.schedule
      let value = await this.$dialog.confirm({
        title: this.$t(`controls.controlSchedule.delete_schedule`),
        message: this.$t(
          `controls.controlSchedule.delete_schedule_confirmation`
        ),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })
      if (value) {
        let { error } = await API.deleteRecord(this.moduleName, [id])
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$router.push({ name: 'schedule-list' })
        }
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.summaryHeader {
  min-height: 80px;
  padding: 15px 20px 10px 20px;
  background: #fff;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}
.visualizer-container {
  height: calc(100vh - 165px);
  flex-grow: 1;
}
</style>
