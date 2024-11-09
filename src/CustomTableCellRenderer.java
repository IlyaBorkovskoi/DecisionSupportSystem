import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import javax.swing.JTable;
import java.text.DecimalFormat;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {
    private final DecimalFormat df = new DecimalFormat("#.##");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        // Вызов базового метода для настройки стандартного рендеринга
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Форматирование значения, если оно является числом
        if (value instanceof Number) {
            double numericValue = ((Number) value).doubleValue();
            setText(df.format(numericValue));
        } else if (value instanceof String) {
            try {
                double numericValue = Double.parseDouble((String) value);
                setText(df.format(numericValue));
            } catch (NumberFormatException ex) {
                setText((String) value);  // Оставляем текст без изменений, если это не число
            }
        } else {
            setText(value != null ? value.toString() : ""); // Если значение null, оставляем ячейку пустой
        }

        return c;
    }
}
