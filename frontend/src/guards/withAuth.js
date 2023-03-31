import React, { useEffect } from 'react';

function withAuth(Component) {
  return function AuthGuard(props) {

   // implementacija

    return <Component {...props} />;
  };
}

export default withAuth;