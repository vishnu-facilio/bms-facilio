// import Vue from 'vue'
import Router from 'src/router'

const errorRedirect = time => {
  window.setTimeout(() => {
    Router.replace({ name: 'welcome' })
  }, time || 5000)
}

export { errorRedirect }
