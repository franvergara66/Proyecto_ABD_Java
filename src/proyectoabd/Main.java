package proyectoabd;
import vista.principal;

public class Main {


    public static void main(String[] args) {
    
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
                                 principal p = new principal();
                                 p.setVisible(true);
			}
		}); 
    }

}
