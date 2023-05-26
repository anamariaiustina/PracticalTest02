package ro.pub.cs.systems.eim.practicaltest02.model;

public class NumbersInformation {

    private String firstNumber;
    private String secondNumber;

    public NumbersInformation() {
        this.firstNumber = null;
        this.secondNumber = null;
    }

    public NumbersInformation(String firstNumber, String secondNumber) {
        this.firstNumber = firstNumber;
        this.secondNumber = secondNumber;
    }

    public String getFirstNumber() {
        return firstNumber;
    }

    public void setFirstNumber(String firstNumber) {
        this.firstNumber = firstNumber;
    }

    public String getSecondNumber() {
        return secondNumber;
    }

    public void setSecondNumber(String secondNumber) {
        this.secondNumber = secondNumber;
    }
}
