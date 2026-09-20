import {createBrowserRouter, createRoutesFromElements, Route} from "react-router";
import App from "../App.tsx";
import ProtectedRoute from "../auth/ProtectedRoute.tsx";
import LoginPage, {loginAction} from "../pages/LoginPage.tsx";
import GaragePage from "../pages/GaragePage.tsx";
import RegisterPage, {registerAction} from "../pages/RegisterPage.tsx";
import GarageAddEditPage from "../pages/GarageAddEditPage.tsx";

const routeDefinitions = createRoutesFromElements(
  <Route path="/" element={<App/>}>
    <Route path="/login" element={<LoginPage/>} action={loginAction}/>
    <Route path="/register" element={<RegisterPage/>} action={registerAction}/>
    <Route element={<ProtectedRoute/>}>
      <Route path="/garage" element={<GaragePage/>}/>
      <Route path="/garage/add" element={<GarageAddEditPage/>}/>
    </Route>
  </Route>
);

const appRouter = createBrowserRouter(routeDefinitions);

export default appRouter;
