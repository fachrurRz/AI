import java.util.*;
import java.io.*;

public class AI_1506689143_Rozi {

	protected final String PLAYER = "Player";
	protected final String NPM = "AI_1506689143_Rozi";
	protected final String ENEMY = "Enemy";
	protected final String BOMB = "Bomb";
	protected final String FLARE = "DOWNare";
	protected final String POWER_B = "Power Up Bomb";
	protected final String POWER_P = "Power Up Power";
	protected final String UNDETRUCTIBLE_WALL = "Undetructible_Wall";
	protected final String DETRUCTIBLE_WALL = "Detructible_Wall";
	protected final String LAND = "Land";

	protected final String DANGER = "Danger";
	protected final String SAFE = "Safe";
	protected final String ARRIVE = "Arrive";


	protected final String MOVE_UP = ">> MOVE UP";
	protected final String MOVE_LEFT = ">> MOVE LEFT";
	protected final String MOVE_DOWN = ">> MOVE DOWN";
	protected final String MOVE_RIGHT = ">> MOVE RIGHT";
	protected final String DROP_BOMB = ">> DROP BOMB";
	protected final String STAY = ">> STAY";

	protected final int MAX = 9999;
	

	protected String condition = SAFE;
	protected int playerIndex = 0;
	protected String lastMove = STAY;

	protected int turn = 0;
	


	class GameProcess {
		private Map map;
		private String output;
		private int y;
		private int x;

		public GameProcess(Map map) {
			this.map = map;
			this.output = STAY;
			y = this.map.playerY;
			x = this.map.playerX;
		}

		public void start() {


			Node[][] tile = this.map.getObjects();
			GameObject wall = this.nearestWall(this.map.getWalls());
			GameObject powerUp = this.nearestPowerUp(this.map.getPowerUps());

			int fp = this.fitnessFunction(tile, y, x, 0);
			if (fp < 0) {
				if (isValidMove(y-1, x)) {
					int fp_atas = this.fitnessFunction(tile, y-1, x, 3);
					if (fp_atas >= 0) {
						this.output = MOVE_UP;
						return;
					}
					
				}
				if (isValidMove(y, x-1)) {
					int fp_kiri = this.fitnessFunction(tile, y, x-1, 4);
					if (fp_kiri >= 0) {
						this.output = MOVE_LEFT;
						return;
					}
				}

				if (isValidMove(y+1, x)) {
					int fp_bawah = this.fitnessFunction(tile, y+1, x, 1);
					if (fp_bawah >=  0) {
						this.output = MOVE_DOWN;
						return;
					}
				}

				if (isValidMove(y, x+1)) {
					int fp_kanan = this.fitnessFunction(tile, y, x+1, 2);
					if (fp_kanan >= 0) {
						this.output = MOVE_RIGHT;
						return;
					}
				}

			} else {


				if (turn == 0) {

					String tempOutput = this.moveToTarget(wall);
					if (condition == ARRIVE) {
						turn = 1;
						condition = SAFE;
					} else {
						this.output = this.moveToTarget(wall);
					}

					return;

				} else if (turn == 1) {

					int fp_atas = 0;
					int fp_kiri = 0;
					int fp_bawah = 0;
					int fp_kanan = 0;
					if (isValidMove(y-1, x)) {
						fp_atas = this.fitnessFunction(tile, y-1, x, 3);

					}
					if (isValidMove(y, x-1)) {
						fp_kiri = this.fitnessFunction(tile, y, x-1, 4);
					}

					if (isValidMove(y+1, x)) {
						fp_bawah = this.fitnessFunction(tile, y+1, x, 1);
					}

					if (isValidMove(y, x+1)) {
						fp_kanan = this.fitnessFunction(tile, y, x+1, 2);
					}

					if ((fp_atas + fp_kiri + fp_bawah + fp_kanan) < 0) {
						this.output = STAY;
						return;
					} else {
						this.output = DROP_BOMB;
						turn = 0;
						return;
					}

				}
			}



			
		}


		public int fitnessFunction(Node[][] tile, int y, int x, int except) {
			int fp_atas = 0;
			int fp_kiri = 0;
			int fp_bawah = 0;
			int fp_kanan = 0;


			if (except != 1) {

				/**
				 * looping Atas
				 */
				for (int i = y; i >= 0; i--) {
					int fp = 0;
					for (int j = 0; j < tile[i][x].node.size(); j++ ) {
						fp += this.fitnessCondition(tile[i][x].node, Math.abs(y-i));
					}

					if (fp != 0) {
						fp_atas = fp;
						break;
					}
				}

			}


			if (except != 2) {
				
				/**
				 * looping Kiri
				 */
				for (int i = x; i >= 0; i--) {
					int fp = 0;
					for (int j = 0; j < tile[y][i].node.size(); j++ ) {
						fp += this.fitnessCondition(tile[y][i].node, Math.abs(x-i));
					}

					if (fp != 0) {
						fp_kiri = fp;
						break;
					}
				}

			}

			if (except != 3) {
				/**
				 * looping Bawah
				 */
				for (int i = y; i < tile.length; i++) {
					int fp = 0;
					for (int j = 0; j < tile[i][x].node.size(); j++ ) {
						fp += this.fitnessCondition(tile[i][x].node, Math.abs(y-i));					
					}
					if (fp != 0) {
						fp_bawah = fp;
						break;
					}
				}

			}

			if (except != 4) {
				/**
				 * looping Kanan
				 */
				for (int i = x; i < tile[y].length; i++) {
					int fp = 0;
					for (int j = 0; j < tile[y][i].node.size(); j++ ) {
						fp += this.fitnessCondition(tile[y][i].node, Math.abs(x-i));
					}
					if (fp != 0) {
						fp_kanan = fp;
						break;
					}
				}
			}

			return fp_atas + fp_kiri + fp_bawah + fp_kanan;
		}


		public int fitnessCondition(ArrayList<GameObject> object, int range) {
			int fp = 0;
			
			for (int i = 0; i < object.size() ; i++ ) {
				String type = object.get(i).getType();
				if (type.equals(BOMB)) {
					Bomb bomb = (Bomb)object.get(i);
					int bombTime = bomb.getBombTime();
					int bombPower = bomb.getBombPower();
					int inRange = Math.abs(bombPower - range) + 2;
					if (bombTime > inRange) {
						// Safe
						fp = -5;
					} else if (bombTime == inRange) {
						fp = -5;						
					} else {
						fp = -5;
						condition = DANGER;
					}

				} else if (type.equals(FLARE)) {
					fp = -5;
				} else if (type.equals(DETRUCTIBLE_WALL) || type.equals(UNDETRUCTIBLE_WALL)) {
					fp += 1;
				}
			}

			return fp;
		}

		public boolean isValidMove(int y, int x) {
			if (y < 0 || y >= this.map.getObjects().length || x < 0 || x >= this.map.getObjects()[0].length ) {
				return false;
			}

			for (int i = 0; i < this.map.getObjects()[y][x].node.size() ; i++ ) {
				
				if (this.map.getObjects()[y][x].node.get(i).getType().equals(DETRUCTIBLE_WALL)) {
					condition = ARRIVE;
					return false;
				}

				if (!this.map.getObjects()[y][x].node.get(i).isPassable()) {
					return false;
				}
			}

			return true;
		}

		public String moveToTarget(GameObject target) {

			int fp_atas = MAX;
			int fp_kiri = MAX;
			int fp_bawah = MAX;
			int fp_kanan = MAX;

			int yTarget = target.getY();
			int xTarget = target.getX();



			if (isValidMove(this.y-1, this.x)) {
				fp_atas = Math.abs(this.y-1 - yTarget) + Math.abs(this.x - xTarget);
			}

			if (isValidMove(this.y, this.x-1)) {
				fp_kiri = Math.abs(this.y - yTarget) + Math.abs(this.x-1 - xTarget);
			}

			if (isValidMove(this.y+1, this.x)) {
				fp_bawah = Math.abs(this.y+1 - yTarget) + Math.abs(this.x - xTarget);
			}

			if (isValidMove(this.y, this.x+1)) {
				fp_kanan = Math.abs(this.y - yTarget) + Math.abs(this.x+1 - xTarget);
			} 


			int fp = Math.min(fp_atas, Math.min(fp_kiri, Math.min(fp_bawah, fp_kanan)));

			Random rand = new Random();

			int n = rand.nextInt(2);
			
			if (n == 0) {
				if (fp_atas == fp && !lastMove.equals(MOVE_DOWN)) {
					return MOVE_UP;
				}else if (fp_kiri == fp && !lastMove.equals(MOVE_RIGHT)) {
					return MOVE_LEFT;
				}else if (fp_bawah == fp && !lastMove.equals(MOVE_UP)) {
					return MOVE_DOWN;
				}else if (fp_kanan == fp && !lastMove.equals(MOVE_LEFT)) {
					return MOVE_RIGHT;
				}
			}else if (n == 1) {
				if (fp_atas == fp && !lastMove.equals(MOVE_DOWN)) {
					return MOVE_UP;
				}else if (fp_kanan == fp && !lastMove.equals(MOVE_LEFT)) {
					return MOVE_RIGHT;
				}else if (fp_bawah == fp && !lastMove.equals(MOVE_UP)) {
					return MOVE_DOWN;
				}else if (fp_kiri == fp && !lastMove.equals(MOVE_RIGHT)) {
					return MOVE_LEFT;
				}
			}

			return STAY;


		}

		public GameObject nearestPowerUp(ArrayList<GameObject> powerUps) {

			if (powerUps.size() == 0) {
				return null;
			}

			GameObject tempPowerUp = powerUps.get(0);
			int tempDist = Math.abs(this.y - powerUps.get(0).getY()) + Math.abs(this.x - powerUps.get(0).getX());
			for (int i = 0; i < powerUps.size() ; i++ ) {
				int yWall = powerUps.get(i).getY();
				int xWall = powerUps.get(i).getX();

				int dist = Math.abs(this.y - yWall) + Math.abs(this.x - xWall);

				if (dist < tempDist) {
					tempPowerUp = powerUps.get(i);
					tempDist= dist;
				}
			}

			return tempPowerUp;
		}


		public DestructibleWall nearestWall(ArrayList<DestructibleWall> walls) {

			if (walls.size() == 0) {
				return null;
			}

			DestructibleWall tempWall = walls.get(0);
			int tempDist = Math.abs(this.y - walls.get(0).getY()) + Math.abs(this.x - walls.get(0).getX());
			for (int i = 0; i < walls.size() ; i++ ) {
				int yWall = walls.get(i).getY();
				int xWall = walls.get(i).getX();

				int dist = Math.abs(this.y - yWall) + Math.abs(this.x - xWall);

				if (dist <= tempDist) {
					if (dist == tempDist) {
						if (walls.get(i).getPowerUpInfo().equals(POWER_B) || walls.get(i).getPowerUpInfo().equals(POWER_P)) {

						} else {
							tempWall = walls.get(i);
							tempDist = dist;
						}
					}else {
						tempWall = walls.get(i);
						tempDist = dist;
					}
				}
			}

			return tempWall;
		}

		public String getOutput() {
			return this.output;
		}
	}
	class Map {
		private Node[][] objects;
		private String[] map;
		private Player[] players;
		private ArrayList<DestructibleWall> detructibleWalls;
		private ArrayList<GameObject> powerUps; 
		protected int playerY = 0;
		protected int playerX = 0;

		public Map(String[] map, Player[] players, int row, int column) {
			this.map = map;
			this.objects = new Node[row][column];
			this.players = players;
			this.detructibleWalls = new ArrayList<DestructibleWall>();
			this.powerUps = new ArrayList<GameObject>();
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
						this.playerY = y;
						this.playerX = x;

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
				DestructibleWall wall =  new DestructibleWall(DETRUCTIBLE_WALL, y, x, false, type, powerUpInfo);
				this.detructibleWalls.add(wall);
				return wall;

			} else if (type.equals("XBX")) {

				String powerUpInfo = "Bomb";
				DestructibleWall wall =  new DestructibleWall(DETRUCTIBLE_WALL, y, x, false, type, powerUpInfo);
				this.detructibleWalls.add(wall);
				return wall;

			} else if (type.equals("XPX")) {

				String powerUpInfo = "Power";
				DestructibleWall wall =  new DestructibleWall(DETRUCTIBLE_WALL, y, x, false, type, powerUpInfo);
				this.detructibleWalls.add(wall);
				return wall;

			} else if (type.equals("+B")) {

				GameObject powerUp = new GameObject(POWER_B, y, x, true, type);
				this.powerUps.add(powerUp);
				return powerUp;

			} else if (type.equals("+P")) {

				GameObject powerUp = new GameObject(POWER_B, y, x, true, type);
				this.powerUps.add(powerUp);
				return powerUp;
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

		public ArrayList<DestructibleWall> getWalls(){
			return this.detructibleWalls;
		}

		public ArrayList<GameObject> getPowerUps(){
			return this.powerUps;
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
			return this.getType() + " status " + this.getStatus(); 
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

				GameProcess game = ai.new GameProcess(map);
				game.start();
				ai.lastMove = game.getOutput();
				System.out.println(game.getOutput());




			}

		} finally 
		{
			bf.close();
		}

	}	
}
