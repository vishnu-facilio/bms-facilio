import TableCell from '@tiptap/extension-table-cell'

export default TableCell.extend({
  addAttributes() {
    return {
      colspan: {
        default: 1,
      },
      rowspan: {
        default: 1,
      },
      colwidth: {
        default: null,
        parseHTML: element => {
          const colwidth = element.getAttribute('colwidth')
          const value = colwidth ? [parseInt(colwidth, 10)] : null
          return value
        },
        renderHTML: () => {
          return {
            style: `border: 1px solid #d0d9e2;color: #2f4058;font-weight: 400;`,
          }
        },
      },
    }
  },
  parseHTML() {
    return [{ tag: 'td' }]
  },
})
