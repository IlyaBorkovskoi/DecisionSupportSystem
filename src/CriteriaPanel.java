import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

public class CriteriaPanel {
    private JPanel panel;
    private JTable comparisonTable;
    private DefaultTableModel comparisonTableModel;
    private JTextField[] criteriaFields;
    private JButton calculateButton;
    private JButton resetButton;
    private double[] criteriaWeights;

    public CriteriaPanel() {
        initialize();
    }

    private void initialize() {
        panel = new JPanel(new BorderLayout());

        JPanel criteriaInputPanel = new JPanel(new GridLayout(6, 2));
        criteriaFields = new JTextField[6];
        for (int i = 0; i < 6; i++) {
            criteriaFields[i] = new JTextField();
            criteriaInputPanel.add(new JLabel("Критерий " + (i + 1) + ":"));
            criteriaInputPanel.add(criteriaFields[i]);
        }

        comparisonTableModel = new DefaultTableModel(6, 6);
        comparisonTable = new JTable(comparisonTableModel);
        comparisonTable.setDefaultRenderer(Object.class, new CustomTableCellRenderer());

        calculateButton = new JButton("Рассчитать данные критерий");
        calculateButton.addActionListener(e -> calculateCriteria());

        resetButton = new JButton("Сбросить рассчитанные данные");
        resetButton.addActionListener(e -> resetComparisonMatrix());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(calculateButton);
        buttonPanel.add(resetButton);

        panel.add(criteriaInputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(comparisonTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    }

    public JPanel getPanel() {
        return panel;
    }

    private void calculateCriteria() {
        for (int i = 0; i < criteriaFields.length; i++) {
            if (criteriaFields[i].getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Пожалуйста, введите все критерии.");
                return;
            }
        }
        updateComparisonTableHeaders();
        fillComparisonMatrix();
        calculateWeights();
        JOptionPane.showMessageDialog(panel, "Данные критериев рассчитаны.");
    }

    private void updateComparisonTableHeaders() {
        for (int i = 0; i < criteriaFields.length; i++) {
            String header = criteriaFields[i].getText();
            comparisonTable.getColumnModel().getColumn(i).setHeaderValue(header);
        }
        comparisonTable.getTableHeader().repaint();
    }

    private void fillComparisonMatrix() {
        int numCriteria = criteriaFields.length;
        DecimalFormat df = new DecimalFormat("#.##");

        for (int i = 0; i < numCriteria; i++) {
            for (int j = 0; j < numCriteria; j++) {
                if (i == j) {
                    comparisonTableModel.setValueAt("1", i, j);
                } else if (i < j) {
                    double preferenceValue = calculatePreferenceValue(i, j);
                    comparisonTableModel.setValueAt(df.format(preferenceValue), i, j);
                    comparisonTableModel.setValueAt(df.format(1 / preferenceValue), j, i);
                }
            }
        }
    }

    private double calculatePreferenceValue(int i, int j) {
        if (criteriaWeights == null || criteriaWeights.length <= i || criteriaWeights.length <= j) {
            return 1;
        }
        return criteriaWeights[i] / criteriaWeights[j];
    }

    private void calculateWeights() {
        criteriaWeights = new double[criteriaFields.length];
        for (int i = 0; i < criteriaFields.length; i++) {
            try {
                criteriaWeights[i] = Double.parseDouble(criteriaFields[i].getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(panel, "Ошибка в данных: неверный формат значения критерия.");
                return;
            }
        }
    }

    private void resetComparisonMatrix() {
        comparisonTableModel.setRowCount(0);
        comparisonTableModel.setRowCount(6);
        comparisonTableModel.setColumnCount(6);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                comparisonTableModel.setValueAt(null, i, j);
            }
        }
    }

    public double[] getCriteriaWeights() {
        return criteriaWeights;
    }

    private static class CustomTableCellRenderer extends DefaultTableCellRenderer {
        private final DecimalFormat df = new DecimalFormat("#.##");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof String) {
                try {
                    double numericValue = Double.parseDouble((String) value);
                    setText(df.format(numericValue));
                } catch (NumberFormatException ex) {
                    setText((String) value);
                }
            } else {
                setText(value != null ? value.toString() : "");
            }
            return c;
        }
    }
}
