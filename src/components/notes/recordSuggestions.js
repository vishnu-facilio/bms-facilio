import { VueRenderer } from '@tiptap/vue-2'
import tippy, { sticky } from 'tippy.js'
import RecordMentions from './commentMentions.vue'

export default {
  render: () => {
    let component
    let popup

    return {
      onStart: props => {
        component = new VueRenderer(RecordMentions, {
          parent: this,
          propsData: props,
          editor: props.editor,
        })
        if (!props.clientRect) {
          return
        }

        popup = tippy('body', {
          plugins: [sticky],
          getReferenceClientRect: props.clientRect,
          appendTo: () => document.body,
          content: component.element,
          showOnCreate: true,
          interactive: true,
          trigger: 'manual',
          placement: 'bottom-start',
          zIndex: 2100,
          sticky: true,
        })
      },

      onUpdate(props) {
        component.updateProps(props)

        if (!props.clientRect) {
          return
        }

        popup[0].setProps({
          getReferenceClientRect: props.clientRect,
        })
      },

      onKeyDown(props) {
        if (props.event.key === 'Escape') {
          popup[0].hide()

          return true
        }

        return component.ref?.onKeyDown(props)
      },

      onExit() {
        popup[0].destroy()
        component.destroy()
      },
    }
  },
}
