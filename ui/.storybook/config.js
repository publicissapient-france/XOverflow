import { configure } from '@kadira/storybook';

function loadStories() {
  require('../src/components/message/message.story');
  require('../src/components/messagethread/messagethread.story');
  require('../src/components/searchbar/searchbar.story');
  require('../src/components/app/app.story');
}

configure(loadStories, module);
