const Dashboard = ({ user }) => {
  return (
    <div style={{ padding: '20px' }}>
      <h1>Welcome to SariTrack Dashboard</h1>
      <div style={{ border: '1px solid #ccc', padding: '20px', borderRadius: '8px' }}>
        <h3>User Profile</h3>
        <p><strong>Name:</strong> {user.name}</p>
        <p><strong>Email:</strong> {user.email}</p>
        <p><strong>Role:</strong> Vendor</p>
      </div>
      <button onClick={() => window.location.reload()} style={{marginTop: '20px'}}>Logout</button>
    </div>
  );
};

export default Dashboard;