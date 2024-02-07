import { mapGetters } from 'vuex'
import { datadogRum } from '@datadog/browser-rum'

export const mapGettersWithLogging = getters => {
  // if datadog is unavailable fallback to vuex `mapGetters` fn
  if (!window.dataDogClientId) {
    return mapGetters(getters)
  }
  // now the custom errors can be tracked in rum error dashboard
  const mappedGetters = mapGetters(getters)
  const loggingGetters = {}
  for (const key in mappedGetters) {
    const originalGetter = mappedGetters[key]
    loggingGetters[key] = function() {
      datadogRum.addError(
        new Error(
          `Getting getter '${key}' from '${this.$options.name}' component.`
        ),
        {
          key,
          componentName: this.$options.name,
        }
      )
      return originalGetter.call(this)
    }
  }
  return loggingGetters
}
