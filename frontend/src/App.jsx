import { useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';

export default function App() {
  const { loginWithRedirect, logout, isAuthenticated, isLoading, user, getAccessTokenSilently } = useAuth0();
  const [result, setResult] = useState('');

  async function callApi() {
    setResult('Calling protected API...');
    try {
      const token = await getAccessTokenSilently();
      const response = await fetch(`${import.meta.env.VITE_API_URL}/api/risk-status`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const body = await response.text();
      if (!response.ok) throw new Error(`API returned ${response.status}${body ? `: ${body}` : ''}`);
      setResult(JSON.stringify(JSON.parse(body), null, 2));
    } catch (error) {
      setResult(`Request failed: ${error.message}`);
    }
  }

  if (isLoading) return <main><p>Loading identity service...</p></main>;
  if (!isAuthenticated) return <main><h1>Enterprise Risk Control</h1><button onClick={() => loginWithRedirect()}>Log in</button></main>;

  return <main>
    <h1>Enterprise Risk Control</h1>
    <p>Signed in as {user.name || user.email || user.sub}</p>
    <div className="actions">
      <button onClick={callApi}>Call protected API</button>
      <button onClick={() => logout({ logoutParams: { returnTo: window.location.origin } })}>Log out</button>
    </div>
    <pre aria-live="polite">{result}</pre>
  </main>;
}
