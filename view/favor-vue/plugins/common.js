import Vue from 'vue'
import VueFilterDateFormat from 'vue-filter-date-format'
import moment from 'moment-timezone'

moment.tz.setDefault('Asia/Seoul')
Vue.use(VueFilterDateFormat)
Vue.prototype.$moment = moment