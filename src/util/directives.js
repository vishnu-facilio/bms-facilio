export default {
  'click-outside': {
    isFn: true,
    bind: function(el, binding, vNode) {
      // Provided expression must evaluate to a function.
      if (typeof binding.value !== 'function') {
        const compName = vNode.context.name
        let warn = `[Vue-click-outside:] provided expression '${binding.expression}' is not a function, but has to be`
        if (compName) {
          warn += `Found in component '${compName}'`
        }
        console.warn(warn)
      }

      // Add element to stack
      if (window.__LatestPopupComponent) {
        window.__LatestPopupComponent.push(vNode.context._uid)
      } else {
        window.__LatestPopupComponent = [vNode.context._uid]
      }

      // Define Handler and cache it on the element
      const bubble = binding.modifiers.bubble

      const handler = e => {
        if (window.__LatestPopupComponent) {
          if (bubble || (!el.contains(e.target) && el !== e.target)) {
            vNode.context[binding.expression]()
            e.stopPropagation()
            window.__LatestPopupComponent.pop()
          }
        }
      }
      el.__vueClickOutside__ = handler

      const escHandler = e => {
        if (window.__LatestPopupComponent) {
          if (
            vNode.context._uid ===
              window.__LatestPopupComponent[
                window.__LatestPopupComponent.length - 1
              ] &&
            e.keyCode === 27
          ) {
            vNode.context[binding.expression]()
            e.stopPropagation()
            window.__LatestPopupComponent.pop()
          }
        }
      }
      el.__vueEscHandler__ = escHandler
    },

    inserted: function(el) {
      document.addEventListener('click', el.__vueClickOutside__)
      document.addEventListener('keydown', el.__vueEscHandler__)
    },

    update: function() {},

    unbind: function(el, binding, vNode) {
      // Remove Event Listeners
      document.removeEventListener('click', el.__vueClickOutside__)
      document.removeEventListener('onkeydown', el.__vueEscHandler__)

      // Remove Handlers
      el.__vueClickOutside__ = null
      el.__vueEscHandler__ = null

      // Pop element from stack
      if (window.__LatestPopupComponent) {
        let index = window.__LatestPopupComponent.findIndex(
          i => i === vNode.context._uid
        )
        if (index && index > -1) {
          window.__LatestPopupComponent.splice(index, 1)
        }
      }
    },
  },
}
