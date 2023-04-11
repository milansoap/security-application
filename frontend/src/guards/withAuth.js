import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function withAuth(Component) {
  return function AuthGuard(props) {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
      const checkToken = async () => {
        const token = localStorage.getItem('authToken');

        if (token) {
          try {
            const response = await fetch('http://localhost:8080/api/v1/auth/validateToken', {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json',
              },
              body: JSON.stringify({ token }),
            });

            if (response.ok) {
              const isValid = await response.json();
              setIsAuthenticated(isValid);
            } else {
              navigate('/', { state: { isRedirected: true } });
            }
          } catch (error) {
            console.error('Error:', error);
            navigate('/', { state: { isError: true } });
          }
        } else {
          navigate('/', { state: { isRedirected: true } });
        }
      };

      checkToken();
    }, [navigate]);

    return isAuthenticated ? <Component {...props} /> : null;
  };
}

export default withAuth;