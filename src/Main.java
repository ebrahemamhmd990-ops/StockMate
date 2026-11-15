import database.DatabaseConnection;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import view.MainFrame;

public class Main {

    public static void main(String[] args) {

        try {
            DatabaseConnection.initializeDatabase();

            SwingUtilities.invokeLater(() -> {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            });

        } catch (Exception e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(
                null,
                "Database Error: " + e.getMessage(),
                "StockMate Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
