import React, { useState } from 'react';
import { TextField, Button, Container, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';

function LoginForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    // Replace this URL with your actual backend API endpoint
    const url = 'https://your-backend-api.com/api/login';

    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      });

      if (response.ok) {
        const data = await response.json();
        // Save the token or user data to localStorage or your state management system
        localStorage.setItem('authToken', data.token);
        navigate('/dashboard');
      } else {
        // Show an error message to the user
        console.error('Login failed.');
      }
    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
    <Container maxWidth="xs">
      <Typography variant="h4">Login</Typography>
      <form noValidate autoComplete="off" onSubmit={handleLogin}>
        <TextField
          label="Email"
          type="email"
          fullWidth
          margin="normal"
          variant="outlined"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <TextField
          label="Password"
          type="password"
          fullWidth
          margin="normal"
          variant="outlined"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <Button
          type="submit"
          variant="contained"
          color="primary"
          fullWidth
        >
          Login
        </Button>
      </form>
    </Container>
  );
}

export default LoginForm;