const customizationModuleRoutes = [
  {
    path: 'layouts',
    component: () => import('pages/setup/modules/FormsList.vue'),
  },
  {
    path: 'visitortypes',
    component: () => import('pages/setup/SelectVisitorTypeForms.vue'),
  },
  {
    path: 'layouts/:id/edit',
    component: () => import('pages/setup/modules/FormsEdit.vue'),
    props: true,
  },
  {
    path: 'fields',
    component: () => import('pages/setup/modules/FieldsList.vue'),
  },
  {
    path: 'extendedModules',
    component: () => import('pages/setup/modules/ExtendedModuleList.vue'),
  },
  {
    path: 'custombuttons',
    component: () => import('pages/setup/custombutton/CustomButtonList.vue'),
  },
  {
    path: 'relationship',
    component: () =>
      import('pages/setup/relationship/RelationshipModuleList.vue'),
  },
]

export default {
  path: '/:app/setup',
  component: () => import('pages/Home.vue'),
  children: [
    {
      name: 'setup',
      path: '',
      redirect: { path: 'home' },
    },
    {
      path: 'home',
      component: () => import('pages/setup/SetupHome.vue'),
    },
    {
      path: 'developerspace',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'apisetup',
          component: () => import('pages/setup/APISetupNew.vue'),
        },
      ],
    },
    {
      path: 'general',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'companyprofile',
          component: () => import('pages/setup/CompanyProfile.vue'),
        },
        {
          path: 'organizationsettings',
          component: () =>
            import(
              'src/pages/setup/organizationSetting/OrganizationSettings.vue'
            ),
        },
        {
          path: 'visitorSettings',
          component: () => import('pages/setup/VisitorSettings.vue'),
        },
        {
          path: 'feedbacksettings',
          component: () => import('pages/setup/FeedbackSettings.vue'),
        },
        {
          path: 'smartcontrolsettings',
          component: () => import('pages/setup/SmartControlsSettings.vue'),
        },
        {
          name: 'visitorTypeSettings',
          path: 'visitortype/:visitorTypeId',
          component: () => import('pages/setup/VisitorTypeLevelSettings.vue'),
        },
        {
          path: 'vendorkiosk',
          component: () => import('pages/setup/CustomKiosk.vue'),
        },
        {
          name: 'catalog-setup-list',
          path: 'catalogs',
          component: () => import('pages/setup/catalogs/CatalogSetupList.vue'),
        },
        {
          path: 'defaultunits',
          component: () => import('pages/setup/DefaultUnits.vue'),
        },
        {
          path: 'shifts',
          component: () => import('pages/setup/Shifts.vue'),
        },
        {
          path: 'tax',
          component: () => import('src/pages/setup/tax/TaxList.vue'),
        },
        {
          path: 'portal/summary/:id',
          name: 'applicationsummary',
          props: true,
          component: () => import('pages/setup/portal/PortalSummaryLayout'),
        },
        {
          path: 'portal',
          component: () => import('pages/setup/portal/PortalSetupListView.vue'),
        },
        {
          path: 'locations',
          component: () => import('pages/setup/Locations.vue'),
          children: [
            {
              path: 'new',
              component: () => import('pages/setup/NewLocation.vue'),
            },
          ],
        },
      ],
    },
    {
      path: 'resources',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'skills',
          component: () => import('pages/setup/Skills.vue'),
        },
        {
          path: 'requesters',
          component: () => import('pages/setup/Requesters.vue'),
        },
        {
          path: 'pendingrequesters',
          component: () => import('pages/setup/PendingRequesters.vue'),
        },
        {
          path: 'labour',
          component: () => import('pages/setup/Labour/Labour.vue'),
        },
        {
          path: 'crafts',
          component: () => import('pages/setup/crafts/Crafts.vue'),
        },
        {
          name: 'setup.people',
          path: 'people',
          component: () => import('pages/setup/people/PeopleList.vue'),
        },
        {
          name: 'peopleSummary',
          path: 'people/:id/summary/',
          component: () => import('pages/setup/people/PeopleSummary'),
        },
      ],
    },
    {
      path: 'resources',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'teams',
          component: () => import('pages/setup/teams/TeamList.vue'),
        },
        {
          path: 'skills',
          component: () => import('pages/setup/Skills.vue'),
        },
        {
          path: 'requesters',
          component: () => import('pages/setup/Requesters.vue'),
        },
        {
          path: 'pendingrequesters',
          component: () => import('pages/setup/PendingRequesters.vue'),
        },
        {
          path: 'labour',
          component: () => import('pages/setup/Labour/Labour.vue'),
        },
        {
          path: 'crafts',
          component: () => import('pages/setup/crafts/Crafts.vue'),
        },
        {
          name: 'setup.people',
          path: 'people',
          component: () => import('pages/setup/people/PeopleList.vue'),
        },
        {
          name: 'peopleSummary',
          path: 'people/:id/summary/',
          component: () => import('pages/setup/people/PeopleSummary'),
        },
      ],
    },
    {
      path: 'security',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          name: 'users',
          path: 'users',
          props: true,
          component: () => import('pages/setup/users/NewUserList'),
        },
        {
          name: 'pendingusers',
          path: 'pendingusers',
          component: () => import('pages/setup/users/NewUserList'),
        },
        {
          name: 'userSummary',
          path: 'users/:id/summary/:appId',
          component: () => import('pages/setup/users/UserSummary'),
        },
        {
          path: 'roles',
          component: () => import('pages/setup/roles/Roles.vue'),
        },
        {
          path: 'sso',
          component: () => import('pages/setup/AccountSSO.vue'),
        },
        {
          path: 'securitypolicy',
          component: () =>
            import('pages/setup/securitypolicy/SecurityPolicyList'),
        },
        {
          path: 'globalscope',
          component: () =>
            import('pages/setup/globalscope/GlobalScopeVariable'),
        },
        {
          name: 'globalscopelist',
          path: 'globalscope/:appId/list',
          component: () =>
            import('pages/setup/globalscope/GlobalScopeVariable'),
        },
        {
          name: 'globalscopenew',
          path: 'globalscope/:appId/new',
          props: true,
          component: () =>
            import('pages/setup/globalscope/GlobalScopeVariableForm.vue'),
        },
        {
          name: 'globalscopeedit',
          path: 'globalscope/:appId/edit/:id',
          props: true,
          component: () =>
            import('pages/setup/globalscope/GlobalScopeVariableForm.vue'),
        },
      ],
    },
    {
      path: 'workordersettings',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          name: 'wo-new-email-settings',
          path: 'newemailsettings',
          component: () =>
            import('pages/setup/emailSettings/EmailSettingsPage.vue'),
        },
        {
          path: 'emailsettings',
          component: () => import('pages/setup/WorkorderEmailSettings.vue'),
        },
        {
          path: 'category',
          component: () => import('pages/setup/WorkorderCategory.vue'),
        },
        {
          path: 'priority',
          alias: 'newPriority',
          component: () => import('pages/setup/NewWorkOrderPriorityList.vue'),
        },
        {
          path: 'types',
          component: () => import('pages/setup/WorkorderTypes.vue'),
        },
        {
          path: 'survey',
          name: 'Survey',
          component: () => import('pages/setup/AutomationsHome'),
          children: [
            {
              path: '',
              redirect: 'workorder/template',
            },
            {
              path: ':/moduleName',
              redirect: 'workorder/template',
            },
            {
              name: 'survey.list',
              path: ':moduleName/list',
              props: true,
              component: () => import('pages/setup/survey/SurveySetup'),
            },
            {
              name: 'newSurvey',
              path: ':moduleName/new',
              props: true,
              component: () => import('pages/setup/survey/NewSurvey'),
            },

            {
              name: 'editSurvey',
              path: ':moduleName/:id/edit',
              props: true,
              component: () => import('pages/setup/survey/NewSurvey'),
            },
            {
              name: 'survey.template',
              path: ':moduleName/template',
              component: () => import('pages/setup/survey/SurveySetup'),
            },
            {
              name: 'newSurveyQandABuilder',
              path: ':moduleName/template/:id/builder',
              props: true,
              component: () =>
                import('pages/setup/survey/SurveyQAndABuilder.vue'),
            },
          ],
        },
      ],
    },
    {
      //this section will be removed in June 2021
      path: 'alarmsettings',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          name: 'alarmactions',
          path: 'alarmactions',
          component: () => import('pages/setup/AlarmActions.vue'),
        },
        {
          path: 'alarmrules',
          component: () => import('pages/setup/ThresholdRules.vue'),
        },
        {
          path: 'newalarmrules',
          component: () => import('pages/setup/new/NewAlarmRules'),
        },
        {
          path: 'notifications',
          component: () => import('pages/setup/FireAlarmNotifications.vue'),
        },
      ],
    },
    {
      name: 'space-categories-new',
      path: 'spacecategoriesnew',
      component: () => import('pages/setup/NewSpaceCategory.vue'),
    },
    {
      name: 'space-categories-edit',
      path: 'spacecategory/edit/:id',
      props: route => ({
        id: route.params.id,
        moduleName: 'spacecategory',
      }),
      component: () => import('pages/setup/NewSpaceCategory.vue'),
    },
    {
      path: 'assetsettings',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'category',
          component: () => import('pages/setup/AssetCategory.vue'),
        },
        {
          path: 'department',
          component: () => import('pages/setup/AssetDepartment.vue'),
        },
        {
          path: 'types',
          component: () => import('pages/setup/AssetTypes.vue'),
        },
        {
          path: 'readings',
          component: () => import('pages/setup/v1/Readings.vue'),
        },
        {
          path: 'sitereadings',
          component: () => import('pages/setup/v1/SiteReadings.vue'),
        },
        {
          path: 'buildingreadings',
          component: () => import('pages/setup/v1/BuildingReadings.vue'),
        },
        {
          path: 'floorreadings',
          component: () => import('pages/setup/v1/FloorReadings.vue'),
        },
        {
          path: 'spacereadings',
          component: () => import('pages/setup/v1/SpaceReadings.vue'),
        },
        {
          path: 'weatherreadings',
          component: () => import('pages/setup/v1/WeatherReadings.vue'),
        },
        {
          path: 'depreciation',
          component: () =>
            import('pages/setup/depreciation/AssetDepreciation.vue'),
        },
        {
          name: 'space-categories',
          path: 'spacecategory',
          component: () => import('pages/setup/SpaceCategories.vue'),
        },

        {
          path: 'fieldMigration',
          component: () => import('pages/setup/FieldMigration.vue'),
        },
        {
          name: 'space-operating-hours',
          path: 'operatinghours',
          component: () => import('pages/setup/OperatingHours.vue'),
        },
        {
          path: 'readings',
          component: () => import('pages/setup/SpaceReadings.vue'),
        },
        {
          path: 'thresholdrules',
          component: () => import('pages/setup/SpaceThresholdRules.vue'),
        },
        {
          path: 'thresholdrules/new',
          component: () => import('pages/setup/new/NewSpaceThresholdRule.vue'),
        },
        {
          path: 'weatherstation',
          name: 'weatherstation',
          component: () =>
            import('pages/setup/weather/WeatherStationSetup.vue'),
          children: [
            {
              path: '',
              redirect: 'service',
            },
            {
              path: 'list',
              name: 'weatherstation.list',
              component: () =>
                import('pages/setup/weather/WeatherStationSetup.vue'),
            },
            {
              path: 'service',
              name: 'weatherservice.list',
              component: () =>
                import('pages/setup/weather/WeatherStationSetup.vue'),
            },
          ],
        },
      ],
    },
    {
      path: 'agents',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'list',
          component: () => import('pages/setup/agents/AgentList.vue'),
        },
        {
          path: 'controllers',
          component: () => import('pages/setup/agents/ControllersList.vue'),
        },
        {
          path: 'logs/:agentId?',
          component: () => import('pages/setup/agents/AgentLogs.vue'),
        },
        {
          path: 'addcontroller',
          component: () => import('pages/setup/Add-Controller.vue'),
        },
        {
          path: 'sourceresourcemapping',
          component: () => import('pages/setup/Node-AssetMapping.vue'),
        },
        {
          path: 'oldcommissioning/:agentId?/:controllerId?',
          component: () =>
            import('pages/setup/commissioning/CommissioningList.vue'),
        },
        {
          path: 'commissioning/:agentId?/:controllerId?',
          component: () =>
            import('pages/setup/commissioning/Commissioning.vue'),
        },
        {
          path: 'config/:agentId?/:controllerId?',
          component: () => import('pages/setup/AgentConfig.vue'),
        },
        {
          path: 'integrations',
          component: () => import('pages/setup/agents/AgentIntegrations.vue'),
        },
      ],
    },
    {
      path: 'energyanalytics',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'energymeters',
          component: () => import('pages/setup/v1/EnergyMeter.vue'),
        },
        {
          path: 'baseline',
          component: () => import('pages/setup/Baseline.vue'),
        },
        {
          path: 'enpi',
          component: () => import('pages/setup/EnPI.vue'),
        },
        {
          path: 'mlsettings',
          component: () => import('pages/setup/MLScatterPLot.vue'),
        },
        {
          name: 'impacttemplates',
          path: 'impacttemplates',
          component: () => import('pages/setup/ImpactTemplates.vue'),
        },
        {
          path: 'kpi',
          name: 'reading-kpi-templates-list',
          component: () => import('pages/setup/readingkpi/ReadingKpiSetup.vue'),
          children: [
            {
              path: '',
              redirect: 'list',
            },
            {
              path: 'list',
              name: 'readingkpi.list',
              component: () =>
                import('src/pages/setup/readingkpi/KPITemplatesList.vue'),
            },
            {
              path: 'logs',
              name: 'readingkpi.logs',
              props: true,
              component: () =>
                import('src/pages/setup/readingkpi/KPIHistoryLogs.vue'),
            },
          ],
        },
        {
          path: 'kpi/createnew',
          name: 'create-new-kpi',
          component: () => import('pages/setup/readingkpi/CreateKpi.vue'),
        },
        {
          name: 'edit-kpi',
          path: 'kpi/:id/edit',
          props: true,
          component: () => import('pages/setup/readingkpi/CreateKpi.vue'),
        },
      ],
    },
    {
      path: 'newenergyanalytics',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'energymeters',
          component: () => import('pages/setup/v1/EnergyMeter.vue'),
        },
      ],
    },
    {
      path: 'bim',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'bimintegration',
          component: () => import('pages/bim/BimIntegration.vue'),
        },
        {
          path: 'bimFiles',
          component: () => import('pages/bim/BimFiles.vue'),
        },
      ],
    },
    {
      path: 'customization',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'modules',
          name: 'module-list',
          component: () => import('pages/setup/modules/ModuleList.vue'),
        },
        {
          name: 'workpermit-details',
          path: 'modules/workpermit',
          props: { moduleName: 'workpermit' },
          component: () =>
            import(
              'pages/setup/modules/workpermit/WorkpermitModuleDetails.vue'
            ),
          children: [
            ...customizationModuleRoutes,
            {
              path: 'checklist',
              component: () =>
                import(
                  'pages/setup/modules/workpermit/PermitTypeChecklist.vue'
                ),
            },
          ],
        },
        {
          name: 'newreadingalarm-details',
          path: 'modules/newreadingalarm',
          props: { moduleName: 'newreadingalarm' },
          component: () =>
            import(
              'pages/setup/modules/newreadingalarm/FaultModuleDetails.vue'
            ),
          children: [
            ...customizationModuleRoutes,
            {
              path: 'severity',
              props: { module: 'alarmSeverity' },
              component: () => import('@/NewFCustomModuleType.vue'),
            },
          ],
        },
        {
          path: 'modules/:moduleName',
          name: 'modules-details',
          props: true,
          component: () => import('pages/setup/modules/ModuleDetails.vue'),
          children: [
            // Modules Default Customizations ↓↓↓
            ...customizationModuleRoutes,
          ],
        },
        {
          name: 'webtabs',
          path: 'webtabs/:appId?',
          component: () =>
            import('pages/setup/customization/WebTabOverview.vue'),
        },
        {
          path: 'translation',
          redirect: 'translation/list',
        },
        {
          name: 'translation-list',
          path: 'translation/list',
          component: () =>
            import('pages/setup/translation/TranslationList.vue'),
        },
        {
          name: 'translation-details',
          path: 'translation/:language/:appId?',
          component: () =>
            import('pages/setup/translation/TranslationDetails.vue'),
        },
        {
          name: 'app-list',
          path: 'apps',
          component: () => import('pages/setup/apps/AppsList.vue'),
        },
        {
          name: 'app-details',
          path: 'apps/summary/:appId',
          component: () => import('pages/setup/apps/AppDetails.vue'),
        },
        {
          path: 'alarmnotifications',
          component: () => import('pages/setup/FireAlarmNotifications.vue'),
        },
        {
          path: 'connectedapps',
          name: 'connectedapp-list',
          component: () => import('pages/setup/ConnectedAppsList.vue'),
        },
        {
          path: 'connectedapps',
          name: 'connectedapps-details',
          component: () =>
            import('pages/setup/connectedapps/ConnectedAppDetails.vue'),
          children: [
            {
              name: 'connectedapp-layout',
              path: ':connectedAppId',
              component: () =>
                import('pages/setup/connectedapps/ConnectedAppLayout.vue'),
            },
          ],
        },
        {
          path: 'connectedapps',
          component: () =>
            import('pages/setup/connectedapps/ConnectedAppDetails.vue'),
          children: [
            {
              path: ':connectedAppName/:connectedAppId',
              component: () =>
                import('pages/setup/connectedapps/ConnectedAppLayout.vue'),
            },
          ],
        },
        {
          path: 'classifications',
          component: () =>
            import('pages/setup/classification/Classifications.vue'),
        },
        {
          path: 'connections',
          component: () => import('pages/setup/Connections.vue'),
        },
        {
          path: 'functions',
          component: () => import('pages/setup/Functions.vue'),
        },
        {
          path: ':namespaceId/:functionId/edit',
          name: 'functions.edit',
          component: () => import('pages/setup/new/FunctionsEditor.vue'),
        },
        {
          path: 'emailtemplates',
          component: () => import('pages/setup/AutomationsHome'),
          children: [
            {
              path: '',
              redirect: 'workorder/list',
            },
            {
              path: ':moduleName',
              redirect: ':moduleName/list',
            },

            {
              path: ':moduleName/list',
              name: 'emailtemplates.list',
              props: true,
              component: () =>
                import('pages/setup/EmailTemplates/EmailTemplatesList.vue'),
            },
            {
              path: ':moduleName/new',
              name: 'emailtemplates.new',
              props: true,
              component: () =>
                import('pages/setup/EmailTemplates/EmailTemplateEditor.vue'),
            },
            {
              path: ':moduleName/:id/edit',
              props: true,
              name: 'emailtemplates.edit',
              component: () =>
                import('pages/setup/EmailTemplates/EmailTemplateEditor.vue'),
            },
          ],
        },

        {
          path: 'userscopes/list',
          name: 'scopes.list',
          component: () => import('pages/setup/UserScopes/UserScopingList'),
        },
        {
          path: 'userscopes/new',
          name: 'scopes.new',
          props: true,
          component: () =>
            import('pages/setup/UserScopes/UserScopingAddOrEdit'),
        },
        {
          path: 'userscopes/:id/edit',
          props: true,
          name: 'scopes.edit',
          component: () =>
            import('pages/setup/UserScopes/UserScopingAddOrEdit'),
        },
      ],
    },
    {
      path: 'automations',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'scheduler',
          component: () => import('pages/setup/scheduler/Scheduler.vue'),
        },
        {
          path: 'workflows',
          component: () => import('pages/setup/AutomationsHome'),
          children: [
            {
              name: 'workflows.list',
              path: ':moduleName/list',
              props: true,
              component: () =>
                import('pages/setup/notificationAndWorkflows/ModuleActionList'),
              meta: {
                currentAction: 'WorkFlow',
              },
            },
          ],
        },
        {
          path: 'notifications',
          component: () => import('pages/setup/AutomationsHome'),
          children: [
            {
              name: 'notifications.list',
              path: ':moduleName/list',
              props: true,
              component: () =>
                import('pages/setup/notificationAndWorkflows/ModuleActionList'),
              meta: {
                currentAction: 'Notification',
              },
            },
          ],
        },
        {
          path: 'triggers',
          component: () => import('pages/setup/AutomationsHome'),
          children: [
            {
              path: '',
              redirect: 'workorder/list',
            },
            {
              path: ':moduleName',
              redirect: ':moduleName/list',
            },
            {
              name: 'triggers.list',
              path: ':moduleName/list',
              props: true,
              component: () => import('pages/setup/trigger/TriggersList'),
            },
          ],
        },
        {
          path: 'conditionmanager',
          component: () => import('pages/setup/AutomationsHome'),
          children: [
            {
              path: '',
              redirect: 'workorder/list',
            },
            {
              path: ':moduleName',
              redirect: ':moduleName/list',
            },
            {
              name: 'conditionmanager.list',
              path: ':moduleName/list',
              props: true,
              component: () =>
                import('pages/setup/conditionmanager/ConditionManagerList'),
            },
          ],
        },
        {
          path: 'variables',
          name: 'variables',
          component: () => import('pages/setup/variables/Variables'),
        },
        // {
        //   path: 'survey',
        //   name: 'Survey',
        //   component: () => import('pages/setup/AutomationsHome'),
        //   children: [
        //     {
        //       path: '',
        //       redirect: 'workorder/template',
        //     },
        //     {
        //       path: ':/moduleName',
        //       redirect: 'workorder/template',
        //     },
        //     {
        //       name: 'survey.list',
        //       path: ':moduleName/list',
        //       props: true,
        //       component: () => import('pages/setup/survey/SurveySetup'),
        //     },
        //     {
        //       name: 'newSurvey',
        //       path: ':moduleName/new',
        //       props: true,
        //       component: () => import('pages/setup/survey/NewSurvey'),
        //     },

        //     {
        //       name: 'editSurvey',
        //       path: ':moduleName/:id/edit',
        //       props: true,
        //       component: () => import('pages/setup/survey/NewSurvey'),
        //     },
        //     {
        //       name: 'survey.template',
        //       path: ':moduleName/template',
        //       component: () => import('pages/setup/survey/SurveySetup'),
        //     },
        //     {
        //       name: 'newSurveyQandABuilder',
        //       path: ':moduleName/template/:id/builder',
        //       props: true,
        //       component: () =>
        //         import('pages/setup/survey/SurveyQAndABuilder.vue'),
        //     },
        //   ],
        // },
      ],
    },
    {
      path: 'automationplus',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'slapolicies',
          component: () => import('pages/setup/SlaHome.vue'),
          children: [
            {
              path: '',
              redirect: 'workorder/list',
            },
            {
              path: ':moduleName',
              redirect: ':moduleName/list',
            },
            {
              // Remove when SLA is supported for all modules
              name: 'sla.default',
              path: ':moduleName',
              redirect: 'workorder/list',
            },
            {
              name: 'sla.list',
              path: ':moduleName/list',
              props: true,
              component: () => import('pages/setup/sla/SLAList.vue'),
            },
            {
              name: 'sla.new',
              path: ':moduleName/new',
              props: true,
              component: () => import('pages/setup/sla/NewSLA'),
            },
            {
              name: 'sla.edit',
              path: ':moduleName/:id/edit',
              props: true,
              component: () => import('pages/setup/sla/NewSLA'),
            },
            {
              name: 'sla.types',
              path: ':moduleName/types',
              props: true,
              component: () => import('pages/setup/sla/SLATypeList.vue'),
            },
          ],
        },
        {
          name: 'assignmentrules',
          path: 'assignmentrules',
          component: () =>
            import('pages/setup/assignmentRules/AssignmentRules.vue'),
        },
        {
          path: 'eventfilter',
          component: () => import('pages/setup/eventFilter/EventFilters.vue'),
        },
        {
          path: 'scoringrules',
          component: () => import('pages/setup/AutomationsHome'),
          children: [
            {
              path: '',
              redirect: 'workorder/list',
            },
            {
              path: ':moduleName',
              redirect: ':moduleName/list',
            },
            {
              name: 'scoringrules.list',
              path: ':moduleName/list',
              props: true,
              component: () =>
                import('pages/setup/scoringRules/ScoringRulesList'),
            },
            {
              name: 'scoringrules.new',
              path: ':moduleName/new',
              props: true,
              component: () =>
                import('pages/setup/scoringRules/NewScoringRule'),
            },
            {
              name: 'scoringrules.edit',
              path: ':moduleName/:id/edit',
              props: true,
              component: () =>
                import('pages/setup/scoringRules/NewScoringRule'),
            },
          ],
        },
        {
          path: 'transactionrules',
          component: () => import('pages/setup/AutomationsHome'),
          children: [
            {
              path: '',
              redirect: 'workorder',
            },
            {
              name: 'transaction.list',
              path: ':moduleName/:subModule?',
              props: true,
              component: () =>
                import('pages/setup/transactionRules/TransactionList'),
            },
          ],
        },
      ],
    },
    {
      path: 'process',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'approvals',
          component: () => import('pages/setup/AutomationsHome'),
          children: [
            {
              path: '',
              redirect: 'workorder/list',
            },
            {
              path: ':moduleName',
              redirect: ':moduleName/list',
            },
            {
              name: 'approvals.list',
              path: ':moduleName/list',
              props: true,
              component: () =>
                import('pages/setup/approvals/ApprovalRulesList.vue'),
            },
            {
              name: 'approvals.new',
              path: ':moduleName/new',
              props: true,
              component: () => import('pages/setup/approvals/NewApprovalRule'),
            },
            {
              name: 'approvals.edit',
              path: ':moduleName/:id/edit',
              props: true,
              component: () => import('pages/setup/approvals/NewApprovalRule'),
            },
          ],
        },
        {
          path: 'stateflows',
          component: () => import('pages/setup/AutomationsHome'),
          children: [
            {
              name: 'stateflow.list',
              path: ':moduleName/list',
              props: true,
              component: () =>
                import('pages/setup/stateflow/StateFlowList.vue'),
            },
            {
              name: 'stateflow.states',
              path: ':moduleName/states',
              props: true,
              component: () => import('pages/setup/stateflow/StateList.vue'),
            },
            {
              name: 'stateflow.transitions',
              path: ':moduleName/:stateFlowId/transitions',
              props: true,
              component: () =>
                import('pages/setup/stateflow/StateTransitionList.vue'),
            },
            {
              name: 'stateflow.edit',
              path: ':moduleName/:stateFlowId/edit',
              props: true,
              component: () => import('pages/stateflows/StateFlowBuilder.vue'),
            },
          ],
        },
      ],
    },
    {
      path: 'dataadministration',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'export',
          component: () => import('pages/setup/Export.vue'),
        },
      ],
    },
    {
      path: 'tenantbilling',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'excelupload',
          component: () => import('pages/setup/ExcelUpload.vue'),
        },
      ],
    },
    {
      path: 'tenantbilling',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'generatebill',
          component: () => import('pages/setup/BillGeneration.vue'),
        },
      ],
    },
    {
      path: 'tenantbilling',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'billtemplate',
          component: () => import('pages/setup/BillTemplate.vue'),
        },
        {
          path: 'ratecard',
          component: () => import('pages/setup/RateCard.vue'),
        },
        {
          path: 'tenant',
          component: () => import('pages/setup/Tenant.vue'),
        },
        {
          path: 'tenantsummary/:id',
          component: () => import('pages/setup/TenantSummary.vue'),
        },
      ],
    },
    {
      path: 'developer',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'bundle',
          component: () =>
            import('pages/setup/developer/DeveloperBundleList.vue'),
        },
        {
          name: 'developerSummary',
          path: 'bundle/:id/summary',
          component: () => import('pages/setup/developer/DeveloperSummary.vue'),
        },
        {
          name: 'developerPublish',
          path: 'bundle/:id/summary/publish',
          component: () =>
            import('pages/setup/developer/DeveloperPublishApplicationList.vue'),
        },
        {
          path: 'installBundle',
          component: () =>
            import('pages/setup/developer/InstallBundleList.vue'),
        },
      ],
    },
    {
      path: 'plans',
      component: () => import('pages/setup/Plans.vue'),
    },
    {
      path: 'logs',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'auditlog',
          component: () => import('pages/setup/AuditLog/AuditLog'),
        },
        {
          path: 'emaillogs',
          name: 'emailLogs',
          redirect: 'emaillogs/list',
        },
        {
          name: 'emailLogsList',
          path: 'emaillogs/list',
          component: () => import('pages/setup/emailLogs/EmailLogs'),
        },
        {
          name: 'emailLogsSummary',
          path: 'emaillogs/summary/:loggerId',
          props: true,
          component: () => import('pages/setup/emailLogs/EmailLogsSummary'),
        },
        {
          name: 'scriptlogs',
          path: 'scriptlogs',
          component: () => import('pages/setup/scriptlogs/ScriptLogs'),
        },
      ],
    },
    {
      path: 'workplacesettings',
      component: () => import('pages/setup/SetupHome.vue'),
      children: [
        {
          path: 'bookingpolicies',
          name: 'spaceBookingPolicy',
          redirect: 'spaceBookingPolicy/list',
        },
        {
          name: 'bookingpolicies',
          path: 'spaceBookingPolicy/list',
          component: () =>
            import('pages/setup/WorkplaceSettings/WorkplaceSettingsHome'),
        },
      ],
    },
  ],
}
