import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import javax.swing.JTable;
import java.text.DecimalFormat;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {
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
