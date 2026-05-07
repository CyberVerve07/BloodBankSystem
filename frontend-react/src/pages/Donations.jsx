import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Plus, X, Droplets } from 'lucide-react';
import api from '../api';

export default function Donations() {
  const [donations, setDonations] = useState([]);
  const [donors, setDonors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [error, setError] = useState('');
  const [form, setForm] = useState({ donorId: '', donationDate: new Date().toISOString().split('T')[0], amount: '', notes: '' });

  const fetchData = () => {
    setLoading(true);
    Promise.all([api.get('/donations'), api.get('/donors')])
      .then(([donRes, donorRes]) => {
        setDonations(donRes.data);
        setDonors(donorRes.data);
        setLoading(false);
      }).catch(() => setLoading(false));
  };

  useEffect(() => { fetchData(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await api.post('/donations', {
        donorId: parseInt(form.donorId),
        donationDate: form.donationDate,
        amount: parseInt(form.amount),
        notes: form.notes,
      });
      setShowModal(false);
      setForm({ donorId: '', donationDate: new Date().toISOString().split('T')[0], amount: '', notes: '' });
      fetchData();
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to add donation');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Delete this donation?')) {
      await api.delete(`/donations/${id}`);
      fetchData();
    }
  };

  return (
    <div>
      <div className="page-header">
        <div><h2>Donations</h2><p>Track all blood donations</p></div>
        <button className="btn btn-primary" onClick={() => { setError(''); setShowModal(true); }}><Plus size={16} /> Record Donation</button>
      </div>

      {loading ? <div className="loading"><div className="spinner" /></div> : (
        <motion.div className="table-container" initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
          <table>
            <thead><tr><th>Donor</th><th>Blood Group</th><th>Date</th><th>Amount (ml)</th><th>Status</th><th>Notes</th><th>Actions</th></tr></thead>
            <tbody>
              {donations.length === 0 ? (
                <tr><td colSpan={7}><div className="empty-state"><Droplets size={48} /><h3>No donations recorded</h3></div></td></tr>
              ) : donations.map(d => (
                <tr key={d.id}>
                  <td style={{ fontWeight: 600 }}>{d.donor?.name || 'N/A'}</td>
                  <td><span className="blood-badge">{d.bloodGroup}</span></td>
                  <td>{d.donationDate}</td>
                  <td>{d.amount} ml</td>
                  <td><span className={`status-badge ${d.status?.toLowerCase()}`}><span className="status-dot" />{d.status}</span></td>
                  <td style={{ color: 'var(--text-secondary)', maxWidth: 200, overflow: 'hidden', textOverflow: 'ellipsis' }}>{d.notes || '—'}</td>
                  <td><button className="btn btn-danger btn-sm" onClick={() => handleDelete(d.id)}>Delete</button></td>
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
              <h3 style={{ margin: 0 }}>Record New Donation</h3>
              <button onClick={() => setShowModal(false)} style={{ background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}><X size={20} /></button>
            </div>
            {error && <div className="error-msg">{error}</div>}
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Select Donor</label>
                <select className="form-select" value={form.donorId} onChange={e => setForm({...form, donorId: e.target.value})} required>
                  <option value="">Choose a donor...</option>
                  {donors.map(d => <option key={d.id} value={d.id}>{d.name} ({d.bloodGroup})</option>)}
                </select>
              </div>
              <div className="form-grid">
                <div className="form-group"><label>Date</label><input className="form-input" type="date" value={form.donationDate} onChange={e => setForm({...form, donationDate: e.target.value})} required /></div>
                <div className="form-group"><label>Amount (ml)</label><input className="form-input" type="number" min="1" max="500" value={form.amount} onChange={e => setForm({...form, amount: e.target.value})} required /></div>
              </div>
              <div className="form-group"><label>Notes</label><input className="form-input" value={form.notes} onChange={e => setForm({...form, notes: e.target.value})} placeholder="Optional notes" /></div>
              <div className="modal-actions">
                <button className="btn btn-secondary" type="button" onClick={() => setShowModal(false)}>Cancel</button>
                <button className="btn btn-primary" type="submit">Record Donation</button>
              </div>
            </form>
          </motion.div>
        </div>
      )}
    </div>
  );
}
