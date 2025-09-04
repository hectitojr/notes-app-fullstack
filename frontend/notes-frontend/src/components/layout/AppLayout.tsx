import { Link, NavLink, Outlet } from "react-router-dom";

export function AppLayout() {
  return (
    <div className="min-h-screen bg-slate-50 text-slate-900">
      <header className="border-b bg-white">
        <nav className="container mx-auto flex items-center gap-4 p-4">
          <Link to="/" className="font-semibold">Notes App</Link>
          <NavLink
            to="/"
            className={({ isActive }) =>
              `px-2 py-1 rounded ${isActive ? "bg-slate-900 text-white" : "hover:bg-slate-100"}`
            }
          >
            Notas
          </NavLink>
          <NavLink
            to="/new"
            className={({ isActive }) =>
              `px-2 py-1 rounded ${isActive ? "bg-slate-900 text-white" : "hover:bg-slate-100"}`
            }
          >
            Nueva
          </NavLink>
        </nav>
      </header>

      <main className="container mx-auto p-6">
        <Outlet />
      </main>
    </div>
  );
}

export default AppLayout;
