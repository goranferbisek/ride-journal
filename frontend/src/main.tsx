import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import {AuthProvider} from "./auth/AuthContext.tsx";
import {createBrowserRouter, createRoutesFromElements, Route, RouterProvider} from "react-router";
import ProtectedRoute from "./auth/ProtectedRoute.tsx";
import LoginPage from "./pages/LoginPage.tsx";
import GaragePage from "./pages/GaragePage.tsx";


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

const appRouter = createBrowserRouter(routeDefinitions)

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <AuthProvider>
      <RouterProvider router={appRouter}/>
    </AuthProvider>
  </StrictMode>,
)

