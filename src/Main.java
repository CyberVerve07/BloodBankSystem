import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

 class BloodBankSystem extends JFrame {

    // ================= MODEL =================
    static class Donor implements Serializable {
        UUID id;
        String name;
        String fatherName;
        String email;
        String bloodGroup;
        String contact;

        Donor(String name, String fatherName, String email, String bloodGroup, String contact) {
            this.id = UUID.randomUUID();
            this.name = name;
            this.fatherName = fatherName;
            this.email = email;
            this.bloodGroup = bloodGroup;
            this.contact = contact;
        }
    }

    // ================= DATA =================
    private ArrayList<Donor> donorList = new ArrayList<>();
    private static final String DATA_FILE = "donors.dat";

    // ================= UI =================
    private JTextField tfName, tfFather, tfEmail, tfContact, tfSearch;
    private JComboBox<String> cbBloodGroup;
    private JTable table;
    private DefaultTableModel model;
    private UUID selectedDonorId;

    public BloodBankSystem() {
        setTitle("Blood Bank Management System");
        setSize(950, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        loadData();

        // ---------- TITLE ----------
        JLabel title = new JLabel("Blood Bank Management System", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(180, 0, 0));
        add(title, BorderLayout.NORTH);

        // ---------- FORM PANEL ----------
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Donor Details"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5);
        g.fill = GridBagConstraints.HORIZONTAL;

        tfName = new JTextField(15);
        tfFather = new JTextField(15);
        tfEmail = new JTextField(15);
        tfContact = new JTextField(15);
        tfSearch = new JTextField(10);

        cbBloodGroup = new JComboBox<>(new String[]{
                "A+","A-","B+","B-","AB+","AB-","O+","O-"
        });

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnSearch = new JButton("Search");
        JButton btnShowAll = new JButton("Show All");

        int y = 0;
        g.gridx=0; g.gridy=y; form.add(new JLabel("Name"), g);
        g.gridx=1; form.add(tfName, g); y++;

        g.gridx=0; g.gridy=y; form.add(new JLabel("Father Name"), g);
        g.gridx=1; form.add(tfFather, g); y++;

        g.gridx=0; g.gridy=y; form.add(new JLabel("Email"), g);
        g.gridx=1; form.add(tfEmail, g); y++;

        g.gridx=0; g.gridy=y; form.add(new JLabel("Blood Group"), g);
        g.gridx=1; form.add(cbBloodGroup, g); y++;

        g.gridx=0; g.gridy=y; form.add(new JLabel("Contact"), g);
        g.gridx=1; form.add(tfContact, g); y++;

        g.gridx=0; g.gridy=y; form.add(btnAdd, g);
        g.gridx=1; form.add(btnUpdate, g); y++;

        g.gridx=0; g.gridy=y; form.add(btnDelete, g);

        add(form, BorderLayout.WEST);

        // ---------- TABLE ----------
        model = new DefaultTableModel(
                new String[]{"ID","Name","Father","Email","Blood","Contact"}, 0
        );
        table = new JTable(model);
        table.setRowHeight(24);
        table.removeColumn(table.getColumnModel().getColumn(0));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ---------- BOTTOM ----------
        JPanel bottom = new JPanel();
        bottom.add(new JLabel("Search Blood Group"));
        bottom.add(tfSearch);
        bottom.add(btnSearch);
        bottom.add(btnShowAll);
        add(bottom, BorderLayout.SOUTH);

        // ---------- EVENTS ----------
        btnAdd.addActionListener(e -> addDonor());
        btnUpdate.addActionListener(e -> updateDonor());
        btnDelete.addActionListener(e -> deleteDonor());
        btnSearch.addActionListener(e -> search());
        btnShowAll.addActionListener(e -> loadTable());

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int r = table.getSelectedRow();
                selectedDonorId = UUID.fromString(model.getValueAt(r,0).toString());
                tfName.setText(model.getValueAt(r,1).toString());
                tfFather.setText(model.getValueAt(r,2).toString());
                tfEmail.setText(model.getValueAt(r,3).toString());
                cbBloodGroup.setSelectedItem(model.getValueAt(r,4).toString());
                tfContact.setText(model.getValueAt(r,5).toString());
            }
        });

        loadTable();
    }

    // ================= LOGIC =================

    private void addDonor() {
        String name = tfName.getText().trim();
        String father = tfFather.getText().trim();
        String email = tfEmail.getText().trim();
        String contact = tfContact.getText().trim();

        if (name.isEmpty() || father.isEmpty() || email.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required");
            return;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Invalid email");
            return;
        }

        if (!contact.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Invalid contact number");
            return;
        }

        for (Donor d : donorList) {
            if (d.contact.equals(contact) || d.email.equalsIgnoreCase(email)) {
                JOptionPane.showMessageDialog(this, "Duplicate donor");
                return;
            }
        }

        donorList.add(new Donor(
                name, father, email,
                cbBloodGroup.getSelectedItem().toString(), contact
        ));
        saveData();
        loadTable();
        clearForm();
    }

    private void updateDonor() {
        if (selectedDonorId == null) return;
        for (Donor d : donorList) {
            if (d.id.equals(selectedDonorId)) {
                d.name = tfName.getText();
                d.fatherName = tfFather.getText();
                d.email = tfEmail.getText();
                d.bloodGroup = cbBloodGroup.getSelectedItem().toString();
                d.contact = tfContact.getText();
                break;
            }
        }
        saveData();
        loadTable();
        clearForm();
    }

    private void deleteDonor() {
        if (selectedDonorId == null) return;
        donorList.removeIf(d -> d.id.equals(selectedDonorId));
        saveData();
        loadTable();
        clearForm();
    }

    private void search() {
        String bg = tfSearch.getText().trim().toUpperCase();
        model.setRowCount(0);
        for (Donor d : donorList) {
            if (d.bloodGroup.equals(bg)) {
                model.addRow(new Object[]{
                        d.id,d.name,d.fatherName,d.email,d.bloodGroup,d.contact
                });
            }
        }
    }

    private void loadTable() {
        model.setRowCount(0);
        for (Donor d : donorList) {
            model.addRow(new Object[]{
                    d.id,d.name,d.fatherName,d.email,d.bloodGroup,d.contact
            });
        }
    }

    private void clearForm() {
        tfName.setText("");
        tfFather.setText("");
        tfEmail.setText("");
        tfContact.setText("");
        selectedDonorId = null;
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(donorList);
        } catch (Exception ignored) {}
    }

    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            donorList = (ArrayList<Donor>) ois.readObject();
        } catch (Exception ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BloodBankSystem().setVisible(true));
    }
}
