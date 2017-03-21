import React from "react";
import {Provider} from "react-redux";
import createLogger from "redux-logger";
import {createStore, applyMiddleware, compose} from "redux";

import {storiesOf, action, linkTo} from "@kadira/storybook";

import getMuiTheme from "material-ui/styles/getMuiTheme";
import MuiThemeProvider from "material-ui/styles/MuiThemeProvider";
import "../../../fonts/index.css";

import MessageThread from "../messagethread/MessageThread";

import moment from 'moment';

const muiTheme = getMuiTheme({});

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

const loggerMiddleware = createLogger()
let store = createStore(composeEnhancers(
  applyMiddleware(
    loggerMiddleware // neat middleware that logs actions
  )))


console.log(store.getState())

storiesOf('Thread of Messages', module).addDecorator((story) => (
  <Provider store={store}>
    <MuiThemeProvider muiTheme={getMuiTheme(muiTheme)}>
      { story() }
    </MuiThemeProvider>
  </Provider>
)).
add('only one message', () => (
  <MessageThread messages={one_message} subject='Subject to process'/>
)).add('only two messages', () => (
  <MessageThread messages={two_message} subject='Subject to process'/>
)).add('only two messages with tags', () => (
  <MessageThread messages={two_message} subject='Subject to process' tags={['DevOps', 'Web']}/>
)).add('a lot of messages with tags', () => (
  <MessageThread messages={messages} subject='Subject to process' tags={['DevOps', 'Web']}/>
)).add('only one message light', () => (
  <MessageThread messages={messages} subject='Subject to process' light={true}/>
))


const jpascal = {
  name: 'Jean-Pascal LEBRUT',
  email: 'jpascalth@gmail.com'
}
const author_without_mail = {
  name: 'John Doe'
}
const antoine = {
  name: 'Antoine La GROSSEBRUTE',
  email: 'aletaxin@xebia.fr'
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

//const now = 'Toto';
const now = moment();

const getDate = (now) => {
  return now.add(1, 'hours').format('MMMM Do YYYY, h:mm:ss a');
}

const one_message = [{author: jpascal, date_publication: getDate(now), content: lipsum, tags: ['DevOps', 'Front']}]
const two_message = [
  {author: jpascal, date_publication: getDate(now), content: lipsum, tags: ['DevOps', 'Front']},
  {author: antoine, date_publication: getDate(now), content: lipsum}
]
const messages = [
  {author: jpascal, date_publication: getDate(now), content: lipsum, tags: ['DevOps', 'Front']},
  {author: antoine, date_publication: getDate(now), content: lipsum},
  {author: jpascal, date_publication: getDate(now), content: lipsum, tags: ['DevOps', 'Front']},
  {author: antoine, date_publication: getDate(now), content: lipsum},
  {author: jpascal, date_publication: getDate(now), content: lipsum, tags: ['DevOps', 'Front']},
  {author: antoine, date_publication: getDate(now), content: lipsum},
  {author: jpascal, date_publication: getDate(now), content: lipsum, tags: ['DevOps', 'Front']},
  {author: antoine, date_publication: getDate(now), content: lipsum},
  {author: jpascal, date_publication: getDate(now), content: lipsum, tags: ['DevOps', 'Front']},
  {author: antoine, date_publication: getDate(now), content: lipsum},
  {author: jpascal, date_publication: getDate(now), content: lipsum, tags: ['DevOps', 'Front']},
  {author: antoine, date_publication: getDate(now), content: lipsum},
  {author: jpascal, date_publication: getDate(now), content: lipsum, tags: ['DevOps', 'Front']},
  {author: antoine, date_publication: getDate(now), content: lipsum},
  {author: jpascal, date_publication: getDate(now), content: lipsum, tags: ['DevOps', 'Front']},
  {author: antoine, date_publication: getDate(now), content: lipsum},
]
