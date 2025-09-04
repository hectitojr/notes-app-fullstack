import { Link, NavLink } from "react-router-dom";
export default function Header() {
  const active = ({ isActive }: { isActive: boolean }) =>
    "px-3 py-1 rounded-md " + (isActive ? "bg-slate-800 text-white" : "hover:bg-slate-200");
  return (
    <header className="border-b bg-white">
      <div className="mx-auto max-w-5xl px-4 py-3 flex items-center gap-6">
        <Link to="/" className="font-semibold">Notes App</Link>
        <nav className="flex gap-2">
          <NavLink to="/" className={active}>Notas</NavLink>
          <NavLink to="/notes/new" className={active}>Nueva</NavLink>
        </nav>
      </div>
    </header>
  );
}
