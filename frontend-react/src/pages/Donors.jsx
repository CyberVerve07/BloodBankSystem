import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Plus, Search, Edit, Trash2, UserCheck, X } from 'lucide-react';
import api from '../api';

export default function Donors() {
  const [donors, setDonors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editDonor, setEditDonor] = useState(null);
  const [error, setError] = useState('');
  const [form, setForm] = useState({ name: '', fatherName: '', email: '', bloodGroup: '', contact: '', address: '' });

  const bloodGroups = ['A+', 'A-', 'B+', 'B-', 'O+', 'O-', 'AB+', 'AB-'];

  const fetchDonors = () => {
    setLoading(true);
    const params = search ? { bloodGroup: search } : {};
    api.get('/donors', { params }).then(res => {
      setDonors(res.data);
      setLoading(false);
    }).catch(() => setLoading(false));
  };

  useEffect(() => { fetchDonors(); }, [search]);

  const openAdd = () => {
    setEditDonor(null);
    setForm({ name: '', fatherName: '', email: '', bloodGroup: '', contact: '', address: '' });
    setError('');
    setShowModal(true);
  };

  const openEdit = (donor) => {
    setEditDonor(donor);
    setForm({ name: donor.name, fatherName: donor.fatherName, email: donor.email, bloodGroup: donor.bloodGroup, contact: donor.contact, address: donor.address || '' });
    setError('');
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      if (editDonor) {
        await api.put(`/donors/${editDonor.id}`, form);
      } else {
        await api.post('/donors', form);
      }
      setShowModal(false);
      fetchDonors();
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to save donor');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this donor?')) {
      await api.delete(`/donors/${id}`);
      fetchDonors();
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h2>Donors</h2>
          <p>Manage blood donors</p>
        </div>
        <button className="btn btn-primary" onClick={openAdd}><Plus size={16} /> Add Donor</button>
      </div>

      <div style={{ marginBottom: 20, display: 'flex', gap: 12 }}>
        <div style={{ position: 'relative', flex: 1, maxWidth: 320 }}>
          <Search size={16} style={{ position: 'absolute', left: 14, top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
          <select className="form-select" value={search} onChange={e => setSearch(e.target.value)} style={{ paddingLeft: 38 }}>
            <option value="">All Blood Groups</option>
            {bloodGroups.map(bg => <option key={bg} value={bg}>{bg}</option>)}
          </select>
        </div>
      </div>

      {loading ? <div className="loading"><div className="spinner" /></div> : (
        <motion.div className="table-container" initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
          <table>
            <thead>
              <tr>
                <th>Name</th><th>Father's Name</th><th>Email</th><th>Blood Group</th><th>Contact</th><th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {donors.length === 0 ? (
                <tr><td colSpan={6}><div className="empty-state"><UserCheck size={48} /><h3>No donors found</h3><p>Add your first donor to get started</p></div></td></tr>
              ) : donors.map(donor => (
                <tr key={donor.id}>
                  <td style={{ fontWeight: 600 }}>{donor.name}</td>
                  <td>{donor.fatherName}</td>
                  <td style={{ color: 'var(--text-secondary)' }}>{donor.email}</td>
                  <td><span className="blood-badge">{donor.bloodGroup}</span></td>
                  <td>{donor.contact}</td>
                  <td>
                    <div className="action-btns">
                      <button className="btn btn-secondary btn-sm" onClick={() => openEdit(donor)}><Edit size={14} /></button>
                      <button className="btn btn-danger btn-sm" onClick={() => handleDelete(donor.id)}><Trash2 size={14} /></button>
                    </div>
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
              <h3 style={{ margin: 0 }}>{editDonor ? 'Edit Donor' : 'Add New Donor'}</h3>
              <button onClick={() => setShowModal(false)} style={{ background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}><X size={20} /></button>
            </div>
            {error && <div className="error-msg">{error}</div>}
            <form onSubmit={handleSubmit}>
              <div className="form-grid">
                <div className="form-group"><label>Full Name</label><input className="form-input" value={form.name} onChange={e => setForm({...form, name: e.target.value})} required /></div>
                <div className="form-group"><label>Father's Name</label><input className="form-input" value={form.fatherName} onChange={e => setForm({...form, fatherName: e.target.value})} required /></div>
              </div>
              <div className="form-grid">
                <div className="form-group"><label>Email</label><input className="form-input" type="email" value={form.email} onChange={e => setForm({...form, email: e.target.value})} required /></div>
                <div className="form-group"><label>Contact (10 digits)</label><input className="form-input" value={form.contact} onChange={e => setForm({...form, contact: e.target.value})} pattern="\d{10}" required /></div>
              </div>
              <div className="form-grid">
                <div className="form-group">
                  <label>Blood Group</label>
                  <select className="form-select" value={form.bloodGroup} onChange={e => setForm({...form, bloodGroup: e.target.value})} required>
                    <option value="">Select</option>
                    {bloodGroups.map(bg => <option key={bg} value={bg}>{bg}</option>)}
                  </select>
                </div>
                <div className="form-group"><label>Address</label><input className="form-input" value={form.address} onChange={e => setForm({...form, address: e.target.value})} /></div>
              </div>
              <div className="modal-actions">
                <button className="btn btn-secondary" type="button" onClick={() => setShowModal(false)}>Cancel</button>
                <button className="btn btn-primary" type="submit">{editDonor ? 'Update' : 'Add'} Donor</button>
              </div>
            </form>
          </motion.div>
        </div>
      )}
    </div>
  );
}
