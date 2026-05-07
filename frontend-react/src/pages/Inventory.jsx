import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Package } from 'lucide-react';
import api from '../api';

export default function Inventory() {
  const [inventory, setInventory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/inventory').then(res => {
      setInventory(res.data);
      setLoading(false);
    }).catch(() => setLoading(false));
  }, []);

  const getStockClass = (units) => {
    if (units === 0) return 'critical';
    if (units <= 3) return 'low-stock';
    return '';
  };

  const getStockLabel = (units) => {
    if (units === 0) return 'OUT OF STOCK';
    if (units <= 3) return 'LOW STOCK';
    return 'IN STOCK';
  };

  return (
    <div>
      <div className="page-header">
        <div><h2>Blood Inventory</h2><p>Real-time blood stock levels</p></div>
      </div>

      {loading ? <div className="loading"><div className="spinner" /></div> : (
        <div className="inventory-grid">
          {inventory.map((item, i) => (
            <motion.div
              key={item.id}
              className={`inventory-card ${getStockClass(item.unitsAvailable)}`}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.05 }}
            >
              <div className="blood-type">{item.bloodGroup}</div>
              <div className="units">{item.unitsAvailable}</div>
              <div className="units-label">Units Available</div>
              <div style={{ marginTop: 12 }}>
                <span className={`status-badge ${item.unitsAvailable === 0 ? 'rejected' : item.unitsAvailable <= 3 ? 'pending' : 'completed'}`}>
                  <span className="status-dot" />
                  {getStockLabel(item.unitsAvailable)}
                </span>
              </div>
              <div style={{ marginTop: 16, display: 'flex', justifyContent: 'space-between', fontSize: 12, color: 'var(--text-muted)' }}>
                <span>Collected: {item.totalCollected}</span>
                <span>Used: {item.totalUsed}</span>
              </div>
            </motion.div>
          ))}
        </div>
      )}

      {!loading && inventory.length === 0 && (
        <div className="empty-state"><Package size={48} /><h3>Inventory not initialized</h3></div>
      )}
    </div>
  );
}
