export default function ({ $axios, redirect }) {
    $axios.setHeader('Content-Type', 'application/x-www-form-urlencoded', [
        'post'
    ]);
    $axios.setHeader('Access-Control-Allow-Origin', '*');
    $axios.setHeader('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS');
    $axios.setHeader('Access-Control-Allow-Headers', '*');
    $axios.setHeader('Access-Control-Allow-Origin', 'Content-Type, Authorization, Content-Length, X-Requested-With');

    $axios.onError(error => {
      if(error.code === 500) {
        redirect('/sorry')
      }
    })
  }