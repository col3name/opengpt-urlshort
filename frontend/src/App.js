import './App.css';
import UrlShortener from "./components/UrlShortener";
import UrlList from "./components/UrlList";
import UrlStatistic from "./components/UrlStatistic";

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <UrlList/>
        <UrlShortener/>
        <UrlStatistic/>
      </header>
    </div>
  );
}

export default App;
