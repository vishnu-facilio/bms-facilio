export default function(url, opts) {
  opts = opts || {}

  let ws,
    num = 0,
    timer = 1,
    result = {}
  let noop = function() {}
  let max = opts.maxAttempts || Infinity

  result.open = function() {
    ws = new WebSocket(url, opts.protocols || [])

    ws.onmessage = opts.onmessage || noop

    ws.onopen = function(e) {
      if (opts.onopen) {
        opts.onopen(e)
      } else {
        noop(e)
      }
      num = 0
    }

    ws.onclose = function(e) {
      if (e.code !== 1e3 && e.code !== 1001 && e.code !== 1005) {
        result.reconnect(e)
      }
      if (opts.onclose) {
        opts.onclose(e)
      } else {
        noop(e)
      }
    }

    ws.onerror = function(e) {
      if (e && e.code === 'ECONNREFUSED') {
        result.reconnect(e)
      } else {
        if (opts.onerror) {
          opts.onerror(e)
        } else {
          noop(e)
        }
      }
    }
  }

  result.reconnect = function(e) {
    if (timer && num++ < max) {
      timer = setTimeout(function() {
        if (opts.onreconnect) {
          opts.onreconnect(e)
        } else {
          noop(e)
        }
        result.open()
      }, opts.timeout || 1e3)
    } else {
      if (opts.onmaximum) {
        opts.onmaximum(e)
      } else {
        noop(e)
      }
    }
  }

  result.json = function(x) {
    ws.send(JSON.stringify(x))
  }

  result.send = function(x) {
    ws.send(x)
  }

  result.close = function(x, y) {
    timer = clearTimeout(timer)
    ws.close(x || 1e3, y)
  }

  result.open() // init

  return result
}
