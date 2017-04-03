import { connect } from 'react-redux'
import App from '../components/app/App'

const mapStateToProps = (state, props) => {
  return {
    threads: state.searchResult['results']
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    searchClick: (criterion) => {
      console.log(`Searching ${criterion}'!`)
    }
  }
}

const AppContainer =  connect(
  mapStateToProps,
  mapDispatchToProps
)(App)

export default AppContainer