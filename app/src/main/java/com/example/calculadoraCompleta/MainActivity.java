package com.example.calculadoraCompleta;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class MainActivity extends Activity {

    private final Queue<String> number1Integer = new LinkedList<>();
    private final Queue<String> number1Decimal = new LinkedList<>();
    private boolean number1IsDecimal = false;

    private final Queue<String> number2Integer = new LinkedList<>();
    private final Queue<String> number2Decimal = new LinkedList<>();
    private boolean number2IsDecimal = false;

    private final Queue<String> operations = new LinkedList<>();

    private boolean isResult = false;
    private boolean isNegativeResult = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
    }

    private void clearVisor() {

        if (this.isResult) {
            this.isResult = false;
            this.isNegativeResult = false;

            final EditText visor = this.findViewById(R.id.visor);
            visor.setText("");

            this.number1Integer.clear();
            this.number1Decimal.clear();
            this.number1IsDecimal = false;

            this.number2Integer.clear();
            this.number2Decimal.clear();
            this.number2IsDecimal = false;

            this.operations.clear();
        }
    }

    public void clickNumber(final View view) {
        this.clearVisor();

        final String number = view.getTag().toString();

        if (this.operations.isEmpty()) {
            if (this.number1IsDecimal) {
                this.number1Decimal.add(number);
            } else {
                this.number1Integer.add(number);
            }
        } else {
            if (this.number2IsDecimal) {
                this.number2Decimal.add(number);
            } else {
                this.number2Integer.add(number);
            }
        }

        final EditText visor = this.findViewById(R.id.visor);
        visor.getText().append(number);
    }

    public void clickDot(final View view) {
        this.clearVisor();

        if (this.operations.isEmpty() && (this.number1IsDecimal || this.number1Integer.isEmpty())) {
            return;
        } else if (!this.operations.isEmpty() && (this.number2IsDecimal || this.number2Integer.isEmpty())) {
            return;
        }

        if (this.operations.isEmpty()) {
            this.number1IsDecimal = true;
        } else {
            this.number2IsDecimal = true;
        }

        final EditText visor = this.findViewById(R.id.visor);
        visor.getText().append(".");
    }

    public void clickOperation(final View view) {
        final EditText visor = this.findViewById(R.id.visor);

        if (visor.getText().toString().equals("ERROR")) {
            visor.setText("");
        }

        if (this.isResult) {
            this.isResult = false;
        }

        if (!this.operations.isEmpty() || this.number1Integer.isEmpty()) {
            return;
        }

        final String operation = view.getTag().toString();
        this.operations.add(operation);
        visor.getText().append(operation);
    }

    public void clickErase(final View view) {
        final String eraseType = view.getTag().toString();
        final EditText visor = this.findViewById(R.id.visor);

        if (eraseType.equals("C")) {
            visor.setText("");

            this.number1Integer.clear();
            this.number1Decimal.clear();
            this.number1IsDecimal = false;

            this.number2Integer.clear();
            this.number2Decimal.clear();
            this.number2IsDecimal = false;

            this.operations.clear();
        } else {
            this.clearVisor();

            final int length = visor.getText().length();
            if (length > 0) {
                final String deletedChar = String.valueOf(visor.getText().charAt(length - 1));
                visor.getText().delete(length - 1, length);

                if (length == 1 && this.isNegativeResult) {
                    this.isNegativeResult = false;
                } else if (deletedChar.equals("+") || deletedChar.equals("-") || deletedChar.equals("*") || deletedChar.equals("/")) {
                    this.operations.clear();
                } else if (deletedChar.equals(".")) {
                    if (this.operations.isEmpty()) {
                        this.number1IsDecimal = false;
                    } else {
                        this.number2IsDecimal = false;
                    }
                } else {
                    if (this.operations.isEmpty()) {
                        if (this.number1IsDecimal) {
                            this.number1Decimal.poll();
                        } else {
                            this.number1Integer.poll();
                        }
                    } else {
                        if (this.number2IsDecimal) {
                            this.number2Decimal.poll();
                        } else {
                            this.number2Integer.poll();
                        }
                    }
                }
            }
        }
    }

    private double convertNumber(final Queue<String> numberInteger, final Queue<String> numberDecimal) {
        final StringBuilder number = new StringBuilder();

        while (!numberInteger.isEmpty()) {
            number.append(numberInteger.poll());
        }

        number.append(".");

        while (!numberDecimal.isEmpty()) {
            number.append(numberDecimal.poll());
        }

        return Double.parseDouble(number.toString());
    }

    private String executeOperation() {
        final double number1 = this.convertNumber(this.number1Integer, this.number1Decimal) * (this.isNegativeResult ? -1 : 1);
        this.number1IsDecimal = false;

        final double number2 = this.convertNumber(this.number2Integer, this.number2Decimal);
        this.number2IsDecimal = false;

        final String operation = this.operations.poll();

        switch (Objects.requireNonNull(operation)) {
            case "+":
                return String.valueOf(number1 + number2);
            case "-":
                return String.valueOf(number1 - number2);
            case "*":
                return String.valueOf(number1 * number2);
            case "/":
                return (number2 == 0.0) ? "ERROR" : String.valueOf(number1 / number2);
            default:
                return "ERROR";
        }
    }

    private void setNumber1(final String number1) {
        this.isResult = true;
        this.isNegativeResult = false;

        if (number1.equals("ERROR")) {
            return;
        }

        number1.chars().forEach(c -> {
            final String actualChar = String.valueOf((char) c);

            if (actualChar.equals("-")) {
                this.isNegativeResult = true;
            } else if (actualChar.equals(".")) {
                this.number1IsDecimal = true;
            } else {
                if (this.number1IsDecimal) {
                    this.number1Decimal.add(actualChar);
                } else {
                    this.number1Integer.add(actualChar);
                }
            }
        });
    }

    public void clickEqual(final View view) {
        this.clearVisor();

        if (this.number1Integer.isEmpty() || this.operations.isEmpty() || this.number2Integer.isEmpty()) {
            return;
        }

        final String result = this.executeOperation();
        this.setNumber1(result);

        final EditText visor = this.findViewById(R.id.visor);
        visor.setText(result);
    }
}
