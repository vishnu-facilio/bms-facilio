import { VueRenderer } from '@tiptap/vue-2'
import tippy from 'tippy.js'
import MentionList from 'components/editors/MentionList'

export default {
  char: '$',
  data() {
    return {
      moduleFields: [],
      loading: true,
      getModuleData: [],
    }
  },
  render: () => {
    let component
    let popup
    return {
      onStart: props => {
        component = new VueRenderer(MentionList, {
          parent: this,
          propsData: {
            ...props,
            ...{ mentionsdata: [{ name: 'test placeHolders' }] },
          },
        })

        popup = tippy('body', {
          getReferenceClientRect: props.clientRect,
          appendTo: () => document.body,
          content: component.element,
          showOnCreate: true,
          interactive: true,
          trigger: 'manual',
          placement: 'bottom-start',
        })
      },
      onUpdate(props) {
        component.updateProps(props)
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
