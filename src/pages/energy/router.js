export default {
  name: 'em',
  path: 'em',
  component: () => import('pages/energy/Layout'),
  children: [
    {
      name: 'leeds',
      path: 'leeds',
      component: () => import('pages/energy/leed/LeedList'),
      children: [
        {
          name: 'leed',
          path: ':id',
          component: () => import('pages/energy/leed/Leed'),
          children: [
            {
              name: 'energy',
              path: 'energy',
              component: () => import('pages/energy/leed/Energy'),
            },
            {
              name: 'water',
              path: 'water',
              component: () => import('pages/energy/leed/Water'),
            },
            {
              name: 'waste',
              path: 'waste',
              component: () => import('pages/energy/leed/Waste'),
            },
            {
              name: 'transportation',
              path: 'transportation',
              component: () => import('pages/energy/leed/Transportation'),
            },
            {
              name: 'humanexperience',
              path: 'humanexperience',
              component: () => import('pages/energy/leed/HumanExperience'),
            },
          ],
        },
      ],
    },
    {
      path: 'reports/new',
      component: () => import('pages/report/forms/NewReport'),
      meta: {
        module: 'energydata',
      },
    },
    {
      path: 'reports/edit/:reportid',
      component: () => import('pages/report/forms/NewReport'),
      meta: {
        module: 'energydata',
      },
    },
    {
      name: 'etisalatmoduleanalytics',
      path: 'modulereports/new',
      component: () => import('pages/report/forms/NewReport.vue'),
      meta: {
        module: 'workorder',
        switch: 'yes',
      },
    },
    {
      name: 'etisalatmodulereports',
      path: 'modulereports',
      component: () => import('pages/report/Layout'),
      meta: { module: 'workorder' },
      children: [
        {
          name: 'etisalatmodulenewreport',
          path: 'newview/:reportid',
          component: () => import('pages/report/ModuleNewReportSummary'),
        },
        {
          name: 'etisalatmodulereportschedule',
          path: 'scheduled',
          component: () => import('pages/report/ReportScheduledList'),
          meta: { module: 'workorder' },
        },
      ],
    },
    {
      path: 'reports/newmatrix',
      component: () => import('pages/report/forms/NewMatrixReport'),
      meta: {
        module: 'energydata',
      },
    },
    {
      path: 'reports/newtabular',
      component: () => import('pages/report/forms/NewTabularReport'),
      meta: {
        module: 'energydata',
      },
    },
    {
      name: 'pivotviewload',
      path: 'pivot/view',
      component: () => import('pages/energy/pivot/Layout'),
    },
    {
      props: route => {
        return {
          id: route.params.id ? parseInt(route.params.id) : null,
        }
      },
      name: 'pivotview',
      path: 'pivot/view/:id',
      component: () => import('pages/energy/pivot/Layout'),
    },
    {
      name: 'pivotfoldermanager',
      path: 'pivot/foldermanager',
      component: () =>
        import('pages/energy/pivot/Components/FolderManagerLayout'),
    },
    {
      props: true,
      name: 'pivotlist',
      path: 'pivot/list',
      component: () => import('pages/energy/pivot/PivotList'),
    },
    {
      props: true,
      name: 'pivotcreate',
      path: 'pivot/new',
      component: () => import('pages/energy/pivot/PivotBuilder'),
    },
    {
      props: true,
      name: 'pivotcreatenew',
      path: 'pivotbuilder/new',
      component: () => import('pages/energy/pivot/PivotBuilderNew'),
    },
    {
      props: true,
      name: 'pivotedit',
      path: 'pivot/edit/:id',
      component: () => import('pages/energy/pivot/PivotBuilder'),
    },
    {
      name: 'enreports',
      path: 'reports',
      component: () => import('pages/report/Layout'),
      meta: {
        module: 'energydata',
      },
      children: [
        {
          path: 'view/:reportid',
          component: () => import('pages/report/ReportSummary'),
        },
        {
          path: 'newview/:reportid',
          component: () => import('pages/report/NewReportSummary'),
        },
        {
          path: 'foldermanager',
          component: () => import('pages/report/FolderManagerLayout.vue'),
          meta: { module: 'energydata' },
        },
        // {
        //   path: 'scheduled',
        //   component: () => import('pages/report/ReportScheduledList'),
        //   meta: {
        //     module: 'energydata',
        //   },
        // },
      ],
    },
    {
      path: 'reports/view/:reportid/show',
      component: () => import('pages/report/ReportChart'),
      meta: {
        module: 'energydata',
      },
    },
    {
      path: 'oldanalytics',
      component: () => import('pages/energy/analytics/Layout'),
      meta: {
        module: 'energydata',
      },
      children: [
        {
          path: 'consumption',
          component: () =>
            import('pages/energy/analytics/tools/ConsumptionAnalysis'),
        },
        {
          path: 'load',
          component: () => import('pages/energy/analytics/tools/LoadAnalysis'),
        },
        {
          path: 'heatmap',
          component: () =>
            import('pages/energy/analytics/tools/HeatmapAnalysis'),
        },
        {
          path: 'reading',
          component: () =>
            import('pages/energy/analytics/tools/ReadingAnalysis'),
        },
        {
          path: 'enpi',
          component: () => import('pages/energy/analytics/tools/EnpiAnalysis'),
        },
        {
          path: 'peakenergy',
          component: () => import('pages/energy/analytics/tools/PeakAnalysis'),
        },
        {
          path: 'regression',
          component: () =>
            import('pages/energy/analytics/tools/RegressionAnalysis'),
        },
      ],
    },
    {
      path: 'analytics',
      component: () => import('pages/energy/analytics/newLayout'),
      meta: {
        module: 'energydata',
      },
      children: [
        {
          path: 'oldportfolio',
          component: () =>
            import('pages/energy/analytics/newTools/PortfolioAnalysis'),
          meta: {
            module: 'energydata',
          },
        },
        {
          path: 'newportfolio',
          component: () =>
            import('pages/energy/analytics/newTools/NewPortfolioAnalysis'),
          meta: {
            module: 'energydata',
          },
        },
        {
          path: 'portfolio',
          component: () =>
            import('pages/energy/analytics/newTools/v1/Fportforlionew'),
          meta: {
            module: 'energydata',
          },
        },
        {
          path: 'oldbuilding',
          component: () =>
            import('pages/energy/analytics/newTools/BuildingAnalysis'),
          meta: {
            module: 'energydata',
            component: 'analytics',
          },
        },
        {
          path: 'building',
          component: () =>
            import('pages/energy/analytics/newTools/NewBuildingAnalysis'),
          meta: {
            module: 'energydata',
            component: 'analytics',
          },
        },
        {
          path: 'site',
          component: () =>
            import('pages/energy/analytics/newTools/BuildingAnalysis'),
          meta: {
            module: 'energydata',
            analyticsType: 'site',
          },
        },
        {
          path: 'oldheatmap',
          component: () =>
            import('pages/energy/analytics/newTools/NewHeatmapAnalysis'),
          meta: {
            module: 'energydata',
          },
        },
        {
          path: 'oldregression',
          component: () =>
            import('pages/energy/analytics/newTools/NewRegressionAnalysis'),
          meta: {
            module: 'energydata',
          },
        },
        {
          path: 'regression',
          component: () =>
            import('pages/energy/analytics/newTools/BuildingAnalysis'),
          meta: {
            module: 'energydata',
          },
        },
        {
          path: 'heatmap',
          component: () => import('pages/energy/analytics/newTools/Heatmap'),
          meta: {
            module: 'energydata',
            analyticsType: 'heatmap',
          },
        },
        {
          path: 'treemap',
          component: () => import('pages/energy/analytics/newTools/Treemap'),
          meta: {
            module: 'energydata',
            analyticsType: 'treemap',
          },
        },
        {
          path: 'workplacetreemap',
          component: () => import('pages/workplaceAnalytics/Layout.vue'),
        },
        {
          path: 'workplacetreemap/:buildingId',
          component: () =>
            import('pages/workplaceAnalytics/WorkPlaceFloorAnalysis.vue'),
          meta: {
            module: 'desks',
            analyticsType: 'workplacetreemap',
          },
        },
        {
          path: 'scatteranalysis',
          component: () =>
            import('pages/energy/analytics/newTools/ScatterAnalysis'),
          meta: {
            module: 'energydata',
          },
        },
        {
          path: 'scatter',
          component: () =>
            import(
              'src/pages/energy/analytics/scatterAnalytics/scatterAnalytics'
            ),
          meta: {
            module: 'energydata',
          },
        },
        {
          path: 'newscatter',
          component: () =>
            import('pages/energy/analytics/newTools/ScatterAnalytics'),
          meta: {
            module: 'energydata',
          },
        },
      ],
    },
    {
      path: 'newanalytics/generate',
      component: () =>
        import('pages/energy/analytics/newTools/NewanalyticsGenerater'),
      meta: {
        module: 'energydata',
        layout: 'mobile',
        source: 'analytics',
      },
    },
    {
      path: 'graphics',
      component: () => import('pages/energy/graphics/Graphics'),
      children: [
        {
          path: 'view/:graphicsid',
          component: () => import('pages/energy/graphics/GraphicsSummary'),
        },
      ],
    },
    {
      path: 'kpi',
      component: () => import('pages/energy/kpi/kpiIndex.vue'),
      children: [
        {
          path: '',
          redirect: 'reading/list',
        },
        {
          path: 'reading',
          redirect: 'reading/list',
        },
        {
          name: 'kpiViewer',
          path: 'reading/list',
          component: () => import('pages/energy/kpi/ReadingKpiList.vue'),
        },
        {
          name: 'reading-kpi-list',
          path: 'reading/templates',
          component: () => import('pages/energy/kpi/ReadingKpiTemplates.vue'),
        },
        {
          path: 'module',
          redirect: 'module/list',
        },
        {
          name: 'module-kpi-list',
          path: 'module/list',
          component: () => import('pages/energy/kpi/ModulekpiList.vue'),
        },
        {
          name: 'kpi-overview',
          path: 'reading/:viewName/:id/overview',
          props: true,
          component: () => import('pages/energy/kpi/kpiSummaryLayout.vue'),
        },
      ],
    },
    {
      path: 'readingKpi',
      component: () => import('pages/energy/readingkpi/ReadingKpiIndex.vue'),
      children: [
        {
          path: '',
          redirect: 'list/live',
        },
        {
          path: 'list',
          redirect: 'list/live',
        },
        {
          name: 'readingKpiList',
          path: 'list/:kpiType',
          component: () =>
            import('pages/energy/readingkpi/ReadingKpiListWrapper.vue'),
        },
        {
          path: 'module',
          redirect: 'module/list',
        },
        {
          name: 'new-module-kpi-list',
          path: 'module/list',
          component: () => import('pages/energy/kpi/ModulekpiList.vue'),
        },
      ],
    },
    {
      name: 'mv',
      path: 'mv',
      redirect: 'mv/open',
    },
    {
      name: 'mv-project-list',
      path: 'mv/:viewname',
      component: () => import('pages/energy/mv/MVList.vue'),
    },
    {
      name: 'mv-project-edit',
      path: 'mv/edit/:id',
      component: () => import('pages/energy/mv/MVProjectCreation.vue'),
    },
    {
      name: 'mv-project-new',
      path: 'mv/project/new',
      component: () => import('pages/energy/mv/MVProjectCreation.vue'),
    },
    {
      name: 'mv-project-summary',
      path: 'mv/summary/:id',
      component: () => import('pages/energy/mv/MVSummary.vue'),
    },
  ],
}
