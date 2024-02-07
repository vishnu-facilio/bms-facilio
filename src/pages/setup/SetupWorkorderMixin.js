import { getApp } from '@facilio/router'

export default {
  computed: {
    subheaderMenu() {
      let appName = getApp().linkName
      appName = appName === 'newapp' ? 'app' : appName
      return [
        {
          label: 'Category',
          path: {
            path: `/${appName}/setup/workordersettings/category`,
          },
        },
        {
          label: 'Priority',
          path: {
            path: `/${appName}/setup/workordersettings/priority`,
          },
        },
        {
          label: 'Types',
          path: {
            path: `/${appName}/setup/workordersettings/types`,
          },
        },
      ]
    },
  },
}
