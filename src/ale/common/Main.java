package ale.common;

public class Main {

	public static void main (final String[] args) {
		View view = new View();
		Controller controller = new Controller(view);
		view.attachObserver(controller);
		view.setVisible(true);
	}
}
