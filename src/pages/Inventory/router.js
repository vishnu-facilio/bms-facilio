export default {
  name: 'inventory',
  path: 'inventory',
  component: () => import('pages/Inventory/Layout'),
  children: [
    // view manager
    {
      name: 'inventory-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout'),
    },
    {
      name: 'inventory-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    // store rooms
    {
      name: 'storerooms',
      path: 'storerooms/:viewname?',
      meta: { module: 'storeRoom' },
      component: () => import(`pages/Inventory/Storerooms/StoreroomList`),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'storeRoom',
      }),
      children: [
        {
          name: 'storeroomSummary',
          path: ':id/summary',
          component: () =>
            import(`pages/Inventory/Storerooms/StoreroomSummary`),
          props: route => ({
            viewname: route.params.viewname,
            moduleName: 'storeRoom',
          }),
        },
      ],
    },
    {
      name: 'new-storeroom',
      path: 'storerooms/new',
      component: () => import(`pages/Inventory/Storerooms/StoreroomForm`),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'storeRoom',
      }),
    },
    {
      name: 'edit-storeroom',
      path: 'storerooms/:id/edit',
      component: () => import(`pages/Inventory/Storerooms/StoreroomForm`),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'storeRoom',
        id: route.params.id,
      }),
    },
    // item types
    {
      name: 'new-itemType',
      path: 'itemtypes/new',
      component: () => import(`pages/Inventory/ItemTypes/ItemTypeForm`),
    },
    {
      name: 'edit-itemType',
      path: 'itemtypes/:id/edit',
      component: () => import(`pages/Inventory/ItemTypes/ItemTypeForm`),
    },
    {
      name: 'itemtypes',
      path: 'itemtypes/:viewname?',
      meta: { module: 'itemTypes' },
      component: () => import(`pages/Inventory/ItemTypes/ItemTypeList`),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'itemTypes',
      }),
      children: [
        {
          name: 'itemtypeSummary',
          path: ':id/summary',
          component: () =>
            import(`pages/Inventory/ItemTypes/ItemTypeSummary.vue`),
          props: route => ({
            viewname: route.params.viewname,
            moduleName: 'itemTypes',
          }),
        },
      ],
    },
    // items
    {
      name: 'item',
      path: 'item/:viewname?',
      meta: { module: 'item' },
      component: () => import(`pages/Inventory/Items/ItemList`),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'item',
      }),
      children: [
        {
          name: 'itemSummary',
          path: ':id/summary',
          component: () => import(`pages/Inventory/Items/ItemSummary`),
          props: route => ({
            viewname: route.params.viewname,
            moduleName: 'item',
          }),
        },
      ],
    },
    // tools
    {
      name: 'tool',
      path: 'tool/:viewname?',
      meta: { module: 'tool' },
      component: () => import(`pages/Inventory/Tools/ToolList`),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'tool',
      }),
      children: [
        {
          name: 'toolSummary',
          path: ':id/summary',
          component: () => import(`pages/Inventory/Tools/ToolSummary`),
          props: route => ({
            viewname: route.params.viewname,
            moduleName: 'tool',
          }),
        },
      ],
    },
    // tool types
    {
      name: 'new-toolType',
      path: 'tooltypes/new',
      component: () => import(`pages/Inventory/Tooltypes/ToolTypeForm`),
    },
    {
      name: 'edit-toolType',
      path: 'tooltypes/:id/edit',
      component: () => import(`pages/Inventory/Tooltypes/ToolTypeForm`),
    },
    {
      name: 'tooltypes',
      path: 'tooltypes/:viewname?',
      meta: { module: 'toolTypes' },
      component: () => import(`pages/Inventory/Tooltypes/ToolTypesList`),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'toolTypes',
      }),
      children: [
        {
          name: 'tooltypeSummary',
          path: ':id/summary',
          component: () => import(`pages/Inventory/Tooltypes/TooltypesSummary`),
          props: route => ({
            viewname: route.params.viewname,
            moduleName: 'toolTypes',
            id: route.params.id,
          }),
        },
      ],
    },
    // shipments
    {
      name: 'shipment',
      path: 'shipment/:viewname?',
      meta: { module: 'shipment' },
      component: () => import(`pages/Inventory/Shipment/ShipmentList`),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'shipment',
      }),
      children: [
        {
          name: 'shipmentSummary',
          path: ':id/summary',
          component: () => import(`pages/Inventory/Shipment/ShipmentSummary`),
          props: true,
        },
      ],
    },
    // gatepass
    {
      name: 'gatepass',
      path: 'gatepass/:viewname?',
      meta: { module: 'gatePass' },
      component: () => import(`pages/Inventory/Gatepass/GatePassList`),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'gatePass',
      }),
      children: [
        {
          name: 'gatepassSummary',
          path: ':id/summary',
          component: () => import(`pages/Inventory/Gatepass/GatepassSummary`),
          props: true,
        },
      ],
    },
    // service
    // list and summary
    {
      name: 'service',
      path: 'service/:viewname?',
      meta: { module: 'service' },
      component: () => import(`pages/Inventory/Service/ServiceList.vue`),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'service',
      }),
      children: [
        {
          name: 'servicesSummary',
          path: ':id/summary',
          component: () => import(`pages/Inventory/Service/ServiceSummary.vue`),
          props: route => ({
            viewname: route.params.viewname,
            moduleName: 'service',
            id: route.params.id,
          }),
        },
      ],
    },
    // form
    {
      name: 'service-create',
      path: 'service/new',
      component: () => import(`pages/Inventory/Service/NewServiceForm.vue`),
      props: true,
    },
    {
      name: 'service-edit',
      path: 'service/edit/:id',
      props: route => ({
        id: route.params.id,
        moduleName: 'service',
      }),
      component: () => import(`pages/Inventory/Service/NewServiceForm.vue`),
    },
    // Inventory Requests
    {
      name: 'inventoryrequest-create',
      path: 'inventoryrequest/new',
      component: () =>
        import(`pages/Inventory/InventoryRequest/InventoryRequestForm.vue`),
      props: true,
    },
    {
      name: 'inventoryrequest-edit',
      path: 'inventoryrequest/edit/:id',
      props: route => ({
        id: route.params.id,
        moduleName: 'inventoryrequest',
      }),
      component: () =>
        import(`pages/Inventory/InventoryRequest/InventoryRequestForm.vue`),
    },

    {
      name: 'inventoryrequest',
      path: 'inventoryrequest/:viewname?',
      component: () =>
        import(`pages/Inventory/InventoryRequest/InventoryRequestList`),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'inventoryrequest',
      }),
      children: [
        {
          name: 'inventoryrequestSummary',
          path: ':id/overview',
          component: () =>
            import(
              `pages/Inventory/InventoryRequest/InventoryRequestSummary.vue`
            ),
          props: route => ({
            viewname: route.params.viewname,
            id: route.params.id,
            moduleName: 'inventoryrequest',
          }),
        },
      ],
    },
    // Transfer Request
    {
      name: 'transferrequest-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'new-transferrequest',
      path: 'transferrequest/new',
      component: () =>
        import(`pages/Inventory/TransferRequest/NewTransferRequestForm`),
      props: true,
    },
    {
      name: 'edit-transferrequest',
      path: 'transferrequest/:id/edit',
      props: true,
      component: () =>
        import(`pages/Inventory/TransferRequest/NewTransferRequestForm`),
    },
    {
      name: 'transferrequestList',
      path: 'transferrequest/:viewname?',
      component: () =>
        import(`pages/Inventory/TransferRequest/TransferRequestList`),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'transferrequest',
      }),
      children: [
        {
          name: 'transferrequestSummary',
          path: ':id/overview',
          component: () =>
            import(`pages/Inventory/TransferRequest/TransferRequestSummary`),
          props: route => ({
            viewname: route.params.viewname,
            moduleName: 'transferrequest',
            id: route.params.id,
          }),
        },
      ],
    },
    // Transfer Request Shipment
    {
      name: 'trShipment-viewmanager',
      path: ':moduleName/viewmanager',
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'edit-trShipment',
      path: 'trShipment/:id/edit',
      props: true,
      component: () =>
        import(`pages/Inventory/TransferRequest/Shipment/TrShipmentForm`),
    },
    {
      name: 'trShipmentList',
      path: 'trShipment/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'transferrequestshipment',
      }),
      component: () =>
        import(`pages/Inventory/TransferRequest/Shipment/TrShipmentList`),

      children: [
        {
          name: 'trShipmentSummary',
          path: ':id/overview',
          component: () =>
            import(
              `pages/Inventory/TransferRequest/Shipment/TrShipmentSummary`
            ),
          props: route => ({
            viewname: route.params.viewname,
            moduleName: 'transferrequestshipment',
            id: route.params.id,
          }),
        },
      ],
    },
    {
      path: 'schedule',
      component: () =>
        import('pages/workorder/workorders/v1/woViewScheduleList'),
      meta: { module: 'item' },
    },
    {
      name: 'inventoryreports',
      path: 'reports',
      component: () => import('pages/report/Layout.vue'),
      meta: { module: 'item' },
      children: [
        {
          path: 'newview/:reportid',
          component: () => import('pages/report/ModuleNewReportSummary.vue'),
        },
        // {
        //   path: 'scheduled',
        //   component: () => import('pages/report/ReportScheduledList'),
        //   meta: { module: 'item' },
        // },
        {
          path: 'foldermanager',
          component: () => import('pages/report/FolderManagerLayout.vue'),
          meta: { module: 'item'},
        },
      ],
    },
    {
      name: 'inventoryanalytics',
      path: 'reports/new',
      component: () => import('pages/report/forms/NewReport.vue'),
      meta: {
        module: 'item',
        switch: 'yes',
      },
    },
  ],
}
