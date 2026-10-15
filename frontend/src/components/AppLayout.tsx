import {Box, Drawer, List, ListItemButton, ListItemText} from "@mui/material";
import {NavLink, Outlet} from "react-router";

const navigationItems = [
  {label: "Dashboard", to: "/dashboard"},
  {label: "Garage", to: "/garage"}
];

export default function AppLayout() {
  return (
    <Box sx={{display: "flex"}}>
      <Drawer
        variant="permanent"
        sx={{width: 220, flexShrink: 0, "& .MuiDrawer-paper": {width: 220, boxSizing: "border-box"}}}
      >
        <List>
          {navigationItems.map((item) => (
            <ListItemButton
              key={item.to}
              component={NavLink}
              to={item.to}
              sx={{"&.active": {bgcolor: "action.selected"}}}
            >
              <ListItemText primary={item.label}/>
            </ListItemButton>
          ))}
        </List>
      </Drawer>
      <Box component="main" sx={{flexGrow: 1}}>
        <Outlet/>
      </Box>
    </Box>
  );
}
