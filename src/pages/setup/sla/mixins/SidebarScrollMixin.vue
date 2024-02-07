<script>
import VueScrollTo from 'vue-scrollto'
export default {
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
    scrollhandler() {
      let {
        highlightSidebarElement,
        anchorSectionHeader,
        deAnchorSectionHeader,
        sectionElements,
      } = this
      let elements = sectionElements.map(id => document.querySelector(id))

      window.requestAnimationFrame(() => {
        elements.forEach(el => {
          let { top } = el.getBoundingClientRect()
          let sectionName = el.getAttribute('id').split('-')[0]

          if (top < 153) {
            anchorSectionHeader(sectionName)
          } else {
            deAnchorSectionHeader(sectionName)
          }

          if (top < 400) {
            highlightSidebarElement(sectionName)
          }
        })
      })
    },

    scrollTo(sectionName) {
      const onDone = () => this.highlightSidebarElement(sectionName)

      VueScrollTo.scrollTo(`#${sectionName}-section`, 500, {
        container: '.scroll-container',
        easing: 'ease-in',
        offset: 0,
        force: true,
        onDone,
      })
    },

    highlightSidebarElement(sectionName) {
      let { sidebarElements } = this
      let elements = sidebarElements.map(e => document.querySelector(e))

      elements.forEach(el => {
        if (el.getAttribute('id') === `${sectionName}-link`)
          el.classList.add('active')
        else el.classList.remove('active')
      })
    },

    anchorSectionHeader(sectionName) {
      let elements = document.querySelectorAll('.section-header')

      elements.forEach(el => {
        if (el.getAttribute('id') === `${sectionName}-header`)
          el.classList.add('anchor-top')
      })
    },

    deAnchorSectionHeader(sectionName) {
      let elements = document.querySelectorAll('.section-header')
      elements.forEach(el => {
        if (el.getAttribute('id') === `${sectionName}-header`)
          el.classList.remove('anchor-top')
      })
    },
  },
}
</script>
