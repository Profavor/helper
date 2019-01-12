export default function ({ $axios, redirect }) {
    $axios.setHeader('Content-Type', 'application/x-www-form-urlencoded', [
        'post'
    ]);
    $axios.onError(error => {
      if(error.code === 500) {
        redirect('/sorry')
      }
    })
  }