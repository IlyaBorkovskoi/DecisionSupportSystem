import javax.swing.*;

public class DecisionSupportSystem {
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private CriteriaPanel criteriaPanel;
    private AlternativesPanel alternativesPanel;
    private ResultPanel resultPanel;

    public DecisionSupportSystem() {
        frame = new JFrame("Система поддержки принятия решений");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        tabbedPane = new JTabbedPane();

        criteriaPanel = new CriteriaPanel();
        alternativesPanel = new AlternativesPanel();
        resultPanel = new ResultPanel(criteriaPanel, alternativesPanel); // Передаем оба объекта в ResultPanel

        tabbedPane.addTab("Критерии", criteriaPanel.getPanel());
        tabbedPane.addTab("Альтернативы", alternativesPanel.getPanel());
        tabbedPane.addTab("Результат", resultPanel.getPanel());

        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DecisionSupportSystem::new);
    }
}
