import * as actions from "./app.action"
//import configureMockStore from "redux-mock-store"
//import thunk from "redux-thunk"
//import fetchMock from "fetch-mock"
//
//const middlewares = [thunk]
//const mockStore = configureMockStore(middlewares)

let {describe, it, expect} = global;


describe('app actions', () => {
  it('should create an request action', () => {
    const criterion = 'react'
    const expectedAction = {
      type: actions.REQUEST_MESSAGE,
      criterion
    }
    expect(actions.requestSearchMessage(criterion)).toEqual(expectedAction)
  })

  it('should create a search result action', () => {
    const criterion = 'react'
    const results = ['messageA']
    const expectedAction = {
      type: actions.RECEIVE_SEARCH_RESULTS,
      criterion,
      results
    }
    expect(actions.receiveSearchResult(criterion, results)).toEqual(expectedAction)
  })

  it('should create a search error action', () => {
    const criterion = 'react'
    const code = 404
    const message = 'Not Found'
    const expectedAction = {
      type: actions.RECEIVE_SEARCH_ERRORS,
      criterion,
      code,
      message
    }
    expect(actions.receiveSearchError(criterion, code, message)).toEqual(expectedAction)
  })

  it('should create a define results action', () => {
    const criterion = 'react'
    const expectedAction = {
      type: actions.DEFINE_RESULTS,
      criterion,
    }
    expect(actions.defineResultsForCriterion(criterion)).toEqual(expectedAction)
  })
})

describe('backend json extractor', () => {
  it('should map empty input', () => {
    const input = []
    const expectedOutput = []
    expect(actions.extracted(input)).toEqual(expectedOutput)
  })

  it('should map single message', () => {
    const input = [{
      id: "AVrtvEE3DW-tPB7nh43m",
      subject: "react/redux",
      origin: "UNKNOW",
      messages: [
        {
          author: {
            email: "jpthiery@xebia.fr",
            username: "jpascal"
          },
          publishDate: 1490047223283,
          content: "Youpi"
        }
      ]
    }]
    const expectedOutput = [{
      subject: "react/redux",
      messages: [{
        author: {
          email: "jpascal@xoverflow.org",
          name: "jpascal",
        },
        content: "Youpi",
        date_publication: "20/03/2017 22h00:23"
      }]
    }]
    expect(actions.extracted(input)).toEqual(expectedOutput)
  })

  it('should map a message with origin from email', () => {
    const input = [{
      id: "AVrtvEE3DW-tPB7nh43m",
      subject: "react/redux",
      origin: "EMAIL",
      messages: [
        {
          author: {
            email: "jpthiery@xebia.fr",
            username: "jpascal"
          },
          publishDate: 1490047223283,
          content: "Youpi"
        }
      ]
    }]
    const expectedOutput = [{
      subject: "react/redux",
      messages: [{
        author: {
          email: "jpascal@xoverflow.org",
          name: "jpascal",
        },
        content: "Youpi",
        date_publication: "20/03/2017 22h00:23"
      }]
    }]
    expect(actions.extracted(input)).toEqual(expectedOutput)
  })

})

//describe('search async actions', () => {
//  beforeEach(function() {
//    fetchMock.useNonGlobalFetch(fetch);
//  })
//
//  afterEach(() => {
//    fetchMock.restore()
//  })
//
//  it('creates REQUEST_MESSAGE when search has been done', () => {
//    fetchMock.mock(`*/thread/search?c=react`, {
//        id: "AVrtvEE3DW-tPB7nh43m",
//        subject: "react/redux",
//        origin: "EMAIL",
//        messages: [
//          {
//            author: {
//              email: "jpthiery@xebia.fr",
//              username: "jpascal"
//            },
//            publishDate: 1490047223283,
//            content: "Youpi"
//          }
//        ]
//      }
//    )
//
//    const expectedActions = [
//      {type: actions.REQUEST_MESSAGE, criterion: 'react'},
//      {
//        type: actions.RECEIVE_SEARCH_RESULTS, body: [{
//        subject: "react/redux",
//        messages: [{
//          author: {
//            email: "jpascal@xoverflow.org",
//            name: "jpascal",
//          },
//          content: "Youpi",
//          date_publication: "20/03/2017 22h00:23"
//        }]
//      }]
//      }
//    ]
//    const store = mockStore({
//      searchResult: {
//        cache: []
//      }
//    })
//
//    return store.dispatch(actions.search('react'))
//      .then(() => { // return of async actions
//        expect(store.getActions()).toEqual(expectedActions)
//      })
//  })
//})
