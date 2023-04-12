import React, { useState } from "react";
import { Container, Typography, TextField, Button, Alert, Box } from "@mui/material";
import withAuth from "../guards/withAuth";
import { useNavigate } from "react-router-dom";

function Dashboard() {
  const [inputValue, setInputValue] = useState("");
  const [inputValueAdmin, setInputValueAdmin] = useState("");
  const [messageSuccess, setMessageSuccess] = useState("");
  const [messageFailed, setMessageFailed] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessageSuccess("");
    setMessageFailed("");
    const token = localStorage.getItem("authToken");
    if (token) {
      try {
        const response = await fetch(
          "http://localhost:8080/api/v1/auth/submitForm",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: token,
            },
            body: JSON.stringify({ inputValue }),
          }
        );
        if (response.ok) {
          setMessageSuccess("Form submited successfully");
        } else {
          setMessageFailed("authentication failed");
        }
      } catch (error) {
        console.error("Error:", error);
        setMessageFailed("error during submit");
      }
    } else {
      navigate("/", { state: { isError: true } });
    }
  };

  const handleAdminSubmit = async (e) => {
    e.preventDefault();
    setMessageSuccess("");
    setMessageFailed("");
    const token = localStorage.getItem("authToken");
    if (token) {
      try {
        const response = await fetch(
          "http://localhost:8080/api/v1/auth/submitFormAdmin",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: token,
            },
            body: JSON.stringify({ inputValue }),
          }
        );
        if (response.ok) {
          setMessageSuccess("Admin form submited successfully");
        } else {
          setMessageFailed("Admin authentication failed");
        }
      } catch (error) {
        console.error("Error:", error);
        setMessageFailed("error during admin form submit");
      }
    } else {
      navigate("/", { state: { isError: true } });
    }
  };

  function handleClearStorage() {
    localStorage.clear();
    window.location.reload();
  }

  return (
    <Container maxWidth="lg">
      <Typography variant="h4" align="center" sx={{ mt: 4, mb: 4 }}>
        Dashboard
      </Typography>
      <Container sx={{ mt: 4, mb: 4 }}>
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
      </Container>
      
      <form onSubmit={handleSubmit}>
      <Container sx={{ mt: 4, mb: 4 }}>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <TextField
            id="input-field"
            label="Input Field"
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            fullWidth
            variant="outlined"
          />
          <Button type="submit" variant="contained" color="primary">
            Submit
          </Button>
        </Box>
        </Container>
      </form>

      <Container sx={{ mt: 4, mb: 4 }}>
      <form onSubmit={handleAdminSubmit} sx={{ mt: 4 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <TextField
            id="input-field"
            label="Input Field Admin"
            value={inputValueAdmin}
            onChange={(e) => setInputValueAdmin(e.target.value)}
            fullWidth
            variant="outlined"
          />
          <Button type="submit" variant="contained" color="primary">
            Submit
          </Button>
        </Box>
      </form>
      </Container>


      <Button
          onClick={handleClearStorage}
          variant="outlined"
          color="error"
          sx={{ mt: 2, width: '100%' }}
        >
          Logout
        </Button>
    </Container>
  );
  
  
}

export default withAuth(Dashboard);
