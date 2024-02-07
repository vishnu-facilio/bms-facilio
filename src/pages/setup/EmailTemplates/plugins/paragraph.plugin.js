import { mergeAttributes } from '@tiptap/core'
import Paragraph from '@tiptap/extension-paragraph'
export default Paragraph.extend({
  renderHTML({ HTMLAttributes }) {
    return [
      'p',
      mergeAttributes(this.options.HTMLAttributes, HTMLAttributes),
      0,
    ]
  },
})
