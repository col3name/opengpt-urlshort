import React, { useState, useEffect } from "react";
import axios from 'axios';
const RedirectUrl = ({ url }) => {
  const [originalUrl, setOriginalUrl] = useState("");

  useEffect(() => {
    axios
      .get(`http://localhost:8080/${url}`)
      .then((response) => {
        setOriginalUrl(response.data)
        console.log(response)
      });
  }, [])

  const onClick = () => {
    if (originalUrl) {
      console.log(originalUrl)
      window.open(originalUrl, '_blank');
      // window.location.replace(originalUrl);
    }
  }

  return  <div>
    {originalUrl && <a href={originalUrl} onClick={onClick}>link {originalUrl}</a> }
    {url}
  </div>
};

export default RedirectUrl