import React from "react";
import {render} from "react-dom";
import { Provider } from 'react-redux'
import thunkMiddleware from 'redux-thunk'
import createLogger from 'redux-logger'
import { createStore, applyMiddleware, compose } from 'redux'
import xoverflowApp from "../components/app/app.reducer";
import {receiveSearchResult} from "../components/app/app.action";

import moment from "moment";

//  Material ui
import getMuiTheme from "material-ui/styles/getMuiTheme";
import MuiThemeProvider from "material-ui/styles/MuiThemeProvider";
import injectTapEventPlugin from "react-tap-event-plugin";

//import Main from './Main';
// Our custom react component
import App from "../containers/app.container";
import "../../fonts/index.css";

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

const loggerMiddleware = createLogger()
let store = createStore(xoverflowApp, composeEnhancers(
  applyMiddleware(
    thunkMiddleware, // lets us dispatch() functions
    loggerMiddleware // neat middleware that logs actions
  )))



console.log(store.getState())

let unsubscribe = store.subscribe(() =>
  console.log(store.getState())
)

const now = moment();

const getDate = (now) => {
  return now.add(1, 'hours').format('MMMM Do YYYY, h:mm:ss a');
}


const jpascal = {
  name: 'Jean-Pascal LEBRUT',
  email: 'jpascalth@gmail.com'
}

const one_message = [{author: jpascal, date_publication: getDate(now), content: 'Youhouuuu', tags: ['DevOps', 'Front']}]

const threads = [
  {
    subject: 'The awersome subject',
    messages: one_message,
    tags: ['DevOps', 'Web']
  },
  {
    subject: 'The awersome subject 2',
    messages: one_message
  }
]

store.dispatch(receiveSearchResult('init',threads))

unsubscribe()

const muiTheme = getMuiTheme({});

// Needed for onTouchTap
// http://stackoverflow.com/a/34015469/988941
injectTapEventPlugin();

// Render the main app react component into the app div.
// For more details see: https://facebook.github.io/react/docs/top-level-api.html#react.render
render(
  <Provider store={store}>
    <MuiThemeProvider muiTheme={getMuiTheme(muiTheme)}>
      <App />
    </MuiThemeProvider>
  </Provider>, document.getElementById('app'));


