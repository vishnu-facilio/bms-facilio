<script>
import VueScrollTo from 'vue-scrollto'
import { isEmpty } from '@facilio/utils/validation'

export default {
  data() {
    return {
      rootElementForScroll: '.fc-security-right',
      sidebarElements: [],
      sectionElements: [],
    }
  },
  beforeDestroy() {
    let root = document.querySelector(this.rootElementForScroll)
    root && root.removeEventListener('scroll', this.scrollhandler)
  },

  methods: {
    registerScrollHandler() {
      let root = document.querySelector(this.rootElementForScroll)

      root &&
        root.addEventListener('scroll', this.scrollhandler, {
          passive: true,
        })
    },

    scrollTo(sectionName) {
      let { rootElementForScroll } = this
      const onDone = () => this.highlightSidebarElement(sectionName)
      VueScrollTo.scrollTo(`#${sectionName}-section`, 500, {
        container: rootElementForScroll,
        easing: 'ease-in',
        offset: 0,
        force: true,
        onDone,
      })
    },

    highlightSidebarElement(sectionName) {
      let elements = this.sidebarElements
        .map(e => document.querySelector(e))
        .filter(e => !isEmpty(e))

      elements.forEach(el => {
        if (el.getAttribute('id') === `${sectionName}-link`)
          el.classList.add('active')
        else el.classList.remove('active')
      })
    },

    scrollhandler() {
      let { highlightSidebarElement } = this
      let elements = this.sectionElements.map(id => document.querySelector(id))
      window.requestAnimationFrame(() => {
        elements.forEach(el => {
          let { top } = el.getBoundingClientRect()
          let sectionName = el.getAttribute('id').split('-')[0]
          if (top <= 90) {
            highlightSidebarElement(sectionName)
          }
        })
      })
    },
  },
}
</script>
