export default {
  formatHierarchy: [
    {
      label: 'minute',
      momentLabel: 'minute',
      formatString: 'mm',
      maxLimit: 60,
    },
    {
      label: 'hourly',
      momentLabel: 'hour',
      formatString: 'HH',
      maxLimit: 24,
    },
    {
      label: 'daily',
      momentLabel: 'day',
      formatString: 'ddd DD',
      maxLimit: 7,
    },
    {
      label: 'weekly',
      momentLabel: 'week',
      formatString: 'WW',
      maxLimit: 4,
    },
    {
      label: 'monthly',
      momentLabel: 'month',
      formatString: 'MMM',
      maxLimit: 12,
    },
    {
      label: 'yearly',
      momentLabel: 'year',
      formatString: 'YYYY',
      maxLimit: 1,
    },
  ],
}
