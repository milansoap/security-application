import React, { useState } from "react";
import { Container, Typography, TextField, Button } from "@mui/material";
import withAuth from '../guards/withAuth'

function Dashboard() {
  const [inputValue, setInputValue] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    // TODO: handle form submission
  };

  function handleClearStorage() {
    localStorage.clear();
    window.location.reload(); // reload the page to reflect changes
  }
  
  return (
    <Container maxWidth="lg">
      <Typography variant="h4">Dashboard</Typography>
      <form onSubmit={handleSubmit}>
        <TextField
          id="input-field"
          label="Input Field"
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
        />
        <Button type="submit" variant="contained">Submit</Button>
        <button onClick={handleClearStorage}>Logout</button>
      </form>
      <Typography variant="body1">{inputValue}</Typography>
    </Container>
  );
}

export default withAuth(Dashboard);