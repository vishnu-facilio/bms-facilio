import { mapState } from 'vuex'
import { datadogRum } from '@datadog/browser-rum'

export const mapStateWithLogging = states => {
  // if datadog is unavailable fallback to vuex `mapState` fn
  if (!window.dataDogClientId) {
    return mapState(states)
  }
  // now the custom errors can be tracked in rum error dashboard
  const mappedStates = mapState(states)
  const loggingStates = {}
  for (const key in mappedStates) {
    const originalGetter = mappedStates[key]
    loggingStates[key] = function() {
      datadogRum.addError(
        new Error(
          `Getting state '${key}' from '${this.$options.name}' component.`
        ),
        {
          key,
          componentName: this.$options.name,
        }
      )
      return originalGetter.call(this)
    }
  }
  return loggingStates
}
