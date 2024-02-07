<template>
  <div class="height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">{{ $t('setup.setup.baseline') }}</div>
        <div class="heading-description">
          {{ $t('setup.list.list_baseline') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          class="setup-el-btn"
          @click="showDialog = true"
        >
          {{ $t('setup.add.add_baseline') }}
        </el-button>
        <new-baseline
          v-if="showDialog"
          :visibility.sync="showDialog"
          @saved="save"
        ></new-baseline>
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout mT30">
        <table class="setting-list-view-table overflow-scroll">
          <thead>
            <tr>
              <th class="setting-table-th setting-th-text">
                {{ $t('common.roles.name') }}
              </th>
              <th class="setting-table-th setting-th-text">
                {{ $t('alarm.alarm.space') }}
              </th>
              <th class="setting-table-th setting-th-text">
                {{ $t('mv.summary.date_range') }}
              </th>
            </tr>
          </thead>
          <tbody v-if="loading">
            <tr>
              <td colspan="100%" class="text-center">
                <spinner :show="loading" size="80"></spinner>
              </td>
            </tr>
          </tbody>
          <tbody v-else-if="!baselinelist.length">
            <tr>
              <td>{{ $t('setup.empty.empty_baseline') }}</td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr
              class="tablerow"
              v-for="(baseline, index) in baselinelist"
              :key="index"
              v-loading="loading"
            >
              <td>{{ baseline.name }}</td>
              <td>
                {{
                  baseline.spaceId === -1
                    ? '---'
                    : $store.getters.getSpace(baseline.spaceId).name
                }}
              </td>
              <td v-if="baseline.rangeType === 8">
                {{ $options.filters.formatDate(baseline.startTime, true) }}
              </td>
              <td v-else-if="baseline.rangeType === 9">
                {{
                  $options.filters.toDateFormat(
                    baseline.startTime,
                    'YYYY-wo '
                  ) + 'Week'
                }}
              </td>
              <td v-else-if="baseline.rangeType === 10">
                {{
                  $options.filters.toDateFormat(baseline.startTime, 'MMM YYYY')
                }}
              </td>
              <td v-else-if="baseline.rangeType === 11">
                {{ $options.filters.toDateFormat(baseline.startTime, 'YYYY') }}
              </td>
              <td v-else-if="baseline.rangeType === 12">
                {{
                  $options.filters.formatDate(baseline.startTime, true) +
                    ' - ' +
                    $options.filters.formatDate(baseline.endTime, true)
                }}
              </td>
              <td v-else>---</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
<script>
import NewBaseline from 'pages/setup/new/NewAddBaseline'
export default {
  title() {
    return 'Baseline'
  },
  components: {
    NewBaseline,
  },
  data() {
    return {
      loading: true,
      baselinelist: [],
      baseline: false,
      showDialog: false,
    }
  },
  mounted() {
    this.loadBaseline()
  },
  methods: {
    save() {
      this.loadBaseline()
    },
    loadBaseline() {
      let self = this
      self.$http.get('/baseline/all').then(function(response) {
        self.loading = false
        if (response.status === 200) {
          self.baselinelist = response.data ? response.data : []
        }
      })
    },
  },
}
</script>
