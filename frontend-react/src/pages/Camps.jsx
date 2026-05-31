import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Calendar, MapPin, Users, Phone, Award, Plus, 
  Search, Edit, CheckCircle, AlertTriangle, X, ChevronRight, UserPlus
} from 'lucide-react';
import api from '../api';

export default function Camps() {
  const [camps, setCamps] = useState([]);
  const [donors, setDonors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCampModal, setShowCampModal] = useState(false);
  const [showRegModal, setShowRegModal] = useState(false);
  
  const [selectedCamp, setSelectedCamp] = useState(null);
  const [registrations, setRegistrations] = useState([]);
  const [regLoading, setRegLoading] = useState(false);
  
  const [editCamp, setEditCamp] = useState(null);
  const [campForm, setCampForm] = useState({
    name: '', location: '', eventDate: '', startTime: '09:00 AM', endTime: '05:00 PM',
    organizer: '', contactNumber: '', capacity: 30, status: 'UPCOMING', notes: ''
  });

  const [regForm, setRegForm] = useState({ donorId: '', slotTime: 'Anytime' });
  
  const [error, setError] = useState('');
  const [regError, setRegError] = useState('');
  const [success, setSuccess] = useState('');

  const fetchCamps = async () => {
    setLoading(true);
    try {
      const res = await api.get('/camps');
      setCamps(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchDonors = async () => {
    try {
      const res = await api.get('/donors');
      setDonors(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchCamps();
    fetchDonors();
  }, []);

  const openAddCamp = () => {
    setEditCamp(null);
    setCampForm({
      name: '', location: '', eventDate: '', startTime: '09:00 AM', endTime: '05:00 PM',
      organizer: '', contactNumber: '', capacity: 30, status: 'UPCOMING', notes: ''
    });
    setError('');
    setShowCampModal(true);
  };

  const openEditCamp = (camp) => {
    setEditCamp(camp);
    setCampForm({
      name: camp.name, location: camp.location, eventDate: camp.eventDate,
      startTime: camp.startTime, endTime: camp.endTime, organizer: camp.organizer || '',
      contactNumber: camp.contactNumber || '', capacity: camp.capacity,
      status: camp.status, notes: camp.notes || ''
    });
    setError('');
    setShowCampModal(true);
  };

  const handleCampSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      if (editCamp) {
        await api.put(`/camps/${editCamp.id}`, campForm);
      } else {
        await api.post('/camps', campForm);
      }
      setShowCampModal(false);
      fetchCamps();
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to save camp');
    }
  };

  const handleDeleteCamp = async (id) => {
    if (window.confirm('Are you sure you want to delete this donation camp?')) {
      try {
        await api.delete(`/camps/${id}`);
        fetchCamps();
      } catch (err) {
        alert(err.response?.data?.error || 'Failed to delete camp');
      }
    }
  };

  // Registration management
  const openRegistrations = async (camp) => {
    setSelectedCamp(camp);
    setShowRegModal(true);
    setRegError('');
    setSuccess('');
    setRegForm({ donorId: '', slotTime: 'Anytime' });
    
    setRegLoading(true);
    try {
      const res = await api.get(`/camps/${camp.id}/registrations`);
      setRegistrations(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setRegLoading(false);
    }
  };

  const refreshRegistrations = async (campId) => {
    setRegLoading(true);
    try {
      const res = await api.get(`/camps/${campId}/registrations`);
      setRegistrations(res.data);
      // Refresh camps as well to update slots
      const campsRes = await api.get('/camps');
      setCamps(campsRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setRegLoading(false);
    }
  };

  const handleRegisterDonor = async (e) => {
    e.preventDefault();
    setRegError('');
    setSuccess('');
    if (!regForm.donorId) {
      setRegError('Please select a donor');
      return;
    }

    try {
      await api.post(`/camps/${selectedCamp.id}/register`, {
        donorId: parseInt(regForm.donorId),
        slotTime: regForm.slotTime
      });
      setSuccess('Donor registered successfully!');
      setRegForm({ donorId: '', slotTime: 'Anytime' });
      refreshRegistrations(selectedCamp.id);
    } catch (err) {
      setRegError(err.response?.data?.error || 'Failed to register donor');
    }
  };

  const handleUpdateRegStatus = async (regId, status, healthStatus) => {
    setRegError('');
    setSuccess('');
    try {
      await api.put(`/camps/registrations/${regId}`, { status, healthStatus });
      setSuccess(`Registration status updated to ${status}`);
      refreshRegistrations(selectedCamp.id);
    } catch (err) {
      setRegError(err.response?.data?.error || 'Failed to update registration status');
    }
  };

  const handleCancelRegistration = async (regId) => {
    if (window.confirm('Cancel this donor registration?')) {
      try {
        await api.delete(`/camps/registrations/${regId}`);
        setSuccess('Registration cancelled successfully');
        refreshRegistrations(selectedCamp.id);
      } catch (err) {
        setRegError(err.response?.data?.error || 'Failed to cancel registration');
      }
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'ACTIVE': return <span className="status-badge status-approved">Active</span>;
      case 'UPCOMING': return <span className="status-badge status-pending">Upcoming</span>;
      case 'COMPLETED': return <span className="status-badge status-fulfilled">Completed</span>;
      case 'CANCELLED': return <span className="status-badge status-rejected">Cancelled</span>;
      default: return <span className="status-badge">{status}</span>;
    }
  };

  const getRegStatusBadge = (status, health) => {
    if (status === 'ATTENDED') {
      return <span className="status-badge status-approved">Attended ({health})</span>;
    } else if (status === 'NOSHOW') {
      return <span className="status-badge status-rejected">Absent</span>;
    } else if (status === 'CANCELLED') {
      return <span className="status-badge status-rejected">Cancelled</span>;
    }
    return <span className="status-badge status-pending">Registered</span>;
  };

  const slotTimes = [
    'Anytime',
    '09:00 AM - 10:00 AM',
    '10:00 AM - 11:00 AM',
    '11:00 AM - 12:00 PM',
    '12:00 PM - 01:00 PM',
    '01:00 PM - 02:00 PM',
    '02:00 PM - 03:00 PM',
    '03:00 PM - 04:00 PM',
    '04:00 PM - 05:00 PM'
  ];

  return (
    <div>
      <div className="page-header">
        <div>
          <h2>Blood Donation Camps</h2>
          <p>Schedule camps, track donors, and manage slots</p>
        </div>
        <button className="btn btn-primary" onClick={openAddCamp}>
          <Plus size={16} /> Schedule Camp
        </button>
      </div>

      {loading ? (
        <div className="loading"><div className="spinner" /></div>
      ) : camps.length === 0 ? (
        <div className="empty-state">
          <Calendar size={48} />
          <h3>No camps scheduled yet</h3>
          <p>Get started by creating a new donation camp drive.</p>
          <button className="btn btn-primary" style={{ marginTop: 12 }} onClick={openAddCamp}>
            Schedule Camp
          </button>
        </div>
      ) : (
        <motion.div 
          className="camps-grid"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(360px, 1fr))',
            gap: '20px',
            marginBottom: '40px'
          }}
        >
          {camps.map(camp => (
            <motion.div 
              key={camp.id} 
              className="card"
              whileHover={{ y: -4 }}
              style={{
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between',
                padding: '24px',
                borderRadius: '12px',
                background: 'white',
                border: '1px solid var(--border-color)',
                boxShadow: 'var(--shadow-sm)'
              }}
            >
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 12 }}>
                  <h3 style={{ margin: 0, fontSize: '1.25rem', fontWeight: 600, color: 'var(--text-primary)' }}>{camp.name}</h3>
                  {getStatusBadge(camp.status)}
                </div>
                
                <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', marginBottom: 16, lineBreak: 'anywhere' }}>
                  {camp.notes || 'No extra notes provided.'}
                </p>

                <div className="camp-details-list" style={{ display: 'flex', flexDirection: 'column', gap: 10, marginBottom: 20 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                    <MapPin size={16} style={{ color: 'var(--primary)' }} />
                    <span>{camp.location}</span>
                  </div>

                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                    <Calendar size={16} style={{ color: 'var(--primary)' }} />
                    <span>{camp.eventDate} ({camp.startTime} - {camp.endTime})</span>
                  </div>

                  {camp.organizer && (
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                      <Award size={16} style={{ color: 'var(--primary)' }} />
                      <span>Organizer: {camp.organizer}</span>
                    </div>
                  )}

                  {camp.contactNumber && (
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                      <Phone size={16} style={{ color: 'var(--primary)' }} />
                      <span>Contact: {camp.contactNumber}</span>
                    </div>
                  )}

                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                    <Users size={16} style={{ color: 'var(--primary)' }} />
                    <span style={{ fontWeight: 500 }}>
                      Capacity: {camp.capacity} Slots
                    </span>
                  </div>
                </div>
              </div>

              <div style={{ display: 'flex', gap: 10, marginTop: 12, borderTop: '1px solid var(--border-color)', paddingTop: 16 }}>
                <button 
                  className="btn btn-secondary" 
                  style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6 }}
                  onClick={() => openRegistrations(camp)}
                >
                  <Users size={15} /> Registrations <ChevronRight size={14} />
                </button>
                <div style={{ display: 'flex', gap: 6 }}>
                  <button className="btn btn-secondary btn-sm" onClick={() => openEditCamp(camp)}><Edit size={14} /></button>
                  <button className="btn btn-danger btn-sm" onClick={() => handleDeleteCamp(camp.id)}><X size={14} /></button>
                </div>
              </div>
            </motion.div>
          ))}
        </motion.div>
      )}

      {/* Camp Scheduling & Edit Modal */}
      {showCampModal && (
        <div className="modal-overlay" onClick={() => setShowCampModal(false)}>
          <motion.div className="modal" onClick={e => e.stopPropagation()} initial={{ scale: 0.95, opacity: 0 }} animate={{ scale: 1, opacity: 1 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
              <h3 style={{ margin: 0 }}>{editCamp ? 'Edit Camp Details' : 'Schedule New Camp'}</h3>
              <button onClick={() => setShowCampModal(false)} style={{ background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}><X size={20} /></button>
            </div>

            {error && <div className="error-msg" style={{ marginBottom: 16 }}>{error}</div>}

            <form onSubmit={handleCampSubmit}>
              <div className="form-group">
                <label>Camp Name</label>
                <input className="form-input" value={campForm.name} onChange={e => setCampForm({...campForm, name: e.target.value})} required placeholder="e.g. Annual Metro Station Donation Camp" />
              </div>

              <div className="form-group">
                <label>Location / Address</label>
                <input className="form-input" value={campForm.location} onChange={e => setCampForm({...campForm, location: e.target.value})} required placeholder="e.g. Metro Station Block B Hall" />
              </div>

              <div className="form-grid">
                <div className="form-group">
                  <label>Event Date</label>
                  <input className="form-input" type="date" value={campForm.eventDate} onChange={e => setCampForm({...campForm, eventDate: e.target.value})} required />
                </div>
                <div className="form-group">
                  <label>Slots Capacity</label>
                  <input className="form-input" type="number" value={campForm.capacity} onChange={e => setCampForm({...campForm, capacity: parseInt(e.target.value)})} min="1" required />
                </div>
              </div>

              <div className="form-grid">
                <div className="form-group">
                  <label>Start Time</label>
                  <input className="form-input" value={campForm.startTime} onChange={e => setCampForm({...campForm, startTime: e.target.value})} required placeholder="e.g. 09:00 AM" />
                </div>
                <div className="form-group">
                  <label>End Time</label>
                  <input className="form-input" value={campForm.endTime} onChange={e => setCampForm({...campForm, endTime: e.target.value})} required placeholder="e.g. 05:00 PM" />
                </div>
              </div>

              <div className="form-grid">
                <div className="form-group">
                  <label>Organizer (Optional)</label>
                  <input className="form-input" value={campForm.organizer} onChange={e => setCampForm({...campForm, organizer: e.target.value})} placeholder="e.g. Indian Red Cross Society" />
                </div>
                <div className="form-group">
                  <label>Contact Number (Optional)</label>
                  <input className="form-input" value={campForm.contactNumber} onChange={e => setCampForm({...campForm, contactNumber: e.target.value})} placeholder="e.g. 9876543210" />
                </div>
              </div>

              <div className="form-grid">
                <div className="form-group">
                  <label>Status</label>
                  <select className="form-select" value={campForm.status} onChange={e => setCampForm({...campForm, status: e.target.value})}>
                    <option value="UPCOMING">Upcoming</option>
                    <option value="ACTIVE">Active (In Progress)</option>
                    <option value="COMPLETED">Completed</option>
                    <option value="CANCELLED">Cancelled</option>
                  </select>
                </div>
              </div>

              <div className="form-group">
                <label>Description / Notes (Optional)</label>
                <textarea 
                  className="form-input" 
                  value={campForm.notes} 
                  onChange={e => setCampForm({...campForm, notes: e.target.value})} 
                  placeholder="Provide registration details, free refreshments availability, cert details etc."
                  style={{ height: 80, resize: 'none', padding: '10px' }}
                />
              </div>

              <div className="modal-actions" style={{ marginTop: 24 }}>
                <button className="btn btn-secondary" type="button" onClick={() => setShowCampModal(false)}>Cancel</button>
                <button className="btn btn-primary" type="submit">{editCamp ? 'Update' : 'Schedule'} Camp</button>
              </div>
            </form>
          </motion.div>
        </div>
      )}

      {/* Camp Registrations & Attendance management Drawer Modal */}
      {showRegModal && selectedCamp && (
        <div className="modal-overlay" onClick={() => setShowRegModal(false)}>
          <motion.div 
            className="modal" 
            onClick={e => e.stopPropagation()} 
            initial={{ scale: 0.95, opacity: 0 }} 
            animate={{ scale: 1, opacity: 1 }}
            style={{ maxWidth: '800px', width: '90%' }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
              <div>
                <h3 style={{ margin: 0 }}>{selectedCamp.name}</h3>
                <p style={{ margin: '4px 0 0 0', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
                  Slot bookings and attendance tracking ({registrations.length} booked / {selectedCamp.capacity} max)
                </p>
              </div>
              <button onClick={() => setShowRegModal(false)} style={{ background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}><X size={20} /></button>
            </div>

            {regError && <div className="error-msg" style={{ marginBottom: 14 }}>{regError}</div>}
            {success && <div className="success-msg" style={{ marginBottom: 14, color: 'var(--success)', background: '#ecfdf5', padding: '10px', borderRadius: '6px', fontSize: '0.875rem', fontWeight: 500 }}>{success}</div>}

            {/* Direct registration form inside drawer */}
            {selectedCamp.status !== 'COMPLETED' && selectedCamp.status !== 'CANCELLED' && (
              <form onSubmit={handleRegisterDonor} style={{ background: '#f8fafc', padding: 16, borderRadius: 8, marginBottom: 20, border: '1px solid #e2e8f0' }}>
                <h4 style={{ margin: '0 0 12px 0', fontSize: '0.925rem', fontWeight: 600, display: 'flex', alignItems: 'center', gap: 6 }}>
                  <UserPlus size={16} style={{ color: 'var(--primary)' }} /> Book Slot / Register Donor
                </h4>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: 12, alignItems: 'flex-end' }}>
                  <div className="form-group" style={{ flex: 2, minWidth: 200, marginBottom: 0 }}>
                    <label style={{ fontSize: '0.75rem', marginBottom: 4 }}>Select Registered Donor</label>
                    <select 
                      className="form-select" 
                      value={regForm.donorId} 
                      onChange={e => setRegForm({...regForm, donorId: e.target.value})}
                      required
                    >
                      <option value="">-- Select Donor --</option>
                      {donors.map(d => (
                        <option key={d.id} value={d.id}>{d.name} ({d.bloodGroup}) - {d.contact}</option>
                      ))}
                    </select>
                  </div>

                  <div className="form-group" style={{ flex: 1, minWidth: 150, marginBottom: 0 }}>
                    <label style={{ fontSize: '0.75rem', marginBottom: 4 }}>Preferred Slot</label>
                    <select 
                      className="form-select" 
                      value={regForm.slotTime} 
                      onChange={e => setRegForm({...regForm, slotTime: e.target.value})}
                    >
                      {slotTimes.map(slot => (
                        <option key={slot} value={slot}>{slot}</option>
                      ))}
                    </select>
                  </div>

                  <button className="btn btn-primary" type="submit" style={{ height: 42 }}>Book Slot</button>
                </div>
              </form>
            )}

            {/* List of registrations */}
            <div style={{ maxHeight: '350px', overflowY: 'auto' }}>
              {regLoading ? (
                <div style={{ display: 'flex', justifyContent: 'center', padding: 30 }}><div className="spinner" /></div>
              ) : registrations.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '40px 20px', color: 'var(--text-secondary)' }}>
                  <Users size={36} style={{ margin: '0 auto 10px auto', opacity: 0.5 }} />
                  <p style={{ margin: 0 }}>No donors have registered for this camp yet.</p>
                </div>
              ) : (
                <div className="table-container" style={{ border: '1px solid var(--border-color)', boxShadow: 'none' }}>
                  <table style={{ fontSize: '0.875rem' }}>
                    <thead>
                      <tr>
                        <th>Donor</th>
                        <th>Blood Group</th>
                        <th>Slot</th>
                        <th>Status</th>
                        <th style={{ textAlign: 'right' }}>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {registrations.map(reg => (
                        <tr key={reg.id}>
                          <td>
                            <div style={{ fontWeight: 600 }}>{reg.donor.name}</div>
                            <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>{reg.donor.contact}</div>
                          </td>
                          <td><span className="blood-badge">{reg.donor.bloodGroup}</span></td>
                          <td>{reg.slotTime}</td>
                          <td>{getRegStatusBadge(reg.status, reg.healthStatus)}</td>
                          <td>
                            <div style={{ display: 'flex', gap: 6, justifyContent: 'flex-end' }}>
                              {reg.status === 'REGISTERED' && (
                                <>
                                  <button 
                                    className="btn btn-primary btn-sm" 
                                    style={{ fontSize: '0.75rem', padding: '4px 8px', background: '#10b981' }}
                                    onClick={() => handleUpdateRegStatus(reg.id, 'ATTENDED', 'ELIGIBLE')}
                                  >
                                    Attended & Eligible
                                  </button>
                                  <button 
                                    className="btn btn-secondary btn-sm" 
                                    style={{ fontSize: '0.75rem', padding: '4px 8px' }}
                                    onClick={() => handleUpdateRegStatus(reg.id, 'NOSHOW', 'PENDING')}
                                  >
                                    No Show
                                  </button>
                                </>
                              )}
                              <button 
                                className="btn btn-danger btn-sm" 
                                style={{ padding: 4 }}
                                onClick={() => handleCancelRegistration(reg.id)}
                                title="Cancel Registration"
                              >
                                <X size={13} />
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>

            <div className="modal-actions" style={{ marginTop: 20 }}>
              <button className="btn btn-secondary" type="button" onClick={() => setShowRegModal(false)}>Close</button>
            </div>
          </motion.div>
        </div>
      )}
    </div>
  );
}
