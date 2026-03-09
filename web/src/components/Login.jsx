import { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

const Login = ({ onLoginSuccess }) => {
  const [credentials, setCredentials] = useState({ email: '', password: '' });
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', credentials);
      
      if (response.data.includes("successful")) {
        onLoginSuccess({ email: credentials.email, name: response.data.split("Welcome ")[1] });
        navigate('/dashboard'); // Move to dashboard
      } else {
        alert(response.data);
      }
    } catch (error) {
      alert("Login failed. Check backend connection.");
    }
  };

  return (
    <div>
      <h2>Login to SariTrack</h2>
      <form onSubmit={handleLogin}>
        <input type="email" placeholder="Email" onChange={e => setCredentials({...credentials, email: e.target.value})} required /><br/>
        <input type="password" placeholder="Password" onChange={e => setCredentials({...credentials, password: e.target.value})} required /><br/>
        <button type="submit">Login</button>
      </form>
      <p>Need an account? <Link to="/register">Register here</Link></p>
    </div>
  );
};

export default Login;