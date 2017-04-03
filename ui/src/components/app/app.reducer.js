import {combineReducers} from "redux";
import {RECEIVE_SEARCH_RESULTS, REQUEST_MESSAGE, DEFINE_RESULTS} from "./app.action";

function searchResult(state = {results: [], cache: []}, action) {
  switch (action.type) {
    case RECEIVE_SEARCH_RESULTS:
      let cache = Object.assign({}, state.cache, {[action.criterion]: action.results})
      return {
        ['results']: action.results,
        cache
      }
      break
    case REQUEST_MESSAGE:
      return Object.assign({}, state, {['results']: []})
      break
    case DEFINE_RESULTS:
      return Object.assign({}, state, {['results']: state.cache[action.criterion]})
      break
    default:
      return state
  }

}

const xoverflowApp = combineReducers({
  searchResult
})

export default xoverflowApp