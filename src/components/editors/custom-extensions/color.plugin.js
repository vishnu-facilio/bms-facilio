import { Mark, mergeAttributes } from '@tiptap/core'

export const inputRegex = /(?:^|\s)((?:==)((?:[^~=]+))(?:==))$/
export const pasteRegex = /(?:^|\s)((?:==)((?:[^~=]+))(?:==))/g

const Color = Mark.create({
  name: 'color',

  addOptions() {
    return {
      multicolor: false,
      HTMLAttributes: {},
    }
  },

  addAttributes() {
    if (!this.options.multicolor) {
      return {}
    }

    return {
      color: {
        default: null,
        parseHTML: element =>
          element.getAttribute('data-text-color') || element.style.color,
        renderHTML: attributes => {
          if (!attributes.color) {
            return {}
          }

          return {
            'data-text-color': attributes.color,
            style: `color: ${attributes.color}`,
          }
        },
      },
    }
  },

  parseHTML() {
    return [
      {
        tag: 'span',
      },
    ]
  },

  renderHTML({ HTMLAttributes }) {
    return [
      'span',
      mergeAttributes(this.options.HTMLAttributes, HTMLAttributes),
      0,
    ]
  },

  addCommands() {
    return {
      toggleColor: attributes => ({ commands }) => {
        return commands.toggleMark(this.name, attributes)
      },
    }
  },
})

export default Color
