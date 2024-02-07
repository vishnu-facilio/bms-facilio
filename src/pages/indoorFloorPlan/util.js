export const getUnassignedList = list => {
  if (list && list.length) {
    return list.filter(data => {
      if (data.isOccupied === false) {
        return data
      }
      return null
    })
  }
  return []
}

export const getAssignedList = list => {
  if (list && list.length) {
    return list.filter(data => {
      if (data.isOccupied) {
        return data
      }
      return null
    })
  }
  return []
}

export const getReservalbleList = list => {
  if (list && list.length) {
    return list.filter(data => {
      if (data.isReservable) {
        return data
      }
      return null
    })
  }
  return []
}

export const getNonReservalbleList = list => {
  if (list && list.length) {
    return list.filter(data => {
      if (data.isReservable === false) {
        return data
      }
      return null
    })
  }
  return []
}

export const getBookedList = list => {
  if (list && list.length) {
    return list.filter(data => {
      if (data.isReservable === true && data.isBooked) {
        return data
      }
      return null
    })
  }
  return []
}

export const getVacantList = list => {
  if (list && list.length) {
    return list.filter(data => {
      if (data.isReservable === true && data.isBooked === false) {
        return data
      }
      return null
    })
  }
  return []
}
