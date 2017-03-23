import java.util.*;
import java.io.*;

/**
 * class AI_1506689143
 * 
 * this class implemented bomberman AI with DLS algorithm
 * 
 */
public class AI_1506689143_Rozi {

	/**
	 * @var PLAYER is the AI
	 * @var NPM is the AI id
	 * @var ENEMY is enemy type object
	 * @var BOMB is bomb type object
	 * @var FLARE is flare type object
	 * @var POWER_B is power up +B info
	 * @var POWER_P is power up +P info
	 * @var UNDETRUCTIBLE_WALL is undetructible wall object type
	 * @var DETRUCTIBLE_WALL is detructible wall object type
	 * @var LAND is free space object type
	 *
	 * @var DANGER is danger status
	 * @var SAFE is safe status
	 * @var ARRIVE is status when AI arrive at the target
	 *
	 * @var MOVE_UP is command to move up
	 * @var MOVE_LEFT is command to move left
	 * @var MOVE_DOWN is command to move down
	 * @var MOVE_RIGHT is command to move right
	 * @var DROP_BOMB is command to drop bomb
	 * @var STAY is command to stay
	 *
	 * @var MAX is maximum fitness function
	 */
	public static final boolean DEBUG_OUTPUT_ENABLE = false;
	protected final String PLAYER = "Player";
	protected final String NPM = "AI_1506689143_Rozi";
	protected final String ENEMY = "Enemy";
	protected final String BOMB = "Bomb";
	protected final String FLARE = "Flare";
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
	
	/**
	 * @var condition is current AI condition
	 * @var player index is index AI in Player[]
	 * @var last move store the last command of AI
	 * @var turn is counter turn
	 */
	protected String condition = SAFE;
	protected int playerIndex = 0;
	protected String lastMove = STAY;
	protected int turn = 0;
	

	/**
	 * class to process every AI movement 
	 */
	class GameProcess {
		/**
		 * @var map is the bomberman board object
		 * @var output is the command to print
		 * @var y is player ordinate location
		 * @var x is player axis location
		 */
		private Map map;
		private String output;
		private int y;
		private int x;

		/**
		 * constructor
		 * @param  map [the bomberman board object]
		 */
		public GameProcess(Map map) {
			this.map = map;
			this.output = STAY;
			y = this.map.playerY;
			x = this.map.playerX;
		}

		/**
		 * method to start game processing
		 * 
		 */
		public void start() {
			Node[][] tile = this.map.getObjects();
			GameObject wall = this.nearestWall(this.map.getWalls());
			GameObject powerUp = this.nearestPowerUp(this.map.getPowerUps());

			// compare the distance of powerup and wall
			if (compareDist(powerUp, wall) < 0) {
				turn = 2;
			} else {
				turn = 0;
			}

			// fp is fitness funtion on [y][x] location
			int fp = this.fitnessFunction(tile, y, x, 0);

			// if that location is dangerous
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

				String tempOutput = this.moveToTarget(wall);

				// start searh wall
				if (turn == 0) {
					if (condition == ARRIVE) {
						turn = 1;
						this.output = DROP_BOMB;
						condition = SAFE;
					} else {
						this.output = this.moveToTarget(wall);
					}

					return;

				// start escape from bomb
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
						turn = 0;
						return;
					} else {
						this.output = DROP_BOMB;
						turn = 0;
						return;
					}


				// start search power up
				} else if (turn == 2) {
					if (powerUp == null) {
						turn = 0;
					} else {
						this.output = this.moveToTarget(powerUp);
					}
				}
			}
			
		}

		/**
		 * compare distace of two game object wth the AI
		 * @param  a [game object a]
		 * @param  b [game object b]
		 * @return   []
		 */
		public int compareDist(GameObject a, GameObject b) {

			if (a == null) {
				return 1;
			}

			if (b == null) {
				return -1;
			}
			int yA = a.getY();
			int xA = a.getX();

			int yB = b.getY();
			int xB = b.getX();

			int distToA = Math.abs(this.y - yA) + Math.abs(this.x - xA);
			int distToB = Math.abs(this.y - yB) + Math.abs(this.x - xB);

			if (distToA <= distToB) {
				return -1;
			} else {
				return 1;
			}
		}


		/**
		 * find fitness funtion at the point
		 * @param  tile   [objects2 in bomberman board]
		 * @param  y      [y location]
		 * @param  x      [x location]
		 * @param  except [flag to find fp except that way]
		 * @return        [fitness point]
		 */
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

		/**
		 * check the move valid or not
		 * @param  y [y location]
		 * @param  x [x location]
		 * @return   [true if valid, else if invalid]
		 */
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

		/**
		 * find best movement to a target
		 * @param  target [game object target]
		 * @return        [String movement]
		 */
		public String moveToTarget(GameObject target) {

			int fp_atas = MAX;
			int fp_kiri = MAX;
			int fp_bawah = MAX;
			int fp_kanan = MAX;

			int yTarget = target.getY();
			int xTarget = target.getX();



			if (isValidMove(this.y-1, this.x)) {
				fp_atas = Math.abs(this.y-1 - yTarget) + Math.abs(this.x - xTarget);
				if (this.fitnessFunction(this.map.getObjects(), y-1, x, 3) < 0) {
					fp_atas = MAX;
				}
			}

			if (isValidMove(this.y, this.x-1)) {
				fp_kiri = Math.abs(this.y - yTarget) + Math.abs(this.x-1 - xTarget);
				if (this.fitnessFunction(this.map.getObjects(), y, x-1, 4) < 0) {
					fp_kiri = MAX;
				}
			}

			if (isValidMove(this.y+1, this.x)) {
				fp_bawah = Math.abs(this.y+1 - yTarget) + Math.abs(this.x - xTarget);
				if (this.fitnessFunction(this.map.getObjects(), y+1, x, 1) < 0) {
					fp_bawah = MAX;
				}
			}

			if (isValidMove(this.y, this.x+1)) {
				fp_kanan = Math.abs(this.y - yTarget) + Math.abs(this.x+1 - xTarget);
				if (this.fitnessFunction(this.map.getObjects(), y, x+1, 2) < 0) {
					fp_kanan = MAX;
				}
			} 


			int fp = Math.min(fp_atas, Math.min(fp_kiri, Math.min(fp_bawah, fp_kanan)));


			// random the sequence
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


		/**
		 * find nearest power up from player
		 * @param  powerUps [array list of objects]
		 * @return          [nearest power Ups]
		 */
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

		/**
		 * find nearest wall
		 * @param  walls [array list of wall]
		 * @return       [nearest wall]
		 */
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

		/**
		 * getter output
		 * @return [output command]
		 */
		public String getOutput() {
			return this.output;
		}
	}

	/**
	 * class to represent bomberman board int node of game object
	 */
	class Map {

		/**
		 * @var objects is all object in bomberman board
		 * @var map is string input from board
		 * @var detructibleWalls is array list of wall
		 * @var powerUps is array list of power up
		 */
		private Node[][] objects;
		private String[] map;
		private Player[] players;
		private ArrayList<DestructibleWall> detructibleWalls;
		private ArrayList<GameObject> powerUps; 
		protected int playerY = 0;
		protected int playerX = 0;

		/**
		 * constructor
		 * @param  map     [bomberman board]
		 * @param  players [players object]
		 * @param  row     [number of row]
		 * @param  column  [number of column]
		 * @return         [description]
		 */
		public Map(String[] map, Player[] players, int row, int column) {
			this.map = map;
			this.objects = new Node[row][column];
			this.players = players;
			this.detructibleWalls = new ArrayList<DestructibleWall>();
			this.powerUps = new ArrayList<GameObject>();
		}

		/**
		 * render input board into bomberman object
		 */
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

		/**
		 * find multiple object in one node
		 * @param  type [types of objects]
		 * @param  y    [y location]
		 * @param  x    [x location]
		 * @return      [arraylist of game object]
		 */
		public ArrayList<GameObject> findMultipleObject(String[] type, int y, int x) {

			ArrayList<GameObject> multipleObject = new ArrayList<GameObject>();
			for (int i = 0; i < type.length ;i++ ) {
				GameObject node = this.find(type[i], y, x);
				multipleObject.add(node);
			}

			return multipleObject;

		}

		/**
		 * find an object in node 
		 * @param  type [type of object]
		 * @param  y    [y location]
		 * @param  x    [x location]
		 * @return      [game object]
		 */
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

		/**
		 * check if a string is numeric or not
		 * @param  s [String input]
		 * @return   [true if string is numeric, false if not numeric]
		 */
		private boolean isNumeric(String s) {
			return s.matches("[-+]?\\d*\\.?\\d+");
		}

		/**
		 * get all objects in board
		 * @return [arrays of objects]
		 */
		public Node[][] getObjects(){
			return this.objects;
		}

		/**
		 * setter
		 * @param objects [description]
		 */
		public void setObjects(Node[][] objects) {
			this.objects = objects;
		}

		/**
		 * getter
		 * @return [description]
		 */
		public ArrayList<DestructibleWall> getWalls(){
			return this.detructibleWalls;
		}

		/**
		 * getter
		 * @return [description]
		 */
		public ArrayList<GameObject> getPowerUps(){
			return this.powerUps;
		}

	}

	/**
	 * class to encapsulate game object into arraylist
	 */
	class Node { 
		/**
		 * @var node is arraylist of game object
		 */
		protected ArrayList<GameObject> node;
		
		/**
		 * constructor
		 * @param  node [node]
		 * @return      [description]
		 */
		public Node(ArrayList<GameObject> node) {
			this.node = node;
		}
	}

	/**
	 * class to represent all object in bomberman board
	 */
	class GameObject {
		/**
		 * @var type is type of the object
		 * @var y is y location
		 * @var x is x location
		 * @var isPassable is flag passable
		 * @var name is object name
		 */
		private String type;
		private int y;
		private int x;
		private boolean isPassable;
		private String name;

		/**
		 * constructor
		 * @param  type       [description]
		 * @param  y          [description]
		 * @param  x          [description]
		 * @param  isPassable [description]
		 * @param  name       [description]
		 * @return            [description]
		 */
		public GameObject(String type, int y, int x, boolean isPassable, String name) {
			this.type = type;
			this.y = y;
			this.x = x;
			this.isPassable = isPassable;
			this.name = name;
		}
		/**
		 * getter type
		 * @return [description]
		 */
		public String getType() {
			return type;
		}

		/**
		 * setter type
		 * @param type [description]
		 */
		public void setType(String type) {
			this.type = type;
		}

		/**
		 * setter y
		 * @return [description]
		 */
		public int getY() {
			return y;
		}

		/**
		 * setter y
		 * @param y [description]
		 */
		public void setY(int y) {
			this.y = y;
		}

		/**
		 * getter x
		 * @return [description]
		 */
		public int getX() {
			return x;
		}

		/**
		 * setter x
		 * @param x [description]
		 */
		public void setX(int x) {
			this.x = x;
		}

		/**
		 * getter isPassable
		 * @return [description]
		 */
		public boolean isPassable() {
			return isPassable;
		}

		/**
		 * setter isPassable
		 * @param passable [description]
		 */
		public void setPassable(boolean passable) {
			isPassable = passable;
		}

		/**
		 * getter name
		 * @return [description]
		 */
		public String getName() {
			return name;
		}

		/**
		 * setter name
		 * @param name [description]
		 */
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


	/**
	 * class represent player in bomberman board
	 */
	class Player extends GameObject {
		/**
		 * @var index is player index in input
		 * @var bombCount is bomb counter of a player
		 * @var bombMax is max bomb
		 * @var bombPower is bomb range
		 * @var status is status of a player
		 * @var score is player score
		 */
		private int index;
		private int bombCount;
		private int bombMax;
		private int bombPower;
		private String status;
		private int score;

		/**
		 * constructor
		 */
		public Player (String type, int y, int x, boolean isPassable, String name, int index, int bombCount, int bombMax, int bombPower, String status ,int score) {
			super(type, y, x, isPassable, name);
			this.index = index;
			this.bombCount = bombCount;
			this.bombMax = bombMax;
			this.bombPower = bombPower;
			this.status = status;
			this.score = score;

		}

		/**
		 * zero constructor
		 */
		public Player() {
			super("None", -1, -1, true, "None");
			this.index = -1;
			this.bombCount = -1;
			this.bombMax = -1;
			this.bombPower = -1;
			this.status = "None";
			this.score = -1;
		}

		/**
		 * getter index
		 * @return [index]
		 */
		public int getIndex() {
			return this.index;
		}

		/**
		 * setter index
		 * @param index [description]
		 */
		public void setIndex(int index) {
			this.index = index;
		}

		/**
		 * getter bomb count
		 * @return [bomb count]
		 */
		public int getBombCount() {
			return this.bombCount;
		}

		/**
		 * getter bombCount
		 * @param bombCount [description]
		 */
		public void setBombCount(int bombCount) {
			this.bombCount = bombCount;
		}

		/**
		 * getter max bomb max
		 * @return [description]
		 */
		public int getBombMax() {
			return this.bombMax;
		}

		/**
		 * setter bomb max
		 * @param bombMax [description]
		 */
		public void setBombMax(int bombMax) {
			this.bombMax = bombMax;
		}

		/**
		 * getter bomb range
		 * @return [description]
		 */
		public int getBombPower() {
			return this.bombPower;
		}

		/**
		 * setter bomb range
		 * @param bombPower [description]
		 */
		public void setBombPower(int bombPower) {
			this.bombPower = bombPower;
		}

		/**
		 * getter status
		 * @return [description]
		 */
		public String getStatus() {
			return this.status;
		}

		/**
		 * setter status
		 * @param status [description]
		 */
		public void setStatus(String status) {
			this.status = status;
		}

		/**
		 * getter score
		 * @return [description]
		 */
		public int getScore() {
			return this.score;
		}

		/**
		 * setter status
		 * @param score [description]
		 */
		public void setScore(int score) {
			this.score = score;
		}

		@Override
		public String toString() {
			return this.getType() + " status " + this.getStatus(); 
		}
	}

	
	/**
	 * class represent bomb in bomberman board
	 */
	class Bomb extends GameObject {
		/**
		 * @var bombPower is bomb range
		 * @var bombTime is bomb timer
		 */
		private int bombPower;
		private int bombTime;

		/**
		 * constructor
		 */
		public Bomb(String type, int y, int x, boolean isPassable, String name, int bombPower, int bombTime) {
			super(type, y, x, isPassable, name);
			this.bombPower = bombPower;
			this.bombTime = bombTime;
		}

		/**
		 * getter bomb range
		 * @return [description]
		 */
		public int getBombPower() {
			return bombPower;
		}

		/**
		 * setter bomb range
		 * @param bombPower [description]
		 */
		public void setBombPower(int bombPower) {
			this.bombPower = bombPower;
		}

		/**
		 * getter bomb time
		 * @return [description]
		 */
		public int getBombTime() {
			return bombTime;
		}

		/**
		 * setter bomb time
		 * @param bombTime [description]
		 */
		public void setBombTime(int bombTime) {
			this.bombTime = bombTime;
		}

		@Override
		public String toString() {
			return this.getType() + " : " + this.getBombPower() + "/" + this.getBombTime();
		} 
		
	}


	class Flare extends GameObject {
		/**
		 * @var flareTime is time of flare
		 */
		private int flareTime;

		/**
		 * comstructor
		 */
		public Flare(String type, int y, int x, boolean isPassable, String name, int flareTime) {
			super(type, y, x, isPassable, name);
			this.flareTime = flareTime;
		}

		/**
		 * getter flare time
		 * @return [description]
		 */
		public int getFlareTime() {
			return flareTime;
		}

		/**
		 * setter flare time
		 * @param flareTime [description]
		 */
		public void setFlareTime(int flareTime) {
			this.flareTime = flareTime;
		}

		@Override
		public String toString() {
			return this.getType() + " : " + this.getFlareTime(); 
		}

	}

	/**
	 * class represent detructible wall in bomberman board
	 */
	class DestructibleWall extends GameObject {
		/**
		 * @var powerUpInfo is power up type
		 */
		private String powerUpInfo;

		/**
		 * costructor
		 */
		public DestructibleWall(String type, int y, int x, boolean isPassable, String name, String powerUpInfo) {
			super(type, y, x, isPassable, name);
			this.powerUpInfo = powerUpInfo;
		}

		/**
		 * getter power up type
		 * @return [description]
		 */
		public String getPowerUpInfo(){
			return this.powerUpInfo;
		}

		/**
		 * setter power up type
		 * @param powerUpInfo [description]
		 */
		public void setPowerUpInfo(String powerUpInfo) {
			this.powerUpInfo = powerUpInfo;
		}

		@Override
		public String toString() {
			return this.getType() + " : " + this.getPowerUpInfo(); 
		}
	}


	/**
	 * main program
	 */
	public static void main(String[] args) throws Exception {

		AI_1506689143_Rozi ai = new AI_1506689143_Rozi();

		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		try{

			while(true) {

				// main information of board
				String input = "";
				Map map = null;
				int turn = 0;
				int playersNum = 0;
				Player[] players = new Player[playersNum];
				int rowNum = 0;
				int columnNum = 0;

				// parse the board
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

					// parse the board
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
