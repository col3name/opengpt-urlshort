import React, { useEffect, useState } from "react";
import axios from "axios";
import RedirectUrl from "./RedirectUrl";
import { updateUrls } from "../actions/urls";
import ToggleUrlStatus from "./ToggleUrlStatus";

const Error = ({error}) => {
  return  (
    <>
      { error && <div>error</div>}
    </>
  )

}
const UrlList = () => {
  const [urls, setUrls] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    updateUrls().then(data => setUrls(data))
  }, []);


  const updateError = (message) => {
    setError(message)
    setInterval(() => setError(null), 200)
  };

  const handleDelete = (url) => {
    axios
      .delete(`http://localhost:8080/delete`, {
        params: {
          url: url.short_url
        }
      })
      .then((response) => {
        if (response.status !== 200) {
          updateError("failed delete")
          return
        }
        setUrls(() => [...urls.filter(u => u.id !== url.id)]);
      });
  };

  const saveStatus = (url, value) => {
    axios.post(`http://localhost:8080/mark/${url}`, null,{
      params: {
        status: value ? "0" : "1"
      }
    }).then(response => {
      if (response.status !== 200) {
        updateError("failed change status")
      } else if (response.data.toLowerCase() !== "ok") {
        updateError(response.data)
      } else {
        const u = urls.find(u => u.id === url.id);
        u.status = value;
        setUrls(() => [...urls.filter(u => u.id !== url.id), u]);
        setError(null);
      }
    })
  };

  return (
    <ul>
      { urls.map((url) => {
        return (
          <li key={ url.id }>
            { url.status === "0" ?
              <RedirectUrl url={ url.short_url }/>
              :
              <div>{url.short_url}</div>
            }
            <button onClick={ () => handleDelete(url) }>Delete</button>
            <ToggleUrlStatus isActiveStatus={ url.status }
                             onChangeStatus={ (value) => {
                               saveStatus(url.id, value);
                             } }/>
          </li>
        );
      }) }
      <Error error={error}/>
    </ul>
  );
};
export default UrlList;