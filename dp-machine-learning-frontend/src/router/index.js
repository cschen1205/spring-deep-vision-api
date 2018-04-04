import Vue from 'vue'
import Router from 'vue-router'
import MachineLearningCatalogue from '@/components/MachineLearningCatalogue'
import ImageClassifier from '@/components/ImageClassifier'
import ImageSearch from '@/components/ImageSearch'
import ImageRecommender from '@/components/ImageRecommender'
import ObjectDetection from '@/components/ObjectDetection'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'MachineLearningCatalogue',
      component: MachineLearningCatalogue
    },
    {
      path: '/image_classifier',
      name: 'ImageClassifier',
      component: ImageClassifier
    },
    {
      path: '/image_search',
      name: 'ImageSearch',
      component: ImageSearch
    },
    {
      path: '/image_recommender',
      name: 'ImageRecommender',
      component: ImageRecommender
    },
    {
      path: '/object_detection',
      name: 'ObjectDetection',
      component: ObjectDetection
    }
  ]
})
