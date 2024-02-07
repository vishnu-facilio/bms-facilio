let date = new Date()

export default [
  {
    label: 'DD/MM/YYYY' + ' ' + date.toLocaleDateString('en-GB'),
    value: 'DD/MM/YYYY',
  },
  {
    label: 'MM/DD/YYYY' + ' ' + date.toLocaleDateString('en-US'),
    value: 'MM/DD/YYYY',
  },
  {
    label: 'YYYY/MM/DD' + ' ' + date.toLocaleDateString('ko-KR'),
    value: 'YYYY/MM/DD',
  },
]
