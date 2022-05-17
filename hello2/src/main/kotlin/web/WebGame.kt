package web

import com.damon140.ur.*
import kotlinx.browser.document

public class WebGame() {

    private var dice: Dice
    private var playArea: PlayArea
    private var ur: Ur
    private var horizontalDrawnBoard: HorizontalDrawnBoard
    private var pageObject: UrPageObject
    private var urView: UrView
    private var playerSetup: PlayerSetup
    private var moveSuppliers: HashMap<Team, PlayerSetup.InputSupplier>

    init {
        this.dice = Dice()
        console.log("rolled " + dice.roll())

        this.playArea = PlayArea()
        console.log("made play area")

        this.ur = Ur(playArea)
        console.log("made ur")

        console.log("will make a board")
        this.horizontalDrawnBoard = HorizontalDrawnBoard(playArea)

        console.log("made a board")

        var f = horizontalDrawnBoard.fullBoard()
        console.log("Full board: " + f)

        this.pageObject = UrPageObject(document)
        this.urView = UrView(pageObject)

        // TODO: add UI bobs to constructor
        this.playerSetup = PlayerSetup(this.urView)

        // TODO: maybe not a member??
        this.moveSuppliers = HashMap<Team, PlayerSetup.InputSupplier>()
        moveSuppliers.put(Team.white, playerSetup.getPlayer(Team.white))
        moveSuppliers.put(Team.black, playerSetup.getPlayer(Team.black))

        setupAndPlayUr()
    }

    fun setupAndPlayUr() {
        // player setup


        // run a game
        playUr(0, false)
    }


    fun playUr(humansRoll:Int, isCallBackIn:Boolean) {
        var isCallBack = isCallBackIn;
        while(true) {
            val currentTeam = ur.currentTeam()
            var roll: Int

            if (isCallBack) {
                roll = humansRoll;
            } else {
                roll = dice.roll();
                urView.clearLastChosen()
            }

            val moves: Map<Square, Square> = ur.askMoves(currentTeam, roll)

            val smallBoard: List<String> = horizontalDrawnBoard.smallBoard()

            val vertBoard: List<String> = horizontalDrawnBoard.verticleBoard2()

            urView.updateWhiteCounters(horizontalDrawnBoard.countersLine(Team.white), playArea.completedCount(Team.white), playArea.unstartedCount(Team.white))
            urView.updateBlackCounters(horizontalDrawnBoard.countersLine(Team.black), playArea.completedCount(Team.black), playArea.unstartedCount(Team.black))
            urView.updateBoard(smallBoard.get(0), smallBoard.get(1), smallBoard.get(2), vertBoard)
            urView.updateInstructions(currentTeam, roll, moves) {
                if (0 == roll || moves.isEmpty()) {
                    // skip processing of human roll, nothing to do
                    playUr(roll, false)
                } else {
                    // regular turn
                    playUr(roll, true)
                }
            }

            val moveSupplier = moveSuppliers.get(currentTeam)!!;

            if (moveSupplier.waitForPlayer()) {
                console.log("Waiting for input of player " + currentTeam);
                return;
            }

            val input = moveSupplier.choose(moves);

            console.log("Making turn of " + currentTeam);
            console.log("Moves are: ");
            moves.entries.forEach { e -> console.log(e.key.name + " -> " + e.value.name) }
            console.log("player input was " + input);

            if (0 == roll) {
                ur.skipTurn(roll);
                continue;
            }

            if (moves.isEmpty()) {
                ur.skipTurn(roll);
                continue;
            }

            if ("x".equals(input)) {
                return
            }

            val moveIndex: Int = input.toInt()
            val fromSquare: Square = moves.keys.toList()[moveIndex - 1];

            var result = ur.moveCounter(fromSquare, roll);

            // FIXME: game over bug here for black
            if (result == Ur.MoveResult.gameOver) {
                //System.out.println("Game won by " + playArea.currentTeam());
                // TODO: add game alert here
                return
            }
            isCallBack = false;
        }
    }
}