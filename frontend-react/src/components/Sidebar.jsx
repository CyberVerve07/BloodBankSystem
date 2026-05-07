import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../AuthContext';
import { 
  LayoutDashboard, Users, Droplets, ClipboardList, 
  Package, LogOut, Heart
} from 'lucide-react';

export default function Sidebar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const links = [
    { to: '/', icon: <LayoutDashboard size={18} />, label: 'Dashboard' },
    { to: '/donors', icon: <Users size={18} />, label: 'Donors' },
    { to: '/donations', icon: <Droplets size={18} />, label: 'Donations' },
    { to: '/inventory', icon: <Package size={18} />, label: 'Inventory' },
    { to: '/requests', icon: <ClipboardList size={18} />, label: 'Blood Requests' },
  ];

  return (
    <div className="sidebar">
      <div className="sidebar-logo">
        <div className="logo-icon">
          <Heart size={22} color="white" />
        </div>
        <div>
          <h1>BloodBank</h1>
          <span>Management System</span>
        </div>
      </div>

      <nav className="sidebar-nav">
        {links.map(link => (
          <NavLink
            key={link.to}
            to={link.to}
            end={link.to === '/'}
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
          >
            {link.icon}
            {link.label}
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-footer">
        <div className="user-info">
          <div className="user-avatar">
            {user?.fullName?.charAt(0) || 'U'}
          </div>
          <div className="user-details">
            <div className="name">{user?.fullName || 'User'}</div>
            <div className="role">{user?.role || 'STAFF'}</div>
          </div>
        </div>
        <button className="nav-link" onClick={handleLogout} style={{ marginTop: 8 }}>
          <LogOut size={18} />
          Logout
        </button>
      </div>
    </div>
  );
}
