import getProperty from 'dlv'
export default {
  computed: {
    errMessage() {
      let query = getProperty(this, '$route.query.errMessage', '')
      return query || ''
    },
    previousRoute() {
      let prevRoute = getProperty(
        this,
        '$route.query.prevRoute',
        window.location.href
      )
      return prevRoute
    },
    errorCode() {
      let status = getProperty(this, '$route.query.errorCode', '')
      return status
    },
  },
}
