# FACILIO CLIENT

## Production Build Environment Variables

- (Boolean) `GENERATE_REPORT`: Generate a report with webpack bundle analyzer
- (Boolean) `GENERATE_SOURCEMAP`: Generate sourcemaps to debug in production (significantly increases build time)

## Setting up the development environment

### Configure a bash profile

https://bitbucket.org/snippets/facilio/RAKobz/bash-profile

### Install Dependecies

You will need access to the `npm` security group on AWS for this to work. Once you have access for it, add your IP address to the secuity group before proceeding.

```bash
npm install
```

### Create env file

- Create a new file named `.env.development` inside this folder.
- Paste the following into the file and save it.

```env
VUE_APP_FACILIO_BASE_URL=https://stage.facilio.in
#VUE_APP_FACILIO_BASE_URL=http://localhost:8080
#VUE_APP_FACILIO_PORT=8080
```

### Run the development server

```bash
npm run dev
```

### Run a build for production

```bash
npm run build
```

### Run your tests

```bash
npm run test:unit
```

### Run your tests in watch mode

```bash
npm run test:unit:watch
```
