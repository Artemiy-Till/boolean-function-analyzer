import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {


            System.out.println("Введите количество переменных");


            while (!scanner.hasNextInt()) {
                System.out.println("Можно целое число, пожалуйста");
                scanner.nextLine();
                System.out.println("Введите количество переменных");
            }


            int count = scanner.nextInt();
            scanner.nextLine();

            if (count > 20) {
                System.out.println("Давайте число переменных меньше 21, пожалуйста\n");
                continue;
            }

            if (count <= 0){
                break;
            }

            int countOfRows = (int) Math.pow(2, count);


            System.out.println("Введите значения функции (двоичная строка длины " + countOfRows + "):");
            String value = scanner.nextLine().trim();

            if (!Validator.validateBinaryString(value, countOfRows)) {
                continue;
            }

            truthTable(count, countOfRows, value);
            normalForms(count, countOfRows, value);
            removeFictiveVariables(count, countOfRows, value);
        }
    }


    static class Validator {
        public static boolean validateBinaryString(String line, int expectedLen) {
            if (line.isEmpty()) {
                System.out.println("Давайте без пустой строки, пожалуйста");
                return false;
            }

            if (line.length() != expectedLen) {
                System.out.println("Вы, наверное, не посчитали количество переменных неверная длина строки (нужно " + expectedLen + ")");
                return false;
            }

            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (c != '0' && c != '1') {
                    System.out.println("На дискре нам говорили, что булева функция состоит только из 0 и 1");
                    return false;
                }
            }

            return true;
        }
    }


    static void truthTable(int count, int countOfRows, String value) {

        System.out.println("Таблица истинности:");
        String firstString = " ";
        for (int i = 1; i <= count; i++) {
            if (i <= 9) {
                firstString += "x" + i + "  | ";
            } else {
                firstString += "x" + i + " | ";
            }
        }
        firstString += "f(x)";
        System.out.println(firstString);

        String line = "------";
        for (int i = 1; i <= count + 1; i++) {
            System.out.print(line);
        }

        System.out.println();

        for (int i = 0; i < countOfRows; i++) {
            for (int j = 1; j <= count; j++) {
                int step = (int) Math.pow(2, count - j);
                int valueBit = (i / step) % 2;
                System.out.print("  " + valueBit + "  |");
            }
            System.out.println("  " + value.charAt(i));
        }
    }

    static void normalForms(int count, int countOfRows, String value) {

        String PCNF = "";
        String PDNF = "";
        int k1 = 0;
        int k0 = 0;

        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '1') {
                k1 += 1;
            }
            if (value.charAt(i) == '0') {
                k0 += 1;
            }
        }

        if (k1 == value.length()) {
            System.out.println("СДНФ: 1");
            System.out.println("СКНФ: 1");
            System.out.println();
            return;
        }
        if (k0 == value.length()) {
            System.out.println("СДНФ: 0");
            System.out.println("СКНФ: 0");
            System.out.println();
            return;
        }


        for (int i = 0; i < countOfRows; i++) {
            if (value.charAt(i) == '1') {
                PDNF += '(';
            } else {
                PCNF += '(';
            }
            for (int j = 1; j <= count; j++) {
                int step = (int) Math.pow(2, count - j);
                int valueBit = (i / step) % 2;
                if (value.charAt(i) == '1') {
                    if (valueBit == 1) PDNF += "x" + j + " ^ ";
                    else PDNF += "!x" + j + " ^ ";
                } else {
                    if (valueBit == 0) PCNF += "x" + j + " v ";
                    else PCNF += "!x" + j + " v ";
                }
            }
            if (value.charAt(i) == '1') {
                PDNF = PDNF.substring(0, PDNF.length() - 3) + ") v ";
            } else {
                PCNF = PCNF.substring(0, PCNF.length() - 3) + ") ^ ";
            }
        }

        if (!PDNF.isEmpty()) {
            PDNF = PDNF.substring(0, PDNF.length() - 3);
        }
        if (!PCNF.isEmpty()) {
            PCNF = PCNF.substring(0, PCNF.length() - 3);
        }

        System.out.println("СДНФ: " + PDNF);
        System.out.println("СКНФ: " + PCNF);
        System.out.println();
    }

    static void removeFictiveVariables(int count, int countOfRows, String value) {
        System.out.println("После удаления фиктивных переменных:");
        int countNotFictive = 0;

        String newFunction = "";
        boolean[] isFictive = new boolean[count];

        for (int i = 1; i <= count; i++) {

            boolean fictive = true;
            int step = (int) Math.pow(2, count - i);

            for (int j = 0; j < countOfRows; j++) {

                int index = j ^ step;

                if (value.charAt(j) != value.charAt(index)) {
                    fictive = false;
                    break;
                }

            }
            isFictive[i - 1] = fictive;

            if (!fictive) {
                countNotFictive++;
            }


        }
        if (countNotFictive == 0) {
            String constFunc = String.valueOf(value.charAt(0));
            System.out.println(0);
            System.out.println(constFunc);

            truthTable(0, 1, constFunc);
            normalForms(0, 1, constFunc);
            return;
        }


        for (int i = 0; i < countOfRows; i++) {
            boolean take = true;

            for (int j = 1; j <= count; j++) {
                if (isFictive[j - 1]) {
                    int step = (int) Math.pow(2, count - j);
                    int valueBit = (i / step) % 2;
                    if (valueBit == 1) {
                        take = false;
                        break;
                    }
                }
            }

            if (take) {
                newFunction += value.charAt(i);
            }
        }

        int k1 = 0;
        int k0 = 0;

        for (int i = 0; i < newFunction.length(); i++) {
            if (newFunction.charAt(i) == '1') {
                k1 += 1;
            }
            if (newFunction.charAt(i) == '0') {
                k0 += 1;
            }
        }

        if (k1 == newFunction.length()) {
            System.out.println(countNotFictive);
            System.out.println("1");
            return;
        } else if (k0 == newFunction.length()) {
            System.out.println(countNotFictive);
            System.out.println("0");
            return;
        } else {
            System.out.println(countNotFictive);
            System.out.println(newFunction);
        }



        truthTable(countNotFictive, newFunction.length(), newFunction);
        normalForms(countNotFictive, newFunction.length(), newFunction);

    }
}
