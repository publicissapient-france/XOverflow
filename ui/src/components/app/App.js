import React, {Component} from "react";

import AppBar from "material-ui/AppBar";
import SearchBar from "../searchbar/SearchBar";
import MessageThread from '../messagethread/MessageThread'
import {Card, CardText} from "material-ui/Card";
import IconButton from "material-ui/IconButton";
import NavigationClose from "material-ui/svg-icons/image/blur-circular";
import {purple700} from "material-ui/styles/colors";

import './card.css'

const style = {
  appBar: {
    backgroundColor: purple700
  },
  searchBar: {
    paddingBottom: '40px'
  },
  result: {
    marginBottom: '20px'
  }

}

class App extends Component {

  render() {

    const {threads = [], searchClick} = this.props

    return (
      <div>
        <AppBar
          title="XOverFlow"
          iconElementLeft={<IconButton><NavigationClose /></IconButton>}
          style={style.appBar}
        />
        <div style={style.searchBar}>
          <SearchBar searchClick={searchClick} />
        </div>
        { typeof threads !== 'undefined' && threads.length > 0 &&
        threads.map(function (thread) {
          return (
            <div style={style.result} key={thread.subject}>
              <Card>
                <CardText className="card" >
                  <MessageThread
                    subject={thread.subject}
                    messages={thread.messages}
                    tags={thread.tags}
                    light={true}
                  />
                </CardText>
              </Card>
            </div>
          );
        })
        }
      </div>
    );
  }

}

App.propTypes = {
  threads: React.PropTypes.array,
  searchClick: React.PropTypes.func.isRequired
}

export default App