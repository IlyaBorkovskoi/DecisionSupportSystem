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

    private String[] criteriaNames = {
            "Стоимость проживания",
            "Доступность транспорта",
            "Качество инфраструктуры",
            "Уровень безопасности",
            "Достопримечательности и культурные объекты",
            "Климат и погода"
    };

    public CriteriaPanel() {
        initialize();
    }

    private void initialize() {
        panel = new JPanel(new BorderLayout());

        // Панель ввода критериев
        JPanel criteriaInputPanel = new JPanel(new GridLayout(criteriaNames.length, 2));
        criteriaFields = new JTextField[criteriaNames.length];
        for (int i = 0; i < criteriaNames.length; i++) {
            criteriaFields[i] = new JTextField();
            criteriaInputPanel.add(new JLabel(criteriaNames[i] + ":"));
            criteriaInputPanel.add(criteriaFields[i]);
        }

        // Таблица для матрицы попарного сравнения
        comparisonTableModel = new DefaultTableModel(criteriaNames.length, criteriaNames.length);
        comparisonTable = new JTable(comparisonTableModel);
        comparisonTable.setDefaultRenderer(Object.class, new CustomTableCellRenderer());

        // Кнопки
        calculateButton = new JButton("Рассчитать данные критерий");
        calculateButton.addActionListener(e -> calculateCriteria());

        resetButton = new JButton("Сбросить рассчитанные данные");
        resetButton.addActionListener(e -> resetComparisonMatrix());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(calculateButton);
        buttonPanel.add(resetButton);

        // Добавление компонентов на панель
        panel.add(criteriaInputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(comparisonTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    }

    public JPanel getPanel() {
        return panel;
    }

    private void calculateCriteria() {
        for (JTextField field : criteriaFields) {
            if (field.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Пожалуйста, введите все критерии.");
                return;
            }
        }

        calculateWeights();
        if (criteriaWeights == null) {
            JOptionPane.showMessageDialog(panel, "Ошибка при расчёте весов. Пожалуйста, проверьте введенные данные.");
            return;
        }

        updateComparisonTableHeaders();
        fillComparisonMatrix();
        JOptionPane.showMessageDialog(panel, "Данные критериев рассчитаны.");
    }

    private void updateComparisonTableHeaders() {
        for (int i = 0; i < criteriaNames.length; i++) {
            String header = criteriaNames[i];
            comparisonTable.getColumnModel().getColumn(i).setHeaderValue(header);
        }
        comparisonTable.getTableHeader().repaint();
    }

    private void fillComparisonMatrix() {
        DecimalFormat df = new DecimalFormat("#.####");

        for (int i = 0; i < criteriaWeights.length; i++) {
            for (int j = 0; j < criteriaWeights.length; j++) {
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
        double sum = 0;

        for (int i = 0; i < criteriaFields.length; i++) {
            try {
                criteriaWeights[i] = Double.parseDouble(criteriaFields[i].getText());
                sum += criteriaWeights[i];
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(panel, "Ошибка в данных: неверный формат значения критерия.");
                criteriaWeights = null;
                return;
            }
        }

        if (sum != 0) {
            for (int i = 0; i < criteriaWeights.length; i++) {
                criteriaWeights[i] /= sum;
            }
        }
    }

    private void resetComparisonMatrix() {
        comparisonTableModel.setRowCount(0);
        comparisonTableModel.setRowCount(criteriaNames.length);
        comparisonTableModel.setColumnCount(criteriaNames.length);
        criteriaWeights = null;  // Сброс весов
        for (int i = 0; i < criteriaNames.length; i++) {
            for (int j = 0; j < criteriaNames.length; j++) {
                comparisonTableModel.setValueAt(null, i, j);
            }
        }
    }

    public double[] getCriteriaWeights() {
        return criteriaWeights;
    }

    private static class CustomTableCellRenderer extends DefaultTableCellRenderer {
        private final DecimalFormat df = new DecimalFormat("#.####");

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