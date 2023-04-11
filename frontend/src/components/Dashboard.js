import React, { useState } from "react";
import { Container, Typography, TextField, Button, Alert } from "@mui/material";
import withAuth from '../guards/withAuth'
import { useNavigate } from 'react-router-dom';

function Dashboard() {
  const [inputValue, setInputValue] = useState("");
  const [messageSuccess, setMessageSuccess] = useState('');
  const [messageFailed, setMessageFailed] = useState('');
  const navigate = useNavigate();
  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessageSuccess('');
    setMessageFailed('');
    const token = localStorage.getItem('authToken');
    if (token) {
      try {
        const response = await fetch('http://localhost:8080/api/v1/auth/submitForm', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': token
          },
          body: JSON.stringify({ inputValue }),
        });
        if (response.ok) {
          setMessageSuccess("Form submited successfully");
        } else {
          setMessageFailed("authentication failed");
        }
      } catch (error) {
        console.error('Error:', error);
        setMessageFailed("error during submit");
      }
    } else {
      navigate('/', { state: { isError: true } });
    }
  };

  function handleClearStorage() {
    localStorage.clear();
    window.location.reload();
  }
  
  return (
    <Container maxWidth="lg">
      <Typography variant="h4">Dashboard</Typography>
      {messageSuccess && (
        <Alert severity="success" sx={{ mt: 2 }}>
          {messageSuccess}
        </Alert>
      )}
      {messageFailed && (
        <Alert severity="error" sx={{ mt: 2 }}>
          {messageFailed}
        </Alert>
      )}
      <form onSubmit={handleSubmit}>
        <TextField
          id="input-field"
          label="Input Field"
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
        />
        <Button type="submit" variant="contained">Submit</Button>
        <Button onClick={handleClearStorage}>Logout</Button>
      </form>
      <Typography variant="body1">{inputValue}</Typography>
    </Container>
  );
}

export default withAuth(Dashboard);