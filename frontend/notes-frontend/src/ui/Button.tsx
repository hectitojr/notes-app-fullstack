import { ButtonHTMLAttributes } from "react";
import { cls } from "../lib/utils";
type Props = ButtonHTMLAttributes<HTMLButtonElement> & { variant?: "primary" | "ghost" };
export default function Button({ className, variant = "primary", ...rest }: Props) {
  const base = "px-3 py-2 rounded-md text-sm";
  const kind = variant === "primary" ? "bg-slate-900 text-white hover:bg-slate-800" : "hover:bg-slate-200";
  return <button className={cls(base, kind, className)} {...rest} />;
}
