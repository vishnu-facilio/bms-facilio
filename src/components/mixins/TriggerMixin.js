export default {
  data() {
    return {
      basedOn: ['Date', 'Week'],
      skipOptions: [
        {
          label: 'Do not skip',
          value: -1,
        },
        {
          label: 'Second Cycle',
          value: 2,
        },
        {
          label: 'Third Cycle',
          value: 3,
        },
        {
          label: 'Fourth Cycle',
          value: 4,
        },
        {
          label: 'Fifth Cycle',
          value: 5,
        },
        {
          label: 'Sixth Cycle',
          value: 6,
        },
        {
          label: 'Seventh Cycle',
          value: 7,
        },
        {
          label: 'Eighth Cycle',
          value: 8,
        },
        {
          label: 'Ninth Cycle',
          value: 9,
        },
        {
          label: 'Tenth Cycle',
          value: 10,
        },
      ],
    }
  },
  methods: {
    getNumberRange(no) {
      let list = []
      let startDate = this.$helpers.getOrgMoment('2013-01-01')
      for (let i = 1; i <= no; i++) {
        list.push({
          label: startDate.format('DDDo'),
          value: i,
        })
        startDate.add(1, 'd')
      }
      return list
    },
    forceUpdate() {
      this.$forceUpdate()
    },
  },
}
