import React, {Component} from "react";

import Snackbar from 'material-ui/Snackbar';

import {connect} from "react-redux";
import TextField from "material-ui/TextField";
import RaisedButton from "material-ui/RaisedButton";
import {search} from "../app/app.action";

const style = {
  container: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    flex: '1 1 auto'
  },
  search_input: {
    flex: '1 1 auto'
  },
  button: {
    marginTop: '20px'
  }
}

class SearchBar extends Component {


  constructor() {
    super()
    this.state = {
      searchIsActive: false
    }
    this.input = ''
  }

  render() {

    const {searchClick} = this.props

    let onCriteriaChange = (event, newValue) => {
      this.input = String(newValue)
      this.setState({searchIsActive: this.input != 'undefined' && this.input.trim().length > 3})
    }

    let searchEvent = e => {
      e.preventDefault()
      if (this.state.searchIsActive) {
        searchClick(this.input)
        this.setState({snackOpen: true})
      }
    }


    return (
      <form onSubmit={searchEvent}>
        <div style={style.container}>
          <TextField
            floatingLabelText='Searching for a subject'
            hintText='Life, the Universe, and Everything.'
            style={style.search_input}
            onChange={onCriteriaChange}
            onTouchTap={searchEvent}
          />
          <RaisedButton
            label='Search'
            primary={true}
            style={style.button}
            onTouchTap={searchEvent}
            disabled={!this.state.searchIsActive}
          />
        </div>
      </form>
    )
  }

}

SearchBar.propTypes = {
  searchClick: React.PropTypes.func.isRequired
}

const mapStateToProps = (state, ownProps) => {
  return {}
}

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    searchClick: (criterion) => {
      dispatch(search(criterion))
    }
  }
}

SearchBar = connect(
  mapStateToProps,
  mapDispatchToProps
)(SearchBar)

export default SearchBar
