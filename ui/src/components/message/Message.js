import React, {Component} from "react";

import RaisedButton from "material-ui/RaisedButton";
import TextField from 'material-ui/TextField';
import {Card, CardHeader, CardTitle, CardText, CardActions} from "material-ui/Card";
import {Avatar} from "material-ui/Avatar";
import {lightBlue500, orange500, teal500, red500, green500} from "material-ui/styles/colors";

import CodeBlock from './CodeBlock'
import ReactMarkdown from 'react-markdown';

import md5 from "js-md5";

const colors = [lightBlue500, orange500, teal500, red500, green500];

class Message extends Component {

  constructor(props) {
    super(props)
    this.state = {
      value: ""
    }
  }

  onChange = (value) => {
    console.log('Value change', value.target.value)
    this.setState({value: value.target.value});
  }

  render() {

    const {author, date_publication, content, index = 0, edit = false} = this.props
    const {value} = this.state

    let gravatarUrl = "https://www.gravatar.com/avatar/" + md5(author.email === undefined ? "" : author.email)

    let headerStyle = {
      backgroundColor: colors[index % colors.length]
    }

    return (
      <Card>
        <CardHeader
          title={author.name}
          subtitle={date_publication}
          style={headerStyle}
          avatar={gravatarUrl}
        />
        {edit &&
        <div>
          <CardText>
            <ReactMarkdown
              source={value}
              skipHtml={true}
              escapeHtml={true}
              className='result'
              renderers={{
                CodeBlock: CodeBlock
              }}

            />
          </CardText>
          <CardActions>
            <TextField
              hintText="Message Field"
              floatingLabelText="MultiLine and FloatingLabel"
              defaultValue={content}
              multiLine={true}
              fullWidth={true}
              rows={6}
              onChange={this.onChange}
            />
            <br />
            <RaisedButton label='Save' primary={true}/>
          </CardActions>
        </div>
        }
        {!edit &&
        <CardText>{content}</CardText>
        }
      </Card>
    );
  }

}

Message.propTypes = {
  author: React.PropTypes.object.isRequired,
  date_publication: React.PropTypes.string.isRequired,
  content: React.PropTypes.string.isRequired,
  index: React.PropTypes.number,
  edit: React.PropTypes.bool,
  onClick: React.PropTypes.func,
  onChange: React.PropTypes.func,
};

export default Message