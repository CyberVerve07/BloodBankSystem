import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Plus, X, Check, XCircle, CheckCircle, ClipboardList } from 'lucide-react';
import api from '../api';

export default function Requests() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [error, setError] = useState('');
  const [form, setForm] = useState({ patientName: '', bloodGroup: '', unitsNeeded: '', hospitalName: '', contactNumber: '', priority: 'NORMAL', notes: '' });

  const bloodGroups = ['A+', 'A-', 'B+', 'B-', 'O+', 'O-', 'AB+', 'AB-'];

  const fetchRequests = () => {
    setLoading(true);
    api.get('/requests').then(res => {
      setRequests(res.data);
      setLoading(false);
    }).catch(() => setLoading(false));
  };

  useEffect(() => { fetchRequests(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await api.post('/requests', {
        ...form,
        unitsNeeded: parseInt(form.unitsNeeded),
      });
      setShowModal(false);
      setForm({ patientName: '', bloodGroup: '', unitsNeeded: '', hospitalName: '', contactNumber: '', priority: 'NORMAL', notes: '' });
      fetchRequests();
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to create request');
    }
  };

  const handleAction = async (id, action, reason) => {
    try {
      if (action === 'reject') {
        const r = prompt('Rejection reason:');
        if (r === null) return;
        await api.put(`/requests/${id}/reject`, { reason: r });
      } else {
        await api.put(`/requests/${id}/${action}`);
      }
      fetchRequests();
    } catch (err) {
      alert(err.response?.data?.error || `Failed to ${action} request`);
    }
  };

  return (
    <div>
      <div className="page-header">
        <div><h2>Blood Requests</h2><p>Emergency blood request management</p></div>
        <button className="btn btn-primary" onClick={() => { setError(''); setShowModal(true); }}><Plus size={16} /> New Request</button>
      </div>

      {loading ? <div className="loading"><div className="spinner" /></div> : (
        <motion.div className="table-container" initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
          <table>
            <thead><tr><th>Patient</th><th>Blood Group</th><th>Units</th><th>Hospital</th><th>Priority</th><th>Status</th><th>Date</th><th>Actions</th></tr></thead>
            <tbody>
              {requests.length === 0 ? (
                <tr><td colSpan={8}><div className="empty-state"><ClipboardList size={48} /><h3>No blood requests</h3></div></td></tr>
              ) : requests.map(req => (
                <tr key={req.id}>
                  <td style={{ fontWeight: 600 }}>{req.patientName}</td>
                  <td><span className="blood-badge">{req.bloodGroup}</span></td>
                  <td>{req.unitsNeeded}</td>
                  <td>{req.hospitalName}</td>
                  <td><span className={`priority-badge ${req.priority?.toLowerCase()}`}>{req.priority}</span></td>
                  <td><span className={`status-badge ${req.status?.toLowerCase()}`}><span className="status-dot" />{req.status}</span></td>
                  <td style={{ fontSize: 13, color: 'var(--text-secondary)' }}>{new Date(req.requestDate).toLocaleDateString()}</td>
                  <td>
                    {req.status === 'PENDING' && (
                      <div className="action-btns">
                        <button className="btn btn-success btn-sm" onClick={() => handleAction(req.id, 'approve')} title="Approve"><Check size={14} /></button>
                        <button className="btn btn-danger btn-sm" onClick={() => handleAction(req.id, 'reject')} title="Reject"><XCircle size={14} /></button>
                      </div>
                    )}
                    {req.status === 'APPROVED' && (
                      <button className="btn btn-success btn-sm" onClick={() => handleAction(req.id, 'fulfill')} title="Fulfill"><CheckCircle size={14} /> Fulfill</button>
                    )}
                    {(req.status === 'FULFILLED' || req.status === 'REJECTED') && <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>—</span>}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </motion.div>
      )}

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <motion.div className="modal" onClick={e => e.stopPropagation()} initial={{ scale: 0.95, opacity: 0 }} animate={{ scale: 1, opacity: 1 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
              <h3 style={{ margin: 0 }}>New Blood Request</h3>
              <button onClick={() => setShowModal(false)} style={{ background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}><X size={20} /></button>
            </div>
            {error && <div className="error-msg">{error}</div>}
            <form onSubmit={handleSubmit}>
              <div className="form-grid">
                <div className="form-group"><label>Patient Name</label><input className="form-input" value={form.patientName} onChange={e => setForm({...form, patientName: e.target.value})} required /></div>
                <div className="form-group">
                  <label>Blood Group</label>
                  <select className="form-select" value={form.bloodGroup} onChange={e => setForm({...form, bloodGroup: e.target.value})} required>
                    <option value="">Select</option>
                    {bloodGroups.map(bg => <option key={bg} value={bg}>{bg}</option>)}
                  </select>
                </div>
              </div>
              <div className="form-grid">
                <div className="form-group"><label>Units Needed</label><input className="form-input" type="number" min="1" value={form.unitsNeeded} onChange={e => setForm({...form, unitsNeeded: e.target.value})} required /></div>
                <div className="form-group"><label>Hospital</label><input className="form-input" value={form.hospitalName} onChange={e => setForm({...form, hospitalName: e.target.value})} required /></div>
              </div>
              <div className="form-grid">
                <div className="form-group"><label>Contact</label><input className="form-input" value={form.contactNumber} onChange={e => setForm({...form, contactNumber: e.target.value})} /></div>
                <div className="form-group">
                  <label>Priority</label>
                  <select className="form-select" value={form.priority} onChange={e => setForm({...form, priority: e.target.value})}>
                    <option value="LOW">Low</option>
                    <option value="NORMAL">Normal</option>
                    <option value="HIGH">High</option>
                    <option value="CRITICAL">Critical</option>
                  </select>
                </div>
              </div>
              <div className="form-group"><label>Notes</label><input className="form-input" value={form.notes} onChange={e => setForm({...form, notes: e.target.value})} placeholder="Additional notes" /></div>
              <div className="modal-actions">
                <button className="btn btn-secondary" type="button" onClick={() => setShowModal(false)}>Cancel</button>
                <button className="btn btn-primary" type="submit">Submit Request</button>
              </div>
            </form>
          </motion.div>
        </div>
      )}
    </div>
  );
}
