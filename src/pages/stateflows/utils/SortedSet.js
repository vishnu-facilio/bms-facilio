import sortedIndexBy from 'lodash/sortedIndexBy'

// Sorted Set
// Set of items sorted by given value.
function SortedSet() {
  this.items = []
  this.hash = {}
  this.values = {}
  this.OPEN = 1
  this.CLOSE = 2
}

SortedSet.prototype.add = function(item, value) {
  if (this.hash[item]) {
    this.items.splice(this.items.indexOf(item), 1)
  } else {
    this.hash[item] = this.OPEN
  }

  this.values[item] = value

  let index = sortedIndexBy(
    this.items,
    item,
    function(i) {
      return this.values[i]
    }.bind(this)
  )

  this.items.splice(index, 0, item)
}

SortedSet.prototype.remove = function(item) {
  this.hash[item] = this.CLOSE
}

SortedSet.prototype.isOpen = function(item) {
  return this.hash[item] === this.OPEN
}

SortedSet.prototype.isClose = function(item) {
  return this.hash[item] === this.CLOSE
}

SortedSet.prototype.isEmpty = function() {
  return this.items.length === 0
}

SortedSet.prototype.pop = function() {
  let item = this.items.shift()
  this.remove(item)
  return item
}

export default SortedSet
