package byow.Core;

public class StringInput implements Input {
    String input;
    int index;
    int length;

    StringInput(String input) {
        this.input = input+":Q";
        index = 0;
        length = input.length();
    }

    @Override
    public char getNextKey() {
        if (index < length) {
            return input.charAt(index++);
        }
        return ' ';
    }

    @Override
    public boolean possibleNextInput() {
        return true;
    }
}
