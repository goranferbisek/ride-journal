import {Button, Container, Stack, TextField} from "@mui/material";


function LoginForm() {
  return <Container maxWidth="sm">
    Login
    <Stack spacing={2}>
      <TextField label="Username"/>
      <TextField label="Password" type="password"/>
      <Button variant="contained">Login</Button>
    </Stack>
  </Container>
}

export default function LoginPage() {
  return <LoginForm/>
}