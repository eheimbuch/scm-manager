import { InputHTMLAttributes } from "react";

type Props = InputHTMLAttributes<HTMLInputElement> & {};

const Input = ({ className, ...props }: Props) => (
  <input
    className={`rounded-md bg-green-500 shadow-md border-2 border-blue-400 focus:ring-primary-400 ${className || ""}`}
    {...props}
  />
);

export default Input;
