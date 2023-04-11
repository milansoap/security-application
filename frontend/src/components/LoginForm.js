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
  
    if (!email.trim().length) {
      setMessage("Please insert email");
      return;
    }
  
    if (!password.trim().length) {
      setMessage("Please insert Password");
      return;
    }
  
    if (password.length < 8) {
      setMessage('Password must be at least 8 characters');
      return;
    }
    
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!regex.test(email)) {
      setMessage('Invalid email format');
      return;
    }
  
    const url = 'http://localhost:8080/api/v1/auth/authenticate';
  
    try {
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
        navigate('/dashboard');
      } else if (response.status === 401) {
        const errorData = await response.text();
        console.log(errorData)
        if (errorData.includes('Account is locked')) {
          setMessage("Your account has been blocked. Please try again later");
        } else {
          setMessage('Invalid credentials. Please try again.');
        }
      } else {
        setMessage('There was an error please try again later.');
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