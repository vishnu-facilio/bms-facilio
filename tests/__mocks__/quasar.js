// __mocks__/quasar.js

import QIcon from './QIcon.vue'

export const mockQuasarModule = {
  QIcon,
  QCard: jest.fn(),
}

export default jest
  .fn()
  .mockImplementation(() => mockQuasarModule)
  .call()
