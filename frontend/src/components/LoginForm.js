import React, { useState, useEffect } from 'react';
import {
  TextField,
  Button,
  Container,
  Typography,
  Alert,
  Box,
  CssBaseline,
} from '@mui/material';
import { useNavigate, useLocation } from 'react-router-dom';

function LoginForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    if (location.state && location.state.isRedirected) {
      setMessage('Please log in to access the dashboard.');
    } else if (location.state && location.state.isError) {
      setMessage('There was an error please try again later.');
    }
  }, [location.state]);

  const handleLogin = async (e) => {
    e.preventDefault();

    const url = 'http://localhost:8080/api/v1/auth/authenticate';

    try {
      console.log(JSON.stringify({ email, password }));
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      });

      if (response.ok) {
        const token = await response.text();
        localStorage.setItem('authToken', token);
        localStorage.setItem('userEmail', email);
        navigate('/dashboard');
      } else {
        setMessage('Invalid email or password. Please try again.');
      }
    } catch (error) {
      console.error('Error:', error);
      setMessage('There was an error please try again later.');
    }
  };

  return (
    <>
      <CssBaseline />
      <Box
        display="flex"
        flexDirection="column"
        justifyContent="center"
        alignItems="center"
        minHeight="100vh"
      >
        <Container maxWidth="xs">
          <Typography variant="h4">Login</Typography>
          {message && (
            <Alert severity="error" sx={{ mt: 2 }}>
              {message}
            </Alert>
          )}
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
              sx={{ mt: 2 }}
            >
              Login
            </Button>
          </form>
        </Container>
      </Box>
    </>
  );
}

export default LoginForm;