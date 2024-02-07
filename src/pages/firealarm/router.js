export default {
  name: 'fa',
  path: 'fa',
  component: () => import('pages/firealarm/Layout'),
  children: [
    {
      path: 'alarms',
      component: () => import('pages/firealarm/alarms/alarmSwitch/Layout'),
      children: [
        {
          path: ':viewname',
          component: () =>
            import('pages/firealarm/alarms/alarmSwitch/AlarmList'),
          children: [
            {
              path: 'overview/:id',
              components: {
                summary: () =>
                  import('pages/firealarm/alarms/v1/AlarmOverview'),
              },
            },
            {
              path: 'summary/:id',
              components: {
                summary: () => import('pages/firealarm/alarms/v1/AlarmSummary'),
              },
            },
            {
              path: 'newsummary/:id',
              components: {
                summary: () =>
                  import('pages/firealarm/alarms/alarmSwitch/AlarmSummary'),
              },
            },
          ],
        },
        {
          path: 'summary/:id',
          component: () => import('pages/firealarm/alarms/NewAlarmSummary'),
        },
      ],
    },

    {
      path: 'sensoralarms/:viewname?',
      name: 'sensorrollupalarm-list',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'sensorrollupalarm',
      }),
      component: () =>
        import('pages/firealarm/alarms/alarms/v3/SensorAlarmList'),
      children: [
        {
          path: 'overview/:id',
          components: {
            summary: () => import('pages/firealarm/alarms/v1/AlarmOverview'),
          },
        },
        {
          path: 'summary/:id',
          components: {
            summary: () => import('pages/firealarm/alarms/v1/AlarmSummary'),
          },
        },
        {
          path: 'newsummary/:id',
          name: 'sensorrollupalarm-summary',
          component: () =>
            import('pages/firealarm/alarms/alarms/v3/SensorAlarmSummary.vue'),
          props: route => ({
            viewname: route.params.viewname,
            id: route.params.id,
            moduleName: 'sensorrollupalarm',
          }),
        },
      ],
    },
    {
      name: 'sensoralarm-viewmanager',
      path: 'sensoralarms/:moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'sensoralarm-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      path: 'faults/:viewname?',
      name: 'newreadingalarm-list',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'newreadingalarm',
      }),
      component: () =>
        import('src/pages/firealarm/alarms/alarms/v3/NewFaultList.vue'),
      children: [
        {
          name: 'newreadingalarm-summary',
          path: 'newsummary/:id',
          component: () =>
            import('src/pages/firealarm/alarms/alarms/v3/FaultsSummary.vue'),
          props: route => ({
            viewname: route.params.viewname,
            id: route.params.id,
            moduleName: 'newreadingalarm',
          }),
        },
      ],
    },
    {
      name: 'faults-viewmanager',
      path: 'faults/:moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      path: 'bmsalarms/:viewname?',
      name: 'bmsalarm-list',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'bmsalarm',
      }),
      component: () =>
        import('src/pages/firealarm/alarms/alarms/v3/BmsAlarmsList.vue'),
      children: [
        {
          path: 'summary/:id',
          name: 'bmsalarm-summary',
          component: () =>
            import('src/pages/firealarm/alarms/alarms/v3/BmsAlarmSummary.vue'),

          props: route => ({
            viewname: route.params.viewname,
            id: route.params.id,
            moduleName: 'bmsalarm',
          }),
        },
      ],
    },
    {
      name: 'bmsalarms-viewmanager',
      path: 'bmsalarms/:moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'newrules',
      path: 'newrules/:viewname?',
      component: () => import('pages/firealarm/rules/NewRulesList'),
      props: route => ({ viewname: route.params.viewname }),
      children: [
        {
          name: 'newRulesSummary',
          path: ':id/summary',
          component: () => import('pages/firealarm/rules/NewRulesSummary'),
          props: route => ({ viewname: route.params.viewname }),
        },
      ],
    },
    {
      name: 'newrules-viewmanager',
      path: 'newrules/:moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      path: 'schedule',
      component: () =>
        import('pages/workorder/workorders/v1/woViewScheduleList'),
      meta: {
        module: 'newreadingalarm',
      },
    },
    {
      path: 'v2alarms',
      component: () => import('pages/firealarm/alarms/v2/Layout'),
      children: [
        {
          path: ':viewname',
          component: () => import('pages/firealarm/alarms/v2/AlarmList'),
          children: [
            {
              path: 'summary/:id',
              components: {
                summary: () => import('pages/firealarm/alarms/v2/AlarmSummary'),
              },
            },
            {
              path: 'newsummary/:id',
              components: {
                summary: () =>
                  import('pages/firealarm/alarms/v1/NewAlarmSummary'),
              },
            },
          ],
        },
        {
          path: 'summary/:id',
          component: () => import('pages/firealarm/alarms/NewAlarmSummary'),
        },
      ],
    },
    {
      path: 'newalarms',
      component: () => import('pages/firealarm/alarms/alarms/v1/layout'),
      children: [
        {
          path: ':viewname',
          component: () =>
            import('pages/firealarm/alarms/alarms/v1/NewAlarmList'),
          children: [
            {
              path: 'summary/:id',
              components: {
                summary: () => import('pages/firealarm/alarms/v1/AlarmSummary'),
              },
            },
            {
              path: 'newsummary/:id',
              components: {
                summary: () =>
                  import('pages/firealarm/alarms/v1/NewAlarmSummary'),
              },
            },
          ],
        },
        {
          path: 'summary/:id',
          component: () => import('pages/firealarm/alarms/NewAlarmSummary'),
        },
      ],
    },
    {
      path: 'events',
      component: () => import('event/pages/firealarm/Layout'),
      children: [
        {
          path: ':viewname',
          component: () => import('event/pages/firealarm/Events'),
          children: [
            {
              path: 'summary/:id',
              components: {
                summary: () => import('event/pages/firealarm/EventsSummary'),
              },
            },
          ],
        },
      ],
    },
    {
      path: 'rules/newtemplates',
      component: () => import('pages/firealarm/rules/AlarmRulesTemplate'),
      children: [
        {
          path: ':id/templatesummary',
          component: () =>
            import('pages/firealarm/rules/AlarmRulesTemplateSummary'),
        },
      ],
    },
    {
      path: 'rules/templates',
      component: () =>
        import('pages/firealarm/rules/v1/AlarmRulesTemplateSummary'),
    },
    // {
    //   path: 'rules/templates/:id/summaryNew',
    //   component: () =>
    //     import('pages/firealarm/rules/AlarmRulesTemplateSummary'),
    // },
    {
      path: 'rules/templates/:id/summary',
      component: () => import('pages/firealarm/rules/v1/TemplateSummaryNew'),
    },
    {
      path: 'rules',
      name: 'rules.list',
      component: () => import('pages/firealarm/rules/Layout'),
      redirect: 'rules/all',
      children: [
        {
          path: ':viewname',
          component: () => import('pages/firealarm/rules/Rules'),
          children: [
            {
              name: 'rulesummary',
              path: 'summary/:id',
              components: {
                summary: () => import('pages/firealarm/rules/RulesSummary'),
              },
            },
            {
              name: 'ruleOverview',
              path: ':id/newsummary',
              components: {
                summary: () => import('pages/firealarm/rules/RuleOverview'),
              },
            },
          ],
        },
      ],
    },
    {
      path: 'anomalies',
      component: () => import('pages/firealarm/anomalies/Layout'),
      children: [
        {
          path: ':viewname',
          component: () => import('pages/firealarm/anomalies/Anomalies'),
          children: [
            {
              path: ':id/summary',
              components: {
                summary: () =>
                  import('pages/firealarm/anomalies/AnomalyOverview'),
              },
            },
          ],
        },
      ],
    },
    {
      name: 'rule-creation-new',
      path: 'newrule/new',
      props: true,
      component: () =>
        import('pages/alarm/rule-creation/AlarmRuleCreation.vue'),
    },
    {
      name: 'new-rule-edit',
      path: 'newrule/:id/edit',
      props: true,
      component: () =>
        import('pages/alarm/rule-creation/AlarmRuleCreation.vue'),
    },
    {
      name: 'rule-creation-news',
      path: 'rule/new',
      props: true,
      component: () => import('pages/alarm/rule/RuleConfiguration.vue'),
    },
    {
      name: 'rule-creation-edit',
      path: 'rule/edit/:id',
      props: true,
      component: () => import('pages/alarm/rule/RuleConfiguration.vue'),
    },
    {
      name: 'template-rule-creation-edit',
      path: 'rule/edit/template/:templateid',
      component: () => import('pages/alarm/rule/RuleConfiguration.vue'),
    },
    {
      name: 'operational-alarm',
      path: 'outofschedule',
      props: true,
      component: () => import('pages/firealarm/operational/Layout'),
      children: [
        {
          name: 'oosalarms',
          path: ':viewname',
          component: () =>
            import('pages/firealarm/operational/OperationalAlarmList'),
          children: [
            {
              path: 'summary/:id',
              components: {
                summary: () =>
                  import(
                    'pages/firealarm/operational/OperationalSummaryOverview'
                  ),
              },
            },
          ],
        },
      ],
    },
    {
      path: 'reports/new',
      component: () => import('pages/report/forms/NewReport'),
      meta: {
        module: 'alarm',
      },
    },
    {
      path: 'reports/edit/:reportid',
      component: () => import('pages/report/forms/NewReport'),
      meta: {
        module: 'alarm',
      },
    },
    {
      path: 'reports/newmatrix',
      component: () => import('pages/report/forms/NewMatrixReport'),
      meta: {
        module: 'alarm',
      },
    },
    {
      path: 'reports/newtabular',
      component: () => import('pages/report/forms/NewTabularReport'),
      meta: {
        module: 'alarm',
      },
    },
    {
      name: 'fareports',
      path: 'reports',
      component: () => import('pages/report/Layout'),
      meta: {
        module: 'alarm',
      },
      children: [
        {
          path: 'view/:reportid',
          component: () => import('pages/report/ReportSummary'),
        },
        {
          path: 'newview/:reportid',
          component: () => import('pages/report/ModuleNewReportSummary'),
        },
        // {
        //   path: 'scheduled',
        //   component: () => import('pages/report/ReportScheduledList'),
        //   meta: {
        //     module: 'alarm',
        //   },
        // },
        {
          path: 'foldermanager',
          component: () => import('pages/report/FolderManagerLayout.vue'),
          meta: { module: 'alarm' },
        },
      ],
    },
    {
      path: 'reports/view/:reportid/show',
      component: () => import('pages/report/ReportChart'),
      meta: {
        module: 'alarm',
      },
    },
  ],
}
