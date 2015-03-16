import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.bluetooth.*;
import javax.microedition.io.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.Position;

public class Server {
	public final UUID uuid = new UUID( // the uid of the service, it has to be
										// unique,
			"27012f0c68af4fbf8dbe6bbaf7aa432a", false); // it can be generated
														// randomly
	public final String name = "Server"; // the name of the service
	public final String url = "btspp://localhost:" + uuid // the service url
			+ ";name=" + name + ";authenticate=false;encrypt=false;";
	LocalDevice local = null;
	StreamConnectionNotifier server = null;
	StreamConnection conn = null;
	DataInputStream din = null;
	Robot r = null;

	public Server() {
		try {
			// System.out.println("Server:  Setting device to be discoverable...");
			local = LocalDevice.getLocalDevice();
			local.setDiscoverable(DiscoveryAgent.GIAC);
			// System.out.println("Server:  Start advertising service...");
			server = (StreamConnectionNotifier) Connector.open(url);
			// System.out.println("Server:  Waiting for incoming connection...");

			conn = server.acceptAndOpen();
			// System.out.println("Server:  Client Connected...");
			din = new DataInputStream(conn.openInputStream());

			byte[] dados = new byte[1024];
			int bytesLidos = -1;
			while (true) {

				bytesLidos = din.read(dados);

				while (bytesLidos != -1) {

					// System.out.println("inicio");

					acao(mostraMensagem(dados, bytesLidos));
					dados = new byte[1024];
					// System.out.println("fim de mensagem");
					bytesLidos = din.read(dados);

					// System.out.println("continua");
				}
				if (bytesLidos == -1) {

					System.out.println("! -1 !");
					restart();
				}

			}

		} catch (Exception e) {

			restart();
			System.out.println("Server: Exception Occured: " + e.toString());
		}
	}

	private void acao(String action) {

		try {

			if (r == null)
				r = new Robot();

		} catch (AWTException e1) {

			e1.printStackTrace();
		}
		if (action.contains("X:") && action.contains("Y:")) {

			String[] str = action.split(" ");
			// System.out.println("X:"+str[1]+" Y:"+str[4]);
			// System.out.println("Quantidade Vetor: "+str.length);
			int xGuess = Integer.parseInt(str[1].trim());
			int yGuess = Integer.parseInt(str[4].trim());

			try {

				PointerInfo pi = MouseInfo.getPointerInfo();
				Point b = pi.getLocation();
				int xHost = (int) b.getX();
				int yHost = (int) b.getY();
				System.out.println("Cursor X: " + xHost + " Y: " + yHost);

				r.mouseMove(xHost + (-xGuess), yHost + (-yGuess));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (action.contains("RIGHTCLICK")) {

			try {
				
				r.mousePress(MouseEvent.BUTTON3_MASK);
				r.mouseRelease(MouseEvent.BUTTON3_MASK);
			} catch (Exception e) {

				e.printStackTrace();
			}

		} else if (action.contains("LEFTCLICK")) {

			try {

				r.mousePress(MouseEvent.BUTTON1_MASK);
				r.mouseRelease(MouseEvent.BUTTON1_MASK);
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

	}

	public void restart() {

		try {
			din.close();
			System.out.println("Fim da conecção!");
			conn.close();
			conn = null;
			System.out.println("Server:  Waiting for incoming connection...");
			conn = server.acceptAndOpen();
			System.out.println("Server:  Client Connected...");
			din = new DataInputStream(conn.openInputStream());

			// System.out.println("Restart!");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String mostraMensagem(byte[] dados, int bytesLidos) {
		//
		String texto = "";
		Date d = new Date(System.currentTimeMillis());

		SimpleDateFormat formatador = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:SS");
		String data = formatador.format(d);

		for (byte b : dados) {

			texto += (char) b;
		}

		System.out.println("Hora: " + data + " | Cliente: " + texto);

		return texto;

	}

	public static void main(String args[]) {
		Server echoserver = new Server();
	}

}
