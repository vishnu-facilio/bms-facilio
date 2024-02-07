export default {
  data() {
    return {
      config: {
        name: '',
        locationModule: 'site/building/assets/...',
        locationField: 'currentLoction/geoLoaction/location',
        criteria: {
          // data condtional form atting
        },
        styles: {
          // map styles
        },
        marker: {
          styles: {
            icon: '', // base64 encoded svg,
          },
          action: {
            type: 'function / url / popover',
            fuction: '',
            url: '',
            popover: '',
          },
          markerValue: 'noOfworkorder/noOfAlarms/totalEnergy', // will discuss
          conditionalFormatting: {
            criteria: {}, // special for formating
            styles: {
              type: '',
            },
          },
        },
        userAction: {},
      },
      mapResult: [
        {
          id: '#2344',
          name: 'Building 1',
          markerValue: '10 kWh',
          location: {
            lat: '',
            lng: '',
          },
          avatarUrl: '',
        },
      ],
      cardContext: {
        cardLayout: 'map',
        cardParams: {
          title: '',
          locationModule: 'site/building/assets/...',
          locationField: 'currentLoction/geoLoaction/location',
          criteria: {},
          markerValue:
            'noOfworkorder/noOfAlarms/noOfBuildings/noOfFloors/totalEnergy',
        },
        cardState: {
          styles: {},
          marker: {
            styles: {
              icon: '', // base64 encoded svg,
            },
            action: {
              type: 'function / url / popover',
              fuction: '',
              url: '',
              popover: '',
            },
            conditionalFormatting: {
              criteria: {}, // special for formating
              styles: {
                type: '',
              },
            },
          },
        },
      },
      tableConfig: {
        columns: [
          {
            key: 'a',
            label: 'No Of Tasks',
            datatype: 'NUMBER',
            width: '20',
            merge: false,
          },
        ],
      },
      tabledata: [
        {
          a: 1,
          b: 2,
          c: 3,
        },
        {
          a: 1,
          b: 2,
          c: 3,
        },
      ],
    }
  },
}
