import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

const state = {
  metricWithUnits: [],
  metrics: [],
  isMetricLoading: false,
}

const getters = {
  getMetrics: state => {
    let { metrics } = state
    let options = []
    if (!isEmpty(metrics)) {
      metrics.forEach(metric => {
        let { name: label, metricId: value } = metric
        options.push({
          label,
          value,
        })
      })
    }
    return options
  },
  getMetricsUnit: state => props => {
    let options = []
    let { metricId } = props
    let { metrics, metricWithUnits } = state
    let selectedMetric = metrics.find(metric => metric.metricId === metricId)
    let { _name } = selectedMetric || {}
    let selectedMetricUnits = metricWithUnits[_name] || []
    if (!isEmpty(selectedMetricUnits)) {
      selectedMetricUnits.forEach(metric => {
        let { displayName, unitId: value, symbol } = metric
        let label = `${displayName} (${symbol})`
        options.push({
          label,
          value,
        })
      })
    }
    return options
  },
}

const mutations = {
  UPDATE_METRICS(state, payload) {
    let { metrics } = payload
    state.metrics = metrics
  },
  UPDATE_METRICS_UNITS(state, payload) {
    let { metricWithUnits } = payload
    state.metricWithUnits = metricWithUnits
  },
  UPDATE_METRIC_LOADING(state, payload) {
    let { isMetricLoading } = payload
    state.isMetricLoading = isMetricLoading
  },
}

const actions = {
  async loadMetricUnits({ commit, state }, payload) {
    let { forceFetch = false } = payload || {}
    let { metrics, metricWithUnits } = state
    let canLoadMetrics =
      forceFetch || (isEmpty(metrics) && isEmpty(metricWithUnits))
    if (canLoadMetrics) {
      commit('UPDATE_METRIC_LOADING', { isMetricLoading: true })
      let url = `units/getDefaultMetricUnits`
      let { error, data } = await API.get(url)
      if (error) {
        let { message } = error || {}
        this.$message.error(message || 'Error Occured')
      } else {
        let { metricWithUnits = [], metrics = [] } = data || {}
        commit('UPDATE_METRICS', { metrics })
        commit('UPDATE_METRICS_UNITS', { metricWithUnits })
      }
      commit('UPDATE_METRIC_LOADING', { isMetricLoading: false })
    }
  },
}

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
}
