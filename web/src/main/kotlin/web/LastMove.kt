package web

class LastMove {

    private var lastChosen: String = "";

    fun clearLastChosen() {
        this.lastChosen = ""
    }

    fun hasLastChosen(): Boolean {
        console.log("lastChosen is [" + this.lastChosen + "]")
        console.log("lastChosen length is " + this.lastChosen.length)
        return this.lastChosen.isNotEmpty()
    }

    fun getLastChosen(): String {
        return lastChosen
    }

    fun setLastChosen(newValue: String) {
        // FIXME: migrate this state & fn to new object so we can use in the other view

        this.lastChosen = newValue
        console.log("Last chosen is now " + this.lastChosen)
    }

}