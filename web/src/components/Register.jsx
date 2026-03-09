import { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

const Register = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: ''
  });
  
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Connects to your Spring Boot AuthController @PostMapping("/register")
      const response = await axios.post('http://localhost:8080/api/auth/register', formData);
      
      if (response.data === "User registered successfully!") {
        alert("Registration Successful! Please login.");
        navigate('/login'); // Automatically send them to the login page
      } else {
        alert(response.data); // Shows "Error: Email already exists!" from Java
      }
    } catch (error) {
      console.error("Registration Error:", error);
      alert("Could not connect to server.");
    }
  };

  return (
    <div className="auth-container">
      <h2>Create SariTrack Account</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Full Name:</label>
          <input 
            type="text" 
            placeholder="Juan Dela Cruz"
            onChange={(e) => setFormData({...formData, name: e.target.value})} 
            required 
          />
        </div>
        <div className="form-group">
          <label>Email:</label>
          <input 
            type="email" 
            placeholder="juan@example.com"
            onChange={(e) => setFormData({...formData, email: e.target.value})} 
            required 
          />
        </div>
        <div className="form-group">
          <label>Password:</label>
          <input 
            type="password" 
            placeholder="••••••••"
            onChange={(e) => setFormData({...formData, password: e.target.value})} 
            required 
          />
        </div>
        <button type="submit">Register</button>
      </form>
      <p>
        Already have an account? <Link to="/login">Login here</Link>
      </p>
    </div>
  );
};

export default Register;