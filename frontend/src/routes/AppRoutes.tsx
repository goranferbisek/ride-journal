import {createBrowserRouter, createRoutesFromElements, Route} from "react-router";
import App from "../App.tsx";
import ProtectedRoute from "../auth/ProtectedRoute.tsx";
import LoginPage, {loginAction} from "../pages/LoginPage.tsx";
import GaragePage from "../pages/GaragePage.tsx";

const routeDefinitions = createRoutesFromElements(
  <Route path="/" element={<App/>}>
    <Route path="/login" element={<LoginPage/>} action={loginAction}/>
    <Route element={<ProtectedRoute/>}>
      <Route path="/garage" element={<GaragePage/>}/>
    </Route>
  </Route>
);

const appRouter = createBrowserRouter(routeDefinitions);

export default appRouter;
