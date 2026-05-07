import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Users, Droplets, Package, ClipboardList } from 'lucide-react';
import { Doughnut, Bar } from 'react-chartjs-2';
import { Chart as ChartJS, ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement } from 'chart.js';
import api from '../api';

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement);

export default function Dashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/dashboard/stats').then(res => {
      setStats(res.data);
      setLoading(false);
    }).catch(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading"><div className="spinner" /></div>;

  const bloodColors = ['#dc2626','#ef4444','#f97316','#f59e0b','#22c55e','#14b8a6','#3b82f6','#8b5cf6'];

  const doughnutData = {
    labels: stats?.bloodGroupDistribution ? Object.keys(stats.bloodGroupDistribution) : [],
    datasets: [{
      data: stats?.bloodGroupDistribution ? Object.values(stats.bloodGroupDistribution) : [],
      backgroundColor: bloodColors,
      borderWidth: 0,
      hoverOffset: 8,
    }],
  };

  const inventoryData = {
    labels: stats?.inventory?.map(i => i.bloodGroup) || [],
    datasets: [{
      label: 'Units Available',
      data: stats?.inventory?.map(i => i.unitsAvailable) || [],
      backgroundColor: bloodColors.map(c => c + '33'),
      borderColor: bloodColors,
      borderWidth: 1.5,
      borderRadius: 6,
    }],
  };

  const chartOptions = {
    responsive: true,
    plugins: { legend: { labels: { color: '#9ca3af', font: { family: 'Inter' } } } },
    scales: {
      x: { ticks: { color: '#6b7280' }, grid: { color: 'rgba(255,255,255,0.04)' } },
      y: { ticks: { color: '#6b7280' }, grid: { color: 'rgba(255,255,255,0.04)' } },
    },
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h2>Dashboard</h2>
          <p>Overview of your blood bank operations</p>
        </div>
      </div>

      <div className="stats-grid">
        {[
          { icon: <Users size={22} />, value: stats?.totalDonors || 0, label: 'Total Donors', cls: 'red' },
          { icon: <Droplets size={22} />, value: stats?.totalDonations || 0, label: 'Total Donations', cls: 'green' },
          { icon: <Package size={22} />, value: stats?.totalUnitsAvailable || 0, label: 'Units Available', cls: 'blue' },
          { icon: <ClipboardList size={22} />, value: stats?.pendingRequests || 0, label: 'Pending Requests', cls: 'orange' },
        ].map((stat, i) => (
          <motion.div
            key={i}
            className="stat-card"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.1 }}
          >
            <div className={`stat-icon ${stat.cls}`}>{stat.icon}</div>
            <div className="stat-info">
              <h3>{stat.value}</h3>
              <p>{stat.label}</p>
            </div>
          </motion.div>
        ))}
      </div>

      <div className="grid-2">
        <motion.div className="chart-container" initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.3 }}>
          <h3>Donor Distribution by Blood Group</h3>
          <div style={{ maxWidth: 280, margin: '0 auto' }}>
            <Doughnut data={doughnutData} options={{ plugins: { legend: { position: 'bottom', labels: { color: '#9ca3af', padding: 12, font: { family: 'Inter' } } } } }} />
          </div>
        </motion.div>

        <motion.div className="chart-container" initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.4 }}>
          <h3>Blood Inventory Levels</h3>
          <Bar data={inventoryData} options={chartOptions} />
        </motion.div>
      </div>
    </div>
  );
}
