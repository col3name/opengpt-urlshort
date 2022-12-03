import axios from "axios";

export const updateUrls = () => {
  return axios
    .get("http://localhost:8080/urls")
    .then((response) => response.data);
}
