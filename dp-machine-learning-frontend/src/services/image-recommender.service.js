import * as axios from 'axios'

function recommendImage (formData) {
  const url = `./api/recommend/image`
  return axios.post(url, formData)
  // get data
    .then(x => x.data)
}

export { recommendImage }
