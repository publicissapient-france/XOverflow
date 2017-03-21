import React, {Component} from "react";

import Paper from "material-ui/Paper";
import {Card, CardHeader, CardTitle, CardText} from "material-ui/Card";
import Chip from "material-ui/Chip";
import {blue500, blueGrey50} from "material-ui/styles/colors";

import Message from "../message/Message";

const container_list_style = {
  message: {
    marginBottom: 10
  }
}

const styles = {
  chip: {
    margin: 4,
  },
  wrapper: {
    display: 'flex',
    flexWrap: 'wrap',
  },
};

class MessageThread extends Component {

  render() {

    const {subject, messages, tags, light = false} = this.props;

    return (
      <Paper>
        <Card>
          <CardHeader
            title={subject}
            style={{backgroundColor: blue500}}
            titleStyle={{color: blueGrey50, fontSize: '1.2em'}}
          />
        </Card>
        { typeof tags !== 'undefined' && tags.length > 0 &&
        <CardText>
          <div style={styles.wrapper}>
            {tags.map(function (tag) {
              return (
                <div key={tag}>
                  <Chip style={styles.chip}>{tag}</Chip>
                </div>
              );
            })}
          </div>
        </CardText>
        }
        { light && messages.length > 0 &&
        <div style={container_list_style.message}>
          <Message
            author={messages[0].author}
            date_publication={messages[0].date_publication}
            content={messages[0].content}
            tags={messages[0].tags}
            index={0}
          />
        </div>
        }
        {!light &&
        <div>

          <div>
            {messages.map(function (message, index) {
              return (
                <div style={container_list_style.message} key={index}>
                  <Message
                    author={message.author}
                    date_publication={message.date_publication}
                    content={message.content}
                    index={index}
                  />
                </div>
              )
            })}
          </div>
        </div>
        }
      </Paper>
    )
  }

}

MessageThread.propTypes = {
  subject: React.PropTypes.string.isRequired,
  messages: React.PropTypes.array.isRequired,
  light: React.PropTypes.bool,
  tags: React.PropTypes.array,
}

export default MessageThread