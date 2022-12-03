import React, { useState, useEffect } from "react";
import axios from 'axios';

const UrlStatistic = () => {
  const [statistics, setStatistics] = useState([]);

  useEffect(() => {
    axios
      .get("http://localhost:8080/statistics")
      .then((response) => setStatistics(response.data));
  }, []);

  return (
    <>
      <h1>URL Statistics</h1>
      <ul>
        {statistics.map((statistic) => (
          <li key={statistic.short_url}>
            {statistic.first }: {statistic.second}
          </li>
        ))}
      </ul>
    </>
  );
};

export default UrlStatistic