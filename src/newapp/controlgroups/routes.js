import { tabTypes } from '@facilio/router'

export default [
  {
    tabType: tabTypes.CUSTOM,
    config: {
      type: 'points',
    },
    component: () => import('pages/controls/ControlPoints/Layout'),
    children: [
      {
        path: '',
        redirect: { name: 'controlpoints-home' },
      },
      {
        name: 'controlpoints-home',
        path: 'points/all',
        component: () =>
          import('src/pages/controls/ControlPoints/ControlPointsList.vue'),
      },
    ],
  },
  {
    tabType: tabTypes.CUSTOM,
    config: {
      type: 'commands',
    },
    component: () => import('pages/controls/Commands/Layout'),
    children: [
      {
        path: '',
        redirect: { name: 'controlcommands-home' },
      },
      {
        name: 'controlcommands-home',
        path: 'commands/all',
        component: () => import('src/pages/controls/Commands/CommandsList.vue'),
      },
    ],
  },
  {
    tabType: tabTypes.CUSTOM,
    config: {
      type: 'graphics',
    },
    component: () => import('pages/controls/Graphics/Graphics'),
    meta: { remember: true },
    children: [
      {
        path: 'view/:graphicsid',
        component: () => import('pages/controls/Graphics/GraphicsSummary'),
      },
    ],
  },
]
