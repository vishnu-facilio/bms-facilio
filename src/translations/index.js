import Vue from 'vue'
import VueI18n from 'vue-i18n'
import { en } from './lang/en'
Vue.use(VueI18n)

let messages = {}
let i18n
messages['en'] = en
const setMessage = (val, lang) => {
  messages[lang] = val
}

const init = async language => {
  let currentLang = language || 'en'
  switch (currentLang) {
    case 'es': {
      let lang = await import('./lang/es/index.js')
      setMessage(lang['es'], 'es')
      break
    }
    case 'de': {
      let lang = await import('./lang/de/index.js')
      setMessage(lang['de'], 'de')
      break
    }
    case 'hu': {
      let lang = await import('./lang/hu/index.js')
      setMessage(lang['hu'], 'hu')
      break
    }
    case 'it': {
      let lang = await import('./lang/it/index.js')
      setMessage(lang['it'], 'it')
      break
    }
    case 'pt': {
      let lang = await import('./lang/pt/index.js')
      setMessage(lang['pt'], 'pt')
      break
    }
  }

  i18n = new VueI18n({
    locale: 'en', // set locale
    fallbackLocale: 'en',
    messages, // set locale messages
  })
  return i18n
}

export { init as default, i18n }
