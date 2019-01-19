const pkg = require('./package')

module.exports = {
  mode: 'spa',

  env: {

  },

  /*
  ** Headers of the page
  */
  head: {
    title: pkg.name,
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      { hid: 'description', name: 'description', content: pkg.description }
    ],
    link: [
      { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }
    ],
    script: [
      {src: 'https://cdnjs.cloudflare.com/ajax/libs/jquery/3.1.1/jquery.min.js'},
      {src: '/semantic-ui/semantic.min.js'},
      {src: '/js/calendar.min.js'},
      {src: '/js/polyfill.min.js'},
      {src: '/js/common.util.js'},
    ]
  },

  /*
  ** Customize the progress-bar color
  */
  loading: { color: '#fff' },

  /*
  ** Global CSS
  */
  css: [
    '~/static/semantic-ui/semantic.min.css',
  ],

  /*
  ** Plugins to load before mounting the App
  */
  plugins: [
    '~/plugins/axios.js',
    '~/plugins/common.js'
  ],

  router: {
    middleware: ['auth'],
    linkActiveClass: 'active'
  },

  /*
  ** Nuxt.js modules
  */
  modules: [
    // Doc: https://github.com/nuxt-community/axios-module#usage
    '@nuxtjs/axios',
    '@nuxtjs/auth',
    '@nuxtjs/proxy',
    '@nuxtjs/toast',
    '@nuxt/babel-preset-app'
  ],
  /*
  ** Axios module configuration
  */
  axios: {
    // See https://github.com/nuxt-community/axios-module#options
    // proxyHeaders: true
      proxy: true
  },

  toast: {
    position: 'top-right',
    duration: 3500,
    iconPack: 'fontawesome',
    theme: 'toasted-primary'
  },

  auth: {
    strategies: {
      local: {
        endpoints: {
          login: { url: '/auth/login', method: 'post', propertyName: 'token' },
          logout: { url: '/auth/logout', method: 'post' },
          user: { url: '/api/usr', method: 'get', propertyName: 'usr',  }          
        },
        tokenRequired: true,
        tokenType: 'bearer',
      }
    },
    plugins: [ '~/plugins/auth.js' ]
  },

  proxy: {
    // Simple proxy
    '/api': 'http://helper-api-server:8001',
    '/auth': 'http://helper-api-server:8001'
  },

  /*
  ** Build configuration
  */
  build: {
    /*
    ** You can extend webpack config here
    */
   babel: {
    presets: [
      '@babel/preset-env'
    ],
    plugins: ['@babel/plugin-syntax-dynamic-import', '@babel/plugin-transform-arrow-functions', '@babel/plugin-transform-async-to-generator', '@babel/plugin-transform-runtime']
    },   

    extend(config, { ctx }) {

    }
  }
}
