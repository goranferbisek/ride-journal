import {Button, Container, Stack, TextField} from "@mui/material";
import {type ActionFunctionArgs, Form} from "react-router";

interface LoginFormData {
  username: string;
  password: string;
};

export default function LoginPage() {
  return <Container maxWidth="sm">
    <Form method="POST">
      <Stack spacing={2}>
        <h2>Login</h2>
        <TextField label="Username" name="username"/>
        <TextField label="Password" name="password" type="password"/>
        <Button variant="contained" type="submit">Login</Button>
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

  //Send data to backend when you create an API client
}