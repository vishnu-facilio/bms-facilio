module.exports = {
  testEnvironment: 'jsdom',
  testMatch: [
    '<rootDir>/(tests/unit/**/*.(test|spec).(js|jsx|ts|tsx)|**/__tests__/*.(js|jsx|ts|tsx))',
    '<rootDir>/tests/integration/**/*.(test|spec).(js|jsx|ts|tsx)',
  ],
  preset: '@vue/cli-plugin-unit-jest',
  moduleFileExtensions: ['js', 'vue'],
  coverageDirectory: './coverage',
  collectCoverageFrom: ['**/src/**/*.(js|vue)', '!**/tests/**'],
  moduleDirectories: ['node_modules', '.'],
  transformIgnorePatterns: ['<rootDir>/node_modules/(?!@facilio)'],
  snapshotSerializers: ['jest-serializer-vue'],
  transform: {
    '^.+\\.(mjs|js)$': '<rootDir>/node_modules/babel-jest',
    '^.+\\.vue$': '@vue/vue2-jest',
  },
  setupFilesAfterEnv: ['<rootDir>/jest-setup.js'],
  moduleNameMapper: {
    '@facilio/router': 'node_modules/@facilio/router/index.mjs',
    '@facilio/survey': 'node_modules/@facilio/survey/dist/index.mjs',
    '@facilio/criteria': 'node_modules/@facilio/criteria/dist/index.mjs',
    '@facilio/ui/forms': 'node_modules/@facilio/ui/forms/index.mjs',
    '@facilio/ui/setup': 'node_modules/@facilio/ui/setup/index.mjs',
    '^@/(.*)$': '<rootDir>/src/components/$1',
    '^pageWidgets/(.*)$': '<rootDir>/src/components/page/widget/$1',
    '^element-ui/lib/theme-chalk/index.css$':
      '<rootDir>/tests/jest-transform-css.js',
    '^quasar$': '<rootDir>/__mocks__/quasar.js',
  },
}
