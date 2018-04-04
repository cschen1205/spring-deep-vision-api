import * as axios from 'axios'

function predictImage (formData) {
  const url = `./api/classifiers/image`
  return axios.post(url, formData)
  // get data
    .then(x => x.data)
}

export { predictImage }
