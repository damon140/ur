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
                // FIXME: human rolls zero bug, hack and try
                //roll = dice.roll();
                // calculates a move for zero roll, a bug
                roll = 0;

                urView.clearLastChosen()
            }

            val moves: Map<Square, Square> = ur.askMoves(currentTeam, roll)

            val smallBoard: List<String> = horizontalDrawnBoard.smallBoard()
            urView.updateWhiteCounters(horizontalDrawnBoard.countersLine(Team.white))
            urView.updateBlackCounters(horizontalDrawnBoard.countersLine(Team.black))
            urView.updateBoard(smallBoard.get(0), smallBoard.get(1), smallBoard.get(2))
            urView.updateInstructions(currentTeam, roll, moves) {
                playUr(roll, true)
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
                // FIXME: need to change turn owner
                // FIXME: bug with taking in list of moves is present
                // FIXME: double white move bug is present
                continue;
            }

            if ("x".equals(input)) {
                return
            }

            val moveIndex: Int = input.toInt()
            val fromSquare: Square = moves.keys.toList()[moveIndex - 1];

            var result = ur.moveCounter(fromSquare, roll);
            if (result == Ur.MoveResult.gameOver) {
                //System.out.println("Game won by " + playArea.currentTeam());
                // TODO: add game alert here
                return
            }
            isCallBack = false;
        }
    }
}