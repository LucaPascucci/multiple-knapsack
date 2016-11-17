package pascu;

/**
 * @author  Luca Pascucci
 * @version 1.0
 * @since   2016-10-16
 */

public class Main {

    public static void main (String[] args) {

        View view = new View();
        Controller controller = new Controller(view);
        view.attachObserver(controller);
        view.setVisible(true);
    }


}

