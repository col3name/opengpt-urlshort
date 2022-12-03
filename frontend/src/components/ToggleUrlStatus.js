import React, { useState } from "react";

const ToggleUrlStatus = (props) => {
  console.log(props)
  const {isActiveStatus, onChangeStatus} = props;
  const [value, setValue] = useState(isActiveStatus === "0");
  console.log(value);
  return (
    <div>
      <input
        type="checkbox"
        id="toggle"
        checked={ value }
        onChange={ () => {
          onChangeStatus(!value);
          setValue((val) => !val)
        } }
      />
      <label htmlFor="toggle">{value ? 'active':'disabled'}</label>
    </div>
  );
};

export default ToggleUrlStatus;