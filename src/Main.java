
import libreriabienvenida.ConsoleLibrary;
import controllers.ApplicationController;
import views.View;

public class Main {
    public static void main(String[] args) {
        ConsoleLibrary.showMessage();

        String username = "luquinhan03";
      
        ApplicationController controller = new ApplicationController(username);
        View view = new View(controller);
        view.setVisible(true);
    }
}
