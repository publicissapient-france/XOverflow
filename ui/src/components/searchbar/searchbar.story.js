import React from "react";
import {Provider} from "react-redux";
import createLogger from "redux-logger";
import {createStore, applyMiddleware, compose} from "redux";
import {storiesOf, action, linkTo} from "@kadira/storybook";
import getMuiTheme from "material-ui/styles/getMuiTheme";
import MuiThemeProvider from "material-ui/styles/MuiThemeProvider";
import SearchBar from "./SearchBar";
import "../../../fonts/index.css";
//import moment from 'moment';

const muiTheme = getMuiTheme({});

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

const loggerMiddleware = createLogger()
let store = createStore(composeEnhancers(
  applyMiddleware(
    loggerMiddleware // neat middleware that logs actions
  )))

storiesOf('Search', module).addDecorator((story) => (
  <Provider store={store}>
    <MuiThemeProvider muiTheme={getMuiTheme(muiTheme)}>
      { story() }
    </MuiThemeProvider>
  </Provider>
))
  .add('search bar', () => (
    <SearchBar/>
  ))