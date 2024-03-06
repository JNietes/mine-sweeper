import java.util.*;

public class MatrixSweeper {
   public static void main(String[] args) {
      Scanner input = new Scanner(System.in);

      printHelp();

      // My solution to prevent 8 if statements when checking adj elements on a matrix
      int[][] coolMatrix = {{1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}, {1, 0}};

      int digs = 0;
      int totalMines = 99; // Can modify
      int flagsLeft = totalMines;
      int minesFlagged = 0;
      MatrixSweeperBoard board = new MatrixSweeperBoard();
      int padLen;

      // Main loop
      while (true) {
         padLen = (board.getTopBoard()[0].length) - (7 + String.valueOf(flagsLeft).length());
         board.printBoard(board.getTopBoard(), flagsLeft, padLen, ":)");
         System.out.print(">>> ");
         String userInput = input.nextLine();
         System.out.println();
         String[] inputs = userInput.split(" ");

         if (inputs[0].equals("help")) {
            printHelp();
            continue;
         }

         if (inputs[0].equals("exit")) {
            break;
         }

         // Hard coded difficulties
         // Creating a board object would make the cases much shorter. board = new MatrixSweeperBoard(rows, columns, mines)
         if (inputs[0].equals("difficulty")) {
            System.out.print(">>> difficulty: ");
            inputs = input.nextLine().split(" ");
             switch (inputs[0]) {
                 case "beginner" -> {
                     digs = 0;
                     totalMines = 10;
                     flagsLeft = totalMines;
                    board = new MatrixSweeperBoard(9, 9, totalMines);
                 }
                 case "intermediate" -> {
                     digs = 0;
                     totalMines = 40;
                     flagsLeft = totalMines;
                    board = new MatrixSweeperBoard(16, 16, totalMines);
                 }
                 case "expert" -> {
                     digs = 0;
                     totalMines = 99;
                     flagsLeft = totalMines;
                    board = new MatrixSweeperBoard(30, 16, totalMines);
                 }
                 case "custom" -> {
                    System.out.print(">>> enter \"column\" \"rows\" \"mines\": ");
                    inputs = input.nextLine().split(" ");
                    digs = 0;
                    totalMines = Integer.parseInt(inputs[2]);
                    flagsLeft = totalMines;
                    board = new MatrixSweeperBoard( Integer.parseInt(inputs[1]),  Integer.parseInt(inputs[0]), Integer.parseInt(inputs[2]));
                 }
                 default -> System.out.println(">>> enter \"help\" for difficulties");
             }

            continue;
         }

         // Flagging Mines
         if (inputs[0].equals("f")) {
            System.out.print(">>> f: ");
            inputs = input.nextLine().split(" ");
            while (!validCoordinate(board.getTopBoard(), inputs)) {
               System.out.println("Please use the syntax \"column row\"");
               System.out.print(">>> f: ");
               inputs = input.nextLine().split(" ");
               System.out.println();
            }
            if (board.getTopBoard()[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] == ' ') {
               if (board.getHeatmap()[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] == 'X')
                  minesFlagged++;
               flagsLeft--;
               board.getTopBoard()[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] = 'f';
            }
            else if (board.getTopBoard()[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] == 'f') {
               if (board.getHeatmap()[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] == 'X') {
                  minesFlagged--;
               }
               flagsLeft++;
               board.getTopBoard()[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] = ' ';
            }

            // Victory
            if (minesFlagged == totalMines) {
               board.printBoard(board.getTopBoard(), flagsLeft, padLen, ":D");
               break;
            }
            continue;
         }

         // Digging
         if (board.getTopBoard()[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] == ' ') {
            if (validCoordinate(board.getTopBoard(), inputs) && digs == 0) {
               board.firstDig(board.getTopBoard(), board.getMineBoard(), board.getHeatmap(), coolMatrix, Integer.parseInt(inputs[1]), Integer.parseInt(inputs[0]));
               board.createNewHeatmap();
            }
            else
               board.digHere(board.getTopBoard(), board.getHeatmap(), coolMatrix, Integer.parseInt(inputs[1]), Integer.parseInt(inputs[0]));
            digs++;
         }
         else
            System.out.println("Cannot dig on flag");

         // Game Over
         if (board.getHeatmap()[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] == 'X' && board.getTopBoard()[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] != 'f' && digs != 1) {
            board.showMines(board.getTopBoard(), board.getHeatmap());
            board.printBoard(board.getTopBoard(), flagsLeft, padLen, "X(");
            System.out.print("Keep playing? \"yes\" or \"no\": ");
            if (input.nextLine().equals("yes")) {
               digs = 0;
               totalMines = 99; // Can modify
               flagsLeft = totalMines;
               board = new MatrixSweeperBoard();
            }
            else
               break;
         }
      }
      System.out.println("Thanks for playing!");
   }

   public static boolean validCoordinate(char[][] mat, String[] array) {
      if (array.length !=2) {
         System.out.println("Not two elements");
         return false;
      }
      if (digitsOnly(array[0]) || digitsOnly(array[1])) {
         System.out.println("Digits only");
         return false;
      }
      if (!isValidSpace(mat, Integer.parseInt(array[1]), Integer.parseInt(array[0]))) { // column row
         System.out.println("Not in board");
         return false;
      }
      return true;
   }

   public static boolean digitsOnly(String str) {
      for (int i=0; i<str.length(); i++)
         if (!Character.isDigit(str.charAt(i))){
            return true;
         }
      return false;
   }

   public static boolean isValidSpace(char[][] mat, int row, int column) {
      if (row<0 || row>mat.length-1) {
         return false;
      }
      else return column >= 0 && column <= mat[row].length - 1;
   }

   public static void printHelp() {
      System.out.println(  "___---Matrix Mine Sweeper Commands---___\n\n" +
                           "To dig, enter a coordinate \"column row\"\n\n" +// Column, row is intuitive for the user (x, y)
                           "To flag a mine, enter \"f\" and the coordinate\n\n"   +       // Row, column is intuitive for matrices
                           "To change the difficulty, enter \"difficulty\",\n" +
                           "then \"beginner\", \"intermediate\", \"expert\", or \"custom\"\n\n" +
                           "To reprint these commands, enter \"help\"\n\n" +
                           "To quit, enter \"exit\"\n");
   }
}