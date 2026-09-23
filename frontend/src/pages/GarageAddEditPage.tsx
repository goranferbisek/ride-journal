import {Button, Container, Stack} from "@mui/material";
import {Link} from "react-router";

export default function GarageAddEditPage() {

  return <>
    <Container>
      Show a Form to add or edit a vehicle
      <Stack direction={"row"}>
        <Button component={Link} to="/garage" variant="outlined">Cancel</Button>
        <Button component={Link} to="/garage" variant="contained">Save</Button>
      </Stack>
    </Container>
  </>
}