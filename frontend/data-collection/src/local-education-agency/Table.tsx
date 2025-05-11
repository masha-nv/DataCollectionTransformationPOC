import React, { useEffect, useState } from "react";
import { api } from "../api/api";

const Table = () => {
  const [data, setData] = useState<any[]>();
  const [pageLimit, setPageLimit] = useState<number>(5);

  async function handleGetAllleasData() {
    const response = await api.get(`/lea-data?limit=${pageLimit}`);
    setData(response.data);
  }

  useEffect(() => {
    handleGetAllleasData();
  }, [pageLimit]);

  return (
    <div>
      <table>
        <thead>
          <tr>
            <td>LEANAME</td>
            <td>County</td>
            <td>LOC_ADDRESS1</td>
            <td>LOC_CITY</td>
            <td>LOC_STATE</td>
            <td>LOC_ZIPCODE</td>
          </tr>
        </thead>
        <tbody>
          {data?.map((item) => (
            <tr key={item.DistrictNCESID}>
              <td>{item.LEANAME}</td>
              <td>{item.County}</td>
              <td>{item.LOC_ADDRESS1}</td>
              <td>{item.LOC_CITY}</td>
              <td>{item.LOC_STATE}</td>
              <td>{item.LOC_ZIPCODE}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <select
        name='limit'
        id='limit'
        onChange={(e) => setPageLimit(Number(e.target.value))}>
        <option>5</option>
        <option>10</option>
        <option>20</option>
        <option>30</option>
        <option>40</option>
        <option>50</option>
        <option>60</option>
        <option>70</option>
        <option>80</option>
        <option>90</option>
        <option>100</option>
      </select>
    </div>
  );
};

export default Table;
