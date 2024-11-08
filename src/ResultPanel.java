import javax.swing.*;
import java.awt.BorderLayout;
import java.util.List;

public class ResultPanel {
    private JPanel panel;
    private JTextArea resultArea;
    private CriteriaPanel criteriaPanel;
    private AlternativesPanel alternativesPanel; // Добавляем панель с альтернативами

    public ResultPanel(CriteriaPanel criteriaPanel) {
        this.criteriaPanel = criteriaPanel;
        this.alternativesPanel = alternativesPanel;
        initialize();
    }

    private void initialize() {
        panel = new JPanel(new BorderLayout());
        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(resultArea);
        JButton applyButton = new JButton("Применить");
        applyButton.addActionListener(e -> calculateBestAlternative());

        panel.add(new JLabel("Результаты"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(applyButton, BorderLayout.SOUTH);
    }

    public JPanel getPanel() {
        return panel;
    }

    private void calculateBestAlternative() {
        double[] criteriaWeights = criteriaPanel.getCriteriaWeights();
        if (criteriaWeights == null) {
            resultArea.setText("Не удалось получить веса критериев. Проверьте введенные данные.");
            return;
        }

        List<Object[]> alternatives = alternativesPanel.getAlternatives(); // Метод для получения списка альтернатив
        if (alternatives.isEmpty()) {
            resultArea.setText("Список альтернатив пуст. Добавьте альтернативы.");
            return;
        }

        Object[] bestAlternative = null;
        double highestScore = Double.NEGATIVE_INFINITY;

        // Проходимся по всем альтернативам
        for (Object[] alternative : alternatives) {
            double score = calculateScore(alternative, criteriaWeights);
            if (score > highestScore) {
                highestScore = score;
                bestAlternative = alternative;
            }
        }

        // Выводим результаты
        if (bestAlternative != null) {
            resultArea.setText("Наилучшая альтернатива:\n");
            resultArea.append("Название: " + bestAlternative[0] + "\n");
            resultArea.append("Оценка: " + String.format("%.2f", highestScore) + "\n");
        } else {
            resultArea.setText("Не удалось определить наилучшую альтернативу.");
        }
    }

    private double calculateScore(Object[] alternative, double[] criteriaWeights) {
        double score = 0.0;
        for (int i = 1; i < alternative.length && i - 1 < criteriaWeights.length; i++) {
            try {
                double value = Double.parseDouble(alternative[i].toString());
                score += value * criteriaWeights[i - 1];
            } catch (NumberFormatException e) {
                // Игнорируем неверные значения в альтернативе
            }
        }
        return score;
    }
}

