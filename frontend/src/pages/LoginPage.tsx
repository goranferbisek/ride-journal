import {Button, Container, Stack, TextField} from "@mui/material";
import {Form} from "react-router";


function LoginForm() {
  return <Container maxWidth="sm">
    <Form action="" method="POST">
      <Stack spacing={2}>
        <h2>Login</h2>
        <TextField label="Username"/>
        <TextField label="Password" type="password"/>
        <Button variant="contained">Login</Button>
      </Stack>
    </Form>
  </Container>
}

export default function LoginPage() {
  return <LoginForm/>
}