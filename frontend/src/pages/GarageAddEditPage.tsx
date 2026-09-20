import {Button} from "@mui/material";
import {Link} from "react-router";

export default function GarageAddEditPage() {

  return <>
    Show a Form to add or edit a vehicle

    <Button component={Link} to="/garage" variant="outlined">Cancel</Button>
  </>
}