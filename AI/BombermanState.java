import java.util.ArrayList;
import java.util.Arrays;

/**
 * Representasi state dari game bomberman pada turn tertentu
 */

public class BombermanState {
    private int turn;
    private int player_count;
    private ArrayList<BombermanPlayer> playerList;
    private BombermanBoard board;

    public BombermanState (String input) {
        String[] inputSplit = input.split("\n");
        playerList = new ArrayList<>();
        // get the turn
        turn = Integer.parseInt(inputSplit[0].split(" ")[1]);
        player_count = Integer.parseInt(inputSplit[1].split(" ")[1]);

        for (int i = 2; i < player_count+2; i++) {
            String playerText = inputSplit[i];
            String[] playerSplitText = playerText.split(" ");

            String name = playerSplitText[1];

            String bombCountAndMaxString = playerSplitText[2].substring(5);
            String[] bombSplitString = bombCountAndMaxString.split("\\/");
            int bombCount = Integer.parseInt(bombSplitString[0]);
            int bombMax = Integer.parseInt(bombSplitString[1]);

            String rangeString = playerSplitText[3].substring(6);
            int range = Integer.parseInt(rangeString);

            String status = playerSplitText[4];

            int playerPoint = Integer.parseInt(playerSplitText[5]);
            playerList.add(new BombermanPlayer(name,bombCount, bombMax, range, status, playerPoint));
        }

        // get the board size
        String sizeText = inputSplit[player_count + 2];
        String[] boardSizeSplit = sizeText.split(" ");
        int boardRow = Integer.parseInt(boardSizeSplit[1]);
        int boardColumn = Integer.parseInt(boardSizeSplit[2]);

        String[] boardString = Arrays.copyOfRange(inputSplit, player_count+3, player_count+4+boardRow);

        this.board = new BombermanBoard(boardString,boardRow, boardColumn);
        String[] playerLocationArray = board.getPlayerLocation(player_count);

        // SET PLAYER
        for(int iteratorPlayer = 0; iteratorPlayer < playerLocationArray.length; iteratorPlayer++) {
            int posXPlayer = Integer.parseInt(playerLocationArray[iteratorPlayer].split(" ")[1]);
            int posYPlayer = Integer.parseInt(playerLocationArray[iteratorPlayer].split(" ")[0]);


            playerList.get(iteratorPlayer).setPlayerXLocation(posXPlayer);
            playerList.get(iteratorPlayer).setPlayerYLocation(posYPlayer);
        }

    }

    /**
     * Mendapatkan indeks player
     * @param playerName nama player yang ingin dicari
     * @return indeks player jika ditemukan, jika tidak akan mengembalikan nilai -1
     */
    public int getPlayerIndex(String playerName) {
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getPlayerName().equals(playerName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Mendapatkan array 2-D boolean agar tahu lokasi yang dapat dilewati pada koordinat tertentu
     * @return array dua dimensi menandakan apakah bisa dilewati atau tidak suatu koordinat
     */
    public boolean[][] getPassableArray() {
        return board.getPassableNode();
    }

    /**
     * Mendapatkan semua objek pada suatu koordinat
     * @param x koordinat kolom (dari 0)
     * @param y koordinat baris (dari 0)
     * @return Semua object yang berada pada (X,Y)
     */
    public ArrayList<BombermanObject> getAllBombermanObjectOnCoordinate(int x, int y) {
        return board.getAllBombermanObjectOnCoordinate(x,y);
    }

    /**
     * Mendapatkan semua objek bomb yang ada di peta
     * @return ArrayList semua bomb objek
     */
    public ArrayList<BombermanObject> getAllBombObject() {
        return board.getAllBombObject();
    }

    /**
     * Mendapatkan semua objek tembok yang bisa dihancurkan. Bisa berupa tembok power up
     * ataupun tembok biasa saja
     *
     * @return Semua objek tembok yang bisa dihancurkan
     *
     */
    public ArrayList<BombermanObject> getAllDestructibleWallObject() {
        return board.getAllDestructibleWallObject();
    }

    /**
     * Mendapatkan semua objek power up yang bisa didapatkan.
     *
     * @return Semua objek power up yang ada di peta
     *
     */
    public ArrayList<BombermanObject> getAllPowerUpObject() {
        return board.getAllPowerUpObject();
    }

    /**
     * Mendapatkan semua objek tembok yang ada di peta kecuali tembok yang tak bisa dihancurkan
     * @return List semua objek kecuali tembok yang tak bisa dihancurkan
     */
    public ArrayList<BombermanObject> getAllBombermanObjectExceptInDestructibleWall() {
        return board.getAllBombermanObjectExceptInDestructibleWall();
    }

    /**
     * Mendapatkan semua objek flare yang ada di peta
     * @return List semua objek flare yang ada di peta
     */
    public ArrayList<BombermanObject> getAllFlareObject() {
        return board.getAllFlareObject();
    }

    /**
     * Mendapatkan semua tembok yang tidak bisa dihancurkan (entah buat apa ini)
     * @return List semua tembok yang tak bisa dihancurkan
     */
    public ArrayList<BombermanObject> getAllIndestructibleWallObject() {
        return board.getAllIndestructibleWallObject();
    }

    /** SETTER GETTER **/

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getPlayer_count() {
        return player_count;
    }

    public void setPlayer_count(int player_count) {
        this.player_count = player_count;
    }

    public ArrayList<BombermanPlayer> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(ArrayList<BombermanPlayer> playerList) {
        this.playerList = playerList;
    }

    public BombermanBoard getBoard() {
        return board;
    }

    public void setBoard(BombermanBoard board) {
        this.board = board;
    }

    @Override
    public String toString() {
        return "BombermanState{" +
                "turn=" + turn +
                ", player_count=" + player_count +
                ", playerList=" + playerList +
                ", board=" + board +
                '}';
    }
}

class BombermanPlayer {
    private String playerName;
    private int playerBombCount;
    private int bombMax;
    private int rangeBomb;
    private String playerStatus;
    private int playerPoint;

    /*
     * Lokasi X player
     */
    private int playerXLocation;

    /*
     * Lokasi Y player
     */
    private int playerYLocation;

    /**
     *
     * @param playerName Nama player
     * @param playerBombCount Bomb player yang dimiliki sekarang
     * @param bombMax Bomb maksimum yang dapat dimiliki player
     * @param rangeBomb Range bomb dari player
     * @param playerStatus Status playernya
     * @param playerPoint Poin musuh
     */
    public BombermanPlayer(String playerName, int playerBombCount, int bombMax, int rangeBomb, String playerStatus, int playerPoint) {
        this.playerName = playerName;
        this.playerBombCount = playerBombCount;
        this.bombMax = bombMax;
        this.rangeBomb = rangeBomb;
        this.playerStatus = playerStatus;
        this.playerPoint = playerPoint;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getPlayerBombCount() {
        return playerBombCount;
    }

    public void setPlayerBombCount(int playerBombCount) {
        this.playerBombCount = playerBombCount;
    }

    public int getBombMax() {
        return bombMax;
    }

    public void setBombMax(int bombMax) {
        this.bombMax = bombMax;
    }

    public int getRangeBomb() {
        return rangeBomb;
    }

    public void setRangeBomb(int rangeBomb) {
        this.rangeBomb = rangeBomb;
    }

    public String getPlayerStatus() {
        return playerStatus;
    }

    public void setPlayerStatus(String playerStatus) {
        this.playerStatus = playerStatus;
    }

    public int getPlayerPoint() {
        return playerPoint;
    }

    public void setPlayerPoint(int playerPoint) {
        this.playerPoint = playerPoint;
    }

    public int getPlayerXLocation() {
        return playerXLocation;
    }

    public void setPlayerXLocation(int playerXLocation) {
        this.playerXLocation = playerXLocation;
    }

    public int getPlayerYLocation() {
        return playerYLocation;
    }

    public void setPlayerYLocation(int playerYLocation) {
        this.playerYLocation = playerYLocation;
    }

    @Override
    public String toString() {
        return "BombermanPlayer{" +
                "playerName='" + playerName + '\'' +
                ", playerBombCount=" + playerBombCount +
                ", bombMax=" + bombMax +
                ", rangeBomb=" + rangeBomb +
                ", playerStatus='" + playerStatus + '\'' +
                ", playerPoint=" + playerPoint +
                ", playerXLocation=" + playerXLocation +
                ", playerYLocation=" + playerYLocation +
                '}';
    }
    // Add more here...


}

class BombermanBoard {
    private int mapHeight;
    private int mapWidth;
    private MapNode[][] mapNode;

    public BombermanBoard(String[] rowSplitString, int boardRow, int boardColumn) {
        mapNode = new MapNode[boardRow][boardColumn];

        for (int rowIndex = 0; rowIndex < boardRow; rowIndex++) {
            String boardRowString = rowSplitString[rowIndex];
            String[] rowString = boardRowString.split("\\[");

            for (int columnIndex = 1; columnIndex <= boardColumn; columnIndex++) {
                String nodeString = rowString[columnIndex];
                nodeString = nodeString.replaceAll("]", "");
                nodeString = nodeString.trim();
                mapNode[rowIndex][columnIndex-1] = new MapNode(nodeString, columnIndex-1, rowIndex);
            }
        }

        this.mapHeight = boardRow;
        this.mapWidth = boardColumn;
    }

    public String[] getPlayerLocation(int playerTotal) {
        String[] playerLocationArray = new String[playerTotal];

        for(int rowIndex = 0; rowIndex < mapNode.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < mapNode[0].length; columnIndex++) {
                ArrayList<BombermanObject> isiObject = mapNode[rowIndex][columnIndex].getListBombermanObject();

                for (BombermanObject bombermanObject : isiObject) {
                    if (bombermanObject instanceof PlayerObject) {
                        PlayerObject playerObject = (PlayerObject) bombermanObject;
                        playerLocationArray[playerObject.getPlayerIndex()] = playerObject.getObjectYLocation() + " " +
                                playerObject.getObjectXLocation();
                    }
                }
            }
        }
        return playerLocationArray;
    }

    public ArrayList<BombermanObject> getAllBombermanObjectOnCoordinate(int x, int y) {
        return mapNode[y][x].getListBombermanObject();
    }

    public ArrayList<BombermanObject> getAllBombObject() {
        ArrayList<BombermanObject>  bombArrayList = new ArrayList<>();

        for(int rowIndex = 0; rowIndex < mapNode.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < mapNode[0].length; columnIndex++) {
                ArrayList<BombermanObject> isiObject = mapNode[rowIndex][columnIndex].getListBombermanObject();

                for (int i = 0; i < isiObject.size(); i++) {
                    BombermanObject bombermanObject = isiObject.get(i);
                    if (bombermanObject instanceof BombObject) {
                        bombArrayList.add(bombermanObject);
                    }
                }
            }
        }
        return bombArrayList;
    }

    public ArrayList<BombermanObject> getAllDestructibleWallObject() {
        ArrayList<BombermanObject>  destructibleWallObject = new ArrayList<>();
        for(int rowIndex = 0; rowIndex < mapNode.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < mapNode[0].length; columnIndex++) {
                ArrayList<BombermanObject> isiObject = mapNode[rowIndex][columnIndex].getListBombermanObject();

                for (BombermanObject bombermanObject : isiObject) {
                    if (bombermanObject.getObjectType().contains("Destuctible")) {
                        destructibleWallObject.add(bombermanObject);
                    }
                }
            }
        }
        return destructibleWallObject;
    }

    public ArrayList<BombermanObject> getAllIndestructibleWallObject() {
        ArrayList<BombermanObject>  indestructibleWallObject = new ArrayList<>();
        for(int rowIndex = 0; rowIndex < mapNode.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < mapNode[0].length; columnIndex++) {
                ArrayList<BombermanObject> isiObject = mapNode[rowIndex][columnIndex].getListBombermanObject();

                for (BombermanObject bombermanObject : isiObject) {
                    if (bombermanObject.getObjectType().contains("Indestuctible Wall")) {
                        indestructibleWallObject.add(bombermanObject);
                    }
                }
            }
        }
        return indestructibleWallObject;
    }

    public ArrayList<BombermanObject> getAllPowerUpObject() {
        ArrayList<BombermanObject>  powerUpObject = new ArrayList<>();
        for(int rowIndex = 0; rowIndex < mapNode.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < mapNode[0].length; columnIndex++) {
                ArrayList<BombermanObject> isiObject = mapNode[rowIndex][columnIndex].getListBombermanObject();

                for (BombermanObject bombermanObject : isiObject) {
                    if (bombermanObject.getObjectType().contains("Power Up")) {
                        powerUpObject.add(bombermanObject);
                    }
                }
            }
        }
        return powerUpObject;
    }

    public ArrayList<BombermanObject> getAllFlareObject() {
        ArrayList<BombermanObject>  flareObjectList = new ArrayList<>();
        for(int rowIndex = 0; rowIndex < mapNode.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < mapNode[0].length; columnIndex++) {
                ArrayList<BombermanObject> isiObject = mapNode[rowIndex][columnIndex].getListBombermanObject();

                for (BombermanObject bombermanObject : isiObject) {
                    if (bombermanObject instanceof FlareObject) {
                        flareObjectList.add(bombermanObject);
                    }
                }
            }
        }
        return flareObjectList;
    }

    public ArrayList<BombermanObject> getAllBombermanObjectExceptInDestructibleWall() {
        ArrayList<BombermanObject>  allItemExceptIndestrucWall = new ArrayList<>();
        for(int rowIndex = 0; rowIndex < mapNode.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < mapNode[0].length; columnIndex++) {
                ArrayList<BombermanObject> isiObject = mapNode[rowIndex][columnIndex].getListBombermanObject();

                for (BombermanObject bombermanObject : isiObject) {
                    if (!bombermanObject.getObjectType().contains("Indestuctible Wall")) {
                        allItemExceptIndestrucWall.add(bombermanObject);
                    }
                }
            }
        }
        return allItemExceptIndestrucWall;
    }

    public boolean[][] getPassableNode() {
        boolean[][] returnPassable = new boolean[mapHeight][mapWidth];
        for(int rowIndex = 0; rowIndex < mapNode.length; rowIndex++) {

            for (int columnIndex = 0; columnIndex < mapNode[0].length; columnIndex++) {
                ArrayList<BombermanObject> isiObject = mapNode[rowIndex][columnIndex].getListBombermanObject();

                boolean passableFlag = true;
                for (BombermanObject bombermanObject : isiObject) {
                    if (!bombermanObject.isPassable()) {
                        passableFlag = false;
                        break;
                    }
                }
                returnPassable[rowIndex][columnIndex] = passableFlag;
            }
        }

        return returnPassable;
    }

    @Override
    public String toString() {
        return "BombermanBoard{" +
                "mapHeight=" + mapHeight +
                ", mapWidth=" + mapWidth +
                ", mapNode=" + Arrays.toString(mapNode) +
                '}';
    }
}

class MapNode {
    ArrayList<BombermanObject> listBombermanObject;

    public MapNode(String nodeString, int XLocation, int YLocation) {
        // Split with ;
        listBombermanObject = new ArrayList<>();
        String[] listOfItemInNodeSplit = nodeString.split(";");

        // List item
        for (int i = 0; i < listOfItemInNodeSplit.length; i++) {
            String itemString =  listOfItemInNodeSplit[i];

            //Player
            if (itemString.isEmpty()) {

            }

            else if (isNumeric(itemString)) {
                PlayerObject playerObject = new PlayerObject("Player",YLocation, XLocation, true, itemString, Integer.parseInt(itemString));
                listBombermanObject.add(playerObject);
            }

            // Indestructible wall
            else if (itemString.equals("###")) {
                BombermanObject indestructibleObject = new BombermanObject("Indestuctible Wall",YLocation, XLocation, false, itemString);
                listBombermanObject.add(indestructibleObject);
            }

            else if (itemString.equals("XBX")) {
                BombermanObject destructibleObject = new BombermanObject("Destuctible Wall +Bomb",YLocation, XLocation, false, itemString);
                listBombermanObject.add(destructibleObject);
            }

            else if (itemString.equals("XXX")) {
                BombermanObject destructibleObject = new BombermanObject("Destuctible Wall",YLocation, XLocation, false, itemString);
                listBombermanObject.add(destructibleObject);
            }

            else if (itemString.equals("XPX")) {
                BombermanObject destructibleObject = new BombermanObject("Destuctible Wall +Power",YLocation, XLocation, false, itemString);
                listBombermanObject.add(destructibleObject);
            }

            else if (itemString.equals("+B")) {
                BombermanObject powerUpObject = new BombermanObject("Power Up +Bomb",YLocation, XLocation, true, itemString);
                listBombermanObject.add(powerUpObject);
            }

            else if (itemString.equals("+P")) {
                BombermanObject powerUpObject = new BombermanObject("Power Up +Power",YLocation, XLocation, true, itemString);
                listBombermanObject.add(powerUpObject);
            }

            else if (itemString.charAt(0) == 'F') {
                int flareTime = Integer.parseInt(itemString.substring(1));
                FlareObject flareObject = new FlareObject("Flare", YLocation, XLocation, true, itemString, flareTime);
                listBombermanObject.add(flareObject);
            }

            else if (itemString.charAt(0) == 'B') {
                int bombTime = Integer.parseInt(itemString.substring(itemString.length()-1));
                int bombPower = Integer.parseInt(itemString.substring(1,itemString.length()-1));
                BombObject bombObject = new BombObject("Bomb", YLocation, XLocation, false, itemString,bombPower, bombTime);
                listBombermanObject.add(bombObject);
            }
        }
    }

    private boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }

    @Override
    public String toString() {
        return "MapNode{" +
                "listBombermanObject=" + listBombermanObject +
                '}';
    }

    public ArrayList<BombermanObject> getListBombermanObject() {
        return listBombermanObject;
    }

    public void setListBombermanObject(ArrayList<BombermanObject> listBombermanObject) {
        this.listBombermanObject = listBombermanObject;
    }
}

class BombermanObject {
    private String objectType;
    private int objectYLocation;
    private int objectXLocation;
    private boolean isPassable;
    private String objectName;

    public BombermanObject(String objectType, int objectYLocation, int objectXLocation, boolean isPassable, String objectName) {
        this.objectType = objectType;
        this.objectYLocation = objectYLocation;
        this.objectXLocation = objectXLocation;
        this.isPassable = isPassable;
        this.objectName = objectName;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public int getObjectYLocation() {
        return objectYLocation;
    }

    public void setObjectYLocation(int objectYLocation) {
        this.objectYLocation = objectYLocation;
    }

    public int getObjectXLocation() {
        return objectXLocation;
    }

    public void setObjectXLocation(int objectXLocation) {
        this.objectXLocation = objectXLocation;
    }

    public boolean isPassable() {
        return isPassable;
    }

    public void setPassable(boolean passable) {
        isPassable = passable;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    @Override
    public String toString() {
        return "BombermanObject{" +
                "objectType='" + objectType + '\'' +
                ", objectYLocation=" + objectYLocation +
                ", objectXLocation=" + objectXLocation +
                ", isPassable=" + isPassable +
                ", objectName='" + objectName + '\'' +
                '}';
    }
}

class BombObject extends BombermanObject {
    private int bombPower;
    private int bombTime;

    public BombObject(String objectType, int objectYLocation, int objectXLocation, boolean isPassable, String objectName, int bombPower, int bombTime) {
        super(objectType, objectYLocation, objectXLocation, isPassable, objectName);
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
}

class FlareObject extends BombermanObject {
    private int flareTime;

    public int getFlareTime() {
        return flareTime;
    }

    public void setFlareTime(int flareTime) {
        this.flareTime = flareTime;
    }

    public FlareObject(String objectType, int objectYLocation, int objectXLocation, boolean isPassable, String objectName, int flareTime) {

        super(objectType, objectYLocation, objectXLocation, isPassable, objectName);
        this.flareTime = flareTime;
    }
}

class PlayerObject extends BombermanObject {
    private int playerIndex;

    public int getPlayerIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    private String playerName;

    public PlayerObject(String objectType, int objectYLocation, int objectXLocation, boolean isPassable, String objectName, int playerIndex) {
        super(objectType, objectYLocation, objectXLocation, isPassable, objectName);
        this.playerIndex = playerIndex;
    }
}

