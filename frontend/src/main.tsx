import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import {AuthProvider} from "./auth/AuthContext.tsx";
import {RouterProvider} from "react-router";
import appRouter from "./routes/AppRoutes.tsx";
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";

let queryClient = new QueryClient();

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <RouterProvider router={appRouter}/>
      </AuthProvider>
    </QueryClientProvider>
  </StrictMode>,
)

