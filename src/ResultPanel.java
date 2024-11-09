import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ResultPanel {
    private JPanel panel;
    private JTextArea resultArea;
    private CriteriaPanel criteriaPanel;
    private AlternativesPanel alternativesPanel;

    public ResultPanel(CriteriaPanel criteriaPanel, AlternativesPanel alternativesPanel) {
        this.criteriaPanel = criteriaPanel;
        this.alternativesPanel = alternativesPanel; // Инициализация панели с альтернативами
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
        // Получение весов критериев
        double[] criteriaWeights = criteriaPanel.getCriteriaWeights();
        if (criteriaWeights == null || criteriaWeights.length == 0) {
            resultArea.setText("Критерии не заданы. Пожалуйста, рассчитайте критерии перед расчетом альтернатив.");
            return;
        }

        // Проверка суммы весов (должна быть близка к 1.0)
        double sumWeights = 0.0;
        for (double weight : criteriaWeights) {
            sumWeights += weight;
        }
        if (Math.abs(sumWeights - 1.0) > 0.001) {
            resultArea.setText("Ошибка: сумма весов критериев не равна 1. Текущая сумма: " + sumWeights);
            return;
        }

        // Получение альтернатив
        List<Object[]> alternatives = alternativesPanel.getAlternatives();
        if (alternatives == null || alternatives.isEmpty()) {
            resultArea.setText("Альтернативы не заданы. Пожалуйста, добавьте альтернативы перед расчетом.");
            return;
        }

        double bestScore = -1;
        String bestAlternative = "";

        for (Object[] altData : alternatives) {
            String altName = (String) altData[0];
            double score = calculateScore(altData, criteriaWeights);

            if (score > bestScore) {
                bestScore = score;
                bestAlternative = altName;
            }
        }

        resultArea.setText("Лучшее место для отдыха: " + bestAlternative + "\nИтоговый балл: " + bestScore);
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
