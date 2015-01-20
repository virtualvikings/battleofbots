package nl.virtualvikings.battleofbots;

import java.util.Random;

public class Parser {

    //TODO: dummy class, to be replaced

    public ExpressionTree parse(String code) {
        return new ExpressionTree();
    }

    public static class ExpressionTree {

        //TODO: dummy class, to be replaced

        private final Random r = new Random();

        public Machine.Command getNextCommand(Machine vm, int botId) {

            //TODO: you could also have a "was i attacked last turn?" variable/method
            byte scanned = vm.scanAhead(botId); //-1 if enemy, 0 if nothing, else obstacle
            int health = vm.getBotById(botId).getHealth();

            //TODO: This stuff should actually be determined from the expression tree

            Machine.Command command;

            if (health <= 2) //Escape (move backward and turn randomly)
                if (r.nextDouble() > 0.2)
                    command = new Machine.Command(Machine.Command.Type.Move, -1);
                else
                    command = new Machine.Command(Machine.Command.Type.Turn, -1);

            else if (scanned == -1) //Attack if enemy found!
                command = new Machine.Command(Machine.Command.Type.Attack);

            else if (scanned != 0 || r.nextDouble() > 0.8) //Turn clockwise if obstacle found (and randomly)
                command = new Machine.Command(Machine.Command.Type.Turn, 1);

            else //Move forward
                command = new Machine.Command(Machine.Command.Type.Move, 1);

            return command;
        }
    }
}
