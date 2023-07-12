import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new gui1();
                frame.setSize(900,600);
                frame.setVisible(true);

            }
        });

    }
}