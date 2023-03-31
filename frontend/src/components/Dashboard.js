import React from "react";
import { Container, Typography } from "@mui/material";
import {withAuth} from '../guards/withAuth'

function Dashboard() {
  return (
    <Container maxWidth="lg">
      <Typography variant="h4">Dashboard</Typography>
      {/* Add your dashboard content here */}
    </Container>
  );
}

export default Dashboard;
