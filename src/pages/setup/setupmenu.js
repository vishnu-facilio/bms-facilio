import { i18n } from 'i18n'

export default [
  {
    label: i18n.t('setup.setup.general'),
    path: 'general',
    menu: [
      {
        label: i18n.t('setup.setup.companyprofile'),
        path: 'companyprofile',
        permission: 'setup:GENERAL',
        tabPermission: 'COMPANY_PROFILE',
        hide_if_license: ['MULTI_CURRENCY'],
      },
      {
        label: i18n.t('setup.setup.organizationsettings'),
        path: 'organizationsettings',
        permission: 'setup:GENERAL',
        feature_license: ['MULTI_CURRENCY'],
      },
      {
        label: i18n.t('setup.setup.portals'),
        path: 'portal',
        hide_if_license: ['ETISALAT'],
      },

      {
        label: i18n.t('setup.setup.visitorSettings'),
        path: 'visitorsettings',
        permission: 'setup:GENERAL',
        tabPermission: 'VISITOR_SETTINGS',
        feature_license: ['VISITOR'],
        hide_if_license: ['ETISALAT'],
      },
      {
        label: i18n.t('setup.setup.feedbackSettings'),
        path: 'feedbacksettings',
        permission: 'setup:GENERAL',
        tabPermission: '',
        hide_if_license: ['ETISALAT'],
      },
      {
        label: i18n.t('Vendor Kiosk'),
        path: 'vendorkiosk',
        permission: 'setup:GENERAL',
        tabPermission: '',
        feature_license: ['VENDOR_KIOSK'],
        hide_if_license: ['ETISALAT'],
      },
      {
        label: i18n.t('setup.setup.smartControls'),
        path: 'smartcontrolsettings',
        permission: 'setup:GENERAL',
        tabPermission: '',
        feature_license: ['GRAPHICS'],
        hide_if_license: ['ETISALAT'],
      },
      {
        label: i18n.t('servicecatalog.setup.service_catalogs'),
        path: 'catalogs',
        permission: 'setup:GENERAL',
        tabPermission: '',
        hide_if_license: ['ETISALAT'],
      },
      {
        label: i18n.t('quotation.common.tax'),
        path: 'tax',
        permission: 'setup:GENERAL',
        tabPermission: '',
        feature_license: ['QUOTATION', 'PURCHASE'],
        hide_if_license: ['ETISALAT', 'MULTI_CURRENCY'],
      },
    ],
  },
  {
    label: i18n.t('setup.setup.resources'),
    path: 'resources',
    menu: [
      {
        label: i18n.t('setup.users_management.people'),
        path: 'people',
        permission: 'setup:USER_MANAGEMENT',
        tabPermission: 'USERS',
      },
      {
        label: i18n.t('setup.setup.teams'),
        path: 'teams',
        permission: 'setup:USER_MANAGEMENT',
        tabPermission: 'TEAMS',
      },
      {
        label: 'Labor',
        path: 'labour',
        permission: 'setup:USER_MANAGEMENT',
        tabPermission: 'LABOUR',
        feature_license: ['RESOURCES'],
        hide_if_license: ['ETISALAT'],
      },
      {
        label: 'Crafts',
        path: 'crafts',
        permission: 'setup:USER_MANAGEMENT',
        tabPermission: 'LABOUR',
        feature_license: ['RESOURCES'],
        hide_if_license: ['ETISALAT'],
      },
    ],
  },
  {
    label: i18n.t('setup.setup.user_and_roles'),
    path: 'security',
    menu: [
      {
        label: i18n.t('setup.setup.users'),
        path: 'users',
        permission: 'setup:USER_MANAGEMENT',
        tabPermission: 'USERS',
      },
      {
        label: i18n.t('setup.setup.roles'),
        path: 'roles',
        permission: 'setup:USER_MANAGEMENT',
        tabPermission: 'ROLES',
      },
      {
        label: i18n.t('setup.setup.sso'),
        path: 'sso',
        permission: 'setup:SECURITY_SETTINGS',
        tabPermission: 'SETTINGS',
        hide_if_license: ['ETISALAT'],
      },
      {
        label: i18n.t('setup.setup.security_policy'),
        path: 'securitypolicy',
        permission: 'setup:SECURITY_SETTINGS',
        tabPermission: 'SETTINGS',
        hide_if_license: ['ETISALAT'],
      },
      {
        label: i18n.t('setup.setup.globalscopevariable'),
        path: 'globalscope',
        feature_license: ['SCOPE_VARIABLE'],
      },
    ],
  },
  {
    label: i18n.t('setup.setup.workordersettings'),
    path: 'workordersettings',
    menuName: 'MAINTENANCE',
    menu: [
      {
        label: i18n.t('setup.setup.emailsettings'),
        path: 'emailsettings',
        permission: 'setup:WORKORDER_SETTINGS',
        tabPermission: 'EMAIL_SETTINGS',
        hide_if_license: ['CUSTOM_MAIL', 'ETISALAT'],
      },
      {
        label: i18n.t('setup.setup.emailsettings'),
        path: 'newemailsettings',
        permission: 'setup:WORKORDER_SETTINGS',
        feature_license: ['CUSTOM_MAIL'],
        hide_if_license: ['ETISALAT'],
      },
      {
        label: 'Customization',
        path: 'category',
        permission: 'setup:WORKORDER_SETTINGS',
        tabPermission: 'WORKORDER_CUSTOMIZATIONS',
        hide_if_license: ['ETISALAT'],
      },
      {
        label: 'Survey',
        path: 'survey',
        permission: 'setup:WORKORDER_SETTINGS',
        tabPermission: 'WORKORDER_CUSTOMIZATIONS',
        feature_license: ['SURVEY'],
      },
    ],
  },
  {
    label: i18n.t('setup.setup_profile.space_asset_settings'),
    path: 'assetsettings',
    menuName: 'SPACE_ASSET',
    menu: [
      {
        label: i18n.t('setup.setup.readings'),
        path: 'readings',
        permission: 'setup:SPACE_SETTINGS',
        tabPermission: 'READINGS',
        hide_if_license: ['ETISALAT'],
      },
      {
        label: 'Customization',
        path: 'category',
        permission: 'setup:ASSET_SETTINGS',
        tabPermission: 'SPACE_ASSET_CUSTOMIZATIONS',
        hide_if_license: ['ETISALAT'],
      },
      {
        label: i18n.t('setup.setup.asset_depreciation'),
        path: 'depreciation',
        permission: 'setup:ASSET_SETTINGS',
        feature_license: ['ASSET_DEPRECIATION'],
        hide_if_license: ['ETISALAT'],
      },
      {
        label: i18n.t('setup.setup.spacecategories'),
        path: { name: 'space-categories' },
        permission: 'setup:SPACE_SETTINGS',
        tabPermission: 'SPACE_CATEGORIES',
        hide_if_license: ['ETISALAT'],
      },
      {
        label: i18n.t('setup.setup.operatinghours'),
        path: { name: 'space-operating-hours' },
        permission: 'setup:SPACE_SETTINGS',
        tabPermission: 'OPERATING_HOURS',
        hide_if_license: ['ETISALAT'],
      },
      {
        label: i18n.t('weather.weather_station'),
        path: 'weatherstation',
        feature_license: ['WEATHER_INTEGRATION'],
      },
    ],
  },
  {
    label: 'Automation',
    path: 'automations',
    menu: [
      {
        label: i18n.t('setup.setup.workflows'),
        path: {
          name: 'workflows.list',
        },
        permission: 'setup:GENERAL',
        tabPermission: 'WORKFLOWS',
        is_automation_tab: true,
      },
      {
        label: i18n.t('setup.setup.notifications'),
        path: {
          name: 'notifications.list',
        },
        permission: 'setup:GENERAL',
        tabPermission: 'NOTIFICATIONS',
        is_automation_tab: true,
      },
      /*{
        label: i18n.t('setup.setup.triggers'),
        path: { name: 'triggers.list' },
        permission: 'setup:GENERAL',
        is_automation_tab: true,
      },*/
      {
        label: i18n.t('setup.setup.condition_manager'),
        path: { name: 'conditionmanager.list' },
        permission: 'setup:GENERAL',
        is_automation_tab: true,
      },
      {
        label: i18n.t('setup.setup.scheduler'),
        path: 'scheduler',
        permission: 'setup:GENERAL',
        tabPermission: 'SCHEDULER',
        feature_license: ['DEVELOPER_SPACE'],
        is_automation_tab: true,
      },
      {
        label: 'Email Settings',
        path: { name: 'wo-new-email-settings' },
        permission: 'setup:GENERAL',
        feature_license: ['ETISALAT'],
        is_automation_tab: true,
      },
      {
        label: i18n.t('setup.setup.variables'),
        path: { name: 'variables' },
        permission: 'setup:GENERAL',
        is_automation_tab: true,
      },
    ],
  },
  {
    label: 'Automation Plus',
    path: 'automationplus',
    menu: [
      {
        label: i18n.t('setup.setup.sla_policies'),
        path: {
          name: 'sla.default',
        },
        permission: 'setup:GENERAL',
        tabPermission: 'SLA_POLICIES',
        is_automation_tab: true,
      },
      {
        label: i18n.t('setup.setup.assignmentrules'),
        path: 'assignmentrules',
        permission: 'setup:WORKORDER_SETTINGS',
        tabPermission: 'ASSIGNMENT_RULES',
        hide_if_license: ['ETISALAT'],
      },
      {
        label: i18n.t('setup.setup.bmseventfilter'),
        path: 'eventfilter',
        permission: 'setup:ALARM_SETTINGS',
        tabPermission: 'EVENT_FILTERING',
        hide_if_license: ['ETISALAT'],
      },
      /*{
        label: i18n.t('setup.setup.scoring_rules'),
        path: { name: 'scoringrules.list' },
        permission: 'setup:GENERAL',
        is_automation_tab: true,
      },*/
      {
        label: i18n.t('setup.setup.transaction_rules'),
        path: { name: 'transaction.list' },
        permission: 'setup:GENERAL',
        feature_license: ['BUDGET_MONITORING'],
        is_automation_tab: true,
      },
    ],
  },
  {
    label: 'Process',
    path: 'process',
    menu: [
      {
        label: i18n.t('setup.setup.stateflow'),
        path: {
          name: 'stateflow.list',
        },
        permission: 'setup:GENERAL',
        tabPermission: 'STATEFLOWS',
        is_automation_tab: true,
      },
      {
        label: i18n.t('setup.setup.approvals'),
        path: {
          name: 'approvals.list',
        },
        feature_license: ['NEW_APPROVALS'],
        permission: 'setup:GENERAL',
        tabPermission: 'APPROVALS',
        is_automation_tab: true,
        hide_if_license: ['ETISALAT'],
      },
    ],
  },
  {
    label: 'Customization',
    path: 'customization',
    menu: [
      {
        label: 'Modules',
        path: 'modules',
        permission: 'setup:ENERGY_ANALYTICS',
        tabPermission: 'MODULES',
      },
      {
        label: 'Tabs and Layouts',
        path: 'webtabs',
        show_if_org: [173, 174, 407, 418, 429, 526, 566, 656],
      },
      {
        label: 'Localization',
        path: 'translation',
        feature_license: ['MULTI_LANGUAGE_TRANSLATION'],
      },
      {
        label: 'Connected Apps',
        path: 'connectedapps',
        permission: 'setup:GENERAL',
        tabPermission: 'CONNECTED_APPS',
        feature_license: ['CONNECTEDAPPS'],
      },
      {
        label: i18n.t('setup.setup.connectors'),
        path: 'connections',
        permission: 'setup:GENERAL',
        tabPermission: 'CONNECTORS',
        feature_license: ['DEVELOPER_SPACE'],
      },
      {
        label: i18n.t('setup.setup.functions'),
        path: 'functions',
        permission: 'setup:GENERAL',
        tabPermission: 'FUNCTIONS',
      },
      {
        label: i18n.t('setup.setup.email_template'),
        path: 'emailtemplates',
        permission: 'setup:GENERAL',
      },
      // {
      //   label: i18n.t('setup.userScoping.user_scoping'),
      //   path: {
      //     name: 'scopes.list',
      //   },
      //   permission: 'setup:GENERAL',
      //   is_automation_tab: true,
      // },
      {
        label: i18n.t('setup.setup.classification'),
        path: 'classifications',
        feature_license: ['CLASSIFICATION'],
      },
    ],
  },
  {
    label: i18n.t('setup.setup.energyanalytics'),
    path: 'energyanalytics',
    menuName: 'ENERGY',
    menu: [
      {
        label: i18n.t('setup.setup.energymeters'),
        path: 'energymeters',
        permission: 'setup:ENERGY_ANALYTICS',
        tabPermission: 'ENERGY_METERS',
        hide_if_license: ['ETISALAT'],
      },
      {
        label: i18n.t('setup.setup.baseline'),
        path: 'baseline',
        permission: 'setup:ENERGY_ANALYTICS',
        tabPermission: 'BASELINE',
        hide_if_license: ['ETISALAT'],
      },
      {
        label: i18n.t('setup.setup.enpi'),
        path: 'enpi',
        permission: 'setup:ENERGY_ANALYTICS',
        tabPermission: '',
        hide_if_license: ['KPI', 'ETISALAT'],
      },
      {
        label: i18n.t('setup.setup.impacttemplates'),
        path: 'impacttemplates',
      },
      {
        label: i18n.t('setup.setup.kpi'),
        path: 'kpi',
        permission: 'setup:ENERGY_ANALYTICS',
        feature_license: ['NEW_KPI'],
      },
    ],
  },

  {
    label: i18n.t('setup.setup.agentConfiguration'),
    path: 'agents',
    menu: [
      {
        label: i18n.t('setup.setup.agents'),
        path: 'list',
        permission: 'setup:CONTROLLER',
        tabPermission: 'AGENTS',
        show_if_org: [146],
      },

      {
        label: i18n.t('setup.setup.controller'),
        path: 'controllers',
        permission: 'setup:CONTROLLER',
        tabPermission: 'CONTROLLERS',
        show_if_org: [146],
      },

      {
        label: i18n.t('setup.setup.logs'),
        path: 'logs',
        permission: 'setup:CONTROLLER',
        tabPermission: 'LOGS',
        show_if_org: [146],
      },

      {
        label: i18n.t('setup.setup.config'),
        path: 'config',
        permission: 'setup:CONTROLLER',
        tabPermission: 'CONFIGURATION',
        show_if_org: [146],
      },

      {
        label: i18n.t('setup.setup.commissioning'),
        path: 'commissioning',
        permission: 'setup:CONTROLLER',
        tabPermission: 'COMMISSIONING',
        show_if_org: [146],
      },
    ],
  },

  {
    label: i18n.t('setup.setup.bimIntegration'),
    path: 'bim',
    menu: [
      {
        label: i18n.t('setup.setup.importBimFile'),
        path: 'bimintegration',
        permission: 'setup:GENERAL',
        tabPermission: '',
        feature_license: ['BIM'],
        hide_if_license: ['ETISALAT'],
      },
      {
        label: i18n.t('setup.setup.bimFiles'),
        path: 'bimFiles',
        permission: 'setup:GENERAL',
        tabPermission: '',
        feature_license: ['BIM'],
        hide_if_license: ['ETISALAT'],
      },
    ],
  },
  {
    label: i18n.t('setup.setup.tenantbilling'),
    path: 'tenantbilling',
    menuName: 'SPACE_ASSET',
    menu: [
      {
        label: i18n.t('setup.setup.tenant'),
        path: 'tenant',
        permission: 'setup:TENANT_BILLING',
        feature_license: ['TENANT_BILLING'],
        hide_if_license: ['ETISALAT'],
      },
      {
        label: i18n.t('setup.setup.ratecard'),
        path: 'ratecard',
        permission: 'setup:TENANT_BILLING',
        feature_license: ['TENANT_BILLING'],
        hide_if_license: ['ETISALAT'],
      },
    ],
  },
  {
    label: i18n.t('setup.setup.developerspace'),
    path: 'developerspace',
    menu: [
      {
        label: i18n.t('setup.setup.apisetup'),
        path: 'apisetup',
        permission: 'setup:GENERAL',
        tabPermission: '',
        feature_license: ['DEVELOPER_SPACE'],
      },
    ],
  },
  {
    label: i18n.t('setup.setup.logs'),
    path: 'logs',
    menu: [
      {
        label: i18n.t('setup.setup.email_logs'),
        path: 'emaillogs',
        feature_license: ['EMAIL_TRACKING'],
      },
      {
        label: i18n.t('setup.setup.audit_logs'),
        path: 'auditlog',
      },
      {
        label: i18n.t('setup.scriptlogs.scriptlogs'),
        path: 'scriptlogs',
        feature_license: ['WORKFLOW_LOG'],
      },
    ],
  },
  {
    label: i18n.t('setup.setup.workplacesettings'),
    path: 'workplacesettings',
    menu: [
      {
        label: i18n.t('setup.setup.booking'),
        path: 'bookingpolicies',
        feature_license: ['WORKPLACE_APPS'],
      },
    ],
  },
]
