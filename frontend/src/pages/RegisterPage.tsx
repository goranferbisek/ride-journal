import {Alert, Button, Container, Stack, TextField} from "@mui/material";
import {type ActionFunctionArgs, Form, useActionData, useNavigate, data, useNavigation} from "react-router";
import api, {setAuthToken} from "../api/client.ts";
import axios from "axios";
import {useEffect} from "react";

interface RegisterFormData {
  username: string;
  password: string;
}

type ApiError = {
  code: number,
  message: string,
};

export default function RegisterPage() {
  const actionData = useActionData();
  const navigate = useNavigate();
  const navigation = useNavigation();
  const redirectPath = "/login";

  const isSubmitting = navigation.state === "submitting";

  useEffect(() => {
    if (actionData?.success) {
      navigate(redirectPath);
    }
  }, [actionData]);

  return <Container maxWidth="sm">
    <Form method="POST">
      <Stack spacing={2}>
        <h2>Register</h2>
        <TextField label="Username" name="username" error={!!actionData?.formErrors?.username}
                   helperText={actionData?.formErrors?.username}/>
        <TextField label="Password" name="password" type="password" error={!!actionData?.formErrors?.password}
                   helperText={actionData?.formErrors?.password}/>
        <TextField label="Confirm Password" name="confirm-password" type="password"
                   error={!!actionData?.formErrors?.confirmPassword}
                   helperText={actionData?.formErrors?.password}/>
        <Button variant="contained" type="submit" loading={isSubmitting}>
          {isSubmitting ? "Creating account..." : "Register"}
        </Button>
        {actionData?.error &&
          <Alert severity="error">
            {actionData?.formErrors?.username}
            {actionData.error}
          </Alert>
        }
      </Stack>
    </Form>
  </Container>
}

export async function registerAction({request}: ActionFunctionArgs) {
  const formData = await request.formData();

  const registerData: RegisterFormData = {
    username: formData.get("username") as string,
    password: formData.get("password") as string,
  };

  const formErrors: Partial<RegisterFormData> = {};

  if (registerData.username.length < 3 || registerData.username.length > 50) {
    formErrors.username = "Username should be from 3 to 50 characters";
  }

  if (registerData.password.length < 3) {
    // short passwords temporarily allowed for development purposes
    formErrors.password = "Password should be at least 3 characters";
  }

  if (Object.keys(formErrors).length > 0) {
    return data({formErrors}, {status: 400});
  }

  try {
    const response = await api.post("/auth/register", registerData)
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