export default function() {
  // Quick test if browser has cookieEnabled host property
  if (navigator.cookieEnabled) return true
  // Create cookie
  document.cookie = 'fc.testCookie=1'
  // Check if it worked
  let hasCookie = document.cookie.indexOf('fc.testCookie=') != -1
  // Delete cookie
  document.cookie = 'fc.testCookie=1; expires=Thu, 01-Jan-1970 00:00:01 GMT'

  return hasCookie
}
