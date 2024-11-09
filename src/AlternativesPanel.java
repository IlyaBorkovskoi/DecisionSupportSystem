import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class AlternativesPanel {
    private JPanel panel;
    private JTable alternativesTable;
    private DefaultTableModel alternativesTableModel;
    private JTextField altNameField;
    private JButton addAltButton;

    public AlternativesPanel() {
        initialize();
    }

    private void initialize() {
        panel = new JPanel(new BorderLayout());

        String[] altColumns = {"Название", "Критерий 1", "Критерий 2", "Критерий 3", "Критерий 4", "Критерий 5", "Критерий 6"};
        alternativesTableModel = new DefaultTableModel(altColumns, 0);
        alternativesTable = new JTable(alternativesTableModel);
        JScrollPane scrollPane = new JScrollPane(alternativesTable);

        JPanel addAltPanel = new JPanel(new FlowLayout());
        altNameField = new JTextField(10);
        addAltButton = new JButton("Добавить альтернативу");
        addAltButton.addActionListener(e -> addAlternative());

        addAltPanel.add(new JLabel("Название альтернативы:"));
        addAltPanel.add(altNameField);
        addAltPanel.add(addAltButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(addAltPanel, BorderLayout.SOUTH);
        loadAlternativesFromFile();
    }

    private void addAlternative() {
        String altName = altNameField.getText();
        if (!altName.isEmpty()) {
            Object[] row = new Object[alternativesTableModel.getColumnCount()];
            row[0] = altName;

            // Запрашиваем значения для каждого критерия и проверяем, что они являются числами
            for (int i = 1; i < row.length; i++) {
                String criterionValue = JOptionPane.showInputDialog(panel, "Введите числовое значение для " + alternativesTableModel.getColumnName(i) + ":");
                if (criterionValue == null || !isNumeric(criterionValue)) {
                    JOptionPane.showMessageDialog(panel, "Ошибка: значение критерия должно быть числом.");
                    return;
                }
                row[i] = criterionValue;
            }

            alternativesTableModel.addRow(row);
            saveAlternativeToFile(row);
            altNameField.setText("");
        }
    }

    // Проверка, что строка является числовым значением
    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void saveAlternativeToFile(Object[] alternativeData) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("alternatives.txt", true))) {
            for (int i = 0; i < alternativeData.length; i++) {
                writer.print(alternativeData[i] == null ? "" : alternativeData[i].toString().replace(",", ""));
                if (i < alternativeData.length - 1) {
                    writer.print(",");
                }
            }
            writer.println();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(panel, "Ошибка при сохранении альтернативы в файл.");
        }
    }

    private void loadAlternativesFromFile() {
        File file = new File("alternatives.txt");
        if (!file.exists()) {
            return; // Если файл не существует, просто возвращаемся
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",", -1); // -1 чтобы сохранить пустые значения
                if (values.length == alternativesTableModel.getColumnCount()) {
                    alternativesTableModel.addRow(values);
                } else {
                    JOptionPane.showMessageDialog(panel, "Ошибка в данных файла: неверное количество столбцов.");
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(panel, "Ошибка при загрузке альтернатив из файла.");
        }
    }

    public List<Object[]> getAlternatives() {
        List<Object[]> alternatives = new ArrayList<>();
        for (int i = 0; i < alternativesTableModel.getRowCount(); i++) {
            Object[] row = new Object[alternativesTableModel.getColumnCount()];
            for (int j = 0; j < row.length; j++) {
                row[j] = alternativesTableModel.getValueAt(i, j);
            }
            alternatives.add(row);
        }
        return alternatives;
    }

    public JPanel getPanel() {
        return panel;
    }
}
