import React, {Component} from "react";
import {Card, CardHeader, CardTitle, CardText} from "material-ui/Card";
import {Avatar} from 'material-ui/Avatar'
import {lightBlue500, orange500, teal500, red500, green500} from "material-ui/styles/colors";
import md5 from 'js-md5';

const colors = [lightBlue500, orange500, teal500, red500, green500];

class Message extends Component {

  render() {

    const {author, date_publication, content, index = 0} = this.props;

    let gravatarUrl = "https://www.gravatar.com/avatar/" + md5(author.email === undefined ? "" : author.email);

    let headerStyle = {
      backgroundColor: colors[index%colors.length]
    }

    return (
      <Card>
        <CardHeader
          title={author.name}
          subtitle={date_publication}
          style={headerStyle}
          avatar={gravatarUrl}
        />
        <CardText>{content}</CardText>
      </Card>
    );
  }

}

Message.propTypes = {
  author: React.PropTypes.object.isRequired,
  date_publication: React.PropTypes.string.isRequired,
  content: React.PropTypes.string.isRequired,
  index: React.PropTypes.number,
  onClick: React.PropTypes.func,
};

export default Message