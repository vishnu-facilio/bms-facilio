import Link from '@tiptap/extension-link'

export default Link.extend({
  addAttributes() {
    return {
      href: {
        default: null,
      },
      target: {
        default: this.options.HTMLAttributes.target,
      },
      style: {
        default: null,
      },
    }
  },
})
