import { tabTypes } from '@facilio/router'

export default [
  {
    tabType: tabTypes.DISPATCHER_CONSOLE,
    component: () =>
      import(
        'src/beta/pages/dispatcher/dispatcher-console/DispatcherConsoleBoard.vue'
      ),
  },
]
