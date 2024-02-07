export const mapTheams = [
  {
    name: 'night',
    value: 1,
    displayName: 'Night',
    theme: {
      gestureHandling: 'cooperative',
      styles: [
        {
          elementType: 'geometry',
          stylers: [
            {
              color: '#242f3e',
            },
          ],
        },
        {
          elementType: 'labels.text.stroke',
          stylers: [
            {
              color: '#242f3e',
            },
          ],
        },
        {
          elementType: 'labels.text.fill',
          stylers: [
            {
              color: '#746855',
            },
          ],
        },
        {
          featureType: 'administrative.locality',
          elementType: 'labels.text.fill',
          stylers: [
            {
              color: '#d59563',
            },
          ],
        },
        {
          featureType: 'poi',
          elementType: 'labels.text.fill',
          stylers: [
            {
              color: '#d59563',
            },
          ],
        },
        {
          featureType: 'poi.park',
          elementType: 'geometry',
          stylers: [
            {
              color: '#263c3f',
            },
          ],
        },
        {
          featureType: 'poi.park',
          elementType: 'labels.text.fill',
          stylers: [
            {
              color: '#6b9a76',
            },
          ],
        },
        {
          featureType: 'road',
          elementType: 'geometry',
          stylers: [
            {
              color: '#38414e',
            },
          ],
        },
        {
          featureType: 'road',
          elementType: 'geometry.stroke',
          stylers: [
            {
              color: '#212a37',
            },
          ],
        },
        {
          featureType: 'road',
          elementType: 'labels.text.fill',
          stylers: [
            {
              color: '#9ca5b3',
            },
          ],
        },
        {
          featureType: 'road.highway',
          elementType: 'geometry',
          stylers: [
            {
              color: '#746855',
            },
          ],
        },
        {
          featureType: 'road.highway',
          elementType: 'geometry.stroke',
          stylers: [
            {
              color: '#1f2835',
            },
          ],
        },
        {
          featureType: 'road.highway',
          elementType: 'labels.text.fill',
          stylers: [
            {
              color: '#f3d19c',
            },
          ],
        },
        {
          featureType: 'transit',
          elementType: 'geometry',
          stylers: [
            {
              color: '#2f3948',
            },
          ],
        },
        {
          featureType: 'transit.station',
          elementType: 'labels.text.fill',
          stylers: [
            {
              color: '#d59563',
            },
          ],
        },
        {
          featureType: 'water',
          elementType: 'geometry',
          stylers: [
            {
              color: '#17263c',
            },
          ],
        },
        {
          featureType: 'water',
          elementType: 'labels.text.fill',
          stylers: [
            {
              color: '#515c6d',
            },
          ],
        },
        {
          featureType: 'water',
          elementType: 'labels.text.stroke',
          stylers: [
            {
              color: '#17263c',
            },
          ],
        },
      ],
    },
  },
  {
    name: 'monochrome',
    value: 2,
    displayName: 'Monochrome',
    theme: {
      gestureHandling: 'cooperative',
      styles: [
        {
          elementType: 'geometry.fill',
          stylers: [
            {
              weight: '2.00',
            },
          ],
          featureType: 'all',
        },
        {
          featureType: 'all',
          elementType: 'geometry.stroke',
          stylers: [
            {
              color: '#9c9c9c',
            },
          ],
        },
        {
          featureType: 'all',
          stylers: [
            {
              visibility: 'on',
            },
          ],
          elementType: 'labels.text',
        },
        {
          featureType: 'landscape',
          stylers: [
            {
              color: '#f2f2f2',
            },
          ],
          elementType: 'all',
        },
        {
          featureType: 'landscape',
          stylers: [
            {
              color: '#ffffff',
            },
          ],
          elementType: 'geometry.fill',
        },
        {
          featureType: 'landscape.man_made',
          elementType: 'geometry.fill',
          stylers: [
            {
              color: '#ffffff',
            },
          ],
        },
        {
          featureType: 'poi',
          stylers: [
            {
              visibility: 'off',
            },
          ],
          elementType: 'all',
        },
        {
          featureType: 'road',
          stylers: [
            {
              saturation: -100,
            },
            {
              lightness: 45,
            },
          ],
          elementType: 'all',
        },
        {
          featureType: 'road',
          stylers: [
            {
              color: '#eeeeee',
            },
          ],
          elementType: 'geometry.fill',
        },
        {
          featureType: 'road',
          stylers: [
            {
              color: '#7b7b7b',
            },
          ],
          elementType: 'labels.text.fill',
        },
        {
          featureType: 'road',
          stylers: [
            {
              color: '#ffffff',
            },
          ],
          elementType: 'labels.text.stroke',
        },
        {
          featureType: 'road.highway',
          elementType: 'all',
          stylers: [
            {
              visibility: 'simplified',
            },
          ],
        },
        {
          featureType: 'road.arterial',
          stylers: [
            {
              visibility: 'off',
            },
          ],
          elementType: 'labels.icon',
        },
        {
          featureType: 'transit',
          elementType: 'all',
          stylers: [
            {
              visibility: 'off',
            },
          ],
        },
        {
          featureType: 'water',
          elementType: 'all',
          stylers: [
            {
              color: '#46bcec',
            },
            {
              visibility: 'on',
            },
          ],
        },
        {
          featureType: 'water',
          stylers: [
            {
              color: '#c8d7d4',
            },
          ],
          elementType: 'geometry.fill',
        },
        {
          featureType: 'water',
          stylers: [
            {
              color: '#070707',
            },
          ],
          elementType: 'labels.text.fill',
        },
        {
          featureType: 'water',
          elementType: 'labels.text.stroke',
          stylers: [
            {
              color: '#ffffff',
            },
          ],
        },
      ],
    },
  },
  {
    name: 'paledawn',
    value: 3,
    displayName: 'Pale Dawn',
    theme: {
      gestureHandling: 'cooperative',
      styles: [
        {
          featureType: 'administrative',
          elementType: 'all',
          stylers: [
            {
              visibility: 'on',
            },
            {
              lightness: 33,
            },
          ],
        },
        {
          featureType: 'landscape',
          elementType: 'all',
          stylers: [
            {
              color: '#f2e5d4',
            },
          ],
        },
        {
          featureType: 'poi.park',
          elementType: 'geometry',
          stylers: [
            {
              color: '#c5dac6',
            },
          ],
        },
        {
          featureType: 'poi.park',
          elementType: 'labels',
          stylers: [
            {
              visibility: 'on',
            },
            {
              lightness: 20,
            },
          ],
        },
        {
          featureType: 'road',
          elementType: 'all',
          stylers: [
            {
              lightness: 20,
            },
          ],
        },
        {
          featureType: 'road.highway',
          elementType: 'geometry',
          stylers: [
            {
              color: '#c5c6c6',
            },
          ],
        },
        {
          featureType: 'road.arterial',
          elementType: 'geometry',
          stylers: [
            {
              color: '#e4d7c6',
            },
          ],
        },
        {
          featureType: 'road.local',
          elementType: 'geometry',
          stylers: [
            {
              color: '#fbfaf7',
            },
          ],
        },
        {
          featureType: 'water',
          elementType: 'all',
          stylers: [
            {
              visibility: 'on',
            },
            {
              color: '#acbcc9',
            },
          ],
        },
      ],
    },
  },
  {
    name: 'manilamap',
    value: 4,
    displayName: 'Manila Map',
    theme: {
      gestureHandling: 'cooperative',
      styles: [
        {
          featureType: 'all',
          elementType: 'labels',
          stylers: [
            {
              visibility: 'off',
            },
          ],
        },
        {
          featureType: 'administrative',
          elementType: 'all',
          stylers: [
            {
              visibility: 'off',
            },
            {
              color: '#efebe2',
            },
          ],
        },
        {
          featureType: 'landscape',
          elementType: 'all',
          stylers: [
            {
              color: '#efebe2',
            },
          ],
        },
        {
          featureType: 'poi',
          elementType: 'all',
          stylers: [
            {
              color: '#efebe2',
            },
          ],
        },
        {
          featureType: 'poi.attraction',
          elementType: 'all',
          stylers: [
            {
              color: '#efebe2',
            },
          ],
        },
        {
          featureType: 'poi.business',
          elementType: 'all',
          stylers: [
            {
              color: '#efebe2',
            },
          ],
        },
        {
          featureType: 'poi.government',
          elementType: 'all',
          stylers: [
            {
              color: '#dfdcd5',
            },
          ],
        },
        {
          featureType: 'poi.medical',
          elementType: 'all',
          stylers: [
            {
              color: '#dfdcd5',
            },
          ],
        },
        {
          featureType: 'poi.park',
          elementType: 'all',
          stylers: [
            {
              color: '#bad294',
            },
          ],
        },
        {
          featureType: 'poi.place_of_worship',
          elementType: 'all',
          stylers: [
            {
              color: '#efebe2',
            },
          ],
        },
        {
          featureType: 'poi.school',
          elementType: 'all',
          stylers: [
            {
              color: '#efebe2',
            },
          ],
        },
        {
          featureType: 'poi.sports_complex',
          elementType: 'all',
          stylers: [
            {
              color: '#efebe2',
            },
          ],
        },
        {
          featureType: 'road.highway',
          elementType: 'geometry.fill',
          stylers: [
            {
              color: '#ffffff',
            },
          ],
        },
        {
          featureType: 'road.highway',
          elementType: 'geometry.stroke',
          stylers: [
            {
              visibility: 'off',
            },
          ],
        },
        {
          featureType: 'road.arterial',
          elementType: 'geometry.fill',
          stylers: [
            {
              color: '#ffffff',
            },
          ],
        },
        {
          featureType: 'road.arterial',
          elementType: 'geometry.stroke',
          stylers: [
            {
              visibility: 'off',
            },
          ],
        },
        {
          featureType: 'road.local',
          elementType: 'geometry.fill',
          stylers: [
            {
              color: '#fbfbfb',
            },
          ],
        },
        {
          featureType: 'road.local',
          elementType: 'geometry.stroke',
          stylers: [
            {
              visibility: 'off',
            },
          ],
        },
        {
          featureType: 'transit',
          elementType: 'all',
          stylers: [
            {
              visibility: 'off',
            },
          ],
        },
        {
          featureType: 'water',
          elementType: 'all',
          stylers: [
            {
              color: '#a5d7e0',
            },
          ],
        },
      ],
    },
  },
]
