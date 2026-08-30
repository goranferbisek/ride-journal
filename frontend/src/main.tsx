import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import {AuthProvider} from "./auth/AuthContext.tsx";
import {RouterProvider} from "react-router";
import appRouter from "./routes/AppRoutes.tsx";

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <AuthProvider>
      <RouterProvider router={appRouter}/>
    </AuthProvider>
  </StrictMode>,
)

