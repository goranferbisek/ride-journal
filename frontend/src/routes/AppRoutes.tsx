import {createBrowserRouter, createRoutesFromElements, Navigate, Route} from "react-router";
import App from "../App.tsx";
import ProtectedRoute from "../auth/ProtectedRoute.tsx";
import AppLayout from "../components/AppLayout.tsx";
import LoginPage, {loginAction} from "../pages/LoginPage.tsx";
import GaragePage from "../pages/GaragePage.tsx";
import RegisterPage, {registerAction} from "../pages/RegisterPage.tsx";
import GarageAddEditPage from "../pages/GarageAddEditPage.tsx";
import DashboardPage from "../pages/DashboardPage.tsx";

const routeDefinitions = createRoutesFromElements(
  <Route path="/" element={<App/>}>
    <Route path="/login" element={<LoginPage/>} action={loginAction}/>
    <Route path="/register" element={<RegisterPage/>} action={registerAction}/>
    <Route element={<ProtectedRoute/>}>
      <Route element={<AppLayout/>}>
        <Route index element={<Navigate to="/dashboard" replace/>}/>
        <Route path="/dashboard" element={<DashboardPage/>}/>
        <Route path="/garage" element={<GaragePage/>}/>
        <Route path="/garage/vehicles/new" element={<GarageAddEditPage/>}/>
        <Route path="/garage/vehicles/:id/edit" element={<GarageAddEditPage/>}/>
      </Route>
    </Route>
  </Route>
);

const appRouter = createBrowserRouter(routeDefinitions);

export default appRouter;
