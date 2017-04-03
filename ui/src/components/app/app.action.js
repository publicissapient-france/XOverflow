import fetch from 'isomorphic-fetch'
import moment from "moment";

export const REQUEST_MESSAGE = 'REQUEST_MESSAGE'
export const RECEIVE_SEARCH_RESULTS = 'RECEIVE_SEARCH_RESULTS'
export const RECEIVE_SEARCH_ERRORS = 'RECEIVE_SEARCH_ERRORS'
export const DEFINE_RESULTS = 'DEFINE_RESULTS'

export function requestSearchMessage(criterion) {
  return {
    type: REQUEST_MESSAGE,
    criterion
  }
}

export function receiveSearchResult(criterion, results) {
  return {
    type: RECEIVE_SEARCH_RESULTS,
    criterion,
    results
  }
}

export function receiveSearchError(criterion, code, message) {
  return {
    type: RECEIVE_SEARCH_ERRORS,
    criterion,
    code,
    message
  }
}

export function defineResultsForCriterion(criterion) {
  return {
    type: DEFINE_RESULTS,
    criterion
  }
}

function requestMustBeFetch(state, criterion) {

  const results = state.searchResult.cache.hasOwnProperty(criterion)
  return !results
}

export function extracted(raw) {
  const res = raw.map(function (el) {
    return {
      subject: el.subject,
      messages: el.messages.map(function (message) {
        return {
          content: message.content,
          author: {
            name: message.author.username,
            email: `${message.author.username}@xoverflow.org`
          },
          date_publication: moment.utc(message.publishDate).format('DD[/]MM[/]YYYY kk[h]mm:ss')
        }
      })
    }
  })
  return res
}


export function search(criterion) {
  return (dispatch, getState) => {
    if (requestMustBeFetch(getState(), criterion)) {
      dispatch(requestSearchMessage(criterion))
      console.log(`fetch /thread/search?c=${criterion}`)
      return fetch(`/thread/search?c=${criterion}`)
        .then(function (response) {
          console.log(response)
          if (response.status == 200) {
            return response.json()
          } else {
            dispatch(receiveSearchError(criterion, response.status, response.statusText))
          }
        })
        .then(function (json) {
          dispatch(receiveSearchResult(criterion,  extracted(json)))
        })
        .catch(function (ex) {
          console.log('an error occur while trying to complete a search.', ex)
          dispatch(receiveSearchError(criterion, 600, 'An error occur while trying to complete a search.'))
        })
    } else {
      dispatch(defineResultsForCriterion(criterion))
    }
  }
}


