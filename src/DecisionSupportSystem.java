import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.text.DecimalFormat;
import java.io.*;

public class DecisionSupportSystem {
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private JPanel criteriaPanel;
    private JPanel alternativesPanel;
    private JPanel resultPanel;
    private JTable comparisonTable;
    private DefaultTableModel comparisonTableModel;
    private JTable alternativesTable;
    private DefaultTableModel alternativesTableModel;
    private JButton calculateButton;
    private JButton resetButton;
    private JTextField[] criteriaFields;
    private double[] criteriaWeights;
    private JTextArea resultArea;

    public DecisionSupportSystem() {
        frame = new JFrame("Система поддержки принятия решений");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        tabbedPane = new JTabbedPane();

        // Инициализация вкладок
        initializeCriteriaPanel();
        initializeAlternativesPanel();
        initializeResultPanel();

        tabbedPane.addTab("Критерии", criteriaPanel);
        tabbedPane.addTab("Альтернативы", alternativesPanel);
        tabbedPane.addTab("Результат", resultPanel);

        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    private void initializeCriteriaPanel() {
        criteriaPanel = new JPanel(new BorderLayout());

        // Панель для ввода критериев
        JPanel criteriaInputPanel = new JPanel(new GridLayout(6, 2));
        criteriaFields = new JTextField[6];
        for (int i = 0; i < 6; i++) {
            criteriaFields[i] = new JTextField();
            criteriaInputPanel.add(new JLabel("Критерий " + (i + 1) + ":"));
            criteriaInputPanel.add(criteriaFields[i]);
        }

        // Модель и таблица для парных сравнений
        comparisonTableModel = new DefaultTableModel(6, 6);
        comparisonTable = new JTable(comparisonTableModel);

        // Установка кастомного рендерера для форматирования чисел в таблице
        comparisonTable.setDefaultRenderer(Object.class, new CustomTableCellRenderer());

        // Кнопка для расчета данных критериев
        calculateButton = new JButton("Рассчитать данные критерий");
        calculateButton.addActionListener(e -> {
            for (int i = 0; i < 6; i++) {
                if (criteriaFields[i].getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(criteriaPanel, "Пожалуйста, введите все критерии.");
                    return;
                }
            }
            updateComparisonTableHeaders();
            fillComparisonMatrix();
            calculateWeights();
            JOptionPane.showMessageDialog(criteriaPanel, "Данные критериев рассчитаны.");
        });

        // Кнопка для сброса данных
        resetButton = new JButton("Сбросить рассчитанные данные");
        resetButton.addActionListener(e -> resetComparisonMatrix());

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(calculateButton);
        buttonPanel.add(resetButton);

        // Добавление компонентов на панель критериев
        criteriaPanel.add(criteriaInputPanel, BorderLayout.NORTH);
        criteriaPanel.add(new JScrollPane(comparisonTable), BorderLayout.CENTER);
        criteriaPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    // Кастомный рендерер для форматирования ячеек
    private static class CustomTableCellRenderer extends DefaultTableCellRenderer {
        private final DecimalFormat df = new DecimalFormat("#.##");  // Форматирование до двух знаков после запятой

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof String) {
                try {
                    double numericValue = Double.parseDouble((String) value);
                    setText(df.format(numericValue));  // Применение форматирования
                } catch (NumberFormatException ex) {
                    setText((String) value);  // Если значение не числовое, оставить как есть
                }
            } else {
                setText(value != null ? value.toString() : "");
            }
            return c;
        }
    }

    private void fillComparisonMatrix() {
        int numCriteria = criteriaFields.length;

        // Форматирование чисел до двух знаков после запятой
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

    private void initializeAlternativesPanel() {
        alternativesPanel = new JPanel(new BorderLayout());

        String[] altColumns = {"Название", "Критерий 1", "Критерий 2", "Критерий 3", "Критерий 4", "Критерий 5", "Критерий 6"};
        alternativesTableModel = new DefaultTableModel(altColumns, 0);
        alternativesTable = new JTable(alternativesTableModel);
        JScrollPane scrollPane = new JScrollPane(alternativesTable);

        JPanel addAltPanel = new JPanel(new FlowLayout());
        JTextField altNameField = new JTextField(10);
        JButton addAltButton = new JButton("Добавить альтернативу");
        addAltPanel.add(new JLabel("Название альтернативы:"));
        addAltPanel.add(altNameField);
        addAltPanel.add(addAltButton);

        alternativesPanel.add(scrollPane, BorderLayout.CENTER);
        alternativesPanel.add(addAltPanel, BorderLayout.SOUTH);

        loadAlternativesFromFile();

        addAltButton.addActionListener(e -> {
            String altName = altNameField.getText();
            if (!altName.isEmpty()) {
                Object[] row = new Object[altColumns.length];
                row[0] = altName;
                for (int i = 1; i < altColumns.length; i++) {
                    row[i] = "";
                }
                alternativesTableModel.addRow(row);
                saveAlternativeToFile(row);
                altNameField.setText("");
            }
        });
    }

    private void saveAlternativeToFile(Object[] alternativeData) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("alternatives.txt", true))) {
            for (int i = 0; i < alternativeData.length; i++) {
                writer.print(alternativeData[i] == null ? "" : alternativeData[i].toString());
                if (i < alternativeData.length - 1) {
                    writer.print(",");
                }
            }
            writer.println();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(alternativesPanel, "Ошибка при сохранении альтернативы в файл.");
        }
    }

    private void loadAlternativesFromFile() {
        File file = new File("alternatives.txt");
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                Object[] row = new Object[values.length];
                System.arraycopy(values, 0, row, 0, values.length);
                alternativesTableModel.addRow(row);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(alternativesPanel, "Ошибка при загрузке альтернатив из файла.");
        }
    }

    private JPanel initializeResultPanel() {
        resultPanel = new JPanel(new BorderLayout());
        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        JButton applyButton = new JButton("Применить");
        resultPanel.add(new JLabel("Результаты"), BorderLayout.NORTH);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        resultPanel.add(applyButton, BorderLayout.SOUTH);

        applyButton.addActionListener(e -> calculateBestAlternative());
        return resultPanel;
    }

    private void calculateBestAlternative() {
        int altCount = alternativesTableModel.getRowCount();
        int critCount = criteriaWeights != null ? criteriaWeights.length : 0;

        if (critCount == 0) {
            resultArea.setText("Критерии не заданы. Пожалуйста, рассчитайте критерии перед расчетом альтернатив.");
            return;
        }

        double bestScore = -1;
        String bestAlternative = "";

        for (int i = 0; i < altCount; i++) {
            double score = 0;
            for (int j = 1; j <= critCount; j++) {
                String valueStr = (String) alternativesTableModel.getValueAt(i, j);
                double value = 0;
                if (valueStr != null && !valueStr.isEmpty()) {
                    try {
                        value = Double.parseDouble(valueStr);
                    } catch (NumberFormatException e) {
                        resultArea.setText("Ошибка в данных: неверный формат значения для альтернативы " +
                                alternativesTableModel.getValueAt(i, 0) + " в критерии " + j);
                        return;
                    }
                }
                score += value * criteriaWeights[j - 1];
            }
            if (score > bestScore) {
                bestScore = score;
                bestAlternative = (String) alternativesTableModel.getValueAt(i, 0);
            }
        }
        resultArea.setText("Лучшее место для отдыха: " + bestAlternative + "\nИтоговый балл: " + bestScore);
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
            criteriaWeights[i] = Double.parseDouble(criteriaFields[i].getText());
        }
    }

    private void updateComparisonTableHeaders() {
        for (int i = 0; i < criteriaFields.length; i++) {
            String header = criteriaFields[i].getText();
            comparisonTable.getColumnModel().getColumn(i).setHeaderValue(header);
        }
        comparisonTable.getTableHeader().repaint();
    }

    private void resetComparisonMatrix() {
        comparisonTableModel.setRowCount(0);
        comparisonTableModel.setRowCount(6);
        comparisonTableModel.setColumnCount(6);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DecisionSupportSystem::new);
    }
}
