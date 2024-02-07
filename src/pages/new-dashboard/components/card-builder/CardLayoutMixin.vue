<script>
export default {
  components: {
    gauge_layout_1: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/targetMeter/type1/Config'
      ),
    gauge_layout_2: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/targetMeter/type2/Config'
      ),
    gauge_layout_3: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/targetMeter/type3/Config'
      ),
    gauge_layout_4: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/targetMeter/type4/Config'
      ),
    gauge_layout_5: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/targetMeter/type5/Config'
      ),
    gauge_layout_6: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/targetMeter/type6/Config'
      ),
    gauge_layout_7: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/targetMeter/type7/Config'
      ),
    readingcard_layout_1: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/readingCard/type1/Config'
      ),
    readingcard_layout_2: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/readingCard/type2/Config'
      ),
    readingcard_layout_3: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/readingCard/type3/Config'
      ),
    readingcard_layout_4: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/readingCard/type4/Config'
      ),
    readingcard_layout_5: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/readingCard/type5/Config'
      ),
    readingCard6: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/readingCard/type6/Config'
      ),
    mapcard_layout_1: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/mapCard/base/Config'
      ),
    datatable: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/dataTable/base/Config'
      ),
    weathercard_layout_1: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/weather/base/Config'
      ),
    carboncard_layout_1: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/carbon/type1/Config'
      ),
    energycost_layout_1: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/energyCost/type1/Config'
      ),
    graphicalcard_layout_1: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/graphicCard/type1/Config'
      ),
    energycard_layout_1: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/energy/type1/Config'
      ),
    controlcard_layout_1: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/controlCard/type1/Config'
      ),
    kpicard_layout_1: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/kpi/type1/Config'
      ),
    kpicard_layout_2: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/kpi/type2/Config'
      ),
    pmreadings_layout_1: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/pmReadings/type1/Config'
      ),
    web_layout_1: () =>
      import(
        /* webpackPreload: true */ 'pages/card-builder/cards/web/base/Config'
      ),
    photos_layout_1: () =>
      import('pages/card-builder/cards/photos/type1/Config'),
    table_layout_1: () =>
      import('pages/card-builder/cards/readingTable/type1/Config'),
    floorplan_layout_1: () =>
      import('pages/card-builder/cards/floorPlan/type1/Config'),
  },
  data() {
    return {
      cardTypes: [
        {
          type: 'target',
          icon: 'svgs/cardbuilder/targetcard',
          name: 'Gauge',
          layouts: [
            {
              type: 'TARGET1',
              component: 'gauge_layout_1',
              icon: 'target-meter1',
              name: 'Gauge Layout 1',
              style: 'width: 200px; height: 233px;',
            },
            {
              type: 'TARGET2',
              component: 'gauge_layout_2',
              icon: 'target-meter2',
              name: 'Gauge Layout 2',
              style: 'width: 200px; height: 233px;',
            },
            {
              type: 'TARGET3',
              component: 'gauge_layout_3',
              icon: 'target-meter3',
              name: 'Gauge Layout 3',
              style: 'width: 200px; height: 233px;',
            },
            {
              type: 'TARGET4',
              component: 'gauge_layout_4',
              icon: 'target-meter4',
              name: 'Gauge Layout 4',
              style: 'width: 200px; height: 233px;',
            },
            {
              type: 'TARGET5',
              component: 'gauge_layout_5',
              icon: 'target-meter6',
              name: 'Gauge Layout 5',
              style: 'width: 200px; height: 233px;',
            },
            {
              type: 'TARGET6',
              component: 'gauge_layout_6',
              icon: 'target-meter-6',
              name: 'Gauge Layout 6',
              style: 'width: 200px; height: 233px;',
            },
            {
              type: 'TARGET7',
              component: 'gauge_layout_7',
              icon: 'target-meter7',
              name: 'Gauge Layout 7',
              style: 'width: 200px; height: 233px;',
            },
          ],
        },
        {
          type: 'reading',
          icon: 'svgs/cardbuilder/readingcard',
          name: 'Reading',
          layouts: [
            {
              type: 'READING1',
              component: 'readingcard_layout_1',
              icon: 'reading-card1',
              name: 'Reading Card 1',
              style: 'width: 250px; height: 205px;',
              styles: {
                popupWidth: '73%',
              },
            },
            {
              type: 'READING2',
              component: 'readingcard_layout_2',
              icon: 'reading-card2',
              name: 'Reading Card (With Baseline)',
              style:
                'width: 306px; height: 205px; border: 1px solid rgb(237, 243, 243);',
            },
            {
              type: 'READING3',
              component: 'readingcard_layout_3',
              icon: 'reading-card-graph',
              name: 'Reading Card 3',
              style: 'width: 215px; height: 200px;',
            },
            {
              type: 'READING4',
              component: 'readingcard_layout_4',
              icon: 'reading-card5',
              name: 'Reading Card (With Multi-Aggregation)',
              style:
                'width: 100%;height: auto; border: 1px solid rgb(237, 243, 243);',
            },
            {
              type: 'READING5',
              component: 'readingcard_layout_5',
              icon: 'reading-card4',
              name: 'Reading Card (Multi-Asset)',
              style:
                'width: 100%; height: auto;border: 1px solid rgb(237, 243, 243);',
              styles: {
                popupWidth: '70%',
              },
            },
            // {
            //   type: 'READING6',
            //   component: 'readingcard_layout_6',
            //   icon: 'svgs/cardbuilder/readingcard',
            //   name: 'Layout 6',
            // },
          ],
        },
        {
          type: 'map',
          icon: 'svgs/cardbuilder/map',
          name: 'Map',
          layouts: [
            {
              styles: {
                popupWidth: '70%',
              },
              type: 'MAP1',
              component: 'mapcard_layout_1',
              icon: 'svgs/cardbuilder/map',
              name: 'Map',
            },
          ],
        },
        // {
        //   type: 'datatable',
        //   icon: 'svgs/cardbuilder/default-icon',
        //   name: 'Data Table',
        //   layouts: [
        //     {
        //       type: 'TABLE1',
        //       component: 'datatable',
        //       icon: 'svgs/cardbuilder/default-icon',
        //       name: 'Layout 1',
        //     },
        //   ],
        // },
        {
          type: 'weather',
          icon: 'svgs/cardbuilder/weather',
          name: 'Weather',
          layouts: [
            {
              type: 'WEATHER1',
              component: 'weathercard_layout_1',
              icon: 'svgs/cardbuilder/weather',
              name: 'Weather Layout 1',
            },
          ],
        },
        {
          type: 'carbon',
          icon: 'svgs/cardbuilder/carbon',
          name: 'Carbon',
          layouts: [
            {
              type: 'CARBON1',
              component: 'carboncard_layout_1',
              icon: 'svgs/cardbuilder/carbon',
              name: 'Carbon Layout 1',
            },
          ],
        },
        {
          type: 'energy',
          icon: 'svgs/cardbuilder/energy',
          name: 'ENERGY',
          layouts: [
            {
              type: 'ENERGYCARD1',
              component: 'energycard_layout_1',
              icon: 'svgs/cardbuilder/energy',
              name: 'Energy Layout 1',
            },
          ],
        },
        {
          type: 'energyCost',
          icon: 'svgs/cardbuilder/energyCost',
          name: 'Energy Cost',
          layouts: [
            {
              type: 'ENERGYCOST1',
              component: 'energycost_layout_1',
              icon: 'svgs/cardbuilder/energyCost',
              name: 'Energy Cost Layout 1',
            },
          ],
        },
        {
          type: 'graphics',
          icon: 'svgs/cardbuilder/livedata',
          name: 'Live Data',
          layouts: [
            {
              type: 'GRAPHICS1',
              component: 'graphicalcard_layout_1',
              icon: 'svgs/cardbuilder/livedata',
              name: 'Live Data Layout 1',
            },
          ],
        },
        {
          type: 'control',
          icon: 'svgs/cardbuilder/controls',
          name: 'Control Command',
          layouts: [
            {
              type: 'CONTROL1',
              component: 'controlcard_layout_1',
              icon: 'svgs/cardbuilder/controls',
              name: 'Control Command Layout 1',
            },
          ],
        },
        {
          type: 'kpi',
          icon: 'svgs/cardbuilder/kpi',
          name: 'KPI',
          layouts: [
            {
              type: 'KPICARD1',
              component: 'kpicard_layout_1',
              style: 'width: 300px; height: 220px;',

              icon: 'reading-card1',
              name: 'KPI Layout 1',
            },
            {
              type: 'KPICARD2',
              component: 'kpicard_layout_2',
              style: 'width: 300px; height: 220px;',
              icon: 'reading-card2',
              name: 'KPI Layout 2',
            },
          ],
        },
        {
          type: 'pmreadings',
          icon: 'svgs/cardbuilder/pm',
          name: 'PM Readings',
          layouts: [
            {
              type: 'PMREADINGS1',
              component: 'pmreadings_layout_1',
              icon: 'svgs/cardbuilder/pm',
              name: 'PM Readings Layout 1',
            },
          ],
        },
        {
          type: 'web',
          icon: 'svgs/cardbuilder/web',
          name: 'Connected App',
          layouts: [
            {
              type: 'WEB1',
              component: 'web_layout_1',
              icon: 'svgs/cardbuilder/web',
              name: 'Connected App',
            },
          ],
        },
        {
          type: 'photos',
          icon: 'svgs/cardbuilder/Photos',
          name: 'Photos',
          layouts: [
            {
              type: 'PHOTOS',
              component: 'photos_layout_1',
              icon: 'svgs/cardbuilder/Photos',
              name: 'Photots Layout 1',
            },
          ],
        },
        {
          type: 'readingtable',
          icon: 'svgs/cardbuilder/default-icon',
          name: 'Reading Table',
          layouts: [
            {
              type: 'READINGTABLE1',
              component: 'table_layout_1',
              icon: 'svgs/cardbuilder/default-icon',
              name: 'Reading Table 1',
            },
          ],
        },
        {
          type: 'floorplan',
          icon: 'svgs/cardbuilder/default-icon',
          name: 'Floor Plan',
          layouts: [
            {
              type: 'FLOORPLAN1',
              component: 'floorplan_layout_1',
              icon: 'svgs/cardbuilder/default-icon',
              name: 'Floor Plan',
            },
          ],
        },
      ],
    }
  },
}
</script>
