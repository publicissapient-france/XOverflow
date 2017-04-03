import React from "react";
import {Provider} from "react-redux";
import createLogger from "redux-logger";
import {createStore, applyMiddleware, compose} from "redux";

import {storiesOf, action, linkTo} from "@kadira/storybook";
import getMuiTheme from "material-ui/styles/getMuiTheme";
import MuiThemeProvider from "material-ui/styles/MuiThemeProvider";
import "../../../fonts/index.css";
import App from "../app/App";
import moment from "moment";

const muiTheme = getMuiTheme({});

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

const loggerMiddleware = createLogger()
let store = createStore(composeEnhancers(
  applyMiddleware(
    loggerMiddleware // neat middleware that logs actions
  )))


console.log(store.getState())
let unsubscribe = store.subscribe(() =>
  console.log(store.getState())
)
unsubscribe()

const searchClick = function (e) {
  e.preventDefault()
}

storiesOf('Application', module).addDecorator((story) => (
  <Provider store={store}>
    <MuiThemeProvider muiTheme={getMuiTheme(muiTheme)}>
      { story() }
    </MuiThemeProvider>
  </Provider>
)).add('main page (on load)', () => (
  <App searchClick={searchClick} />
)).add('main page with on thread', () => (
  <App threads={[one_thread]}  searchClick={searchClick}/>
)).add('main page with two thread and lot of message', () => (
  <App threads={threads}  searchClick={searchClick}/>
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
  {author: author_without_mail, date_publication: getDate(now), content: lipsum, tags: ['DevOps', 'Front']},
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

const one_thread = {
  subject: 'The awersome subject',
  messages: one_message
}
const threads = [
  {
    subject: 'The awersome subject',
    messages: one_message,
    tags: ['DevOps', 'Web']
  },
  {
    subject: 'The awersome subject 2',
    messages: messages
  }
]
