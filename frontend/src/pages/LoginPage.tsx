import {Alert, Button, Container, Stack, TextField} from "@mui/material";
import {type ActionFunctionArgs, Form, useActionData, useNavigate} from "react-router";
import api, {setAuthToken} from "../api/client.ts";
import axios from "axios";
import {useEffect} from "react";
import {useAuth} from "../auth/AuthContext.tsx";

interface LoginFormData {
  username: string;
  password: string;
}

type ApiError = {
  code: number,
  message: string,
};

export default function LoginPage() {
  const actionData = useActionData();
  const navigate = useNavigate();
  const {loginSuccess} = useAuth();
  const redirectPath = sessionStorage.getItem("redirectPath") || "/garage";

  useEffect(() => {
    if (actionData?.success) {
      loginSuccess(actionData.jwtToken, actionData.user);
      sessionStorage.removeItem("redirectPath");
      navigate(redirectPath);
    }
  }, [actionData]);

  return <Container maxWidth="sm">
    <Form method="POST">
      <Stack spacing={2}>
        <h2>Login</h2>
        <TextField label="Username" name="username" required={true}/>
        <TextField label="Password" name="password" type="password" required={true}/>
        <Button variant="contained" type="submit">Login</Button>
        {actionData?.error &&
          <Alert severity="error">
            {actionData.error}
          </Alert>
        }
      </Stack>
    </Form>
  </Container>
}

export async function loginAction({request}: ActionFunctionArgs) {
  const data = await request.formData();

  const loginData: LoginFormData = {
    username: data.get("username") as string,
    password: data.get("password") as string,
  };

  try {
    const response = await api.post("/auth/login", loginData)
    const {jwtToken, user} = response.data;
    setAuthToken(jwtToken);
    return {success: true, jwtToken, user};
  } catch (error) {
    if (axios.isAxiosError<ApiError>(error)) {
      if (error.response?.status === 401) {
        return {
          success: false,
          error: "Invalid username or password" // replace this with error message from the backend
        }
      }
      // display message on Login page instead of triggering React Router ErrorBoundary
      return {error: "An error occurred"}
    }
    throw error;
  }
}