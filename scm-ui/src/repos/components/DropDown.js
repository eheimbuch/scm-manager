// @flow

import React from "react";

type Props = {
  options: string[],
  optionSelected: string => void,
  preselectedOption?: string
};

class DropDown extends React.Component<Props> {
  render() {
    const { options, preselectedOption } = this.props;
    return (
      <div className="select">
        <select
          value={preselectedOption ? preselectedOption : ""}
          onChange={this.change}
        >
          <option key="" />
          {options.map(option => {
            return (
              <option key={option} value={option}>
                {option}
              </option>
            );
          })}
        </select>
      </div>
    );
  }

  change = (event: Event) => {
    this.props.optionSelected(event.target.value);
  };
}

export default DropDown;
