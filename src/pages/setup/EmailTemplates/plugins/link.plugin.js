import Link from '@tiptap/extension-link'

export default Link.extend({
  inclusive() {
    return (this.options.autolink = false)
  },
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
