import React, { useState } from "react";
import axios from "axios";
import RedirectUrl from "./RedirectUrl";

const UrlShortener = ({}) => {
  const [url, setUrl] = useState("");
  const [shortUrl, setShortUrl] = useState("");
  const [error, setError] = useState(null);
  const isValidUrl = (input) => {
    // Regular expression to match URL format
    const regex = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_+.~#?&//=]*)/;
    return regex.test(input);
  };

  const handleChange = (event) => {
    setUrl(event.target.value);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    if (!isValidUrl(url)) {
      setError('invalid url');
      return
    }
    setUrl(null);
    setError(null);
    axios.post("http://localhost:8080/shorten", null, {
      params: {
        url: url
      }
    }).then((response) => {
      if (response.status !== 200) {
        setError("failed save");
        return
      } else if (error) {
        setError(null);
      }
      // onAddUrl()
      setShortUrl(response.data);
    });
  };

  return (
    <form onSubmit={ handleSubmit }>
      <label>
        Original URL:
        <input type="text" value={ url } onChange={ handleChange }/>
      </label>
      <input type="submit" value="Shorten"/>
      { shortUrl && (
        <RedirectUrl url={ shortUrl }/>
        // <a href={ shortUrl } target="_blank" rel="noopener noreferrer">
        //   { shortUrl }
        // </a>
      ) }
      { error && <div>{ error }</div> }
    </form>
  );
};

export default UrlShortener;