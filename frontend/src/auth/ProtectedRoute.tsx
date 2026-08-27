import {Navigate, Outlet, useLocation} from "react-router";
import {useAuth} from "./AuthContext.tsx";
import {useEffect} from "react";

export default function ProtectedRoute() {
  const {isAuthenticated} = useAuth();
  const location = useLocation();

  useEffect(() => {
    if (!isAuthenticated && location.pathname !== "/login") {
      sessionStorage.setItem("redirectPath", location.pathname)
    }
  }, [isAuthenticated, location.pathname]);

  return isAuthenticated ? <Outlet/> : <Navigate to="/login"/>;
}