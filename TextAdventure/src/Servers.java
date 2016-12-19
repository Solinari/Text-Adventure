import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servers extends Thread {

	final private ServerSocket masterServerSock;
	final public static int MAX_CLIENTS = 15;
	public static int num_clients = 0;
	private static BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

	final private SubServer[] subServersForClients = new SubServer[MAX_CLIENTS];
	// public ConcurrentHashMap<Object,Socket> clients;

	Semaphore token;

	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			System.err.println("Usage: java Servers <port number>");
			System.exit(1);
		}

		// get the port# as an int
		int portNumber = Integer.parseInt(args[0]);
		new Servers(portNumber);

	}

	public Servers(int port) throws IOException {

		this.masterServerSock = new ServerSocket(port);
		token = new Semaphore(MAX_CLIENTS);

		/*
		 * Trying to figure out implementation to have a list of clients to send
		 * them all messages clientList = new ArrayList<SubServer>(); clients =
		 * new HashMap<Object,Socket>(); Collections.synchronizedMap(clients);
		 */
		start();

	}

	@Override
	public void run() {

		while (!Servers.interrupted()) {
			// wait for clients
			Socket connection;

			try {

				// handle connection
				connection = this.masterServerSock.accept();

				// sub server to handle clients
				assignConnectionToSubServer(connection);

				// print out information
				System.out.println(connection.toString());
			} catch (IOException e) {

				System.err.println("Failed to connect to client ");
				e.printStackTrace();
				System.exit(1);
			}

		}
	}

	public void assignConnectionToSubServer(Socket connection) {

		/*
		 * for (int i = 0; i < MAX_CLIENTS; i++) {
		 * 
		 * // find an unassigned subserver (waiter) SubServer currentSubServer =
		 * this.subServersForClients[i];
		 * 
		 * if (currentSubServer == null) {
		 * 
		 * currentSubServer = new SubServer(connection, i, token, queue);
		 * this.subServersForClients[i] = currentSubServer; // /*Trying to
		 * figure out implementation to have a list of clients // * to send them
		 * all messages // clients = new ConcurrentHashMap<Object,Socket>(); //
		 * clients.put(connection.toString(), connection); ++num_clients; break;
		 * 
		 * } }
		 */

		this.subServersForClients[num_clients] = new SubServer(connection,
				num_clients, token, queue);
		++num_clients;
	}

	protected class SubServer extends Thread {

		final private int clientId;
		final private Socket clientConnection;
		@SuppressWarnings("unused")
		private OutputStream output;
		private Semaphore token;
		private final BlockingQueue<String> queue;

		String currentScene = "";

		// you can store additional client properties here if you want, for
		// example:
		// private int m_gameRating = 1500;
		public SubServer(Socket connection, int id, Semaphore semaIn,
				BlockingQueue<String> q) {
			this.clientId = id;
			this.clientConnection = connection;
			queue = q;
			try {
				output = clientConnection.getOutputStream();
			} catch (IOException e) {
				System.err.println("A player's connection has been lost!");
				// e.printStackTrace();
			}
			this.token = semaIn;
			start();
		}

		@Override
		public void run() {
			while (!SubServer.interrupted() && !clientConnection.isClosed()) {
				// process a client request

				// Using the TextAdventure protocol (to be implemented)
				// write all client replies to a file
				try {
					token.acquire();
				} catch (InterruptedException e) {

					System.err.println("Token interruption");
					e.printStackTrace();
				}

				try {
					processMessage();
				} catch (InterruptedException e) {
					System.out
							.println("The client has aborted its connection through error!");
					close();
					token.release();
					e.printStackTrace();
				}
				token.release();
				close();
				Servers.num_clients -= 1;
			}
		}

		// as an example, if you read String messages from your client,
		// just call this method from the run() method to process the client
		// request
		public  void processMessage() throws InterruptedException {
			PrintWriter output = null;
			BufferedReader input = null;
			String logFile = "loggedInputOutput.txt";

			try {
				input = new BufferedReader(new InputStreamReader(
						clientConnection.getInputStream()));
				output = new PrintWriter(clientConnection.getOutputStream(),
						true);
			} catch (SocketException socketException) {
				//System.err.println("Socket Excpetion, oh no!!!");
				close();

			} catch (IOException e1) {
				System.err.println("A player's connection has been lost!");
				// e.printStackTrace();
			}
			String adventureChoice = "";
			try {
				adventureChoice = input.readLine();
//				if (adventureChoice.contains("End of adventure")){
//					
//					System.out.println("Player has completed adventure");
//					close();
//					System.exit(-1);
//					
//				}
				InputOutputLogger.logToFile(logFile,
						"FROM CLIENT: Client has selected the follwing adventure choice: \n"
								+ adventureChoice);

			} catch (SocketException socketException) {
				//System.err.println("Socket Excpetion, oh no!!!");
				close();

			} catch (IOException ex) {
				Logger.getLogger(Servers.class.getName()).log(Level.SEVERE,
						null, ex);
			}
			Adventure adventure = new Adventure(adventureChoice);

			
			
			String currentChoice = adventure.getFirstSceneKey();
			currentScene = adventure.getCurrentScene(currentChoice);
			ArrayList<String> currentSceneKeys = adventure.getSceneKeys(
					currentChoice, currentScene);

			String sendSceneAndKeysToClient = formatClientKeys(currentSceneKeys);
			output.println(sendSceneAndKeysToClient);
			InputOutputLogger.logToFile(logFile,
					"FROM SERVER: Sent to client the following scene and keys: \n"
							+ sendSceneAndKeysToClient);

			// the client sending/receiving stuff would have to go here
			while (!currentChoice.contains("Z")) {

				try {
					// System.out.println(AdventureMain.getChoicesWithFormating(currentSceneKeys));
					String check = null;
					try {

						if ((check = input.readLine()) != null) {
							
//							if (check.contains("End of adventure")){
//								
//								System.out.println("Player has completed adventure");
//								close();
//								System.exit(-1);
//								
//							}
							
							currentChoice = check;
							InputOutputLogger.logToFile(logFile,
									"FROM CLIENT: Client chose:\n"
											+ currentChoice);

						}

					} catch (SocketException socketException) {
						//System.err.println("Socket Excpetion, oh no!!!");
						close();

					}
					if (clientId == 0 && check != null) {

						// Thread is the main thread
						int num_clients = Servers.num_clients;

						// String choiceArray[] = new String[num_clients];
						HashMap<String, Integer> choices = new HashMap<String, Integer>();
						int max = 0;
						String win = currentChoice;
						for (int i = 0; i < num_clients - 1; ++i) {
							String choice = queue.take();

							Integer cnt = choices.get(choice);
							if (cnt == null)
								cnt = 0;
							++cnt;
							choices.put(choice, cnt);
							if (cnt > max) {
								max = cnt;
								win = choice;
							} else if (cnt == max) {
								win = "TIE";
							}
						}

						if (win.equals("TIE")) {
							System.out
									.println("The players are lost, a random choice was generated!: "
											+ win);
							currentChoice = AdventureGenerator
									.randomPick(new ArrayList<String>(choices
											.keySet()));
						} else {
							
							System.out.println("The player(s) chose: " + win);
							if (win.contains("Z")){
								
								output.println("Z");
								close();
								//System.exit(-1);
								
							}
							currentChoice = win;
						}
						// I know this isn't ideal, but it should do for now
						for (int i = 0; i < num_clients - 1; ++i) {
							queue.add(currentChoice);
						}
						synchronized (queue) {
							queue.notifyAll();
						}
					} else if (Servers.num_clients > 1) {

						queue.add(currentChoice);
						synchronized (queue) {
							queue.wait();
						}
						currentChoice = queue.take();

					}
					String formattedCurrentChoice = currentChoice.split("-")[0];
					System.out.println("formattedCurrentChoice is:"+formattedCurrentChoice);
					formattedCurrentChoice = formattedCurrentChoice.split(" ")[2];

					currentChoice = formattedCurrentChoice;
					currentScene = adventure.getCurrentScene(currentChoice);
					currentSceneKeys = adventure.getSceneKeys(currentChoice,
							currentScene);

					sendSceneAndKeysToClient = formatClientKeys(currentSceneKeys);
					output.println(sendSceneAndKeysToClient);
					
					InputOutputLogger.logToFile(logFile,
							"FROM SERVER: Sent to client the following scene and keys:\n"
									+ sendSceneAndKeysToClient);
//					if (formattedCurrentChoice.contains("Z")){
//						
//						output.println("Z");
//						close();
//						//System.exit(-1);
//						
//					}
					output.flush();
					// System.out.println(currentScene);

				} catch (IOException ex) {
					Logger.getLogger(Servers.class.getName()).log(Level.SEVERE,
							"IO error", ex);
					System.err
							.println("A player's connection has been lost!...Dropping them");
					token.release();
					close();
				}
			}
			// No client input to pass
			// Passing null tells the protocol that3

			// we want the welcome message
		}

		// terminates the connection with this client (i.e. stops serving him)
		public void close() {
			try {
				this.clientConnection.close();
			} catch (IOException e) {
				// ignore
			}
		}

		private  String formatClientKeys(
				ArrayList<String> currentSceneKeys) {
			String sendSceneAndKeysToClient = "";
			for (String key : currentSceneKeys) {
				sendSceneAndKeysToClient = sendSceneAndKeysToClient + key + ";";
			}
			return currentScene = currentScene + "|" + sendSceneAndKeysToClient;

		}

	}

}
