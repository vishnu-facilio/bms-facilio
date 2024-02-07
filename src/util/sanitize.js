/**
 * Util to sanitize html and text content from users.
 * Should be used when sending html to server (insertHTML) and when displaying html using v-html
 *
 * @param  html The raw html that must be sanitized
 */
import DOMPurify from 'dompurify'

export default function sanitize(html) {
  return DOMPurify.sanitize(html, {
    ADD_ATTR: ['target'],
  })
}
