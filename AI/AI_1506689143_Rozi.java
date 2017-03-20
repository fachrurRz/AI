import java.util.*;
import java.io.*;

public class AI_1506689143_Rozi {

	protected final String PLAYER = "Player";
	protected final String NPM = "1506689143";
	protected final String ENEMY = "Enemy";
	protected final String BOMB = "Bomb";
	protected final String FLARE = "Flare";
	protected final String POWER_B = "Power Up Bomb";
	protected final String POWER_P = "Power Up Power";
	protected final String UNDETRUCTIBLE_WALL = "Undetructible_Wall";
	protected final String DETRUCTIBLE_WALL = "Detructible_Wall";
	protected final String LAND = "Land";

	protected int playerIndex;

	class Map {
		private Node[][] objects;
		private String[] map;
		private Player[] players;

		public Map(String[] map, Player[] players, int row, int column) {
			this.map = map;
			this.objects = new Node[row][column];
			this.players = players;
		}

		public void render() {
			for (int i = 0; i < this.map.length; i++ ) {
				String mainString = this.map[i];
				mainString = mainString.substring(1).replace("[","").replace("]","[");
				String[] tile = mainString.split("\\[");

				for (int j = 0; j < tile.length; j++ ) {

					tile[j] = tile[j].trim();

					if (tile[j].contains(";")) {
						String[] parse = tile[j].split(";");

						ArrayList<GameObject> multipleObject = this.findMultipleObject(parse, i, j);

						this.objects[i][j] = new Node(multipleObject);


					} else {

						GameObject node = this.find(tile[j], i, j);

						ArrayList<GameObject> multipleObject = new ArrayList<GameObject>();
						multipleObject.add(node);

						this.objects[i][j] = new Node(multipleObject);

					}
				}
			}
		}

		public ArrayList<GameObject> findMultipleObject(String[] type, int y, int x) {

			ArrayList<GameObject> multipleObject = new ArrayList<GameObject>();
			for (int i = 0; i < type.length ;i++ ) {
				GameObject node = this.find(type[i], y, x);
				multipleObject.add(node);
			}

			return multipleObject;

		}

		public GameObject find(String type, int y, int x) {
			if (type.length() == 0) {
				
				return new GameObject(LAND, y, x, true, LAND);

			} else if (isNumeric(type)) {

				int index = Integer.parseInt(type);

				for (int i = 0; i < this.players.length; i++ ) {
					if (i == index && i == playerIndex) {
						players[i].setType(PLAYER);
						players[i].setY(y);
						players[i].setX(x);

						return players[i];
					} else if ( i == index ) {
						players[i].setType(ENEMY);
						players[i].setY(y);
						players[i].setX(x);

						return players[i];
					}
				}

				return new Player();

			} else if (type.charAt(0) == 'B') {

				int power = Character.getNumericValue(type.charAt(1));
				int time = Character.getNumericValue(type.charAt(2));

				return new Bomb(BOMB, y, x, false, type, power, time);

			} else if (type.charAt(0) == 'F') {

				int time = Character.getNumericValue(type.charAt(1));
				return new Flare(FLARE, y, x, true, type, time);

			} else if (type.equals("###")) {

				return new GameObject(UNDETRUCTIBLE_WALL, y, x, false, type);

			} else if (type.equals("XXX")) {
				
				String powerUpInfo = "None";
				return new DestructibleWall(DETRUCTIBLE_WALL, y, x, false, type, powerUpInfo);

			} else if (type.equals("XBX")) {

				String powerUpInfo = "Bomb";
				return new DestructibleWall(DETRUCTIBLE_WALL, y, x, false, type, powerUpInfo);

			} else if (type.equals("XPX")) {

				String powerUpInfo = "Power";
				return new DestructibleWall(DETRUCTIBLE_WALL, y, x, false, type, powerUpInfo);

			} else if (type.equals("+B")) {

				return new GameObject(POWER_B, y, x, true, type);

			} else if (type.equals("+P")) {

				return new GameObject(POWER_P, y, x, true, type);

			} else {
				return null;
			} 

		}

		private boolean isNumeric(String s) {
			return s.matches("[-+]?\\d*\\.?\\d+");
		}

		public Node[][] getObjects(){
			return this.objects;
		}

		public void setObjects(Node[][] objects) {
			this.objects = objects;
		}

	}

	class Node {
		protected ArrayList<GameObject> node;
		
		public Node(ArrayList<GameObject> node) {
			this.node = node;
		}
	}

	class GameObject {
		private String type;
		private int y;
		private int x;
		private boolean isPassable;
		private String name;

		public GameObject(String type, int y, int x, boolean isPassable, String name) {
			this.type = type;
			this.y = y;
			this.x = x;
			this.isPassable = isPassable;
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public boolean isPassable() {
			return isPassable;
		}

		public void setPassable(boolean passable) {
			isPassable = passable;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "GameObject{" +
			"type='" + type + '\'' +
			", y=" + y +
			", x=" + x +
			", isPassable=" + isPassable +
			", name='" + name + '\'' +
			'}';
		}

	}

	class Player extends GameObject {
		private int index;
		private int bombCount;
		private int bombMax;
		private int bombPower;
		private String status;
		private int score;

		public Player (String type, int y, int x, boolean isPassable, String name, int index, int bombCount, int bombMax, int bombPower, String status ,int score) {
			super(type, y, x, isPassable, name);
			this.index = index;
			this.bombCount = bombCount;
			this.bombMax = bombMax;
			this.bombPower = bombPower;
			this.status = status;
			this.score = score;

		}

		public Player() {
			super("None", -1, -1, true, "None");
			this.index = -1;
			this.bombCount = -1;
			this.bombMax = -1;
			this.bombPower = -1;
			this.status = "None";
			this.score = -1;
		}

		public int getIndex() {
			return this.index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public int getBombCount() {
			return this.bombCount;
		}

		public void setBombCount(int bombCount) {
			this.bombCount = bombCount;
		}

		public int getBombMax() {
			return this.bombMax;
		}

		public void setBombMax(int bombMax) {
			this.bombMax = bombMax;
		}

		public int getBombPower() {
			return this.bombPower;
		}

		public void setBombPower(int bombPower) {
			this.bombPower = bombPower;
		}

		public String getStatus() {
			return this.status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public int getScore() {
			return this.score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		@Override
		public String toString() {
			return this.getName() + " status " + this.getStatus(); 
		}
	}

	
	class Bomb extends GameObject {
		private int bombPower;
		private int bombTime;

		public Bomb(String type, int y, int x, boolean isPassable, String name, int bombPower, int bombTime) {
			super(type, y, x, isPassable, name);
			this.bombPower = bombPower;
			this.bombTime = bombTime;
		}

		public int getBombPower() {
			return bombPower;
		}

		public void setBombPower(int bombPower) {
			this.bombPower = bombPower;
		}

		public int getBombTime() {
			return bombTime;
		}

		public void setBombTime(int bombTime) {
			this.bombTime = bombTime;
		}

		@Override
		public String toString() {
			return this.getType() + " : " + this.getBombPower() + "/" + this.getBombTime();
		} 
		
	}


	class Flare extends GameObject {
		private int flareTime;

		public Flare(String type, int y, int x, boolean isPassable, String name, int flareTime) {
			super(type, y, x, isPassable, name);
			this.flareTime = flareTime;
		}

		public int getFlareTime() {
			return flareTime;
		}

		public void setFlareTime(int flareTime) {
			this.flareTime = flareTime;
		}

		@Override
		public String toString() {
			return this.getType() + " : " + this.getFlareTime(); 
		}

	}

	class DestructibleWall extends GameObject {
		private String powerUpInfo;

		public DestructibleWall(String type, int y, int x, boolean isPassable, String name, String powerUpInfo) {
			super(type, y, x, isPassable, name);
			this.powerUpInfo = powerUpInfo;
		}

		public String getPowerUpInfo(){
			return this.powerUpInfo;
		}

		public void setPowerUpInfo(String powerUpInfo) {
			this.powerUpInfo = powerUpInfo;
		}

		@Override
		public String toString() {
			return this.getType() + " : " + this.getPowerUpInfo(); 
		}
	}



	public static void main(String[] args) throws Exception {

		AI_1506689143_Rozi ai = new AI_1506689143_Rozi();

		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		try{

			while(true) {

				String input = "";
				Map map = null;
				int turn = 0;
				int playersNum = 0;
				Player[] players = new Player[playersNum];
				int rowNum = 0;
				int columnNum = 0;

				while (!input.equals("END")) {

					input = bf.readLine();

					String inputArray[] = input.split(" ");

					if (inputArray[0].equals("TURN")) {
						turn = Integer.parseInt(inputArray[1]);
					} else if (inputArray[0].equals("PLAYER")) {
						playersNum = Integer.parseInt(inputArray[1]);
						players = new Player[playersNum];
					} else if (inputArray[0].charAt(0) == 'P') {

						Player player = ai.new Player();
						int index = Character.getNumericValue(inputArray[0].charAt(1));

						String name = inputArray[1];

						if (name.equals(ai.NPM)) {
							ai.playerIndex = index;
						}

						String[] bomb = inputArray[2].substring(5).split("/");

						int bombCount = Integer.parseInt(bomb[0]);
						player.setBombCount(bombCount);

						int bombMax = Integer.parseInt(bomb[1]);
						player.setBombMax(bombMax);

						int bombPower = Integer.parseInt(inputArray[3].substring(6));
						player.setBombPower(bombPower);

						String status = inputArray[4];
						player.setStatus(status);

						int score = Integer.parseInt(inputArray[5]);
						player.setScore(score);

						players[index] = player;

					} else if (inputArray[0].equals("BOARD")) {
						rowNum = Integer.parseInt(inputArray[1]);
						columnNum = Integer.parseInt(inputArray[2]);

						String[] rows = new String[rowNum];

						for (int i = 0; i < rows.length ; i++ ) {
							rows[i] = bf.readLine();	
						}

						map = ai.new Map(rows, players, rowNum, columnNum);
						map.render();
					}
				}




			}

		} finally {

			bf.close();
		}

	}	
}
