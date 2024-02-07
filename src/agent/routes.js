import { pageTypes, tabTypes } from '@facilio/router'

export default {
  pageType: pageTypes.APP_HOME,
  appName: 'iot',
  layoutType: 1,
  component: () => import('agent/App'),
  children: [
    {
      tabType: tabTypes.CUSTOM,
      config: {
        type: 'overview',
      },
      name: 'overview',
      component: () => import('agent/Overview'),
      children: [
        {
          path: ':childRoute',
          component: () => import('agent/AgentIntegration'),
        },
      ],
    },
    {
      tabType: tabTypes.CUSTOM,
      config: {
        type: 'agents',
      },
      component: () => import('agent/Agents'),
      children: [
        {
          path: `:viewname/:id/overview`,
          component: () => import('agent/agentOverview'),
        },
      ],
    },
    {
      tabType: tabTypes.CUSTOM,
      config: {
        type: 'points',
      },
      component: () => import('agent/AgentPoints'),
    },

    {
      tabType: tabTypes.CUSTOM,
      config: {
        type: 'device',
      },
      component: () => import('agent/AgentDevice'),
    },

    {
      tabType: tabTypes.CUSTOM,
      config: {
        type: 'commissioning',
      },
      component: () => import('agent/AgentCommissioning'),
      children: [
        {
          path: ':logId/edit',
          component: () => import('pages/newcoms/CommissioningSheet'),
        },
      ],
    },
    {
      tabType: tabTypes.CUSTOM,
      config: {
        type: 'alarm_mapping',
      },
      component: () => import('agent/AlarmMapping'),
    },
    {
      tabType: tabTypes.CUSTOM,
      config: {
        type: 'alarmrule',
      },
      component: () => import('agent/Agentnotify'),
      meta: {
        tabType: 'MODULE',
        moduleName: 'rule',
      },
    },

    {
      tabType: tabTypes.CUSTOM,
      config: {
        type: 'metrics',
      },
      component: () => import('agent/AgentMetrics'),
    },

    {
      tabType: tabTypes.CUSTOM,
      config: {
        type: 'log',
      },
      component: () => import('agent/AgentAuditLog'),
    },
    {
      tabType: tabTypes.CUSTOM,
      config: {
        type: 'logs',
      },
      name: 'logs',
      component: () => import('agent/logs/DataLogs'),
      children: [
        {
          path: 'summary/:parentId',
          name: 'logsTableSummary',
          component: () => import('agent/logs/LogsTableSummary'),
        },
      ],
    },
    {
      tabType: tabTypes.CUSTOM,
      config: {
        type: 'agent_data',
      },
      component: () => import('agent/AgentData'),
    },
    {
      tabType: tabTypes.CUSTOM,
      config: {
        type: 'trigger',
      },
      component: () => import('agent/AgentTrigger/AgentTrigger'),
    },

    {
      pageType: pageTypes.LIST,
      moduleName: 'agentAlarm',
      component: () => import('agent/Alarm/AgentAlarmList'),
    },

    {
      pageType: pageTypes.OVERVIEW,
      moduleName: 'agentAlarm',
      component: () => import('agent/Alarm/AgentAlarmList'),
      children: [
        {
          path: '',
          component: () => import('agent/Alarm/AgentAlarmSummary'),
        },
      ],
    },
    {
      pageType: pageTypes.LIST,
      moduleName: 'controller',
      component: () => import('agent/v1/AgentController'),
    },
  ],
}
