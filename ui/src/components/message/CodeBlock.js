import React, {Component} from "react";
import PureRenderMixin from "react-addons-pure-render-mixin";
import hljs from "highlight.js";

class CodeBlock extends React.Component {

  constructor(props) {
    super(props)
    hljs.initHighlightingOnLoad();
    this.displayName = 'CodeBlock'
    this.mixins = [PureRenderMixin]
  }

  componentDidMount() {
    hljs.initHighlightingOnLoad()
    this.highlightCode();
  }

  componentDidUpdate() {
    this.highlightCode();
  }

  highlightCode() {
    hljs.initHighlightingOnLoad();
    var current = React.findDOMNode(this);
    hljs.highlightBlock(current);
  }

  render() {
    return (
      <pre>
        <code>
          {this.props.literal}
        </code>
      </pre>
    );
  }

}

CodeBlock.PropTypes = {
  literal: React.PropTypes.string,
  language: React.PropTypes.string
}

export default CodeBlock