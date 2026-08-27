import {createBrowserRouter, createRoutesFromElements, Route} from "react-router";
import App from "../App.tsx";
import ProtectedRoute from "../auth/ProtectedRoute.tsx";
import LoginPage from "../pages/LoginPage.tsx";
import GaragePage from "../pages/GaragePage.tsx";

const routeDefinitions = createRoutesFromElements(
  <Route path="/" element={<App/>}>
    <Route path="/login" element={<LoginPage/>}/>
    {/* public paths */}
    <Route element={<ProtectedRoute/>}>
      {/* protected paths */}
      <Route path="/garage" element={<GaragePage/>}/>
    </Route>
  </Route>
);

const appRouter = createBrowserRouter(routeDefinitions);

export default appRouter;
