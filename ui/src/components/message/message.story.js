import React from "react";
import {Provider} from "react-redux";
//import thunkMiddleware from 'redux-thunk'
import createLogger from "redux-logger";
import {createStore, applyMiddleware, compose} from "redux";


import {storiesOf, action, linkTo} from "@kadira/storybook";
import getMuiTheme from "material-ui/styles/getMuiTheme";
import MuiThemeProvider from "material-ui/styles/MuiThemeProvider";
import Message from "./Message";
import injectTapEventPlugin from "react-tap-event-plugin";
import "../../../fonts/index.css";
import moment from "moment";

injectTapEventPlugin();

const muiTheme = getMuiTheme({});

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

const loggerMiddleware = createLogger()
let store = createStore(composeEnhancers(
  applyMiddleware(
    loggerMiddleware // neat middleware that logs actions
  )))


console.log(store.getState())

storiesOf('Messages', module).addDecorator((story) => (
  <Provider store={store}>
    <MuiThemeProvider muiTheme={getMuiTheme(muiTheme)}>
      { story() }
    </MuiThemeProvider>
  </Provider>
))
  .add('only one message', () => (
    <Message author={jpascal} date_publication={getDate(now)} content={lipsum}/>
  ))
  .add('message with author without mail', () => (
    <Message author={author_without_mail} date_publication={getDate(now)} content={lipsum}/>
  ))

const jpascal = {
  name: 'Jean-Pascal LEBRUT',
  email: 'jpascalth@gmail.com'
}
const author_without_mail = {
  name: 'John Doe'
}

const now = moment();

const getDate = (now) => {
  return now.add(1, 'hours').format('MMMM Do YYYY, h:mm:ss a');
}

const lipsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. \
Donec mattis pretium massa. Aliquam erat volutpat. Nulla facilisi.\
  Donec vulputate interdum sollicitudin. Nunc lacinia auctor quam sed pellentesque.\
  Aliquam dui mauris, mattis quis lacus id, pellentesque lobortis odio.\
   Lorem ipsum dolor sit amet, consectetur adipiscing elit. \
Donec mattis pretium massa. Aliquam erat volutpat. Nulla facilisi.\
  Donec vulputate interdum sollicitudin. Nunc lacinia auctor quam sed pellentesque.\
  Aliquam dui mauris, mattis quis lacus id, pellentesque lobortis odio.\
   Lorem ipsum dolor sit amet, consectetur adipiscing elit. \
Donec mattis pretium massa. Aliquam erat volutpat. Nulla facilisi.\
  Donec vulputate interdum sollicitudin. Nunc lacinia auctor quam sed pellentesque.\
  Aliquam dui mauris, mattis quis lacus id, pellentesque lobortis odio.\
   Lorem ipsum dolor sit amet, consectetur adipiscing elit. \
Donec mattis pretium massa. Aliquam erat volutpat. Nulla facilisi.\
  Donec vulputate interdum sollicitudin. Nunc lacinia auctor quam sed pellentesque.\
  Aliquam dui mauris, mattis quis lacus id, pellentesque lobortis odio.\
   Lorem ipsum dolor sit amet, consectetur adipiscing elit. \
Donec mattis pretium massa. Aliquam erat volutpat. Nulla facilisi.\
  Donec vulputate interdum sollicitudin. Nunc lacinia auctor quam sed pellentesque.\
  Aliquam dui mauris, mattis quis lacus id, pellentesque lobortis odio.\
   Lorem ipsum dolor sit amet, consectetur adipiscing elit. \
Donec mattis pretium massa. Aliquam erat volutpat. Nulla facilisi.\
  Donec vulputate interdum sollicitudin. Nunc lacinia auctor quam sed pellentesque.\
  Aliquam dui mauris, mattis quis lacus id, pellentesque lobortis odio.\
   Lorem ipsum dolor sit amet, consectetur adipiscing elit. \
Donec mattis pretium massa. Aliquam erat volutpat. Nulla facilisi.\
  Donec vulputate interdum sollicitudin. Nunc lacinia auctor quam sed pellentesque.\
  Aliquam dui mauris, mattis quis lacus id, pellentesque lobortis odio.\
  ";